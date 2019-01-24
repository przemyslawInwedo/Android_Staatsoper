package app.nunc.com.staatsoperlivestreaming.apis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionAttribute;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionPreview;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption;

/**
 * Created by bonnie.kyeon on 2015-10-15.
 */
public class CaptionRenderViewSettingsDialog {

	private AlertDialog mDialog = null;
	private Context mContext = null;
	private NexCaptionAttribute mSettings = null;
	private OnSettingsChangedListener mListener = null;

	public interface OnSettingsChangedListener {
		void onSettingsChanged(NexCaptionAttribute settings);
	}

	public CaptionRenderViewSettingsDialog(Context context, OnSettingsChangedListener listener) {
		mContext = context;
		mListener = listener;
	}

	public boolean isShowing() {
		return mDialog != null && mDialog.isShowing();
	}

	private AlertDialog createDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.caption_style);

		final AlertDialog dialog = builder.setView(createView(context)).create();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		dialog.getWindow().setAttributes(params);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
				int action = keyEvent.getAction();

				if (action == KeyEvent.KEYCODE_BACK) {
					if (dialog.isShowing()) {
						dialog.dismiss();
						return true;
					}
				}
				return false;
			}
		});


		return dialog;
	}

	public void createAndShow(NexCaptionAttribute settings) {
		mSettings = new NexCaptionAttribute(settings);

		if( mDialog == null ) {
			mDialog = createDialog(mContext);
		}
		mDialog.show();
		setupUIComponents(mDialog, mSettings);
	}

	private void setupUIComponents(final AlertDialog dialog, final NexCaptionAttribute settings) {
		final NexCaptionPreview preview = (NexCaptionPreview) dialog.findViewById(R.id.caption_preview);
		final TextView curFontSizeTextView = (TextView) dialog.findViewById(R.id.cur_font_size);
		final TextView curFontOpacityTextView = (TextView) dialog.findViewById(R.id.cur_font_opacity);
		final TextView curEdgeOpacityTextView = (TextView) dialog.findViewById(R.id.cur_shadow_opacity);
		final TextView curBGOpacityTextView = (TextView) dialog.findViewById(R.id.cur_bg_opacity);
		final TextView curWindowOpacityTextView = (TextView) dialog.findViewById(R.id.cur_window_opacity);
		final SeekBar fontSizeSeekBar = (SeekBar) dialog.findViewById(R.id.font_size_seek_bar);
		final SeekBar fontOpacitySeekBar = (SeekBar) dialog.findViewById(R.id.font_opacity_seek_bar);
		final SeekBar edgeOpacitySeekBar = (SeekBar) dialog.findViewById(R.id.shadow_opacity_seek_bar);
		final SeekBar bgOpacitySeekBar = (SeekBar) dialog.findViewById(R.id.bg_opacity_seek_bar);
		final SeekBar windowOpacitySeekBar = (SeekBar) dialog.findViewById(R.id.window_opacity_seek_bar);
		final Spinner fontColorSpinner = (Spinner) dialog.findViewById(R.id.font_color_spinner);
		final Spinner edgeColorSpinner = (Spinner) dialog.findViewById(R.id.shadow_color_spinner);
		final Spinner bgColorSpinner = (Spinner) dialog.findViewById(R.id.bg_color_spinner);
		final Spinner windowColorSpinner = (Spinner) dialog.findViewById(R.id.window_color_spinner);
		final Spinner fontEdgeSpinner = (Spinner) dialog.findViewById(R.id.font_edge_attributes_spinner);
		final LinearLayout edgeLayout = (LinearLayout) dialog.findViewById(R.id.edge_settings_layout);
		Button resetButton = (Button) dialog.findViewById(R.id.reset_button);
		Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
		Button saveButton = (Button) dialog.findViewById(R.id.save_button);
		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSettings = new NexCaptionAttribute();
				setupUIComponents(dialog, mSettings);
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				if ( mListener != null )
					mListener.onSettingsChanged(mSettings);
			}
		});

		preview.setPreviewText("Caption Preview.", 16);
		preview.setPreviewTextAlign(NexCaptionPreview.PTEXT_ALIGN_HORIZONTAL_CENTER, NexCaptionPreview.PTEXT_ALIGN_VERTICAL_MIDDLE);
		preview.postInvalidate();

		float fontSize = (Float)settings.getValue(NexCaptionAttribute.FLOAT_SCALE_FACTOR)*100;
		int fontOpacity = (Integer)settings.getValue(NexCaptionAttribute.OPACITY_FONT);
		int fontEdgeOpacity = (Integer)settings.getValue(NexCaptionAttribute.OPACITY_EDGE);
		int bgOpacity = (Integer)settings.getValue(NexCaptionAttribute.OPACITY_BACKGROUND);
		int windowOpacity = (Integer)settings.getValue(NexCaptionAttribute.OPACITY_WINDOW);

		curFontSizeTextView.setText(String.valueOf(fontSize));
		curFontOpacityTextView.setText(String.valueOf(fontOpacity));
		curEdgeOpacityTextView.setText(String.valueOf(fontEdgeOpacity));
		curBGOpacityTextView.setText(String.valueOf(bgOpacity));
		curWindowOpacityTextView.setText(String.valueOf(windowOpacity));
		fontSizeSeekBar.setProgress((int)(fontSize / 50) - 1);
		fontOpacitySeekBar.setProgress(fontOpacity);
		edgeOpacitySeekBar.setProgress(fontEdgeOpacity);
		bgOpacitySeekBar.setProgress(bgOpacity);
		windowOpacitySeekBar.setProgress(windowOpacity);

		ArrayAdapter<NexClosedCaption.CaptionColor> colorAdapter = getColorAdapter(mContext);
		ArrayAdapter<NexCaptionAttribute.EdgeStyle> fontEdgeAdapter = getFontEdgeAdater(mContext);

		fontColorSpinner.setAdapter(colorAdapter);
		edgeColorSpinner.setAdapter(colorAdapter);
		bgColorSpinner.setAdapter(colorAdapter);
		windowColorSpinner.setAdapter(colorAdapter);
		fontEdgeSpinner.setAdapter(fontEdgeAdapter);

		NexClosedCaption.CaptionColor fontColor = getCaptionColorAttribute(settings, NexCaptionAttribute.COLOR_FONT);
		NexClosedCaption.CaptionColor fontEdgeColor = getCaptionColorAttribute(settings, NexCaptionAttribute.COLOR_EDGE);
		NexClosedCaption.CaptionColor bgColor = getCaptionColorAttribute(settings, NexCaptionAttribute.COLOR_BACKGROUND);
		NexClosedCaption.CaptionColor windowColor = getCaptionColorAttribute(settings, NexCaptionAttribute.COLOR_WINDOW);
		NexCaptionAttribute.EdgeStyle edgeStyle = getFontEdgeAttribute(settings, NexCaptionAttribute.EDGE_STYLE);

		fontColorSpinner.setSelection(colorAdapter.getPosition(fontColor));
		edgeColorSpinner.setSelection(colorAdapter.getPosition(fontEdgeColor));
		bgColorSpinner.setSelection(colorAdapter.getPosition(bgColor));
		windowColorSpinner.setSelection(colorAdapter.getPosition(windowColor));
		fontEdgeSpinner.setSelection(fontEdgeAdapter.getPosition(edgeStyle));

		fontColorSpinner.setOnItemSelectedListener(new SpinnerListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				NexClosedCaption.CaptionColor color = (NexClosedCaption.CaptionColor) fontColorSpinner.getSelectedItem();
				settings.setValue(NexCaptionAttribute.COLOR_FONT, color);

				preview.setFGCaptionColor(color, (Integer) settings.getValue(NexCaptionAttribute.OPACITY_FONT));
				preview.postInvalidate();
			}
		});
		edgeColorSpinner.setOnItemSelectedListener(new SpinnerListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				NexClosedCaption.CaptionColor color = (NexClosedCaption.CaptionColor) edgeColorSpinner.getSelectedItem();
				settings.setValue(NexCaptionAttribute.COLOR_EDGE, color);

				changePreviewFontStyle(preview,
						getFontEdgeAttribute(settings, NexCaptionAttribute.EDGE_STYLE),
						color,
						(Integer) settings.getValue(NexCaptionAttribute.OPACITY_EDGE));
				preview.postInvalidate();
			}
		});
		bgColorSpinner.setOnItemSelectedListener(new SpinnerListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				NexClosedCaption.CaptionColor color = (NexClosedCaption.CaptionColor) bgColorSpinner.getSelectedItem();
				settings.setValue(NexCaptionAttribute.COLOR_BACKGROUND, color);

				preview.setBGCaptionColor(color, (Integer)settings.getValue(NexCaptionAttribute.OPACITY_BACKGROUND));
				preview.postInvalidate();
			}
		});
		windowColorSpinner.setOnItemSelectedListener(new SpinnerListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				NexClosedCaption.CaptionColor color = (NexClosedCaption.CaptionColor) windowColorSpinner.getSelectedItem();
				settings.setValue(NexCaptionAttribute.COLOR_WINDOW, color);

				preview.setCaptionWindowColor(color, (Integer) settings.getValue(NexCaptionAttribute.OPACITY_WINDOW));
				preview.postInvalidate();
			}
		});
		fontEdgeSpinner.setOnItemSelectedListener(new SpinnerListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				NexCaptionAttribute.EdgeStyle selectedItem = (NexCaptionAttribute.EdgeStyle) fontEdgeSpinner.getItemAtPosition(position);
				boolean isVisible = !selectedItem.equals(NexCaptionAttribute.EdgeStyle.NONE);
				changeViewVisibility(edgeLayout, isVisible);

				settings.setValue(NexCaptionAttribute.EDGE_STYLE, selectedItem);
				changePreviewFontStyle(preview, selectedItem,
						getCaptionColorAttribute(settings, NexCaptionAttribute.COLOR_EDGE),
						(Integer) settings.getValue(NexCaptionAttribute.OPACITY_EDGE));
				preview.postInvalidate();
			}
		});
		fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBarListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int size = (progress * 50) + 50;
				settings.setValue(NexCaptionAttribute.FLOAT_SCALE_FACTOR, (float)size/100);
				curFontSizeTextView.setText(String.valueOf(size));

				preview.changeFontSize(size);
				preview.postInvalidate();
			}
		});
		fontOpacitySeekBar.setOnSeekBarChangeListener(new SeekBarListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				settings.setValue(NexCaptionAttribute.OPACITY_FONT, progress);
				curFontOpacityTextView.setText(String.valueOf(progress));

				preview.setFGCaptionColor(getCaptionColorAttribute(settings, NexCaptionAttribute.COLOR_FONT), progress);
				preview.postInvalidate();
			}
		});
		edgeOpacitySeekBar.setOnSeekBarChangeListener(new SeekBarListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				settings.setValue(NexCaptionAttribute.OPACITY_EDGE, progress);
				curEdgeOpacityTextView.setText(String.valueOf(progress));

				changePreviewFontStyle(preview,
						getFontEdgeAttribute(settings, NexCaptionAttribute.EDGE_STYLE),
						getCaptionColorAttribute(settings, NexCaptionAttribute.COLOR_EDGE), progress);
				preview.postInvalidate();
			}
		});
		bgOpacitySeekBar.setOnSeekBarChangeListener(new SeekBarListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				settings.setValue(NexCaptionAttribute.OPACITY_BACKGROUND, progress);
				curBGOpacityTextView.setText(String.valueOf(progress));

				preview.setBGCaptionColor(getCaptionColorAttribute(settings, NexCaptionAttribute.COLOR_BACKGROUND), progress);
				preview.postInvalidate();
			}
		});
		windowOpacitySeekBar.setOnSeekBarChangeListener(new SeekBarListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				settings.setValue(NexCaptionAttribute.OPACITY_WINDOW, progress);
				curWindowOpacityTextView.setText(String.valueOf(progress));

				preview.setCaptionWindowColor(getCaptionColorAttribute(settings, NexCaptionAttribute.COLOR_WINDOW), progress);
				preview.postInvalidate();
			}
		});
	}

	private NexClosedCaption.CaptionColor getCaptionColorAttribute(NexCaptionAttribute settings, int attr) {
		return (NexClosedCaption.CaptionColor)settings.getValue(attr);
	}

	private NexCaptionAttribute.EdgeStyle getFontEdgeAttribute(NexCaptionAttribute settings, int attr) {
		return (NexCaptionAttribute.EdgeStyle)settings.getValue(attr);
	}

	private void changePreviewFontStyle(NexCaptionPreview preview,
										NexCaptionAttribute.EdgeStyle edgeStyle,
										NexClosedCaption.CaptionColor color, int opacity) {
		switch (edgeStyle) {
			case NONE:
				preview.resetEdgeStyle();
				break;
			case DROP_SHADOW:
				preview.setShadow(true, color, opacity);
				break;
			case RAISED:
				preview.setRaisedWithColor(true, color, opacity);
				break;
			case DEPRESSED:
				preview.setDepressedWithColor(true, color, opacity);
				break;
			case UNIFORM:
				preview.setCaptionStroke(color, opacity, 1.0f);
				break;
		}
	}

	public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
		@Override
		public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	}

	public class SpinnerListener implements Spinner.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {

		}
	}

	private void changeViewVisibility(View view, boolean isVisible) {
		int visibility = isVisible ? View.VISIBLE : View.GONE;
		view.setVisibility(visibility);
	}

	private ArrayAdapter<NexClosedCaption.CaptionColor> getColorAdapter(Context context) {
		ArrayList<NexClosedCaption.CaptionColor> arrayList = new ArrayList<NexClosedCaption.CaptionColor>();
		arrayList.add(NexClosedCaption.CaptionColor.BLACK);
		arrayList.add(NexClosedCaption.CaptionColor.BLUE);
		arrayList.add(NexClosedCaption.CaptionColor.CYAN);
		arrayList.add(NexClosedCaption.CaptionColor.GREEN);
		arrayList.add(NexClosedCaption.CaptionColor.MAGENTA);
		arrayList.add(NexClosedCaption.CaptionColor.RED);
		arrayList.add(NexClosedCaption.CaptionColor.WHITE);
		arrayList.add(NexClosedCaption.CaptionColor.YELLOW);

		return new ArrayAdapter<NexClosedCaption.CaptionColor>(context,
				android.R.layout.simple_spinner_item, arrayList);
	}

	private ArrayAdapter<NexCaptionAttribute.EdgeStyle> getFontEdgeAdater(Context context) {
		ArrayList<NexCaptionAttribute.EdgeStyle> arrayList = new ArrayList<NexCaptionAttribute.EdgeStyle>();
		arrayList.add(NexCaptionAttribute.EdgeStyle.NONE);
		arrayList.add(NexCaptionAttribute.EdgeStyle.DROP_SHADOW);
		arrayList.add(NexCaptionAttribute.EdgeStyle.DEPRESSED);
		arrayList.add(NexCaptionAttribute.EdgeStyle.RAISED);
		arrayList.add(NexCaptionAttribute.EdgeStyle.UNIFORM);

		return new ArrayAdapter<NexCaptionAttribute.EdgeStyle>(context,
				android.R.layout.simple_spinner_item, arrayList);
	}

	private View createView(Context context) {
		return View.inflate(context, R.layout.caption_render_view_settings_dialog, null);
	}
}
