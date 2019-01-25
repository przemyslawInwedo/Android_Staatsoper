package app.nunc.com.staatsoperlivestreaming.apis;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import app.nunc.com.staatsoperlivestreaming.R;

public class ActivityLauncherPlugin implements IActivityLauncherPlugin {
	private final static String LOG_TAG = "ActivityLauncherPlugin";
	Context mContext;
	HashMap<String, String> mMap;
	private final static int TOAST_LENGTH_EVEN_SHORTER = 30;
	
	private ActivityLauncherPlugin(Context context) {
		mContext = context;
		mMap = new HashMap<String, String>();
		loadDefaultMap(mMap);
		loadInternalMap(mMap);
	}
	
	public static IActivityLauncherPlugin getPlugin(Context context)
	{
		ActivityLauncherPlugin plugin =  new ActivityLauncherPlugin(context);
		return plugin;
	}

	protected void loadDefaultMap(HashMap<String, String> map) {
		/* =========== PUBLIC SAMPLES ============= */
		map.put(mContext.getString(R.string.app_list_video_ofoff_item), "app.nunc.com.staatsoperlivestreaming.apis.NexVideoOnOffSample");
		map.put(mContext.getString(R.string.app_list_video_view_item), "app.nunc.com.staatsoperlivestreaming.apis.NexVideoViewSample");
	}

	@Override
	public String getActivityClassName(String sdkMode) {
		String className = mMap.get(sdkMode);
		if( !canFindClass(className) ) {
			className = null;
		}
		return className;
	}

	private boolean canFindClass(String className) {
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			Log.e(LOG_TAG, "Can't find " + className + ".");
			Toast toast = Toast.makeText(mContext,"Can't find " + className + " You can't use this demo app.", Toast.LENGTH_LONG);
			toast.show();
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

	@Override
	public String getStoreActivityClassName(String sdkMode) {
		String act = null;


		return act;
	}

	@Override
	public boolean shouldLaunchActivity(Context context, String mSdkMode, String url, SharedPreferences pref, ArrayList<String> urlList ) {
		boolean result = true;
		result = shouldLaunchActivityInternal(context, mMap, mSdkMode, url, pref, urlList);
		return result;
	}
	
	protected void loadInternalMap(HashMap<String, String> map) {

		// NexMediaDrm Start
		map.put(mContext.getString(R.string.app_list_video_view_media_drm_item), "app.nunc.com.staatsoperlivestreaming.apis.NexVideoViewSampleMediaDRM");
		// NexMediaDrm end
	}
		
	private boolean shouldLaunchActivityInternal(Context context, HashMap<String, String> map, String sdkMode, String url, SharedPreferences pref, ArrayList<String> urlList) {
		boolean result = true;


		Log.d(LOG_TAG, "shouldLaunchActivityInternal:" + result);
		return result;
	}
}
