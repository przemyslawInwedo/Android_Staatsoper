package com.nexstreaming.app.nxb.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import app.nunc.com.staatsoperlivestreaming.R;
import com.nexstreaming.app.util.NexFileIO;

import java.util.ArrayList;

public class NxbListAdapter extends ArrayAdapter<NxbInfo> {

	public NxbListAdapter(Context context, int resource,
                          ArrayList<NxbInfo> objects) {
		super(context, resource,  objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if(view == null)
		{
			LayoutInflater inflater = (LayoutInflater)this.getContext().
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = inflater.inflate(R.layout.nxb_row, parent,false);
		}
		
		NxbInfo nxb = getItem(position);
		setTextview(view , nxb);

		return view;
	}
	
	private void setTextview(View view , NxbInfo nxb) {
		TextView urlTextView = (TextView)view.findViewById(R.id.url_text);
		TextView titleTextView = (TextView)view.findViewById(R.id.title_text);

		if( urlTextView != null )
			urlTextView.setText(nxb.getUrl());

		if( titleTextView != null ) {
			String title = nxb.getTitle();
			titleTextView.setText( title == null ? NexFileIO.getContentTitle(nxb.getUrl()) : title);
		}
	}
}
