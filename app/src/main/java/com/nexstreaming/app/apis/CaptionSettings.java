package com.nexstreaming.app.apis;

import com.nexstreaming.nexplayerengine.NexClosedCaption.CaptionColor;

public class CaptionSettings {
	public int mFontsize = 1;
	public int mFontColorIndex = 0;
	public int mFontopacity = 0;
	public int mFontedge = 0;
	public int mBackgroundColorIndex = 7;
	public int mBackGroundOpacity = 1;
	public int mWindowColorIndex = 7;
	public int mWindowOpacityIndex = 1;
	public int mFontShadowColorIndex = 7;
	public int mOpacity = 0;
	
	//caption property value. 
	//font 
	public int mNCaptionFontSize = 100;
	public int mNCaptionFontStyle = 0;
	public int mNCaptionBackGround= 0; /* position index from CaptiohnStyleDialogUtil's Background Color */
	/* FIXME: This constant should move into CaptionStyleDialogUtil, and mNCaptionBackground should hold an actual color value */
	public final static int CaptionBackgroundColorIndexTransparent = 8;
	
	public int mCaptionTextOpacity = 255;

	public CaptionColor mFontColor = CaptionColor.WHITE;
	public CaptionColor mFontShadowColor = CaptionColor.BLACK;
	public CaptionColor mBackGroundColor = CaptionColor.BLACK;
	public int mCaptionBackGroundOpacity = 255;
	public int mWindowOpacity = 255;
	public CaptionColor mWindowColor = CaptionColor.BLACK;
	
	public int mFontShadowOpacity = 255;
	
	public CaptionSettings() {
		// TODO Auto-generated constructor stub
	}

	public CaptionSettings(CaptionSettings settings) {
		if (null != settings)
		{
			mFontsize = settings.mFontsize;
			mFontColorIndex = settings.mFontColorIndex;
			mFontopacity = settings.mFontopacity;
			mFontedge = settings.mFontedge;
			mBackgroundColorIndex = settings.mBackgroundColorIndex;
			mBackGroundOpacity = settings.mBackGroundOpacity;
			mWindowColorIndex = settings.mWindowColorIndex;
			mWindowOpacityIndex = settings.mWindowOpacityIndex;
			mFontShadowColorIndex = settings.mFontShadowColorIndex;
			mOpacity = settings.mOpacity;

			mNCaptionFontSize = settings.mNCaptionFontSize;
			mNCaptionFontStyle = settings.mNCaptionFontStyle;
			mNCaptionBackGround = settings.mNCaptionBackGround;
			mCaptionTextOpacity = settings.mCaptionTextOpacity;
			mFontColor = settings.mFontColor;
			mFontShadowColor = settings.mFontShadowColor;
			mBackGroundColor = settings.mBackGroundColor;
			mCaptionBackGroundOpacity = settings.mCaptionBackGroundOpacity;
			mWindowOpacity = settings.mWindowOpacity;
			mWindowColor = settings.mWindowColor;
			mFontShadowOpacity = settings.mFontShadowOpacity;
		}
		else
		{
			resetValuesToDefault();
		}
	}
	
	public void resetValuesToDefault() {
		mFontsize = 1;
		mFontColorIndex = 0;
		mFontopacity = 0;
		mFontedge = 0;
		mBackgroundColorIndex = 7;
		mBackGroundOpacity = 1;
		mWindowColorIndex = 7;
		mWindowOpacityIndex = 1;
		mFontShadowColorIndex = 7;
		mOpacity = 0;		

		mNCaptionFontSize = 100;
		mNCaptionFontStyle = 0;
		mNCaptionBackGround= 0;

		mCaptionTextOpacity = 255;

		mFontColor = CaptionColor.WHITE;
		mFontShadowColor = CaptionColor.BLACK;
		mBackGroundColor = CaptionColor.BLACK;
		mCaptionBackGroundOpacity = 255;
		mWindowOpacity = 255;
		mWindowColor = CaptionColor.BLACK;

		mFontShadowOpacity = 255;
	}
}
