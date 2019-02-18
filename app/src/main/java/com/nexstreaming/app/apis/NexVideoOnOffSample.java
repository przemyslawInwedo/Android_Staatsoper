package com.nexstreaming.app.apis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import app.nunc.com.staatsoperlivestreaming.R;
import com.nexstreaming.app.util.NexFileIO;
import com.nexstreaming.nexplayerengine.NexPlayer;
import com.nexstreaming.nexplayerengine.NexVideoView;

public class NexVideoOnOffSample extends NexVideoViewSample {
    private final static String LOG_TAG = "VideoOnOffSample";
    private static final Handler mHandler = new Handler();

    private CheckBox mVideoCheck = null;
    private CheckBox mAudioCheck = null;
    private CheckBox mTextCheck = null;
    private boolean mBackPressed = false;
    private boolean mServiceConnected = false;
    private BroadcastReceiver mBroadcastReceiver = null;
    private int mStartPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupBroadcastReceiver();
    }

    @Override
    protected void onPlayerStart(NexPlayer mp) {
        super.onPlayerStart(mp);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setMediaStreamCheckBoxEnabled(true);
                if( mServiceConnected )
                    updateNotification(true);
            }
        });
    }

    private void setMediaStreamCheckBoxEnabled(boolean enabled) {
        setMediaStreamCheckBoxEnabled(enabled, enabled, enabled);
    }

    private void setMediaStreamCheckBoxEnabled(boolean videoEnabled, boolean audioEnabled, boolean textEnabled) {
        mVideoCheck.setChecked(videoEnabled);
        mAudioCheck.setChecked(audioEnabled);
        mTextCheck.setChecked(textEnabled);
    }

    private void setupBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        filter.addAction(VideoOnOffPlayerService.INTENT_ACTION_NEXT);
        filter.addAction(VideoOnOffPlayerService.INTENT_ACTION_PLAY_PAUSE);
        filter.addAction(VideoOnOffPlayerService.INTENT_ACTION_PREV);

        mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {

                    Log.d(LOG_TAG, "(BroadcastReceiver) -------------------------------------------->  AUDIO_BECOMING_NOISY");
                    if( mVideoView.isPlaying() ) {
                        mVideoView.pause();

                        if( mServiceConnected )
                            updateNotification(false);
                    }
                } else if(intent.getAction().equals(VideoOnOffPlayerService.INTENT_ACTION_PREV)) {
                    Log.d(LOG_TAG, "(BroadcastReceiver) -------------------------------------------->  INTENT_ACTION_PREV");
                    setupPlayContent(REPLAY_MODE_PREVIOUS);
                    mVideoView.stopPlayback();
                    setVisibility(View.INVISIBLE);
                    updateNotification(false);
                } else if(intent.getAction().equals(VideoOnOffPlayerService.INTENT_ACTION_PLAY_PAUSE)) {
                    Log.d(LOG_TAG, "(BroadcastReceiver) -------------------------------------------->  INTENT_ACTION_PLAY_PAUSE");
                    boolean isPlaying = mVideoView.isPlaying();
                    if( isPlaying ) {
                        mVideoView.pause();
                    } else {
                        mVideoView.resume();
                    }
                    updateNotification(!isPlaying);
                } else if(intent.getAction().equals(VideoOnOffPlayerService.INTENT_ACTION_NEXT)) {
                    Log.d(LOG_TAG, "(BroadcastReceiver) -------------------------------------------->  INTENT_ACTION_NEXT");
                    setupPlayContent(REPLAY_MODE_NEXT);
                    mVideoView.stopPlayback();
                    setVisibility(View.INVISIBLE);
                    updateNotification(false);
                }
            }
        };

        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onActivityPause() {
        if( !mBackPressed &&
                ( mCurrentPath != null || mCurrentUri != null ) ) {
            backgroundPlay();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        mForeground = true;
        onActivityResume();
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mForeground = false;
        Log.d(LOG_TAG, "onPause");
        onActivityPause();

    }


    @Override
    protected void onActivityResume() {
        foregroundPlay();
    }

    @Override
    protected void onStreamChanged(NexPlayer mp, int result, final int streamType, final int streamID) {
        Log.d(LOG_TAG, "onStreamChanged result : " + result + " streamType : " + streamType + " streamID : " + streamID  );
        if(hasWindowFocus()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    changeMediaCheckBoxStatus(streamType, streamID != NexPlayer.MEDIA_STREAM_DISABLE_ID);
                }
            });
        }
    }

	@Override
    protected void onPlayerPrepared(NexPlayer mp) {
        if( (!mForeground) || !mVideoCheck.isChecked() )
            turnVideo(false);
        if( !mAudioCheck.isChecked() )
            turnAudio(false);
        if( !mTextCheck.isChecked())
            turnText(false);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG, "onPrepared");
                setVisibility(View.INVISIBLE);
                int startPosition = mPrefData.mStartSec * 1000;
                if (mNeedToStart) {
                    startPosition = mStartPosition;
                    mNeedToStart = false;
                }
                mVideoView.start(startPosition);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(LOG_TAG, "onKeyDown KEYCODE_BACK");
            mBackPressed = true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void inflateTitleBar() {
        ViewStub stub = (ViewStub) findViewById(R.id.title_view_stub);
        stub.setLayoutResource(R.layout.title_bar_video_on_off);
        stub.inflate();
    }

    @Override
    protected void setupTitleBar() {
        super.setupTitleBar();

        setupVideoCheckBox();
        setupAudioCheckBox();
        setupTextCheckBox();
    }

    @Override
    protected void resetPlayerStatus() {
        super.resetPlayerStatus();

        setMediaStreamCheckBoxEnabled(false);
    }

    private void setupVideoCheckBox() {
        mVideoCheck = (CheckBox)findViewById(R.id.videoCheck);
        mVideoCheck.setEnabled(true);
        mVideoCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "mVideoCheck onClick");
                if ((mContentInfo != null) ) {
                    if (turnVideo(mVideoCheck.isChecked()) == 0) {
                        mVideoCheck.setChecked(!mVideoCheck.isChecked());
                        String text = getString((!mVideoCheck.isChecked() ? R.string.menu_video_on : R.string.menu_video_off));
                        mVideoCheck.setText(text);
                    }
                }
            }
        });
        mVideoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mAudioCheck != null){
                    mAudioCheck.setEnabled(mVideoCheck.isChecked());
                }
            }
        });

    }

    private void setupAudioCheckBox() {
        mAudioCheck = (CheckBox)findViewById(R.id.audioCheck);
        mAudioCheck.setEnabled(true);
        mAudioCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "mAudioCheck onClick");
                if ((mContentInfo != null) ) {
                    if (turnAudio(mAudioCheck.isChecked()) == 0) {
                        mAudioCheck.setChecked(!mAudioCheck.isChecked());
                        String text = getString((!mAudioCheck.isChecked() ? R.string.menu_audio_on : R.string.menu_audio_off));
                        mAudioCheck.setText(text);
                    }
                }
            }
        });
        mAudioCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mVideoCheck != null){
                    mVideoCheck.setEnabled(mAudioCheck.isChecked());
                }
            }
        });
    }

    private void setupTextCheckBox() {
        mTextCheck = (CheckBox)findViewById(R.id.textCheck);
        mTextCheck.setEnabled(true);
        mTextCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "mTextCheck onClick");
                if (mContentInfo != null) {
                    if (turnText(mTextCheck.isChecked()) == 0) {
                        mTextCheck.setChecked(!mTextCheck.isChecked());
                    }
                }
            }
        });
    }

    private void changeMediaCheckBoxStatus(int mediaType, boolean enable) {
        Log.d(LOG_TAG, "changeMediaCheckBoxStatus mediaType : " + mediaType + " enable : " + enable);
        if( mediaType == NexVideoView.STREAM_TYPE_AUDIO ) {
            mAudioCheck.setChecked(enable);

            String text = getString((enable ? R.string.menu_audio_on : R.string.menu_audio_off));
            mAudioCheck.setText(text);
        }
        else if( mediaType == NexVideoView.STREAM_TYPE_VIDEO ) {
            mVideoCheck.setChecked(enable);

            String text = getString((enable ? R.string.menu_video_on : R.string.menu_video_off));
            mVideoCheck.setText(text);
        }
        else if( mediaType == NexVideoView.STREAM_TYPE_TEXT ) {
            mTextCheck.setChecked(enable);

            String text = getString((enable ? R.string.menu_text_on : R.string.menu_text_off));
            mTextCheck.setText(text);
        }
    }

    private int turnText( boolean enable ) {
        int ret = -1;
        if( mVideoView != null) {
                ret = mVideoView.setMediaStream(NexVideoView.STREAM_TYPE_TEXT,
                        enable ? NexPlayer.MEDIA_STREAM_DEFAULT_ID : NexPlayer.MEDIA_STREAM_DISABLE_ID, NexPlayer.MEDIA_STREAM_DEFAULT_ID);
        }
        return ret;
    }

    private int turnAudio( boolean enable ) {
        int ret = -1;
        if( mVideoView != null) {
            ret = mVideoView.setMediaStream(NexVideoView.STREAM_TYPE_AUDIO,
                    enable ? NexPlayer.MEDIA_STREAM_DEFAULT_ID : NexPlayer.MEDIA_STREAM_DISABLE_ID, NexPlayer.MEDIA_STREAM_DEFAULT_ID);
        }
        return ret;
    }

    private int turnVideo( boolean enable ) {
        int ret = -1;
        if( mVideoView != null) {
            ret = mVideoView.setMediaStream(NexVideoView.STREAM_TYPE_VIDEO,
                    enable ? NexPlayer.MEDIA_STREAM_DEFAULT_ID : NexPlayer.MEDIA_STREAM_DISABLE_ID, NexPlayer.MEDIA_STREAM_DEFAULT_ID);
        }
        return ret;
    }

    private void updateNotification(boolean isPlaying) {
        Log.d(LOG_TAG, "updateNotification! isPlaying : " + isPlaying);
        Intent intent = new Intent(VideoOnOffPlayerService.INTENT_ACTION_START);
        intent.setPackage(this.getPackageName());
        intent.putExtra("title", NexFileIO.getContentTitle(getCurrentPath()));
        intent.putExtra("isPlay", isPlaying);

        startService(intent);
    }

    /**
     * \bref This funtion is  to operate the  backgroundPlay
     *
     *
     */
    private void backgroundPlay() {
        if( mVideoView != null && mContentInfo != null ) {
//            turnVideo(mVideoCheck.isChecked());
//            turnAudio(mAudioCheck.isChecked());
//            turnText(mTextCheck.isChecked());

        }
        startService();
    }

    private void foregroundPlay() {
        if(mServiceConnected){
//            turnVideo(mVideoCheck.isChecked());
//            turnAudio(mAudioCheck.isChecked());
//            turnText(mTextCheck.isChecked());
        }

        stopService();
    }

    private void startService() {
        Log.d(LOG_TAG, "startService! mVideoView.isPlaying() : " + mVideoView.isPlaying());

        Intent intent = new Intent(VideoOnOffPlayerService.INTENT_ACTION_START);
        intent.setPackage(this.getPackageName());
        intent.putExtra("title", NexFileIO.getContentTitle(getCurrentPath()));
        intent.putExtra("isPlay", mVideoView.isPlaying());
        startService(intent);

        mServiceConnected = true;
    }

    private void stopService() {
        if( mServiceConnected ) {
            Log.d(LOG_TAG, "stopService!");
            Intent intent = new Intent(VideoOnOffPlayerService.INTENT_ACTION_START);
            intent.setPackage(this.getPackageName());
            stopService(intent);


            mServiceConnected = false;
        }
    }
}
