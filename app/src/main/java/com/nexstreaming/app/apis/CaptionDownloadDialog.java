package com.nexstreaming.app.apis;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import app.nunc.com.staatsoperlivestreaming.R;

import java.util.List;

public class CaptionDownloadDialog {
	private static final String LOG_TAG = "CaptionDownloadDialog";

	public static final int SUCCESS = 0;
	public static final int FAIL = 1;

	private CaptionDownloadIListener mListener = null;
	private Context mContext = null;

	private BroadcastReceiver mDownloadBroadcastReceiver = null;
	private DownloadManager mDownloadManager = null;
	private long mDownloadQueueId;
	private AlertDialog mDialog = null;
	private EditText mURLText = null;

	private String mFilePath = null;
	private int mResult = FAIL;

	public interface  CaptionDownloadIListener {
		void onCaptionDownloadComplete(int result, String captionPath);
	}

	public CaptionDownloadDialog(Context context, CaptionDownloadIListener listener) {
		mContext = context;
		mListener = listener;

		mDownloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
		mDownloadBroadcastReceiver = new BroadcastReceiver() {
			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			@Override
			public void onReceive(Context context, Intent intent) {
				if( intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE) ) {
					DownloadManager.Query query = new DownloadManager.Query();
					query.setFilterById(mDownloadQueueId);
					Cursor cursor = mDownloadManager.query(query);

					if( cursor.getCount() > 0 ) {
						cursor.moveToFirst();

						mFilePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
						int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
						cursor.close();

						if( status == DownloadManager.STATUS_SUCCESSFUL )
							mResult = SUCCESS;

						Log.d(LOG_TAG, "mDownloadBroadcastReceiver mFilePath : " + mFilePath + " status : " + status + " mResult : " + mResult);
					}

					mDialog.dismiss();
				}
			}
		};

	}

	private boolean isDownloaderEnable() {
		boolean enable = true;
		String url = mURLText.getText().toString().trim();
		if( url.equals("") )
			enable = false;

		if( !url.startsWith("http") && !url.startsWith("https") )
			enable = false;

		return enable;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void download(String url) {
		Uri uri = Uri.parse(url);
		DownloadManager.Request request = new DownloadManager.Request(uri);
		List<String> pathSegmentList = uri.getPathSegments();
		request.setTitle("Download");
		request.setDescription("caption");
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pathSegmentList.get(pathSegmentList.size() - 1));
		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
		mDownloadQueueId = mDownloadManager.enqueue(request);
	}

	private void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.pref_download_caption_dialog_title);
		builder.setMessage(R.string.go_to_url_url);

		View view = View.inflate(mContext, R.layout.caption_download_dialog, null);
		mURLText = (EditText)view.findViewById(R.id.url_text);
		final Button downloadBtn = (Button)view.findViewById(R.id.download_button);
		final Button cancelBtn = (Button)view.findViewById(R.id.cancel_button);
		downloadBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if( isDownloaderEnable() ) {
					mDialog.setMessage(mContext.getString(R.string.downloading));
					download(mURLText.getText().toString());
					cancelBtn.setEnabled(false);
					downloadBtn.setEnabled(false);
				}

			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

		mDialog = builder.setView(view).create();
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mListener.onCaptionDownloadComplete(mResult, mFilePath);
				mContext.unregisterReceiver(mDownloadBroadcastReceiver);
			}
		});
	}

	public void createAndShowDialog() {
		if( mDialog == null ) {
			createDialog();
		}

		if( !mDialog.isShowing() ) {
			IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
			mContext.registerReceiver(mDownloadBroadcastReceiver, filter);
			mDialog.show();
		}
	}
}
