package com.nexstreaming.app.apis;

import android.graphics.Bitmap;

public class ThumbnailInfo {

	private int mCTS = 0;
	private Bitmap mBitmap = null;
	
	public ThumbnailInfo(int cts, Bitmap bitmap) {
		mCTS = cts;
		mBitmap = bitmap;
	}
	
	public int getCTS() {
		return mCTS;
	}
	
	public Bitmap getBitmap() {
		return mBitmap;
	}
	
}
