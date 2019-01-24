package app.nunc.com.staatsoperlivestreaming.apis;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.dialog.DialogBuilder;
import app.nunc.com.staatsoperlivestreaming.dialog.NexStoredInfoDialog;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexOfflineStoreController;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStoredInfoFileUtils;
import app.nunc.com.staatsoperlivestreaming.util.NexFileIO;

public class TabOfflinePlayback extends ListFragment {
	// streaming log database information
	private ArrayList<File> mStoreFileList = new ArrayList<File>();
	private SharedPreferences mPref;
	private View mRootView = null;
	private String mSdkMode = "";
	private static final Handler mHandler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.tab_offline_playback, container, false);
		return mRootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
		mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
		mSdkMode = mPref.getString(getString(R.string.pref_sdk_mode_key), getString(R.string.app_list_default_item));
		Button clearButton = (Button) mRootView.findViewById(R.id.bottom_button);
		clearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mStoreFileList != null ) {
					for( File file : mStoreFileList ) {
						deleteCacheFolder(file);
					}
					updateUIComponents();
				}
			}
		});
	}

	private void updateUIComponents() {
		updateCacheList();
		setUpTitleVisibility(mStoreFileList.size() < 1);
		setAdapter();
	}

	private void setAdapter() {
		StoreFileListAdapter adapter = new StoreFileListAdapter(getContext(), R.layout.download_row, mStoreFileList);
		setListAdapter(adapter);
	}

	private void updateCacheList() {
		if( !mStoreFileList.isEmpty() )
			mStoreFileList.clear();

		File root = Environment.getExternalStorageDirectory();
		ArrayList<File> folderList = new ArrayList<File>();
		folderList.add(root);
		searchWholeFolderList(folderList, root);
		if( folderList.size() > 0 ) {
			mStoreFileList = searchWholeStoreFileList(folderList);
			Collections.sort(mStoreFileList, new Comparator<File>() {
				@Override
				public int compare(File lhs, File rhs) {
					return Long.valueOf(lhs.lastModified()).compareTo(rhs.lastModified()) == 1 ? -1 : 1;
				}
			});
		}
	}

	private ArrayList<File> searchWholeStoreFileList(ArrayList<File> folderList) {
		ArrayList<File> fileList = new ArrayList<File>();
		if( folderList != null ) {
			for( File folder : folderList ) {
				File[] storeFileArray = folder.listFiles(new StoreFileFilter());
				if( storeFileArray != null ) {
					for( File storeFile : storeFileArray ) {
						fileList.add(storeFile);
					}
				}
			}
		}

		return fileList;
	}
	
	private void setupAndStartActivity(ArrayList<File> fileList, File file, int position, boolean isPlay) {
		if( file != null ) {
			String activityClassName = null;
			boolean shouldLaunch = true;
			BaseActivity act = (BaseActivity)getActivity();
			IActivityLauncherPlugin launcherPlugin = act.getActivityLauncherPlugin();
			ArrayList<String> urlList = new ArrayList<String>();
			
			if ( launcherPlugin != null ) {
				if( isPlay )
					activityClassName = launcherPlugin.getActivityClassName(mSdkMode);
				else
					activityClassName = launcherPlugin.getStoreActivityClassName(mSdkMode);
				shouldLaunch = launcherPlugin.shouldLaunchActivity(getContext(), mSdkMode, file.getAbsolutePath(), mPref, urlList);
			}

			if (shouldLaunch) {
				if (activityClassName == null) {
					if (isPlay)
						activityClassName = NexPlayerSample.class.getName();
					else
						activityClassName = NexStreamDownloaderActivity.class.getName();
				}
				startActivity(activityClassName, fileList, file, position, urlList);
			} // else: the plugin denied to launch the activity
		}
	}

	private void startActivity(String className, ArrayList<File> fileList, File file, int position, ArrayList<String> urlList) {
		Intent intent = new Intent();
		intent.setClassName(getContext().getPackageName(), className);
		intent.putExtra("selectedURL", file.getAbsolutePath());
		intent.putExtra("selectedItem", position);

		if ( fileList != null ) {
			intent.putExtra("data", fileList);
		}

		if (urlList != null && urlList.size() > 0) {
			intent.putStringArrayListExtra("url_array", urlList);
		}

		startActivity(intent);
	}
	private void searchWholeFolderList(ArrayList<File> folderList, File root) {
		if( root != null ) {
			File[] childFileList = root.listFiles(new DirectoryFilter());
			if( childFileList != null ) {
				for( File childFile : childFileList ) {
					folderList.add(childFile);
					searchWholeFolderList(folderList, childFile);
				}
			}
		}
	}

	private void deleteCacheFolder(File file) {
		NexOfflineStoreController.deleteOfflineCache(file.getAbsolutePath());
	}

	private void setUpTitleVisibility(boolean visible) {
		int visibility = visible ? View.VISIBLE : View.GONE;
		mRootView.findViewById(R.id.textview).setVisibility(visibility);
	}

	@Override
	public void onResume() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				updateUIComponents();
			}
		});
		super.onResume();
	}

	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {
		final BaseActivity activity = (BaseActivity)getActivity();
		if (activity.hasWindowFocus()) {
			if (mStoreFileList != null) {
				File file = mStoreFileList.get(position);
				JSONObject obj = NexStoredInfoFileUtils.parseJSONObject(file);
				String title = file.getAbsolutePath();
				if( obj != null ) {
					try {
						title = obj.getString(NexStoredInfoFileUtils.STORED_INFO_KEY_STORE_URL);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				DialogBuilder.makeDialog(getContext(), title, DialogBuilder.DialogMode.SET_ITEMS, R.array.offline_dialog_items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0: // Play
								setupAndStartActivity(mStoreFileList, mStoreFileList.get(position), position, true);
								break;
							case 1: // Continue store
								setupAndStartActivity(mStoreFileList, mStoreFileList.get(position), position, false);
								break;
							case 2:  // Delete
								deleteCacheFolder(mStoreFileList.get(position));
								updateUIComponents();
								break;
							case 3: // store info
								NexStoredInfoDialog.show(getContext(), mStoreFileList.get(position));
								break;
						}
					}
				}, null).show();
			}
		}
		super.onListItemClick(l, v, position, id);
	}

	private class StoreFileListAdapter extends ArrayAdapter<File> {

		public StoreFileListAdapter(Context context, int resource, ArrayList<File> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;

			if(view == null) {
				LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.download_row, parent,false);
			}

			File file = getItem(position);
			JSONObject obj = NexStoredInfoFileUtils.parseJSONObject(file);
			String url = "";
			String detail = "";

			try {
				url = obj.getString(NexStoredInfoFileUtils.STORED_INFO_KEY_STORE_URL);
				int bw = obj.getInt(NexStoredInfoFileUtils.STORED_INFO_KEY_BW);
				int percentage = obj.getInt(NexStoredInfoFileUtils.STORED_INFO_KEY_STORE_PERCENTAGE);
				String audioStreamID = obj.getString(NexStoredInfoFileUtils.STORED_INFO_KEY_AUDIO_STREAM_ID);
				//For backward compatibility, use the following function(optString api).
				String audioTrackID = obj.optString(NexStoredInfoFileUtils.STORED_INFO_KEY_AUDIO_TRACK_ID, "-1");
				String videoStreamID = obj.getString(NexStoredInfoFileUtils.STORED_INFO_KEY_VIDEO_STREAM_ID);
				String textStreamID = obj.getString(NexStoredInfoFileUtils.STORED_INFO_KEY_TEXT_STREAM_ID);
				detail = "Store Percentage : " + percentage + " BW : " + bw + "\n" +
						" Audio Stream ID : " + audioStreamID + " Audio Track ID : " + audioTrackID + " Video Stream ID : " + videoStreamID + " Text Stream ID : " + textStreamID;
			} catch (JSONException e) {
				e.printStackTrace();
			}

			TextView labelText = (TextView)view.findViewById(R.id.labeltext);
			TextView detailText = (TextView)view.findViewById(R.id.detailtext);
			ImageView icon = (ImageView)view.findViewById(R.id.entryicon);

			labelText.setText(url);
			detailText.setText(detail);
			icon.setImageResource(imageResourceForFile(file));

			return view;
		}

		private int imageResourceForFile(File file) {
			if( file.isDirectory() ) {
				return R.drawable.orange_folder;
			}  else {
				return R.drawable.icon_filming;
			}
		}
	}

	private class DirectoryFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	}

	private class StoreFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			boolean accept = true;

			if( pathname.getName().endsWith(NexFileIO.STORE_INFO_EXTENSION) ) {
				JSONObject obj = NexStoredInfoFileUtils.parseJSONObject(pathname);
				if( mSdkMode.equals(getString(R.string.app_list_default_item)) ) {
				}
			} else {
				accept = false;
			}

			return accept;
		}
	}
}
