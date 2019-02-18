package com.nexstreaming.app.apis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import app.nunc.com.staatsoperlivestreaming.R;
import com.nexstreaming.nexplayerengine.NexOfflineStoreController;
import com.nexstreaming.nexplayerengine.NexPlayer;

public class NexPreferenceData {

	private SharedPreferences mPref;
	private Context mContext;

	public static final int HOME_BUTTON_MODE_STOP = 1;
	public static final int HOME_BUTTON_MODE_PAUSE = 2;

	// Preference Data
	public int		mCaptionMode;
	public int      mHomeButtonMode;
	public int		mLogLevel;
	public int		mProtoclLogLevel;
	public int		mCodecLogLevel;
	public int		mRendererLogLevel;
	public int		mStartNearestBW;
	public int		mStartSec;
	public int		mColorSpace;
	public int		mReplayMode;
	public int		mCodecMode;
	public int		mMaxBandWidth; // Check
	public int		mMinBandWidth;
	public int		mSeekOffset;
	public int		mTrackdownThreshold;
	public int		mVideoDisplaySkip;
	public int		mStoreTrackBW;
	public int		mVideoDisplayWait;
	public int		mCEA608RenderMode;
	public int      mEnableDolbyAC3PostProcessing;
	public int      mDolbyAC3EndPoint;
	public int      mDolbyAC3EnhancementGain;
	public int      mDolbyAC4Virtualization;
	public int      mDolbyAC4EnhancementGain;
	public int      mDolbyAC4MainAssoPref;
	public int      mDolbyAC4PresentationIndex;
	public int		mMaxThumbnailFrame;
	public int		mStoreStreamIDAudio;
	public int		mStoreTrackIDAudio;
	public int		mStoreStreamIDVideo;
	public int		mStoreStreamIDText;
	public int		mStoreStreamIDCustomAttr;
	public int 		mLowLatencyValue;
	public double	mBufferTime;
	public float	mAVSyncOffset;
	public String mRenderMode;
	public String mTextEncodingPreset;
	public String mTSDumpPath;
	public String mPCMDumpPath;
	public String mCacheFolder;
	public String mStoreInfoFolder;
	public String mStorePrefLanguageAudio;
	public String mStorePrefLanguageText;
	public String mPrefLanguageAudio;
	public String mPrefLanguageText;
	public String mSubtitleDownloadPath;
    public String mSdkMode;
	public boolean	mUseEyePleaser;
	public boolean	mEnableWebVTT;
	public boolean	mWebVTTWaitOpen;
	public boolean	mUseUDP;
	public boolean	mUseExternalPD;
	public boolean	mIsTrackdownEnabled;
	public boolean	mTSDumpEnable;
	public boolean  mPCMDumpEnable;
	public boolean	mHLSRunModeStable;
	public boolean	mIgnoreTextmode;
	public boolean	mPreloadHWOnly;
	public boolean	mEnableAudioOnlyTrack;
	public boolean	mEnableDynamicThumbnail;
	public boolean	mShowCaptionDownloadDialog;
	public boolean  mEnableStatisticsMonitor;
	public int 		mVideoViewMode;
	public boolean 	mUseSurfaceTextrue;
	public boolean 	mUseRenderThread;
	public boolean	mEnableID3TTML;
	public boolean 	mIsLowLatencyEnabled;
	public boolean  mIsAutoLowLatencyEnabled;

	
	public boolean mEnableClientSideTimeShift;
	public int	   mTimeShiftMaxBufferSize;
	public int	   mTimeShiftMaxBufferDuration;
	public boolean mEnableUpdateMaxBW;

    public boolean mEnableSPD;
    public int   mSPDValue;



	public NexPreferenceData(Context context) {
		mContext = context;
		mPref = PreferenceManager.getDefaultSharedPreferences( mContext );
		getDefaultPreferenceData();
	}
	
	protected void getDefaultPreferenceData() {
		mCaptionMode			    = mLogLevel = mProtoclLogLevel
									= mStartNearestBW = mStartSec = 0;
		mCodecLogLevel			    = mRendererLogLevel = -1;
		mColorSpace				    = mHomeButtonMode = 1;
		mReplayMode				    = 2;
		mCodecMode				    = 3;
		mMaxBandWidth		    	= 0;
		mMinBandWidth			    = 0;
		mSeekOffset				    = 5;
		mTrackdownThreshold		    = 70;
		mLowLatencyValue			= 200;
		mVideoDisplaySkip		    = 70;
		mStoreTrackBW			    = 3000;
		mVideoDisplayWait		    = -50;
		mCEA608RenderMode		    = 0;
		mEnableDolbyAC3PostProcessing  = 0;
		mDolbyAC3EndPoint              = 0;
		mDolbyAC3EnhancementGain       = 0;
		mDolbyAC4Virtualization  = 0;
		mDolbyAC4EnhancementGain       = 0;
		mDolbyAC4MainAssoPref          = 0;
		mDolbyAC4PresentationIndex     = 0;
		mMaxThumbnailFrame		= 10;
		mStoreStreamIDAudio		= 0;
		mStoreStreamIDVideo		= 0;
		mStoreStreamIDText		= 0;
		mStoreStreamIDCustomAttr= 0;
		mBufferTime				    = 0.0;
		mAVSyncOffset			    = 0.0f;
		mRenderMode				    = "Auto";
		mCacheFolder			= Environment.getExternalStorageDirectory().getPath() + "/NexPlayerCache/";
		mStoreInfoFolder 		= Environment.getExternalStorageDirectory().getPath() + "/NexPlayerCache/";
		mSubtitleDownloadPath       = mContext.getFilesDir().getAbsolutePath() + "/";
		mTextEncodingPreset		    = "UTF-8";
		mTSDumpPath				    = mPCMDumpPath = Environment.getExternalStorageDirectory().getPath() + "/";
        mSdkMode = "";
		mPrefLanguageAudio		    = "";
		mPrefLanguageText		    = "";
		mStorePrefLanguageAudio		    = "";
		mStorePrefLanguageText		    = "";
		mUseEyePleaser			    = mEnableWebVTT  = mWebVTTWaitOpen = true;
		mUseUDP					    = mUseExternalPD = mIsTrackdownEnabled	= mTSDumpEnable = mPCMDumpEnable = mHLSRunModeStable = mIgnoreTextmode = mPreloadHWOnly
									= mEnableDynamicThumbnail = mShowCaptionDownloadDialog = mEnableStatisticsMonitor = mEnableID3TTML = false;
		mIsLowLatencyEnabled        = false;

		



		mEnableClientSideTimeShift = false;
		mTimeShiftMaxBufferSize				= 100;
		mTimeShiftMaxBufferDuration			= 10;
		mEnableUpdateMaxBW = true;

		mEnableSPD = false;
		mSPDValue = 2000;

	}
	
	public void loadPreferenceData() {
		mCaptionMode		= Integer.parseInt(mPref.getString("captionMode", "0"));
		mLogLevel			= Integer.parseInt(mPref.getString("logLevel", "0"));
		mHomeButtonMode     = Integer.parseInt(mPref.getString(mContext.getString(R.string.pref_home_button_mode_key), String.valueOf(HOME_BUTTON_MODE_STOP)));

		mProtoclLogLevel	= 0;
		if( mPref.getBoolean("protocolLogDebug", true) ) {
			mProtoclLogLevel += 1;
		}
		if( mPref.getBoolean("protocolLogRTP", false) ) {
			mProtoclLogLevel += 2;
		}
		if( mPref.getBoolean("protocolLogRTCP", false) ) {
			mProtoclLogLevel += 4;
		}
		if( mPref.getBoolean("protocolLogFrame", false) ) {
			mProtoclLogLevel += 8;
		}
		mCodecLogLevel		= Integer.parseInt(mPref.getString("codecLog", "-1"));
		mRendererLogLevel	= Integer.parseInt(mPref.getString("rendererLog", "-1"));
		
		//Below 4 values will be written from string, and we cannot guarantee those string values are valid.
		
		try{
			mStartNearestBW		= Integer.parseInt(mPref.getString("startNearestBW", "0"));
		}catch(NumberFormatException e){
			e.printStackTrace();
			mStartNearestBW = 0;
		}
		
		try{ 
			mStartSec			= Integer.parseInt(mPref.getString(mContext.getString(R.string.pref_start_sec_key), "0"));
		}catch(NumberFormatException e){
			e.printStackTrace();
			mStartSec = 0;
		}
		
		try{
			mTimeShiftMaxBufferSize		= Integer.parseInt(mPref.getString(mContext.getString(R.string.pref_timeshift_buffer_size_key), "100"));
		}catch(NumberFormatException e){
			e.printStackTrace();
			mTimeShiftMaxBufferSize = 100;
		}
		
		try{
			mTimeShiftMaxBufferDuration		= Integer.parseInt(mPref.getString(mContext.getString(R.string.pref_timeshift_buffer_duration_key), "10"));
		}catch(NumberFormatException e){
			e.printStackTrace();
			mTimeShiftMaxBufferDuration = 10;
		}
		
		mColorSpace			        = Integer.parseInt(mPref.getString("colorSpace", "1"));
		mReplayMode			        = Integer.parseInt(mPref.getString("replayMode", "2"));
		mCodecMode			        = Integer.parseInt(mPref.getString("codecMode", "3"));
		mCEA608RenderMode	        = Integer.parseInt(mPref.getString("cea608RenderMode", "0"));
		mEnableDolbyAC3PostProcessing  = Integer.parseInt(mPref.getString(mContext.getString(R.string.dolby_ac3_post_processing), "0"));
		mDolbyAC3EndPoint              = Integer.parseInt(mPref.getString(mContext.getString(R.string.dolby_ac3_end_point), "0"));
		mDolbyAC3EnhancementGain       = (int)(mPref.getFloat(mContext.getString(R.string.dolby_ac3_enhance_gain), 0.0f));
		mDolbyAC4Virtualization  = Integer.parseInt(mPref.getString(mContext.getString(R.string.dolby_ac4_virtualization), "0"));
		mDolbyAC4EnhancementGain       = (int)(mPref.getFloat(mContext.getString(R.string.dolby_ac4_enhance_gain), 0.0f));
		mDolbyAC4MainAssoPref          = (int)(mPref.getFloat(mContext.getString(R.string.dolby_ac4_main_asso_pref), 0.0f));
		mDolbyAC4PresentationIndex     = (int)(mPref.getFloat(mContext.getString(R.string.dolby_ac4_presentation_index), 0.0f));
		mMaxThumbnailFrame		= (int)mPref.getFloat(mContext.getString(R.string.pref_maximum_dynamic_thumbnail_frame_key), 10.0f);

		boolean storeAllAudioStreams = mPref.getBoolean(mContext.getString(R.string.pref_store_all_audio_streams_key), false );
		boolean storeAllVideoStreams = mPref.getBoolean(mContext.getString(R.string.pref_store_all_video_streams_key), false );
		boolean storeAllTextStreams = mPref.getBoolean(mContext.getString(R.string.pref_store_all_text_streams_key), false );
		boolean defaultIDAudio = mPref.getBoolean(mContext.getString(R.string.pref_offline_audio_use_default_id_key), true );
		boolean defaultIDVideo = mPref.getBoolean(mContext.getString(R.string.pref_offline_video_use_default_id_key), true );
		boolean defaultIDText = mPref.getBoolean(mContext.getString(R.string.pref_offline_text_use_default_id_key), true );
		boolean defaultIDCustomAttr = mPref.getBoolean(mContext.getString(R.string.pref_offline_custom_use_default_id_key), true );
		boolean defaultTrackIDAudio = mPref.getBoolean(mContext.getString(R.string.pref_offline_audio_use_default_track_id_key), true);

		int audioStreamId = 0;
		int audioTrackId = 0;
		int videoStreamId = 0;
		int textStreamId = 0;

		if( storeAllAudioStreams ) {
			audioStreamId = NexOfflineStoreController.NexOfflineStoreSetting.STREAM_ID_ALL;
			audioTrackId = NexPlayer.MEDIA_TRACK_DEFAULT_ID;
		} else {
			audioStreamId = defaultIDAudio ? NexPlayer.MEDIA_STREAM_DEFAULT_ID : Integer.parseInt(mPref.getString(mContext.getString(R.string.pref_offline_stream_id_audio_key), String.valueOf(audioStreamId)));
			audioTrackId = defaultTrackIDAudio ? NexPlayer.MEDIA_TRACK_DEFAULT_ID : Integer.parseInt(mPref.getString(mContext.getString(R.string.pref_offline_input_track_id_key), String.valueOf(audioTrackId)));
		}

		if( storeAllVideoStreams ) {
			videoStreamId = NexOfflineStoreController.NexOfflineStoreSetting.STREAM_ID_ALL;
		} else {
			videoStreamId = defaultIDVideo ? NexPlayer.MEDIA_STREAM_DEFAULT_ID : Integer.parseInt(mPref.getString(mContext.getString(R.string.pref_offline_stream_id_video_key), String.valueOf(videoStreamId)));
		}

		if( storeAllTextStreams ) {
			textStreamId = NexOfflineStoreController.NexOfflineStoreSetting.STREAM_ID_ALL;
		} else {
			textStreamId = defaultIDText ? NexPlayer.MEDIA_STREAM_DEFAULT_ID : Integer.parseInt(mPref.getString(mContext.getString(R.string.pref_offline_stream_id_text_key), String.valueOf(textStreamId)));
		}

		mStoreStreamIDAudio		= audioStreamId;
		mStoreTrackIDAudio		= audioTrackId;
		mStoreStreamIDVideo		= videoStreamId;
		mStoreStreamIDText		= textStreamId;
		mStoreStreamIDCustomAttr= defaultIDCustomAttr ? NexPlayer.MEDIA_STREAM_DEFAULT_ID : Integer.parseInt(mPref.getString(mContext.getString(R.string.pref_offline_stream_id_custom_attr_key), "0"));


		mMaxBandWidth			= (int)mPref.getFloat( mContext.getString(R.string.pref_max_bandwidth_key), mMaxBandWidth );
		mMinBandWidth			= (int)mPref.getFloat( mContext.getString(R.string.pref_min_bandwidth_key), mMinBandWidth );
		mSeekOffset				= (int)mPref.getFloat( mContext.getString(R.string.pref_seek_offset_key), mSeekOffset );
		mTrackdownThreshold		= (int)mPref.getFloat( "trackdownThreshold", mTrackdownThreshold );
		mLowLatencyValue		= (int)mPref.getFloat("lowlatencyValue", mLowLatencyValue);
		mVideoDisplaySkip		= (int)mPref.getFloat( "VideoDisplaySkip", mVideoDisplaySkip );
		mStoreTrackBW			= (int)(mPref.getFloat( "storeTrackBW", (float)mStoreTrackBW)) * 1000;
		mVideoDisplayWait		= (int)mPref.getFloat( "VideoDisplayWait", mVideoDisplayWait );
		mBufferTime				= mPref.getFloat( mContext.getString(R.string.pref_buffering_time_key), (float)mBufferTime );
		mAVSyncOffset			= mPref.getFloat("AVSyncOffset", mAVSyncOffset);
		mRenderMode				= mPref.getString("renderMode", mRenderMode);
		mTextEncodingPreset		= mPref.getString( "TextEncoding Preset", mTextEncodingPreset );
		mTSDumpPath				= mPref.getString( mContext.getString(R.string.pref_ts_dumpPath_key), mTSDumpPath );
		mPCMDumpPath            = mPref.getString( mContext.getString(R.string.pref_pcm_dumpPath_key), mPCMDumpPath );
		mSubtitleDownloadPath   = mPref.getString(mContext.getString(R.string.pref_subtitle_download_path_key), mSubtitleDownloadPath);
		mCacheFolder			= mPref.getString( "offlineCacheLocation", mCacheFolder );
		mStoreInfoFolder		= mPref.getString(mContext.getString(R.string.pref_offline_info_file_path_key), mStoreInfoFolder);
		mPrefLanguageAudio		= mPref.getString( mContext.getString(R.string.pref_prefer_language_audio_key), mPrefLanguageAudio );
		mPrefLanguageText		= mPref.getString(mContext.getString(R.string.pref_prefer_language_text_key), mPrefLanguageText);
		mStorePrefLanguageAudio		= mPref.getString( mContext.getString(R.string.pref_offline_prefer_language_audio_key), mStorePrefLanguageAudio );
		mStorePrefLanguageText		= mPref.getString(mContext.getString(R.string.pref_offline_prefer_language_text_key), mStorePrefLanguageText);
        mSdkMode                = mPref.getString(mContext.getString(R.string.pref_sdk_mode_key), mSdkMode);
		mUseEyePleaser			= mPref.getBoolean( "eyePleaser", mUseEyePleaser );
		mEnableWebVTT			= mPref.getBoolean( "enabledtWebvtt", mEnableWebVTT );
		mWebVTTWaitOpen			= mPref.getBoolean( "WebvttWaitOpen", mWebVTTWaitOpen );
		mUseUDP					= mPref.getBoolean( "useUDP", mUseUDP );
		mUseExternalPD			= mPref.getBoolean( "useExternalPD", mUseExternalPD );
		mIsTrackdownEnabled		= mPref.getBoolean( "trackdownEnable", mIsTrackdownEnabled );
		mIsLowLatencyEnabled	= mPref.getBoolean(mContext.getString(R.string.pref_lowlatencyEnable_key),mIsLowLatencyEnabled);
		mIsAutoLowLatencyEnabled= mPref.getBoolean(mContext.getString(R.string.pref_automaticLowlatencyEnable_key),mIsAutoLowLatencyEnabled);
		mTSDumpEnable			= mPref.getBoolean( mContext.getString(R.string.pref_ts_dumpEnable_key), mTSDumpEnable );
		mPCMDumpEnable          = mPref.getBoolean( mContext.getString(R.string.pref_pcm_dumpEnable_key), mPCMDumpEnable );
		mHLSRunModeStable		= mPref.getBoolean( "HLSRunModeStable", mHLSRunModeStable );
		mIgnoreTextmode			= mPref.getBoolean( "ignoreTextmode", mIgnoreTextmode );
		mEnableAudioOnlyTrack 	= mPref.getBoolean( mContext.getString(R.string.pref_enableAudioOnlyTrack_key), mEnableAudioOnlyTrack );
		mEnableDynamicThumbnail	= mPref.getBoolean( mContext.getString(R.string.pref_dynamic_thumbnail_key), mEnableDynamicThumbnail);
		mShowCaptionDownloadDialog = mPref.getBoolean(mContext.getString(R.string.pref_download_caption_dialog_key), mShowCaptionDownloadDialog);
		mEnableStatisticsMonitor = mPref.getBoolean( "StatisticsMonitor", mEnableStatisticsMonitor );
		mEnableID3TTML			= mPref.getBoolean(mContext.getString(R.string.pref_enable_id3_ttml_key), mEnableID3TTML);

		
		mVideoViewMode = Integer.parseInt(mPref.getString(mContext.getString(R.string.pref_videoviewmode_key), "0"));
		mUseSurfaceTextrue = mPref.getBoolean(mContext.getString(R.string.pref_surfacetexture_key), mUseSurfaceTextrue);
		mUseRenderThread = mPref.getBoolean(mContext.getString(R.string.pref_renderthread_key), mUseRenderThread);

		mEnableClientSideTimeShift = mPref.getBoolean(mContext.getString(R.string.pref_enable_client_side_timeshift_key), mEnableClientSideTimeShift);
		mEnableUpdateMaxBW	= mPref.getBoolean( mContext.getString(R.string.pref_enableUpdateBW_key), mEnableUpdateMaxBW );

		mEnableSPD = mPref.getBoolean(mContext.getString(R.string.pref_SPDEnable_key),mEnableSPD);
		mSPDValue = (int)mPref.getFloat(mContext.getString(R.string.pref_SPDValue_key),mSPDValue);

	}
	
	public void setMinMaxBandwidth(float min_bw, float max_bw) {
		if(mEnableUpdateMaxBW) {
			editPreference(mContext.getString(R.string.pref_min_bandwidth_key), min_bw);
			editPreference(mContext.getString(R.string.pref_max_bandwidth_key), max_bw);
		}
	}

    public void editPreference(String key, String value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.commit();
        loadPreferenceData();
    }

    public void editPreference(String key, int value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, value);
        editor.commit();
        loadPreferenceData();
    }

    public void editPreference(String key, boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
        loadPreferenceData();
    }

    public void editPreference(String key, float value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putFloat(key, value);
        editor.commit();
        loadPreferenceData();
    }

	
	protected String getStringWithStringResourceId(int resId, String defaultValue) {
		return mPref.getString(mContext.getString(resId), defaultValue);
	}
}
