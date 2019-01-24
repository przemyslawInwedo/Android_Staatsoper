/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.nunc.com.staatsoperlivestreaming.apis;

import android.os.Environment;

import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexContentInformation;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer;
import app.nunc.com.staatsoperlivestreaming.util.NexFileIO;


public class DolbyAC3ApprovalTestCasePreset {
	private NexPlayer mNexPlayer;
	private NexContentInformation mContentInfo = null;

	private AC3Settings[] ac3_option_table =
			{
					new AC3Settings("dlb_android_001", 20, 1, 1, 0),
					new AC3Settings("dlb_android_002", 20, 2, 1, 0),
					new AC3Settings("dlb_android_003", 20, 1, 1, 0),
					new AC3Settings("dlb_android_004", 20, 2, 1, 0),
					new AC3Settings("dlb_android_005", 20, 2, 1, 0),
					new AC3Settings("dlb_android_006", 20, 2, 0, 0),
					new AC3Settings("dlb_android_007", 20, 2, 1, 1),
					new AC3Settings("dlb_android_008", 20, 2, 1, 0),
					new AC3Settings("dlb_android_009", 20, 2, 1, 5),
					new AC3Settings("dlb_android_010", 20, 2, 1, 12),
					new AC3Settings("dlb_android_011", 20, 2, 1, 0),
					new AC3Settings("dlb_android_012", 20, 2, 1, 0),
					new AC3Settings("dlb_android_013", 20, 2, 1, 0),
					new AC3Settings("dlb_android_014", 20, 2, 1, 0),
					new AC3Settings("dlb_android_015", 20, 2, 1, 0),
					new AC3Settings("dlb_android_016", 20, 2, 1, 0),
					new AC3Settings("dlb_android_017", 20, 2, 1, 0),
					new AC3Settings("dlb_android_018", 20, 2, 1, 0),
					new AC3Settings("dlb_android_019", 20, 2, 1, 0),
					new AC3Settings("dlb_android_020", 20, 1, 1, 0),
					new AC3Settings("dlb_android_021", 20, 1, 0, 0)
			};

	DolbyAC3ApprovalTestCasePreset (NexPlayer nexplayer) {
		mNexPlayer = nexplayer;
		mContentInfo = mNexPlayer.getContentInfo();
	}

	private class AC3Settings
	{
		String _name;
		int _out_channel; // for property - 2001, value - 20, 51, 71
		int _endpoint; // for property - 2002, value - 1 (speaker), 2(headphone)
		int _post_processing;// for property - 2003, value - 0, 1
		int _enhancement; // for property - 2004, value - 0 (off), 1 (default), 2 (medium), 3 (high)

		AC3Settings(String name, int out_channel, int endpoint, int post_processing, int enhancement)
		{
			_name = name;
			_out_channel = out_channel;
			_endpoint = endpoint;
			_post_processing = post_processing;
			_enhancement = enhancement;
		}
	};

	private String makeDataDumpPath(String name, String subPath)
	{
		String dumpPath = Environment.getExternalStorageDirectory().getPath() + subPath + name;
		NexFileIO.makeDirectory(dumpPath);
		return dumpPath;
	}

	public void set_ac3_settings_for_approval(String contentPath)
	{
		for (int i = 0; i < ac3_option_table.length; ++i)
		{
			if (contentPath.contains(ac3_option_table[i]._name))
			{
				mNexPlayer.setProperties(2001, ac3_option_table[i]._out_channel);
				mNexPlayer.setProperties(2002, ac3_option_table[i]._endpoint);
				mNexPlayer.setProperties(2003, ac3_option_table[i]._post_processing);
				mNexPlayer.setProperties(2004, ac3_option_table[i]._enhancement);

				String dumpPath = makeDataDumpPath(ac3_option_table[i]._name, "/dump/");
				mNexPlayer.setProperties(0x70000, dumpPath);

				if ("dlb_android_014".equals(ac3_option_table[i]._name))
				{
					mNexPlayer.setMediaStream(
							1,
							mContentInfo.mCurrTextStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID ? NexPlayer.MEDIA_STREAM_DISABLE_ID : NexPlayer.MEDIA_STREAM_DEFAULT_ID,
							mContentInfo.mCurrVideoStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID ? NexPlayer.MEDIA_STREAM_DISABLE_ID : NexPlayer.MEDIA_STREAM_DEFAULT_ID,
							NexPlayer.MEDIA_STREAM_DEFAULT_ID);
				}

				break;
			}
		}
	}
}
