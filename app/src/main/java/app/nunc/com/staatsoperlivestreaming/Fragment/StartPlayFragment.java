package app.nunc.com.staatsoperlivestreaming.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nexstreaming.app.apis.BaseActivity;
import com.nexstreaming.app.apis.NexPlayerSample;
import com.nexstreaming.app.nxb.info.NxbInfo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.nunc.com.staatsoperlivestreaming.Base.Keys;
import app.nunc.com.staatsoperlivestreaming.Model.Stream;
import app.nunc.com.staatsoperlivestreaming.Presenter.StreamsPresenter;
import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.StreamsView;

public class StartPlayFragment extends Fragment implements StreamsView {

    private int position;
    public static List<Stream> stream = new ArrayList<>();
    private String id;
    private SharedPreferences sharedPref;
    private ImageView coverPhoto;
    private StreamsPresenter streamsPresenter;
    private Button watchNowButton;
    private View progress;
    protected ArrayList<NxbInfo> mInfoList;
    protected ArrayList<Integer> mSelectedIndexList = new ArrayList<>();

    public StartPlayFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_play, container, false);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        position = sharedPref.getInt(Keys.CLICKED_POSITION, 0);
        id = sharedPref.getString(Keys.STREAM_ID, "0");
        progress = view.findViewById(R.id.progress);
        streamsPresenter = new StreamsPresenter(this);
        streamsPresenter.getStreams(id);
        watchNowButton = view.findViewById(R.id.watch_now_button);
        coverPhoto = view.findViewById(R.id.cover_photo);

        String photoUrl = LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getImg();

        Picasso.get().load(photoUrl).error(R.drawable.ic_image_placeholder_big).fit().into(coverPhoto, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Log.d("PICASSO_ERROR", e.getMessage());
            }
        });

        TextView tvDirector = view.findViewById(R.id.tv_director);
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        try {
            tvDirector.setText(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getTitle_ext());
            tvTitle.setText(LiveFragment.events.get(0).getResults().get(position).getTitle());
            tvDesc.setText(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(2).getCasting().get(0).getName());
        } catch (Exception e) {

        }
        watchNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Toast.makeText(getContext(), stream.get(0).getStreams().get(0).getUrl(), Toast.LENGTH_LONG).show();
                Log.d("STREAM URL", stream.get(0).getStreams().get(0).getUrl());
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("STREAM_URL", stream.get(0).getStreams().get(0).getUrl());
                Toast.makeText(getContext(), stream.get(0).getStreams().get(0).getUrl(), Toast.LENGTH_LONG).show();
                startActivity(i);*/
                startTheVideo(stream.get(0).getStreams().get(0).getUrl());
            }
        });

        return view;
    }

    @Override
    public void setStream(Stream stream) {
        StartPlayFragment.stream.clear();
        StartPlayFragment.stream.add(stream);
        Toast.makeText(getContext(), stream.getStreams().get(0).getUrl(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);

    }

    @Override
    public void onError(Throwable e) {

    }

    public void startTheVideo(String videoUrl) {
        NxbInfo info = new NxbInfo();
        info.setUrl(videoUrl);
        info.setUrl(videoUrl);
        setupNxbInfo(info);
        ArrayList<NxbInfo> infoList = new ArrayList<>();
        infoList.add(info);
        setupAndStartActivity(infoList, info);
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

}