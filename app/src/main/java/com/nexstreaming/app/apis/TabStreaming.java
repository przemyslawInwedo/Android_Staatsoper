package com.nexstreaming.app.apis;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;

import com.nexstreaming.app.nxb.info.NxbInfo;
import com.nexstreaming.app.nxb.info.NxbListAdapter;

import java.util.ArrayList;

import app.nunc.com.staatsoperlivestreaming.R;

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

        Intent intent = getActivity().getIntent();
        String videoUrl = intent.getStringExtra("STREAM_URL");
        NxbInfo info = new NxbInfo();
        info.setUrl(videoUrl);
        setupNxbInfo(info);
        ArrayList<NxbInfo> infoList = new ArrayList<>();
        infoList.add(info);
        setupAndStartActivity(infoList, info);
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

    private void setupNxbInfo(NxbInfo info) {
        String type = NxbInfo.DEFAULT;
        String extras = null;
        info.setType(type);
        info.setExtra(extras);
    }

    protected void setupAndStartActivity(ArrayList<NxbInfo> infoList, NxbInfo info) {
        if (info != null) {
            String activityClassName = NexPlayerSample.class.getName();
            ArrayList<String> urlList = new ArrayList<String>();
            startActivity(activityClassName, info.getUrl(), infoList, mSelectedIndexList, urlList);
            mSelectedIndexList.clear();
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
    public void onResume() {
        updateListView();
        super.onResume();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}