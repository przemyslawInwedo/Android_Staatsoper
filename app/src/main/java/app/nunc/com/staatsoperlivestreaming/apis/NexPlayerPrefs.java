package app.nunc.com.staatsoperlivestreaming.apis;


import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.util.NexFileIO;


public class NexPlayerPrefs extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private SharedPreferences mPref;

	private static final String LOG_TAG = "NexPlayerPrefs";
	private int mXmlRes = R.xml.pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		checkPreferenceValues(mPref);
		String sdkMode = mPref.getString(getString(R.string.pref_sdk_mode_key), getString(R.string.app_list_default_item));

		if((sdkMode.equals(getString(R.string.app_list_video_view_item)))) {
			mXmlRes = R.xml.pref_for_videoview;
		}
		PreferenceManager.setDefaultValues(this, mXmlRes, false);

		setContentView(R.layout.preference_activity);
		addPreferencesFromResource(mXmlRes);

		setupResetButton(sdkMode);
	}

	private void checkPreferenceValues(SharedPreferences pref) {
		File externalStorage = Environment.getExternalStorageDirectory();
		boolean exists = false;
		SharedPreferences.Editor editor = pref.edit();

		exists = NexFileIO.isFileExist(externalStorage, getString(R.string.pref_stable_audio_file_name));
		editor.putBoolean(getString(R.string.pref_stable_audio_key), exists);

		exists = NexFileIO.isFileExist(externalStorage, getString(R.string.pref_stable_video_file_name));
		editor.putBoolean(getString(R.string.pref_stable_video_key), exists);

		editor.apply();
	}

	private void setupResetButton(final String sdkMode) {
		Button button = findViewById(R.id.reset_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = mPref.edit();
				editor.clear();
				editor.putString(getString(R.string.pref_sdk_mode_key), sdkMode);
				editor.commit();

				refreshActivity();
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	private void refreshActivity() {
		setPreferenceScreen(null);
		addPreferencesFromResource(mXmlRes);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
		Log.d(LOG_TAG, "onSharedPreferenceChanged is called " + key) ;

		if( key.equals(getString(R.string.pref_stable_audio_key)) || key.equals(getString(R.string.pref_stable_video_key)) ) {
			boolean value = sharedPreferences.getBoolean(key, false);
			File externalStorage = Environment.getExternalStorageDirectory();
			String fileName = getString(key.equals(getString(R.string.pref_stable_audio_key)) ?
					R.string.pref_stable_audio_file_name : R.string.pref_stable_video_file_name);
			if( value )
				NexFileIO.makeFile(externalStorage, fileName);
			else
				NexFileIO.deleteFile(externalStorage, fileName);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onResume() {
		Log.d(LOG_TAG," onResume is called.. ");
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
}
