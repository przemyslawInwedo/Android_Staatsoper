package app.nunc.com.staatsoperlivestreaming.apis;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexABRController;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexContentInformation;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStreamInformation;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexTrackInformation;


/**
 * Created by bonnie.kyeon on 2015-04-30.
 */
public class TargetBandWidthDialog {

	private Activity mActivity = null;
	private AlertDialog mDialog = null;
	private Spinner mTargetOptionSpinner = null;
	private Spinner mSegmentOptionSpinner = null;
	private Spinner mTargetBandWidthSpinner = null;
	private LinearLayout mTargetBandWidthEditTextLayout = null;
	private EditText mTargetBandWidthEditText = null;
	private TargetBandWidthIListener mIListener = null;
	private CheckBox mABRCheckBox = null;
	private boolean mEnableAudioOnlyTrack = true;
	private int mTargetOptionIndex = 0;
	private int mSegmentOptionIndex = 0;

	private enum MediaType {
		NONE    (0, "None"),
		AUDIO   (1, "Audio"),
		VIDEO   (2, "Video"),
		AV      (3, "AV");

		int mCode;
		String mDesc;
		MediaType(int code, String desc) {
			mCode = code;
			mDesc = desc;
		}
		private static MediaType fromIntegerValue( int code ) {
			for(int i = 0; i< MediaType.values().length; i++ ) {
				if( MediaType.values()[i].mCode == code )
					return MediaType.values()[i];
			}
			return NONE;
		}
		private String getDescription() {
			return mDesc;
		}
	}

	public TargetBandWidthDialog(Activity activity, TargetBandWidthIListener listener, boolean enableAudioOnlyTrack) {
		mActivity = activity;
		mIListener = listener;
		mEnableAudioOnlyTrack = enableAudioOnlyTrack;
	}

	public interface TargetBandWidthIListener {
		void onTargetBandWidthDialogUpdated(boolean abrEnabled, int targetBandWidth, NexABRController.SegmentOption segOption, NexABRController.TargetOption targetOption);
	}

	public boolean isShowing() {
		return mDialog != null ? mDialog.isShowing() : false;
	}

	public void dismiss() {
		if( mDialog != null ) {
			mDialog.dismiss();
		}
	}

	public void createAndShow(NexContentInformation contentInfo, boolean isABREnabled) {
		if( mDialog == null ) {
			createDialog();
		}

		if( contentInfo != null ) {
			updateUIComponents(contentInfo, isABREnabled);
			mDialog.show();
		}
	}

	private NexABRController.TargetOption getTargetOption(int position) {
		NexABRController.TargetOption option = NexABRController.TargetOption.DEFAULT;

		if( position == 1 ) {
			option = NexABRController.TargetOption.BELOW;
		} else if( position == 2 ) {
			option = NexABRController.TargetOption.ABOVE;
		} else if( position == 3 ) {
			option = NexABRController.TargetOption.MATCH;
		}

		return option;
	}

	private NexABRController.SegmentOption getSegmentOption(int position) {
		NexABRController.SegmentOption option = NexABRController.SegmentOption.DEFAULT;

		if( position == 1 ) {
			option = NexABRController.SegmentOption.QUICKMIX;
		} else if( position == 2 ) {
			option = NexABRController.SegmentOption.LATEMIX;
		}

		return option;
	}

	private void updateTargetBandWidthLayout(NexABRController.TargetOption option) {
		if( option == NexABRController.TargetOption.MATCH ) {
			mTargetBandWidthSpinner.setVisibility(View.VISIBLE);
			mTargetBandWidthEditText.setText("");
			mTargetBandWidthEditTextLayout.setVisibility(View.GONE);
		} else {
			mTargetBandWidthSpinner.setVisibility(View.GONE);
			mTargetBandWidthEditTextLayout.setVisibility(View.VISIBLE);
		}
	}

	private void updateTrackBandWidthList(NexContentInformation contentInfo) {
		ArrayList<String> bandWidthList = new ArrayList<String>();
		int currentIndex = 0;

		for (int i=0; i<contentInfo.mStreamNum; i++) {
			// Do not support setTargetBandwidth for audio/text stream
			if( contentInfo.mCurrVideoStreamID == contentInfo.mArrStreamInformation[i].mID ) {
				NexStreamInformation curStreamInfo = contentInfo.mArrStreamInformation[i];
				for(int trackIndex = 0; trackIndex < curStreamInfo.mTrackCount; trackIndex++) {
					NexTrackInformation trackInfo = curStreamInfo.mArrTrackInformation[trackIndex];
					if( trackInfo.mBandWidth > 0 && trackInfo.mValid == 1 && !trackInfo.mIFrameTrack ) {
						if( trackInfo.mTrackID == curStreamInfo.mCurrTrackID ) {
							currentIndex = bandWidthList.size();
						}

						MediaType mediaType = MediaType.fromIntegerValue(trackInfo.mType);
						if( !(mediaType.equals(MediaType.AUDIO) && !mEnableAudioOnlyTrack) ) {
							bandWidthList.add(mediaType.getDescription() + " : " + trackInfo.mBandWidth);
						}

					}
				}
				break;
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, bandWidthList);
		mTargetBandWidthSpinner.setAdapter(adapter);
		mTargetBandWidthSpinner.setSelection(currentIndex);
	}

	private void updateUIComponents(NexContentInformation contentInfo, boolean isABREnabled) {
		mTargetOptionSpinner.setSelection(mTargetOptionIndex);
		mSegmentOptionSpinner.setSelection(mSegmentOptionIndex);
		updateTrackBandWidthList(contentInfo);
		mTargetBandWidthEditText.setText("0");
		mABRCheckBox.setChecked(isABREnabled);
		enableUIComponents(!isABREnabled);
	}

	private void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(R.string.target_bandwidth);

		View view = View.inflate(mActivity, R.layout.target_bandwidth_dialog, null);
		setupUIComponents(view);
		mDialog = builder.setView(view).create();
		WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
		params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mDialog.getWindow().setAttributes(params);
	}

	private void enableUIComponents(boolean enabled) {
		mSegmentOptionSpinner.setEnabled(enabled);
		mTargetOptionSpinner.setEnabled(enabled);
		mTargetBandWidthSpinner.setEnabled(enabled);
		mTargetBandWidthEditText.setEnabled(enabled);
	}

	private void setupUIComponents(View view) {
		mTargetOptionSpinner = (Spinner)view.findViewById(R.id.target_option_spinner);
		mSegmentOptionSpinner = (Spinner)view.findViewById(R.id.segment_option_spinner);
		mTargetBandWidthEditTextLayout = (LinearLayout)view.findViewById(R.id.target_bandwidth_edittext_layout);
		mTargetBandWidthSpinner = (Spinner)view.findViewById(R.id.target_bandwidth_spinner);
		mTargetBandWidthEditText = (EditText)view.findViewById(R.id.target_bandwidth_edittext);
		mABRCheckBox = (CheckBox)view.findViewById(R.id.abr_check_box);
		Button okButton = (Button)view.findViewById(R.id.ok_button);
		Button cancelButton = (Button)view.findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NexABRController.TargetOption targetOption = getTargetOption(mTargetOptionSpinner.getSelectedItemPosition());
				NexABRController.SegmentOption segOption = getSegmentOption(mSegmentOptionSpinner.getSelectedItemPosition());
				boolean abrEnabled = mABRCheckBox.isChecked();
				boolean error = false;
				int bandWidth = 0;
				if( targetOption.equals(NexABRController.TargetOption.MATCH) ) {
					if( mTargetBandWidthSpinner.getSelectedItem() != null ) {
						String[] item = ((String) mTargetBandWidthSpinner.getSelectedItem()).split(":");
						if( item[1] != null ) {
							bandWidth = Integer.parseInt( item[1].trim() );
						}
					}
				} else {
					if( mTargetBandWidthEditText.getText().toString().length() > 0 )
						bandWidth = Integer.parseInt(mTargetBandWidthEditText.getText().toString());
					else
						error = true;
				}

				if( !error ) {
					mTargetOptionIndex = mTargetOptionSpinner.getSelectedItemPosition();
					mSegmentOptionIndex = mSegmentOptionSpinner.getSelectedItemPosition();
					mIListener.onTargetBandWidthDialogUpdated(abrEnabled, bandWidth, segOption, targetOption);
					mDialog.dismiss();
				}
			}
		});

		mABRCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				enableUIComponents(!isChecked);
			}
		});
		mTargetOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateTargetBandWidthLayout(getTargetOption(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}
}
