package com.nexstreaming.app.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import app.nunc.com.staatsoperlivestreaming.R;
import com.nexstreaming.app.util.Util;

public class PlayerControlView extends FrameLayout {

    public static final int DEFAULT_FAST_REWIND_MS = 5_000;
    public static final int DEFAULT_FAST_FORWARD_MS = 15_000;
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 3_000;

    private final ViewHolder viewHolder;

    boolean alwaysShow;

    private MediaController.MediaPlayerControl player;
    private boolean showing;
    private boolean dragging;
    private int fastRewindMs;
    private int fastForwardMs;
    private int showTimeoutMs;

    private OnClickListener nextListener, prevListener;
    private OnVisibilityChangedListener onVisibilityChangedListener;

    private Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            int pos = updateProgress();
            if (!dragging && showing && player != null && player.isPlaying()) {
                postDelayed(updateProgressRunnable, 1000 - (pos % 1000));
            }
        }
    };
    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public PlayerControlView(Context context) {
        this(context, null);
    }

    public PlayerControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.media_controller, this);
        viewHolder = new ViewHolder(this);
        fastRewindMs = DEFAULT_FAST_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;
        showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS;

        if (isInEditMode()) {
            return;
        }

        ComponentListener componentListener = new ComponentListener();
        viewHolder.pausePlayButton.setOnClickListener(componentListener);
        viewHolder.fastForwardButton.setOnClickListener(componentListener);
        viewHolder.fastRewindButton.setOnClickListener(componentListener);
        viewHolder.skipPrevButton.setOnClickListener(componentListener);
        viewHolder.skipNextButton.setOnClickListener(componentListener);
        viewHolder.seekBar.setOnSeekBarChangeListener(componentListener);

        viewHolder.skipNextButton.setVisibility(View.INVISIBLE);
        viewHolder.skipPrevButton.setVisibility(View.INVISIBLE);

        hide();
    }

    public void setPlayer(MediaController.MediaPlayerControl player) {
        this.player = player;
        updatePausePlayImage();
    }

    public void attach(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);
        attach(rootView);
    }

    public void setMarkers(int[] markers) {
        if( viewHolder.seekBar != null )
            viewHolder.seekBar.setMarkers(markers);
    }

    public void attach(ViewGroup rootView) {
        rootView.removeView(this);
        if (rootView instanceof RelativeLayout) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rootView.addView(this, layoutParams);
        } else {
            LayoutParams layoutParams = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
            );
            rootView.addView(this, layoutParams);
        }
    }

    public void show() {
        show(showTimeoutMs);
    }

    void show(int showTimeoutMs) {
        showing = true;
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onShown(this);
        }
        setVisibility(VISIBLE);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        requestFocus();

        updateProgress();
        viewHolder.pausePlayButton.requestFocus();
        disableUnsupportedButtons();
        updatePausePlayImage();

        removeCallbacks(updateProgressRunnable);
        post(updateProgressRunnable);

        removeCallbacks(hideRunnable);
        if (!alwaysShow) {
            postDelayed(hideRunnable, showTimeoutMs);
        }
    }

    public boolean isShowing() {
        return showing;
    }

    public void hide() {
        showing = false;
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onHidden(this);
        }
        removeCallbacks(hideRunnable);
        removeCallbacks(updateProgressRunnable);
        setVisibility(GONE);
    }

    public void toggleVisibility() {
        if (showing) {
            hide();
        } else {
            show();
        }
    }

    private int updateProgress() {
        if (dragging || player == null) {
            return 0;
        }
        updatePausePlayImage();
        int currentTime = player.getCurrentPosition();
        int totalTime = player.getDuration();
        if (totalTime > 0) {
            viewHolder.seekBar.setMax(totalTime);
            viewHolder.seekBar.setProgress(currentTime);
        }
        int percent = player.getBufferPercentage();
        viewHolder.seekBar.setSecondaryProgress(percent * 10);

        viewHolder.currentTimeText.setText(Util.formatTime(currentTime));
        viewHolder.totalTimeText.setText(Util.formatTime(totalTime));
        return currentTime;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (player == null) {
            return super.dispatchKeyEvent(event);
        }
        final boolean uniqueDown = event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN;

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_SPACE:
                if (uniqueDown) {
                    doPauseResume();
                    show();
                    viewHolder.pausePlayButton.requestFocus();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (uniqueDown && !player.isPlaying()) {
                    player.start();
                    show();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_STOP:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (uniqueDown && player.isPlaying()) {
                    player.pause();
                    show();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_MUTE:
            case KeyEvent.KEYCODE_CAMERA:
            case KeyEvent.KEYCODE_MENU:
                return super.dispatchKeyEvent(event);
            case KeyEvent.KEYCODE_BACK:
                if (alwaysShow) {
                    return super.dispatchKeyEvent(event);
                }
                if (uniqueDown) {
                    hide();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                if (player.canSeekForward()) {
                    player.seekTo(fastForwardMs);
                    show();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                if (player.canSeekForward()) {
                    player.seekTo(fastRewindMs);
                    show();
                }
                return true;
            default:
                break;
        }

        show();
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(updateProgressRunnable);
        removeCallbacks(hideRunnable);
    }

    private void updatePausePlayImage() {
        if (player == null) {
            return;
        }
        viewHolder.pausePlayButton.setImageResource(player.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    private void doPauseResume() {
        if (player == null) {
            return;
        }
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
        updatePausePlayImage();
    }

    @Override
    public void setEnabled(boolean enabled) {
        viewHolder.pausePlayButton.setEnabled(enabled);
        viewHolder.fastForwardButton.setEnabled(enabled);
        viewHolder.fastRewindButton.setEnabled(enabled);
        viewHolder.skipNextButton.setEnabled(enabled && nextListener != null);
        viewHolder.skipPrevButton.setEnabled(enabled && prevListener != null);
        viewHolder.seekBar.setEnabled(enabled);
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    private void disableUnsupportedButtons() {
        if (player == null) {
            return;
        }
        if (!player.canPause()) {
            viewHolder.pausePlayButton.setEnabled(false);
        }
        if (!player.canSeekBackward()) {
            viewHolder.fastRewindButton.setEnabled(false);
        }
        if (!player.canSeekForward()) {
            viewHolder.fastForwardButton.setEnabled(false);
        }
        if (!player.canSeekBackward() && !player.canSeekForward()) {
            viewHolder.seekBar.setEnabled(false);
        }
    }

    public void setPrevListener(@Nullable OnClickListener prevListener) {
        this.prevListener = prevListener;
        viewHolder.skipPrevButton.setVisibility(prevListener == null ? View.INVISIBLE : View.VISIBLE);
    }

    public void setNextListener(@Nullable OnClickListener nextListener) {
        this.nextListener = nextListener;
        MediaController a;
        viewHolder.skipNextButton.setVisibility(nextListener == null ? View.INVISIBLE : View.VISIBLE);
    }

    public void setFastRewindMs(int fastRewindMs) {
        this.fastRewindMs = fastRewindMs;
    }

    public void setFastForwardMs(int fastForwardMs) {
        this.fastForwardMs = fastForwardMs;
    }

    public void setShowTimeoutMs(int showTimeoutMs) {
        this.showTimeoutMs = showTimeoutMs;
    }

    public void setAlwaysShow(boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
        if (alwaysShow) {
            removeCallbacks(hideRunnable);
        }
    }

    public void setOnVisibilityChangedListener(OnVisibilityChangedListener listener) {
        this.onVisibilityChangedListener = listener;
    }

    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    public MediaController getMediaController() {
        return new NexMediaController(this);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return PlayerControlView.class.getName();
    }

    public interface OnVisibilityChangedListener {
        void onShown(PlayerControlView view);

        void onHidden(PlayerControlView view);
    }

    public static final class ViewHolder {

        public final NexSeekBar seekBar;
        public final TextView totalTimeText;
        public final TextView currentTimeText;
        public final ImageButton pausePlayButton;
        public final ImageButton fastForwardButton;
        public final ImageButton fastRewindButton;
        public final ImageButton skipNextButton;
        public final ImageButton skipPrevButton;

        private ViewHolder(View view) {
            pausePlayButton = (ImageButton) view.findViewById(R.id.pause);
            fastForwardButton = (ImageButton) view.findViewById(R.id.ffwd);
            fastRewindButton = (ImageButton) view.findViewById(R.id.rew);
            skipNextButton = (ImageButton) view.findViewById(R.id.next);
            skipPrevButton = (ImageButton) view.findViewById(R.id.prev);
            seekBar = (NexSeekBar) view.findViewById(R.id.mediacontroller_progress);
            totalTimeText = (TextView) view.findViewById(R.id.time);
            currentTimeText = (TextView) view.findViewById(R.id.time_current);
        }
    }

    private final class ComponentListener implements SeekBar.OnSeekBarChangeListener, OnClickListener {

        @Override
        public void onClick(View v) {
            if (player == null) {
                return;
            }
            if (v == viewHolder.pausePlayButton) {
                doPauseResume();
            } else if (v == viewHolder.fastRewindButton) {
                int position = player.getCurrentPosition();
                position -= fastRewindMs;
                player.seekTo(position);
                updateProgress();
            } else if (v == viewHolder.fastForwardButton) {
                int position = player.getCurrentPosition();
                position += fastForwardMs;
                player.seekTo(position);
                updateProgress();
            } else if (v == viewHolder.skipPrevButton) {
                if (prevListener != null) {
                    prevListener.onClick(v);
                }
            } else if (v == viewHolder.skipNextButton) {
                if (nextListener != null) {
                    nextListener.onClick(v);
                }
            }
            show();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser || player == null) {
                return;
            }
            player.seekTo(progress);
            viewHolder.currentTimeText.setText(Util.formatTime(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            show();
            dragging = true;
            removeCallbacks(hideRunnable);
            removeCallbacks(updateProgressRunnable);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            dragging = false;
            show();
            post(updateProgressRunnable);
        }
    }
}