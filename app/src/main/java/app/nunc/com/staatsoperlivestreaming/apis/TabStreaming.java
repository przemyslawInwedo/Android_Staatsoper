package app.nunc.com.staatsoperlivestreaming.apis;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.info.NxbInfo;
import app.nunc.com.staatsoperlivestreaming.info.NxbListAdapter;

public class TabStreaming extends ListFragment implements CompoundButton.OnCheckedChangeListener {

	// streaming log database information
	protected ArrayList<NxbInfo> mInfoList;
	protected ArrayList<Integer> mSelectedIndexList = new ArrayList<>();
	protected final CharSequence[] DIALOG_ITEMS = {"Play", "Store"};

	protected SharedPreferences mPref;
	protected NexPreferenceData mPrefData = null;
	private View mRootView = null;

	// Widevine start
	private OptionalHeaderAdapter mOptionalHeader;
	private ArrayList<String> mOptionalList;
	private ArrayList<String> mOptionalHeaderItem = new ArrayList<String>();
	private ListView mListView;
	private Button mAddButton;
	private Button mDeleteButton;
	private EditText mOptionalKey;
	private EditText mOptionalValue;
	// Widevine End
	// NexMediaDrm start
	private CheckBox mMediaDRMCheckBox;

	// NexMediaDrm end
	// NexWVSWDrm start
	private CheckBox mSWDRMCheckBox;
	// NexWVSWDrm end

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.tab_streaming, container, false);
		return mRootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
		mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
		mPrefData = new NexPreferenceData(getContext());
		mPrefData.loadPreferenceData();
		showNewUrlDialog();
		/*init();*/
	}

	/*private void init() {
		setGoToUrlButton();
	}*/

	private void updateListView() {
		mInfoList = PlaybackHistory.getStreamingPlaybackList(getContext());
		// Widevine start
		mOptionalList = PlaybackHistory.getStreamingPlaybackOptionalHeader(getContext());
		// Widevine End
		checkBasicTextView();
		setAdapter();
	}

	private void checkBasicTextView(){
		setTitleVisibility(mInfoList.size() < 1);
	}

	private void setTitleVisibility(boolean visible) {
		int visibility = View.VISIBLE;
		if( !visible )
			visibility = View.GONE;

		mRootView.findViewById(R.id.streaming_text).setVisibility(visibility);
	}

	private void setAdapter() {
		NxbListAdapter adapter = new NxbListAdapter(getContext(), R.layout.nxb_row, mInfoList);
		setListAdapter(adapter);
	}

	/*private void setGoToUrlButton() {
		mRootView.findViewById(R.id.bottom_button).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mPrefData.loadPreferenceData();
				showNewUrlDialog();
			}
		});
	}*/

	private void showNewUrlDialog() {
		final ScrollView linearLayout = (ScrollView)View.inflate(getContext(), R.layout.go_to_url, null);

		// NexWVSWDrm start
//		EditText swDrmServerKeyText = (EditText)linearLayout.findViewById(R.id.sw_drm_server_key_edit_text);
//		mSWDRMCheckBox = (CheckBox)linearLayout.findViewById(R.id.sw_drm_check_box);
//		swDrmServerKeyText.setText(mPrefData.mWidevineDRMServerKey);
//		mSWDRMCheckBox.setText(getCheckBoxString(false));
//		mSWDRMCheckBox.setOnCheckedChangeListener(this);
		// NexWVSWDrm end

		// Widevine start
		SetDRMInterface(linearLayout);
		// Widevine end


		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(getContext());
		alertDialogBuilder.setTitle(getResources().getString(R.string.url_detail)).setView(linearLayout);
		alertDialogBuilder.setPositiveButton(
				getResources().getString(R.string.play),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String url = setContentURL(linearLayout);
						if (url != null) {
//								if( getCheckCount(linearLayout) <= 1 ) {
							NxbInfo info = new NxbInfo();
							info.setUrl(url);
							setupNxbInfo(info, linearLayout);

							ArrayList<NxbInfo> infoList = new ArrayList<>();
							infoList.add(info);
							setupAndStartActivity(infoList, info, 0, true);
//								} else {
//									Toast.makeText(getContext(), R.string.go_to_url_drm_error, Toast.LENGTH_LONG).show();
//								}
						} else {
							Toast.makeText(getContext(), R.string.go_to_url_error, Toast.LENGTH_LONG).show();
						}

					}
				}
		);
		alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// Widevine start
				if(mOptionalHeaderItem != null)
					mOptionalHeaderItem.clear();
				// Widevine end
			}
		});

		alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Widevine start
				if(mOptionalHeaderItem != null)
					mOptionalHeaderItem.clear();
				// Widevine end
			}
		});
		alertDialogBuilder.show();
	}

	private int getCheckCount(ScrollView layout) {
		int count = 0;

		// NexMediaDrm start
		if( mPrefData.mEnableMediaDRM)
			count++;
		// NexMediaDrm end

		// NexWVSWDrm start
		if( mPrefData.mEnableWVSWDRM)
			count++;
		// NexWVSWDrm end

		return count;
	}

	private void setupNxbInfo(NxbInfo info , ScrollView detailLayout) {
		String type = NxbInfo.DEFAULT;
		String extras = null;
		CheckBox checkBox = null;

		if( detailLayout != null ) {

			if( type.equals(NxbInfo.DEFAULT) ) {
				//NexMediaDrm start
				if (mPrefData.mEnableMediaDRM) {
					type = NxbInfo.MEDIADRM;

					EditText drmServerKeyText = (EditText) detailLayout.findViewById(R.id.widevine_drm_server_key_edit_text);
					extras = drmServerKeyText.getText().toString();
				}
				//NexMediaDrm end

				//NexWVSWDrm start
				if( type.equals(NxbInfo.DEFAULT) ) {

					if (mPrefData.mEnableWVSWDRM) {
						type = NxbInfo.WVDRM;
						EditText drmServerKeyText = (EditText) detailLayout.findViewById(R.id.widevine_drm_server_key_edit_text);
						extras = drmServerKeyText.getText().toString();
					}
				}
				//NexWVSWDrm end
			}
		}
		info.setType(type);
		info.setExtra(extras);
	}

	private String getCheckBoxString(boolean isChecked) {
		return getString(isChecked ? R.string.enable : R.string.disable);
	}

	private String setContentURL(ScrollView linearLayout) {
		EditText content = (EditText)linearLayout.findViewById(R.id.content_url);
		String url = content.getText().toString();

		if(!content.getText().toString().equals(""))
			return url;

		return null;
	}

	private void SetDRMInterface(ScrollView linearLayout) {

		//NexMediaDrm start

		mMediaDRMCheckBox = (CheckBox) linearLayout.findViewById(R.id.media_drm_check_box);
		if (mPref.getBoolean(getContext().getString(R.string.pref_enable_media_drm_key),false) ) {
			mMediaDRMCheckBox.setChecked(true);
			mMediaDRMCheckBox.setText("Media DRM Enable");
		} else {
			mMediaDRMCheckBox.setChecked(false);
			mMediaDRMCheckBox.setText("Media DRM Disable");
		}
		mMediaDRMCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = mPref.edit();
				if (isChecked) {
					mMediaDRMCheckBox.setText("Media DRM Enable");
				} else {
					mMediaDRMCheckBox.setText("Media DRM Disable");
				}
				editor.putBoolean(getString(R.string.pref_enable_media_drm_key),isChecked);
				editor.apply();
			}
		});
		//NexMediaDrm end

		//NexWVSWDrm start
		mSWDRMCheckBox = (CheckBox) linearLayout.findViewById(R.id.sw_drm_check_box);
		if (mPref.getBoolean(getContext().getString(R.string.pref_enable_sw_drm_Key),false) ) {
			mSWDRMCheckBox.setChecked(true);
			mSWDRMCheckBox.setText("SW DRM Enable");
		} else {
			mSWDRMCheckBox.setChecked(false);
			mSWDRMCheckBox.setText("SW DRM Disable");
		}
		mSWDRMCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = mPref.edit();
				if (isChecked) {
					mSWDRMCheckBox.setText("SW DRM Enable");
				} else {
					mSWDRMCheckBox.setText("SW DRM Disable");
				}
				editor.putBoolean(getString(R.string.pref_enable_sw_drm_Key),isChecked);
				editor.apply();
			}
		});

		//NexWVSWDrm end


		// Widevine start
		EditText drmServerKeyText = (EditText) linearLayout.findViewById(R.id.widevine_drm_server_key_edit_text);
		drmServerKeyText.setText(mPrefData.mWidevineDRMServerKey);

		mOptionalKey = (EditText) linearLayout.findViewById(R.id.optional_header_key);
		mOptionalValue = (EditText) linearLayout.findViewById(R.id.optional_header_value);
		mAddButton = (Button) linearLayout.findViewById(R.id.addItem);

		mDeleteButton = (Button) linearLayout.findViewById(R.id.deleteItem);
		mOptionalHeader = new OptionalHeaderAdapter(getContext(), mOptionalHeaderItem);
		mListView = (ListView) linearLayout.findViewById(R.id.listView_optional_header);
		mListView.setAdapter(mOptionalHeader);


		mAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String key = "";
				String value = "";
				String item = "";

				if (mOptionalKey.getText().toString() != null) {
					key = mOptionalKey.getText().toString();
				}
				if (mOptionalValue.getText().toString() != null) {
					value = mOptionalValue.getText().toString();
				}
				// Check length and spaces
				if ((key.length() > 0 && !key.matches("\\s"))
						&& (value.length() > 0 && !value.matches("\\s"))) {
					item = key + ":" + value;
					mOptionalHeaderItem.add(item);
					mOptionalHeader.notifyDataSetChanged();
					mListView.setAdapter(mOptionalHeader);
				} else {
					Toast.makeText(getActivity(), "Optional header must have item.", Toast.LENGTH_LONG).show();
				}
				mOptionalValue.setText("");
				mOptionalKey.setText("");
			}
		});
		mDeleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOptionalHeaderItem.size() > 0) {
					mOptionalHeaderItem.remove(mOptionalHeaderItem.size() - 1);
					mOptionalHeader.notifyDataSetChanged();
					mListView.setAdapter(mOptionalHeader);
				} else {
					Toast.makeText(getActivity(), "Optional header is empty.", Toast.LENGTH_LONG).show();
				}

			}
		});
		// Widevine end

	}


	protected IActivityLauncherPlugin getActivityLauncherPlugin() {
		BaseActivity act = (BaseActivity)getActivity();
		return act.getActivityLauncherPlugin();
	}


	protected void setupAndStartActivity(ArrayList<NxbInfo> infoList, NxbInfo info, int position, boolean isPlay) {
		if( info != null ) {
			String activityClassName = null;
			boolean shouldLaunch = true;
			IActivityLauncherPlugin launcherPlugin = getActivityLauncherPlugin();
			ArrayList<String> urlList = new ArrayList<String>();

			if ( launcherPlugin != null ) {
				if( isPlay )
					activityClassName = launcherPlugin.getActivityClassName(mPrefData.mSdkMode);
				else
					activityClassName = launcherPlugin.getStoreActivityClassName(mPrefData.mSdkMode);
				shouldLaunch = launcherPlugin.shouldLaunchActivity(getActivity(), mPrefData.mSdkMode, info.getUrl(), mPref, urlList);
				mSelectedIndexList.add(position);
			}

			if ( shouldLaunch ) {
				if ( activityClassName == null ) {
					if( isPlay )
						activityClassName = NexPlayerSample.class.getName();
					else
						activityClassName = NexStreamDownloaderActivity.class.getName();
				}
				startActivity(activityClassName, info.getUrl(), infoList, mSelectedIndexList, urlList);
				mSelectedIndexList.clear();
			} // else: the plugin denied to launch the activity
		}
	}

	protected void startActivity(String className, String url, ArrayList<NxbInfo> infoList, ArrayList<Integer> positionList, ArrayList<String> urlList) {
		Intent intent = new Intent();
		intent.setClassName(getActivity().getPackageName(), className );
		intent.putExtra("theSimpleUrl", url);

		if ( positionList != null && positionList.size() > 0 ) {
			if (1 == positionList.size()) {
				intent.putExtra("selectedItem", positionList.get(0));
			} else {
				intent.putIntegerArrayListExtra("selectedItemList", positionList);
			}
		}

		if( infoList != null )
			intent.putParcelableArrayListExtra("wholelist", infoList);

		if ( urlList != null && urlList.size() > 0 ) {
			intent.putStringArrayListExtra("url_array", urlList);
		}
		// Widevine start
		if(mOptionalHeaderItem != null){
			if(mOptionalHeaderItem.size() > 0)
				intent.putStringArrayListExtra("WVDRMOptionalHeaders", mOptionalHeaderItem);
		}
		// Widevine end

		startActivity(intent);
		// Widevine start
		if(mOptionalHeaderItem != null)
			mOptionalHeaderItem.clear();
		// Widevine end
	}

	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {
		if( getActivity().hasWindowFocus() ) {
			if (mInfoList != null) {
				final NxbInfo info = mInfoList.get(position);
				// Widevine start
				if(mOptionalList.get(position) != null && mOptionalList.get(position).length()>3) {	// minimum length must be longer than 3
					final String[] optionals = mOptionalList.get(position).split("/");
					for (String option : optionals) {
						mOptionalHeaderItem.add(option);
					}
				}
				// Widevine end

				if (BaseActivity.supportOfflinePlayback(getActivity(), mPrefData.mSdkMode)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle(info.getUrl());
					builder.setItems(DIALOG_ITEMS, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							setupAndStartActivity(mInfoList, info, position, which == 0);
						}
					});
					builder.create().show();
				} else {
					setupAndStartActivity(mInfoList, info, position, true);
				}
			}
		}
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onResume() {
		updateListView();
		super.onResume();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (isChecked) {
			switch (buttonView.getId()) {
				//NexMediaDrm start
//			case R.id.widevine_drm_check_box:
//				//NexWVSWDrm start
////				mSWDRMCheckBox.setChecked(false);
//				//NexWVSWDrm end
//				break;
				//NexMediaDrm end
				//NexWVSWDrm start
//			case R.id.sw_drm_check_box:
//				//NexMediaDrm start
//				mWidevineDRMCheckBox.setChecked(false);
//				//NexMediaDrm end
//				break;
				//NexWVSWDrm end
			}
		}

		buttonView.setText(getCheckBoxString(isChecked));
	}

	// Widevine start
	public class OptionalHeaderAdapter extends BaseAdapter {
		Context context;
		ArrayList<String> list_optional;

		TextView item;


		public OptionalHeaderAdapter(Context context, ArrayList<String> item){
			this.context = context;
			this.list_optional = item;
		}

		@Override
		public int getCount() {
			return this.list_optional.size();
		}

		@Override
		public Object getItem(int position) {
			return list_optional.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if(view==null){
				convertView = LayoutInflater.from(context).inflate(R.layout.optional_header_item,null);
				item = (TextView)convertView.findViewById(R.id.item_key);

			}
			item.setText(list_optional.get(position));

			return convertView;
		}
	}
	// Widevine end
}