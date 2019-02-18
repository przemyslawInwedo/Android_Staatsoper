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
import app.nunc.com.staatsoperlivestreaming.R.id;
import app.nunc.com.staatsoperlivestreaming.R.layout;

public class DolbyAC4Dialog {
	
	private Activity mActivity = null;
	private IDolbyAC4Listener	mListener = null;
	private AlertDialog mDolbyAC4Dialog = null;

	private int mDolbyAC4Virtualization = 0;
    private int mDolbyAC4EnhancementGain = 0;
    private int mDolbyAC4MainAssoPref = 0;
    private int mDolbyAC4PresentationIndex = 0;

	public final static int AC4_PROPERTY_VIRTUALIZATION = 2101;
	public final static int AC4_PROPERTY_ENHANCEMENT_GAIN = 2102;
    public final static int AC4_PROPERTY_MAIN_ASSO_PREF = 2103;
	public final static int AC4_PROPERTY_PRESENTATION_INDEX = 2104;

	private Spinner mDolbyAC4VirtualizationSpinner = null;
	private SeekBar mDolbyAC4EnhancementGainSeekbar = null;
	private TextView mDolbyAC4EnhancementGainTextView = null;
	private SeekBar mDolbyAC4MainAssoPrefSeekbar = null;
	private TextView mDolbyAC4MainAssoPrefTextView = null;
	private SeekBar mDolbyAC4PresentationIndexSeekbar = null;
	private TextView mDolbyAC4PresentationIndexTextView = null;

	public enum DOLBY_BUTTON {
		DOLBY_VIRTUALIZATION,
		DOLBY_ENHANCEMENT_GAIN,
		DOLBY_MAIN_ASSO_PREF,
		DOLBY_PRESENTATION_INDEX
	}

	public interface IDolbyAC4Listener {
		void onDolbyAC4DialogUpdated(DOLBY_BUTTON button, int value);
	}
	
	public DolbyAC4Dialog(Activity activity, IDolbyAC4Listener listener, int virtualization, int enhancementGain, int mainAssoPref, int presentationIndex) {
		mActivity = activity;
		mListener = listener;
		mDolbyAC4Virtualization = virtualization;
    	mDolbyAC4EnhancementGain = enhancementGain;
    	mDolbyAC4MainAssoPref = mainAssoPref;
    	mDolbyAC4PresentationIndex = presentationIndex;
	}

	private AlertDialog createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(R.string.dolby_ac4_set_up);
		View view = View.inflate(mActivity, R.layout.dolby_ac4_dialog_layout, null);
		setupUIComponents(view);
		AlertDialog dialog = builder.setView(view).create();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		dialog.getWindow().setAttributes(params);

		return dialog;
	}

	public void createAndShow() {
		if( mDolbyAC4Dialog == null ) {
			mDolbyAC4Dialog = createDialog();
		}

		if( !mDolbyAC4Dialog.isShowing() ) {
			updateUIComponents();
			mDolbyAC4Dialog.show();
		}
	}

	private void updateUIComponents() {
		mDolbyAC4EnhancementGain = 0;
		mDolbyAC4MainAssoPref = 0;
		mDolbyAC4PresentationIndex = 0;
		mDolbyAC4Virtualization = 0;
	}

	private void resetUIComponents() {
		mDolbyAC4Virtualization = 0;
		mDolbyAC4EnhancementGain = 0;
		mDolbyAC4MainAssoPref = 0;
		mDolbyAC4PresentationIndex = 0;
		mDolbyAC4VirtualizationSpinner.setSelection(mDolbyAC4Virtualization);
		mDolbyAC4EnhancementGainSeekbar.setProgress(mDolbyAC4EnhancementGain);
		mDolbyAC4EnhancementGainTextView.setText(String.valueOf(mDolbyAC4EnhancementGain));
		mDolbyAC4MainAssoPrefSeekbar.setProgress(mDolbyAC4MainAssoPref+32);
		mDolbyAC4MainAssoPrefTextView.setText(String.valueOf(mDolbyAC4MainAssoPref));
		mDolbyAC4PresentationIndexSeekbar.setProgress(mDolbyAC4PresentationIndex);
		mDolbyAC4PresentationIndexTextView.setText(mDolbyAC4PresentationIndex == 0 ? "Default Value" : String.valueOf(mDolbyAC4PresentationIndex-1));
	}

	private void setupUIComponents(View view) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
				R.layout.simple_spinner_item, id.textview, mActivity.getResources().getStringArray(R.array.pref_dolby_ac4_virtualization_entries));
		mDolbyAC4VirtualizationSpinner = (Spinner)view.findViewById(id.dolby_ac4_virtualization_spinner);
		mDolbyAC4VirtualizationSpinner.setAdapter(adapter);
		mDolbyAC4VirtualizationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if( mListener != null ) {
					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_VIRTUALIZATION, position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		mDolbyAC4EnhancementGainTextView = (TextView)view.findViewById(id.dolby_ac4_enhancement_gain_value);
		mDolbyAC4EnhancementGainSeekbar = (SeekBar)view.findViewById(id.dolby_ac4_enhancement_gain_seekbar);
		mDolbyAC4EnhancementGainSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mDolbyAC4EnhancementGainTextView.setText(String.valueOf(progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mDolbyAC4EnhancementGain = seekBar.getProgress();
//				if (mListener != null) {
//					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_ENHANCEMENT_GAIN, mDolbyAC4EnhancementGain);
//				}
			}
		});

		mDolbyAC4MainAssoPrefTextView = (TextView)view.findViewById(id.dolby_ac4_main_asso_pref_value);
		mDolbyAC4MainAssoPrefSeekbar = (SeekBar)view.findViewById(id.dolby_ac4_main_asso_pref_seekbar);
		mDolbyAC4MainAssoPrefSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mDolbyAC4MainAssoPrefTextView.setText(String.valueOf(progress-32));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mDolbyAC4MainAssoPref = seekBar.getProgress() - 32;
//				if (mListener != null) {
//					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_MAIN_ASSO_PREF, mDolbyAC4MainAssoPref);
//				}
			}
		});

		mDolbyAC4PresentationIndexTextView = (TextView)view.findViewById(id.dolby_ac4_presentation_index_value);
		mDolbyAC4PresentationIndexSeekbar = (SeekBar)view.findViewById(id.dolby_ac4_presentation_index_seekbar);
		mDolbyAC4PresentationIndexSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mDolbyAC4PresentationIndexTextView.setText(progress == 0 ? "Default Value" : String.valueOf(progress-1));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progressValue = seekBar.getProgress();
				mDolbyAC4PresentationIndex = progressValue == 0 ? 0xffff : progressValue - 1;
//				if (mListener != null) {
//					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_PRESENTATION_INDEX, mDolbyAC4PresentationIndex);
//				}
			}
		});

		Button resetButton = (Button)view.findViewById(id.dolby_ac4_reset_button);
		resetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if( mListener != null ) {
					resetUIComponents();
					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_VIRTUALIZATION, mDolbyAC4Virtualization);
					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_ENHANCEMENT_GAIN, mDolbyAC4EnhancementGain);
					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_PRESENTATION_INDEX, mDolbyAC4PresentationIndex);
					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_MAIN_ASSO_PREF, mDolbyAC4MainAssoPref);
				}
			}
		});

		Button saveButton = (Button)view.findViewById(id.dolby_ac4_save_button);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDolbyAC4Virtualization = mDolbyAC4VirtualizationSpinner.getSelectedItemPosition();
				mDolbyAC4EnhancementGain = mDolbyAC4EnhancementGainSeekbar.getProgress();
				mDolbyAC4MainAssoPref = mDolbyAC4MainAssoPrefSeekbar.getProgress()-32;
				mDolbyAC4PresentationIndex = mDolbyAC4PresentationIndexSeekbar.getProgress();
				if( mListener != null ) {
					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_VIRTUALIZATION, mDolbyAC4Virtualization);
					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_ENHANCEMENT_GAIN, mDolbyAC4EnhancementGain);
					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_PRESENTATION_INDEX, mDolbyAC4PresentationIndex);
					mListener.onDolbyAC4DialogUpdated(DOLBY_BUTTON.DOLBY_MAIN_ASSO_PREF, mDolbyAC4MainAssoPref);
				}
				dismiss();
			}
		});
	}

	public void dismiss() {
		if( (mDolbyAC4Dialog!=null) && 
				(mDolbyAC4Dialog.isShowing() == true) ) {
			mDolbyAC4Dialog.dismiss();
		}
	}
}
