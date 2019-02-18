package com.nexstreaming.app.apis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.R.layout;
import app.nunc.com.staatsoperlivestreaming.R.id;
import app.nunc.com.staatsoperlivestreaming.R.string;
import app.nunc.com.staatsoperlivestreaming.R.array;
import com.nexstreaming.app.widget.NexSeekBar;

import java.util.ArrayList;

public class ThumbnailSeekBar extends LinearLayout {
	private static final Handler mHandler = new Handler();

	public interface ISeekBarListener {
		void onStopTrackingTouch(SeekBar seekBar);
		void onProgressChanged(SeekBar seekBar, int progress);
	}
	
	private ISeekBarListener mSeekBarListener = null;
	private ArrayList<ThumbnailInfo> mThumbnailList = null;
	private int mLastThumbnailCTS = 0;
	
	private Bitmap mDefaultBitmap = null;
	private ImageView mThumbnailView;
	private SeekBar mSeekBar;
	private TextView mCurrentTimeTextView;
	private TextView mDurationTimeTextView;

	private int mThumbnailSearchInterval = 0;
	private int mMaximumThumbnailFrame = 10;

	private boolean mUseDynamicThumbnail = false;
	private boolean mIsTrackingTouch = false;
	private boolean mIsDynamicThumbnailRecvEnd = false;
	
	public ThumbnailSeekBar(Context context) {
		super(context);
		init(context, null);
	}
	
	public ThumbnailSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	@SuppressLint("NewApi")
	public ThumbnailSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		View parent = View.inflate(context, R.layout.thumbnail_seekbar_layout, (ViewGroup) findViewById(R.id.parent_layout));
		parent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		setupUIComponents(context, parent);

		if( attrs != null )
			setCustomAttributeSet(context, parent, attrs);
		addView(parent);
	}

	private void setCustomAttributeSet(Context context, View parent, AttributeSet attrs) {
		LinearLayout seekLayout = (LinearLayout) parent.findViewById(R.id.seek_layout);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThumbnailSeekBar);
		seekLayout.setBackgroundColor(typedArray.getColor(R.styleable.ThumbnailSeekBar_backgroundColor, Color.TRANSPARENT));
		typedArray.recycle();
	}
	
	private void setupUIComponents(Context context, View parent) {
	    mThumbnailList = new ArrayList<ThumbnailInfo>();
	    
	    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.audio_skin2);
	    mDefaultBitmap = Bitmap.createScaledBitmap(bitmap, 160, 120, true);
	    bitmap.recycle();
	    
		mThumbnailView = (ImageView) parent.findViewById(R.id.thumbnail_imageview);
		mThumbnailView.setImageBitmap(mDefaultBitmap);
		mCurrentTimeTextView = (TextView) parent.findViewById(R.id.currenttime);
		mDurationTimeTextView = (TextView) parent.findViewById(R.id.durationtime);
		mSeekBar = (NexSeekBar) parent.findViewById(R.id.seekbar);
		mSeekBar.setMax(0);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mIsTrackingTouch  = false;
				
				if( mUseDynamicThumbnail ) {
					mThumbnailView.setVisibility(View.GONE);
				}
				
				if( mSeekBarListener != null )
					mSeekBarListener.onStopTrackingTouch(seekBar);
			}
			
			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
				mIsTrackingTouch = true;
				
				if( mUseDynamicThumbnail ) {
					setThumbnailBitmap(seekBar.getProgress());
					setThumbnailOutputPosition();
					mThumbnailView.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
				if( fromUser && mUseDynamicThumbnail ) {
					setThumbnailBitmap(progress);
					setThumbnailOutputPosition();
				}
				
				if( mSeekBarListener != null )
					mSeekBarListener.onProgressChanged(seekBar, progress);
			}
		});
	}
	public void setMarkers(int[] markers){
		if(mSeekBar != null && mSeekBar instanceof NexSeekBar){
			((NexSeekBar) mSeekBar).setMarkers(markers);
		}
	}

	public void setMaximumThumbnailFrame(int max) {
		mMaximumThumbnailFrame = max;
	}

	private void setThumbnailBitmap(int progress) {
		if( mThumbnailList.size() > 0 ) {
			if( !mIsDynamicThumbnailRecvEnd && ( mLastThumbnailCTS + mThumbnailSearchInterval ) < progress ) {
				mThumbnailView.setImageBitmap(mDefaultBitmap);
				return;
			}
			
			ThumbnailInfo info = findThumbnailInfo(progress);
			mThumbnailView.setImageBitmap(info.getBitmap());
		}
	}
	
	private void setThumbnailOutputPosition() {
		int range = getWidth() - mThumbnailView.getWidth();
		float seekBarProgress = mSeekBar.getProgress();
		float seekBarPercent = 0;
		int thumbnailX = 0;
		
		if( seekBarProgress != 0 ) {
			seekBarPercent = mSeekBar.getMax() / seekBarProgress;
			thumbnailX = (int)(range / seekBarPercent);
		}
				
		ViewGroup.MarginLayoutParams params = new MarginLayoutParams(mThumbnailView.getLayoutParams());
		params.leftMargin = thumbnailX;
		mThumbnailView.setLayoutParams(new LinearLayout.LayoutParams(params));
	}
	
	private static String getTimeToString(int nTime) {
		String strTime;
		
		int minutes = nTime/60000;
		int seconds = (nTime%60000)/1000;
		int hour = 0;
		if(minutes >= 60) {
			hour = minutes / 60;
			minutes = minutes - (hour*60); 
		}
		String leadingZero = "";
		String strPaddingM = null;
		
		if(minutes < 10) {
			strPaddingM = "0";
		} else {
			strPaddingM = "";
		}
		
		if( seconds < 10 )
			leadingZero = "0";
		
		if(hour >= 1)
			strTime = hour + ":" + strPaddingM + minutes + ":" + leadingZero + seconds;
		else
			strTime = strPaddingM + minutes + ":" + leadingZero + seconds;
		
		return strTime;
	}
	
	private ThumbnailInfo findThumbnailInfo(int progress) {
		int thumbnailIndex = 0;
		int temp = Math.abs(mThumbnailList.get(0).getCTS() - progress);
		
		for( int i = 1; i < mThumbnailList.size(); i++ ) {
			ThumbnailInfo thumbnailInfo = mThumbnailList.get(i);
			int diff = Math.abs(thumbnailInfo.getCTS() - progress);
			if( diff < temp ) {
				thumbnailIndex = i;
				temp = diff;
			} else
				break;
		}
		
		return mThumbnailList.get(thumbnailIndex);
	}
	
	private void updateThumbnailView() {
		if( mUseDynamicThumbnail && isTrackingTouch() ) {
			BitmapDrawable thumbnailBitmapDrawble = (BitmapDrawable)mThumbnailView.getDrawable();
			Bitmap thumbnailBitmap = thumbnailBitmapDrawble.getBitmap();
			if( mDefaultBitmap.equals(thumbnailBitmap) ) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						setThumbnailBitmap(getProgress());
					}
				});
			}
		}
	}

	public void setKeyProgressIncrement(int increment) {
		mSeekBar.setKeyProgressIncrement(increment);
	}
	
	public void enableSeekBar(final boolean isEnable) {
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				int visibility = isEnable ? View.VISIBLE : View.INVISIBLE;
				boolean needToChange = mSeekBar.isEnabled() != isEnable;
				
				if( needToChange ) {
					mSeekBar.setEnabled(isEnable);
					mCurrentTimeTextView.setVisibility(visibility);
					mDurationTimeTextView.setVisibility(visibility);
				}
			}
		});
	}
	
	public void setThumbnailSearchInterval(int interval) {
		mThumbnailSearchInterval = interval;
	}
	
	public void addDynamicThumbnailInfo(int cts, Bitmap bitmap) {
		mThumbnailList.add(new ThumbnailInfo(cts, bitmap));
		mLastThumbnailCTS = cts;
		updateThumbnailView();
	}
	
	public void setCurrentTimeText(int sec) {
		mCurrentTimeTextView.setText(getTimeToString(sec));
	}
	
	public void setDurationTimeText(final int sec) {
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				mDurationTimeTextView.setText(getTimeToString(sec));
			}
		});
	}
	
	public void enableDynamicThumbnail(boolean isEnable) {
		mUseDynamicThumbnail = isEnable;
		
		if( isTrackingTouch() ) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if( mUseDynamicThumbnail )
						mThumbnailView.setVisibility(View.VISIBLE);
					else 
						mThumbnailView.setVisibility(View.GONE);
				}
			});
		}
	}
	
	public void setOnSeekBarChangeListener(ISeekBarListener listener) {
		mSeekBarListener = listener;
	}
	
	public void dynamicThumbnailRecvEnd() {
		mIsDynamicThumbnailRecvEnd = true;
		updateThumbnailView();
	}
	
	public void setMax(int max) {
		mSeekBar.setMax(max);
	}
	
	public void setProgress(int progress) {
		mSeekBar.setProgress(progress);
	}
	
	public void setSecondaryProgress(int secondaryProgress) {
		mSeekBar.setSecondaryProgress(secondaryProgress);
	}
	
	public int getMax() {
		return mSeekBar.getMax();
	}
	
	public int getProgress() {
		return mSeekBar.getProgress();
	}
	
	public boolean isTrackingTouch() {
		return mIsTrackingTouch;
	}
	
	public boolean isThumbnailArrayFull() {
		return mThumbnailList.size() >= mMaximumThumbnailFrame;
	}
	
	public void resetThumbnailStatus() {
		if( mThumbnailList != null ) {
			for( ThumbnailInfo thumbnail : mThumbnailList ) {
				thumbnail.getBitmap().recycle();
			}
			mThumbnailList.clear();
		}
		mThumbnailView.setImageBitmap(mDefaultBitmap);
		mLastThumbnailCTS = 0;
		mIsDynamicThumbnailRecvEnd = false;
		mThumbnailSearchInterval = 0;
	}
	
	public void resetSeekBarStatus() {
		enableSeekBar(true);
		setMax(0);
		setSecondaryProgress(0);
		setDurationTimeText(0);
	}
}
