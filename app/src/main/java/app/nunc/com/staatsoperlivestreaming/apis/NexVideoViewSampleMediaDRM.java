package app.nunc.com.staatsoperlivestreaming.apis;

import android.text.TextUtils;
import android.util.Base64;

import app.nunc.com.staatsoperlivestreaming.info.NxbInfo;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexVideoView;

public class NexVideoViewSampleMediaDRM extends NexVideoViewSample {
    private static final int NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM = 215;
    private String mKeyId = null;

    @Override
    protected void onPlayerConfiguration() {
        NexPlayer player = mVideoView.getNexPlayer();
        NxbInfo info = NxbInfo.getNxbInfo(mNxbInfoList, mCurrentPath, mCurrentIndex);

        int ret = initDRM(player, info, mPrefData);

        if( ret == NexPlayer.NexErrorCode.NONE.getIntegerCode() )
            super.onPlayerConfiguration();
        /*else
            onPlayerError(player, NexPlayer.NexErrorCode.fromIntegerValue(ret));*/
    }

    private int initDRM(NexPlayer player, NxbInfo info, NexPreferenceData prefData) {
        int ret = NexPlayer.NexErrorCode.NONE.getIntegerCode();

        if( info.getType().equals(NxbInfo.MEDIADRM) ) {
            if( prefData.mEnableMediaDRM )
                initMediaDRM(player, info, prefData);
        }

        return ret;
    }

    private void initMediaDRM(NexPlayer player, NxbInfo info, NexPreferenceData prefData) {
        String keyServer = info.getExtra(NxbInfo.MEDIA_DRM_SERVER_KEY_INDEX);
        if( TextUtils.isEmpty(keyServer) )
            keyServer = prefData.mWidevineDRMServerKey;
        player.setNexMediaDrmKeyServerUri(keyServer);
        player.setOfflineKeyListener(new NexPlayer.IOfflineKeyListener() {
            @Override
            public void onOfflineKeyStoreListener(NexPlayer mp, byte[] keyId) {
                if (null != keyId) {
                    mKeyId = Base64.encodeToString(keyId, Base64.DEFAULT);
                }
            }

            @Override
            public byte[] onOfflineKeyRetrieveListener(NexPlayer mp) {
                byte[] keyId = null;
                if (null != mKeyId) {
                    keyId = Base64.decode(mKeyId, Base64.DEFAULT);
                }
                return keyId;
            }
        });
    }

    @Override
    protected void setProperties(NexVideoView videoView, NexPreferenceData prefData) {
        NxbInfo info = NxbInfo.getNxbInfo(mNxbInfoList, mCurrentPath, mCurrentIndex);
        if( info.getType().equals(NxbInfo.MEDIADRM) ) {
            NexPlayer player = videoView.getNexPlayer();
            player.setProperties(NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM, prefData.mEnableMediaDRM ? 1 : 0);
        }
        super.setProperties(videoView, prefData);
    }
}
