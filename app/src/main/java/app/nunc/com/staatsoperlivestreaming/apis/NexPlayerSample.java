/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.nunc.com.staatsoperlivestreaming.apis;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PictureInPictureParams;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Rational;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.apis.BandwidthDialog.IBandwidthListener;
import app.nunc.com.staatsoperlivestreaming.apis.DolbyAC3Dialog.IDolbyAC3Listener;
import app.nunc.com.staatsoperlivestreaming.apis.DolbyAC4Dialog.IDolbyAC4Listener;
import app.nunc.com.staatsoperlivestreaming.apis.MultiStreamDialog.IMultiStreamListener;
import app.nunc.com.staatsoperlivestreaming.apis.VolumeDialog.IVolumeListener;
import app.nunc.com.staatsoperlivestreaming.apis.VolumeDialog.VOLUME_BUTTON;
import app.nunc.com.staatsoperlivestreaming.dialog.NexContentInfoDialog;
import app.nunc.com.staatsoperlivestreaming.info.NxbInfo;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.INexDRMLicenseListener;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexABRController;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexALFactory;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionPainter;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionSetting;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexContentInformation;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexDateRangeData;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexEmsgData;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexEventReceiver;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexID3TagInformation;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexID3TagPicture;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexID3TagText;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexNetAddrTable;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPictureTimingInfo;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer.NexErrorCode;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer.NexProperty;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexSessionData;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStatisticsMonitor;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStoredInfoFileUtils;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexSurfaceTextureView;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexVideoRenderer;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexVideoViewFactory;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexWVDRM;
import app.nunc.com.staatsoperlivestreaming.util.FastPlayUtil;
import app.nunc.com.staatsoperlivestreaming.util.NetworkBroadcastReceiver;
import app.nunc.com.staatsoperlivestreaming.util.NetworkUtils;
import app.nunc.com.staatsoperlivestreaming.util.NexFileIO;
import app.nunc.com.staatsoperlivestreaming.util.PlayListUtils;
import app.nunc.com.staatsoperlivestreaming.util.Util;
import app.nunc.com.staatsoperlivestreaming.widget.NexImageButton;

import static app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStatisticsMonitor.IStatistics;
import static app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStatisticsMonitor.STATISTICS_GENERAL;

public class NexPlayerSample extends AppCompatActivity implements NetworkBroadcastReceiver.NetworkListener{

	private static final String LOG_TAG = "NexPlayerSample";
	public static final Handler mHandler = new Handler();

	private final int AUDIO_ONLY    = 1;
	private final int VIDEO_ONLY    = 2;
	private final int AUDIO_VIDEO   = 3;

	private static final int BANDWIDTH_KBPS = 1024;

	private static final int RESET_PLAYER_ALL = 0;
	private static final int RESET_PLAYER_PROGRESS = 1;
	private static final int RESET_PLAYER_BASIC = 2;

	private static final int REPLAY_MODE_NEXT = 0;
	private static final int REPLAY_MODE_AGAIN = 1;
	private static final int REPLAY_MODE_QUIT = 2;
	private static final int REPLAY_MODE_PREVIOUS = 3;

	//Widevine start
	private static final int NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM = 215;
	//Widevine end
	private static final int NEXPLAYER_PROPERTY_DATA_DUMP_SUB_PATH = 0x00070000;

	private static final int SCALE_FIT_TO_SCREEN = 0;
	private static final int SCALE_ORIGINAL = 1;
	private static final int SCALE_STRETCH_TO_SCREEN = 2;

	private int mScaleMode = SCALE_FIT_TO_SCREEN;

	private ArrayList<File> mFileList;
	protected String mCurrentPath;
	private boolean mExistFollowingContentPath;

	private String mCurrentExtraData;
	private String mCurrentSubtitlePath = null;
	private int mCurrentPosition = 0;
	private String mTargetSubtitlePath = null;

	private NexContentInformation mContentInfo = null;
	private ArrayList<NxbInfo>mNxbWholeList;

	//preference
	private float mVolume = 10.0f;

	private boolean mNeedResume = false;
	private boolean mIsHeadsetRemoved = false;

	private boolean mAudioInitEnd = false;

	private NexSurfaceTextureView mVideoSurfaceView;
	private NexVideoViewFactory.INexVideoView mVideoView = null;
	protected NexPlayer mNexPlayer;
	private NexEventReceiver mEventReceiver;
	private NexALFactory mNexALFactory;
	private NexABRController mABRController;

	private BroadcastReceiver mBroadcastReceiver;

	//ui components
	private ViewGroup mVisibilityLayout;
	private ThumbnailSeekBar mSeekBar;
	private ImageView mImageView;
	private ImageView mThumbnailView;
	private NexImageButton mPreviousButton;
	private NexImageButton mRewindButton;
	private NexImageButton mPlayPauseButton;
	private NexImageButton mFastForwardButton;
	private NexImageButton mNextButton;
	private NexImageButton mGoToLiveButton;
	private TextView mStatisticTextview;
	private ScrollView mLyricScrollView;
	private TextView mLyricView;
	private TextView mErrorView;
	private TextView mTrackView;

	private CaptionStyleDialog mCaptionDialog;
	private int mVideoWidth = 0;
	private int mVideoHeight = 0;

	// buffer info
	private RelativeLayout mProgressLayout;
	private TextView mProgressTextView;
	private boolean mIsBuffering = false;

	// seek information
	private int mProgressBase = 0;
	private int mSeekPoint = -1;
	private boolean mIsSeeking = false;

	//Streaming status
	private boolean mIsStreaming = false;
	private boolean mIsLive = false;
	private boolean mIsStoreManagerInitialized = false;

	private boolean mIsEnginOpen = false;
	//player status
	private enum PLAYER_FLOW_STATE {
		START_PLAY, BEGINNING_OF_COMPLETE, END_OF_COMPLETE, FINISH_ACTIVITY, BEGINNING_OF_ONERROR, END_OF_ONERROR, STATE_NONE
	};
	private PLAYER_FLOW_STATE mPlayerState = PLAYER_FLOW_STATE.STATE_NONE;
	//Caption Render Information

	private NexCaptionPainter mCaptionPainter = null;

	private int mCaptionIndex = -1;
	CaptionStreamList mCaptionStreamList = new CaptionStreamList();

	private boolean mForeground = true;

	private int mExternalPDBufferDuration = 0;
	private int	mDownloaderState = NexPlayer.NEXDOWNLOADER_STATE_NONE;
	private String mStrExternalPDFile = null;
	private long mTotalSize = 0;

	//time meta
	private TextView mLyricTextView = null;
	private TextView mTimedMetaTextView = null;

	private RelativeLayout mParentView;

	private boolean mNeedStartSeekBarTime = false;

	private PlayerSampleUtils playerSampleUtils = PlayerSampleUtils.sharedInstance();
	private PlayListUtils mPlayListUtil;

	private NexPreferenceData mPrefData = null;

	private StatisticsDialog mStatisticsDialog;
	private MultiStreamDialog mMultistreamDialog;
	private BandwidthDialog mBandwidthDialog;
	private VolumeDialog mVolumeDialog;
	private DolbyAC3Dialog mDolbyAC3Dialog;
	private DolbyAC4Dialog mDolbyAC4Dialog;
	private AlertDialog mScaleDialog = null;
	private CaptionDownloadDialog mCaptionDownloadDialog = null;
	private TargetBandWidthDialog mTargetBandWidthDialog = null;
	private CaptionLanguageDialog mCaptionLanquageDialog = null;
	private SubtitleChangeDialog mSubtitleChangeDialog = null;
	private NexContentInfoDialog mContentInfoDialog = null;

	private boolean mFastPlay = false;
	private float   mFastPlaySpeed = 1.0f;

	private Timer mTimer = null;
	private CountDownTimer clientTimeshiftTimer = null;
	private String mCacheFolderPath = null;

	//NexWVSWDrm start
	private NexWVDRM mNexWVDRM;
	//NexWVSWDrm end
	private int mTempVideoStreamID = NexPlayer.MEDIA_STREAM_DEFAULT_ID;

	private NexStatisticsMonitor.IStatisticsListener mStatisticsListener;
	private NexStatisticsMonitor mStatisticsMonitor;

	protected Toolbar mToolbar;

	private  SendTimeHandler mTimeHandler = null;
	private  TextView mUTCTime = null;
	private static final int MSG_START_TIME  = 0;
	private static final int MSG_UPDATE_TIME  = 1;

	WaitingForUnlockThread mWaitingThread = null;

	@SuppressLint("InlinedApi")
	public void onCreate(Bundle icicle) {
		mPrefData = new NexPreferenceData( getApplicationContext() );
		mPrefData.loadPreferenceData();

		setPreloader();

		super.onCreate(icicle);
		if( Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		}
		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		mStatisticsDialog = new StatisticsDialog(this);
		mMultistreamDialog = new MultiStreamDialog(this);
		mBandwidthDialog = new BandwidthDialog(this);
		mVolumeDialog = new VolumeDialog(this);
		mDolbyAC3Dialog = new DolbyAC3Dialog(this, mDolbyAC3DialogListener, mPrefData.mEnableDolbyAC3PostProcessing, mPrefData.mDolbyAC3EndPoint, mPrefData.mDolbyAC3EnhancementGain);
		mDolbyAC4Dialog = new DolbyAC4Dialog(this, mDolbyAC4DialogListener, mPrefData.mDolbyAC4Virtualization, mPrefData.mDolbyAC4EnhancementGain, mPrefData.mDolbyAC4MainAssoPref, mPrefData.mDolbyAC4PresentationIndex);
		mTargetBandWidthDialog = new TargetBandWidthDialog(this, new TargetBandWidthDialog.TargetBandWidthIListener() {
			@Override
			public void onTargetBandWidthDialogUpdated(boolean abrEnabled, int targetBandWidth, NexABRController.SegmentOption segOption, NexABRController.TargetOption targetOption) {
				mABRController.setABREnabled(abrEnabled);

				if( !abrEnabled ) {
					NexErrorCode result = mABRController.setTargetBandWidth(targetBandWidth, segOption, targetOption);
					Log.d(LOG_TAG, "onTargetBandWidthUpdated setTargetBandWidth result : " + result);
				}
			}
		}, mPrefData.mEnableAudioOnlyTrack);
		mCaptionLanquageDialog = new CaptionLanguageDialog(this, new CaptionLanguageDialog.Listener() {
			@Override
			public void onItemClicked(int position, boolean disable) {
				mNexPlayer.setCaptionLanguage(position);
				clearCaptionString();
			}
		});
		mSubtitleChangeDialog = new SubtitleChangeDialog(this, new SubtitleChangeDialog.Listener() {
			@Override
			public void onSubtitleChanged(String subtitlePath) {
				Log.d(LOG_TAG, "mSubtitleChangeDialog onSubtitleChanged subtitlePath : " + subtitlePath);
				int result = mNexPlayer.changeSubtitlePath(subtitlePath);
				if( result == 0 ) {
					mTargetSubtitlePath = subtitlePath;
				} else {
					showToastMsg(getString(R.string.invalid_subtitle_path));
				}
				Log.d(LOG_TAG, "changeSubtitlePath result : " + result);
			}
		});


		mPlayListUtil = new PlayListUtils(getResources().getStringArray(R.array.playable_extension_list));

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.nexplayer_sample);

		getIntentExtra();
		setBroadcastReceiver();
		setUIComponents();

		if( setPlayer() < 0 ) {
			mPlayerState = PLAYER_FLOW_STATE.END_OF_ONERROR;
			return;
		}

		if( mPrefData.mShowCaptionDownloadDialog ) {
			mCaptionDownloadDialog = new CaptionDownloadDialog(this, new CaptionDownloadDialog.CaptionDownloadIListener() {
				@Override
				public void onCaptionDownloadComplete(int result, String captionPath) {
					Log.d("captiondown", "onCaptionDownloadComplete result : " + result + " captionPath : " + captionPath);
					mTargetSubtitlePath = captionPath;
					startPlay();
				}
			});
			mCaptionDownloadDialog.createAndShowDialog();
		} else {
			startPlay();
		}

		mToolbar.requestFocus();
	}

	@Override
	public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
		Log.d(LOG_TAG, "onPictureInPictureModeChanged	isInPictureInPictureMode : " + isInPictureInPictureMode);
		setControllerVisibility(isInPictureInPictureMode? View.INVISIBLE : View.VISIBLE);
		super.onPictureInPictureModeChanged(isInPictureInPictureMode);
	}

	private IDolbyAC3Listener mDolbyAC3DialogListener = new IDolbyAC3Listener() {

		@Override
		public void onDolbyAC3DialogUpdated(DolbyAC3Dialog.DOLBY_BUTTON button, int value) {
			Log.d(LOG_TAG, "onDolbyAC3DialogUpdated button : " + button + " value : " + value);
			switch (button) {
				case DOLBY_END_POINT:
					changeDolbyAC3EndPointValue(value);
					break;
				case DOLBY_ENHANCEMENT_GAIN:
					changeDolbyAC3EnhancementGainValue(value);
					break;
				case DOLBY_POST_PROCESSING:
					changeDolbyAC3PostProcessingValue(value);
					break;
			}
		}
	};

	private IDolbyAC4Listener mDolbyAC4DialogListener = new IDolbyAC4Listener() {

		@Override
		public void onDolbyAC4DialogUpdated(DolbyAC4Dialog.DOLBY_BUTTON button, int value) {
			Log.d(LOG_TAG, "onDolbyAC4DialogUpdated button : " + button + " value : " + value);
			switch (button) {
				case DOLBY_VIRTUALIZATION:
					changeDolbyAC4VirtualizationValue(value);
					break;
				case DOLBY_ENHANCEMENT_GAIN:
					changeDolbyAC4EnhancementGainValue(value);
					break;
				case DOLBY_MAIN_ASSO_PREF:
					changeDolbyAC4MainAssoPrefValue(value);
					break;
				case DOLBY_PRESENTATION_INDEX:
					changeDolbyAC4PresentationIndexValue(value);
					break;
			}
		}
	};

	private void setPreloader() {
		if( !PlayerEnginePreLoader.isLoaded() ) {
			Log.d(LOG_TAG, "Load NexPlayerEngine Library");
			int codecMode = mPrefData.mPreloadHWOnly ? 2 : mPrefData.mCodecMode;
			String libraryPath = this.getApplicationInfo().dataDir+"/" ;
			PlayerEnginePreLoader.Load(libraryPath, this, codecMode);
		}
	}

	private void stopOrPausePlayer() {
		if (mNexPlayer != null && mNexPlayer.isInitialized()) {
			if (mFastPlay)
				stopFastPlay();

			if ( mPrefData.mHomeButtonMode == NexPreferenceData.HOME_BUTTON_MODE_STOP ) {
				if ( mNexPlayer.getState() >= NexPlayer.NEXPLAYER_STATE_STOP ) {
					clearBufferStatus();
					mNeedStartSeekBarTime = true;
					mNexPlayer.stop();
				}
			} else {
				mNexPlayer.pause();
			}
			mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(LOG_TAG, "onPause");


		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			if (!isInPictureInPictureMode()) {
				if(mVideoView != null)
					mVideoView.onPause();
				stopOrPausePlayer();
				mForeground = false;
			}
		}else{
			if(mVideoView != null)
				mVideoView.onPause();
			stopOrPausePlayer();
			mForeground = false;
		}

		mToolbar.getMenu().close();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(LOG_TAG, "onStop");

		if (mForeground) {
			stopOrPausePlayer();
		}

		stopWaiting();
		mForeground = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mForeground = true;
		Log.d(LOG_TAG, "onResume");
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			if (!isInPictureInPictureMode()) {
				if (mVideoView != null)
					mVideoView.onResume();
			}else{
				if( supportPIP() ) {
					setPlayerOutputPosition(mVideoWidth, mVideoHeight, mScaleMode);
				}
			}
		}
		else{
			if (mVideoView != null)
				mVideoView.onResume();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mForeground = true;
		Log.d(LOG_TAG, "onStart");
		resetPlayerStatus(RESET_PLAYER_BASIC);

		waitingForKeyGuardUnlock();
	}

	private void waitingForKeyGuardUnlock() {
		mWaitingThread = new WaitingForUnlockThread();
		mWaitingThread.run();
	}

	private void notifyKeyGuardUnlocked() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if ( mPrefData.mHomeButtonMode == NexPreferenceData.HOME_BUTTON_MODE_STOP )
					startPlayer();
				else {
					mNeedResume = !mFastPlay;
					resumePlayer();
				}
			}
		});
	}

	private void stopWaiting() {
		if( mWaitingThread != null && mWaitingThread.isAlive() ) {
			mWaitingThread.interrupt();
			mWaitingThread = null;
		}
	}

	private void resumePlayer() {
		if( mNexPlayer.getState()==NexPlayer.NEXPLAYER_STATE_PAUSE && !mIsHeadsetRemoved || mNeedResume ) {
			mNexPlayer.resume();
			mNeedResume = false;
		}
	}

	private void startPlayer() {
		if( mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_STOP &&
				mPlayerState != PLAYER_FLOW_STATE.BEGINNING_OF_ONERROR &&
				mPlayerState != PLAYER_FLOW_STATE.END_OF_ONERROR ) {
			int startSec = (mIsLive ? 0: mSeekBar.getProgress());

			if( !mNeedStartSeekBarTime ) {
				startSec = mPrefData.mStartSec * 1000;
			}
			mNeedStartSeekBarTime = false;
			mNexPlayer.start(startSec);
			mSeekBar.setCurrentTimeText(startSec);

		}
	}

	private void resetPlayerStatus(int resetMode) {
		switch (resetMode) {
			case RESET_PLAYER_ALL:
				mStrExternalPDFile = null;
				mExternalPDBufferDuration = 0;
				mVideoWidth = 0;
				mVideoHeight = 0;
				mIsLive = false;
				mAudioInitEnd = false;
				mContentInfo = null;
				mTempVideoStreamID = NexPlayer.MEDIA_STREAM_DEFAULT_ID;
				mExistFollowingContentPath = false;
				mCaptionStreamList.clear();

				mCaptionIndex = -1;
				mABRController.setABREnabled(true);
			case RESET_PLAYER_PROGRESS:
			case RESET_PLAYER_BASIC:
				resetSeekStatus();
				mCaptionLanquageDialog.reset();
				break;
			default:
				break;
		}

		mHandler.post(new Runnable() {
			int mode;

			public Runnable init(int resetMode) {
				this.mode = resetMode;
				return(this);
			}

			@Override
			public void run() {

				mTimedMetaTextView.setText("");
				mCaptionPainter.clear();

				switch (mode) {
					case RESET_PLAYER_ALL:
						mSeekBar.resetSeekBarStatus();

						mErrorView.setVisibility(View.INVISIBLE);
						mErrorView.requestLayout();
						mGoToLiveButton.setVisibility(View.INVISIBLE);
						mGoToLiveButton.requestLayout();

						mImageView.setImageResource(R.drawable.audio_skin2);
						mImageView.setVisibility(View.INVISIBLE);
						mLyricTextView.setText("");
						mLyricView.setText("");

						mTrackView.setText("Track : " + 0);

						mCaptionPainter.setCaptionType(mPrefData.mCaptionMode == 0 ? NexContentInformation.NEX_TEXT_CEA608 : NexContentInformation.NEX_TEXT_CEA708);

					case RESET_PLAYER_PROGRESS:
						mSeekBar.setProgress(0);
					case RESET_PLAYER_BASIC:
						mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
						if( mTargetBandWidthDialog.isShowing() )
							mTargetBandWidthDialog.dismiss();
						break;
					default:
						break;
				}
			}
		}.init(resetMode));
	}

	@Override
	protected void onDestroy() {
		setupTimer(false);
		stopDownload();
		stopPlayer();
		releasePlayer();
		mVideoView.release();
		unregisterReceivers();
		PlayerEnginePreLoader.deleteAPKAsset(this);
		if(mCurrentPath != null) {
			getContentPath(REPLAY_MODE_QUIT);
		}
		super.onDestroy();
	}

	private void enableDynamicThumbnail() {
		if( mPrefData.mEnableDynamicThumbnail )
			mNexPlayer.enableDynamicThumbnail();
	}

	private void disableDynamicThumbnail() {
		if( mPrefData.mEnableDynamicThumbnail ) {
			mNexPlayer.disableDynamicThumbnail();
			mSeekBar.enableDynamicThumbnail(false);
			mSeekBar.resetThumbnailStatus();
		}
	}

	private void closePlayer() {
		disableDynamicThumbnail();
		mNexPlayer.close();
	}

	private void stopPlayer() {
		mHandler.removeCallbacks(updateStatisticsThread);

		if( mNexPlayer.getState() > NexPlayer.NEXPLAYER_STATE_STOP ) {
			mNexPlayer.stop();
			try {
				while(mNexPlayer.getState() !=NexPlayer.NEXPLAYER_STATE_STOP) {
					Thread.sleep(100);
				}
			}
			catch (InterruptedException e) {
				Log.e(LOG_TAG, "Exception - stopPlayer() : " + e.getMessage());
			}
		}
	}

	private void unregisterReceivers() {
		unregisterReceiver(mBroadcastReceiver);
		NetworkBroadcastReceiver.removeListener(this);
	}

	private void stopDownload() {
		try {
			if(mStrExternalPDFile != null) {
				int dw_tries = 0;
				if(NexPlayer.NEXDOWNLOADER_STATE_DOWNLOAD == mDownloaderState) {
					mNexPlayer.DownloaderStop();
				}
				else if (mDownloaderState == NexPlayer.NEXDOWNLOADER_STATE_STOP) {
					mNexPlayer.DownloaderClose();
				}
				while (NexPlayer.NEXDOWNLOADER_STATE_CLOSED != mDownloaderState) {
					if (NexPlayer.NEXDOWNLOADER_STATE_NONE == mDownloaderState) {
						break;
					}
					if (dw_tries > 30) {
						break;
					}
					Thread.sleep(100);
					dw_tries++;
				}
			}
		}
		catch (Exception e) {
			Log.e(LOG_TAG, "Exception - stopDownload() : " + e.getMessage());
		}
	}

	private void releasePlayer() {
		try {
			if (mNexPlayer != null) {
				if (mNexPlayer.getState() > NexPlayer.NEXPLAYER_STATE_CLOSED) {
					closePlayer();
				}
				//NexWVSWDrm start
				if(mPrefData.mEnableWVSWDRM && mNexWVDRM != null ) {
					mNexWVDRM.releaseDRMManager();
				}
				//NexWVSWDrm end
				mNexPlayer.release();
				mNexALFactory.release();
			}
		}
		catch (Exception e) {
			Log.e(LOG_TAG, "Exception - releasePlayer() : " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void getIntentExtra() {
		Intent intent = getIntent();

		Uri uri = intent.getData();

		if(uri != null) {
			mIsStreaming = false;
			String tmpString;

			String proj[] = { MediaStore.Video.Media._ID,
					MediaStore.Video.Media.DATA};

			ContentResolver cr = getContentResolver();
			Cursor videoCursor = null;
			try {
				videoCursor = cr.query(uri, proj, null, null, null);
			}
			catch(Exception e) {
				Log.e(LOG_TAG, "Exception - getIntentExtra() : " + e.getMessage());
			}

			if (videoCursor != null && videoCursor.moveToFirst()) {
				int videoDataCol = videoCursor
						.getColumnIndex(MediaStore.Video.Media.DATA);
				String url = videoCursor.getString(videoDataCol);

				if (url != null && url.length() > 0) {
					mCurrentPath = url;
				}

				videoCursor.close();
			}
			else {
				tmpString = Uri.decode(uri.toString());

				if (tmpString.startsWith("http://")
						|| tmpString.startsWith("https://")
						|| tmpString.startsWith("rtsp://")
						|| tmpString.startsWith("mms://")) {
					mIsStreaming = true;
					mCurrentPath = tmpString;
				} else if (tmpString.startsWith("file://")) {
					String path = tmpString.replaceFirst("file://", "");
					mCurrentPath = path;
					mTargetSubtitlePath = NexFileIO.subtitlePathFromMediaPath(mCurrentPath);
				}
			}

		}
		else {
			mFileList = (ArrayList<File>) intent.getSerializableExtra("data");
			mNxbWholeList = (ArrayList<NxbInfo>) intent.getSerializableExtra("wholelist");

			mCurrentPosition = intent.getIntExtra("selectedItem", 0);

			if (mFileList != null)
			{
				mIsStreaming = false;
				mCurrentPath = mFileList.get(mCurrentPosition).getAbsolutePath();
				mTargetSubtitlePath = NexFileIO.subtitlePathFromMediaPath(mCurrentPath);
			}
			else
			{
				mIsStreaming = true;
				mCurrentPath = intent.getStringExtra("theSimpleUrl");
				if ( mCurrentPath != null ) {
					mCurrentPath = mCurrentPath.trim();
				}

				mCurrentExtraData =  getNxbExtraData();
				mTargetSubtitlePath = getFirstSubtitlePath();
			}
		}
	}

	private String getNxbExtraData() {
		String extraData = null;
		NxbInfo info = NxbInfo.getNxbInfo(mNxbWholeList, mCurrentPath, mCurrentPosition);
		extraData = info.getExtra();
		return extraData;
	}

	private boolean isNeededExtraSetting(String type) {
		NxbInfo info = NxbInfo.getNxbInfo(mNxbWholeList, mCurrentPath, mCurrentPosition);
		Log.d(LOG_TAG, "isNeededExtraSetting info.getType() : " + info.getType());
		boolean ret = info.getType().equalsIgnoreCase(type);
		if( type.equalsIgnoreCase(NxbInfo.MEDIADRM) && !ret ) {
			if( mCurrentPath.endsWith(NexFileIO.STORE_INFO_EXTENSION) ) {
				JSONObject obj = NexStoredInfoFileUtils.parseJSONObject(new File(mCurrentPath));
				if( obj != null ) {
					try {
						ret = !TextUtils.isEmpty(obj.getString(NexStoredInfoFileUtils.STORED_INFO_KEY_MEDIA_DRM_KEY_SERVER_URI));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		//do sth
		return ret;
	}

	private String getFirstSubtitlePath() {
		String path = null;
		NxbInfo info = NxbInfo.getNxbInfo(mNxbWholeList, mCurrentPath, mCurrentPosition);
		if( info.getSubtitle().size() > 0 )
			path = info.getSubtitle().get(0);
		return path;
	}

	private void setBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_HEADSET_PLUG);
		filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		NetworkBroadcastReceiver.addListener(this);

		mBroadcastReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
					if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PLAY) {
						mNexPlayer.pause();
						mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);

					}
					else if(mIsBuffering || mIsSeeking) {
						mIsHeadsetRemoved = true;
					}
				}
				else if( intent.getAction().equals(Intent.ACTION_HEADSET_PLUG) ) {
					int headSetState = intent.getExtras().getInt("state");
					if( mNexPlayer != null ) {
						mNexPlayer.notifyHeadsetState(headSetState);
					}
				}
			}
		};
		registerReceiver(mBroadcastReceiver, filter);
	}

	private void setUIComponents() {
		mTrackView = (TextView)findViewById(R.id.cur_video_track_text_view);
		setVisibilityLayout();
		setVideoRendererView();
		setCaptionPainter();
		setAlbumImageView();
		setLyricView();
		setThumbnailView();
		setGoToLiveButton();
		setSeekLayout();
		setErrorView();
		setControlButton();
		setBufferComponent();
		setTimeMeta();
		setupTimedMetaCheckBox();
		setupToolbar();
		setupUTCTime();
	}

	private void setupTimedMetaCheckBox() {
		final CheckBox check = (CheckBox)findViewById(R.id.check_timed_meta_text);
		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int visibility = View.VISIBLE;
				int stringRes = R.string.show_timed_meta_text;

				if( !isChecked ) {
					visibility = View.INVISIBLE;
					stringRes = R.string.hide_timed_meta_text;
				}

				mTimedMetaTextView.setVisibility(visibility);
				check.setText(stringRes);
			}
		});
	}

	private void setupUTCTime(){
		mUTCTime = (TextView)findViewById(R.id.text_utc_time);
		mUTCTime.setVisibility(View.VISIBLE);
		mTimeHandler = new SendTimeHandler();
		mTimeHandler.sendEmptyMessage(0);

	}

	class SendTimeHandler extends Handler{

		@Override
		public void handleMessage(Message msg){

			super.handleMessage(msg);
			switch(msg.what){

				case MSG_START_TIME:
					mTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
					break;
				case MSG_UPDATE_TIME:
					DateFormat df = DateFormat.getTimeInstance();
					String utcTime = df.format(new Date());
					mUTCTime.setText(utcTime);
					mTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 100);
					break;


			}
		}
	}



	private void setupToolbar() {
		mToolbar = (Toolbar)findViewById(R.id.tool_bar);
		mToolbar.setTitle("");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setTimeMeta() {
		mTimedMetaTextView = (TextView)findViewById(R.id.time_meta_textview);
		mLyricTextView = (TextView)findViewById(R.id.time_lyirc_textview);
		mLyricTextView.setGravity(Gravity.CENTER);
		mLyricTextView.setTextColor(Color.RED);
		mTimedMetaTextView.setText("");
	}

	private void setGoToLiveButton() {
		mGoToLiveButton = (NexImageButton)findViewById(R.id.go_to_live_button);
		mGoToLiveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				long[] seekableRange = mNexPlayer.getSeekableRangeInfo();

				if(seekableRange != null) {
					int seekbarCriterion = (int)seekableRange[0];
					int seekbarMaxLength = (int)seekableRange[1] - seekbarCriterion;
					mProgressBase = 0;

					mSeekBar.setMax(seekbarMaxLength);
					mSeekBar.setProgress(seekbarMaxLength);
					mNexPlayer.gotoCurrentLivePosition();
				}
			}
		});
	}
	private void setBufferComponent() {
		mProgressLayout = (RelativeLayout)findViewById(R.id.progress_layout);
		mProgressTextView = (TextView)findViewById(R.id.progress_text);
	}

	@SuppressLint("NewApi")
	private void setVideoRendererView() {
		{
			mVideoView = (NexVideoRenderer)findViewById(R.id.videoview);
			boolean useSurfaceTexture = mPrefData.mUseSurfaceTextrue;
			boolean useRenderThreadWithSurfaceTexture = mPrefData.mUseRenderThread;
			((NexVideoRenderer)mVideoView).setUseSurfaceTexture(useSurfaceTexture, useRenderThreadWithSurfaceTexture);
		}

		mVideoView.setVisibility(View.VISIBLE);
		if(mPrefData.mColorSpace == 1)
			mVideoView.setScreenPixelFormat(PixelFormat.RGB_565);
		else
			mVideoView.setScreenPixelFormat(PixelFormat.RGBA_8888);

		mVideoView.setListener(new NexVideoRenderer.IListener()
		{

			@Override
			public void onVideoSizeChanged()
			{
				Point videoSize = new Point();
				mVideoView.getVideoSize(videoSize);

				mVideoWidth = videoSize.x;
				mVideoHeight = videoSize.y;

				if (null != mContentInfo) {
					if (90 == mContentInfo.mRotationDegree || 270 == mContentInfo.mRotationDegree) {
						int rotationWidth = mVideoWidth;
						mVideoWidth = mVideoHeight;
						mVideoHeight = rotationWidth;
					}
				}

				setPlayerOutputPosition(mVideoWidth, mVideoHeight, mScaleMode);
			}

			@Override
			public void onSizeChanged()
			{
				setPlayerOutputPosition(mVideoWidth, mVideoHeight, mScaleMode);
			}

			@Override
			public void onFirstVideoRenderCreate()
			{
				/* Initialization of mScaleMode has been done at the Activity Instance Initialization
				 * Not every time a new playback begins: NPDS-2404
				 */
				setPlayerOutputPosition(mVideoWidth, mVideoHeight, mScaleMode);
			}

			@Override
			public void onDisplayedRectChanged()
			{
			}
		});
		mVideoView.setPostNexPlayerVideoRendererListener(mEventReceiver);
	}

	private void setCaptionPainter() {
		mCaptionPainter = (NexCaptionPainter) findViewById(R.id.NexCaptionPainter);
	}

	private void setAlbumImageView() {
		mImageView = (ImageView)findViewById(R.id.imageview);
		mImageView.setImageResource(R.drawable.audio_skin2);
		mImageView.setBackgroundColor(Color.BLACK);
		mImageView.setVisibility(View.INVISIBLE);
	}

	private void setLyricView() {
		mLyricScrollView = (ScrollView)findViewById(R.id.lyric_scrollview);
		mLyricView = (TextView)findViewById(R.id.lyricview);
		mLyricView.setOnClickListener(mOnClickListener);
	}

	private void setThumbnailView() {
		mThumbnailView = (ImageView)findViewById(R.id.thumbnailView);
		mThumbnailView.setVisibility(View.INVISIBLE);
	}

	View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			setControllerVisibility(mVisibilityLayout.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
		}
	};

	private void setVisibilityLayout() {
		mVisibilityLayout = (ViewGroup)findViewById(R.id.visibility_layout);
		mParentView = (RelativeLayout)findViewById(R.id.parent_view);
		mParentView.setOnClickListener(mOnClickListener);
		mParentView.setBackgroundColor(Color.BLACK);
	}

	private void setControllerVisibility(final int visibility) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mVisibilityLayout.setVisibility(visibility);
				mVisibilityLayout.requestLayout();
				CheckBox check = (CheckBox)findViewById(R.id.check_timed_meta_text);
				check.setVisibility(visibility);
				ActionBar toolbar = getSupportActionBar();
				if( toolbar != null ) {
					if (visibility == View.VISIBLE && !toolbar.isShowing()) {
						toolbar.show();
					} else if (visibility == View.INVISIBLE && toolbar.isShowing()) {
						toolbar.hide();
					}
				}
			}
		});
	}

	private void createAndShowContentInfoDialog() {
		if( mContentInfo != null ) {
			if( mContentInfoDialog == null )
				mContentInfoDialog = new NexContentInfoDialog(this, new NexContentInfoDialog.IListener() {
					@Override
					public int getAC3DecoderType() {
						return mNexPlayer.getProperties(DolbyAC3Dialog.AC3_PROPERTY_DECODER_TYPE);
					}
				});

			mContentInfoDialog.setContentInfo(mContentInfo);
			mContentInfoDialog.show();
		}
	}

	private void setSeekLayout() {
		mSeekBar = (ThumbnailSeekBar)findViewById(R.id.seek_layout);
		mSeekBar.setOnSeekBarChangeListener(new ThumbnailSeekBar.ISeekBarListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (mNexPlayer != null && mForeground) {
					int state = mNexPlayer.getState();

					if (mFastPlay)
						stopFastPlay();

					if (state > NexPlayer.NEXPLAYER_STATE_STOP) {
						int position = seekBar.getProgress();
						clearCaptionString();

						boolean shouldSeek = true;
						if (mIsLive) {
							long[] seekableRange = mNexPlayer.getSeekableRangeInfo();
							if (seekableRange != null) {
								position += (int) seekableRange[0];
								position = Math.min(position, (int) seekableRange[1]);
							} else {
								shouldSeek = false;
							}
						} else if (mStrExternalPDFile != null) {
							if (position > mExternalPDBufferDuration) {
								shouldSeek = false;
							}
						}

						if (shouldSeek) {
							if (mIsBuffering || mIsSeeking) {
								mSeekPoint = position;
								return;
							}

							mIsSeeking = (mNexPlayer.seek(position) == 0);
						}
					} else if (mNexPlayer != null && mPlayerState == PLAYER_FLOW_STATE.END_OF_ONERROR) {
						mNeedStartSeekBarTime = true;
					}
				}
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress) {
				if (mIsLive) {
					progress += mProgressBase;
				}
				Log.i(LOG_TAG,"Current Progress " + progress);
				mSeekBar.setCurrentTimeText(progress);
			}
		});
	}

	private void setErrorView() {
		mErrorView = (TextView)findViewById(R.id.error_textview);
		mErrorView.setVisibility(View.INVISIBLE);
	}

	private void setControlButton() {
		setupPreviousButton();
		setupRewindButton();
		setupPlayPauseButton();
		setupFastForwardButton();
		setupNextButton();
	}

	private void setupPreviousButton() {
		mPreviousButton = (NexImageButton)findViewById(R.id.prev_button);
		mPreviousButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				replay(REPLAY_MODE_PREVIOUS);
			}
		});
	}

	private void setupRewindButton() {
		mRewindButton = (NexImageButton)findViewById(R.id.rewind_button);
		mRewindButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if( mFastPlay ) {
					changeFastPlaySpeed(FastPlayUtil.getFastPlaySpeed(mFastPlaySpeed, false));
				} else {
					rewind();
				}
			}
		});
		mRewindButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				int result = -1;

				if (!mFastPlay) {
					result = startFastPlay(FastPlayUtil.getFastPlaySpeed(mFastPlaySpeed, false));
				}

				return result == 0;
			}
		});
	}

	private void rewind() {
		if( mNexPlayer != null && mNexPlayer.isInitialized()
				&& (mNexPlayer.getState() > NexPlayer.NEXPLAYER_STATE_STOP) && mForeground) {
			int seekPosition = mSeekBar.getProgress() - (mPrefData.mSeekOffset * 1000);
			int min = 0;
			if( mSeekPoint >= 0 ) {
				seekPosition = mSeekPoint - (mPrefData.mSeekOffset * 1000);
			}

			if( mIsLive ) {
				long[] seekableRange = mNexPlayer.getSeekableRangeInfo();
				if( seekableRange != null ) {
					seekPosition += seekableRange[0];
					min = (int)seekableRange[0];
				}
			}

			seekPosition = Math.max(min, seekPosition);

			if( mIsBuffering || mIsSeeking ) {
				mSeekPoint = seekPosition;
				return;
			}

			mIsSeeking = (mNexPlayer.seek(seekPosition) == 0);
		}
	}

	private void setupNextButton() {
		mNextButton = (NexImageButton)findViewById(R.id.next_button);
		mNextButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				replay(REPLAY_MODE_NEXT);
			}
		});
	}

	private void replay(int replayMode) {
		if (mPlayerState != PLAYER_FLOW_STATE.END_OF_COMPLETE &&
				mPlayerState != PLAYER_FLOW_STATE.END_OF_ONERROR && mPlayerState != PLAYER_FLOW_STATE.START_PLAY) {
			return;
		}

		int state = mNexPlayer.getState();

		if( state != NexPlayer.NEXPLAYER_STATE_STOP &&
				state != NexPlayer.NEXPLAYER_STATE_NONE ) {
			if (mFastPlay)
				stopFastPlay();

			if (mStrExternalPDFile != null)
				stopDownload();

			resetPlayerStatus(RESET_PLAYER_ALL);
			getContentPath(replayMode);

			if (state == NexPlayer.NEXPLAYER_STATE_CLOSED) {
				startPlay();
			} else if (state > NexPlayer.NEXPLAYER_STATE_STOP) {
				stopPlayer();
			}
		}
	}

	private void setupFastForwardButton() {
		mFastForwardButton = (NexImageButton)findViewById(R.id.fastforward_button);
		mFastForwardButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if( mFastPlay ) {
					changeFastPlaySpeed(FastPlayUtil.getFastPlaySpeed(mFastPlaySpeed, true));
				} else {
					fastForward();
				}
			}
		});
		mFastForwardButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				int result = -1;

				if (!mFastPlay) {
					result = startFastPlay(FastPlayUtil.getFastPlaySpeed(mFastPlaySpeed, true));
				}

				return result == 0;
			}
		});
	}

	private void fastForward() {
		if(mNexPlayer != null && mNexPlayer.isInitialized()
				&& (mNexPlayer.getState() > NexPlayer.NEXPLAYER_STATE_STOP) && mForeground) {
			int position;
			int duration = mNexPlayer.getContentInfoInt(NexPlayer.CONTENT_INFO_INDEX_MEDIA_DURATION);

			if(mSeekPoint > -1) {
				position = mSeekPoint + (mPrefData.mSeekOffset * 1000);
			}
			else {
				position = mSeekBar.getProgress() + (mPrefData.mSeekOffset * 1000);
			}

			boolean shouldSeek = true;
			if ( mIsLive ) {
				long[] seekableRange = mNexPlayer.getSeekableRangeInfo();
				if ( seekableRange != null ) {
					position += (int)seekableRange[0];
					position = Math.min(position, (int)seekableRange[1]);
				} else {
					shouldSeek = false;
				}
			} else if ( mStrExternalPDFile != null ) {
				if ( position > mExternalPDBufferDuration ) {
					shouldSeek = false;
				}
			} else {
				position = Math.min(position, duration);
			}

			if( shouldSeek ) {
				if ( mIsBuffering || mIsSeeking ) {
					mSeekPoint = position;
					return;
				}

				mIsSeeking = (mNexPlayer.seek(position) == 0);
			}
		}
	}

	public void setupTimer(boolean enable)
	{
		if(clientTimeshiftTimer == null && enable)
		{
			int timerDuration = mPrefData.mTimeShiftMaxBufferDuration * 60 * 1000; //unit is min.
			clientTimeshiftTimer = new CountDownTimer(timerDuration, 60*1000) {

				@Override
				public void onTick(long millisUntilFinished) {

				}

				@Override
				public void onFinish() {
					if(mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PAUSE)
						mNexPlayer.resume();
				}
			};
		}

		if(enable)
		{
			clientTimeshiftTimer.start();
		}
		else if(clientTimeshiftTimer != null && enable == false)
		{
			clientTimeshiftTimer.cancel();
			clientTimeshiftTimer = null;
		}
	}

	private void setupPlayPauseButton() {
		mPlayPauseButton = (NexImageButton)findViewById(R.id.play_pause_button);
		mPlayPauseButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mPlayerState != PLAYER_FLOW_STATE.END_OF_COMPLETE &&
						mPlayerState != PLAYER_FLOW_STATE.END_OF_ONERROR) {
					return;
				}

				if(mNexPlayer != null && mNexPlayer.isInitialized()) {
					int state = mNexPlayer.getState();

					if( state == NexPlayer.NEXPLAYER_STATE_PLAY ) {
						int ret = mNexPlayer.pause();
						if(mIsLive &&  mPrefData.mEnableClientSideTimeShift && ret == 0)
						{
							//create timer.
							NexPlayerSample.this.setupTimer(true);
						}
						mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
					}
					else if( state == NexPlayer.NEXPLAYER_STATE_PAUSE ) {
						int ret = mNexPlayer.resume();
						if(mIsLive && mPrefData.mEnableClientSideTimeShift && ret == 0)
						{
							NexPlayerSample.this.setupTimer(false);
						}
						mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
					}
					else if( state == NexPlayer.NEXPLAYER_STATE_CLOSED ) {
						mErrorView.setVisibility(View.INVISIBLE);
						mErrorView.requestLayout();
						startPlay();
					}
					else if( state == NexPlayer.NEXPLAYER_STATE_PLAYxN ) {
						if( mFastPlay )
							stopFastPlay();
					}
					else if( state == NexPlayer.NEXPLAYER_STATE_STOP ) {
						startPlayer();
					}
				}
			}
		});
	}

	private void stopFastPlay() {
		mNexPlayer.fastPlayStop(mForeground ? true : false);
		mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
		mPlayPauseButton.setText("");
		mFastPlay = false;
		mFastPlaySpeed = 1.0f;
	}

	private int startFastPlay(float speed) {
		int result = -1;
		if( FastPlayUtil.isFastPlayPossible(mContentInfo, mPrefData.mMinBandWidth, mPrefData.mMaxBandWidth)
				&& mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PLAY ) {
			result = mNexPlayer.fastPlayStart(mNexPlayer.getCurrentPosition(), speed);

			if( result == 0 ) {
				mFastPlaySpeed = speed;
				mFastPlay = true;
				mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
				mPlayPauseButton.setText("" + (int) mFastPlaySpeed + "X");
				clearCaptionString();
			}
		}
		return result;
	}

	private void changeFastPlaySpeed(float speed) {
		int result = mNexPlayer.fastPlaySetPlaybackRate(speed);
		if( result == 0 ) {
			mFastPlaySpeed = speed;
			mPlayPauseButton.setText("" + (int) mFastPlaySpeed + "X");
		}
	}

	@SuppressLint("NewApi")
	private int setPlayer() {
		mNexPlayer = new NexPlayer();
		mNexALFactory = new NexALFactory();
		System.gc();

		int debugLogLevel = mPrefData.mLogLevel;

		if( debugLogLevel< 0 )
			debugLogLevel = 0xF0000000;


		//captionRenderer.createRenderView(this);

		if(mNexALFactory.init(this, android.os.Build.MODEL, mPrefData.mRenderMode,debugLogLevel, 1) == false) {
			showErrorStatus("ALFactory initialization failed");
			return -2;
		}

		mNexPlayer.setLicenseFile("/sdcard/test_lic.xml");

		mNexPlayer.setNexALFactory(mNexALFactory);
		if(mNexPlayer.init(this, mPrefData.mLogLevel) == false) {
			showErrorStatus("NexPlayer initialization failed");
			return -3;
		}

		setProperties();

		// If you want to set custom address by hostname, please use below codes.
		//setCustomNetAddrTable();


		addEventReceiver();

		mABRController = new NexABRController(mNexPlayer);
		mABRController.setIABREventListener(new NexABRController.IABREventListener() {
			@Override
			public void onMinMaxBandWidthChanged(NexErrorCode result, int minBwBps, int maxBwBps) {
				if( result == NexErrorCode.NONE ) {
					mPrefData.setMinMaxBandwidth(minBwBps/BANDWIDTH_KBPS, maxBwBps/BANDWIDTH_KBPS);
				}
				Log.d(LOG_TAG,"onMinMaxBandWidthChanged (Result : " + result +", Min : " + minBwBps + ",  Max : " + maxBwBps + ")");
			}

			@Override
			public void onTargetBandWidthChanged(NexErrorCode result, int reqBwBps, int selBwBps) {
				//Log.d(LOG_TAG,"onTargetBandwidthChanged (Result : " + result +", Min : " + reqBwBps + ",  Max : " + selBwBps + ")");
			}
		});

		mVideoView.init(mNexPlayer);
		mVideoView.setVisibility(View.VISIBLE);

		return 0;
	}

	protected void addEventReceiver() {
		mEventReceiver = new NexEventReceiver() {
			@Override
			public void onAsyncCmdComplete(NexPlayer mp, int command, int result, int param1, int param2) {
				super.onAsyncCmdComplete(mp, command, result, param1, param2);

				Log.d(LOG_TAG, "onAsyncCmdComplete : mp : " + mp + " command : " + command + ", result : " + result);
				switch (command) {
					case NexPlayer.NEXPLAYER_ASYNC_CMD_OPEN_LOCAL:
					case NexPlayer.NEXPLAYER_ASYNC_CMD_OPEN_STREAMING:
						Log.d(LOG_TAG, "onAsyncCmdComplete : OPEN");
						clearBufferStatus();
						mExistFollowingContentPath = false;
						mPlayerState = PLAYER_FLOW_STATE.BEGINNING_OF_COMPLETE;
						mIsEnginOpen = true;

						if(result == 0) {
							final int duration = mNexPlayer.getContentInfoInt(NexPlayer.CONTENT_INFO_INDEX_MEDIA_DURATION);
							mCaptionStreamList.clear();
							mContentInfo = mNexPlayer.getContentInfo();
							if( mContentInfoDialog != null )
								mContentInfoDialog.setContentInfo(mContentInfo);

							if ( !TextUtils.isEmpty(mTargetSubtitlePath) ){
								result = mNexPlayer.changeSubtitlePath(mTargetSubtitlePath);
								if( result != 0 && result != 1 ) {
									showToastMsg(getString(R.string.invalid_subtitle_path));
								}
							}

							if( duration < 0 ) {
								mIsLive = true;
								if( mIsStoreManagerInitialized ) {
									onError(mp, NexErrorCode.UNKNOWN);
									return;
								}
							}

							setDynamicThumbnailOption(mContentInfo);
							updateControllerVisibility();

							mCaptionStreamList.setPreferCCType(mPrefData.mCaptionMode == 0 ? NexContentInformation.NEX_TEXT_CEA608 : NexContentInformation.NEX_TEXT_CEA708);
							mCaptionStreamList.setCaptionStreams(mContentInfo);
							setCaptionType();

							mHandler.post(new Runnable() {
								@Override
								public void run() {
									try {String text = "Track : " + NexContentInfoExtractor.getCurrTrackID(NexPlayer.MEDIA_STREAM_TYPE_VIDEO, mContentInfo);
										mTrackView.setText(text);

										setVolume();
										if( mCurrentPath != null )
											mToolbar.setTitle(NexFileIO.getContentTitle(mCurrentPath));

										mSeekBar.setMax(duration);
										mSeekBar.setDurationTimeText(duration);

										if(mContentInfo.mMediaType == 1) {
											setAlbumImage();
											setLyric();
										}
										else {
											mImageView.setVisibility(View.INVISIBLE);
											mLyricView.setText("");
											mLyricScrollView.setVisibility(View.INVISIBLE);
										}

										if( mForeground ) {
											startPlayer();
										}
									}
									catch (Throwable e) {
										Log.d(LOG_TAG, "Exception - onAsyncCmdComplete : OPEN : " + e.getMessage() );
										e.printStackTrace();
									}
								}
							});
						}
						else {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									if( mCurrentPath != null )
										mToolbar.setTitle(NexFileIO.getContentTitle(mCurrentPath));
								}
							});
							onError(mp, NexErrorCode.fromIntegerValue(result));
						}
						break;
					case NexPlayer.NEXPLAYER_ASYNC_CMD_OPEN_STORE_STREAM :
						Log.d(LOG_TAG, "onAsyncCmdComplete : OPEN STORE STREAM");
						clearBufferStatus();
						mPlayerState = PLAYER_FLOW_STATE.BEGINNING_OF_COMPLETE;
						mIsEnginOpen = true;

						if(result == 0) {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									try {
										if( mForeground ) {
											startPlayer();
										}
									}
									catch (Throwable e) {
										Log.d(LOG_TAG, "Exception - onAsyncCmdComplete : OPEN STORE STREAM: " + e.getMessage() );
										e.printStackTrace();
									}
								}
							});

						}
						else {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									if( mCurrentPath != null )
										mToolbar.setTitle(NexFileIO.getContentTitle(mCurrentPath));
								}
							});
							onError(mp, NexErrorCode.fromIntegerValue(result));
						}
						break;
					case NexPlayer.NEXPLAYER_ASYNC_CMD_START_LOCAL:
					case NexPlayer.NEXPLAYER_ASYNC_CMD_START_STREAMING:
						Log.d(LOG_TAG, "onAsyncCmdComplete : START");


						mPlayerState = PLAYER_FLOW_STATE.BEGINNING_OF_COMPLETE;



						if(result == 0) {
							if(mIsStreaming) {
								NxbInfo info = NxbInfo.getNxbInfo(mNxbWholeList, mCurrentPath, mCurrentPosition);
								Intent intent = getIntent();
								ArrayList<String> optionalHeaders = intent.getStringArrayListExtra("WVDRMOptionalHeaders");
								String optionalStrings = "";
								if(optionalHeaders != null) {
									for (String s : optionalHeaders) {
										optionalStrings += (s + "/");
									}
								}

								if( PlaybackHistory.isExist(NexPlayerSample.this, info) ) {
									PlaybackHistory.updateHistory(NexPlayerSample.this,	info, optionalStrings);
								} else {
									PlaybackHistory.addHistory(NexPlayerSample.this, info, optionalStrings);
								}
							}

							mHandler.post(new Runnable() {
								@SuppressLint("NewApi")
								@Override
								public void run() {
									mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);

								}
							});

							mHandler.postDelayed(updateStatisticsThread, 1000);
						}
						else {
							onError(mp, NexErrorCode.fromIntegerValue(result));
						}

						if( mPlayerState == PLAYER_FLOW_STATE.BEGINNING_OF_COMPLETE ) {
							mPlayerState = PLAYER_FLOW_STATE.END_OF_COMPLETE;
						}
						break;
					case NexPlayer.NEXPLAYER_ASYNC_CMD_PAUSE:
						if( mIsLive && mForeground )
							scheduleSeekableRangeTimeTask();
						break;
					case NexPlayer.NEXPLAYER_ASYNC_CMD_STOP:
						Log.d(LOG_TAG, "onAsyncCmdComplete : STOP");

						clearCaptionString();

						cancelSeekableRangeTimer();
						postProcessingForStopCmd();

						break;
					case NexPlayer.NEXPLAYER_ASYNC_CMD_SET_MEDIA_STREAM:
						Log.d(LOG_TAG, "onAsyncCmdComplete : SET_MEDIA_STREAM. param1 : " + param1 + ", param2  : " + param2);

						mPlayerState = PLAYER_FLOW_STATE.BEGINNING_OF_COMPLETE;
						mContentInfo = mNexPlayer.getContentInfo();
						mNeedResume = false;

						if(mPlayerState == PLAYER_FLOW_STATE.BEGINNING_OF_COMPLETE)
							mPlayerState = PLAYER_FLOW_STATE.END_OF_COMPLETE;

						NexPlayer.NexErrorCode errorCode = NexErrorCode.fromIntegerValue(result);

						if (NexPlayer.MEDIA_STREAM_TYPE_TEXT == param1 && NexErrorCode.NONE == errorCode) {
							clearCaptionString();
//							onTextStreamDialogUpdated 에서 선택된 Subtitle로 SetMediaStream  하므로 아래의 코드는 불필요하다.
//							if(param2 == NexPlayer.MEDIA_STREAM_DISABLE_ID){
//								mCaptionIndex = 0;
//							}
//							else if(param2 == 1) {
//								mCaptionIndex = 1;
//							}
//							else{
//								mCaptionIndex = param2 - 1;
//							}
//							Log.d("test", "NEXPLAYER_ASYNC_CMD_SET_MEDIA_STREAM : " + mCaptionIndex + "," + param2 );
//							mCaptionStreamList.setEnable(mCaptionIndex);
						}

						break;
					case NexPlayer.NEXPLAYER_ASYNC_CMD_SEEK:
						Log.d(LOG_TAG, "onAsyncCmdComplete : SEEK");
						if( mSeekPoint > -1 ) {
							if( mForeground )
								mNexPlayer.seek(mSeekPoint);
							else
								mIsSeeking = false;
							mSeekPoint = -1;
						}
						else if( mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_PAUSE ) {
							mSeekBar.setProgress(mNexPlayer.getCurrentPosition());
							mIsSeeking = false;
						} else {
							if( mIsHeadsetRemoved || !mForeground ) {
								mHandler.post(new Runnable() {

									@Override
									public void run() {
										mNexPlayer.pause();
										mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
										mIsHeadsetRemoved = false;
									}
								});
							}

							mIsSeeking = false;
						}

						clearCaptionString();
						break;
					case NexPlayer.NEXPLAYER_ASYNC_CMD_RESUME:
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
							}
						});

						cancelSeekableRangeTimer();
						break;
					case NexPlayer.NEXPLAYER_ASYNC_CMD_SETEXTSUBTITLE:
						if( result == 0 ) {
							mCurrentSubtitlePath = mTargetSubtitlePath;

							mCaptionStreamList.setCaptionStreams(mContentInfo);
							setCaptionType();

							clearCaptionString();
						} else {
							mTargetSubtitlePath = mCurrentSubtitlePath;
							showToastMsg(getString(R.string.invalid_subtitle_path));
						}

						break;
				}
			}


			@Override
			public void onDynamicThumbnailData(NexPlayer mp, int width, int height, int cts, Object bitmap) {
				super.onDynamicThumbnailData(mp, width, height, cts, bitmap);

				if (!mSeekBar.isThumbnailArrayFull()) {
					Bitmap bm = Bitmap.createBitmap(width, height, Config.RGB_565);
					ByteBuffer buffer = (ByteBuffer) bitmap;
					bm.copyPixelsFromBuffer(buffer.asIntBuffer());
					mSeekBar.addDynamicThumbnailInfo(cts, bm);
				}
			}

			@Override
			public void onDynamicThumbnailRecvEnd(NexPlayer mp) {
				super.onDynamicThumbnailRecvEnd(mp);

				mSeekBar.dynamicThumbnailRecvEnd();
			}

			@Override
			public byte[] onOfflineKeyRetrieveListener(NexPlayer mp) {
				byte[] keyId = null;

				if( mFileList != null ) {
					File file = new File(mCurrentPath);
					JSONObject obj = NexStoredInfoFileUtils.parseJSONObject(file);
					if( obj != null ) {
						try {
							String sKeyId = obj.getString(NexStoredInfoFileUtils.STORED_INFO_KEY_OFFLINE_KEY_ID);
							if(sKeyId != null)
								keyId = Base64.decode(sKeyId, Base64.DEFAULT);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				return keyId;
			}


			@Override
			public void onOfflineKeyStoreListener(NexPlayer mp, byte[] keyId) {
			}

			@Override
			public void onEndOfContent(NexPlayer mp) {
				getContentPath(mPrefData.mReplayMode);

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if( mFastPlay )
							stopFastPlay();
						else
							mNexPlayer.stop();
					}
				});
			}

			@Override
			public void onTime(NexPlayer mp, int millisec) {
				showProgressBar(millisec);
			}

			@Override
			public void onHTTPResponse(NexPlayer mp, String strResponse) {
				playerSampleUtils.logHTTPResponse(strResponse);
			}

			@Override
			public void onHTTPRequest(NexPlayer mp, String strRequest) {
				playerSampleUtils.logHTTPRequest(strRequest);
			}

			@Override
			public void onSessionData(NexPlayer mp, NexSessionData[] data) {
				Log.d(LOG_TAG, "onSessionData is called.");
				if(data != null)
				{
					Log.d(LOG_TAG, "length: " + data.length);
					for(int i=0;i<data.length;i++)
					{
						Log.d(LOG_TAG, "DataiD: " + data[i].mDataID + ", Value: " +
								data[i].mValue + ", URI: " + data[i].mUri + ", Language: " + data[i].mLanguage + ", Abstract URL: " +
								data[i].mAbstractUrl + ", Data: " + data[i].mDataFromUrl);
					}
				}
			}
			@Override
			public void  onDateRangeData(NexPlayer mp , NexDateRangeData[] data) {

				final NexDateRangeData[] datas;
				datas = data;
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Log.d(LOG_TAG,"onDateRangeData is called.");
						Log.d(LOG_TAG,"length: " + datas.length);
						for(int i=0;i<datas.length;i++)
						{
							Log.d(LOG_TAG,"ID : "+datas[i].mID + "CLASS : "+datas[i].mClass+"StartDate" + datas[i].mStartDate +"EndDate"+ datas[i].mEndDate);
							Log.d(LOG_TAG,"FullString : "+datas[i].mFullString +"SCTE35CMD : "+datas[i].mSCTE35CMD + "SCTE35IN"+datas[i].mSCTE35IN + "SCTE35OUT"+datas[i].mSCTE35OUT);
							Log.d(LOG_TAG,"Duration : "+datas[i].mDuration +"PlanDuration : "+datas[i].mPlanDuration);
							Log.d(LOG_TAG,"EndonNext : "+datas[i].mEndOnNext);
						}
					}
				});
			}

			@Override
			public void  onEmsgData(NexPlayer mp , NexEmsgData data) {

				final NexEmsgData   emsg = data;
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Log.d(LOG_TAG, "onEmsgData is called.");
						Log.d(LOG_TAG, "[Emsg] " + emsg.mStartTime + "-" + emsg.mEndTime);
						Log.d(LOG_TAG, "[Emsg] scheme_id_uri: " + emsg.mSchemeIdUri + ", value: " + emsg.mValue);
						Log.d(LOG_TAG, "[Emsg] ver: " + emsg.mVersion + ", timescale: " + emsg.mTimescale + ", presentation_time: " + emsg.mPresentationTime + ", event_duration: " + emsg.mEventDuration + ", id: " + emsg.mId);

						StringBuilder sb = new StringBuilder();
						for (final byte b : emsg.mMessageData) {
							sb.append(String.format("%c", b & 0xff));
						}
						Log.d(LOG_TAG, "[Emsg] message_data_size: " + emsg.mMessageDataSize + ", message_data: " + sb.toString());
					}
				});
			}

			@Override
			public void onError(NexPlayer mp, NexErrorCode errorcode) {
				Log.e(LOG_TAG, "onError: " + errorcode);
				if (mPlayerState == PLAYER_FLOW_STATE.BEGINNING_OF_ONERROR)
					return;

				mPlayerState = PLAYER_FLOW_STATE.BEGINNING_OF_ONERROR;

				String lineBreak = "\n";
				if ("".equals(mp.getDetailedError())) {
					lineBreak = "";
				}

				if( errorcode == null ) {
					showErrorStatus("onError : Unknown Error Occured with Invalid errorcode object");
				}
				else {
					switch (errorcode.getCategory()) {
						case API:
						case BASE:
						case NO_ERROR:
						case INTERNAL:
							showErrorStatus("An internal error occurred while attempting to open the media: "
									+ errorcode.name());
							break;

						case AUTH:
							showErrorStatus("You are not authorized to view this content, "
									+ "or it was not possible to verify your authorization, "
									+ "for the following reason:\n\n" + errorcode.getDesc());
							break;

						case CONTENT_ERROR:
							showErrorStatus("The content cannot be played back, probably because of an error in "
									+ "the format of the content (0x"
									+ Integer.toHexString(errorcode.getIntegerCode())
									+ ": " + errorcode.name() + ").");
							break;

						case NETWORK:
							showErrorStatus("The content cannot be played back because of a "
									+ "problem with the network.  This may be temporary, "
									+ "and trying again later may resolve the problem.\n("
									+ errorcode.getDesc() + lineBreak + mp.getDetailedError() + ")");
							break;

						case NOT_SUPPORT:
							showErrorStatus("The content can not be played back because it uses a "
									+ "feature which is not supported by NexPlayer.\n\n("
									+ errorcode.getDesc() + ")");
							break;

						case GENERAL:
							showErrorStatus("The content cannot be played back for the following reason:\n\n"
									+ errorcode.getDesc() + lineBreak + mp.getDetailedError());
							break;

						case PROTOCOL:
							showErrorStatus("The content cannot be played back because of a "
									+ "protocol error.  This may be due to a problem with "
									+ "the network or a problem with the server you are "
									+ "trying to access.  Trying again later may resolve "
									+ "the problem.\n(" + errorcode.name()  + lineBreak + mp.getDetailedError() + ")");
							break;
						case DOWNLOADER:
							showErrorStatus("Download has the problem\n\n(" + errorcode.name() + ")" );
							break;

						case SYSTEM:
							showErrorStatus("SYSTEM has the problem\n\n(" + errorcode.name() + ")" );
							break;
					}
				}

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						int state = mNexPlayer.getState();

						if ( state == NexPlayer.NEXPLAYER_STATE_PLAY
								|| state == NexPlayer.NEXPLAYER_STATE_PAUSE ) {
							mNexPlayer.stop();
						}
						else if( state == NexPlayer.NEXPLAYER_STATE_STOP ){
							closePlayer();
							mPlayerState = PLAYER_FLOW_STATE.END_OF_ONERROR;
						}
						else if( state == NexPlayer.NEXPLAYER_STATE_CLOSED ) {
							mPlayerState = PLAYER_FLOW_STATE.END_OF_ONERROR;
						}
					}
				});

				resetPlayerStatus(RESET_PLAYER_PROGRESS);
			}

			@Override
			public int onHTTPABRTrackChange(NexPlayer mp, int param1, int param2,
											int param3) {
				int targetBW;
				Log.d(LOG_TAG, "onHTTPABRTrackChange is called! Current BW: " + param1 + ", Current Track: " + param2 + ", Next Track: " + param3);
				targetBW = param3; // Do not change the target BW.
				Log.d(LOG_TAG, "target BW is " + targetBW);
				return targetBW;
			}

			@Override
			public void onDataInactivityTimeOut(NexPlayer mp) {
				onError(mp, NexErrorCode.DATA_INACTIVITY_TIMEOUT);
			}

			@Override
			public void onBufferingBegin(NexPlayer mp) {
				mIsBuffering = true;
				showBufferStatus(getResources().getString(R.string.buffer_start));
				enableUIControls(false);
			}

			@Override
			public void onBufferingEnd(NexPlayer mp) {
				mIsBuffering = false;
				clearBufferStatus();
				enableUIControls(true);

				if(mSeekPoint > -1) {
					if( mForeground )
						mNexPlayer.seek(mSeekPoint);
					else
						mIsSeeking = false;
					mSeekPoint = -1;
				}
			}

			@Override
			public void onBuffering(NexPlayer mp, int progress_in_percent) {
				showBufferStatus(getResources().getString(R.string.buffer_ing) +
						progress_in_percent + getResources().getString(R.string.buffer_percent));
			}

			@Override
			public void onAudioRenderCreate(NexPlayer mp, int samplingRate, int channelNum) {
				Log.d(LOG_TAG, "onAudioRenderCreate");
				mAudioInitEnd = true;
			}

			@Override
			public void onVideoRenderCreate(NexPlayer mp, int width, int height,
											Object rgbBuffer) {
				Log.d(LOG_TAG, "onVideoRenderCreate");
			}

			@Override
			public void onVideoRenderCapture(NexPlayer mp, int width, int height,
											 int pixelbyte, Object rgbBuffer) {
				final Bitmap thumbnailBitmap;
				Bitmap bitmap = Bitmap.createBitmap(width, height, pixelbyte == 2 ? Config.RGB_565 : Config.ARGB_8888);
				ByteBuffer RGBBuffer = (ByteBuffer) rgbBuffer;

				if (RGBBuffer.capacity() > 0) {
					RGBBuffer.asIntBuffer();
					bitmap.copyPixelsFromBuffer(RGBBuffer);

					thumbnailBitmap = Bitmap.createScaledBitmap(bitmap, 100, 75, true);
					bitmap.recycle();

					mHandler.post(new Runnable() {
						public void run() {
							mThumbnailView.setImageBitmap(thumbnailBitmap);
							mThumbnailView.setEnabled(true);
							mThumbnailView.setVisibility(View.VISIBLE);
							mThumbnailView.requestLayout();
						}
					});

					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						public void run() {
							mHandler.post(new Runnable() {
								public void run() {
									mThumbnailView.setVisibility(View.INVISIBLE);
								}
							});
						}
					}, 5000);
				}
				else {
					mHandler.post(new Runnable() {
						public void run() {
							mThumbnailView.setVisibility(View.VISIBLE);
							mThumbnailView.requestLayout();
						}
					});
				}
			}

			@Override
			public void onTextRenderRender(NexPlayer mp, int trackIndex, NexClosedCaption textInfo) {
				mCaptionPainter.setDataSource(textInfo);

				try {
					mCaptionStreamList.maybeActivateCCStreams(mContentInfo.mCurrTextStreamID, mCaptionPainter.getCaptionType(), textInfo.getCaptionType());
				} catch (Exception e) {
					Log.d(LOG_TAG, "Exception - addEmbeddedCCCaptionStreams() : " + e.getMessage());
				}
			}

			@Override
			public void onTimedMetaRenderRender(NexPlayer mp,
												final NexID3TagInformation TimedMeta) {
				mHandler.post(new Runnable() {
					public void run() {
						try {
							NexID3TagText text = null;
							String strInfo = "";
							String str = "";

							text = TimedMeta.getArtist();
							if( text != null && text.getTextData() != null) {
								str = new String(text.getTextData(), 0,
										text.getTextData().length,getTextEncodingType(text.getEncodingType()));
								strInfo += str + "\n";
							}

							text = TimedMeta.getTitle();
							if( text != null && text.getTextData() != null) {
								str = new String(text.getTextData(), 0,text.getTextData().length,
										getTextEncodingType(text.getEncodingType()));
								strInfo += str + "\n";
							}

							text = TimedMeta.getAlbum();
							if(text != null &&  text.getTextData() != null) {
								str = new String(text.getTextData(), 0, text.getTextData().length,
										getTextEncodingType(text.getEncodingType()));
								strInfo += str;
							}

							text = TimedMeta.getLyric();
							if (text != null)  {
								str = new String(
										text.getTextData(), 0, text.getTextData().length,
										getTextEncodingType(text.getEncodingType()));
								strInfo += str;

								mLyricTextView.setText(strInfo);
							}
							else  {
								mLyricTextView.setText(strInfo);
							}

							setTimeMetaImage(TimedMeta);
							text = TimedMeta.getPrivateFrame();

							if (null != text)
							{
								String strPrivateFrame = new String(
										text.getTextData(), 0, text.getTextData().length,
										getTextEncodingType(text.getEncodingType()));

								Log.d(LOG_TAG, "TimedMeta PRIVATE FRAME: " + strPrivateFrame);
							}

							text = TimedMeta.getText();
							String strTextFrame;

							if( text != null) {
								strTextFrame = new String(
										text.getTextData(), 0, text.getTextData().length,
										getTextEncodingType(text.getEncodingType()));
								mTimedMetaTextView.setText(strTextFrame);
								Log.d(LOG_TAG, "TimedMeta<1>: " + strTextFrame);
							}

							ArrayList<NexID3TagText> arrExtraData = TimedMeta.getArrExtraData();

							String str1 = "";
							String str2 = "";

							if (arrExtraData != null) {
								for (int i = 0; i < arrExtraData.size(); ++i) {
									NexID3TagText ID3ExtraData = arrExtraData.get(i);
									str1 = new String(ID3ExtraData.getTextData(), 0, ID3ExtraData.getTextData().length,
											getTextEncodingType(ID3ExtraData.getEncodingType()));

									str2 = new String(ID3ExtraData.getExtraDataID(), 0, ID3ExtraData.getExtraDataID().length,
											getTextEncodingType(ID3ExtraData.getEncodingType()));

									if (null != str1 && null != str2) {
										final String strText = String.format("getExtraDataID : " + str2 + " getExtraData : " + str1);

										if (null != mTimedMetaTextView) {
											mHandler.post(new Runnable() {
												@Override
												public void run() {
													mTimedMetaTextView.setText(strText);
													Log.d(LOG_TAG, "TimedMeta<2>: " + strText);
												}
											});
										}
									}
								}
							}

						}
						catch (Throwable e) {
							Log.d(LOG_TAG, "Exception - onTimedMetaRenderRender() : " + e.getMessage() );
						}
					}
				});
			}

			@Override
			public void onStatusReport(NexPlayer mp, int msg, int param1) {
				Log.d(LOG_TAG, "onStatusReport msg : " + msg + " , param1 : " + param1);

				if (msg == NexPlayer.NEXPLAYER_STATUS_REPORT_CONTENT_INFO_UPDATED) {
					mContentInfo = mNexPlayer.getContentInfo();

					if( mContentInfoDialog != null )
						mContentInfoDialog.setContentInfo(mContentInfo);

					mCaptionStreamList.setCaptionStreams(mContentInfo);
					if (mCaptionStreamList.updateCaptionType(mContentInfo)) {
						mCaptionPainter.setCaptionType(mContentInfo.mCaptionType);
					}

					mHandler.post(new Runnable() {
						@Override
						public void run() {
							String text = "Track : " + NexContentInfoExtractor.getCurrTrackID(NexPlayer.MEDIA_STREAM_TYPE_VIDEO, mContentInfo);
							mTrackView.setText(text);
						}
					});

					if(!mIsLive) {
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if( mContentInfo != null ) {
									mSeekBar.setMax(mContentInfo.mMediaDuration);
									mSeekBar.setDurationTimeText(mContentInfo.mMediaDuration);
								}
							}
						});
					}

					if(mContentInfo.mMediaType == AUDIO_ONLY && mAudioInitEnd) {
						mHandler.post(new Runnable() {
							public void run() {
								mImageView.setVisibility(View.VISIBLE);
								mImageView.requestLayout();
							}
						});
					}
					else if (mContentInfo.mMediaType == AUDIO_VIDEO && mAudioInitEnd) {
						boolean videoInitEnd = mVideoView.isInitialized();

						if (mAudioInitEnd &&
								(videoInitEnd || mContentInfo.mCurrVideoStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID) ) {
							mHandler.post(new Runnable() {
								public void run() {
									mImageView.setVisibility(View.INVISIBLE);
									mImageView.requestLayout();
								}
							});
						}
					}
					else if (mContentInfo.mMediaType == VIDEO_ONLY) {
						boolean videoInitEnd = false;
						videoInitEnd = mVideoView.isInitialized();

						if (videoInitEnd) {
							mHandler.post(new Runnable() {
								public void run() {
									mImageView.setVisibility(View.INVISIBLE);
									mImageView.requestLayout();
								}
							});
						}

					}
				}
				else if (msg == NexPlayer.NEXPLAYER_STATUS_REPORT_STREAM_RECV_PAUSE) {
					Log.d(LOG_TAG, "Stream Receive Pause.");
				}
				else if (msg == NexPlayer.NEXPLAYER_STATUS_REPORT_STREAM_RECV_RESUME) {
					Log.d(LOG_TAG, "Stream Receive Resume.");
				}
			}

			@Override
			public void onDownloaderError(NexPlayer mp, int msg, int param1) {
				Log.d(LOG_TAG, "onDownloaderError MSG : " + msg + " param1 : " + param1 + " mDownloaderState : " + mDownloaderState);

				onError(mp, NexErrorCode.fromIntegerValue(msg));

				if( mDownloaderState == NexPlayer.NEXDOWNLOADER_STATE_DOWNLOAD ) {
					mNexPlayer.DownloaderStop();
				}
				else if( mDownloaderState == NexPlayer.NEXDOWNLOADER_STATE_STOP ) {
					mNexPlayer.DownloaderClose();
				}
				mStrExternalPDFile = null;
				mTotalSize = 0;
			}

			@Override
			public void onDownloaderAsyncCmdComplete(NexPlayer mp, int msg, int result,
													 int param2) {
				Log.d(LOG_TAG, "onDownloaderAsyncCmdComplete msg : " + msg + " result : " + result);

				switch( msg) {
					case NexPlayer.NEXDOWNLOADER_ASYNC_CMD_OPEN:
						if(result == 0) {
							Log.d(LOG_TAG, "onDownloaderAsyncCmdComplete : OPEN");
							mNexPlayer.DownloaderStart();
						} else {
							NexPlayer.NexErrorCode errorCode = NexErrorCode.fromIntegerValue(result);

							if(NexErrorCode.HTTPDOWNLOADER_ERROR_ALREADY_DOWNLOADED.equals(errorCode)) {

								mHandler.post(new Runnable() {
									@Override
									public void run() {
										int result = mNexPlayer.open(
												mStrExternalPDFile,
												null, // JDKIM 2010/08/18
												null,
												NexPlayer.NEXPLAYER_SOURCE_TYPE_LOCAL_NORMAL,
												NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP);

										if( result != 0 ) {
											onError(mNexPlayer, NexErrorCode.fromIntegerValue(result));
										}

										mCurrentPath = mStrExternalPDFile;
										mStrExternalPDFile = null;
										mIsStreaming = false;
									}
								});
							}
							else {
								onError(mp, NexErrorCode.fromIntegerValue(result));
								mStrExternalPDFile = null;
							}
						}

						break;

					case NexPlayer.NEXDOWNLOADER_ASYNC_CMD_START:
						Log.d(LOG_TAG, "onDownloaderAsyncCmdComplete : START");
						if(result != 0) {
							onError(mp, NexErrorCode.fromIntegerValue(result));
							mNexPlayer.DownloaderClose();
						}
						break;

					case NexPlayer.NEXDOWNLOADER_ASYNC_CMD_STOP:
						Log.d(LOG_TAG, "onDownloaderAsyncCmdComplete : STOP");
						mNexPlayer.DownloaderClose();
						mTotalSize = 0;
						break;

					case NexPlayer.NEXDOWNLOADER_ASYNC_CMD_CLOSE:
						Log.d(LOG_TAG, "onDownloaderAsyncCmdComplete : CLOSE");
						break;

					default:
						break;
				}
			}

			@Override
			public void onDownloaderEventBegin(NexPlayer mp, int param1, int param2) {
				Log.d(LOG_TAG, "onDownloaderEventBegin " +" size : + " + param2);
				mTotalSize = param2;
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if( mCurrentPath != null ) {
							int result = mNexPlayer.open(
									mCurrentPath,
									null, // JDKIM 2010/08/18
									mStrExternalPDFile,
									NexPlayer.NEXPLAYER_SOURCE_TYPE_STREAMING,
									NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP);

							if( result != 0 ) {
								onError(mNexPlayer, NexErrorCode.fromIntegerValue(result));
							}
						}
					}
				});
			}

			@Override
			public void onDownloaderEventProgress(NexPlayer mp, int param1, int param2,
												  long param3, long param4) {
				mTotalSize = param4;
				mNexPlayer.SetExternalPDFileDownloadSize(param3, param4);
			}

			@Override
			public void onDownloaderEventComplete(NexPlayer mp, int param1) {
				Log.d(LOG_TAG, "onDownloaderEventComplete ");
				mNexPlayer.DownloaderStop();
				mNexPlayer.SetExternalPDFileDownloadSize(mTotalSize, mTotalSize);
			}

			@Override
			public void onDownloaderEventState(NexPlayer mp, int param1, int param2) {
				mDownloaderState = param2;
			}

			@Override
			public void onPictureTimingInfo(NexPlayer mp,
											NexPictureTimingInfo[] arrPictureTimingInfo) {
			}
		};

		mNexPlayer.addEventReceiver(mEventReceiver);
	}

	private void setProperties() {
		mNexPlayer.setProperty(NexPlayer.NexProperty.MAX_BW, mPrefData.mMaxBandWidth * BANDWIDTH_KBPS);
		mNexPlayer.setProperty(NexPlayer.NexProperty.MIN_BW, mPrefData.mMinBandWidth * BANDWIDTH_KBPS);
		mNexPlayer.setProperty(NexProperty.TIMESTAMP_DIFFERENCE_VDISP_WAIT, mPrefData.mVideoDisplayWait);
		mNexPlayer.setProperty(NexProperty.TIMESTAMP_DIFFERENCE_VDISP_SKIP, mPrefData.mVideoDisplaySkip);
		mNexPlayer.setProperty(NexProperty.AV_SYNC_OFFSET, (int) (mPrefData.mAVSyncOffset * 1000));
		mNexPlayer.setProperty(NexProperty.PREFER_LANGUAGE_AUDIO, mPrefData.mPrefLanguageAudio);
		mNexPlayer.setProperty(NexProperty.PREFER_LANGUAGE_TEXT, mPrefData.mPrefLanguageText);
		mNexPlayer.setProperty(NexProperty.PREFER_BANDWIDTH, 100);
		mNexPlayer.setProperty(NexProperty.PREFER_AV, 1);
		mNexPlayer.setProperty(NexProperty.START_NEARESTBW, mPrefData.mStartNearestBW);
		mNexPlayer.setProperty(NexProperty.SUBTITLE_TEMP_PATH, mPrefData.mSubtitleDownloadPath);
		mNexPlayer.setProperty(NexProperty.ENABLE_CEA708, 1);
		mNexPlayer.setProperty(NexProperty.ENABLE_ID3_TTML, mPrefData.mEnableID3TTML ? 1 : 0);


		mNexPlayer.setDebugLogs( mPrefData.mCodecLogLevel, mPrefData.mRendererLogLevel, mPrefData.mProtoclLogLevel );

		changeDolbyAC3EnhancementGainValue(mPrefData.mDolbyAC3EnhancementGain);
		changeDolbyAC3PostProcessingValue(mPrefData.mEnableDolbyAC3PostProcessing);
		changeDolbyAC3EndPointValue(mPrefData.mDolbyAC3EndPoint);

		changeDolbyAC4VirtualizationValue(mPrefData.mDolbyAC4Virtualization);
		changeDolbyAC4EnhancementGainValue(mPrefData.mDolbyAC4EnhancementGain);
		changeDolbyAC4MainAssoPrefValue(mPrefData.mDolbyAC4MainAssoPref);
		changeDolbyAC4PresentationIndexValue(mPrefData.mDolbyAC4PresentationIndex);

		if( !mPrefData.mEnableAudioOnlyTrack )
			mNexPlayer.setProperty(NexProperty.ENABLE_AUDIOONLY_TRACK, 0);

		if( mPrefData.mIgnoreTextmode )
			mNexPlayer.setProperty(NexProperty.IGNORE_CEA608_TEXTMODE_COMMAND, 1);

		if( mPrefData.mEnableWebVTT == false )
			mNexPlayer.setProperty(NexProperty.ENABLE_WEBVTT, 0);

		if( mPrefData.mWebVTTWaitOpen == false )
			mNexPlayer.setProperty(NexProperty.WEBVTT_WAITOPEN, 0);

		if( mPrefData.mHLSRunModeStable == true )
			mNexPlayer.setProperty(NexProperty.HLS_RUNMODE, 1);

		if ( mPrefData.mUseEyePleaser == false )
			mNexPlayer.setProperty(NexProperty.SUPPORT_EYE_PLEASER, 0);

		if( mPrefData.mIsTrackdownEnabled ) {
			mNexPlayer.setProperty(NexProperty.ENABLE_TRACKDOWN, 1);
			mNexPlayer.setProperty(NexProperty.TRACKDOWN_VIDEO_RATIO, (int)mPrefData.mTrackdownThreshold);
		}

		if(mPrefData.mIsLowLatencyEnabled){
			mNexPlayer.setProperty(NexProperty.LIVE_VIEW_OPTION,3);
			mNexPlayer.setProperty(NexProperty.PARTIAL_PREFETCH, 1);
			mNexPlayer.setProperty(NexProperty.RE_BUFFERING_DURATION, mPrefData.mLowLatencyValue);
			mNexPlayer.setProperty(NexProperty.INITIAL_BUFFERING_DURATION, mPrefData.mLowLatencyValue);
		}

		if( mPrefData.mBufferTime != 0 ) {
			mNexPlayer.setProperty(NexProperty.INITIAL_BUFFERING_DURATION, (int) (mPrefData.mBufferTime * 1000));
			mNexPlayer.setProperty(NexProperty.RE_BUFFERING_DURATION, (int) (mPrefData.mBufferTime * 1000));
		}
		if( mPrefData.mTSDumpEnable ) {
			File dump = new File(mPrefData.mTSDumpPath);

			if( !dump.exists() ) {
				dump.mkdir();
			}
			mNexPlayer.setProperties(36,0x12);
			mNexPlayer.setProperties(37, mPrefData.mTSDumpPath);
		}


		if( mPrefData.mPCMDumpEnable ) {
			String path = mPrefData.mPCMDumpPath + NexFileIO.getContentTitle(mCurrentPath) + "/";
			File dump = new File(path);
			if (!dump.exists()) {
				dump.mkdirs();
			}
			mNexPlayer.setProperties(NEXPLAYER_PROPERTY_DATA_DUMP_SUB_PATH, path);
		}

		if( mPrefData.mEnableSPD){
			mNexPlayer.setProperty(NexProperty.SET_APPLS_PROGRAM_DATE_TIME_PRESENTATION_DELAY, mPrefData.mSPDValue);
			mNexPlayer.setProperty(NexProperty.PARTIAL_PREFETCH, 1);
			mNexPlayer.setProperties(591, 1);
		}

		//For NexHD
		/*
		{
			NexHDManager manager = new NexHDManager();
			manager.initManager(strEnginePath);
		}
		*/

		if( mPrefData.mEnableClientSideTimeShift)
		{
			Log.d(LOG_TAG,"Enalbed Client-Side TimeShift. BufferSize(" + mPrefData.mTimeShiftMaxBufferSize + " MB), BufferDuration("+mPrefData.mTimeShiftMaxBufferDuration+" Min)");
			String strTSPath = "/sdcard/TimeShift/";
			File tsPath = new File(strTSPath);
			if(tsPath.isDirectory() == false)
			{
				tsPath.mkdirs();
			}
			mNexPlayer.setClientTimeShift(	mPrefData.mEnableClientSideTimeShift,
					strTSPath,
					mPrefData.mTimeShiftMaxBufferSize,
					mPrefData.mTimeShiftMaxBufferDuration);
		}
		else
		{
			Log.d(LOG_TAG, "Disable Client-Side TimeShift.");
			mNexPlayer.setClientTimeShift(mPrefData.mEnableClientSideTimeShift, null, 0, 0);
		}
	}

	private void setCustomNetAddrTable()
	{
		NexNetAddrTable table = new NexNetAddrTable();
		table.addEntry("localhost", "127.0.0.1");
		mNexPlayer.setNetAddrTable(table, NexNetAddrTable.NETADDR_TABLE_FALLBACK);
	}


	private void showToastMsg(final String msg) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setVolume() {
		mNexPlayer.setVolume((mVolume/10)); //Range : 0 ~1.0
	}

	private NexPlayerSampleExtensionLoader.ISampleExtension mSampleExtension = null;
	private synchronized NexPlayerSampleExtensionLoader.ISampleExtension getSampleExtension() {
		if ( mSampleExtension == null ) {
			Context context = NexPlayerSample.this.getApplicationContext();
			mSampleExtension = NexPlayerSampleExtensionLoader.loadExtension(mNexPlayer, context);
			mSampleExtension.setListener(new NexPlayerSampleExtensionLoader.ISampleExtensionListener() {
				public void errorStatus(NexErrorCode errorCode, String message) {
					showErrorStatus(message + '(' + errorCode.getDesc() +')');
				}
			});
		}
		return mSampleExtension;
	}

	boolean shouldStartPlay() {
		return getSampleExtension().shouldStartPlay(mPrefData,
				NxbInfo.getNxbInfo(mNxbWholeList, mCurrentPath, mCurrentPosition));
	}

	private void startPlay() {
		if (mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_STOP || mPlayerState == PLAYER_FLOW_STATE.START_PLAY )
		{
			closePlayer();
		}
		mCurrentPath = String.valueOf(mCurrentPath);

		boolean enableRetrieving = false;
		if( !mCurrentPath.endsWith(NexFileIO.STORE_INFO_EXTENSION) ) {
			if (!shouldStartPlay()) {
				return;
			}
		} else {
			enableRetrieving = true;
		}
		mPlayerState = PLAYER_FLOW_STATE.START_PLAY;
		mNexPlayer.setOfflineMode(false, enableRetrieving);

		if ( mCurrentPath.length() == 0 ) {
			showErrorStatus("Media URL/path not set for playback");
			return;
		}
		showBufferStatus(getResources().getString(R.string.buffer_open));
		mFastPlay = false;

		int sourceType = NexPlayer.NEXPLAYER_SOURCE_TYPE_LOCAL_NORMAL;

		String contentPath = mCurrentPath;
		if( contentPath.contains(String.valueOf('?')) ) {
			contentPath = contentPath.substring(0, contentPath.indexOf('?'));
		}
		boolean end = (contentPath.endsWith("ismv") || contentPath.endsWith("mp4"));
		boolean start = (mCurrentPath.startsWith("http") || mCurrentPath.startsWith("https"));

		if( mPrefData.mUseExternalPD && end && start ) {
			String parentPath = Environment.getExternalStorageDirectory().getPath();
			mStrExternalPDFile = parentPath
					+ File.separator
					+ NexFileIO.getExternalFileName(mCurrentPath);
			mNexPlayer.DownloaderOpen(mCurrentPath, mStrExternalPDFile, null, 0, NexPlayer.NEXDOWNLOADER_OPEN_TYPE_APPEND);
		} else {
			if( mIsStreaming ) {
				sourceType = NexPlayer.NEXPLAYER_SOURCE_TYPE_STREAMING;
				enableDynamicThumbnail();
			}

			if( mPrefData.mEnableStatisticsMonitor) {
				mStatisticsMonitor = new NexStatisticsMonitor(mNexPlayer, false);
				mStatisticsMonitor.setDuration(STATISTICS_GENERAL, 5);
				setStatisticListener();
			}

			String keyServer = "";
			Intent intent = getIntent();

			HashMap<String,String> WVDRMOptionalHeaders = GetOptionalHeaderFields();


			INexDRMLicenseListener mLicenseRequestListener = null;
			// if you want to get license data instead of nexplayer. use below code.
			/*
			INexDRMLicenseListener mLicenseRequestListener = new INexDRMLicenseListener() {
				@Override
				public byte[] onLicenseRequest(byte[] requestData) {
					final String LicenseServer = mCurrentExtraData;
					Log.d(LOG_TAG, "onLicenseRequest data length : " + requestData.length);
					Object response = null;
					try {
						response = NexHTTPUtil.executePost(LicenseServer, requestData, null);
						Log.d(LOG_TAG, "[getLicense ] license resonse: length:" + ((byte[])response).length);
					} catch (IOException e) {

					}
					return (byte[]) response;
				}
			};
			*/

			// Widevine start
			int drmType = 0;
			// Widevine end

			// NexMediaDrm start
			if (mPrefData.mEnableMediaDRM) {
				keyServer = mCurrentExtraData;
				if( TextUtils.isEmpty(keyServer) )
					keyServer = mPrefData.mWidevineDRMServerKey;
				Log.d(LOG_TAG, "setNexMediaDrmKeyServerUri ( " + keyServer + " )");
				mNexPlayer.setNexMediaDrmKeyServerUri(keyServer);
				if(WVDRMOptionalHeaders != null){
					Log.i(LOG_TAG, "Set Media DRM Optional Header : "  + WVDRMOptionalHeaders.size() );
					mNexPlayer.setNexMediaDrmOptionalHeaderFields(WVDRMOptionalHeaders);
				}
				mNexPlayer.setLicenseRequestListener(mLicenseRequestListener);
				drmType |= 1;
			}
			// NexMediaDrm end

			//NexWVSWDrm start
			if(mPrefData.mEnableWVSWDRM) {
				mNexWVDRM = new NexWVDRM();
				File fileDir = this.getFilesDir();
				String strCertPath = fileDir.getAbsolutePath() + "/wvcert";

				//String keyServer = "";
				keyServer= mCurrentExtraData;
				if( TextUtils.isEmpty(keyServer) )
					keyServer = mPrefData.mWidevineDRMServerKey;
				Log.d(LOG_TAG, "setSWDrmKeyServerUri ( " + keyServer + " )");

				int offlineMode = 0;
				if(enableRetrieving)
					offlineMode += 2;

//				mNexWVDRM.setProperties(0xA001, 0);
				mNexWVDRM.setLicenseRequestListener(mLicenseRequestListener);
				if(null != WVDRMOptionalHeaders){
					Log.i(LOG_TAG, "Set Widevine DRM Optional Header : "  + WVDRMOptionalHeaders.size() );
					mNexWVDRM.setNexWVDrmOptionalHeaderFields(WVDRMOptionalHeaders);
				}
				if(mNexWVDRM.initDRMManager(Util.getEnginePath(this), strCertPath, keyServer, offlineMode) == 0) {
					//mNexWVDRM.setServiceCertificate(1, null);
					mNexWVDRM.enableWVDRMLogs(mPrefData.mPrintSWDRMAllLogs);
					mNexWVDRM.setListener(new NexWVDRM.IWVDrmListener() {
						@Override
						public String onModifyKeyAttribute(String strKeyAttr) {
							String strAttr = strKeyAttr;
							String strRet = strKeyAttr;
							//modify here;
							Log.d(LOG_TAG, "Key Attr: " + strAttr);
							List<String> keyAttrArray = new ArrayList<String>();
							String strKeyElem = "";
							String strKeyRemain = "";
							int end = 0;
							boolean bFound = false;
							while (true) {
								end = strAttr.indexOf("\n");
								if (end != -1 && end != 0) {
									strKeyElem = strAttr.substring(0, end);
									keyAttrArray.add(strKeyElem);
									strKeyRemain = strAttr.substring(end, strAttr.length());
									strAttr = strKeyRemain;
								} else if ((end == -1 || end == 0) && strKeyElem.isEmpty() == false) {
									keyAttrArray.add(strAttr.substring(0, strAttr.length()));
									break;
								} else {
									keyAttrArray.add(strAttr);
									break;
								}
							}

							for (int i = 0; i < keyAttrArray.size(); i++) {
								strKeyElem = keyAttrArray.get(i);
								if (strKeyElem.indexOf("com.widevine") != -1) {
									Log.d(LOG_TAG, "Found Key!");
									strRet = strKeyElem;
									break;
								}
							}

							return strRet;
						}
					});
					drmType |= 2;
				}
			}
			//NexWVSWDrm end

			// Widevine start
			mNexPlayer.setProperties(NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM, drmType);
			// Widevine end

			int result = mNexPlayer.open(mCurrentPath, null, null, sourceType,
					mPrefData.mUseUDP ? NexPlayer.NEXPLAYER_TRANSPORT_TYPE_UDP :
							NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP);
			if( result != 0 ) {
				mEventReceiver.onError(mNexPlayer, NexErrorCode.fromIntegerValue(result));
			}


		}
	}

	private void setStatisticListener() {
		mStatisticsListener = new NexStatisticsMonitor.IStatisticsListener() {
			@Override
			public void onUpdated(int statisticsType, HashMap<IStatistics, Object> map) {
				mStatisticsDialog.onUpdated(statisticsType, map);
			}
		};
		mStatisticsMonitor.setListener(mStatisticsListener);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(LOG_TAG, "onKeyDown keyCode : " + keyCode);

		if( mSeekBar.hasFocus() ) {
			if( keyCode == KeyEvent.KEYCODE_DPAD_CENTER ) {
				if( mFastPlay )
					stopFastPlay();

				mIsSeeking = (mNexPlayer.seek(mSeekBar.getProgress()) == 0);
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void showProgressBar(int millisec) {
		if( !mSeekBar.hasFocus() ) {
			if( mIsLive ) {
				long[] seekableRange = mNexPlayer.getSeekableRangeInfo();

				if( seekableRange == null ) {
					mSeekBar.enableSeekBar(false);
					mProgressBase = 0;
				} else {
					mSeekBar.enableSeekBar(true);
					mProgressBase = (int)seekableRange[0];
					int seekbarMaxLength = (int)seekableRange[1] - mProgressBase;
					mSeekBar.setMax(seekbarMaxLength);

					if(!mSeekBar.isTrackingTouch()) {
						millisec = Math.min(millisec, (int)seekableRange[1]);
						int progress = Math.max((millisec - mProgressBase), 0);
						mSeekBar.setProgress(progress);
						mSeekBar.setDurationTimeText((int)seekableRange[1]);

						if( mContentInfo != null ) {
							int streamType = mContentInfo.mMediaType == VIDEO_ONLY ? NexPlayer.MEDIA_STREAM_TYPE_VIDEO : NexPlayer.MEDIA_STREAM_TYPE_AUDIO;
							mSeekBar.setSecondaryProgress(mNexPlayer.getBufferInfo(streamType, NexPlayer.NEXPLAYER_BUFINFO_INDEX_LASTCTS));
						}
					}
				}
			} else {
				int duration = mNexPlayer.getContentInfoInt(NexPlayer.CONTENT_INFO_INDEX_MEDIA_DURATION);

				if(duration < millisec) {
					duration = millisec;
				}

				mSeekBar.setMax(duration);

				if(!mSeekBar.isTrackingTouch()) {
					if(mSeekPoint < 0) {
						mSeekBar.setProgress(millisec);
					} else {
						mSeekBar.setProgress(mSeekPoint);
					}
				}

				if( mContentInfo != null ) {
					int streamType = mContentInfo.mMediaType == VIDEO_ONLY ? NexPlayer.MEDIA_STREAM_TYPE_VIDEO : NexPlayer.MEDIA_STREAM_TYPE_AUDIO;
					mExternalPDBufferDuration = mNexPlayer.getBufferInfo(streamType, NexPlayer.NEXPLAYER_BUFINFO_INDEX_LASTCTS);
					mSeekBar.setSecondaryProgress(mExternalPDBufferDuration);
				}
			}
		}

		mSeekBar.setKeyProgressIncrement(mPrefData.mSeekOffset * 1000);
		NexPlayer.PROGRAM_TIME pTime = new NexPlayer.PROGRAM_TIME();
		mNexPlayer.getProgramTime(pTime);
	}


	private void showErrorStatus(final String status) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mErrorView.setText(status);
				mErrorView.setVisibility(View.VISIBLE);
				clearBufferStatus();
			}
		});
	}

	private void resetSeekStatus() {
		mIsBuffering = false;
		mIsSeeking = false;
		mSeekPoint = -1;
		mProgressBase = 0;
	}

	private void setDynamicThumbnailOption(NexContentInformation contentInfo) {
		if( mPrefData.mEnableDynamicThumbnail && contentInfo.mMediaDuration > 0 && (contentInfo.mSubSrcType == 6 || contentInfo.mSubSrcType == 5)) {
			mSeekBar.enableDynamicThumbnail(true);
			int intervalTime = contentInfo.mMediaDuration / mPrefData.mMaxThumbnailFrame;
			mSeekBar.setMaximumThumbnailFrame(mPrefData.mMaxThumbnailFrame);
			mSeekBar.setThumbnailSearchInterval(intervalTime);
			mNexPlayer.setOptionDynamicThumbnail(NexPlayer.OPTION_DYNAMIC_THUMBNAIL_INTERVAL, intervalTime, 0);
		}
	}

	private void scheduleSeekableRangeTimeTask() {
		mTimer = new Timer();
		UpdateSeekableRangeTimerTask timerTask = new UpdateSeekableRangeTimerTask();
		mTimer.schedule(timerTask, 1000, 1000);
	}

	private void cancelSeekableRangeTimer() {
		if( mTimer != null ) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	private class UpdateSeekableRangeTimerTask extends TimerTask {
		@Override
		public void run() {
			Log.d(LOG_TAG, "updateSeekableRangeTimer run");
			showProgressBar(mNexPlayer.getCurrentPosition());
		}
	}

	private void getContentPath(int mode) {
		int newIndex = mCurrentPosition;

		switch(mode) {
			case REPLAY_MODE_NEXT:
				newIndex++;
				if( mFileList != null ) {
					mIsStreaming = false;

					newIndex = mPlayListUtil.getNextContentIndex(mFileList, newIndex);
					if( newIndex != PlayListUtils.NOT_FOUND ) {
						mCurrentPosition = newIndex;
						mCurrentPath = mFileList.get(mCurrentPosition).getAbsolutePath();
						mTargetSubtitlePath = NexFileIO.subtitlePathFromMediaPath(mCurrentPath);
					}
				} else {
					if( mNxbWholeList != null ) {
						mIsStreaming = true;

						if( mNxbWholeList.size() <= newIndex )
							newIndex = 0;
						mCurrentPosition = newIndex;
						NxbInfo info = mNxbWholeList.get(mCurrentPosition);
						mCurrentPath = info.getUrl();

						mTargetSubtitlePath = getSubtitlePath(info, 0);
						mCurrentExtraData =  getNxbExtraData();
					}

					mCurrentExtraData =  getNxbExtraData();
					mTargetSubtitlePath = getFirstSubtitlePath();
				}
				mExistFollowingContentPath = true;
				break;
			case REPLAY_MODE_QUIT:
				mCurrentPath = null;
				break;
			case REPLAY_MODE_AGAIN:
				mExistFollowingContentPath = true;
				break;
			case REPLAY_MODE_PREVIOUS:
				newIndex--;

				if( mFileList != null ) {
					mIsStreaming = false;

					newIndex = mPlayListUtil.getPreviousContentIndex(mFileList, newIndex);
					if( newIndex != PlayListUtils.NOT_FOUND ) {
						mCurrentPosition = newIndex;
						mCurrentPath = mFileList.get(mCurrentPosition).getAbsolutePath();
						mTargetSubtitlePath = NexFileIO.subtitlePathFromMediaPath(mCurrentPath);
					}
				} else if( mNxbWholeList != null ) {
					mIsStreaming = true;

					if( newIndex == -1 )
						newIndex = mNxbWholeList.size() - 1;
					mCurrentPosition = newIndex;
					NxbInfo info = mNxbWholeList.get(mCurrentPosition);
					mCurrentPath = info.getUrl();

					mTargetSubtitlePath = getSubtitlePath(info, 0);
					mCurrentExtraData =  getNxbExtraData();
				}
				mExistFollowingContentPath = true;
				break;
		}
	}

	private String getSubtitlePath(NxbInfo info, int index) {
		String path = null;
		if( info != null && info.getSubtitle().size() > index )
			path = info.getSubtitle().get(index);
		return path;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		getIntentExtra();
		Log.d(LOG_TAG, "onNewIntent");

		if(mCurrentPath != null && mNexPlayer != null) {
			int state = mNexPlayer.getState();

			if( state != NexPlayer.NEXPLAYER_STATE_STOP &&
					state != NexPlayer.NEXPLAYER_STATE_NONE ) {

				resetPlayerStatus(RESET_PLAYER_ALL);
				mExistFollowingContentPath = true;

				if (mFastPlay)
					stopFastPlay();

				if (mStrExternalPDFile != null)
					stopDownload();

				if (state == NexPlayer.NEXPLAYER_STATE_CLOSED) {
					startPlay();
				} else if (state > NexPlayer.NEXPLAYER_STATE_STOP) {
					stopPlayer();

				}
			}
		}
		super.onNewIntent(intent);
	}

	private void setAlbumImage() {
		NexID3TagPicture picture = null;
		if (mContentInfo.mID3Tag != null) {
			picture = mContentInfo.mID3Tag.getPicture();
			if(picture != null) {
				Bitmap bm = BitmapFactory.decodeByteArray(picture.getPictureData(),0, picture.getPictureData().length);
				if( bm != null ) {
					mImageView.setImageBitmap(bm);
				}
				else {
					mImageView.setImageResource(R.drawable.audio_skin2);
				}
				mImageView.setVisibility(View.VISIBLE);
			}
			else {
				mImageView.setImageResource(R.drawable.audio_skin2);
				mImageView.setVisibility(View.VISIBLE);
			}
		}
		else {
			mImageView.setImageResource(R.drawable.audio_skin2);
			mImageView.setVisibility(View.VISIBLE);
		}
	}

	private void setTimeMetaImage(NexID3TagInformation TimedMeta) {
		NexID3TagPicture picture = TimedMeta.getPicture();
		if( picture != null && picture.getPictureData() != null) {
			Bitmap bm = BitmapFactory.decodeByteArray(picture.getPictureData(),0, picture.getPictureData().length);
			mImageView.setImageBitmap(bm);
			mImageView.setBackgroundColor(Color.TRANSPARENT);
			mImageView.requestLayout();
		}
	}

	private void setCaptionType() {
		if (null != mContentInfo) {
			int captionType = mContentInfo.mCaptionType;
			if (NexContentInformation.NEX_TEXT_CEA == mContentInfo.mCaptionType) {
				if (mCaptionStreamList.checkClosedCaptionInCaptionStreams(NexContentInformation.NEX_TEXT_CEA608)) {
					captionType = NexContentInformation.NEX_TEXT_CEA608;
				} else if (mCaptionStreamList.checkClosedCaptionInCaptionStreams(NexContentInformation.NEX_TEXT_CEA708)) {
					captionType = NexContentInformation.NEX_TEXT_CEA708;
				} else {
					captionType = mPrefData.mCaptionMode == 0 ? NexContentInformation.NEX_TEXT_CEA608 : NexContentInformation.NEX_TEXT_CEA708;
				}
			}

			mCaptionPainter.setCaptionType(captionType);
		}
	}

	private void setLyric() {
		try {
			if (mContentInfo.mID3Tag != null) {
				NexID3TagText text = null;
				String strInfo = "";
				String str = "";

				text = mContentInfo.mID3Tag.getArtist();
				str = new String(text.getTextData(), 0, text.getTextData().length, getTextEncodingType(text.getEncodingType()));
				strInfo += str + "\n";

				text = mContentInfo.mID3Tag.getTitle();
				str = new String(text.getTextData(), 0, text.getTextData().length, getTextEncodingType(text.getEncodingType()));
				strInfo += str + "\n";

				text = mContentInfo.mID3Tag.getAlbum();
				str = new String(text.getTextData(), 0, text.getTextData().length, getTextEncodingType(text.getEncodingType()));
				strInfo += str + "\n\n";

				text = mContentInfo.mID3Tag.getLyric();
				str = new String(text.getTextData(), 0, text.getTextData().length, getTextEncodingType(text.getEncodingType()));
				strInfo += str;

				mLyricView.setText(strInfo);

				if( text != null ) {
					mLyricScrollView.scrollTo(0, 0);
					mLyricScrollView.setVisibility(View.VISIBLE);
					setLyricScrollViewOutputPosition();
				}
			} else {
				mLyricView.setText("");
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, "Exception - setLyric() : " + e.getMessage() );
			mLyricView.setText("");
		}
	}

	private void setLyricScrollViewOutputPosition() {
		Matrix m = mImageView.getImageMatrix();
		float[] values = new float[9];
		m.getValues(values);

		int bmWidth = mImageView.getDrawable().getIntrinsicWidth();
		int bmHeight = mImageView.getDrawable().getIntrinsicHeight();
		int transWidth = (int)(values[Matrix.MSCALE_X] * bmWidth);
		int transHeight = (int)(values[Matrix.MSCALE_Y] * bmHeight);

		RelativeLayout.LayoutParams params =
				new RelativeLayout.LayoutParams(transWidth, transHeight);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);

		mLyricScrollView.setLayoutParams(params);
		mLyricScrollView.requestLayout();
		mLyricView.setWidth(transWidth);
	}

	public String getTextEncodingType(int eType) {
		if (eType == NexID3TagText.ENCODING_TYPE_UTF16)
			return new String("UTF-16");
		else if(eType == NexID3TagText.ENCODING_TYPE_UNICODE)
			return new String("UTF-16LE");
		else
			return new String("UTF-8");
	}

	private void updateControllerVisibility() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				int visibility = (mFileList != null || mNxbWholeList != null) ? View.VISIBLE : View.INVISIBLE;
				mPreviousButton.setVisibility(visibility);
				mNextButton.setVisibility(visibility);

				if( mIsLive ) {
					mGoToLiveButton.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	private void enableUIControls(final boolean isEnable){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if(mPlayPauseButton != null) {
					mPlayPauseButton.setEnabled(isEnable);
				}
			}
		});
	}

	private void showBufferStatus(final String message){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressLayout.setVisibility(View.VISIBLE);
				mProgressLayout.requestLayout();
				mProgressTextView.setText(message);
			}
		});
	}
	private void clearBufferStatus(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressLayout.setVisibility(View.GONE);
				mProgressLayout.requestLayout();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_multi_stream:
				createAndShowMultiStreamDialog();
				break;
			case R.id.action_min_max_bandwidth:
				createAndShowBandWidthDialog();
				break;
			case R.id.action_scale:
				createAndShowScaleModeDialog();
				break;
			case R.id.action_caption_style:
				createAndShowCaptionStyleDialog();
				break;
			case R.id.action_statistics:
				createAdnShowStatisticDialog();
				break;
			case R.id.action_volume:
				createAndShowVolumeDialog();
				break;
			case R.id.action_dolby_ac3_sound:
				mDolbyAC3Dialog.createAndShow();
				break;
			case R.id.action_dolby_ac4_sound:
				mDolbyAC4Dialog.createAndShow();
				break;
			case R.id.action_target_bandwidth:
				mTargetBandWidthDialog.createAndShow(mContentInfo, mNexPlayer.getProperty(NexProperty.SUPPORT_ABR) == 1);
				break;
			case R.id.action_language:
				mCaptionLanquageDialog.createAndShowDialog(mContentInfo, mNexPlayer.getCaptionLanguage());
				break;
			case R.id.action_subtitle:
				NxbInfo info = NxbInfo.getNxbInfo(mNxbWholeList, mCurrentPath, mCurrentPosition);
				mSubtitleChangeDialog.createAndShow(info.getSubtitle(), mCurrentSubtitlePath);
				break;
			case R.id.action_content_info:
				createAndShowContentInfoDialog();
				break;
			case R.id.action_pip:
				if ( mContentInfo != null && mVideoWidth > 0 && mVideoHeight > 0 ) {
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						PictureInPictureParams.Builder builder = new PictureInPictureParams.Builder();
						builder.setAspectRatio(reviseRational(mVideoWidth, mVideoHeight));
						enterPictureInPictureMode(builder.build());
					} else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
						enterPictureInPictureMode();
					}
					else{
						showToastMsg(getString(R.string.error_pip));
					}
				} else {
					showToastMsg(getString(R.string.error_pip));
				}
				break;
			default:
				return false;
		}

		return super.onOptionsItemSelected(item);
	}

	private Rational reviseRational(float videoWidth, float videoHeight) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if ((videoWidth / videoHeight) > 2.39) {
				videoWidth = (videoHeight * 2.39f);
			} else if ((videoHeight / videoWidth) > 2.39) {
				videoHeight = (videoWidth * 2.39f);
			}

			return new Rational((int) videoWidth, (int) videoHeight);
		}
		else{
			return null;
		}
	}

	private boolean supportPIP() {
		UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);

		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) ||
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
	}

	private void createScaleDialog() {
		mScaleDialog = new AlertDialog.Builder(NexPlayerSample.this)
				.setTitle(R.string.scale_mode)
				.setItems(R.array.sacle_list_array, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int selectedIndex) {
						mScaleMode = selectedIndex;
						setPlayerOutputPosition(mVideoWidth, mVideoHeight, mScaleMode);
					}
				}).create();
	}

	private void createAndShowScaleModeDialog() {
		if( mScaleDialog == null ) {
			createScaleDialog();
		}

		if( !mScaleDialog.isShowing() )
			mScaleDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.player_activity_action, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (menu != null && menu.findItem(R.id.bandwidth) != null) {
			NxbInfo info = NxbInfo.getNxbInfo(mNxbWholeList, mCurrentPath, mCurrentPosition);
			List<String> subtitleList = info.getSubtitle();

			menu.findItem(R.id.action_statistics).setVisible(mPrefData.mEnableStatisticsMonitor);
			menu.findItem(R.id.action_multi_stream).setEnabled(!mFastPlay);
			menu.findItem(R.id.action_pip).setVisible(supportPIP());
			menu.findItem(R.id.bandwidth).getSubMenu().findItem(R.id.action_min_max_bandwidth).setEnabled(!mFastPlay);

			boolean dolbyAC3Visible = false;
			boolean dolbyAC4Visible = false;
			boolean captionLanguageVisible = false;
			boolean volumeVisible = false;

			if( mContentInfo != null ) {
				dolbyAC3Visible = (mIsEnginOpen && mContentInfo.mAudioCodec == NexContentInformation.NEXOTI_EC3) ||
						(mIsEnginOpen && mContentInfo.mAudioCodec == NexContentInformation.NEXOTI_AC3);
				dolbyAC4Visible = (mIsEnginOpen && mContentInfo.mAudioCodec == NexContentInformation.NEXOTI_AC4);
				captionLanguageVisible = mContentInfo.mCaptionLanguages != null && mContentInfo.mCaptionLanguages.length > 0;
				volumeVisible = mContentInfo.mCurrAudioStreamID != NexPlayer.MEDIA_STREAM_DISABLE_ID;
			}
			menu.findItem(R.id.audio).getSubMenu().findItem(R.id.action_dolby_ac3_sound).setVisible(dolbyAC3Visible);
			menu.findItem(R.id.audio).getSubMenu().findItem(R.id.action_dolby_ac4_sound).setVisible(dolbyAC4Visible);
			menu.findItem(R.id.audio).getSubMenu().findItem(R.id.action_volume).setVisible(volumeVisible);
			menu.findItem(R.id.text).getSubMenu().findItem(R.id.action_language).setVisible(captionLanguageVisible);
			menu.findItem(R.id.text).getSubMenu().findItem(R.id.action_subtitle).setVisible(subtitleList.size() > 0);
		}

		return super.onMenuOpened(featureId, menu);
	}

	@SuppressLint("NewApi")
	void setPlayerOutputPosition(int videoWidth, int videoHeight, int scaleMode) {
		int width, height, top, left;
		width = height = top = left = 0;
		final int screenWidth = mVideoView.getWidth();
		final int screenHeight = mVideoView.getHeight();
		Log.d(LOG_TAG, "setPlayerOutputPosition screenWidth : " + screenWidth + " screenHeight : " + screenHeight);
		if (mVideoWidth == 0 && mVideoHeight == 0) //(mVideoWidth == 0 && mVideoHeight == 0) means Video Off state
			scaleMode = SCALE_STRETCH_TO_SCREEN;

		float scale = 1f;

		switch (scaleMode) {
			case SCALE_FIT_TO_SCREEN:
				scale = Math.min((float) screenWidth / (float) videoWidth,
						(float) screenHeight / (float) videoHeight);

				width = (int) (videoWidth * scale);
				height = (int) (videoHeight * scale);
				top = (screenHeight - height) / 2;
				left = (screenWidth - width) / 2;

				break;
			case SCALE_ORIGINAL:
				width = videoWidth;
				height = videoHeight;
				top = (screenHeight - videoHeight) / 2;
				left = (screenWidth - videoWidth) / 2;

				break;
			case SCALE_STRETCH_TO_SCREEN:
				width = screenWidth;
				height = screenHeight;

				if(videoWidth != 0 && videoHeight != 0) {
					scale = Math.min((float) screenWidth / (float) videoWidth,
							(float) screenHeight / (float) videoHeight);
				}

				break;
		}

		mCaptionPainter.setRenderingArea(new Rect(left, top, left + width, top + height), scale);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mCaptionPainter.invalidate();
			}
		});

		if (mContentInfo != null && mContentInfo.mCurrVideoStreamID != NexPlayer.MEDIA_STREAM_DISABLE_ID) {
			mVideoView.setOutputPos(left, top, width, height);
		}
	}

	private void changeDolbyAC3PostProcessingValue(int position){
		mNexPlayer.setProperties(DolbyAC3Dialog.AC3_PROPERTY_POST_PROCESSING, position);
	}

	private void changeDolbyAC3EndPointValue(int position){
		Log.d(LOG_TAG, "AC3. SetEndPoint : " + (position+1));
		mNexPlayer.setProperties(DolbyAC3Dialog.AC3_PROPERTY_END_POINT, position+1);
	}

	private void changeDolbyAC3EnhancementGainValue(int position){
		Log.d(LOG_TAG, "AC3. Set Enhancement Gain : " + position+1);
		mNexPlayer.setProperties(DolbyAC3Dialog.AC3_PROPERTY_ENHANCEMENT_GAIN, position);
	}

	private void changeDolbyAC4VirtualizationValue(int position){
		mNexPlayer.setProperties(DolbyAC4Dialog.AC4_PROPERTY_VIRTUALIZATION, position);
	}

	private void changeDolbyAC4EnhancementGainValue(int position){
		mNexPlayer.setProperties(DolbyAC4Dialog.AC4_PROPERTY_ENHANCEMENT_GAIN, position);
	}

	private void changeDolbyAC4MainAssoPrefValue(int position){
		mNexPlayer.setProperties(DolbyAC4Dialog.AC4_PROPERTY_MAIN_ASSO_PREF, position);
	}

	private void changeDolbyAC4PresentationIndexValue(int position){
		mNexPlayer.setProperties(DolbyAC4Dialog.AC4_PROPERTY_PRESENTATION_INDEX, position == -1 ? 0xffff : position);
	}

	private void clearCaptionString() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mTimedMetaTextView.setText("");
				mCaptionPainter.clear();
			}
		});
	}

	private void createAdnShowStatisticDialog() {
		mStatisticsDialog.show();
	}

	private void createAndShowBandWidthDialog() {
		mBandwidthDialog.createAndShow(mPrefData.mMinBandWidth, mPrefData.mMaxBandWidth, new IBandwidthListener() {

			@Override
			public void onBandwidthDialogUpdated(int minBW, int maxBW) {
				boolean isChangedMaxBW = mPrefData.mMaxBandWidth != maxBW;
				boolean isChangedMinBW = mPrefData.mMinBandWidth != minBW;
				NexErrorCode ret = NexErrorCode.UNKNOWN;
				if( isChangedMaxBW && isChangedMinBW ) {
					ret = mABRController.changeMinMaxBandWidth(minBW*BANDWIDTH_KBPS, maxBW*BANDWIDTH_KBPS);
				} else if( isChangedMaxBW ) {
					ret = mABRController.changeMaxBandWidth(maxBW*BANDWIDTH_KBPS);
				} else if( isChangedMinBW ) {
					ret = mABRController.changeMinBandWidth(minBW*BANDWIDTH_KBPS);
				}
				Log.d(LOG_TAG, "changeMinMaxBandWidth Ret : " + ret);
			}
		});
	}

	private void createAndShowCaptionStyleDialog() {
		if (mCaptionDialog == null) {
			mCaptionDialog = new CaptionStyleDialog(this, new CaptionStyleDialog.OnSettingsChangedListener() {
				@Override
				public void onSettingsChanged(NexCaptionSetting settings) {
					mCaptionPainter.setUserCaptionSettings(settings);

					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mCaptionPainter.invalidate();
						}
					});
				}
			});
		}

		if (!mCaptionDialog.isShowing()) {
			mCaptionDialog.createAndShow(mCaptionPainter.getUserCaptionSettings());
		}
	}

	private void createAndShowVolumeDialog() {
		mVolumeDialog.createAndShow(mVolume, new IVolumeListener() {
			@Override
			public void onVolumeDialogUpdated(VOLUME_BUTTON button, int volume) {
				if( button == VOLUME_BUTTON.PROGRESS_CHANGED) {
					mVolume = volume;
					setVolume();
				}
			}
		});
	}

	private void createAndShowMultiStreamDialog() {
		mMultistreamDialog.createAndShow(mContentInfo, mCaptionStreamList, new IMultiStreamListener() {
			public void onVideoStreamDialogUpdated(int streamID, int attrID) {
				if (mContentInfo.mCurrVideoStreamID != streamID ||
						NexContentInfoExtractor.getCurCustomAttributeID(mContentInfo, NexPlayer.MEDIA_STREAM_TYPE_VIDEO) != attrID ) {
					if( mPrefData.mEnableDynamicThumbnail ) {
						if( streamID != NexPlayer.MEDIA_STREAM_DISABLE_ID ) {
							if( mTempVideoStreamID == streamID && mContentInfo.mCurrVideoStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID ) {
								mSeekBar.enableDynamicThumbnail(true);
							} else {
								disableDynamicThumbnail();
								enableDynamicThumbnail();
								setDynamicThumbnailOption(mContentInfo);
							}
						} else {
							mTempVideoStreamID = mContentInfo.mCurrVideoStreamID;
							mSeekBar.enableDynamicThumbnail(false);
						}
					}

					changeVideoStream(streamID, attrID);
					mMultistreamDialog.dismiss();
				}
			}

			@Override
			public void onTextStreamDialogUpdated(int streamID, int channelId, int captionType, int position, boolean inStream) {
				if (mCaptionStreamList.getEnabledIndex() != position) {
					boolean bSetEmbeddedCEA608 = false;

					if (NexContentInformation.NEX_TEXT_CEA608 == captionType) {
						bSetEmbeddedCEA608 = !inStream;
					}

					mCaptionIndex = position;
					if (bSetEmbeddedCEA608) {
						if (streamID != mContentInfo.mCurrTextStreamID) {
							changeTextStream(streamID);
						}
						mNexPlayer.setCEA608CaptionChannel(channelId);
					} else {
						changeTextStream(streamID);
					}

					if (NexContentInformation.NEX_TEXT_CEA608 == captionType || NexContentInformation.NEX_TEXT_CEA708 == captionType) {
						clearCaptionString();
					}

					if (NexContentInformation.NEX_TEXT_CEA == captionType) {
						captionType = mPrefData.mCaptionMode == 0 ? NexContentInformation.NEX_TEXT_CEA608 : NexContentInformation.NEX_TEXT_CEA708;
					}
					mCaptionStreamList.setEnable(position);
					mCaptionPainter.setCaptionType(captionType);

					Log.d(LOG_TAG, "o" +
							" : " + captionType + " , channelId : " + channelId + " , streamID : " + streamID + " , position : " + position);
					mMultistreamDialog.dismiss();
				}
			}

			@Override
			public void onAudioStreamDialogUpdated(int streamID) {
				if (mContentInfo.mCurrAudioStreamID != streamID) {
					changeAudioStream(streamID);
					mMultistreamDialog.dismiss();
				}
			}
		});
	}

	private void changeAudioStream(int streamId) {
		mNexPlayer.setMediaStream(
				streamId,
				mContentInfo.mCurrTextStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID ? NexPlayer.MEDIA_STREAM_DISABLE_ID : NexPlayer.MEDIA_STREAM_DEFAULT_ID,
				mContentInfo.mCurrVideoStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID ? NexPlayer.MEDIA_STREAM_DISABLE_ID : NexPlayer.MEDIA_STREAM_DEFAULT_ID,
				NexPlayer.MEDIA_STREAM_DEFAULT_ID);
	}

	private void changeTextStream(int streamId) {
		mNexPlayer.setMediaStream(
				mContentInfo.mCurrAudioStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID ? NexPlayer.MEDIA_STREAM_DISABLE_ID : NexPlayer.MEDIA_STREAM_DEFAULT_ID,
				streamId,
				mContentInfo.mCurrVideoStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID ? NexPlayer.MEDIA_STREAM_DISABLE_ID : NexPlayer.MEDIA_STREAM_DEFAULT_ID,
				NexPlayer.MEDIA_STREAM_DEFAULT_ID);
	}

	private void changeVideoStream(int streamId, int customAttrId) {
		mNexPlayer.setMediaStream(
				mContentInfo.mCurrAudioStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID ? NexPlayer.MEDIA_STREAM_DISABLE_ID : NexPlayer.MEDIA_STREAM_DEFAULT_ID,
				mContentInfo.mCurrTextStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID ? NexPlayer.MEDIA_STREAM_DISABLE_ID : NexPlayer.MEDIA_STREAM_DEFAULT_ID,
				streamId,
				customAttrId);
	}

	private Runnable updateStatisticsThread = new Runnable() {
		public void run() {
			mHandler.postDelayed(this, 1000);
		}
	};

	private void postProcessingForStopCmd() {
		Log.d(LOG_TAG, "postProcessingForStopCmd mCurrentPath : " + mCurrentPath + " mForeground : " + mForeground);

		// stopped by onError
		if (mPlayerState == PLAYER_FLOW_STATE.BEGINNING_OF_ONERROR)
		{
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					closePlayer();
					mPlayerState = PLAYER_FLOW_STATE.END_OF_ONERROR;
				}
			});
		}
		else
		{
			if ( null == mCurrentPath ) {
				/*
				 * Playback has been completed, no next content to play. Finishing the activity.
				 * onDestroy() will take care of close()/release() of mNexPlayer and mNexALFactory
				 */
				Log.d(LOG_TAG, "null == mCurrentPath");
				mPlayerState = PLAYER_FLOW_STATE.FINISH_ACTIVITY;
				finish();
			} else if(mExistFollowingContentPath) {
				// stopped by previous/next button
				System.gc();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						startPlay();
					}
				}, 300);
			}
		}
	}

	protected boolean isStreaming() {
		return mIsStreaming;
	}

	protected HashMap<String, String> GetOptionalHeaderFields(){
		HashMap<String, String>  optionalHeaders = new HashMap<>();
		Intent intent = getIntent();
		ArrayList<String> optionalHeadersList = intent.getStringArrayListExtra("WVDRMOptionalHeaders");
		if(optionalHeadersList == null ){
			return null;
		}


		for (String optionalHeader : optionalHeadersList) {
			String[] item = optionalHeader.split(":");
			optionalHeaders.put(item[0], item[1]);
			Log.i(LOG_TAG, " WVDRM Optional Header key : " + item[0] + "," + "value : " + item[1]);
		}

		return optionalHeaders;
	}

	@Override
	public void onNetworkStateChanged(Context context) {
		if( NetworkUtils.getConnectivityStatus(context) != NetworkUtils.TYPE_NOT_CONNECTED ) {
			if( mNexPlayer != null && mNexPlayer.isInitialized() && isStreaming() ) {
				int state = mNexPlayer.getState();

				if( state == NexPlayer.NEXPLAYER_STATE_PLAY || state == NexPlayer.NEXPLAYER_STATE_PAUSE )
					mNexPlayer.reconnectNetwork();
			}
		}
	}

	private class WaitingForUnlockThread extends Thread {
		@Override
		public void run() {
			KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
			if (keyguardManager != null) {
				Log.d(LOG_TAG, "waitForKeyGuardUnlock keyguardManager.inKeyguardRestrictedInputMode() : " + keyguardManager.inKeyguardRestrictedInputMode());
				while( keyguardManager.inKeyguardRestrictedInputMode() ) {
					try {
						if( isInterrupted() )
							break;

						Log.d(LOG_TAG, "waiting for 100 msec...");
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
			}

			notifyKeyGuardUnlocked();
		}
	}

}
