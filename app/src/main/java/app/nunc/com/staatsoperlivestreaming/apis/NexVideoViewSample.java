package app.nunc.com.staatsoperlivestreaming.apis;

import android.app.AlertDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.dialog.NexContentInfoDialog;
import app.nunc.com.staatsoperlivestreaming.info.NxbInfo;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexABRController;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionAttribute;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionRenderView;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexContentInformation;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexID3TagInformation;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexID3TagPicture;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexID3TagText;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStatisticsMonitor;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStreamInformation;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexVideoView;
import app.nunc.com.staatsoperlivestreaming.util.NetworkBroadcastReceiver;
import app.nunc.com.staatsoperlivestreaming.util.NetworkUtils;
import app.nunc.com.staatsoperlivestreaming.util.NexFileIO;
import app.nunc.com.staatsoperlivestreaming.util.PlayListUtils;

import static app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStatisticsMonitor.STATISTICS_GENERAL;

public class NexVideoViewSample extends AppCompatActivity implements NetworkBroadcastReceiver.NetworkListener {
    private static final String LOG_TAG = "NexVideoViewSample";
    private static final Handler mHandler = new Handler();

    private static final int BANDWIDTH_KBPS = 1024;
    private static final int BUFFERING_END_PERCENTAGE = 100;
    private static final int BUFFERING_BEGIN_PERCENTAGE = 0;

    protected static final int REPLAY_MODE_NEXT = 0;
    protected static final int REPLAY_MODE_QUIT = 2;
    protected static final int REPLAY_MODE_PREVIOUS = 3;

    protected NexVideoView mVideoView = null;
    private NexCaptionRenderView mCaptionRenderView = null;
    private LinearLayout mProgressLayout = null;
    private TextView mProgressTextView = null;
    private TextView mErrorTextView = null;
    private TextView mCurVideoTrackTextView = null;
    private ImageView mImageView = null;
    private ScrollView mAudioLyricScrollView = null;
    private TextView mAudioLyricTextView = null;
    private TextView mTimedMetaTextView = null;
    private TextView mExternalSubtitleView = null;
    private MediaController mMediaController = null;

    protected NexPreferenceData mPrefData = null;

    private int mCurrentTextStream = 0;
    private int mCurrentAudioStream = 0;
    private int mCurrentVideoStream = 0;
    private float mVolume = 10.0f;

    private NexContentInfoDialog mContentInfoDialog = null;
    private AlertDialog mScalingModeDialog = null;
    private DolbyAC3Dialog mDolbyAC3Dialog = null;
    private DolbyAC4Dialog mDolbyAC4Dialog = null;
    private VolumeDialog mVolumeDialog = null;
    private BandwidthDialog mBandwidthDialog = null;
    private StatisticsDialog mStatisticsDialog = null;
    private SubtitleChangeDialog mSubtitleChangeDialog = null;
    private MultiStreamDialog mMultiStreamDialog = null;
    private TargetBandWidthDialog mTargetBandWidthDialog = null;
    private CaptionLanguageDialog mCaptionLanguageDialog = null;
    private CaptionRenderViewSettingsDialog mCaptionSettingsDialog = null;

    private NexStatisticsMonitor mStatisticsMonitor = null;

    private PlayListUtils mPlayListUtils = null;
    protected ArrayList<File> mLocalFileList = null;
    protected ArrayList<NxbInfo> mNxbInfoList = null;
    protected String mCurrentPath = null;
    protected Uri mCurrentUri = null;
    protected String mCurrentSubtitlePath = null;
    protected int mCurrentIndex = 0;
    protected NexContentInformation mContentInfo = null;

    private int mStartPosition = 0;
    protected boolean mNeedToStart = false;
    protected boolean mForeground = true;

    private int mCurrentCaptionIndex = 1;
    private int mTargetCaptionIndex = 0;

    private Toolbar mToolbar;

    private boolean isTVDevice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if( Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO )
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB )
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.nex_video_view_sample_activity);

        mPlayListUtils = new PlayListUtils(getResources().getStringArray(R.array.playable_extension_list));
        mPrefData = new NexPreferenceData(this);
        mPrefData.loadPreferenceData();

        setupDialogs();

        preloadLibs();
        getIntentExtra(getIntent());

        mMediaController = new MediaController(this, true);
        if( mNxbInfoList != null || mLocalFileList != null )
            mMediaController.setPrevNextListeners(mNextButtonClickListener, mPrevButtonClickListener);

        mVideoView = (NexVideoView)findViewById(R.id.video_view);
        mCaptionRenderView = mVideoView.getCaptionRenderView();

        setupUIComponents();

        mVideoView.setMediaController(mMediaController);
        setVideoViewListeners();

        NexVideoView.Settings settings = new NexVideoView.Settings();
        setupVideoViewSettings(settings, mPrefData);
        mVideoView.setSettings(settings);

        NetworkBroadcastReceiver.addListener(this);
        open(mCurrentUri, mCurrentPath);

        UiModeManager manager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        Log.d(LOG_TAG, "manager.getCurrentModeType() : " + manager.getCurrentModeType());
        isTVDevice = manager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }

    private void setupDialogs() {
        mStatisticsDialog = new StatisticsDialog(this);
        mSubtitleChangeDialog = new SubtitleChangeDialog(this, new SubtitleChangeDialog.Listener() {
            @Override
            public void onSubtitleChanged(String subtitlePath) {
                Log.d(LOG_TAG, "mSubtitleChangeDialog onSubtitleChanged subtitlePath : " + subtitlePath);
                mVideoView.addSubtitleSource(subtitlePath);
                mCurrentSubtitlePath = subtitlePath;
            }
        });
    }
    private void getIntentExtra(Intent intent) {
        Uri uri = intent.getData();

        if( uri != null ) {
            try {
                mCurrentUri = Uri.parse(URLEncoder.encode(uri.toString(), "utf-8"));
            }
            catch(UnsupportedEncodingException e) {
                Log.d(LOG_TAG, "UnsupportedEncodingException uri : " + uri.toString());
                mCurrentUri = uri;
            }
        } else {
            mLocalFileList = (ArrayList<File>) intent.getSerializableExtra("data");
            mNxbInfoList = (ArrayList<NxbInfo>) intent.getSerializableExtra("wholelist");
            mCurrentIndex = intent.getIntExtra("selectedItem", 0);
            mCurrentPath = intent.getStringExtra("theSimpleUrl");

            if( mLocalFileList != null && mCurrentPath == null ) {
                mCurrentPath = mLocalFileList.get(mCurrentIndex).getAbsolutePath();
                mCurrentSubtitlePath = NexFileIO.subtitlePathFromMediaPath(mCurrentPath);
            } else if( mNxbInfoList != null ) {
                List<String> subtitles = mNxbInfoList.get(mCurrentIndex).getSubtitle();
                if( subtitles != null && subtitles.size() > 0 )
                    mCurrentSubtitlePath = subtitles.get(0);
            }
        }
    }

    protected void open(Uri uri, String path) {
        mProgressTextView.setText(getResources().getString(R.string.buffer_open));
        mProgressLayout.setVisibility(View.VISIBLE);
        mToolbar.setTitle(NexFileIO.getContentTitle(getCurrentPath()));

        if( uri != null ) {
            mVideoView.setVideoURI(uri);
        } else if( path != null ) {
            mVideoView.setVideoPath(path);
        }
    }

    protected String getCurrentPath() {
        String path = "";
        if( mCurrentUri != null ) {
            path = mCurrentUri.getPath();
        } else if( mCurrentPath != null ) {
            path = mCurrentPath;
        }

        return path;
    }

    private void setupUIComponents() {
        mProgressLayout = (LinearLayout)findViewById(R.id.progress_layout);
        mProgressTextView = (TextView)findViewById(R.id.progress_text);
        mErrorTextView = (TextView)findViewById(R.id.error_text_view);
        mAudioLyricScrollView = (ScrollView)findViewById(R.id.lyric_scroll_view);
        mAudioLyricTextView = (TextView)findViewById(R.id.lyric_text_view);
        mTimedMetaTextView = (TextView)findViewById(R.id.timed_meta_text_view);
        mToolbar = (Toolbar)findViewById(R.id.tool_bar);
        mToolbar.setTitle("");
        mToolbar.setBackgroundColor(getResources().getColor(R.color.color_controller_background));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inflateTitleBar();
        setupTitleBar();

        mImageView = new ImageView(this);
        mImageView.setBackgroundColor(Color.BLACK);
        mImageView.setVisibility(View.INVISIBLE);

        mExternalSubtitleView = new TextView(this);
        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        param.gravity = Gravity.BOTTOM;
        mCaptionRenderView.setExternalSubtitleTextView(mExternalSubtitleView, param, mPrefData.mTextEncodingPreset);

        mVideoView.addView(mImageView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        mImageView.setOnTouchListener(mOnTouchListener);
        mAudioLyricScrollView.setOnTouchListener(mOnTouchListener);
    }

    protected void inflateTitleBar() {
        ViewStub stub = (ViewStub) findViewById(R.id.title_view_stub);
        stub.setLayoutResource(R.layout.title_bar_default);
        stub.inflate();
    }

    protected void setupTitleBar() {
        mCurVideoTrackTextView = (TextView)findViewById(R.id.cur_video_track_text_view);
        updateCurVideoTrackTextView(null);
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if( event.getAction() == MotionEvent.ACTION_UP )
                return mVideoView.onTouchEvent(event);
            return false;
        }
    };

    private void setVideoViewListeners() {
        mVideoView.setOnPreparedListener(new NexVideoView.OnPreparedListener() {
            @Override
            public void onPrepared(NexPlayer mp) {
                onPlayerPrepared(mp);
            }
        });
        mVideoView.setOnCompletionListener(new NexVideoView.OnCompletionListener() {
            @Override
            public void onCompletion(NexPlayer mp) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setupPlayContent(mPrefData.mReplayMode);
                        mVideoView.stopPlayback();
                    }
                });
            }
        });
        mVideoView.setOnInfoListener(new NexVideoView.OnInfoListener() {
            @Override
            public void onInfo(NexPlayer mp, final NexContentInformation info) {
                onContentInformationUpdated(mp, info);
            }
        });
        mVideoView.setOnErrorListener(new NexVideoView.OnErrorListener() {
            @Override
            public void onError(NexPlayer mp, final NexPlayer.NexErrorCode errorCode) {
                onPlayerError(mp, errorCode);
            }
        });
        mVideoView.setOnBufferingUpdateListener(new NexVideoView.OnBufferingUpdateListener() {
            @Override
            public void onBufferingBegin(NexPlayer mp) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressLayout.setVisibility(View.VISIBLE);
                        setProgressBufferingPercent(BUFFERING_BEGIN_PERCENTAGE);
                    }
                });
            }

            @Override
            public void onBuffering(NexPlayer mp, final int progressInPercent) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setProgressBufferingPercent(progressInPercent);
                        mProgressLayout.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onBufferingEnd(NexPlayer mp) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setProgressBufferingPercent(BUFFERING_END_PERCENTAGE);
                        mProgressLayout.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        mVideoView.setOnConfigurationListener(new NexVideoView.OnConfigurationListener() {
            @Override
            public void onConfiguration() {
                onPlayerConfiguration();
            }
        });
        mVideoView.setOnStartCompleteListener(new NexVideoView.OnStartCompleteListener() {
            @Override
            public void onStartComplete(NexPlayer mp) {
                onPlayerStart(mp);
            }
        });
        mVideoView.setOnStopCompleteListener(new NexVideoView.OnStopCompleteListener() {
            @Override
            public void onStopComplete(NexPlayer mp, int result) {
                onPlayerStop(mp, result);
            }
        });
        mVideoView.setOnTimedMetaRenderRenderListener(new NexVideoView.OnTimedMetaRenderRenderListener() {
            @Override
            public void onTimedMetaRenderRender(NexPlayer mp, final NexID3TagInformation timedMeta) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        NexID3TagPicture picture = timedMeta.getPicture();
                        if (picture != null && picture.getPictureData() != null)
                            showAudioImage(timedMeta);
                        showLyricText(timedMeta);
                        showTimedMetaText(timedMeta);
                    }
                });
            }
        });
        mVideoView.setOnMediaStreamChangedListener(new NexVideoView.OnMediaStreamChangedListener() {
            @Override
            public void onMediaStreamChanged(NexPlayer mp, int result, int streamType, int streamID) {
                onStreamChanged(mp, result, streamType, streamID);
            }
        });
    }

    protected void onStreamChanged(NexPlayer mp, int result, int streamType, int streamID) {
        if (NexPlayer.MEDIA_STREAM_TYPE_TEXT == streamType && 0 == result) {
            mCurrentCaptionIndex = mTargetCaptionIndex;
        }
    }

    private void setCaptionStreams(NexContentInformation contentInformation, ArrayList<CaptionInformation> captionStreams) {
        captionStreams.clear();

        if (null != contentInformation) {
            for (NexStreamInformation streamInfo : contentInformation.mArrStreamInformation) {
                if (streamInfo.mType == NexPlayer.MEDIA_STREAM_TYPE_TEXT) {
                    boolean target = streamInfo.mID == contentInformation.mCurrTextStreamID;
                    switch (streamInfo.mRepresentCodecType) {
                        case NexContentInformation.NEX_TEXT_CEA:
                            if (!TextUtils.isEmpty(streamInfo.mInStreamID)) {
                                final String KEYWORD_CEA608 = "CC";
                                final String KEYWORD_CEA708 = "SERVICE";

                                if (streamInfo.mInStreamID.toUpperCase().startsWith(KEYWORD_CEA608)) {
                                    captionStreams.add(new CaptionInformation(getStringByID3TagInfo(streamInfo.mName), streamInfo.mID, getStringByID3TagInfo(streamInfo.mLanguage), streamInfo.mInStreamID, NexContentInformation.NEX_TEXT_CEA608, target));
                                } else if (streamInfo.mInStreamID.toUpperCase().startsWith(KEYWORD_CEA708)) {
                                    captionStreams.add(new CaptionInformation(getStringByID3TagInfo(streamInfo.mName), streamInfo.mID, getStringByID3TagInfo(streamInfo.mLanguage), streamInfo.mInStreamID, NexContentInformation.NEX_TEXT_CEA708, target));
                                }
                            }
                            break;
                        default:
                            captionStreams.add(new CaptionInformation(getStringByID3TagInfo(streamInfo.mName), streamInfo.mID, getStringByID3TagInfo(streamInfo.mLanguage), streamInfo.mInStreamID, streamInfo.mRepresentCodecType, target));
                            break;
                    }
                }
            }
        }
    }

    protected void onPlayerPrepared(NexPlayer mp) {
        if( mForeground ) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "onPrepared");
                    setVisibility(View.INVISIBLE);
                    mVideoView.addSubtitleSource(mCurrentSubtitlePath);
                    mVideoView.seekTo(mPrefData.mStartSec * 1000);
                    mVideoView.start();
                }
            });
        }
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

    protected void onPlayerError(NexPlayer mp, final NexPlayer.NexErrorCode errorCode) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String errorMsg = getErrorMsg(errorCode);

                setVisibility(View.INVISIBLE);
                mErrorTextView.setText(errorMsg);
                mErrorTextView.setVisibility(View.VISIBLE);

                mVideoView.stopPlayback();
            }
        });
    }
    protected void onContentInformationUpdated(NexPlayer mp, final NexContentInformation info) {
        mContentInfo = info;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (info.mMediaType == 1) {
                    showAudioImage(info.mID3Tag);
                    showLyricText(info.mID3Tag);
                } else {
                    mAudioLyricScrollView.setVisibility(View.INVISIBLE);
                    mImageView.setVisibility(View.INVISIBLE);
                }

                if( mContentInfoDialog != null )
                    mContentInfoDialog.setContentInfo(mContentInfo);
                updateCurVideoTrackTextView(mContentInfo);
            }
        });
    }

    protected void onPlayerConfiguration() {
        if (mPrefData.mEnableStatisticsMonitor) {
            setupStatisticsMonitor();
        }

        setupABRListener();
        setProperties(mVideoView, mPrefData);
        mVideoView.getNexPlayer().setDebugLogs(mPrefData.mCodecLogLevel, mPrefData.mRendererLogLevel, mPrefData.mProtoclLogLevel);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mVideoView.bringChildToFront(mImageView);
                mVideoView.bringChildToFront(mCaptionRenderView);
            }
        });
    }

    protected void onPlayerStart(final NexPlayer mp) {
        NxbInfo info = NxbInfo.getNxbInfo(mNxbInfoList, mCurrentPath, mCurrentIndex);

        if( info != null ) {
            if (PlaybackHistory.isExist(NexVideoViewSample.this, info)) {
                PlaybackHistory.updateHistory(NexVideoViewSample.this, info);
            } else {
                PlaybackHistory.addHistory(NexVideoViewSample.this, info);
            }
        }

    }

    protected void onPlayerStop(final NexPlayer mp, final int result) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mErrorTextView.getVisibility() == View.INVISIBLE) {
                    if (mCurrentPath == null && mCurrentUri == null)
                        finish();
                    else if (!mNeedToStart) {
                        resetPlayerStatus();
                        open(mCurrentUri, mCurrentPath);
                    }
                }
            }
        });
    }

    private void showTimedMetaText(NexID3TagInformation timedMeta) {
        try {
            NexID3TagText text = timedMeta.getText();
            String strText;
            if( text != null ) {
                strText = new String(text.getTextData(), 0, text.getTextData().length, getTextEncodingType(text.getEncodingType()));
                mTimedMetaTextView.setText(strText);
            }

            ArrayList<NexID3TagText> arrExtraData = timedMeta.getArrExtraData();

            if ( arrExtraData != null ) {
                for (int i = 0; i < arrExtraData.size(); ++i) {
                    NexID3TagText ID3ExtraData = arrExtraData.get(i);
                    String str1 = new String(ID3ExtraData.getTextData(), 0, ID3ExtraData.getTextData().length,
                            getTextEncodingType(ID3ExtraData.getEncodingType()));
                    String str2 = new String(ID3ExtraData.getExtraDataID(), 0, ID3ExtraData.getExtraDataID().length,
                            getTextEncodingType(ID3ExtraData.getEncodingType()));

                    strText = "getExtraDataID : " + str2 + " getExtraData : " + str1;
                    mTimedMetaTextView.setText(strText);
                }
            }

            if( mTimedMetaTextView.getText().length() > 0 ) {
                Log.d(LOG_TAG, "showTimedMetaText getText() : " + mTimedMetaTextView.getText());
                mTimedMetaTextView.setVisibility(View.VISIBLE);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void showLyricText(NexID3TagInformation info) {
        if( mAudioLyricScrollView != null && mAudioLyricTextView != null ) {
            try {
                String strInfo = "";
                if ( info != null ) {
                    NexID3TagText text;

                    text = info.getArtist();
                    strInfo += new String(text.getTextData(), 0, text.getTextData().length, getTextEncodingType(text.getEncodingType())) + "\n";

                    text = info.getTitle();
                    strInfo += new String(text.getTextData(), 0, text.getTextData().length, getTextEncodingType(text.getEncodingType())) + "\n";

                    text = info.getAlbum();
                    strInfo += new String(text.getTextData(), 0, text.getTextData().length, getTextEncodingType(text.getEncodingType())) + "\n\n";

                    text = info.getLyric();
                    strInfo += new String(text.getTextData(), 0, text.getTextData().length, getTextEncodingType(text.getEncodingType()));

                    if( strInfo.length() > 0 ) {
                        mAudioLyricScrollView.scrollTo(0, 0);
                        mAudioLyricScrollView.setVisibility(View.VISIBLE);
                        setLyricScrollViewOutputPosition();
                    }
                }

                mAudioLyricTextView.setText(strInfo);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Exception - setLyric() : " + e.getMessage());
                mAudioLyricTextView.setText("");
            }
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

        mAudioLyricScrollView.setLayoutParams(params);
        mAudioLyricScrollView.requestLayout();
        mAudioLyricTextView.setWidth(transWidth);
    }

    private String getTextEncodingType(int eType) {
        if (eType == NexID3TagText.ENCODING_TYPE_UTF16)
            return "UTF-16";
        else if(eType == NexID3TagText.ENCODING_TYPE_UNICODE)
            return "UTF-16LE";
        else
            return "UTF-8";
    }

    private void showAudioImage(NexID3TagInformation info) {
        if( mImageView != null ) {
            int res = R.drawable.audio_skin2;
            Bitmap bm = null;

            if( info != null ) {
                NexID3TagPicture picture = info.getPicture();
                if( picture != null ) {
                    bm = BitmapFactory.decodeByteArray(picture.getPictureData(), 0, picture.getPictureData().length);
                }
            }

            if( bm != null ) {
                mImageView.setImageBitmap(bm);
            } else {
                mImageView.setImageResource(res);
            }
            mImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateCurVideoTrackTextView(NexContentInformation info) {
        String text = "Track : " + NexContentInfoExtractor.getCurrTrackID(NexPlayer.MEDIA_STREAM_TYPE_VIDEO, info);
        mCurVideoTrackTextView.setText(text);
    }

    private void setupStatisticsMonitor() {
        mStatisticsMonitor = new NexStatisticsMonitor(mVideoView.getNexPlayer(), false);
        mStatisticsMonitor.setDuration(STATISTICS_GENERAL, 5);
        NexStatisticsMonitor.IStatisticsListener listener = new NexStatisticsMonitor.IStatisticsListener() {
            @Override
            public void onUpdated(int statisticsType, HashMap<NexStatisticsMonitor.IStatistics, Object> map) {
                mStatisticsDialog.onUpdated(statisticsType, map);
            }
        };
        mStatisticsMonitor.setListener(listener);
    }

    public void setupABRListener() {
        mVideoView.getABRController().setIABREventListener(new NexABRController.IABREventListener() {
            @Override
            public void onMinMaxBandWidthChanged(NexPlayer.NexErrorCode result, int minBwBps, int maxBwBps) {
                if (result == NexPlayer.NexErrorCode.NONE) {
                    mPrefData.setMinMaxBandwidth(minBwBps / BANDWIDTH_KBPS, maxBwBps / BANDWIDTH_KBPS);
                }
            }

            @Override
            public void onTargetBandWidthChanged(NexPlayer.NexErrorCode result, int reqBwBps, int selBwBps) {
            }
        });
    }

	protected void resetPlayerStatus() {
		if( mCaptionLanguageDialog != null )
			mCaptionLanguageDialog.reset();
		if( mTargetBandWidthDialog != null )
			mTargetBandWidthDialog.dismiss();
		mCurrentVideoStream = 0;
		mCurrentAudioStream = 0;
		mCurrentTextStream = 0;

        mCurrentCaptionIndex = 1;
        mTargetCaptionIndex = 0;

		updateCurVideoTrackTextView(null);
		resetAllTextView();
		setVisibility(View.INVISIBLE);
	}

	private void resetAllTextView() {
		mToolbar.setTitle("");
		mCurVideoTrackTextView.setText("");
		mErrorTextView.setText("");
		mAudioLyricTextView.setText("");
		mTimedMetaTextView.setText("");
	}

	protected void setVisibility(int visibility) {
		mImageView.setVisibility(visibility);
		mAudioLyricScrollView.setVisibility(visibility);
		mTimedMetaTextView.setVisibility(visibility);
		mProgressLayout.setVisibility(visibility);
		mErrorTextView.setVisibility(visibility);
	}

	protected void setupPlayContent(int replayMode) {
		int newIndex = mCurrentIndex;

		switch (replayMode) {
			case REPLAY_MODE_NEXT :
				newIndex++;
				if( mLocalFileList != null ) {
					newIndex = mPlayListUtils.getNextContentIndex(mLocalFileList, newIndex);
					if( newIndex != PlayListUtils.NOT_FOUND ) {
						mCurrentIndex = newIndex;
						mCurrentPath = mLocalFileList.get(mCurrentIndex).getAbsolutePath();
						mCurrentSubtitlePath = NexFileIO.subtitlePathFromMediaPath(mCurrentPath);
					}
				} else {
					int size = 0;
					if( mNxbInfoList != null ) {
						size = mNxbInfoList.size();
					}

					if( size <= newIndex ) {
						newIndex = 0;
					}

					mCurrentIndex = newIndex;
					if( mNxbInfoList != null ) {
						mCurrentPath = mNxbInfoList.get(mCurrentIndex).getUrl();
                        List<String> subtitles = mNxbInfoList.get(mCurrentIndex).getSubtitle();
                        if( subtitles != null && subtitles.size() > 0 )
                            mCurrentSubtitlePath = subtitles.get(0);
					}
				}
				break;
			case REPLAY_MODE_QUIT :
				mCurrentPath = null;
				mCurrentUri = null;
				break;
			case REPLAY_MODE_PREVIOUS :
				newIndex--;
				if( mLocalFileList != null ) {
					newIndex = mPlayListUtils.getPreviousContentIndex(mLocalFileList, newIndex);
					if( newIndex != PlayListUtils.NOT_FOUND ) {
						mCurrentIndex = newIndex;
						mCurrentPath = mLocalFileList.get(mCurrentIndex).getAbsolutePath();
						mCurrentSubtitlePath = NexFileIO.subtitlePathFromMediaPath(mCurrentPath);
					}
				} else {
					int size = 0;
					if( mNxbInfoList != null ) {
						size = mNxbInfoList.size();
					}

					if( newIndex < 0 ) {
						newIndex = size - 1;
					}

					mCurrentIndex = newIndex;
					if( mNxbInfoList != null ) {
						mCurrentPath = mNxbInfoList.get(mCurrentIndex).getUrl();
                        List<String> subtitles = mNxbInfoList.get(mCurrentIndex).getSubtitle();
                        if( subtitles != null && subtitles.size() > 0 )
                            mCurrentSubtitlePath = subtitles.get(0);
					}
				}
				break;
		}
	}

	protected void setProperties(NexVideoView videoView, NexPreferenceData prefData) {
		videoView.setProperty(NexPlayer.NexProperty.MAX_BW, prefData.mMaxBandWidth * BANDWIDTH_KBPS);
		videoView.setProperty(NexPlayer.NexProperty.MIN_BW, prefData.mMinBandWidth * BANDWIDTH_KBPS);
		videoView.setProperty(NexPlayer.NexProperty.TIMESTAMP_DIFFERENCE_VDISP_WAIT, prefData.mVideoDisplayWait);
		videoView.setProperty(NexPlayer.NexProperty.TIMESTAMP_DIFFERENCE_VDISP_SKIP, prefData.mVideoDisplaySkip);
		videoView.setProperty(NexPlayer.NexProperty.AV_SYNC_OFFSET, (int) (prefData.mAVSyncOffset * 1000));
		videoView.setProperty(NexPlayer.NexProperty.PREFER_LANGUAGE_AUDIO, prefData.mPrefLanguageAudio);
		videoView.setProperty(NexPlayer.NexProperty.PREFER_LANGUAGE_TEXT, prefData.mPrefLanguageText);
		videoView.setProperty(NexPlayer.NexProperty.PREFER_BANDWIDTH, 100);
		videoView.setProperty(NexPlayer.NexProperty.PREFER_AV, 1);
		videoView.setProperty(NexPlayer.NexProperty.START_NEARESTBW, prefData.mStartNearestBW);
		videoView.setProperty(NexPlayer.NexProperty.SUBTITLE_TEMP_PATH, prefData.mSubtitleDownloadPath);

		if( !prefData.mEnableAudioOnlyTrack )
			videoView.setProperty(NexPlayer.NexProperty.ENABLE_AUDIOONLY_TRACK, 0);

		if( prefData.mIgnoreTextmode )
			videoView.setProperty(NexPlayer.NexProperty.IGNORE_CEA608_TEXTMODE_COMMAND, 1);

		if( !prefData.mEnableWebVTT )
			videoView.setProperty(NexPlayer.NexProperty.ENABLE_WEBVTT, 0);

		if( !prefData.mWebVTTWaitOpen )
			videoView.setProperty(NexPlayer.NexProperty.WEBVTT_WAITOPEN, 0);

		if(  prefData.mHLSRunModeStable )
			videoView.setProperty(NexPlayer.NexProperty.HLS_RUNMODE, 1);

		if( prefData.mCaptionMode == 1 )
			videoView.setProperty(NexPlayer.NexProperty.ENABLE_CEA708, 1);

		if ( !prefData.mUseEyePleaser )
			videoView.setProperty(NexPlayer.NexProperty.SUPPORT_EYE_PLEASER, 0);

		if( prefData.mIsTrackdownEnabled ) {
			videoView.setProperty(NexPlayer.NexProperty.ENABLE_TRACKDOWN, 1);
			videoView.setProperty(NexPlayer.NexProperty.TRACKDOWN_VIDEO_RATIO, prefData.mTrackdownThreshold);
		}

		if( prefData.mBufferTime != 0 ) {
			videoView.setProperty(NexPlayer.NexProperty.INITIAL_BUFFERING_DURATION, (int) (prefData.mBufferTime * 1000));
			videoView.setProperty(NexPlayer.NexProperty.RE_BUFFERING_DURATION, (int) (prefData.mBufferTime * 1000));
		}
	}

	private void setupVideoViewSettings(NexVideoView.Settings settings, NexPreferenceData prefData) {
		NexVideoView.Settings.CEARenderMode ceaRenderMode = NexVideoView.Settings.CEARenderMode.CEA_608;
		if( prefData.mCaptionMode == 1 )
			ceaRenderMode = NexVideoView.Settings.CEARenderMode.CEA_708;

		int pixelFormat = PixelFormat.RGBA_8888;
		if( prefData.mColorSpace == 1 )
			pixelFormat = PixelFormat.RGB_565;

		settings.setValue(NexVideoView.Settings.BOOL_USE_UDP, prefData.mUseUDP);
		settings.setValue(NexVideoView.Settings.CEA_RENDER_MODE, ceaRenderMode);
		settings.setValue(NexVideoView.Settings.PIXELFORMAT_FORMAT, pixelFormat);
		settings.setValue(NexVideoView.Settings.INT_LOG_LEVEL, prefData.mLogLevel);

	}

	private void setProgressBufferingPercent(int progress_in_percent) {
		String text = getResources().getString(R.string.buffer_ing) + progress_in_percent + getResources().getString(R.string.buffer_percent);
		mProgressTextView.setText(text);
	}

	private void preloadLibs() {
		if( !PlayerEnginePreLoader.isLoaded() ) {
			int codecMode = mPrefData.mPreloadHWOnly ? 2 : mPrefData.mCodecMode;
			String libraryPath = this.getApplicationInfo().dataDir+"/" ;
			PlayerEnginePreLoader.Load(libraryPath, this, codecMode);
		}
	}

	private ArrayAdapter<NexVideoView.ScalingMode> getScalingModeArrayAdapter() {
		ArrayList<NexVideoView.ScalingMode> list = new ArrayList<NexVideoView.ScalingMode>();
		list.add(NexVideoView.ScalingMode.SCALE_ASPECT_FIT);
		list.add(NexVideoView.ScalingMode.SCALE_TO_FILL);
        list.add(NexVideoView.ScalingMode.SCALE_ASPECT_FILL);
		return new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, list);
	}

	private AlertDialog createScalingDialog() {
		final ArrayAdapter<NexVideoView.ScalingMode> adapter = getScalingModeArrayAdapter();

		return new AlertDialog.Builder(this)
				.setTitle(R.string.scale_mode)
				.setSingleChoiceItems(adapter, mVideoView.getScalingMode().getInteger(), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mVideoView.setScalingMode(adapter.getItem(which));
						dialog.dismiss();
					}
				}).create();
	}

	private void createAndShowScalingModeDialog() {
		if( mScalingModeDialog == null ) {
			mScalingModeDialog = createScalingDialog();
		}

		if( !mScalingModeDialog.isShowing() )
			mScalingModeDialog.show();
	}

	private void createAndShowContentInfoDialog(NexContentInformation info) {
		if( info != null ) {
			if ( mContentInfoDialog == null )
				mContentInfoDialog = new NexContentInfoDialog(this, new NexContentInfoDialog.IListener() {
					@Override
					public int getAC3DecoderType() {
						return mVideoView.getNexPlayer().getProperties(DolbyAC3Dialog.AC3_PROPERTY_DECODER_TYPE);
					}
				});

			mContentInfoDialog.setContentInfo(info);
			mContentInfoDialog.show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
        mForeground = true;
        onActivityResume();
    }

    protected void onActivityResume() {
        mVideoView.onResume();

        if( mNeedToStart ) {
            mNeedToStart = false;
            mVideoView.start(mStartPosition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mForeground = false;
        onActivityPause();
    }

    protected void onActivityPause() {
        mVideoView.onPause();

        if( mErrorTextView.getVisibility() != View.VISIBLE ) {
            mNeedToStart = true;
            mStartPosition = mVideoView.getCurrentPosition();
            mVideoView.stopPlayback();
        }
    }

    @Override
    protected void onDestroy() {
        mCurrentPath = null;
        mCurrentUri = null;
        mVideoView.release();

        PlayerEnginePreLoader.deleteAPKAsset(this);
        NetworkBroadcastReceiver.removeListener(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.player_activity_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if( menu != null && menu.findItem(R.id.bandwidth) != null ) {
            NxbInfo info = NxbInfo.getNxbInfo(mNxbInfoList, mCurrentPath, mCurrentIndex);
            List<String> subtitleList = info.getSubtitle();

            menu.findItem(R.id.action_statistics).setVisible(mPrefData.mEnableStatisticsMonitor);
            boolean dolbyAC3Visible = false;
            boolean dolbyAC4Visible = false;
            boolean captionLanguageVisible = false;
            boolean volumeVisible = false;

            if( mContentInfo != null ) {
                dolbyAC3Visible = (mContentInfo.mAudioCodec == NexContentInformation.NEXOTI_EC3) ||
                        (mContentInfo.mAudioCodec == NexContentInformation.NEXOTI_AC3);
                dolbyAC4Visible = (mContentInfo.mAudioCodec == NexContentInformation.NEXOTI_AC4);
                captionLanguageVisible = mContentInfo.mCaptionLanguages != null && mContentInfo.mCaptionLanguages.length > 0;
                volumeVisible = mContentInfo.mCurrAudioStreamID != NexPlayer.MEDIA_STREAM_DISABLE_ID;
            }
            menu.findItem(R.id.audio).getSubMenu().findItem(R.id.action_dolby_ac3_sound).setVisible(dolbyAC3Visible);
            menu.findItem(R.id.audio).getSubMenu().findItem(R.id.action_dolby_ac4_sound).setVisible(dolbyAC4Visible);
            menu.findItem(R.id.text).getSubMenu().findItem(R.id.action_language).setVisible(captionLanguageVisible);
            menu.findItem(R.id.audio).getSubMenu().findItem(R.id.action_volume).setVisible(volumeVisible);
            menu.findItem(R.id.text).getSubMenu().findItem(R.id.action_subtitle).setVisible(subtitleList.size() > 0);
        }

        return super.onMenuOpened(featureId, menu);
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
                createAndShowScalingModeDialog();
                break;
            case R.id.action_caption_style:
                createAndShowCaptionSettingDialog();
                break;
            case R.id.action_statistics:
                mStatisticsDialog.show();
                break;
            case R.id.action_volume:
                createAndShowVolumeDialog();
                break;
            case R.id.action_dolby_ac3_sound:
                createAndShowDolbyAC3Dialog();
                break;
            case R.id.action_dolby_ac4_sound:
                createAndShowDolbyAC4Dialog();
                break;
            case R.id.action_target_bandwidth:
                createAndShowTargetBandWidthDialog();
                break;
            case R.id.action_language:
                createAndShowCaptionLanguageDialog();
                break;
            case R.id.action_subtitle:
                NxbInfo info = NxbInfo.getNxbInfo(mNxbInfoList, mCurrentPath, mCurrentIndex);
                mSubtitleChangeDialog.createAndShow(info.getSubtitle(), mCurrentSubtitlePath);
                break;
            case R.id.action_content_info:
                createAndShowContentInfoDialog(mContentInfo);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAndShowDolbyAC3Dialog() {
        if( mDolbyAC3Dialog == null ) {
            mDolbyAC3Dialog = new DolbyAC3Dialog(this, new DolbyAC3Dialog.IDolbyAC3Listener() {
                @Override
                public void onDolbyAC3DialogUpdated(DolbyAC3Dialog.DOLBY_BUTTON button, int position) {
                    NexPlayer player = mVideoView.getNexPlayer();
                    switch ( button ) {
                        case DOLBY_END_POINT:
                            player.setProperties(DolbyAC3Dialog.AC3_PROPERTY_END_POINT, position + 1);
                            break;
                        case DOLBY_ENHANCEMENT_GAIN:
                            player.setProperties(DolbyAC3Dialog.AC3_PROPERTY_ENHANCEMENT_GAIN, position);
                            break;
                        case DOLBY_POST_PROCESSING:
                            player.setProperties(DolbyAC3Dialog.AC3_PROPERTY_POST_PROCESSING, position);
                            break;
                    }
                }
            }, mPrefData.mEnableDolbyAC3PostProcessing,
                    mPrefData.mDolbyAC3EndPoint, mPrefData.mDolbyAC3EnhancementGain);
        }

        mDolbyAC3Dialog.createAndShow();
    }

    private void createAndShowDolbyAC4Dialog() {
        if( mDolbyAC4Dialog == null ) {
            mDolbyAC4Dialog = new DolbyAC4Dialog(this, new DolbyAC4Dialog.IDolbyAC4Listener() {
                @Override
                public void onDolbyAC4DialogUpdated(DolbyAC4Dialog.DOLBY_BUTTON button, int position) {
                    NexPlayer player = mVideoView.getNexPlayer();
                    switch ( button ) {
                        case DOLBY_VIRTUALIZATION:
                            player.setProperties(DolbyAC4Dialog.AC4_PROPERTY_VIRTUALIZATION, position);
                            break;
                        case DOLBY_ENHANCEMENT_GAIN:
                            player.setProperties(DolbyAC4Dialog.AC4_PROPERTY_ENHANCEMENT_GAIN, position);
                            break;
                        case DOLBY_MAIN_ASSO_PREF:
                            player.setProperties(DolbyAC4Dialog.AC4_PROPERTY_MAIN_ASSO_PREF, position);
                            break;
                        case DOLBY_PRESENTATION_INDEX:
                            player.setProperties(DolbyAC4Dialog.AC4_PROPERTY_PRESENTATION_INDEX, position);
                            break;
                    }
                }
            }, mPrefData.mDolbyAC4Virtualization, mPrefData.mDolbyAC4EnhancementGain, mPrefData.mDolbyAC4MainAssoPref, mPrefData.mDolbyAC4PresentationIndex);
        }

        mDolbyAC4Dialog.createAndShow();
    }

    private void createAndShowCaptionLanguageDialog() {
        if( mCaptionLanguageDialog == null ) {
            mCaptionLanguageDialog = new CaptionLanguageDialog(this, new CaptionLanguageDialog.Listener() {
                @Override
                public void onItemClicked(int position, boolean disable) {
                    mVideoView.setCaptionLanguage(position);
                }
            });
        }

        mCaptionLanguageDialog.createAndShowDialog(mContentInfo, mVideoView.getNexPlayer().getCaptionLanguage());
    }

    private void createAndShowTargetBandWidthDialog() {
        if( mTargetBandWidthDialog == null ) {
            mTargetBandWidthDialog = new TargetBandWidthDialog(this, new TargetBandWidthDialog.TargetBandWidthIListener() {
                @Override
                public void onTargetBandWidthDialogUpdated(boolean abrEnabled, int targetBandWidth, NexABRController.SegmentOption segOption, NexABRController.TargetOption targetOption) {
                    NexABRController abrController = mVideoView.getABRController();
                    abrController.setABREnabled(abrEnabled);

                    if( !abrEnabled ) {
                        abrController.setTargetBandWidth(targetBandWidth, segOption, targetOption);
                    }
                }
            }, mPrefData.mEnableAudioOnlyTrack);
        }

        if( !mTargetBandWidthDialog.isShowing() )
            mTargetBandWidthDialog.createAndShow(mContentInfo, (Integer) mVideoView.getProperty(NexPlayer.NexProperty.SUPPORT_ABR) == 1);
    }

    private void createAndShowCaptionSettingDialog() {
        if ( mCaptionSettingsDialog == null ) {
            mCaptionSettingsDialog = new CaptionRenderViewSettingsDialog(this, new CaptionRenderViewSettingsDialog.OnSettingsChangedListener() {
                @Override
                public void onSettingsChanged(NexCaptionAttribute settings) {
                    mCaptionRenderView.setCaptionAttribute(settings);
                }
            });
        }

        if( !mCaptionSettingsDialog.isShowing() ) {
            NexCaptionAttribute settings = mCaptionRenderView.getCaptionAttribute();
            if(null == settings) {
                settings = new NexCaptionAttribute();
            }

            mCaptionSettingsDialog.createAndShow(settings);
        }
    }

    private void createAndShowBandWidthDialog() {
        if( mBandwidthDialog == null )
            mBandwidthDialog = new BandwidthDialog(this);
        mBandwidthDialog.createAndShow(mPrefData.mMinBandWidth, mPrefData.mMaxBandWidth, new BandwidthDialog.IBandwidthListener() {

            @Override
            public void onBandwidthDialogUpdated(int minBW, int maxBW) {
                boolean isChangedMaxBW = mPrefData.mMaxBandWidth != maxBW;
                boolean isChangedMinBW = mPrefData.mMinBandWidth != minBW;
                NexPlayer.NexErrorCode ret = NexPlayer.NexErrorCode.UNKNOWN;
                NexABRController controller = mVideoView.getABRController();
                if( isChangedMaxBW && isChangedMinBW ) {
                    ret = controller.changeMinMaxBandWidth(minBW*BANDWIDTH_KBPS, maxBW*BANDWIDTH_KBPS);
                } else if( isChangedMaxBW ) {
                    ret = controller.changeMaxBandWidth(maxBW*BANDWIDTH_KBPS);
                } else if( isChangedMinBW ) {
                    ret = controller.changeMinBandWidth(minBW*BANDWIDTH_KBPS);
                }
                Log.d(LOG_TAG, "changeMinMaxBandWidth Ret : " + ret);
            }
        });
    }

    private void createAndShowVolumeDialog() {
        if( mVolumeDialog == null )
            mVolumeDialog = new VolumeDialog(this);
        mVolumeDialog.createAndShow(mVolume, new VolumeDialog.IVolumeListener() {
            @Override
            public void onVolumeDialogUpdated(VolumeDialog.VOLUME_BUTTON button, int volume) {
                if( button == VolumeDialog.VOLUME_BUTTON.PROGRESS_CHANGED) {
                    mVolume = volume;
                    mVideoView.getNexPlayer().setVolume(mVolume/10);  // volume range : 0 ~ 1.0
                }
            }
        });
    }

    private View.OnClickListener mPrevButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setupPlayContent(REPLAY_MODE_PREVIOUS);
            mVideoView.stopPlayback();
        }
    };

    private View.OnClickListener mNextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setupPlayContent(REPLAY_MODE_NEXT);
            mVideoView.stopPlayback();
        }
    };

    String getStringByID3TagInfo(NexID3TagText text) {
        String ret = "";
        final String STRING_UTF8 = "UTF-8";
        if( text != null && text.getTextData() != null ) {
            try {
                ret = new String(text.getTextData(), 0, text.getTextData().length, STRING_UTF8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }


    private void createAndShowMultiStreamDialog() {
		if( mMultiStreamDialog == null )
			mMultiStreamDialog = new MultiStreamDialog(this);

        mMultiStreamDialog.createAndShow(mContentInfo, null, new MultiStreamDialog.IMultiStreamListener() {

			@Override
			public void onVideoStreamDialogUpdated(int streamID, int attrID) {
                if ( mCurrentVideoStream != streamID ||
                        NexContentInfoExtractor.getCurCustomAttributeID(mContentInfo, NexPlayer.MEDIA_STREAM_TYPE_VIDEO) != attrID ) {
                    mVideoView.setMediaStream(NexVideoView.STREAM_TYPE_VIDEO, streamID, attrID);
                    mCurrentVideoStream = streamID;

                    mMultiStreamDialog.dismiss();
                }
			}

            @Override
            public void onTextStreamDialogUpdated(int streamID, int channelId, int captionType, int groupPosition, boolean inStream) {

            }

			@Override
			public void onAudioStreamDialogUpdated(int streamID) {
					if( mCurrentAudioStream != streamID ) {
						mVideoView.setMediaStream(NexVideoView.STREAM_TYPE_AUDIO, streamID, NexPlayer.MEDIA_STREAM_DEFAULT_ID);
						mCurrentAudioStream = streamID;

						mMultiStreamDialog.dismiss();
					}
			}
		});
	}

    protected boolean isStreaming() {
        return mLocalFileList == null;
    }

    @Override
    public void onNetworkStateChanged(Context context) {
        if( NetworkUtils.getConnectivityStatus(context) != NetworkUtils.TYPE_NOT_CONNECTED ) {
            NexPlayer player = mVideoView.getNexPlayer();
            if( player != null && player.isInitialized() && isStreaming() ) {
                int state = player.getState();

                if( state == NexPlayer.NEXPLAYER_STATE_PLAY || state == NexPlayer.NEXPLAYER_STATE_PAUSE )
                    player.reconnectNetwork();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if( isTVDevice && keyCode == KeyEvent.KEYCODE_DPAD_DOWN ) {
            mMediaController.show(5000);
        }

        return super.onKeyDown(keyCode, event);
    }
}
