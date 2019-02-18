package com.nexstreaming.app.apis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

import app.nunc.com.staatsoperlivestreaming.R;
import com.nexstreaming.nexplayerengine.NexContentInformation;

/**
 * Created by bonnie.kyeon on 2015-06-29.
 */
public class CaptionLanguageDialog {

	private Context mContext = null;
	private Listener mListener= null;
	private AlertDialog mDialog = null;

	public interface Listener {
		void onItemClicked(int position, boolean disable);
	}

	public CaptionLanguageDialog(Context context, Listener listener) {
		mContext = context;
		mListener = listener;
	}

	private AlertDialog createDialog(NexContentInformation contentInfo, int nDefaultLanguageIndex) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.select_language);

		String[] listItems = new String[contentInfo.mCaptionLanguages.length + 1];
		int i;
		for( i = 0; i < contentInfo.mCaptionLanguages.length; i++ ) {
			listItems[i] = contentInfo.mCaptionLanguages[i];
		}
		listItems[i] = "Disable";

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_single_choice, listItems);

		builder.setSingleChoiceItems(adapter, nDefaultLanguageIndex, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onItemClicked(which, (which + 1) == adapter.getCount());
				mDialog.dismiss();
			}
		});

		return builder.create();
	}

	public void createAndShowDialog(NexContentInformation contentInfo, int nDefaultLanguageIndex) {
		if( mDialog == null ) {
			mDialog = createDialog(contentInfo, nDefaultLanguageIndex);
		}

		if( !mDialog.isShowing() )
			mDialog.show();
	}

	public void reset() {
		mDialog = null;
	}
}
