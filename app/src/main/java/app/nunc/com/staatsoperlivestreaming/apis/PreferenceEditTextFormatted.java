package app.nunc.com.staatsoperlivestreaming.apis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import app.nunc.com.staatsoperlivestreaming.R;

public class PreferenceEditTextFormatted extends EditTextPreference {

    private static final String TAG = "PrefEditTextFormatted";

    private EditTextFormatted mEditText;
    private String mText;
    private String completeFormat;
    
    @SuppressLint("InlinedApi")
    public PreferenceEditTextFormatted(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        mEditText = new EditTextFormatted(context, attrs);
        mEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );

        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditTextFormat);
        String inputFormat = a.getString(R.styleable.EditTextFormat_inputformat);
        Log.i(TAG, "PreferenceEditTextFormatted created:inputFormat:"+ inputFormat);
        if (inputFormat != null) mEditText.setFormat(inputFormat);
        completeFormat = a.getString(R.styleable.EditTextFormat_completeformat);
        Log.i(TAG, "PreferenceEditTextFormatted created:completeFormat:"+ completeFormat);
	    a.recycle();

        mEditText.setId(android.R.id.edit);

        mEditText.setEnabled(true);
    }

    public PreferenceEditTextFormatted(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextPreferenceStyle);
    }

    public PreferenceEditTextFormatted(Context context) {
        this(context, null);
    }

    
    @Override
    public void setText(String text) {
        final boolean wasBlocking = shouldDisableDependents();
        Log.i(TAG, "PreferenceEditTextFormatted setText:"+ text);
        mText = text;
        
        persistString(text);
	    setSummary(text);
	    notifyChanged();

	    final boolean isBlocking = shouldDisableDependents();
        Log.i(TAG, "PreferenceEditTextFormatted wasBlocking:"+ wasBlocking+", isBlocking:"+isBlocking);
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
    }

    
    @Override
    public String getText() {
        Log.i(TAG, "PreferenceEditTextFormatted getText:"+ mText);
        return mText;
    }

    @Override
    protected void onBindDialogView(View view) {
        //super.onBindDialogView(view);
        
        EditText editText = mEditText;
        editText.setText(getText());
        
		CharSequence text = editText.getText();
		Log.i(TAG, "PreferenceEditTextFormatted onBindDialogView::text="+ text);
		if(text != null && text.length() != 0 )
		{
			int cursorPos = Math.min(text.length(), 100);
			editText.setSelection(cursorPos);
		}
		
        ViewParent oldParent = editText.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(editText);
            }
            onAddEditTextToDialogView(view, editText);
        }
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String value = mEditText.getText().toString();
            value = value.trim(); 
    		Log.i(TAG, "PreferenceEditTextFormatted onDialogClosed::value="+ value);
            if (callChangeListener(value)) {
                setText(value);
            }
        }
    }

    @Override
    protected boolean callChangeListener(Object newValue) {
	    if ( completeFormat != null )
	    {
		    Pattern pattern;
		    try {
			    pattern = Pattern.compile(completeFormat);
		    } catch(PatternSyntaxException e){
			    return false;
		    }

		    Matcher matcher = pattern.matcher((String) newValue);

		    if ( !matcher.find() ){
			    Toast.makeText(getContext(), R.string.pref_msg_invalid_format, Toast.LENGTH_SHORT).show();
			    return false;
		    }

		    if( getKey().equals(getContext().getString(R.string.pref_subtitle_download_path_key)) ) {
			    return new File((String)newValue).canWrite();
		    }
	    }
        return true;
    }

    @Override
    public EditText getEditText() {
        return mEditText;
    }

}
