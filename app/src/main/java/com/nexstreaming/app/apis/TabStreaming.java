package com.nexstreaming.app.apis;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import app.nunc.com.staatsoperlivestreaming.R;

import com.nexstreaming.app.nxb.info.NxbInfo;
import com.nexstreaming.app.nxb.info.NxbListAdapter;

import java.util.ArrayList;

public class TabStreaming extends ListFragment implements CompoundButton.OnCheckedChangeListener {

    // streaming log database information
    protected ArrayList<NxbInfo> mInfoList;
    protected ArrayList<Integer> mSelectedIndexList = new ArrayList<>();
    protected final CharSequence[] DIALOG_ITEMS = {"Play", "Store"};

    protected SharedPreferences mPref;
    protected NexPreferenceData mPrefData = null;
    private View mRootView = null;


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
        init();

        final ScrollView linearLayout = (ScrollView) View.inflate(getContext(), R.layout.go_to_url, null);

        Intent i = getActivity().getIntent();
        String videoUrl = i.getStringExtra("STREAM_URL");
        NxbInfo info = new NxbInfo();
        info.setUrl(videoUrl);
        setupNxbInfo(info, linearLayout);
        ArrayList<NxbInfo> infoList = new ArrayList<>();
        infoList.add(info);
        setupAndStartActivity(infoList, info, 0, true);
    }

    private void init() {
        setGoToUrlButton();
    }

    private void updateListView() {
        mInfoList = PlaybackHistory.getStreamingPlaybackList(getContext());
        checkBasicTextView();
        setAdapter();
    }

    private void checkBasicTextView() {
        setTitleVisibility(mInfoList.size() < 1);
    }

    private void setTitleVisibility(boolean visible) {
        int visibility = View.VISIBLE;
        if (!visible)
            visibility = View.GONE;

        mRootView.findViewById(R.id.streaming_text).setVisibility(visibility);
    }

    private void setAdapter() {
        NxbListAdapter adapter = new NxbListAdapter(getContext(), R.layout.nxb_row, mInfoList);
        setListAdapter(adapter);
    }

    private void setGoToUrlButton() {
        mRootView.findViewById(R.id.bottom_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPrefData.loadPreferenceData();
                showNewUrlDialog();
            }
        });
    }

    private void showNewUrlDialog() {


       /* AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(getResources().getString(R.string.url_detail)).setView(linearLayout);
        alertDialogBuilder.setPositiveButton(
                getResources().getString(R.string.play),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//
                }
    }
		);
		alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener()

    {
        @Override
        public void onCancel (DialogInterface dialog){
    }
    });

		alertDialogBuilder.setNegativeButton(

    getResources().

    getString(R.string.cancel), new DialogInterface.OnClickListener()

    {

        @Override
        public void onClick (DialogInterface dialog,int which){
    }
    });
		alertDialogBuilder.show();*/
}

    private int getCheckCount(ScrollView layout) {
        int count = 0;


        return count;
    }

    private void setupNxbInfo(NxbInfo info, ScrollView detailLayout) {
        String type = NxbInfo.DEFAULT;
        String extras = null;
        CheckBox checkBox = null;

        if (detailLayout != null) {

            if (type.equals(NxbInfo.DEFAULT)) {

            }
        }
        info.setType(type);
        info.setExtra(extras);
    }

    private String getCheckBoxString(boolean isChecked) {
        return getString(isChecked ? R.string.enable : R.string.disable);
    }

    private String setContentURL(ScrollView linearLayout) {
        EditText content = (EditText) linearLayout.findViewById(R.id.content_url);
        String url = content.getText().toString();

        if (!content.getText().toString().equals(""))
            return url;

        return null;
    }

    private void SetDRMInterface(ScrollView linearLayout) {


    }


    protected IActivityLauncherPlugin getActivityLauncherPlugin() {
        BaseActivity act = (BaseActivity) getActivity();
        return act.getActivityLauncherPlugin();
    }


    protected void setupAndStartActivity(ArrayList<NxbInfo> infoList, NxbInfo info, int position, boolean isPlay) {
        if (info != null) {
            String activityClassName = null;
            boolean shouldLaunch = true;
            IActivityLauncherPlugin launcherPlugin = getActivityLauncherPlugin();
            ArrayList<String> urlList = new ArrayList<String>();

            if (launcherPlugin != null) {
                if (isPlay)
                    activityClassName = launcherPlugin.getActivityClassName(mPrefData.mSdkMode);
                else
                    activityClassName = launcherPlugin.getStoreActivityClassName(mPrefData.mSdkMode);
                shouldLaunch = launcherPlugin.shouldLaunchActivity(getActivity(), mPrefData.mSdkMode, info.getUrl(), mPref, urlList);
                mSelectedIndexList.add(position);
            }

            if (shouldLaunch) {
                if (activityClassName == null) {
                    if (isPlay)
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
        intent.setClassName(getActivity().getPackageName(), className);
        intent.putExtra("theSimpleUrl", url);

        if (positionList != null && positionList.size() > 0) {
            if (1 == positionList.size()) {
                intent.putExtra("selectedItem", positionList.get(0));
            } else {
                intent.putIntegerArrayListExtra("selectedItemList", positionList);
            }
        }

        if (infoList != null)
            intent.putParcelableArrayListExtra("wholelist", infoList);

        if (urlList != null && urlList.size() > 0) {
            intent.putStringArrayListExtra("url_array", urlList);
        }

        startActivity(intent);
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        if (getActivity().hasWindowFocus()) {
            if (mInfoList != null) {
                final NxbInfo info = mInfoList.get(position);

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
            }
        }

        buttonView.setText(getCheckBoxString(isChecked));
    }

}