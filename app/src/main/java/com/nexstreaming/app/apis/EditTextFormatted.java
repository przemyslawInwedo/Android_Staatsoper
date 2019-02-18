package com.nexstreaming.app.apis;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class EditTextFormatted extends EditText {

		private static final String TAG = "EditTextFormatted";
		private static final boolean D = false;

		public static final String FORMAT_ID = "^[[a-z][A-Z][0-9][\\-\\_\\:\\/\\.\\s]]{1,}?$";//"[a-z0-9A-Z-_:/.]";//"^[a-zA-Z][a-zA-Z0-9]*$";

		private String format = new String();
		
		public EditTextFormatted(Context context, AttributeSet attrs,
                                 int defStyle) {
			super(context, attrs, defStyle);
			setFilter();
		}
		public EditTextFormatted(Context context, AttributeSet attrs) {
			super(context, attrs);
			setFilter();
		}
		public EditTextFormatted(Context context) {
			super(context);
			setFilter();
		}

		public void setFormat(String format){
			if (D)	Log.i(TAG, "set format : " + format);
			this.format = format;
		}

		private void setFilter(){
			if (D)	Log.i(TAG, "call setFilter ");
			InputFilter[] filters = new InputFilter[1];
			filters[0] = filter;
			this.setFilters(filters);
		}

		private final InputFilter filter = new InputFilter(){
			public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
							
				if (D)	Log.i(TAG, "source : " + source);
				if (D)	Log.i(TAG, "start : " + start);
				if (D)	Log.i(TAG, "end : " + end);
				if (D)	Log.i(TAG, "dest : " + dest);
				if (D)	Log.i(TAG, "dstart : " + dstart);
				if (D)	Log.i(TAG, "dend : " + dend);

				String newText = new String();
				newText += dest.subSequence(0, dstart);
				newText += source.subSequence(start, end);
				newText += dest.subSequence(dend, dest.length());
							
				if (D)	Log.i(TAG, "newText : " + newText);

				Pattern pattern = null;
				try{
					pattern = Pattern.compile(EditTextFormatted.this.format);
				}catch(PatternSyntaxException e){
					Log.e(TAG, "Pattern syntax error", e);
					return null;
				}
				if (D) Log.i(TAG, "pattern : " + pattern);

				Matcher matcher = pattern.matcher(newText);

				if (matcher.find() == false)
				{
					Log.i(TAG, "matcher.find() : " + matcher.find());
					return "";
				}	
				return null;
			}
		};

}
