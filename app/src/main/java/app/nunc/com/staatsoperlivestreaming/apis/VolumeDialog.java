package app.nunc.com.staatsoperlivestreaming.apis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.R.id;

public class VolumeDialog {
	
	private Activity mActivity = null;
	private IVolumeListener	mListener = null;
	private AlertDialog mVolumeDialog = null;
	
	public static enum VOLUME_BUTTON {
		PROGRESS_CHANGED,
		START_TRACKING_TOUCH,
		STOP_TRACKING_TOUCH,
	}
	
	public interface IVolumeListener {
		void onVolumeDialogUpdated(VOLUME_BUTTON button, int volume);	
	}
	
	public VolumeDialog(final Activity activity) {
		mActivity = activity;
	}

	@SuppressWarnings("deprecation")
	public void createAndShow(final float volume, final IVolumeListener listener){
		if( mVolumeDialog != null && mVolumeDialog.isShowing() )
			return;
		mListener = listener;

		Builder builder = new Builder(mActivity);
		builder.setTitle("Volume");
		
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View layout = inflater.inflate(R.layout.seekbar_volume_layout, (ViewGroup)mActivity.findViewById(id.seekbar_layout_vol));
	    
	    mVolumeDialog = builder.setView(layout).create();
	    mVolumeDialog.show();
	    
	    Display display = mActivity.getWindowManager().getDefaultDisplay();
	    
	    int width = display.getWidth();
	    int height = display.getHeight();
	    
	    if(width > height) {
	    	mVolumeDialog.getWindow().setLayout(height, LayoutParams.WRAP_CONTENT);
	    }
	    else {
	    	mVolumeDialog.getWindow().setLayout(width, LayoutParams.WRAP_CONTENT);
	    }
	    
	    SeekBar seekbar = (SeekBar)layout.findViewById(id.seekbar_vol);
	    seekbar.setProgress((int)volume);
	    
	    final TextView currentbw = (TextView)layout.findViewById(id.start_vol);
	    currentbw.setText("" + volume/10 );
	    
	    seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	        public void onProgressChanged(SeekBar seekBar, int volume, boolean fromUser){
	        	if( mListener != null) {
	        		currentbw.setText("" + ((float)volume/10) );
	        		mListener.onVolumeDialogUpdated(VOLUME_BUTTON.PROGRESS_CHANGED, volume);
				}
	        }
	
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				if( mListener != null) {
					mListener.onVolumeDialogUpdated(VOLUME_BUTTON.START_TRACKING_TOUCH, seekBar.getProgress());
				}
			}
	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if( mListener != null) {
					mListener.onVolumeDialogUpdated(VOLUME_BUTTON.STOP_TRACKING_TOUCH, seekBar.getProgress());
				}
			}
	    });
	}
	
	public void dismiss() {
		if ( (mVolumeDialog != null) &&
				(mVolumeDialog.isShowing() == true) ) {
			mVolumeDialog.dismiss();
		}
	}
}
