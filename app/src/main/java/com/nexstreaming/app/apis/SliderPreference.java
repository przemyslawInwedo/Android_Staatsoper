package com.nexstreaming.app.apis;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.text.DecimalFormat;

public class SliderPreference extends Preference {

	private static final String LOG_TAG = "SliderPreferences";
	private float scaleDIPtoPX;
	private TextView mValueText = null;
	private float mMinValue = 0;
	private float mMaxValue = 100;
	private float mInterval = 1;
	private float mCurrentValue = 0;
	private String mUnitLabel = "";
	private int mScaleFactor = 1;
	private int mScaleMax = 100;
	private int mScaleMin = 0;
	private int mScaleInterval = 1;
	private DecimalFormat mFmt;
	private String defaultTextAtMin = "";
	private Context mContext;

	
	private void readAttrs( AttributeSet attrs ) {
		for( int i = 0; i< attrs.getAttributeCount(); i++) {
			String name = attrs.getAttributeName(i);
			try {
				Log.d( LOG_TAG, i + ": name=" + name + ", value=" + attrs.getAttributeValue(i) );
			} catch (Exception e) {
				Log.d( LOG_TAG, i + ": name=" + name + ", value=?ERROR", e );
			}
			if ( name.equals("min") ) {
				try {
					mMinValue 		= Float.parseFloat(attrs.getAttributeValue(i));
				} catch ( NumberFormatException e ) {
					Log.d(LOG_TAG,"Error parsing attribute '" + name + "'='" + attrs.getAttributeValue(i) + "'",e);
					mMinValue		= 0;
				}
			} else if( name.equals("max") ) {
				try {
					mMaxValue		= Float.parseFloat(attrs.getAttributeValue(i));
				} catch ( NumberFormatException e ) {
					Log.d(LOG_TAG,"Error parsing attribute '" + name + "'='" + attrs.getAttributeValue(i) + "'",e);
					mMaxValue		= 100;
				}
			} else if( name.equals("interval") ) {
				try {
					mInterval 		= Float.parseFloat(attrs.getAttributeValue(i));
				} catch ( NumberFormatException e ) {
					Log.d(LOG_TAG,"Error parsing attribute '" + name + "'='" + attrs.getAttributeValue(i) + "'",e);
					mMaxValue		= 0;
				}
				if( mInterval != (float)(int)mInterval ) {
					mScaleFactor = (int)(1/(mInterval % 1));
				}
			} else if( name.equals("unitLabel") ) {
				mUnitLabel	= " " + attrs.getAttributeValue(i);
			} else if( name.equals("defaultTextAtMin") ) {
				defaultTextAtMin = attrs.getAttributeValue(i);
			}
		}
		mScaleMin = (int)(mMinValue * mScaleFactor);
		mScaleMax = (int)(mMaxValue * mScaleFactor);
		mScaleInterval = (int)(mInterval * mScaleFactor);
	}
	
	public SliderPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		scaleDIPtoPX = getContext().getResources().getDisplayMetrics().density;
		readAttrs( attrs );
	}

	public SliderPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		scaleDIPtoPX = getContext().getResources().getDisplayMetrics().density;
		readAttrs(attrs);
	}

	public SliderPreference(Context context) {
		super(context);
		scaleDIPtoPX = getContext().getResources().getDisplayMetrics().density;
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		super.onCreateView(parent);

		LinearLayout.LayoutParams leftAlign = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		leftAlign.gravity = Gravity.START;
		
		LinearLayout.LayoutParams rightAlign = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		rightAlign.gravity = Gravity.END;
		
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(
				(int)(15*scaleDIPtoPX+0.5f),
				(int)(3*scaleDIPtoPX+0.5f),
				(int)(15*scaleDIPtoPX+0.5f),
				(int)(5*scaleDIPtoPX+0.5f));
		
		LinearLayout headerRow = new LinearLayout(mContext);
		headerRow.setOrientation(LinearLayout.HORIZONTAL);
		
		TextView labelText = new TextView(mContext);
		labelText.setText(getTitle());
		labelText.setGravity(Gravity.START);
		labelText.setTextSize(20);
		labelText.setTextColor(0xFFFFFFFF);
		
		String fmtstr = "";
		for( int i=(""+(mInterval%1)).length()-1; i>1; i-- ) {
			fmtstr = fmtstr + "0";
		}
		if( fmtstr.length() > 0 ) {
			mFmt = new DecimalFormat("#0." + fmtstr   );
		} else {
			mFmt = new DecimalFormat("#0");
		}
		
		mValueText = new TextView(mContext);
		if (mCurrentValue == mMinValue && defaultTextAtMin.length() > 0 )
			mValueText.setText( "" + defaultTextAtMin );
		else
			mValueText.setText( "" + mFmt.format(mCurrentValue) + mUnitLabel );
		mValueText.setGravity(Gravity.END);
		mValueText.setTextSize(20);
		
		headerRow.addView(labelText,leftAlign);
		headerRow.addView(mValueText,rightAlign);
		
		SeekBar seekBar = new SeekBar(mContext);
		seekBar.setMax(mScaleMax-mScaleMin);
		seekBar.setProgress((int)(mCurrentValue*mScaleFactor)-mScaleMin);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float progressValue = seekBar.getProgress();
				if ( progressValue == mMinValue && defaultTextAtMin.length() > 0 )
			    	mValueText.setText( "" + defaultTextAtMin );
			    updateFloatPreference(mCurrentValue);
			    notifyChanged();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			    progress = Math.round(((float)progress)/mScaleInterval)*mScaleInterval;
			    
			    if(!callChangeListener(progress)) {
			    	// If we're here, the change was refused; don't update the
			    	// current value, and set the slider back to reflect the
			    	// previous value.
					seekBar.setProgress((int)(mCurrentValue*mScaleFactor)-mScaleMin);
			    	return; 
			    }
			    
			    seekBar.setProgress(progress);
			    mCurrentValue = ((float)progress / (float)mScaleFactor) + mMinValue;
				if (mCurrentValue == mMinValue && defaultTextAtMin.length() > 0 )
					mValueText.setText( "" + defaultTextAtMin );
				else
					mValueText.setText( "" + mFmt.format(mCurrentValue) + mUnitLabel );
			}
		});
		
		layout.addView(headerRow,
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(seekBar,
				LinearLayout.LayoutParams.FILL_PARENT,
				(int)(28*scaleDIPtoPX+0.5f));
		
		return layout;
	}

	private void updateFloatPreference( float newValue ) {
		SharedPreferences.Editor editor =  getEditor();
		editor.putFloat(getKey(), newValue);
		editor.commit();
	}
	
/*	private float limitToRange( float value ) {
		if( value < mMinValue )
			return mMinValue;
		if( value > mMaxValue )
			return mMaxValue;
		return value;
	}*/

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getFloat(index, 0);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
	     float workingValue = restorePersistedValue ? getPersistedFloat(0) : (Float)defaultValue;
	     
	      if(!restorePersistedValue)
	        persistFloat(workingValue);
	     
	      mCurrentValue = workingValue;
	}
	
}
