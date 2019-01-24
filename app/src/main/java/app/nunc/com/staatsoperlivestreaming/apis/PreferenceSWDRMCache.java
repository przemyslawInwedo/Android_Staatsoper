package app.nunc.com.staatsoperlivestreaming.apis;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.util.NexFileIO;

public class PreferenceSWDRMCache extends Preference {

	private int mClickCounter = 100;
	private static final String LOG_TAG = "PreferenceSWDRMCache";
	private String mCachFolder ;
	private SharedPreferences mPref;
	private Context mContext = null;

	// This is the constructor called by the inflater
	public PreferenceSWDRMCache(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWidgetLayoutResource(R.layout.pref_swdrm_cash);
		mContext = context;
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		// Set our custom views inside the layout
		final Button mOfflineCash = (Button) view.findViewById(R.id.swdrm_cash_clear);
		mOfflineCash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				File fileDir = getContext().getFilesDir();
				if( fileDir == null)
					throw new IllegalStateException("No files directory!");
				NexFileIO.deleteFolder(fileDir.getAbsolutePath() + "/wvcert/");

			}
		});
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInteger(index, 0);
	}

	@Override
	protected void onClick() {
		int nKeep = mClickCounter + 1;
		callChangeListener(nKeep);//add lock
		mClickCounter = nKeep;
		persistInt(mClickCounter);
		notifyChanged();//notify UI to refresh
		// super.onClick();
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		if (restoreValue) {
			// Restore state
			mClickCounter = getPersistedInt(mClickCounter);
		} else {
			// Set state
			int value = (Integer) defaultValue;
			mClickCounter = value;
			persistInt(value);
		}
	}

	@Override
	protected boolean callChangeListener(Object newValue) {
		return super.callChangeListener(newValue);
	}

	private void showToast() {
		Toast toast = Toast.makeText(getContext(),getContext().getResources().getString(R.string.sucess), Toast.LENGTH_LONG);
		toast.show();
	}


}
