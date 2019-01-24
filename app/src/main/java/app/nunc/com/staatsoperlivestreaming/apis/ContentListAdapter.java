package app.nunc.com.staatsoperlivestreaming.apis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.nunc.com.staatsoperlivestreaming.R;

public class ContentListAdapter extends ArrayAdapter<String> {
	
	public ContentListAdapter(Context context, int resource,
                              List<String> objects) {
		super(context, resource,  objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if( view == null ) {
			LayoutInflater inflater = (LayoutInflater)this.getContext().
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.download_row, parent,false);
		}

		String url = getItem(position);
		setUpIconImageView(view);
		setUpTextView(view, url);
		
		return view;
	}
	
	private void setUpIconImageView(View view) {
		ImageView iconView = (ImageView)view.findViewById(R.id.entryicon);
		iconView.setVisibility(View.GONE);
	}
	
	private void setUpTextView(View view , String url) {
		 TextView titleTextView =(TextView)view.findViewById(R.id.labeltext);
		 TextView urlTextView = (TextView)view.findViewById(R.id.detailtext);
		 
		 titleTextView.setText(getTitle(url));
		 urlTextView.setText(url);
	}
	
	private String getTitle(String url) {
		return url.substring(url.lastIndexOf("/") + 1, url.length());
	}
}
