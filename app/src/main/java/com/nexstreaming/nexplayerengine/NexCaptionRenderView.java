package com.nexstreaming.nexplayerengine;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

/**
 * @brief This class is used to manage caption rendering in \c NexVideoView. 
 */
public class NexCaptionRenderView extends FrameLayout {
	private static final String LOG_TAG = "NexCaptionRenderView";

	private static final Handler    mHandler = new Handler();
	private Context                 mContext;
	private RendererType            mRendererType;

	private NexCaptionRenderer              mCEA608Renderer;
	private NexEIA708CaptionView            mEIA708Renderer;
	private NexCaptionRendererForTimedText  mCaptionRendererForTimedText;
	private NexCaptionRendererForWebVTT     mCaptionRendererForWebVTT;
	private NexEIA708Struct                 mEIA708CC;
	private TextView                        mExternalSubtitleTextView;
	private TextView                        mUserTextView;
	private LayoutParams                    mUserTextViewParam;
	private String                          mEncodingPreset;

	private NexCaptionAttribute             mCaptionAttribute;
	private RendererAttribute               mRendererAttribute;

	private OnTextRegionChangedListener     mTextRegionChangedListener;

	protected enum RendererType {
		_UNKNWON, _CEA608, _CEA708, _3GPP, _TTML, _WEBVTT, _EXTERNAL_SUBTITLE;

		protected static RendererType get( int captionType ) {
			switch( captionType ) {
				case NexClosedCaption.TEXT_TYPE_NTSC_CC_CH1 :
				case NexClosedCaption.TEXT_TYPE_NTSC_CC_CH2 :
					return RendererType._CEA608;
				case NexClosedCaption.TEXT_TYPE_ATSCMH_CC :
					return RendererType._CEA708;
				case NexClosedCaption.TEXT_TYPE_TTML_TIMEDTEXT :
				case NexClosedCaption.TEXT_TYPE_EXTERNAL_TTML :
					return RendererType._TTML;
				case NexClosedCaption.TEXT_TYPE_3GPP_TIMEDTEXT :
					return RendererType._3GPP;
				case NexClosedCaption.TEXT_TYPE_WEBVTT :
					return RendererType._WEBVTT;
				case NexClosedCaption.TEXT_TYPE_SMI :
				case NexClosedCaption.TEXT_TYPE_SRT :
				case NexClosedCaption.TEXT_TYPE_SUB :
					return RendererType._EXTERNAL_SUBTITLE;
				case NexClosedCaption.TEXT_TYPE_GENERAL :
				case NexClosedCaption.TEXT_TYPE_ATSCMH_BAR :
				case NexClosedCaption.TEXT_TYPE_ATSCMH_AFD :
				case NexClosedCaption.TEXT_TYPE_UNKNOWN :
				default:
					return RendererType._UNKNWON;
			}
		}
	}

	protected static class RendererAttribute {

		protected final static int CEA608_RENDERMODE = 0;
		private int mCEA608RenderMode;

		protected RendererAttribute() {
			mCEA608RenderMode   = NexCaptionRenderer.RENDER_MODE_CUSTOM;
		}

		protected void setValue(int key, Object value) {
			switch(key) {

				case CEA608_RENDERMODE :
					mCEA608RenderMode = (Integer)value;
					break;
			}
		}

		protected Object getValue(int key) {
			switch(key) {
				case CEA608_RENDERMODE :
					return mCEA608RenderMode;
			}
			return null;
		}
	}

	interface OnTextRegionChangedListener {
		void OnTextRegionChanged( int[] textRegion );
	}

	/**
	 * @brief Constructor for NexCaptionRenderView.
	 *
	 * @param context The \link android.content.Context Context\endlink instance
	 * associated with the activity that will contain this view.
	 *
	 * @since version 6.42
	 */
	public NexCaptionRenderView(Context context) {
		super(context);
		init(context);
	}
	
	/**
	 * @brief Constructor for NexCaptionRenderView.
	 *
	 * @see NexVideoRenderer.NexVideoRenderer(android.content.Context)
	 * @since version 6.42
	 */
	public NexCaptionRenderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @brief This method sets the attributes of captions used by the \c NexVideoView. 
	 *
	 * @param attribute A \c NexCaptionAttribute object. 
	 *
	 * @since version 6.42
	 */
	public void setCaptionAttribute(NexCaptionAttribute attribute) {
		mCaptionAttribute = attribute;

		if (null != attribute) {
			setFontColorAndSize(attribute);
			setFontEdge(attribute);
			setBackgroundColor(attribute);
			setWindowColor(attribute);
		} else {
			resetStyle();
		}
	}

	private void resetStyle() {
		switch(mRendererType) {
			case _CEA608 :
				mCEA608Renderer.initCaptionStyle();
				break;
			case _CEA708:
				mEIA708Renderer.initCaptionStyle();
				break;
			case _WEBVTT :
				mCaptionRendererForWebVTT.initCaptionStyle();
				break;
			case _TTML :
			case _3GPP :
				mCaptionRendererForTimedText.initCaptionStyle();
				break;
		}

		NexLog.d(LOG_TAG, "resetStyle " + mRendererType);
	}

	/**
	 * @brief This method gets the attribute information set to the current caption.
	 * 
	 * @return The \c NexCaptionAttribute object set to the current caption. 
	 * @since version 6.42
	 */
	public NexCaptionAttribute getCaptionAttribute() {
		return mCaptionAttribute;
	}

	/**
	 * @brief This method sets the text view to display external subtitles.     
	 *
	 * @param textview 		A TextView instance.
	 * @param param    		The layout parameters associated with the textview.
	 * @param charsetName   Converts the byte array to a string using the named charset.
	 *
	 * @since version 6.42
	 */
	public void setExternalSubtitleTextView(TextView textview, LayoutParams param, String encodingPreset) {
		mUserTextView = textview;
		mUserTextViewParam = param;
		mEncodingPreset = encodingPreset;
	}

	protected void setOnTextRegionChangedListener(OnTextRegionChangedListener l) {
		mTextRegionChangedListener = l;
	}

	protected RendererType getRendererType() {
		return mRendererType;
	}

	protected void clearCaptionString() {
		switch(mRendererType) {
			case _CEA608 :
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if( mCEA608Renderer != null ) {
							NexLog.d(LOG_TAG, "clearCaptionString _CEA608");
							mCEA608Renderer.makeBlankData();
							mCEA608Renderer.invalidate();
						}
					}
				});
				break;
			case _CEA708:
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mEIA708Renderer != null) {
							NexLog.d(LOG_TAG, "clearCaptionString _CEA708");
							mEIA708Renderer.clearCaptionString();
							mEIA708Renderer.setValidateUpdate(true);
							mEIA708Renderer.invalidate();
						}
					}
				});
				break;
			case _WEBVTT :
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mCaptionRendererForWebVTT != null) {
							NexLog.d(LOG_TAG, "clearCaptionString _WEBVTT");
							mCaptionRendererForWebVTT.clear();
							mCaptionRendererForWebVTT.invalidate();
						}
					}
				});
				break;
			case _TTML :
			case _3GPP :
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mCaptionRendererForTimedText != null) {
							NexLog.d(LOG_TAG, "clearCaptionString _3GPP");
							mCaptionRendererForTimedText.clear();
							mCaptionRendererForTimedText.invalidate();
						}
					}
				});
				break;
			case _EXTERNAL_SUBTITLE :
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mExternalSubtitleTextView != null) {
							NexLog.d(LOG_TAG, "clearCaptionString _EXTERNAL_SUBTITLE");
							mExternalSubtitleTextView.setText("");
						}
					}
				});
				break;
		}
	}

	protected void setRenderingArea( RenderingArea area ) {
		if (area.mVideo != null) {
			switch(mRendererType) {
				case _CEA608 :
					if (null != mCEA608Renderer) {
						mCEA608Renderer.setRenderArea(area.mVideo.left, area.mVideo.top, area.mVideo.width(), area.mVideo.height() );
					}
					break;
				case _CEA708:
					if (null != mEIA708Renderer) {
						mEIA708Renderer.setDisplayArea(area.mVideo.left, area.mVideo.top, area.mVideo.width(), area.mVideo.height() );
					}
					break;
				case _WEBVTT :
					if (null != mCaptionRendererForWebVTT) {
						mCaptionRendererForWebVTT.setVideoSizeInformation(area.mVideo.width(), area.mVideo.height(), area.mView.width(), area.mView.height(), area.mVideo.left, area.mVideo.top);
					}
					break;
				case _TTML :
				case _3GPP :
					if (null != mCaptionRendererForTimedText) {
						setTimedTextRenderingArea(area);
						mCaptionRendererForTimedText.setVideoSizeInformation(area.mVideo.width(), area.mVideo.height(), area.mView.width(), area.mView.height(), area.mVideo.left, area.mVideo.top);
					}
					break;
			}
		}
	}

	private void setTimedTextRenderingArea(RenderingArea area) {
		mCaptionRendererForTimedText.setScaleRatio(area.mRatio);
		mCaptionRendererForTimedText.setTextBoxOnLayout( area.mText );
	}

	protected static class RenderingArea {
		Rect    mVideo;
		Rect    mView;
		Rect    mText;
		float   mRatio;

		RenderingArea() {
			mVideo  = null;
			mView   = null;
			mText   = null;
			mRatio  = 0.0f;
		}
	}

	private void attachCaptionView(final RendererType type, View rendererView, final RenderingArea area, LayoutParams layoutParams) {
		addView(rendererView, layoutParams);
        setRenderingArea(area);
		setCaptionAttribute( mCaptionAttribute );
	}

	protected synchronized void activate(final RendererType type, RendererAttribute params, final RenderingArea area) {
		deactivate();
		mRendererAttribute = params;

		switch(type) {
			case _CEA608 :
				if( mCEA608Renderer == null ) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {

							NexLog.d(LOG_TAG, "activate-mCEA608Renderer");
							mCEA608Renderer = new NexCaptionRenderer(mContext, 10, 10);
							mCEA608Renderer.setMode(mRendererAttribute.mCEA608RenderMode);
							LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
							attachCaptionView(type, mCEA608Renderer, area, param);
						}
					});
				}
				break;

			case _CEA708:
				if( mEIA708Renderer == null ) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							NexLog.d(LOG_TAG, "activate-mEIA708Renderer");
							mEIA708Renderer = new NexEIA708CaptionView(mContext);
							mEIA708CC = new NexEIA708Struct();
							mEIA708Renderer.setEIA708CC(mEIA708CC);
							LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
							attachCaptionView(type, mEIA708Renderer, area, param);
						}
					});
				}
				break;

			case _WEBVTT :
				if( mCaptionRendererForWebVTT == null ) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							NexLog.d(LOG_TAG, "activate-mCaptionRendererForWebVTT");
							mCaptionRendererForWebVTT = new NexCaptionRendererForWebVTT(mContext);
							LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
							attachCaptionView(type, mCaptionRendererForWebVTT, area, param);
						}
					});
				}
				break;

			case _TTML :
			case _3GPP :
				if( mCaptionRendererForTimedText == null ) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							NexLog.d(LOG_TAG, "activate-mCaptionRendererForTimedText");
							mCaptionRendererForTimedText = new NexCaptionRendererForTimedText(mContext);
							LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
							attachCaptionView(type, mCaptionRendererForTimedText, area, param);
						}
					});
				}
				break;

			case _EXTERNAL_SUBTITLE :
				if( mExternalSubtitleTextView == null ) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							NexLog.d(LOG_TAG, "activate-ExternalTextView");
							LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							param.gravity = Gravity.BOTTOM;

							if( mUserTextView != null ) {
								mExternalSubtitleTextView = mUserTextView;

								if( mUserTextViewParam != null ) {
									param = mUserTextViewParam;
								}
							}
							else {
								mExternalSubtitleTextView = new TextView(mContext);
							}
							attachCaptionView(type, mExternalSubtitleTextView, area, param);
						}
					});
				}
				break;
		}
		mRendererType = type;
	}

	protected synchronized void deactivate() {
		if( mCEA608Renderer != null ) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					NexLog.d(LOG_TAG, "CEA608Renderer deactivated");
					removeView(mCEA608Renderer);
					mCEA608Renderer = null;
				}
			});
		}
		if( mEIA708Renderer != null ) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					NexLog.d(LOG_TAG, "EIA708Renderer deactivated");
					removeView(mEIA708Renderer);
					mEIA708Renderer = null;
					mEIA708CC = null;
				}
			});
		}
		if( mCaptionRendererForTimedText != null ) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					NexLog.d(LOG_TAG, "CaptionRendererForTimedText deactivated");
					removeView(mCaptionRendererForTimedText);
					mCaptionRendererForTimedText = null;
				}
			});
		}
		if( mCaptionRendererForWebVTT != null ) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					NexLog.d(LOG_TAG, "CaptionRendererForWebVTT deactivated");
					removeView(mCaptionRendererForWebVTT);
					mCaptionRendererForWebVTT = null;
				}
			});
		}
		if( mExternalSubtitleTextView != null ) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					NexLog.d(LOG_TAG, "SubtitleTextView deactivated");
					removeView(mExternalSubtitleTextView);
					mExternalSubtitleTextView = null;
				}
			});
		}
		mRendererType = RendererType._UNKNWON;
	}

	protected void renderClosedCaption(int captionType, final NexClosedCaption textInfo) {
		if( textInfo != null ) {
			switch( captionType ) {
				case NexClosedCaption.TEXT_TYPE_NTSC_CC_CH1 :
				case NexClosedCaption.TEXT_TYPE_NTSC_CC_CH2:
					renderCEA608( textInfo );
					break;
				case NexClosedCaption.TEXT_TYPE_ATSCMH_CC :
					renderEIA708( textInfo );
					break;
				case NexClosedCaption.TEXT_TYPE_TTML_TIMEDTEXT:
				case NexClosedCaption.TEXT_TYPE_EXTERNAL_TTML:
				case NexClosedCaption.TEXT_TYPE_3GPP_TIMEDTEXT:
					renderTimedText( textInfo );
					break;
				case NexClosedCaption.TEXT_TYPE_WEBVTT:
					renderWebVTT( textInfo );
					break;
				case NexClosedCaption.TEXT_TYPE_SMI :
				case NexClosedCaption.TEXT_TYPE_SRT :
				case NexClosedCaption.TEXT_TYPE_SUB :
					renderSubtitle( textInfo );
					break;
				case NexClosedCaption.TEXT_TYPE_UNKNOWN:
				case NexClosedCaption.TEXT_TYPE_GENERAL:
				case NexClosedCaption.TEXT_TYPE_ATSCMH_BAR:
				case NexClosedCaption.TEXT_TYPE_ATSCMH_AFD:
				default:
					break;
			}
		}
	}

	private void init(Context context) {
		mContext        = context;
		mRendererType   = RendererType._UNKNWON;
		mTextRegionChangedListener  = null;

		mCaptionAttribute = null;
		mRendererAttribute = new RendererAttribute();

		mCEA608Renderer                 = null;
		mEIA708Renderer                 = null;
		mCaptionRendererForTimedText    = null;
		mCaptionRendererForWebVTT       = null;
		mEIA708CC                       = null;
		mExternalSubtitleTextView = null;
		mUserTextView = null;
		mUserTextViewParam = null;
		mEncodingPreset = "UTF-8";
	}

	private void renderCEA608(final NexClosedCaption textInfo) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if( mCEA608Renderer != null ) {
					NexLog.d(LOG_TAG, "renderCEA608");
					mCEA608Renderer.SetData(textInfo);
					mCEA608Renderer.invalidate();
				}
			}
		});
	}

	private void renderEIA708(final NexClosedCaption textInfo) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if ( mEIA708Renderer != null && mEIA708CC != null ) {
					mEIA708Renderer.setEIA708CC(mEIA708CC);
					if (mEIA708CC.SetSourceByteStream(textInfo.mCEA708ServiceNO, textInfo.mCEA708Data, textInfo.mCEA708Len)) {
						NexLog.d(LOG_TAG, "renderEIA708");
						mEIA708Renderer.invalidate();
						mEIA708Renderer.setValidateUpdate(true);
					}
				}
			}
		});
	}

	private void renderWebVTT(final NexClosedCaption textInfo) {
		mHandler.post( new Runnable() {
			@Override
			public void run() {
				if( mCaptionRendererForWebVTT != null ) {
					NexLog.d(LOG_TAG, "renderWebVTT");
					mCaptionRendererForWebVTT.setData( textInfo );
					mCaptionRendererForWebVTT.invalidate();
				}
			}
		});
	}

	private void renderTimedText(final NexClosedCaption textInfo) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if( mCaptionRendererForTimedText != null ) {
					NexLog.d(LOG_TAG, "renderTimedText");
					mCaptionRendererForTimedText.setData(textInfo);
					mCaptionRendererForTimedText.invalidate();

					int[] textRegion = textInfo.getTextboxCoordinatesFor3GPPTT();

					if( textRegion != null && mTextRegionChangedListener != null ) {
						mTextRegionChangedListener.OnTextRegionChanged( textRegion );
					}
				}
			}
		});
	}

	private void renderSubtitle(final NexClosedCaption textInfo) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if( mExternalSubtitleTextView != null ) {
					String strText = null;
					if(textInfo.getTextData() == null) {
						strText = "";
					}
					else {
						try {
							strText = new String( textInfo.getTextData(), 0, textInfo.getTextData().length, (mEncodingPreset != null) ? mEncodingPreset : "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					final String subtitleText = strText;

					NexLog.d(LOG_TAG, "renderSubtitle");
					mExternalSubtitleTextView.setText(subtitleText);
				}
			}
		});
	}

	private void setFontEdgeCEA608(NexCaptionAttribute attribute) {
		switch ( attribute.mEdgeStyle) {
			case NONE:
				mCEA608Renderer.resetEdgeEffect();
				break;
			case DROP_SHADOW:
				mCEA608Renderer.setShadowWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case DEPRESSED:
				mCEA608Renderer.setDepressedWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case RAISED:
				mCEA608Renderer.setRaiseWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case UNIFORM:
				mCEA608Renderer.setCaptionStroke(attribute.mEdgeColor, attribute.mStrokeWidth);
				break;
		}
	}

	private void setFontEdgeEIA708(NexCaptionAttribute attribute) {
		switch ( attribute.mEdgeStyle) {
			case NONE:
				mEIA708Renderer.resetEdgeEffect();
				break;
			case DROP_SHADOW:
				mEIA708Renderer.setShadowWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case DEPRESSED:
				mEIA708Renderer.setDepressedWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case RAISED:
				mEIA708Renderer.setRaiseWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case UNIFORM:
				mEIA708Renderer.setCaptionStroke(attribute.mEdgeColor, attribute.mStrokeWidth);
				break;
		}
	}

	private void setFontEdgeWebVTT(NexCaptionAttribute attribute) {
		switch ( attribute.mEdgeStyle) {
			case NONE:
				mCaptionRendererForWebVTT.resetEdgeEffect();
				break;
			case DROP_SHADOW:
				mCaptionRendererForWebVTT.setShadowWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case DEPRESSED:
				mCaptionRendererForWebVTT.setDepressedWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case RAISED:
				mCaptionRendererForWebVTT.setRaisedWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case UNIFORM:
				mCaptionRendererForWebVTT.setCaptionStroke(attribute.mEdgeColor, attribute.mEdgeOpacity, attribute.mStrokeWidth);
				break;
		}
	}

	private void setFontEdgeTTML(NexCaptionAttribute attribute) {
		switch ( attribute.mEdgeStyle) {
			case NONE:
				mCaptionRendererForTimedText.resetEdgeEffect();
				break;
			case DROP_SHADOW:
				mCaptionRendererForTimedText.setShadowWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case DEPRESSED:
				mCaptionRendererForTimedText.setDepressedWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case RAISED:
				mCaptionRendererForTimedText.setRaiseWithColor(true, attribute.mEdgeColor, attribute.mEdgeOpacity);
				break;
			case UNIFORM:
				mCaptionRendererForTimedText.setCaptionStroke(attribute.mEdgeColor, attribute.mEdgeOpacity, attribute.mStrokeWidth);
				break;
		}
	}

	private void setFontEdge(NexCaptionAttribute attribute) {
		switch(mRendererType) {
			case _CEA608 :
				setFontEdgeCEA608(attribute);
				break;
			case _CEA708:
				setFontEdgeEIA708(attribute);
				break;
			case _WEBVTT :
				setFontEdgeWebVTT(attribute);
				break;
			case _TTML :
			case _3GPP :
				setFontEdgeTTML(attribute);
				break;
		}
	}

	private void setBackgroundColor(NexCaptionAttribute attribute) {
		switch(mRendererType) {
			case _CEA608 :
				mCEA608Renderer.setBGCaptionColor(attribute.mBackGroundColor, attribute.mBackgroundOpacity);
				break;
			case _CEA708:
				mEIA708Renderer.setBGCaptionColor(attribute.mBackGroundColor, attribute.mBackgroundOpacity);
				break;
			case _WEBVTT :
				mCaptionRendererForWebVTT.setBGCaptionColor(attribute.mBackGroundColor, attribute.mBackgroundOpacity);
				break;
			case _TTML :
			case _3GPP :
				mCaptionRendererForTimedText.setBGCaptionColor(attribute.mBackGroundColor, attribute.mBackgroundOpacity);
				break;
		}
	}

	private void setWindowColor(NexCaptionAttribute attribute) {
		switch(mRendererType) {
			case _CEA608 :
				mCEA608Renderer.setWindowColor(attribute.mWindowColor, attribute.mWindowOpacity);
				break;
			case _CEA708:
				mEIA708Renderer.setCaptionWindowColor(attribute.mWindowColor, attribute.mWindowOpacity);
				break;
			case _WEBVTT :
				mCaptionRendererForWebVTT.setCaptionWindowColor(attribute.mWindowColor, attribute.mWindowOpacity);
				break;
			case _TTML :
			case _3GPP :
				mCaptionRendererForTimedText.setCaptionWindowColor(attribute.mWindowColor, attribute.mWindowOpacity);
				break;
		}
	}

	private void setFontColorAndSize(NexCaptionAttribute attribute) {
		switch(mRendererType) {
			case _CEA608 :
				mCEA608Renderer.setFGCaptionColor(attribute.mFontColor, attribute.mFontOpacity);
				mCEA608Renderer.changeTextSize((int)(attribute.mScaleFactor*100));
				break;
			case _CEA708:
				mEIA708Renderer.setFGCaptionColor(attribute.mFontColor, attribute.mFontOpacity);
				mEIA708Renderer.changeFontSize((int)(attribute.mScaleFactor*100));
				break;
			case _WEBVTT :
				mCaptionRendererForWebVTT.setFGCaptionColor(attribute.mFontColor, attribute.mFontOpacity);
				mCaptionRendererForWebVTT.setTextSize((int)(attribute.mScaleFactor*100));
				break;
			case _TTML :
			case _3GPP :
				mCaptionRendererForTimedText.setFGCaptionColor(attribute.mFontColor, attribute.mFontOpacity);
				mCaptionRendererForTimedText.setFontSize(attribute.mScaleFactor*100);
				break;
			case _EXTERNAL_SUBTITLE :
				break;
		}
	}
}