package app.nunc.com.staatsoperlivestreaming.apis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import app.nunc.com.staatsoperlivestreaming.info.NxbInfo;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer.NexErrorCode;;


public class NexPlayerSampleExtensionLoader {
	private final static String LOG_TAG = "NexPlayerSampleExtensionLoader";
	
	public interface ISampleExtensionListener {
		public void errorStatus(NexErrorCode errorCode, String message);
	}
	/*
	 * nexPlayer and context must be set by extension loader
	 * listener may be set by the caller Activity, NexPlayerSample for example, if interested in errorStatus() callback
	 */
	public interface ISampleExtension {
		/* called by the calling Player Activity if the start of play-back can continue with the given arguments */
		public boolean shouldStartPlay(NexPreferenceData prefData, NxbInfo nxbInfo);
		public String getName();
		public void setListener(ISampleExtensionListener listener);
		public void setNexPlayer(NexPlayer nexPlayer);
		public void setContext(Context context);
	}
	
	class DefaultExtension implements ISampleExtension {
		@Override
		public
		boolean shouldStartPlay(NexPreferenceData prefData, NxbInfo nxbInfo) {
			return true;
		}
		@Override
		public String getName() {
			return "Default";
		}
		
		@Override
		public void setListener(ISampleExtensionListener listener) {
			// nothing to report back. ignore listener
		}
		
		@Override
		public void setNexPlayer(NexPlayer nexPlayer) {
			// nothing to report back. ignore nexPlayer
		}
		
		@Override
		public void setContext(Context context) {
			// nothing to report back. ignore context
		}
	}
	
	/* use static loadFeatureExtension() instead of the constructor */
	private NexPlayerSampleExtensionLoader() {
	}
	
	@SuppressLint("LongLogTag")
	public static ISampleExtension loadExtension(NexPlayer nexPlayer, Context context) {
		ISampleExtension extension = null;
		
		
		if (extension == null) {
			/* No extension deployed. Return the default/dummy extension does nothing */
			NexPlayerSampleExtensionLoader loader = new NexPlayerSampleExtensionLoader();
			extension = loader.new DefaultExtension();
		}
		Log.d(LOG_TAG, "Loaded sample extension:" + extension.getName() + "(" + extension.getClass().getName() + ")");
		return extension;
	}
	
	@SuppressLint("LongLogTag")
	private static ISampleExtension loadFeatureExtension(String className) {
		ISampleExtension extension = null;
		try {
			Class<?> cls = NexPlayerSampleExtensionLoader.class.getClassLoader().loadClass(className);
			extension = (ISampleExtension) cls.getConstructor().newInstance();
		} catch (Exception e) {
			Log.d(LOG_TAG, "Feature Extension not found:" + e.getClass().getName());
		}	
		return extension;
	}
}
