package app.nunc.com.staatsoperlivestreaming.apis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import app.nunc.com.staatsoperlivestreaming.R;

/**
 * Created by bonnie.kyeon on 2015-08-27.
 */
public class SubtitleChangeDialog {

	private Context mContext = null;
	private Listener mListener= null;
	private AlertDialog mDialog = null;

    private static final int NOT_FOUND = -1;

	public interface Listener {
		void onSubtitleChanged(String subtitlePath);
	}

	public SubtitleChangeDialog(Context context, Listener listener) {
		mContext = context;
		mListener = listener;
	}

	private AlertDialog createDialog(final List<String> subtitleList, String currentSubtitle) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.change_subtitle);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.custom_simple_list_item_single_choice, subtitleList);
		final int index = findCurrentSubtitleIndex(subtitleList, currentSubtitle);

		builder.setSingleChoiceItems(adapter, index, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if( index != which ) {
					String subtitlePath = subtitleList.get(which);
					mListener.onSubtitleChanged(subtitlePath);
					mDialog.dismiss();
				}
			}
		});

		return builder.create();
	}

	private void updateDialog(final List<String> subtitleList, String currentSubtitle) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.custom_simple_list_item_single_choice, subtitleList);
		mDialog.getListView().setAdapter(adapter);
		final int index = findCurrentSubtitleIndex(subtitleList, currentSubtitle);
		mDialog.getListView().setItemChecked(index, true);
		mDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if( index != position ) {
					String subtitlePath = subtitleList.get(position);
					mListener.onSubtitleChanged(subtitlePath);
					mDialog.dismiss();
				}
			}
		});
	}

	private int findCurrentSubtitleIndex(List<String> subtitleList, String currentSubtitle) {
        Log.d("NexPlayerSample", "findCurrentSubtitleIndex subtitleList : " + subtitleList + " currentSubtitle : " + currentSubtitle);
		int index = NOT_FOUND;
		for( int i = 0; i < subtitleList.size(); i++ ) {
			if( subtitleList.get(i).equals(currentSubtitle) ) {
				index = i;
				break;
			}
		}
		return index;
	}

	public void createAndShow(List<String> subtitleList, String currentSubtitle) {
		if( subtitleList != null && subtitleList.size() > 0 ) {
			if( mDialog == null ) {
				mDialog = createDialog(subtitleList, currentSubtitle);
			} else {
				updateDialog(subtitleList, currentSubtitle);
			}

			if( !mDialog.isShowing() )
				mDialog.show();
		}
	}
}
