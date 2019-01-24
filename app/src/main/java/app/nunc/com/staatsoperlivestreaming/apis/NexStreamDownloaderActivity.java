package app.nunc.com.staatsoperlivestreaming.apis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.info.NxbInfo;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexALFactory;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexOfflineStoreController;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStoredInfoFileUtils;
import app.nunc.com.staatsoperlivestreaming.util.NexFileIO;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexOfflineStoreController.NexOfflineStoreSetting;;

public class NexStreamDownloaderActivity extends Activity implements NexOfflineStoreController.IOfflineStoreListener {

	private static final String LOG_TAG = "StreamDownloaderAct";
	private static final Handler mHandler = new Handler();

    protected NexPlayer mNexPlayer;
	private NexOfflineStoreController mStoreController;
    private NexALFactory mNexALFactory;

	protected NexPreferenceData mPrefData = null;
	protected NxbInfo mNxbInfo = null;
	protected File mStoreInfoFile = null;
	private String mStoreInfoPath = null;

	private TextView mURLTextView = null;
	private TextView mErrorTextView = null;
	private LinearLayout mDownloadNotiLayout = null;
	private SeekBar mDownloadPercentSeekBar = null;
	private TextView mDownloadPercentTextView = null;
	private Button mDownloadStartStopButton = null;
	private Button mDownloadPauseResumeButton = null;

	private static final int STATE_NONE = 0;
	private static final int STATE_STOP = 1;
	private static final int STATE_START = 2;
	private static final int STATE_PAUSE = 3;

	private int mOfflineStoreState = STATE_NONE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.stream_downloader_activity);
		mPrefData = new NexPreferenceData(this);
		mPrefData.loadPreferenceData();

		loadLibs();

		Intent intent = getIntent();

		ArrayList<NxbInfo> nxbInfoList = intent.getParcelableArrayListExtra("wholelist");
		ArrayList<File> fileList = (ArrayList<File>)intent.getSerializableExtra("data");
		int index = intent.getIntExtra("selectedItem", 0);

		if( fileList != null )
			mStoreInfoFile = fileList.get(index);
		else if( nxbInfoList != null )
			mNxbInfo = nxbInfoList.get(index);
		Log.d(LOG_TAG, "mStoreInfoFile : " + mStoreInfoFile);
        mNexPlayer = new NexPlayer();
		mNexPlayer.setListener(new NexListenerObject());
        mNexALFactory = new NexALFactory();
        System.gc();

        int debugLogLevel = mPrefData.mLogLevel;
        if( debugLogLevel< 0 )
            debugLogLevel = 0xF0000000;

        if(!mNexALFactory.init(this, android.os.Build.MODEL, mPrefData.mRenderMode, debugLogLevel, 1)) {
	        showErrorMsg("ALFactory initialization failed");
            return;
        }

        mNexPlayer.setLicenseFile("/sdcard/test_lic.xml");
        mNexPlayer.setNexALFactory(mNexALFactory);
        if(!mNexPlayer.init(this, mPrefData.mLogLevel)) {
	        showErrorMsg("NexPlayer initialization failed");
            return;
        }

		mNexPlayer.setOfflineMode(true, true);

		mNexPlayer.setOfflineKeyListener(new NexPlayer.IOfflineKeyListener() {
			@Override
			public void onOfflineKeyStoreListener(NexPlayer mp, byte[] keyId) {
				if (null != keyId) {
					Log.d(LOG_TAG, "onOfflineKeyStoreListener keyId : " + Base64.encodeToString(keyId, Base64.DEFAULT));
					mStoreController.setOfflineStoreSetting(NexOfflineStoreSetting.STRING_OFFLINE_KEY_ID, Base64.encodeToString(keyId, Base64.DEFAULT));
				}
			}

			@Override
			public byte[] onOfflineKeyRetrieveListener(NexPlayer mp) {
				byte[] keyId = null;

				if( mStoreInfoFile != null ) {
					JSONObject obj = NexStoredInfoFileUtils.parseJSONObject(mStoreInfoFile);
					try {
						String sKeyId = obj.getString(NexStoredInfoFileUtils.STORED_INFO_KEY_OFFLINE_KEY_ID);
						if(sKeyId != null)
							keyId = Base64.decode(sKeyId, Base64.DEFAULT);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				return keyId;
			}
		});

		mStoreController = new NexOfflineStoreController(mNexPlayer, this);
		mStoreController.setListener(this);

		setupUIComponents();

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				downloadStart();
			}
		});
	}

	private void downloadStart() {
		int ret = 0;

		Log.d(LOG_TAG, "downloadStart mNxbInfo : " + mNxbInfo + " mStoreInfoFile : " + mStoreInfoFile);

		if( mNxbInfo != null )
			ret = downloadStart(mStoreController, mNxbInfo, mPrefData);
		else if( mStoreInfoFile != null )
			ret = downloadStart(mStoreController, mStoreInfoFile, mPrefData);
		if( ret != 0 ) {
			onError(NexPlayer.NexErrorCode.fromIntegerValue(ret));
		}
	}

	protected void showErrorMsg(final String msg) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mErrorTextView.setVisibility(View.VISIBLE);
				mErrorTextView.setText(msg);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		updateInfoUI(mNxbInfo, mStoreInfoFile);
	}

	protected void updateInfoUI(NxbInfo info, File file) {
		String url = "";
		if( info != null )
			url = info.getUrl();
		else if( file != null ) {
			JSONObject obj = NexStoredInfoFileUtils.parseJSONObject(file);
			try {
				url = obj.getString(NexStoredInfoFileUtils.STORED_INFO_KEY_STORE_URL);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if( url != null )
			mURLTextView.setText(url);
	}

	private void loadLibs() {
		if( !PlayerEnginePreLoader.isLoaded() ) {
			int codecMode = mPrefData.mPreloadHWOnly ? 2 : mPrefData.mCodecMode;
			String libraryPath = this.getApplicationInfo().dataDir+"/";
			PlayerEnginePreLoader.Load(libraryPath, this, codecMode);
		}
	}

	private void setupUIComponents() {
		mURLTextView = (TextView)findViewById(R.id.url_text_view);
		mErrorTextView = (TextView)findViewById(R.id.error_text_view);
		mDownloadNotiLayout = (LinearLayout)findViewById(R.id.download_noti_layout);
		mDownloadPercentSeekBar = (SeekBar)findViewById(R.id.download_percent_seek_bar);
		mDownloadPercentSeekBar.setMax(100);
		mDownloadPercentTextView = (TextView)findViewById(R.id.download_percent_text_view);
		setupDownloadStartStopButton();
		setupDownloadPauseResumeButton();
	}

	private void setupDownloadStartStopButton() {
		mDownloadStartStopButton = (Button)findViewById(R.id.download_start_stop_button);
		mDownloadStartStopButton.setEnabled(false);
		mDownloadStartStopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mOfflineStoreState == STATE_STOP ) {
					mErrorTextView.setVisibility(View.INVISIBLE);
					downloadStart();
				} else {
					downloadStop();
				}
			}
		});
	}

	private void setupDownloadPauseResumeButton() {
		mDownloadPauseResumeButton = (Button)findViewById(R.id.download_pause_resume_button);
		mDownloadPauseResumeButton.setEnabled(false);
		mDownloadPauseResumeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mOfflineStoreState == STATE_START ) {
					downloadPause();
				} else if( mOfflineStoreState == STATE_PAUSE ){
					downloadResume();
				}
			}
		});
	}

	protected int downloadStart(NexOfflineStoreController controller, NxbInfo info, NexPreferenceData pref) {
		String cacheFolder = NexFileIO.getCacheFolderPath(pref.mCacheFolder, NexFileIO.getContentTitle(info.getUrl()), pref.mStoreTrackBW);
		Log.d(LOG_TAG, "cacheFolder : " + cacheFolder);
		controller.setOfflineStoreSetting(NexOfflineStoreSetting.INTEGER_BANDWIDTH, pref.mStoreTrackBW);
		controller.setOfflineStoreSetting(NexOfflineStoreSetting.INTEGER_AUDIO_STREAM_ID, pref.mStoreStreamIDAudio);
		controller.setOfflineStoreSetting(NexOfflineStoreSetting.INTEGER_AUDIO_TRACK_ID, pref.mStoreTrackIDAudio);
		controller.setOfflineStoreSetting(NexOfflineStoreSetting.INTEGER_VIDEO_STREAM_ID, pref.mStoreStreamIDVideo);
		controller.setOfflineStoreSetting(NexOfflineStoreSetting.INTEGER_TEXT_STREAM_ID, pref.mStoreStreamIDText);
		controller.setOfflineStoreSetting(NexOfflineStoreSetting.INTEGER_CUSTOM_ATTRIBUTE_ID, pref.mStoreStreamIDCustomAttr);
		controller.setOfflineStoreSetting(NexOfflineStoreSetting.STRING_PREFER_LANGUAGE_AUDIO, pref.mStorePrefLanguageAudio);
		controller.setOfflineStoreSetting(NexOfflineStoreSetting.STRING_PREFER_LANGUAGE_TEXT, pref.mStorePrefLanguageText);
		controller.setOfflineStoreSetting(NexOfflineStoreSetting.STRING_STORE_PATH, cacheFolder);

		// Widevine start
		int drmType = 0;
		// Widevine end

		// NexMediaDrm start
		if( mPrefData.mEnableMediaDRM) {
			String serverKey = mPrefData.mWidevineDRMServerKey;
			if( mNxbInfo != null && mNxbInfo.getType().equals(NxbInfo.MEDIADRM) )
				serverKey = mNxbInfo.getExtra(NxbInfo.MEDIA_DRM_SERVER_KEY_INDEX);
			Log.d(LOG_TAG, "setNexMediaDrmKeyServerUri ( " + serverKey + " )");
			controller.setOfflineStoreSetting(NexOfflineStoreSetting.STRING_MEDIA_DRM_KEY_SERVER_URL, serverKey);
			drmType |= 1;
		}
		// NexMediaDrm end

		// NexWVSWDrm start
		if(mPrefData.mEnableWVSWDRM) {
			String serverKey = mPrefData.mWidevineDRMServerKey;

			if( mNxbInfo != null && (mNxbInfo.getType().equals(NxbInfo.WVDRM) || mNxbInfo.getType().equals(NxbInfo.MEDIADRM)) )
				serverKey = mNxbInfo.getExtra(NxbInfo.WV_DRM_PROXY_SERVER_INDEX);
			Log.d(LOG_TAG, "setNexWVKeyServerUri ( " + serverKey + " )");
			controller.setOfflineStoreSetting(NexOfflineStoreSetting.STRING_MEDIA_DRM_KEY_SERVER_URL, serverKey);
			drmType |= 2;
		}
		// NexWVSWDrm end

		// Widevine start
		controller.setOfflineStoreSetting(NexOfflineStoreSetting.INTEGER_DRM_TYPE, drmType);
		// Widevine end

		String storeFilePath = pref.mStoreInfoFolder + NexFileIO.makeUniqueStoreInfoFileName(pref.mStoreInfoFolder, info.getUrl(), pref.mStoreTrackBW);
		int result = controller.startOfflineStore(info.getUrl(), storeFilePath, pref.mUseUDP ? NexPlayer.NEXPLAYER_TRANSPORT_TYPE_UDP : NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP);
		if( result == 0 )
			mStoreInfoPath = storeFilePath;
		Log.d(LOG_TAG, "startOfflineStore result : " + result + " mStoreInfoPath : " + mStoreInfoPath);
		return result;
	}

	private boolean isNeededExtraSetting(NxbInfo info, String type) {
		Log.d(LOG_TAG, "isNeededExtraSetting info.getType() : " + info.getType());
		return info.getType().equalsIgnoreCase(type);
	}

	protected int downloadStart(NexOfflineStoreController controller, File file, NexPreferenceData pref) {
		int result = controller.startOfflineStore(file.getAbsolutePath(),
				pref.mUseUDP ? NexPlayer.NEXPLAYER_TRANSPORT_TYPE_UDP : NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP);
		Log.d(LOG_TAG, "startOfflineStore result : " + result);
		return result;
	}

	private void resetUIComponents() {
		mDownloadStartStopButton.setEnabled(true);
		mDownloadStartStopButton.setText(getString(R.string.start));
        mDownloadPauseResumeButton.setText(getString(R.string.pause));
		mDownloadPauseResumeButton.setEnabled(false);
		mDownloadPercentSeekBar.setProgress(0);
		mDownloadPercentTextView.setText(getString(R.string.download_percent_default));
		mDownloadNotiLayout.setVisibility(View.GONE);
	}

	private void downloadStop() {
		mStoreController.stopOfflineStore();
	}

	private void downloadPause() {
		if( mStoreController.pauseOfflineStore() == 0 ) {
			mDownloadPauseResumeButton.setText(getString(R.string.resume));
		}
	}

	private void downloadResume() {
        int result = mStoreController.resumeOfflineStore();
		if( result == 0 ) {
			mDownloadPauseResumeButton.setText(getString(R.string.pause));
		}
	}

	@Override
	protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy.");
		if( mOfflineStoreState > STATE_STOP )
			downloadStop();

		while ( mOfflineStoreState > STATE_STOP ) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		mNexPlayer.release();
        mNexALFactory.release();
		PlayerEnginePreLoader.deleteAPKAsset(this);

		super.onDestroy();
	}

	protected String getErrorMsg(NexPlayer.NexErrorCode errorCode) {
		String errorMsg = null;
		if( errorCode == null ) {
			errorMsg = "onError : Unknown Error Occured with Invalid errorcode object";
		} else {
			switch (errorCode.getCategory()) {
				case API:
				case BASE:
				case NO_ERROR:
				case INTERNAL:
					errorMsg = "An internal error occurred while attempting to open the media: "
							+ errorCode.name();
					break;

				case AUTH:
					errorMsg = "You are not authorized to view this content, "
							+ "or it was not possible to verify your authorization, "
							+ "for the following reason:\n\n" + errorCode.getDesc();
					break;

				case CONTENT_ERROR:
					errorMsg = "The content cannot be played back, probably because of an error in "
							+ "the format of the content (0x"
							+ Integer.toHexString(errorCode.getIntegerCode())
							+ ": " + errorCode.name() + ").";
					break;

				case NETWORK:
					errorMsg = "The content cannot be played back because of a "
							+ "problem with the network.  This may be temporary, "
							+ "and trying again later may resolve the problem.\n\n("
							+ errorCode.getDesc() + ")";
					break;

				case NOT_SUPPORT:
					errorMsg = "The content cannot be played back because it uses a "
							+ "feature which is not supported by NexPlayer.\n\n("
							+ errorCode.getDesc() + ")";
					break;

				case GENERAL:
					errorMsg = "The content cannot be played back for the following reason:\n\n"
							+ errorCode.getDesc();
					break;

				case PROTOCOL:
					errorMsg = "The content cannot be played back because of a "
							+ "protocol error.  This may be due to a problem with "
							+ "the network or a problem with the server you are "
							+ "trying to access.  Trying again later may resolve "
							+ "the problem.\n\n(" + errorCode.name() + ")";
					break;
				case DOWNLOADER:
					errorMsg = "Download has the problem\n\n(" + errorCode.name() + ")";
					break;

				case SYSTEM:
					errorMsg = "SYSTEM has the problem\n\n(" + errorCode.name() + ")";
					break;
			}
		}

		return errorMsg;
	}

	@Override
	public void onError(final NexPlayer.NexErrorCode errorCode) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				showErrorMsg(getErrorMsg(errorCode));

				switch (mOfflineStoreState) {
					case STATE_START:
					case STATE_PAUSE:
						downloadStop();
						break;
					case STATE_STOP:
						resetUIComponents();
						break;
				}
			}
		});
	}

	@Override
	public boolean onContentInfoReady() {
		Log.d(LOG_TAG, "onContentInfoReady()...");

		/* When this callback is called, the user gets the content information
		 * and sets the required streamID/trackID.
		 * If there is no match the trackID in the streamID, the default trackID is set.
		 *
		 * ex)
		 * NexContentInformation contentInfo = mNexPlayer.getContentInfo();
		 * parsing contentInfo structure - see NexPlayer documentation.
		 * mStoreController.setOfflineStoreSetting(NexOfflineStoreSetting.INTEGER_AUDIO_STREAM_ID, streamID);
		 * mStoreController.setOfflineStoreSetting(NexOfflineStoreSetting.INTEGER_AUDIO_TRACK_ID, trackID);
		 */
		return true;
	}

	@Override
	public void offlineStoreStarted() {
		Log.d(LOG_TAG, "offlineStoreStarted.");
		mOfflineStoreState = STATE_START;

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mDownloadNotiLayout.setVisibility(View.VISIBLE);
				mDownloadPauseResumeButton.setEnabled(true);
				mDownloadStartStopButton.setText(R.string.stop);
				mDownloadStartStopButton.setEnabled(true);
			}
		});
	}

	@Override
	public void offlineStoreStopped() {
		Log.d(LOG_TAG, "offlineStoreStopped. mStoreInfoPath : " + mStoreInfoPath);
		mOfflineStoreState = STATE_STOP;

		if( !TextUtils.isEmpty(mStoreInfoPath) ) {
			mStoreInfoFile = new File(mStoreInfoPath);
			mNxbInfo = null;
		}

		mStoreInfoPath = null;

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				resetUIComponents();
			}
		});
	}

	@Override
	public void offlineStoreResumed() {
		Log.d(LOG_TAG, "offlineStoreResumed");
		mOfflineStoreState = STATE_START;
	}

	@Override
	public void offlineStorePaused() {
		Log.d(LOG_TAG, "offlineStorePaused");
		mOfflineStoreState = STATE_PAUSE;
	}

	@Override
	public void onDownloadBegin() {
		Log.d(LOG_TAG, "onDownloadBegin");
	}

	@Override
	public void onDownloading(final int percentage) {
		Log.d(LOG_TAG, "onDownloading percentage : " + percentage);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mDownloadPercentSeekBar.setProgress(percentage);
				mDownloadPercentTextView.setText("" + percentage + " %");
			}
		});
	}

	@Override
	public void onDownloadEnd(boolean completed) {
		Log.d(LOG_TAG, "onDownloadEnd completed : " + completed);
		if( completed ) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(NexStreamDownloaderActivity.this, "Download complete", Toast.LENGTH_LONG).show();
					downloadStop();
				}
			});
		}
	}
}
