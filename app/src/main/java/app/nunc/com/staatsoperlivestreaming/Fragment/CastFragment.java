package app.nunc.com.staatsoperlivestreaming.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import app.nunc.com.staatsoperlivestreaming.R;

public class CastFragment extends Fragment {

    private int position;
    private ImageView coverPhoto;
    private LinearLayout llCast;
    private boolean isFullCastAdded0, isFullCastAdded1, isFullCastAdded2;

    public CastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cast, container, false);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        position = sharedPref.getInt("position", 0);

        llCast = view.findViewById(R.id.ll_tips);

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
        tvDirector.setText(LiveFragment.events.get(0).getResults().get(position).getTitle());

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("Cast");

        for (int i = 0; i < LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(0).getCasting().size(); i++) {

            if (!isFullCastAdded0) {
                addFullCast(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(0).getCasting().get(i).getName(), LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(0).getCasting().get(i).getRole(), LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(0).getRole_type());
                isFullCastAdded0 = true;
            } else {
                addCast(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(0).getCasting().get(i).getName(), LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(0).getCasting().get(i).getRole());
            }

        }
        for (int j = 0; j < LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(1).getCasting().size(); j++) {
            if (!isFullCastAdded1) {
                addFullCast(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(1).getCasting().get(j).getName(), LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(1).getCasting().get(j).getRole(), LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(1).getRole_type());
                isFullCastAdded1 = true;
            } else {
                addCast(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(1).getCasting().get(j).getName(), LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(1).getCasting().get(j).getRole());
            }
        }

        for (int k = 0; k < LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(2).getCasting().size(); k++) {
            if (!isFullCastAdded2) {
                addFullCast(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(2).getCasting().get(k).getName(), LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(2).getCasting().get(k).getRole(), LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(2).getRole_type());
                isFullCastAdded2 = true;
            } else {
                addCast(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(2).getCasting().get(k).getName(), LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().get(2).getCasting().get(k).getRole());
            }
        }

        return view;
    }

    private void addCast(String name, String value) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.cast_view, null);
        TextView tvName = view.findViewById(R.id.tv_name);
        tvName.setText(name);
        TextView tvValue = view.findViewById(R.id.tv_value);
        tvValue.setText(value);
        llCast.addView(view);

    }

    private void addFullCast(String name, String value, String title) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.full_cast_view, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        TextView tvName = view.findViewById(R.id.tv_name);
        tvName.setText(name);
        TextView tvValue = view.findViewById(R.id.tv_value);
        tvValue.setText(value);
        llCast.addView(view);

    }

}