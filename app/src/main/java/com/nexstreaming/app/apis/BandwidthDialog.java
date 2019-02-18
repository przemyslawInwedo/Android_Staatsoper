package com.nexstreaming.app.apis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.R.id;
import app.nunc.com.staatsoperlivestreaming.R.string;

public class BandwidthDialog {

	private Activity mActivity = null;
	private IBandwidthListener	mListener = null;
	private AlertDialog mBandWidthDialog = null;
		
	public interface IBandwidthListener {
		void onBandwidthDialogUpdated(int minBW, int maxBW);
	}
	
	public BandwidthDialog(final Activity activity) {
		mActivity = activity;
	}
	
	public void dismiss() {
		if( (mBandWidthDialog != null ) && (mBandWidthDialog.isShowing() == true) ) {
			mBandWidthDialog.dismiss();
		}
	}
	
	public void createAndShow(final int minBW, final int maxBW, final IBandwidthListener listener) {
		if( mBandWidthDialog != null && mBandWidthDialog.isShowing() )
			return;

		mListener = listener;
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View layout = inflater.inflate(R.layout.two_seekbar_in_dialog, (ViewGroup)mActivity.findViewById(id.seekbar_layout));
	    
	    final SeekBar max_SeekBar = layout.findViewById(id.max_seekbar);
	    final SeekBar min_SeekBar = layout.findViewById(id.min_seekbar);
	    final TextView currentMaxBW = layout.findViewById(id.current_max_bw);
	    final TextView currentMinBW = layout.findViewById(id.current_min_bw);

		min_SeekBar.setOnKeyListener(mOnKeyListener);
		max_SeekBar.setOnKeyListener(mOnKeyListener);

	    Builder builder = new Builder(mActivity);
		builder.setTitle("Band Width")
		.setPositiveButton(mActivity.getString(R.string.ok), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if( mListener != null )
					mListener.onBandwidthDialogUpdated(min_SeekBar.getProgress(), max_SeekBar.getProgress());
			}
		}).setNegativeButton(mActivity.getString(R.string.cancel), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		
	    mBandWidthDialog = builder.setView(layout).create();
	    WindowManager.LayoutParams params = mBandWidthDialog.getWindow().getAttributes();
		params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mBandWidthDialog.getWindow().setAttributes(params);
	    mBandWidthDialog.show();
	    
	    max_SeekBar.setProgress(maxBW);
	    min_SeekBar.setProgress(minBW);
	    currentMaxBW.setText("" + maxBW + " kbps");
	    currentMinBW.setText("" + minBW + " kbps");
	    
	    max_SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
	        	currentMaxBW.setText("" + progress + " kbps");
	        }
	
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
	    });
	    
	    min_SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
	        	currentMinBW.setText("" + progress + " kbps");
	        }
	
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
	    });
	}

	View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

		private long mDownTime = 0;

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			SeekBar seekbar = (SeekBar) v;
			if( event.getAction() == KeyEvent.ACTION_DOWN ) {
				if (mDownTime == event.getDownTime()) {
					seekbar.setKeyProgressIncrement(100);
				}

				if (mDownTime == 0 && event.isLongPress())
					mDownTime = event.getDownTime();
			} else if( event.getAction() == KeyEvent.ACTION_UP ) {
				mDownTime = 0;
				seekbar.setKeyProgressIncrement(1);
			}

			return false;
		}
	};
}
