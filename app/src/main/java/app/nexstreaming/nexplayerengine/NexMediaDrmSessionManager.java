package app.nexstreaming.nexplayerengine;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NexMediaDrmSessionManager implements NexMediaDrmSession.ProvisioningManager {

    @Override
    public void provisionRequired(NexMediaDrmSession session) {
        provisioningSessions.add(session);
        if (provisioningSessions.size() == 1) {
            session.provision();
        }
    }

    @Override
    public void onProvisionError(Exception error) {
        for (NexMediaDrmSession session : provisioningSessions) {
            session.onProvisionError(error);
        }
        provisioningSessions.clear();
    }

    @Override
    public void onProvisionCompleted() {
        for (NexMediaDrmSession session : provisioningSessions) {
            session.onProvisionCompleted();
        }
        provisioningSessions.clear();
    }

    public interface EventListener {
        void onDrmKeysLoaded(int mediaType, byte[] keySetId, byte[] sessionId);

        void onDrmSessionManagerError(Exception e);

        void onDrmKeysRestored();

        void onDrmKeysRemoved();
    }

    static final int MODE_PLAYBACK = 0;
    static final int MODE_QUERY = 1;
    static final int MODE_DOWNLOAD = 2;
    static final int MODE_RELEASE = 3;
    private static final int MODE_DOWNLOAD_AND_PLAYBACK = 4;

    private static final String TAG = "NexMediaDrmSessionMgr";

    private final NexPlayer nexPlayer;
    private final UUID uuid;
    private final NexMediaDrm mediaDrm;
    private final NexMediaDrm.HttpNexMediaDrmCallback callback;
    private final HashMap<String, String> optionalKeyRequestParameters;
    private final EventListener eventListener;
    private final boolean multiSession;
    private final int initialDrmRequestRetryCount;

    private final List<NexMediaDrmSession> sessions;
    private final List<NexMediaDrmSession> provisioningSessions;

    private Looper playbackLooper;
    private int mode;
    private byte[] offlineLicenseKeySetId;

    private volatile MediaDrmHandler mediaDrmHandler;

    NexMediaDrmSessionManager(NexPlayer nexPlayer, UUID uuid, NexMediaDrm mediaDrm, NexMediaDrm.HttpNexMediaDrmCallback callback,
                              HashMap<String, String> optionalKeyRequestParameters,
                              EventListener eventListener, boolean multiSession, int initialDrmRequestRetryCount) {
        this.nexPlayer = nexPlayer;
        this.uuid = uuid;
        this.mediaDrm = mediaDrm;
        this.callback = callback;
        this.optionalKeyRequestParameters = optionalKeyRequestParameters;
        this.eventListener = eventListener;
        this.multiSession = multiSession;
        this.initialDrmRequestRetryCount = initialDrmRequestRetryCount;
        mode = MODE_PLAYBACK;
        sessions = new ArrayList<>();
        provisioningSessions = new ArrayList<>();
        if (multiSession) {
            mediaDrm.setPropertyString("sessionSharing", "enable");
        }
        mediaDrm.setOnEventListener(new MediaDrmEventListener());
    }

    final String getPropertyString(String key) {
        return mediaDrm.getPropertyString(key);
    }

    final void setPropertyString(String key, String value) {
        mediaDrm.setPropertyString(key, value);
    }

    final byte[] getPropertyByteArray(String key) {
        return mediaDrm.getPropertyByteArray(key);
    }

    final void setPropertyByteArray(String key, byte[] value) {
        mediaDrm.setPropertyByteArray(key, value);
    }

    private NexMediaDrmSession getSession(byte[] pssh) throws Exception {
        NexMediaDrmSession session = null;

        for (NexMediaDrmSession existingSession : sessions) {
            if (existingSession.hasInitData(pssh)) {
                session = existingSession;
                break;
            }
        }

        return session;
    }

    private int convertMediaDrmMode(NexPlayer.OfflineMode offlineMode) {
        int returnMode = MODE_PLAYBACK;

        if (NexPlayer.OfflineMode.STORE == offlineMode) {
            returnMode = MODE_DOWNLOAD;
        } else if (NexPlayer.OfflineMode.RETRIEVE == offlineMode) {
            returnMode = MODE_QUERY;
        } else if (NexPlayer.OfflineMode.RETRIEVE_STORE == offlineMode) {
            returnMode = MODE_DOWNLOAD_AND_PLAYBACK;
        }

        return returnMode;
    }

    private NexPlayer.OfflineMode convertOfflineMode(int mode) {
        NexPlayer.OfflineMode offlineMode = NexPlayer.OfflineMode.NONE;

        if (MODE_QUERY == mode) {
            offlineMode = NexPlayer.OfflineMode.RETRIEVE;
        } else if (MODE_DOWNLOAD == mode) {
            offlineMode = NexPlayer.OfflineMode.STORE;
        } else if (MODE_PLAYBACK == mode) {
            offlineMode = NexPlayer.OfflineMode.NONE;
        } else if (MODE_DOWNLOAD_AND_PLAYBACK == mode) {
            offlineMode = NexPlayer.OfflineMode.RETRIEVE_STORE;
        }

        return offlineMode;
    }

    NexPlayer.OfflineMode getMode() {
        return convertOfflineMode(mode);
    }

    NexMediaDrmSession acquireSession(Looper playbackLooper, byte[] pssh, NexPlayer.OfflineMode offlineMode, int mediaType) throws Exception {
        if (sessions.isEmpty()) {
            this.playbackLooper = playbackLooper;
            if (mediaDrmHandler == null) {
                mediaDrmHandler = new MediaDrmHandler(playbackLooper);
            }
        }

        NexMediaDrmSession session = getSession(pssh);

        maybeSetKeyId(offlineMode);
        mode = convertMediaDrmMode(offlineMode);

        String mimeType = "video/mp4";

        if (session == null) {
            session = new NexMediaDrmSession(uuid, mediaDrm, this, pssh, mimeType, mode,
                    offlineLicenseKeySetId, optionalKeyRequestParameters, callback, playbackLooper,
                    eventListener, initialDrmRequestRetryCount, mediaType);
            sessions.add(session);
        }
        session.acquire();
        return session;
    }

    void releaseSession(NexMediaDrmSession session) {
        if (session.release()) {
            sessions.remove(session);
            if (provisioningSessions.size() > 1 && provisioningSessions.get(0) == session) {
                provisioningSessions.get(1).provision();
            }
            provisioningSessions.remove(session);
        }
    }

    private void setMode(NexPlayer.OfflineMode offlineMode, byte[] offlineLicenseKeySetId) {
        int mode = convertMediaDrmMode(offlineMode);

        boolean shouldSetMode = true;
        if (mode == MODE_QUERY || mode == MODE_RELEASE) {
            if (null == offlineLicenseKeySetId) {
                shouldSetMode = false;
            }
        }

        if (shouldSetMode) {
            this.offlineLicenseKeySetId = offlineLicenseKeySetId;
            this.mode = mode;
        }

        NexLog.e(TAG, "shouldSetMode : " + mode);
    }

    private void maybeSetKeyId(NexPlayer.OfflineMode offlineMode) {
        if (NexPlayer.OfflineMode.RETRIEVE == offlineMode || NexPlayer.OfflineMode.RETRIEVE_STORE == offlineMode) {
            if (nexPlayer.getEventForwarder().hasInterface(NexPlayer.IOfflineKeyListener.class)) {
                NexLog.d(TAG, "onOfflineKeyRetrieveListener...");
                NexPlayerEvent event = new NexPlayerEvent(NexPlayerEvent.NEXPLAYER_OFFLINE_RETREIVE_KEY, new int[0], new long[0], null);
                byte[] keySetId = (byte[]) nexPlayer.getEventForwarder().handleEvent(nexPlayer, event);
                NexLog.d(TAG, "onOfflineKeyRetrieveListener ketSetId : " + Arrays.toString(keySetId));
                if (null != keySetId) {
                    setMode(offlineMode, keySetId);
                }
            } else {
                NexLog.e(TAG, "please add a callback function for retrieving key id");
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class MediaDrmHandler extends Handler {

        MediaDrmHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            byte[] sessionId = (byte[]) msg.obj;
            for (NexMediaDrmSession session : sessions) {
                if (session.hasSessionId(sessionId)) {
                    session.onMediaDrmEvent(msg.what);
                    return;
                }
            }
        }
    }

    private class MediaDrmEventListener implements NexMediaDrm.OnEventListener {

        @Override
        public void onEvent(NexMediaDrm mediaDrm, byte[] sessionId, int event, int extra, byte[] data) {
            if (mode == NexMediaDrmSessionManager.MODE_PLAYBACK) {
                mediaDrmHandler.obtainMessage(event, sessionId).sendToTarget();
            }
        }
    }
}
