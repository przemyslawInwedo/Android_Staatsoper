package app.nunc.com.staatsoperlivestreaming.apis;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public interface IActivityLauncherPlugin {

	public boolean shouldLaunchActivity(Context context, String mSdkMode, String url, SharedPreferences prefData, ArrayList<String> urlList);
	public String getActivityClassName(String sdkMode);
	public String getStoreActivityClassName(String sdkMode);
	
}
