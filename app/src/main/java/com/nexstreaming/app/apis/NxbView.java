package com.nexstreaming.app.apis;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import app.nunc.com.staatsoperlivestreaming.R;
import com.nexstreaming.app.nxb.info.NxbInfo;
import com.nexstreaming.app.nxb.info.NxbListAdapter;
import com.nexstreaming.app.nxb.info.NxbParser;

import java.io.File;
import java.util.ArrayList;

public class NxbView extends Fragment {

	private static final String LOG_TAG = "NxbView";

	private final CharSequence[] DIALOG_ITEMS = {"Play", "Store"};

	protected View mRootView = null;
	private TextView mTitleView;
	protected ListView mListview;
	protected NxbListAdapter mListAdapter;

	protected SharedPreferences mPref;
	protected String mSdkMode = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.nxbview_activity, container, false);
		return mRootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
		mSdkMode = mPref.getString(getString(R.string.pref_sdk_mode_key), getString(R.string.app_list_default_item));
		setupUIComponents(getNxbFile());
	}

	private void setupUIComponents(File file) {
		setupTitleView(file);
		setupListView(NxbParser.getNxbInfoList(file));
		Button backButton = (Button)mRootView.findViewById(R.id.nxb_back_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((TabLocalContainer)getParentFragment()).popFragmentOrFolder();
			}
		});
	}

	protected File getNxbFile() {
		Bundle bundle = getArguments();
		File file = null;
		if( bundle != null ) {
			file = (File)bundle.getSerializable("nxbFile");
		}

		return file;
	}

	protected IActivityLauncherPlugin getActivityLauncherPlugin() {
		return ActivityLauncherPlugin.getPlugin(getContext());
	}

	protected void setupListView(final ArrayList<NxbInfo> infoList) {
		mListview = (ListView)mRootView.findViewById(R.id.nxb_listview);

		mListAdapter = new NxbListAdapter(getContext(), R.layout.nxb_row, infoList);
		mListview.setAdapter(mListAdapter);

		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, final int position,
                                    long arg3) {
				if( getActivity().hasWindowFocus() ) {
					if( infoList != null ) {
						if( BaseActivity.supportOfflinePlayback(getContext(), mSdkMode) ) {
							AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
							builder.setTitle(infoList.get(position).getUrl());
							builder.setItems(DIALOG_ITEMS, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									setupAndStartActivity(infoList, position, which == 0);
								}
							});
							builder.create().show();
						} else {
							setupAndStartActivity(infoList, position, true);
						}
					}
				}
			}
		});
	}

	protected void setupAndStartActivity(ArrayList<NxbInfo> infoList, int position, boolean isPlay) {
		if( infoList != null ) {
			String activityClassName = null;
			boolean shouldLaunch = true;
			IActivityLauncherPlugin launcherPlugin = getActivityLauncherPlugin();
			ArrayList<String> urlList = new ArrayList<String>();
			String url = infoList.get(position).getUrl();

			if ( launcherPlugin != null ) {
				if( isPlay )
					activityClassName = launcherPlugin.getActivityClassName(mSdkMode);
				else
					activityClassName = launcherPlugin.getStoreActivityClassName(mSdkMode);
				shouldLaunch = launcherPlugin.shouldLaunchActivity(getContext(), mSdkMode, url, mPref, urlList);
			}

			if ( shouldLaunch ) {
				if ( activityClassName == null ) {
					if( isPlay )
						activityClassName = NexPlayerSample.class.getName();
					else
						activityClassName = NexStreamDownloaderActivity.class.getName();
				}
				startActivity(activityClassName, url, infoList, position, urlList);
			} // else: the plugin denied to launch the activity
		}
	}

	protected void startActivity(String className, String url, ArrayList<NxbInfo> infoList, int position, ArrayList<String> urlList) {
		Intent intent = new Intent();
		intent.setClassName(getActivity().getPackageName(), className );
		intent.putExtra("theSimpleUrl", url);
		intent.putExtra("selectedItem", position);
		intent.putParcelableArrayListExtra("wholelist", infoList);

		if ( urlList != null && urlList.size() > 0 ) {
			intent.putStringArrayListExtra("url_array", urlList);
	 	}
		
		startActivity(intent);
	}
	
	/**
	 * set The nxbListview Title.
	 */
	private void setupTitleView(File file) {
		mTitleView =(TextView)mRootView.findViewById(R.id.nxb_Title);
		mTitleView.setText(file.getName());
	}
}
