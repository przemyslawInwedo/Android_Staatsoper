package app.nunc.com.staatsoperlivestreaming.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.nunc.com.staatsoperlivestreaming.Base.Keys;
import app.nunc.com.staatsoperlivestreaming.Model.Results;
import app.nunc.com.staatsoperlivestreaming.R;
import io.reactivex.annotations.NonNull;

public class ListAdapter extends ArrayAdapter<Results> {

    private Context mContext;
    private List<Results> resultsData = new ArrayList<>();

    public ListAdapter(@NonNull Context context, ArrayList<Results> results) {
        super(context, 0, results);
        mContext = context;
        resultsData = results;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_list_stream, parent, false);

        Results currentResult = resultsData.get(position);

        ImageView coverPhoto = listItem.findViewById(R.id.cover_photo);
        String photoUrl = currentResult.getMetaDataList().getImg();

        Picasso.get().load(photoUrl).error(R.drawable.ic_image_placeholder_big).fit().into(coverPhoto, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Log.d("PICASSO_ERROR", e.getMessage());
            }
        });

        //ImageView iv_avatar = listItem.findViewById(R.id.iv_avatar);

        TextView tvDirector = listItem.findViewById(R.id.tv_director);
        tvDirector.setText(currentResult.getMetaDataList().getTitle_ext());

        TextView tvTitle = listItem.findViewById(R.id.tv_title);
        tvTitle.setText(currentResult.getTitle());

        TextView tvDate = listItem.findViewById(R.id.tv_date);

        String input = currentResult.getBeginTime();
        try {
            SimpleDateFormat parser = new SimpleDateFormat("EEE, MMMM dd, yyyy | HH:mm zzz");
            Date date = new SimpleDateFormat(Keys.DATE_PATTERN).parse(input);
            String formattedDate = parser.format(date);
            tvDate.setText(formattedDate);
        } catch (Exception e) {
            Log.d("ERROR ADAPTER","ERROR ADAPTER");
        }

        return listItem;
    }
}