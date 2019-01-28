package app.nunc.com.staatsoperlivestreaming.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import app.nunc.com.staatsoperlivestreaming.R;

public class StartPlayFragment extends Fragment {

    private int position;
    private ImageView coverPhoto;
    private TabLayout tabLayout;

    public StartPlayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start_play, container, false);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int position = sharedPref.getInt("position", 0);

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
        tvDirector.setText(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getTitle_ext());

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(LiveFragment.events.get(0).getResults().get(position).getTitle());

        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvDesc.setText(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(2).getCasting().get(0).getName());

        return view;
    }

}