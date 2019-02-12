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

import java.util.ArrayList;
import java.util.List;

import app.nunc.com.staatsoperlivestreaming.Base.Keys;
import app.nunc.com.staatsoperlivestreaming.Model.Cast;
import app.nunc.com.staatsoperlivestreaming.R;

public class CastFragment extends Fragment {

    private int position;
    private ImageView coverPhoto;
    private LinearLayout llCast;
    private SharedPreferences sharedPref;
    private String photoUrl;
    private boolean isFullCastAdded;
    private List<Cast> castList = new ArrayList<>();

    public CastFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cast, container, false);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        position = sharedPref.getInt(Keys.CLICKED_POSITION, 0);

        if(!LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList().isEmpty())
        {
            castList.addAll(LiveFragment.events.get(0).getResults().get(position).getMetaDataList().getCastList());
        }

        llCast = view.findViewById(R.id.ll_tips);

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
        tvTitle.setText(R.string.cast);
        isFullCastAdded = false;

        for (int i = 0; i < castList.size(); i++) {
            for (int j = 0; j < castList.get(i).getCasting().size(); j++) {

                if (!isFullCastAdded) {
                    addFullCast(castList.get(i).getCasting().get(j).getName(), castList.get(i).getCasting().get(j).getRole(), castList.get(i).getRole_type());
                    isFullCastAdded = true;
                } else {
                    addCast(castList.get(i).getCasting().get(j).getName(), castList.get(i).getCasting().get(j).getRole());
                }
            }isFullCastAdded = false;
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