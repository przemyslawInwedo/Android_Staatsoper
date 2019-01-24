package app.nunc.com.staatsoperlivestreaming.dialog;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStoredInfoFileUtils;

/**
 * Created by bonnie.kyeon on 2016-08-08.
 */
public class NexStoredInfoDialog {
	public static void show(Context context, File storeInfoFile) {
		if( storeInfoFile != null ) {
			JSONObject storeInfo = NexStoredInfoFileUtils.parseJSONObject(storeInfoFile);

			if( storeInfo != null ) {
				String info = "";
				try {
					info += "URL :\n" + storeInfo.getString(NexStoredInfoFileUtils.STORED_INFO_KEY_STORE_URL) + "\n\n";
					info += "Store path :\n" + storeInfo.getString(NexStoredInfoFileUtils.STORED_INFO_KEY_STORE_PATH) + "\n\n";
					info += "Store Percentage : " + storeInfo.getInt(NexStoredInfoFileUtils.STORED_INFO_KEY_STORE_PERCENTAGE) + "\n\n";
					info += "Bandwidth : " + storeInfo.getInt(NexStoredInfoFileUtils.STORED_INFO_KEY_BW) + "\n\n";
					info += "Audio Stream ID : " + storeInfo.getInt(NexStoredInfoFileUtils.STORED_INFO_KEY_AUDIO_STREAM_ID) + "\n";
					//For backward compatibility, use the following function(optString api).
					info += "Audio Track ID : " + storeInfo.optString(NexStoredInfoFileUtils.STORED_INFO_KEY_AUDIO_TRACK_ID, "-1") + "\n";
					info += "Video Stream ID : " + storeInfo.getInt(NexStoredInfoFileUtils.STORED_INFO_KEY_VIDEO_STREAM_ID) + "\n";
					info += "Text Stream ID : " + storeInfo.getInt(NexStoredInfoFileUtils.STORED_INFO_KEY_TEXT_STREAM_ID) + "\n";
					info += "Custom Attribute ID : " + storeInfo.getInt(NexStoredInfoFileUtils.STORED_INFO_KEY_CUSTOM_ATTR_ID) + "\n";
				} catch (JSONException e) {
					e.printStackTrace();
				}

				DialogBuilder.makeMessageDialog(context, R.string.store_info, info, null).show();
			}
		}
	}
}
