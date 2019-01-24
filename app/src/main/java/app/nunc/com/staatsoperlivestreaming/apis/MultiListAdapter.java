package app.nunc.com.staatsoperlivestreaming.apis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.nunc.com.staatsoperlivestreaming.R;

public class MultiListAdapter extends ArrayAdapter<String> {
	
	private List<String> mDataList;
	
	public static final int AUDIO	= 0;
	public static final int VIDEO	= 1;
	public static final int TEXT	= 2;
	
	private boolean[] mEnableList = new boolean[3];
	private int[] mStreamCountList = null;
	
	public MultiListAdapter(Context context, int resource,
                            List<String> objects) {
		super(context, resource,  objects);
		mDataList = objects;
	}
	
	public MultiListAdapter(Context context, int resource,
                            List<String> objects, int[] streamCountList) {
		super(context, resource,  objects);
		mDataList = objects;
		mStreamCountList = streamCountList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if(view == null) {
			LayoutInflater inflater = (LayoutInflater)this.getContext().
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = inflater.inflate(R.layout.multi_row, parent, false);
		}
		
		setTitleTextView(view, position);
		setStreamCountTextView(view, position);

		return view;
	}

	private void setTitleTextView(View view, int position) {
		TextView textView = (TextView)view.findViewById(R.id.title_text_view);
		textView.setText(mDataList.get(position));
		textView.setEnabled(isEnabled(position));
	}
	
	private void setStreamCountTextView(View view, int position) {
		if( mStreamCountList != null ) {
			TextView textView = (TextView)view.findViewById(R.id.stream_count_text_view);
			if( mStreamCountList[position] > 0 )
				textView.setText("(" + mStreamCountList[position] + ")");
			textView.setEnabled(isEnabled(position));
		}
	}

	@Override
	public boolean isEnabled(int position) {
		return mEnableList[position];
	}
	
	public void setEnable(int position, boolean isEnable) {
		mEnableList[position] = isEnable;
	}
	
	public void setEnable(int position, boolean isEnable, String msg) {
		mEnableList[position] = isEnable;
		mDataList.set(position, msg);
	}

}
