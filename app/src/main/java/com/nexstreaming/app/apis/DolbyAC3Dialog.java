package com.nexstreaming.app.apis;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.R.layout;
import app.nunc.com.staatsoperlivestreaming.R.id;
import app.nunc.com.staatsoperlivestreaming.R.string;
import app.nunc.com.staatsoperlivestreaming.R.array;

public class DolbyAC3Dialog {
	
	private Activity mActivity = null;
	private IDolbyAC3Listener	mListener = null;
	private AlertDialog mDolbyAC3Dialog = null;

	private int mDolbyAC3PostProcessing = 0;
    private int mDolbyAC3EndPoint = 0;
    private int mDolbyAC3EnhancementGain = 0;

	public final static int AC3_PROPERTY_END_POINT = 2002;
	public final static int AC3_PROPERTY_POST_PROCESSING = 2003;
    public final static int AC3_PROPERTY_ENHANCEMENT_GAIN = 2004;
	public final static int AC3_PROPERTY_DECODER_TYPE = 2005;

	private Spinner mDolbyAC3PostProcessingSpinner = null;
	private Spinner mDolbyAC3EndpointSpinner = null;
	private SeekBar mDolbyAC3EnhancementGainSeekbar = null;
	private TextView mDolbyAC3EnhancementGainTextView = null;

	public enum DOLBY_BUTTON {
		DOLBY_POST_PROCESSING,
		DOLBY_END_POINT,
		DOLBY_ENHANCEMENT_GAIN
	}

	public interface IDolbyAC3Listener {
		void onDolbyAC3DialogUpdated(DOLBY_BUTTON button, int value);
	}
	
	public DolbyAC3Dialog(Activity activity, IDolbyAC3Listener listener, int postProcessing, int endPoint, int enhancementGain) {
		mActivity = activity;
		mListener = listener;
		mDolbyAC3PostProcessing = postProcessing;
		mDolbyAC3EndPoint = endPoint;
		mDolbyAC3EnhancementGain = enhancementGain;
	}

	private AlertDialog createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(R.string.dolby_ac3_set_up);
		View view = View.inflate(mActivity, R.layout.dolby_ac3_dialog_layout, null);
		setupUIComponents(view);
		AlertDialog dialog = builder.setView(view).create();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		dialog.getWindow().setAttributes(params);

		return dialog;
	}

	public void createAndShow() {
		if( mDolbyAC3Dialog == null ) {
			mDolbyAC3Dialog = createDialog();
		}

		if( !mDolbyAC3Dialog.isShowing() ) {
			updateUIComponents();
			mDolbyAC3Dialog.show();
		}
	}

	private void updateUIComponents() {
		mDolbyAC3PostProcessingSpinner.setSelection(mDolbyAC3PostProcessing);
		mDolbyAC3EndpointSpinner.setSelection(mDolbyAC3EndPoint);
		mDolbyAC3EnhancementGain = mDolbyAC3EnhancementGainSeekbar.getProgress();
	}

	private void resetUIComponents() {
		mDolbyAC3PostProcessing = 0;
		mDolbyAC3EndPoint = 0;
		mDolbyAC3EnhancementGain = 0;
		mDolbyAC3PostProcessingSpinner.setSelection(mDolbyAC3PostProcessing);
		mDolbyAC3EndpointSpinner.setSelection(mDolbyAC3EndPoint);
		mDolbyAC3EnhancementGainSeekbar.setProgress(mDolbyAC3EnhancementGain);
		mDolbyAC3EnhancementGainTextView.setText(String.valueOf(mDolbyAC3EnhancementGain));
	}

	private void setupUIComponents(View view) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
				layout.simple_spinner_item, id.textview, mActivity.getResources().getStringArray(R.array.pref_dolby_ac3_post_processing_entries));
		mDolbyAC3PostProcessingSpinner = (Spinner)view.findViewById(id.dolby_ac3_post_processing_spinner);
		mDolbyAC3PostProcessingSpinner.setAdapter(adapter);
		mDolbyAC3PostProcessingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if( mListener != null ) {
					mListener.onDolbyAC3DialogUpdated(DOLBY_BUTTON.DOLBY_POST_PROCESSING, position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});


		adapter = new ArrayAdapter<String>(mActivity,
				layout.simple_spinner_item, id.textview, mActivity.getResources().getStringArray(R.array.pref_dolby_ac3_end_point_entries));
		mDolbyAC3EndpointSpinner = (Spinner)view.findViewById(id.dolby_ac3_end_point_spinner);
		mDolbyAC3EndpointSpinner.setAdapter(adapter);
		mDolbyAC3EndpointSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if( mListener != null ) {
					mListener.onDolbyAC3DialogUpdated(DOLBY_BUTTON.DOLBY_END_POINT, position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		mDolbyAC3EnhancementGainTextView = (TextView)view.findViewById(id.dolby_ac3_enhancement_gain_value);
		mDolbyAC3EnhancementGainSeekbar = (SeekBar)view.findViewById(id.dolby_ac3_enhancement_gain_seekbar);
		mDolbyAC3EnhancementGainSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mDolbyAC3EnhancementGainTextView.setText(String.valueOf(progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mDolbyAC3EnhancementGain = seekBar.getProgress();
//				if (mListener != null) {
//					mListener.onDolbyAC3DialogUpdated(DOLBY_BUTTON.DOLBY_ENHANCEMENT_GAIN, mDolbyAC3EnhancementGain);
//				}
			}
		});

		Button resetButton = (Button)view.findViewById(id.dolby_ac3_reset_button);
		resetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if( mListener != null ) {
					resetUIComponents();
					mListener.onDolbyAC3DialogUpdated(DOLBY_BUTTON.DOLBY_POST_PROCESSING, mDolbyAC3PostProcessing);
					mListener.onDolbyAC3DialogUpdated(DOLBY_BUTTON.DOLBY_END_POINT, mDolbyAC3EndPoint);
					mListener.onDolbyAC3DialogUpdated(DOLBY_BUTTON.DOLBY_ENHANCEMENT_GAIN, mDolbyAC3EnhancementGain);
				}
			}
		});

		Button saveButton = (Button)view.findViewById(id.dolby_ac3_save_button);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDolbyAC3PostProcessing = mDolbyAC3PostProcessingSpinner.getSelectedItemPosition();
				mDolbyAC3EndPoint = mDolbyAC3EndpointSpinner.getSelectedItemPosition();
				mDolbyAC3EnhancementGain = mDolbyAC3EnhancementGainSeekbar.getProgress();
				if( mListener != null ) {
					mListener.onDolbyAC3DialogUpdated(DOLBY_BUTTON.DOLBY_POST_PROCESSING, mDolbyAC3PostProcessing);
					mListener.onDolbyAC3DialogUpdated(DOLBY_BUTTON.DOLBY_END_POINT, mDolbyAC3EndPoint);
					mListener.onDolbyAC3DialogUpdated(DOLBY_BUTTON.DOLBY_ENHANCEMENT_GAIN, mDolbyAC3EnhancementGain);
				}
				dismiss();
			}
		});
	}

	public void dismiss() {
		if( (mDolbyAC3Dialog!=null) && 
				(mDolbyAC3Dialog.isShowing() == true) ) {
			mDolbyAC3Dialog.dismiss();
		}
	}
}
