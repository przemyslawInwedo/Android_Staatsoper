package com.nexstreaming.app.apis;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;

import app.nunc.com.staatsoperlivestreaming.R;

public class CheckBoxDialogPreference extends DialogPreference {
	private SharedPreferences mPref;
	
	private CheckBox mDebugCheck;
	private CheckBox mRTPCheck;
	private CheckBox mRTCPCheck;
	private CheckBox mFrameCheck;
	
	public CheckBoxDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		setDialogLayoutResource(R.layout.dialog_preference_layout);
		
		init(context);
	}
	
	private void init(Context context) {
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
	
	}

	@Override
	protected void onBindDialogView(View view) {
		mDebugCheck = (CheckBox)view.findViewById(R.id.debug_checkbox);
		mRTPCheck = (CheckBox)view.findViewById(R.id.rtp_checkbox);
		mRTCPCheck = (CheckBox)view.findViewById(R.id.rtcp_checkbox);
		mFrameCheck = (CheckBox)view.findViewById(R.id.frame_checkbox);

		setCheckBox();
		
		super.onBindDialogView(view);
	}
	
	private void setCheckBox() {
		if(mPref.getBoolean(getContext().getString(R.string.pref_debug_protocol_logs_debug_key), true)) {
			mDebugCheck.setChecked(true);
		} else {
			mDebugCheck.setChecked(false);
		}
		
		if(mPref.getBoolean(getContext().getString(R.string.pref_debug_protocol_logs_rtp_key), false)) {
			mRTPCheck.setChecked(true);
		} else {
			mRTPCheck.setChecked(false);
		}
		
		if(mPref.getBoolean(getContext().getString(R.string.pref_debug_protocol_logs_rtcp_key), false)) {
			mRTCPCheck.setChecked(true);
		} else {
			mRTCPCheck.setChecked(false);
		}
		
		if(mPref.getBoolean(getContext().getString(R.string.pref_debug_protocol_logs_frame_key), false)) {
			mFrameCheck.setChecked(true);
		} else {
			mFrameCheck.setChecked(false);
		}
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		
		if(!positiveResult)
			return;
		
		SharedPreferences.Editor editor = mPref.edit();
		
		editor.putBoolean
			(getContext().getString(R.string.pref_debug_protocol_logs_debug_key), mDebugCheck.isChecked());
		editor.putBoolean
			(getContext().getString(R.string.pref_debug_protocol_logs_rtp_key), mRTPCheck.isChecked());
		editor.putBoolean
			(getContext().getString(R.string.pref_debug_protocol_logs_rtcp_key), mRTCPCheck.isChecked());
		editor.putBoolean
			(getContext().getString(R.string.pref_debug_protocol_logs_frame_key), mFrameCheck.isChecked());
		
		editor.commit();

		super.onDialogClosed(positiveResult);
	}

}
