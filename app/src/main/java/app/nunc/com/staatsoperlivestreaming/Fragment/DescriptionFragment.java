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
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import app.nunc.com.staatsoperlivestreaming.Base.Keys;
import app.nunc.com.staatsoperlivestreaming.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DescriptionFragment extends Fragment {

    private int position;
    private SharedPreferences sharedPref;
    private String photoUrl;
    private ImageView coverPhoto;

    public DescriptionFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        position = sharedPref.getInt(Keys.CLICKED_POSITION, 0);

        coverPhoto = view.findViewById(R.id.cover_photo);
        photoUrl = LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getImg();

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
        tvTitle.setText(R.string.description);

        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvDesc.setText(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getLong_description());

        return view;
    }

}
