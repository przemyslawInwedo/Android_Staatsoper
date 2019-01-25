package app.nunc.com.staatsoperlivestreaming.apis;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.dialog.DialogBuilder;
import app.nunc.com.staatsoperlivestreaming.dialog.NexStoredInfoDialog;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexOfflineStoreController;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStoredInfoFileUtils;
import app.nunc.com.staatsoperlivestreaming.util.MediaScannerBroadcastReceiver;
import app.nunc.com.staatsoperlivestreaming.util.NexFileIO;

public class TabLocal extends android.support.v4.app.ListFragment implements MediaScannerBroadcastReceiver.MediaScanFinishedListener {

	private static final String LOG_TAG = "TabLocal";

	//File Info
	private ArrayList<File> mFileList;
	public File mCurrentFolder = null;
	public ArrayList<File> mFolderStack = null;

	// Gui Component
	private View mRootView = null;

	// thread information
	private Handler mHandler;

	private SharedPreferences mPref;
	private String mSdkMode = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.tab_local, container, false);
		return mRootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
		mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
		mSdkMode = mPref.getString(getString(R.string.pref_sdk_mode_key), getString(R.string.app_list_default_item));
		init();
	}

	private void init() {
		setMediaScanFinishedListener();
		setBasicData();
		setThreadHandler();
		setGuiComponent();
		updateAfterFolderSwitch();
	}

	private void setBasicData() {
		setFolderORFileData();
		setCurrentFolder();
	}

	private void setFolderORFileData() {
		if( getArguments() != null ) {
			mFolderStack = (ArrayList<File>)getArguments().getSerializable("folderStack");
		}

		if( mFolderStack == null ) {
			mFolderStack = new ArrayList<File>();
		}
		mFileList = new ArrayList<File>();
	}

	/**
	 * set the current FoldFile position.
	 */
	private void setCurrentFolder() {
		if( getArguments() != null ) {
			mCurrentFolder = (File)getArguments().getSerializable("currentFolder");
		}

		if( mCurrentFolder == null ) {
			Map<String, File> externalLocations = ExternalStorage.getAllStorageLocations();

			Iterator<String> iterator = externalLocations.keySet().iterator();
			String key = iterator.next();
			File file = externalLocations.get(key);
			File parentFile = file.getParentFile();

			if( parentFile.canRead() )
				mCurrentFolder = parentFile;
			else
				mCurrentFolder = file.getAbsoluteFile();
		}

		Log.d(LOG_TAG, "mCurrentFolder : " + mCurrentFolder + " file.getAbsoluteFile" + mCurrentFolder.getAbsoluteFile() + " parentFile.canRead() : " + mCurrentFolder.canRead());
	}

	private void setThreadHandler() {
		mHandler = new Handler();
	}

	private void setMediaScanFinishedListener() {
		MediaScannerBroadcastReceiver.addListener(this);
	}

	private void setGuiComponent() {
		setBackButton();
	}

	private void setBackButton() {
		mRootView.findViewById(R.id.prevFolder).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popFolder();
			}
		});
	}
	
    public boolean popFolder() {
		boolean isPop = false;
    	if( mFolderStack != null && mFolderStack.size() > 0 ) {
			mCurrentFolder = mFolderStack.remove(mFolderStack.size()-1);
			isPop = true;

			updateAfterFolderSwitch();
		}
		return isPop;
	}

	@Override
	public void onMediaScanFinished() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				updateAfterFolderSwitch();
			}
		},2);
	}
	/**
	 *  update the current folder
	 */
	private void updateAfterFolderSwitch() {
		if( mFolderStack.size() < 1 ) {
			mRootView.findViewById(R.id.folderHeader).setVisibility(View.GONE);
		}
		else
		{
			try {
				mRootView.findViewById(R.id.folderHeader).setVisibility(View.VISIBLE);
				TextView titleTextView = (TextView)mRootView.findViewById(R.id.folderTitle);

				if(titleTextView != null)
					titleTextView.setText(mCurrentFolder.getName());
			} catch (StackOverflowError e) {
				Log.e(LOG_TAG, " error : " + e.getMessage());
			}
		}
		
		try {
				updateList();
		} catch (StackOverflowError e) {
			Log.e(LOG_TAG, " error : " + e.getMessage());
		}
	}

	private void updateList() {
        mFileList.clear();
        
        try {
        	
        	if(mCurrentFolder !=null) {
				final String[] EXTENSION_LIST = getResources().getStringArray(R.array.visible_extension_list);

        		File[] folderList = mCurrentFolder.listFiles(new FileFilter() {
					private boolean isAccessableDir( File file ) {
						
						if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD)
			        	{
							if(( !file.canRead() || !file.canWrite()) && file.isAbsolute() )
								return false;
							
							if(file.isHidden())
								return false;
							
							if(file.isDirectory()) {
								return true;
							}
						
			        	}
						else
						{
							if((!file.canExecute() || !file.canRead() || !file.canWrite()) && file.isAbsolute() )
								return false;
							
							if(file.isHidden())
								return false;
							
							if(file.isDirectory()) {
								return true;
							}
						}
						
						return false;
					}
			
					@Override
					public boolean accept(File pathname) {
						return (isAccessableDir(pathname));
					}
				});

				File[] fileList = mCurrentFolder.listFiles(new FileFilter() {
					private boolean isMediaFile( File file ) {
						if( file.isHidden() )
							return false;
						
						if( !file.isDirectory() ) {
							String name = file.getName().toLowerCase(Locale.getDefault());

							for(int i = 0; i < EXTENSION_LIST.length; i++)
								if( name.endsWith(EXTENSION_LIST[i]) )
									return true;
						}
						
						return false;
					}
			
					@Override
					public boolean accept(File pathname) {
						return (isMediaFile(pathname));
					}
				});
   
				if(folderList != null) {
					Arrays.sort(folderList);
					mFileList.addAll(Arrays.asList(folderList));
				}
					
				if(fileList != null) {
					Arrays.sort(fileList);
					mFileList.addAll(Arrays.asList(fileList));
				}		
			}

			setListAdapter(new FileListAdapter(getContext(), R.layout.download_row, mFileList));
		} catch (Exception e) {
			Log.e(LOG_TAG, " error : " +e.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		MediaScannerBroadcastReceiver.removeListener(this);
		super.onDestroy();
	}

	protected void showNxbViewWithFile(ArrayList<File> folderStack, File currentFolder, File file) {
		NxbView nxbView = new NxbView();
		Bundle bundle = new Bundle();
		if( file != null )
			bundle.putSerializable("nxbFile", file);
		if( folderStack != null )
			bundle.putSerializable("folderStack", folderStack);
		if( currentFolder != null )
			bundle.putSerializable("currentFolder", currentFolder);
		nxbView.setArguments(bundle);
		((TabLocalContainer)getParentFragment()).replaceFragment(nxbView, true);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		File file = mFileList.get(position);
		String lowerCaseName = file.getName().toLowerCase(Locale.getDefault());

		if( file.isDirectory() ) {
			pushFolder(file);
		} else if(lowerCaseName.endsWith(".nxb") || lowerCaseName.endsWith(".nxb.txt")){
			showNxbViewWithFile(mFolderStack, mCurrentFolder, file);
		} else if(lowerCaseName.endsWith(NexFileIO.STORE_INFO_EXTENSION)) {
			showStoredInfoFileDialog(file, position);
		} else {
			String activityClassName = null;
			boolean shouldLaunch = true;
			IActivityLauncherPlugin launcherPlugin = getActivityLauncherPlugin();
			ArrayList<String> urlList = new ArrayList<String>();

			if ( launcherPlugin != null ) {
				activityClassName = launcherPlugin.getActivityClassName(mSdkMode);
				shouldLaunch = launcherPlugin.shouldLaunchActivity(getContext(), mSdkMode, file.getAbsolutePath(), mPref, urlList);
			}
			
			if ( shouldLaunch ) {
				if ( activityClassName == null ) {
					//activityClassName = app.nunc.com.staatsoperlivestreaming.apis.NexPlayerSample.class.getName();
				}
				startActivity(activityClassName, mFileList, file, position, urlList);
			} // else: the plugin denied to launch the activity		 			
		}		
	}

	private IActivityLauncherPlugin getActivityLauncherPlugin() {
		BaseActivity act = (BaseActivity)getActivity();
		return act.getActivityLauncherPlugin();
	}

	protected void setupAndStartActivity(ArrayList<File> fileList, File file, int position, boolean shouldPlay) {
		if( file != null ) {
			String activityClassName = null;
			boolean shouldLaunch = true;
			IActivityLauncherPlugin launcherPlugin = getActivityLauncherPlugin();
			ArrayList<String> urlList = new ArrayList<String>();

			if ( launcherPlugin != null ) {
				if( shouldPlay )
					activityClassName = launcherPlugin.getActivityClassName(mSdkMode);
				else
					activityClassName = launcherPlugin.getStoreActivityClassName(mSdkMode);
				shouldLaunch = launcherPlugin.shouldLaunchActivity(getActivity(), mSdkMode, file.getAbsolutePath(), mPref, urlList);
			}

			if ( shouldLaunch ) {
				if ( activityClassName == null ) {
					if( shouldPlay )
						activityClassName = NexPlayerSample.class.getName();
					else
						activityClassName = NexStreamDownloaderActivity.class.getName();
				}
				startActivity(activityClassName, fileList, file, position, urlList);
			} // else: the plugin denied to launch the activity
		}
	}

	private void showStoredInfoFileDialog(final File file, final int position) {
		final JSONObject storedInfo = NexStoredInfoFileUtils.parseJSONObject(file);

		if( storedInfo != null ) {
			DialogBuilder.makeDialog(getContext(), file.getName(), DialogBuilder.DialogMode.SET_ITEMS,
					R.array.offline_dialog_items, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
								case 0: // play
									setupAndStartActivity(mFileList, file, position, true);
									break;
								case 1: // continue store
									int percentage = -1;
									try {
										percentage = storedInfo.getInt(NexStoredInfoFileUtils.STORED_INFO_KEY_STORE_PERCENTAGE);
									} catch (JSONException e) {
										e.printStackTrace();
									}

									if (percentage > -1 && percentage < 100) {
										setupAndStartActivity(mFileList, file, position, false);
									}
									break;
								case 2: // delete
									NexOfflineStoreController.deleteOfflineCache(file.getAbsolutePath());
									break;
								case 3: // store info
									NexStoredInfoDialog.show(getContext(), file);
									break;
							}
						}
					},null).show();
		} else {
			Toast.makeText(getContext(), R.string.error_invalid_store_file, Toast.LENGTH_SHORT).show();
		}
	}

	private void startActivity(String className, ArrayList<File> fileData, File file, int position, ArrayList<String> urlList) {
		Intent intent = new Intent();
		intent.setClassName(getActivity().getPackageName(), className );
		intent.putExtra("selectedItem", position);
		
		if ( file != null ) {
			intent.putExtra("selectedURL", file.getAbsolutePath() );
			intent.putExtra("selectedTitle", file.getName() );
 		}
	
		if ( fileData != null ) {
				intent.putExtra("data", fileData);
	 	}
		if ( urlList != null && urlList.size() > 0 ) {
				intent.putStringArrayListExtra("url_array", urlList);
	 	}
	
		startActivity(intent);
	}

	private void pushFolder(File folder) {
		Log.d(LOG_TAG, "pushFolder : " + folder);
		mFolderStack.add(mCurrentFolder);
		mCurrentFolder = folder;
		updateAfterFolderSwitch();
	}

	private class FileListAdapter extends ArrayAdapter<File> {

		public FileListAdapter(Context context, int resource, ArrayList<File> objects) {
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

			TextView labelText = (TextView)view.findViewById(R.id.labeltext);
			TextView detailText = (TextView)view.findViewById(R.id.detailtext);
			ImageView icon = (ImageView)view.findViewById(R.id.entryicon);

			labelText.setText(file.getName());

			String detail = "Hidden:" + file.isHidden() + " File:" + file.isFile() + " Folder:" + file.isDirectory();
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

}
