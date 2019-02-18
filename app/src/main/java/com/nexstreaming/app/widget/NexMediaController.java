package com.nexstreaming.app.widget;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

public class NexMediaController extends MediaController {

    private final PlayerControlView mPlayerControlView;

    public NexMediaController(Context context) {
        this(new PlayerControlView(context));
    }

    public NexMediaController(PlayerControlView playerControlView) {
        super(playerControlView.getContext());
        this.mPlayerControlView = playerControlView;
    }

    public PlayerControlView getPlayerControlView() {
        return mPlayerControlView;
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayerControlView.setPlayer(player);
    }

    public void setMarkers(int[] markers) {
        if( mPlayerControlView != null )
            mPlayerControlView.setMarkers(markers);
    }

    @Override
    public void setAnchorView(View view) {
        ViewGroup parentView;
        View rootView = view;
        while (true) {
            if (rootView instanceof ViewGroup) {
                parentView = (ViewGroup) view;
                break;
            }
            rootView = view.getRootView();
        }
        mPlayerControlView.attach(parentView);
    }

    @Override
    public void show() {
        mPlayerControlView.show();
    }

    @Override
    public void show(int timeout) {
        mPlayerControlView.show(timeout);
    }

    @Override
    public boolean isShowing() {
        return mPlayerControlView.isShowing();
    }

    @Override
    public void hide() {
        if (!mPlayerControlView.alwaysShow) {
            mPlayerControlView.hide();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mPlayerControlView.onTouchEvent(event);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        return mPlayerControlView.onTrackballEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mPlayerControlView.dispatchKeyEvent(event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mPlayerControlView.setEnabled(enabled);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return mPlayerControlView.getAccessibilityClassName();
    }

    @Override
    public void setPrevNextListeners(View.OnClickListener next, View.OnClickListener prev) {
        mPlayerControlView.setNextListener(next);
        mPlayerControlView.setPrevListener(prev);
    }
}
