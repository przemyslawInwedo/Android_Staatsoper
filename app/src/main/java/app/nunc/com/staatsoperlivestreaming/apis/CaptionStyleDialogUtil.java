package app.nunc.com.staatsoperlivestreaming.apis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.R.id;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionPreview;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption.CaptionColor;



public class CaptionStyleDialogUtil {
	private static final String LOG_TAG = "CaptionStyleDialogUtil";
	
	private final static String[] CAPTION_TEXT_FONT_SIZE = {"Small", "Medium(Default)", "Large", "Extra Large"};
	private final static String[] CAPTION_TEXT_FONT_COLOR ={"WHITE", "GREEN", "BLUE", "CYAN", "RED", "YELLOW", "MAGENTA", "BLACK"};
	private final static String[] CAPTION_TEXT_FONT_COLOR_TranseParent ={"WHITE", "GREEN", "BLUE", "CYAN", "RED", "YELLOW", "MAGENTA", "BLACK","TRANSEPARENT"};
	private final static String[] CAPTION_BACKGROUND_OPACITY = {"50","100","75","25","0"};
	private final static String[] CAPTION_SHADOW_OPACITY = {"255","128"};
	
	private final static String[] CAPTION_TEXT_OPACITY = {"opaque","semi_transparent"};
	private final static String[] CAPTION_TEXT_EDGE = {"None","Drop Shadow","Raised","Depressed","Uniform" };
	private final int EDGE_OPACITY_DEFAULT = 255;
		
	private IListener mListener = null;

    private NexCaptionPreview mCaptionPreviewer = null;
	private CaptionSettings mCaptionSettings = new CaptionSettings();

	public void restoreCaptionPreview(CaptionSettings captionSettings) {

		// Font Attributes
		mCaptionPreviewer.changeFontSize(captionSettings.mNCaptionFontSize);
		mCaptionPreviewer.setFGCaptionColor(captionSettings.mFontColor, captionSettings.mCaptionTextOpacity);

		if (captionSettings.mFontedge >= 1 && captionSettings.mFontedge <= 4) {
			changePreviewEdgeColor(captionSettings);
		}

		changePreviewEdgeStyle(captionSettings);

		//The others Attributes

		if (captionSettings.mBackGroundColor == CaptionColor.TRANSPARENT)
			captionSettings.mCaptionBackGroundOpacity = 0;

		mCaptionPreviewer.setBGCaptionColor(captionSettings.mBackGroundColor, captionSettings.mCaptionBackGroundOpacity);
		mCaptionPreviewer.setCaptionWindowColor(captionSettings.mWindowColor, captionSettings.mWindowOpacity);

		mCaptionPreviewer.postInvalidate();
	}

	public static enum BUTTON {
		SAVE, RESET, CANCEL
	};
	
	public interface IListener {
		public void didClickButton(BUTTON button, AlertDialog dialog);
		public void didChangeCaptionFontColor(CaptionColor fontColor);
		public void didChangeFontOpacity(int fontOpacity);
		public void canceledDialog();

	}
	
	public CaptionStyleDialogUtil() {
	}

	private void setupSaveButton(final AlertDialog dialog) {
		Log.d(LOG_TAG, " setupSaveButton is called.. ");
		final Button saveButton = (Button)dialog.findViewById(id.save_button);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( mListener != null ) {
					mListener.didClickButton(BUTTON.SAVE, dialog);
				}
			}
		});
	}
	
	private void setupResetButton(final Context context, final AlertDialog dialog) {
		final Button resetButton = (Button)dialog.findViewById(R.id.reset_button);
		resetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( mListener != null ) {
					mListener.didClickButton(BUTTON.RESET, dialog);

					Log.d(LOG_TAG, " restAllValue is called.. ");
					mCaptionSettings.resetValuesToDefault();		
					 
					// font 
					setCaptionFont(context, dialog);

					// background 
					setCaptionBackGround(context, dialog);

					//window
					setCaptionWindow(context, dialog);						
				}
			}
		});
	}	

	private void setUpCancelButton(final AlertDialog dialog) {
		final Button cancelButton = (Button)dialog.findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.didClickButton(BUTTON.CANCEL, dialog);
				}
			}
		});
	}	
	
	private void setCaptionPreview(AlertDialog dialog)
	{
		mCaptionPreviewer = (NexCaptionPreview) dialog.findViewById(R.id.caption_preview);
		mCaptionPreviewer.setPreviewText("Caption Preview. Lorem lpsum Lorem lpsum", 16);
		mCaptionPreviewer.setPreviewTextAlign(NexCaptionPreview.PTEXT_ALIGN_HORIZONTAL_CENTER, NexCaptionPreview.PTEXT_ALIGN_VERTICAL_MIDDLE);
		mCaptionPreviewer.postInvalidate();		
	}
	
	private void setCaptionFont(Context context, AlertDialog dialog) {
		
		// font Size
		setCaptionFontSize(context, dialog);
		
		// font color
		setCaptionFontColor(context, dialog);
		
		// font opacity
		setCaptionFontOpacity(context, dialog);
		
		setCaptionShadowColor(context, dialog);

		// shadow Color Test UI.
		setCaptionShadowOpacity(context, dialog);
		// font edge
		setCaptionFontEdgeAttributes(context, dialog);
	}
		
	private void setCaptionFontSize(Context context, AlertDialog dialog){
		ArrayAdapter<String> fontSizeAdapter = new ArrayAdapter<String>(context,
				R.layout.simple_spinner_item, R.id.textview, CAPTION_TEXT_FONT_SIZE);
		final Spinner fontsizeSpinner = (Spinner)dialog.findViewById(R.id.text_font_spinner_size);
		fontsizeSpinner.setAdapter(fontSizeAdapter);
		fontsizeSpinner.setSelection(mCaptionSettings.mFontsize);
		fontsizeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
				
				int size = 0;
				size = captionChoice(position);
				
				mCaptionSettings.mNCaptionFontSize = size;
				mCaptionSettings.mFontsize = position;
				
				mCaptionPreviewer.changeFontSize(size);
				mCaptionPreviewer.postInvalidate();				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}

	private void setCaptionFontColor(Context context, AlertDialog dialog){
		ArrayAdapter<String> fontColorAdapter = new ArrayAdapter<String>(context,
				R.layout.simple_spinner_item, R.id.textview,CAPTION_TEXT_FONT_COLOR);
			final Spinner fontColorSpinner = (Spinner)dialog.findViewById(R.id.text_font_spinner_color);
			fontColorSpinner.setAdapter(fontColorAdapter);
			fontColorSpinner.setSelection(mCaptionSettings.mFontColorIndex);
			fontColorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
				CaptionColor fontColor = captionColor(position);
				if ( mListener != null ) {
					mListener.didChangeCaptionFontColor(fontColor);
				}
				mCaptionSettings.mFontColor = fontColor; 
				mCaptionSettings.mFontColorIndex = position;
				mCaptionPreviewer.setFGCaptionColor(fontColor, mCaptionSettings.mCaptionTextOpacity);
				mCaptionPreviewer.postInvalidate();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}
			
	private void setCaptionFontOpacity(Context context, AlertDialog dialog) {
		ArrayAdapter<String> fontOpacityAdapter = new ArrayAdapter<String>(context,
				R.layout.simple_spinner_item, R.id.textview,CAPTION_TEXT_OPACITY);
		final Spinner fontOpacitySpinner = (Spinner)dialog.findViewById(R.id.text_font_spinner_opacity);
		fontOpacitySpinner.setAdapter(fontOpacityAdapter);
		fontOpacitySpinner.setSelection(mCaptionSettings.mFontopacity);
		fontOpacitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
				if(position == 0)
				{
					mCaptionSettings.mCaptionTextOpacity = 255;
				}else
				{
					mCaptionSettings.mCaptionTextOpacity = 128;
				}
				
				if ( mListener != null ) {
					mListener.didChangeFontOpacity(mCaptionSettings.mCaptionTextOpacity);
				}
				
				mCaptionSettings.mFontopacity = position;
				
				mCaptionPreviewer.setFGCaptionColor(mCaptionSettings.mFontColor, mCaptionSettings.mCaptionTextOpacity);
				mCaptionPreviewer.postInvalidate();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}	
	
	private void setCaptionShadowColor(Context context, AlertDialog dialog) {
		ArrayAdapter<String> fontShadowAdapter = new ArrayAdapter<String>(context,
				R.layout.simple_spinner_item, R.id.textview, CAPTION_TEXT_FONT_COLOR);
		final Spinner fontShadowColorSpinner = (Spinner)dialog.findViewById(R.id.text_font_spinner_shadow_color);
		fontShadowColorSpinner.setAdapter(fontShadowAdapter);
		fontShadowColorSpinner.setSelection(mCaptionSettings.mFontShadowColorIndex);
		fontShadowColorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
				
				mCaptionSettings.mFontShadowColorIndex = position;
				mCaptionSettings.mFontShadowColor = captionColor(position);

				if(mCaptionSettings.mFontedge >= 1 &&  mCaptionSettings.mFontedge <= 4) {
					changePreviewEdgeColor(mCaptionSettings);
					mCaptionPreviewer.postInvalidate();
				}

			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}

	private void changePreviewEdgeColor(CaptionSettings settings) {
		Log.d(LOG_TAG,"changePreviewEdgeColor " + settings.mFontedge);

		switch( settings.mFontedge ) {
        case 1:
            mCaptionPreviewer.setShadow(true, settings.mFontShadowColor, settings.mFontShadowOpacity);
            break;

        case 2:
            mCaptionPreviewer.setRaisedWithColor(true, settings.mFontShadowColor, EDGE_OPACITY_DEFAULT);
            break;

        case 3:
            mCaptionPreviewer.setDepressedWithColor(true, settings.mFontShadowColor, EDGE_OPACITY_DEFAULT);
            break;

        case 4:
            mCaptionPreviewer.setCaptionStroke(settings.mFontShadowColor, EDGE_OPACITY_DEFAULT, 1.0f);
            break;
            default:
                break;
        }
	}

	private void setCaptionShadowOpacity(Context context, AlertDialog dialog) {
		ArrayAdapter<String> fontShadowOpacityAdapter = new ArrayAdapter<String>(context,
				R.layout.simple_spinner_item, R.id.textview,CAPTION_SHADOW_OPACITY);
		final Spinner fontShadowOpacitySpinner = (Spinner)dialog.findViewById(R.id.text_font_spinner_shadow_opacity);
		fontShadowOpacitySpinner.setAdapter(fontShadowOpacityAdapter);
		fontShadowOpacitySpinner.setSelection(mCaptionSettings.mOpacity);
		fontShadowOpacitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
				mCaptionSettings.mOpacity = position;
				setShadowOpacity(position);
				if (1 == mCaptionSettings.mFontedge) {
					mCaptionPreviewer.setShadow(true, mCaptionSettings.mFontShadowColor, mCaptionSettings.mFontShadowOpacity);
				}
				mCaptionPreviewer.postInvalidate();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}
	
	private void setCaptionFontEdgeAttributes(Context context, AlertDialog dialog) {
		
		ArrayAdapter<String> fontEdgeAdapter = new ArrayAdapter<String>(context,
				R.layout.simple_spinner_item, R.id.textview,CAPTION_TEXT_EDGE);

		final Spinner fontEdgeSpinner = (Spinner)dialog.findViewById(R.id.text_font_spinner_edge);
		final RelativeLayout fontShadowOpacityLayout = (RelativeLayout)dialog.findViewById(R.id.text_shadow_style_opacity);
		final int DROP_SHADOW = 1;

		fontEdgeSpinner.setAdapter(fontEdgeAdapter);
		fontEdgeSpinner.setSelection(mCaptionSettings.mFontedge);
		fontEdgeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
				Log.d(LOG_TAG, "onItemSelected is called.. " + mCaptionSettings.mNCaptionFontStyle );
				int visibility = View.GONE;
				if( position == DROP_SHADOW )
					visibility = View.VISIBLE;
				fontShadowOpacityLayout.setVisibility(visibility);

				mCaptionSettings.mFontedge = position;
				mCaptionSettings.mNCaptionFontStyle = position;
				changePreviewEdgeStyle(mCaptionSettings);
				mCaptionPreviewer.postInvalidate();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
	}
		
	private void setCaptionBackGround(Context context, AlertDialog dialog) {
		setCaptionBackGroundColor(context, dialog);
		setCaptionBackGroundOpacity(context, dialog);
	}
		
	private void setCaptionWindow(Context context, AlertDialog dialog) {
		setCaptionWindowColor(context, dialog);
		setCaptionWindowOpacity(context, dialog);
		
	}	
	
	private void setCaptionBackGroundColor(Context context, AlertDialog dialog) {
		
		ArrayAdapter<String> backgroundColorAdapter = new ArrayAdapter<String>(context,
				R.layout.simple_spinner_item, R.id.textview,CAPTION_TEXT_FONT_COLOR_TranseParent);
			final Spinner backgroundColorSpinner = (Spinner)dialog.findViewById(R.id.background_color_spinner);
			backgroundColorSpinner.setAdapter(backgroundColorAdapter);
			backgroundColorSpinner.setSelection(mCaptionSettings.mBackgroundColorIndex);
			backgroundColorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
				
				mCaptionSettings.mBackGroundColor = captionColor(position);
				mCaptionSettings.mNCaptionBackGround = position;
				mCaptionSettings.mBackgroundColorIndex = position;
				// background color == transparent
				int opacity = mCaptionSettings.mCaptionBackGroundOpacity;
				if(position == CaptionSettings.CaptionBackgroundColorIndexTransparent) {
					opacity = 0;
				}
				mCaptionPreviewer.setBGCaptionColor(mCaptionSettings.mBackGroundColor, opacity);
				mCaptionPreviewer.postInvalidate();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
	}	

	private void setCaptionBackGroundOpacity(Context context, AlertDialog dialog) {
		ArrayAdapter<String> opacityAdapter = new ArrayAdapter<String>(context,
				R.layout.simple_spinner_item, R.id.textview,CAPTION_BACKGROUND_OPACITY);
			final Spinner opacitySpinner = (Spinner)dialog.findViewById(R.id.text_backgr_opacity_spinner);
			opacitySpinner.setAdapter(opacityAdapter);
			opacitySpinner.setSelection(mCaptionSettings.mBackGroundOpacity);
			opacitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
				setOpacity(position);
				mCaptionSettings.mBackGroundOpacity = position;
				int opacity = mCaptionSettings.mCaptionBackGroundOpacity;
				if(mCaptionSettings.mBackgroundColorIndex == CaptionSettings.CaptionBackgroundColorIndexTransparent) {
					opacity = 0;
				}
				mCaptionPreviewer.setBGCaptionColor(mCaptionSettings.mBackGroundColor,opacity);
				mCaptionPreviewer.postInvalidate();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}
	
	private void setCaptionWindowColor(Context context, AlertDialog dialog) {
		
		ArrayAdapter<String> windowColorAdapter = new ArrayAdapter<String>(context,
				R.layout.simple_spinner_item, R.id.textview,CAPTION_TEXT_FONT_COLOR);
			final Spinner windowColorSpinner = (Spinner)dialog.findViewById(R.id.window_color_spinner);
			windowColorSpinner.setAdapter(windowColorAdapter);
			windowColorSpinner.setSelection(mCaptionSettings.mWindowColorIndex);
			windowColorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
				
				mCaptionSettings.mWindowColor = captionColor(position);
				mCaptionSettings.mWindowColorIndex = position;
				mCaptionPreviewer.setCaptionWindowColor(mCaptionSettings.mWindowColor, mCaptionSettings.mWindowOpacity);
				mCaptionPreviewer.postInvalidate();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
	}
	
	private void setCaptionWindowOpacity(Context context, AlertDialog dialog) {
		ArrayAdapter<String> windowOpacityAdapter = new ArrayAdapter<String>(context,
				R.layout.simple_spinner_item, R.id.textview,CAPTION_BACKGROUND_OPACITY);
			final Spinner windowopacitySpinner = (Spinner)dialog.findViewById(R.id.windowr_opacity_spinner);
			windowopacitySpinner.setAdapter(windowOpacityAdapter);
			windowopacitySpinner.setSelection(mCaptionSettings.mWindowOpacityIndex);
			windowopacitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
				setWindowOpacity(position);
				mCaptionSettings.mWindowOpacityIndex = position;
				mCaptionPreviewer.setCaptionWindowColor(mCaptionSettings.mWindowColor, mCaptionSettings.mWindowOpacity);
				mCaptionPreviewer.postInvalidate();
			
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
	}

	private void setOpacity(int position) {
		
		switch (position) {
		case 0:
			mCaptionSettings.mCaptionBackGroundOpacity = 128 ;
			break;
		case 1:
			mCaptionSettings.mCaptionBackGroundOpacity = 255 ;
			break;
		case 2:
			mCaptionSettings.mCaptionBackGroundOpacity = 192 ;
			break;
		case 3:
			mCaptionSettings.mCaptionBackGroundOpacity = 64 ;
			break;
		case 4:
			mCaptionSettings.mCaptionBackGroundOpacity = 0 ;
			break;
		}
		
	}
	
	private void setWindowOpacity(int position) {
		
		switch (position) {
		case 0:
			mCaptionSettings.mWindowOpacity = 128 ;
			break;
		case 1:
			mCaptionSettings.mWindowOpacity = 255 ;
			break;
		case 2:
			mCaptionSettings.mWindowOpacity = 192 ;
			break;
		case 3:
			mCaptionSettings.mWindowOpacity = 64 ;
			break;
		case 4:
			mCaptionSettings.mWindowOpacity = 0 ;
			break;
		}
		
	}
	
	private int captionChoice(int position) {
		int textsize = 100;
		switch (position) {
		case 0:
			// small
			textsize = 50;
			break;
		case 1:
			// medium
			textsize = 100;
			
			break;
		case 2:
			// Large
			textsize = 150;
			break;
			
		case 3:
			// Extra Large
			textsize = 200;
			break;
		}
		
		return textsize ;
	}
	
	private CaptionColor captionColor(int position) {
		CaptionColor fgColor = CaptionColor.TRANSPARENT;
			switch (position) {
			case 0:
				fgColor = CaptionColor.WHITE;
				break;
			
			case 1:
				fgColor = CaptionColor.GREEN;
				break;
				
			case 2:
				fgColor = CaptionColor.BLUE;
				break;
				
			case 3:
				fgColor = CaptionColor.CYAN;
				break;
	
			case 4:						
				fgColor = CaptionColor.RED;
				break;
	
			case 5:
				fgColor = CaptionColor.YELLOW;
				break;
	
			case 6:
				fgColor = CaptionColor.MAGENTA;
				break;
			
			case 7:
				fgColor = CaptionColor.BLACK;
				break;
			case 8:
				fgColor = CaptionColor.TRANSPARENT;
				
			}
			
		return fgColor;
	}	
	
	private void changePreviewEdgeStyle(CaptionSettings settings) {
		//changePreviewEdgeStyle(captionSettings.mNCaptionFontStyle);
		switch (settings.mNCaptionFontStyle) {
		//none
		case 0:
			mCaptionPreviewer.resetEdgeStyle();
			break;
		// shadow		
		case 1:
			mCaptionPreviewer.setShadow(true, settings.mFontShadowColor, settings.mFontShadowOpacity);
			break;
		//raised
		case 2:
//			mCaptionPreviewer.setRaise(true);
			mCaptionPreviewer.setRaisedWithColor(true, settings.mFontShadowColor, EDGE_OPACITY_DEFAULT);
			break;
		//depressed	
		case 3:
//			mCaptionPreviewer.setDepressed(true);
			mCaptionPreviewer.setDepressedWithColor(true, settings.mFontShadowColor, EDGE_OPACITY_DEFAULT);
			break;
		//uniform	
		case 4:
			//mCaptionPreviewer.setUniform(true);
			mCaptionPreviewer.setCaptionStroke(settings.mFontShadowColor, EDGE_OPACITY_DEFAULT, 1.0f);
			break;
		}
		
	}
	
	private void setShadowOpacity(int position)
	{
		switch (position) {
		case 0:
			mCaptionSettings.mFontShadowOpacity = 255;
			break;
		case 1:
			mCaptionSettings.mFontShadowOpacity = 128;
			break;
		}
		
	}
	
	public void setCaptionSettings(CaptionSettings captionSettings) {
		mCaptionSettings = new CaptionSettings(captionSettings);
	}
		
	public CaptionSettings getCaptionSettings() {
		return new CaptionSettings(mCaptionSettings);
	}
	
	public void setListener(IListener listener) {
		mListener = listener;
	}

	public void prepareCaptionDialog(Context context, AlertDialog dialog) {
		//close Dialog

		setupCancelListener(dialog);
		setupSaveButton(dialog);
		setupResetButton(context, dialog);
		setUpCancelButton(dialog);
		
		//preview string
		setCaptionPreview(dialog);
		
		// font 
		setCaptionFont(context, dialog);
		
		// background 
		setCaptionBackGround(context, dialog);
		
		//window
		setCaptionWindow(context, dialog);


	}

	private void setupCancelListener(AlertDialog dialog) {
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				mListener.canceledDialog();
			}
		});
	}

	public AlertDialog createDialog(Context context, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		View view = View.inflate(context, R.layout.popup_dialog_caption_layout, null);
		AlertDialog dialog = builder.setView(view).create();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		dialog.getWindow().setAttributes(params);
		
		return dialog;
	}
		
	public static int spToPx( float sp, DisplayMetrics displayMetrics ) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, displayMetrics);
	}	
	
}
