package app.nunc.com.staatsoperlivestreaming.apis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionPreview;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionSetting;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexLog;

class
CaptionStyleDialog {
    private AlertDialog mDialog = null;
    private Context mContext = null;
    private NexCaptionSetting mSettings = new NexCaptionSetting();
    private CaptionStyleDialog.OnSettingsChangedListener mListener = null;

    private int mFontColorOpacity = 255;
    private int mBackgroundColorOpacity = 255;
    private int mWindowOpacity = 255;
    private int mEdgeColorOpacity = 255;

	private boolean isCheckedFont = false;
	private boolean isCheckedBold = false;
	private boolean isCheckedItalic = false;
	private boolean isCheckedUnderline = false;
	private boolean isCheckedBackGround = false;
	private boolean isCheckedWindow = false;
	private boolean isCheckedBoundingBox = false;

    private HashMap<Integer, String> mMapColor = new HashMap<Integer, String>();
    private HashMap<Integer, String> mMapGravity = new HashMap<Integer, String>();

    interface OnSettingsChangedListener {
        void onSettingsChanged(NexCaptionSetting settings);
    }

    CaptionStyleDialog(Context context, CaptionStyleDialog.OnSettingsChangedListener listener) {
        mContext = context;
        mListener = listener;

        mMapColor.put(NexCaptionSetting.DEFAULT, "DEFAULT");
        mMapColor.put(Color.BLACK, "BLACK");
        mMapColor.put(Color.BLUE, "BLUE");
        mMapColor.put(Color.CYAN, "CYAN");
        mMapColor.put(Color.GREEN, "GREEN");
        mMapColor.put(Color.MAGENTA, "MAGENTA");
        mMapColor.put(Color.RED, "RED");
        mMapColor.put(Color.WHITE, "WHITE");
        mMapColor.put(Color.YELLOW, "YELLOW");

        mMapGravity.put(NexCaptionSetting.DEFAULT, "DEFAULT");
        mMapGravity.put(Gravity.START| Gravity.CENTER_VERTICAL, "LEFT");
        mMapGravity.put(Gravity.CENTER, "CENTER");
        mMapGravity.put(Gravity.END| Gravity.CENTER_VERTICAL, "RIGHT");
    }

    public boolean isShowing() {
        return mDialog != null && mDialog.isShowing();
    }

    private AlertDialog createDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.caption_style);

        final AlertDialog dialog = builder.setView(createView(context)).create();

        Window window = dialog.getWindow();

        if (null != window) {
            WindowManager.LayoutParams params = window.getAttributes();
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
        }

        return dialog;
    }

    public void createAndShow(NexCaptionSetting settings) {
        mSettings = new NexCaptionSetting(settings);

        if (mDialog == null) {
            mDialog = createDialog(mContext);
        }
        mDialog.show();
        setupUIComponents(mDialog);
    }

    private void setupUIComponents(final AlertDialog dialog) {
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

		/**/ //------------------------------------------------------------------------------------------ // added from derrick start.
        final CheckBox boldCheckBox = (CheckBox) dialog.findViewById(R.id.font_effect_bold);
        final CheckBox underlineCheckBox = (CheckBox) dialog.findViewById(R.id.font_effect_underline);
        final CheckBox italicCheckBox = (CheckBox) dialog.findViewById(R.id.font_effect_italic);
        final Spinner alignmentSpinner = (Spinner) dialog.findViewById(R.id.alignment_spinner);
        final CheckBox defaultBoundingBoxCheckBox = (CheckBox) dialog.findViewById(R.id.boundingbox_default);

        final SeekBar boundingBoxTopSeekBar = (SeekBar) dialog.findViewById(R.id.boundingbox_value_top);
        final TextView boundingBoxTopTextView = (TextView) dialog.findViewById(R.id.bondingbox_title_top);
        final SeekBar boundingBoxLeftSeekBar = (SeekBar) dialog.findViewById(R.id.boundingbox_value_left);
        final TextView boundingBoxLeftTextView = (TextView) dialog.findViewById(R.id.bondingbox_title_left);
        final SeekBar boundingBoxWidthSeekBar = (SeekBar) dialog.findViewById(R.id.boundingbox_value_width);
        final TextView boundingBoxWidthTextView = (TextView) dialog.findViewById(R.id.bondingbox_title_width);
        final SeekBar boundingBoxHeightSeekBar = (SeekBar) dialog.findViewById(R.id.boundingbox_value_height);
        final TextView boundingBoxHeightTextView = (TextView) dialog.findViewById(R.id.bondingbox_title_height);


        final CheckBox fontCheckBox = (CheckBox) dialog.findViewById(R.id.font_default_checkbox);
        final CheckBox backgroundCheckBox = (CheckBox) dialog.findViewById(R.id.bg_color_default_checkbox);
        final CheckBox windowCheckBox = (CheckBox) dialog.findViewById(R.id.window_default_checkbox);

        //fontSize
		fontCheckBox.setChecked(isCheckedFont);
        fontCheckBox.setOnCheckedChangeListener(new CaptionStyleDialog.CheckBoxListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String strColor = (String) fontColorSpinner.getSelectedItem();

                if (b) {
                    if (!"DEFAULT".equals(strColor)) {
                        fontOpacitySeekBar.setEnabled(b);
                    }
                } else {
                    fontOpacitySeekBar.setEnabled(b);
                }

                fontSizeSeekBar.setEnabled(b);
                edgeColorSpinner.setEnabled(b);
                fontEdgeSpinner.setEnabled(b);
                fontColorSpinner.setEnabled(b);

                if (!b) {
                    mSettings.mFontColor = NexCaptionSetting.DEFAULT;
                    mSettings.mFontScale = NexCaptionSetting.DEFAULT;
                    mSettings.mEdgeColor = NexCaptionSetting.DEFAULT;
                    mSettings.mEdgeStyle = NexCaptionSetting.EdgeStyle.DEFAULT;
                }
            }
        });

        fontSizeSeekBar.setProgress(NexCaptionSetting.DEFAULT == mSettings.mFontScale ? 2 : (int)(mSettings.mFontScale * 2f));
        fontSizeSeekBar.setEnabled(fontCheckBox.isChecked());
        fontSizeSeekBar.setOnSeekBarChangeListener(new CaptionStyleDialog.SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float progressD = (float) progress / 2;
                curFontSizeTextView.setText(String.valueOf(progressD));
                if (fromUser) {
                    mSettings.mFontScale = progressD;
                    NexLog.d("test", "changed font scale dialog : " + progressD);
                }
            }
        });

        //fontOpacity
        fontOpacitySeekBar.setEnabled(fontCheckBox.isChecked());
		fontOpacitySeekBar.setProgress(mFontColorOpacity);
        fontOpacitySeekBar.setOnSeekBarChangeListener(new CaptionStyleDialog.SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFontColorOpacity = progress;
                mSettings.mFontColor = getColorWithOpacity(mSettings.mFontColor, mFontColorOpacity);
                curFontOpacityTextView.setText(String.valueOf(progress));
            }
        });

        final ArrayAdapter<String> colorAdapter = getColorAdapter(mContext);
        fontColorSpinner.setEnabled(fontCheckBox.isChecked());
        fontColorSpinner.setAdapter(colorAdapter);

        String color = mMapColor.get(getColorWithOpacity(mSettings.mFontColor, NexCaptionSetting.DEFAULT == mSettings.mFontColor ? 0 : 255));
        fontColorSpinner.setSelection(colorAdapter.getPosition(color));

        fontColorSpinner.setOnItemSelectedListener(new CaptionStyleDialog.SpinnerListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String strColor = (String) fontColorSpinner.getSelectedItem();
                if (!"DEFAULT".equals(strColor)) {
                    fontOpacitySeekBar.setEnabled(true);

                    mSettings.mFontColor = getColorWithOpacity(Color.parseColor(strColor), mFontColorOpacity);
                } else {
                    mSettings.mFontColor = NexCaptionSetting.DEFAULT;
                    fontOpacitySeekBar.setEnabled(false);
                }
            }
        });

        //font edge
        ArrayAdapter<NexCaptionSetting.EdgeStyle> fontEdgeAdapter = getFontEdgeAdapter(mContext);
        fontEdgeSpinner.setEnabled(fontCheckBox.isChecked());
        fontEdgeSpinner.setAdapter(fontEdgeAdapter);
        fontEdgeSpinner.setSelection(mSettings.mEdgeStyle.ordinal() - 1); //  -1 for excepting None
        fontEdgeSpinner.setOnItemSelectedListener(new CaptionStyleDialog.SpinnerListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NexCaptionSetting.EdgeStyle selectedItem = (NexCaptionSetting.EdgeStyle) fontEdgeSpinner.getItemAtPosition(position);

                boolean isVisible = !selectedItem.equals(NexCaptionSetting.EdgeStyle.NONE) && !selectedItem.equals(NexCaptionSetting.EdgeStyle.DEFAULT);
                changeViewVisibility(edgeLayout, isVisible);

                if (isVisible) {
                    String strColor = (String) edgeColorSpinner.getSelectedItem();
                    if (!"DEFAULT".equals(strColor)) {
                        if (!edgeOpacitySeekBar.isEnabled()) {
                            edgeOpacitySeekBar.setEnabled(true);
                        }
                    } else {
                        edgeOpacitySeekBar.setEnabled(false);
                    }
                }

                edgeColorSpinner.setEnabled(isVisible);

                mSettings.mEdgeStyle = selectedItem;
                mSettings.mEdgeWidth = selectedItem == NexCaptionSetting.EdgeStyle.UNIFORM ? 1f * mContext.getResources().getDisplayMetrics().density : NexCaptionSetting.DEFAULT;
            }
        });

        edgeLayout.setVisibility(NexCaptionSetting.EdgeStyle.DEFAULT == mSettings.mEdgeStyle ? View.GONE : View.VISIBLE);
        edgeColorSpinner.setEnabled(fontCheckBox.isChecked());
        edgeColorSpinner.setAdapter(colorAdapter);

        color = mMapColor.get(getColorWithOpacity(mSettings.mEdgeColor, NexCaptionSetting.DEFAULT == mSettings.mEdgeColor ? 0 : 255));
        edgeColorSpinner.setSelection(colorAdapter.getPosition(color));
        edgeColorSpinner.setOnItemSelectedListener(new CaptionStyleDialog.SpinnerListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String strColor = (String) edgeColorSpinner.getSelectedItem();
                if (!"DEFAULT".equals(strColor)) {
                    if (!edgeOpacitySeekBar.isEnabled()) {
                        edgeOpacitySeekBar.setEnabled(true);
                    }

                    mSettings.mEdgeColor = getColorWithOpacity(Color.parseColor(strColor), mEdgeColorOpacity);
                } else {
                    mSettings.mEdgeColor = NexCaptionSetting.DEFAULT;
                    edgeOpacitySeekBar.setEnabled(false);
                }
            }
        });

        edgeOpacitySeekBar.setEnabled(fontCheckBox.isChecked());
		edgeOpacitySeekBar.setProgress(mEdgeColorOpacity);
        edgeOpacitySeekBar.setOnSeekBarChangeListener(new CaptionStyleDialog.SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mEdgeColorOpacity = progress;
                curEdgeOpacityTextView.setText(String.valueOf(progress));

                if (fromUser) {
                    mSettings.mEdgeColor = getColorWithOpacity(mSettings.mEdgeColor, mEdgeColorOpacity);
                }
            }
        });

		backgroundCheckBox.setChecked(isCheckedBackGround);
        backgroundCheckBox.setOnCheckedChangeListener(new CaptionStyleDialog.CheckBoxListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                bgColorSpinner.setEnabled(b);

                if (b) {
                    String strColor = (String) bgColorSpinner.getSelectedItem();
                    if (!"DEFAULT".equals(strColor)) {
                        bgOpacitySeekBar.setEnabled(b);
                    }
                } else {
                    mSettings.mBackgroundColor = NexCaptionSetting.DEFAULT;
                    bgOpacitySeekBar.setEnabled(b);
                }
            }
        });

        bgColorSpinner.setEnabled(backgroundCheckBox.isChecked());
        bgColorSpinner.setAdapter(colorAdapter);
        color = mMapColor.get(getColorWithOpacity(mSettings.mBackgroundColor, NexCaptionSetting.DEFAULT == mSettings.mBackgroundColor ? 0 : 255));
        bgColorSpinner.setSelection(colorAdapter.getPosition(color));

        bgColorSpinner.setOnItemSelectedListener(new CaptionStyleDialog.SpinnerListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String strColor = (String) bgColorSpinner.getSelectedItem();
                if (!"DEFAULT".equals(strColor)) {
                    if (!bgOpacitySeekBar.isEnabled()) {
                        bgOpacitySeekBar.setEnabled(true);
                    }

                    mSettings.mBackgroundColor = getColorWithOpacity(Color.parseColor(strColor), mBackgroundColorOpacity);
                } else {
                    mSettings.mBackgroundColor = NexCaptionSetting.DEFAULT;
                    bgOpacitySeekBar.setEnabled(false);
                }
            }
        });

        bgOpacitySeekBar.setEnabled(backgroundCheckBox.isChecked());
		bgOpacitySeekBar.setProgress(mBackgroundColorOpacity);
        bgOpacitySeekBar.setOnSeekBarChangeListener(new CaptionStyleDialog.SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBackgroundColorOpacity = progress;
                curBGOpacityTextView.setText(String.valueOf(progress));

                if (fromUser) {
                    mSettings.mBackgroundColor = getColorWithOpacity(mSettings.mBackgroundColor, mBackgroundColorOpacity);
                }
            }
        });

		windowCheckBox.setChecked(isCheckedWindow);
        windowCheckBox.setOnCheckedChangeListener(new CaptionStyleDialog.CheckBoxListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                windowColorSpinner.setEnabled(b);

                if (b) {
                    String strColor = (String) windowColorSpinner.getSelectedItem();
                    if (!"DEFAULT".equals(strColor)) {
                        windowOpacitySeekBar.setEnabled(b);
                    }
                } else {
                    mSettings.mWindowColor = NexCaptionSetting.DEFAULT;
                    windowOpacitySeekBar.setEnabled(b);
                }
            }
        });

        windowColorSpinner.setEnabled(windowCheckBox.isChecked());
        windowColorSpinner.setAdapter(colorAdapter);

        color = mMapColor.get(getColorWithOpacity(mSettings.mWindowColor, NexCaptionSetting.DEFAULT == mSettings.mWindowColor ? 0 : 255));
        windowColorSpinner.setSelection(colorAdapter.getPosition(color));

        windowColorSpinner.setOnItemSelectedListener(new CaptionStyleDialog.SpinnerListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String strColor = (String) windowColorSpinner.getSelectedItem();
                if (!"DEFAULT".equals(strColor)) {
                    windowOpacitySeekBar.setEnabled(true);
                    mSettings.mWindowColor = getColorWithOpacity(Color.parseColor(strColor), mWindowOpacity);
                } else {
                    mSettings.mWindowColor = NexCaptionSetting.DEFAULT;
                    windowOpacitySeekBar.setEnabled(false);
                }
            }
        });

        windowOpacitySeekBar.setEnabled(windowCheckBox.isChecked());
		windowOpacitySeekBar.setProgress(mWindowOpacity);
        windowOpacitySeekBar.setOnSeekBarChangeListener(new CaptionStyleDialog.SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWindowOpacity = progress;
                curWindowOpacityTextView.setText(String.valueOf(progress));
                if (fromUser) {
                    mSettings.mWindowColor = getColorWithOpacity(mSettings.mWindowColor, mWindowOpacity);
                }
            }
        });

        //Bold Effect.
		boldCheckBox.setChecked(isCheckedBold);
        boldCheckBox.setOnCheckedChangeListener(new CheckBoxListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                mSettings.mBold = enable ? NexCaptionSetting.StringStyle.APPLY : NexCaptionSetting.StringStyle.DEFAULT;
            }
        });

        //Underline Effect.
		underlineCheckBox.setChecked(isCheckedUnderline);
        underlineCheckBox.setOnCheckedChangeListener(new CheckBoxListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                mSettings.mUnderLine = enable ? NexCaptionSetting.StringStyle.APPLY : NexCaptionSetting.StringStyle.DEFAULT;
            }
        });

        //Italic Effect.
		italicCheckBox.setChecked(isCheckedItalic);
        italicCheckBox.setOnCheckedChangeListener(new CheckBoxListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                mSettings.mItalic = enable ? NexCaptionSetting.StringStyle.APPLY : NexCaptionSetting.StringStyle.DEFAULT;
            }
        });

        final ArrayAdapter<String> alignmentAdapter = getAlignmentAdapter(mContext);
        alignmentSpinner.setAdapter(alignmentAdapter);
        String gravity = mMapGravity.get(mSettings.mGravity);
        alignmentSpinner.setSelection(alignmentAdapter.getPosition(gravity));

        alignmentSpinner.setOnItemSelectedListener(new CaptionStyleDialog.SpinnerListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                String gravity = alignmentAdapter.getItem(index);

                Iterator iterator = mMapGravity.keySet().iterator();
                if (null != gravity) {
                    while (iterator.hasNext()) {
                        Integer value = (Integer)iterator.next();
                        if (gravity.equals(mMapGravity.get(value))) {
                            mSettings.mGravity = value;
                            break;
                        }
                    }
                }
            }
        });


		defaultBoundingBoxCheckBox.setChecked(isCheckedBoundingBox);
        defaultBoundingBoxCheckBox.setOnCheckedChangeListener(new CaptionStyleDialog.CheckBoxListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boundingBoxLeftSeekBar.setEnabled(b);
                boundingBoxTopSeekBar.setEnabled(b);
                boundingBoxWidthSeekBar.setEnabled(b);
                boundingBoxHeightSeekBar.setEnabled(b);

                if (!b) {
                    mSettings.mRelativeWindowRect.init();
                } else {
                    mSettings.mRelativeWindowRect.xPercent = boundingBoxLeftSeekBar.getProgress();
                    mSettings.mRelativeWindowRect.yPercent = boundingBoxTopSeekBar.getProgress();
                    mSettings.mRelativeWindowRect.widthPercent = boundingBoxWidthSeekBar.getProgress();
                    mSettings.mRelativeWindowRect.heightPercent = boundingBoxHeightSeekBar.getProgress();
                }

                mSettings.mRelativeWindowRect.userDefined = b;
            }
        });

        boundingBoxLeftSeekBar.setEnabled(defaultBoundingBoxCheckBox.isChecked());
		boundingBoxLeftSeekBar.setProgress(NexCaptionSetting.DEFAULT == mSettings.mRelativeWindowRect.xPercent ? 10 : mSettings.mRelativeWindowRect.xPercent);
        boundingBoxLeftSeekBar.setOnSeekBarChangeListener(new CaptionStyleDialog.SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSettings.mRelativeWindowRect.xPercent = i;
                boundingBoxLeftTextView.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        boundingBoxTopSeekBar.setEnabled(defaultBoundingBoxCheckBox.isChecked());
        boundingBoxTopSeekBar.setProgress(NexCaptionSetting.DEFAULT == mSettings.mRelativeWindowRect.yPercent ? 75 : mSettings.mRelativeWindowRect.yPercent);
        boundingBoxTopSeekBar.setOnSeekBarChangeListener(new CaptionStyleDialog.SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSettings.mRelativeWindowRect.yPercent = i;
                boundingBoxTopTextView.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        boundingBoxWidthSeekBar.setEnabled(defaultBoundingBoxCheckBox.isChecked());
		boundingBoxWidthSeekBar.setProgress(NexCaptionSetting.DEFAULT == mSettings.mRelativeWindowRect.widthPercent ? 80 : mSettings.mRelativeWindowRect.widthPercent);
        boundingBoxWidthSeekBar.setOnSeekBarChangeListener(new CaptionStyleDialog.SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSettings.mRelativeWindowRect.widthPercent = i;
                boundingBoxWidthTextView.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        boundingBoxHeightSeekBar.setEnabled(defaultBoundingBoxCheckBox.isChecked());
		boundingBoxHeightSeekBar.setProgress(NexCaptionSetting.DEFAULT == mSettings.mRelativeWindowRect.heightPercent ? 20 : mSettings.mRelativeWindowRect.heightPercent);
        boundingBoxHeightSeekBar.setOnSeekBarChangeListener(new CaptionStyleDialog.SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSettings.mRelativeWindowRect.heightPercent = i;
                boundingBoxHeightTextView.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
        Button saveButton = (Button) dialog.findViewById(R.id.save_button);

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
				isCheckedFont = fontCheckBox.isChecked();
				isCheckedBackGround = backgroundCheckBox.isChecked();
				isCheckedWindow = windowCheckBox.isChecked();

				isCheckedBold = boldCheckBox.isChecked();
				isCheckedItalic = italicCheckBox.isChecked();
				isCheckedUnderline = underlineCheckBox.isChecked();
				isCheckedBoundingBox = defaultBoundingBoxCheckBox.isChecked();

                mListener.onSettingsChanged(mSettings);
            }
        });
    }

    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
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

    private class CheckBoxListener implements CheckBox.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        }
    }

    private class SpinnerListener implements Spinner.OnItemSelectedListener {
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

    private ArrayAdapter<String> getColorAdapter(Context context) {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("DEFAULT");
        arrayList.add("BLACK");
        arrayList.add("BLUE");
        arrayList.add("CYAN");
        arrayList.add("GREEN");
        arrayList.add("MAGENTA");
        arrayList.add("RED");
        arrayList.add("WHITE");
        arrayList.add("YELLOW");

        return new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayList);
    }

    private ArrayAdapter<NexCaptionSetting.EdgeStyle> getFontEdgeAdapter(Context context) {
        ArrayList<NexCaptionSetting.EdgeStyle> arrayList = new ArrayList<NexCaptionSetting.EdgeStyle>();
        arrayList.add(NexCaptionSetting.EdgeStyle.DEFAULT);
        arrayList.add(NexCaptionSetting.EdgeStyle.DROP_SHADOW);
        arrayList.add(NexCaptionSetting.EdgeStyle.RAISED);
        arrayList.add(NexCaptionSetting.EdgeStyle.DEPRESSED);
        arrayList.add(NexCaptionSetting.EdgeStyle.UNIFORM);

        return new ArrayAdapter<NexCaptionSetting.EdgeStyle>(context,
                android.R.layout.simple_spinner_item, arrayList);
    }

    private ArrayAdapter<String> getAlignmentAdapter(Context context) {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("DEFAULT");
        arrayList.add("LEFT");
        arrayList.add("CENTER");
        arrayList.add("RIGHT");

        return new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, arrayList);
    }

    private int getColorWithOpacity(int setColor, int cOpacity) {
        return Color.argb(cOpacity, Color.red(setColor), Color.green(setColor), Color.blue(setColor));
    }


    private View createView(Context context) {
        return View.inflate(context, R.layout.captionstyledialog, null);
    }
}
