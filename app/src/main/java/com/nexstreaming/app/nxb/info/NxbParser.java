package com.nexstreaming.app.nxb.info;

import android.text.TextUtils;
import android.util.Log;

import com.nexstreaming.app.util.NexFileIO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NxbParser {
	private static final String LOG_TAG = "NxbParser";
	private static final String NXB2_FORMAT_KEYWORD = "NXB2";
	private static final String NXB3_FORMAT_KEYWORD = "NXB3";
	private static final String FIELD_SEPARATOR = " ";
	private static final String EXTRA_FIELD_SEPARATOR = "&&";
	private static final int URL_FIELD_INDEX = 0;
	private static final int KEYWORD_INDICATES_NEWFORMAT_FIELD_INDEX = 1;
	private static final int EXTRA_FIELD_INDEX = 2;
	private static final int EXTRA_FIELD_INDEX_TYPE = 0;
	private static final int EXTRA_FIELD_INDEX_TITLE = 1;

	public static ArrayList<NxbInfo> getNxbInfoList(File file) {
		ArrayList<NxbInfo> infoList = new ArrayList<NxbInfo>();

		if (file.exists()) {
			String line;
			String[] fieldArray;
			String keyword;

			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));

				// Check This is NXB3 or not.
				if ((line = reader.readLine()) != null) {
					line.trim();
					if (line.equals(NXB3_FORMAT_KEYWORD)) {
						// Parse as Jason
						parseNxb3Data(infoList, reader);

						return infoList;
					}
				}

				while (line != null) {
					line = line.trim();

					fieldArray = line.split(FIELD_SEPARATOR);
					NxbInfo info = new NxbInfo();

					if (isURLValid(fieldArray[URL_FIELD_INDEX])) {
						info.setUrl(fieldArray[URL_FIELD_INDEX]);

						if (fieldArray.length > KEYWORD_INDICATES_NEWFORMAT_FIELD_INDEX) {
							keyword = fieldArray[KEYWORD_INDICATES_NEWFORMAT_FIELD_INDEX];

							if (keyword.equals(NXB2_FORMAT_KEYWORD)) {
								String[] extraArray = fieldArray[EXTRA_FIELD_INDEX].split(EXTRA_FIELD_SEPARATOR);
								info.setType(extraArray[EXTRA_FIELD_INDEX_TYPE]);
								if (extraArray.length > EXTRA_FIELD_INDEX_TITLE) {
									info.setTitle(extraArray[EXTRA_FIELD_INDEX_TITLE]);

									for (int i = 2; i < extraArray.length; i++)
										info.addExtra(extraArray[i]);
								}
							} else {
								int i;
								for (i = 1; i < fieldArray.length; i++) {
									if (!fieldArray[i].equals(""))
										break;
								}

								int separatorIndex = fieldArray[i].indexOf(EXTRA_FIELD_SEPARATOR);
								String title = fieldArray[i];
								if (separatorIndex > 0) {
									info.setType("vm");
									info.setExtra(title);
									title = NexFileIO.getContentTitle(fieldArray[URL_FIELD_INDEX]);
								}

								info.setTitle(title);
							}
						}

						infoList.add(info);
					}

					line = reader.readLine();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return infoList;
	}

	private static boolean isURLValid(String url) {
		Pattern pat = Pattern.compile("[a-zA-Z0-9]*://*");
		Matcher mat;

		if (url == null)
			return false;

		mat = pat.matcher(url);
		return mat.find();
	}

	private static boolean parseNxb3Data(ArrayList<NxbInfo> infoList, BufferedReader reader) {
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				line = "{" + line + "}";

				NxbInfo info = new NxbInfo();
				JSONObject jsonObj = new JSONObject(line);

				Iterator<String> keys = jsonObj.keys();
				String[] arrExtra = {"", ""};
				while (keys.hasNext()) {
					String key = keys.next();
					String value = jsonObj.getString(key);

					Log.d(LOG_TAG, "Key : " + key + "  Value : " + value);

					if (key.equalsIgnoreCase("url")) {
						info.setUrl(value);
					} else if (key.equalsIgnoreCase("title")) {
						info.setTitle(value);
					} else if (key.equalsIgnoreCase("subtitle")) {
						JSONArray subtitles = jsonObj.getJSONArray(key);
						for( int i = 0; i < subtitles.length(); i++ ) {
							info.addSubtitle(subtitles.getString(i));
						}
					} else if (key.equalsIgnoreCase("drmtype")) {
						info.setType(value);
					} else if (key.equalsIgnoreCase("VCAS")) {
						arrExtra[0] = value;
					} else if (key.equalsIgnoreCase("VCAS_COMPANY")) {
						arrExtra[1] = value;
					} else if (key.equalsIgnoreCase("LAURL")) {
						arrExtra[0] = value;
					}
				}

				if (info.getType().equalsIgnoreCase("vm")) {
					info.setExtra(arrExtra[0] + "&&" + arrExtra[1]);
				} else if (info.getType().equalsIgnoreCase("mediadrm")) {
					info.setExtra(arrExtra[0]);
				} else if (info.getType().equalsIgnoreCase("wvdrm")) {
					info.setExtra(arrExtra[0]);
				}

				if( !TextUtils.isEmpty(info.getUrl()) )
					infoList.add(info);
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}
}
