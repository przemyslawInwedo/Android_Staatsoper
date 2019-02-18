package com.nexstreaming.app.apis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nexstreaming.app.apis.NexContentInfoExtractor.StreamStatus;
import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.R.array;
import app.nunc.com.staatsoperlivestreaming.R.layout;
import app.nunc.com.staatsoperlivestreaming.R.string;

import com.nexstreaming.nexplayerengine.NexContentInformation;
import com.nexstreaming.nexplayerengine.NexPlayer;

import java.util.ArrayList;
import java.util.Arrays;

class MultiStreamDialog {
	private static final String LOG_TAG = "MultiStreamDialog";
	private Activity mActivity = null;
	private IMultiStreamListener	mListener = null;
	private PlayerSampleUtils mPlayerUtils = null;

	private AlertDialog mMainDialog = null;
	private AlertDialog mAudioStreamDialog	= null;
	private AlertDialog mVideoStreamDialog	= null;
	private AlertDialog mTextStreamDialog	= null;

	private final int MEDIA_TYPE_AUDIO_VIDEO = 3;

	private final static String[] empty = new String[0];

	interface IMultiStreamListener {
		void onAudioStreamDialogUpdated(int streamID);
		void onVideoStreamDialogUpdated(int streamID, int attrID);
		void onTextStreamDialogUpdated(int streamID, int channelId, int captionType, int groupPosition, boolean inStream);
	}
	
	MultiStreamDialog(final Activity activity) {
		mActivity = activity;
		mPlayerUtils = PlayerSampleUtils.sharedInstance();
	}
	
	public void dismiss() {
		dismissDialog(mMainDialog);
		dismissDialog(mAudioStreamDialog);
		dismissDialog(mVideoStreamDialog);
		dismissDialog(mTextStreamDialog);
	}

	private void dismissDialog(AlertDialog dialog) {
		if( dialog != null && dialog.isShowing() ) {
			dialog.dismiss();
		}
	}
	
	private int[] getStreamCountArray(NexContentInformation contentInfo) {
		int[] countList = new int[mActivity.getResources().getStringArray(R.array.multi_list_array).length];
	
		countList[MultiListAdapter.AUDIO] = NexContentInfoExtractor.getCurStreamStatus(contentInfo, NexContentInfoExtractor.AUDIO).streamCount;
		countList[MultiListAdapter.VIDEO] = NexContentInfoExtractor.getCurStreamStatus(contentInfo, NexContentInfoExtractor.VIDEO).streamCount;
		countList[MultiListAdapter.TEXT] = 0;

		return countList;
	}

	public void createAndShow(final NexContentInformation contentInfo, final CaptionStreamList captionStreamList, final IMultiStreamListener listener) {
		if( mMainDialog != null && mMainDialog.isShowing() )
			return;

		mListener = listener;

		if (null != contentInfo) {
			final Builder builder = new Builder(mActivity);
			final MultiListAdapter multiAdapter = new MultiListAdapter(mActivity, layout.multi_row, Arrays.asList(mActivity.getResources()
					.getStringArray(array.multi_list_array)), getStreamCountArray(contentInfo));

			multiAdapter.setEnable(MultiListAdapter.AUDIO, NexContentInfoExtractor.isStreamExist(contentInfo, NexContentInfoExtractor.AUDIO ) );
			multiAdapter.setEnable(MultiListAdapter.VIDEO, NexContentInfoExtractor.isStreamExist(contentInfo, NexContentInfoExtractor.VIDEO) );
			multiAdapter.setEnable(MultiListAdapter.TEXT, (captionStreamList != null && captionStreamList.getCaptionGroupList().size() > 0) );

			builder.setTitle(mActivity.getResources().getString(string.multi_track_title));
			builder.setAdapter(multiAdapter, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case 0:
							showAudioMultiStream(contentInfo);
							break;
						case 1:
							showVideoMultiStream(contentInfo);
							break;
						case 2:
							showTextMultiStream(captionStreamList);
							break;
					}
				}
			});
			mMainDialog = builder.create();
			mMainDialog.show();
		}

	}

	private void showAudioMultiStream(final NexContentInformation contentInfo) {
		Builder audioStreamDialog = new Builder(mActivity);
		audioStreamDialog.setTitle("AudioStream");
		
		StreamStatus streamStatus = NexContentInfoExtractor.getCurStreamStatus(contentInfo, NexContentInfoExtractor.AUDIO );
		String[] textsDisableEnable = empty;

		int curStreamIndex = (contentInfo.mCurrAudioStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID) ? -1 : streamStatus.streamIndex;
		final boolean useDisableText =
						streamStatus.streamCount > 0 &&
						contentInfo.mCurrVideoStreamID != NexPlayer.MEDIA_STREAM_DISABLE_ID &&
						contentInfo.mMediaType == MEDIA_TYPE_AUDIO_VIDEO;
		if( useDisableText ) {
			textsDisableEnable = new String[]{ mActivity.getString(R.string.disable) };
			curStreamIndex++;
		}
		String[] arrStrAudioStream = NexContentInfoExtractor.getStreamDescList(contentInfo, NexContentInfoExtractor.AUDIO, streamStatus.streamCount);
		arrStrAudioStream = mPlayerUtils.concatStringArrays(textsDisableEnable, arrStrAudioStream);
		
		audioStreamDialog.setSingleChoiceItems(arrStrAudioStream, curStreamIndex, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
						if( mListener != null ) {
							int index = whichButton;
							int streamID = NexPlayer.MEDIA_STREAM_DEFAULT_ID;
							if( useDisableText ) {
								if( whichButton == 0 ) {
									streamID = NexPlayer.MEDIA_STREAM_DISABLE_ID;
								} else {
									index--;
								}
							}

							if ( streamID == NexPlayer.MEDIA_STREAM_DEFAULT_ID ) {
								streamID = NexContentInfoExtractor.getStreamId(contentInfo, NexContentInfoExtractor.AUDIO, index);
							}

							Log.d(LOG_TAG, "audioStreamDialog onClick whichButton : " + whichButton + " index : " + index + " streamID : " + streamID);
							mListener.onAudioStreamDialogUpdated(streamID);
						}
					}
				}).setNegativeButton(string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		mAudioStreamDialog = audioStreamDialog.create();
		mAudioStreamDialog.show();
	}

	private void showVideoMultiStream(final NexContentInformation contentInfo) {
		Builder videoStreamDialog = new Builder(mActivity);
		videoStreamDialog.setTitle("VideoStream");

		StreamStatus streamStatus = NexContentInfoExtractor.getCurStreamStatus(contentInfo, NexContentInfoExtractor.VIDEO );
		String[] textsDisableEnable = empty;

		int curStreamIndex = (contentInfo.mCurrVideoStreamID == NexPlayer.MEDIA_STREAM_DISABLE_ID) ? -1 : streamStatus.streamIndex;
		final boolean useDisableText =
						streamStatus.streamCount > 0 &&
						contentInfo.mCurrAudioStreamID != NexPlayer.MEDIA_STREAM_DISABLE_ID &&
						contentInfo.mMediaType == MEDIA_TYPE_AUDIO_VIDEO;
		if( useDisableText ) {
			textsDisableEnable = new String[]{ mActivity.getString(R.string.disable) };
			curStreamIndex++;
		}
		String[] arrStrVideoStream = NexContentInfoExtractor.getStreamDescList(contentInfo, NexContentInfoExtractor.VIDEO, streamStatus.streamCount);
		arrStrVideoStream = mPlayerUtils.concatStringArrays(textsDisableEnable, arrStrVideoStream);
		
		videoStreamDialog.setSingleChoiceItems(arrStrVideoStream, curStreamIndex, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (mListener != null) {
					int index = whichButton;
					int streamID = NexPlayer.MEDIA_STREAM_DEFAULT_ID;
					int attrID = NexPlayer.MEDIA_STREAM_DEFAULT_ID;
					if (useDisableText) {
						if (whichButton == 0) {
							streamID = NexPlayer.MEDIA_STREAM_DISABLE_ID;
						} else {
							index--;
						}
					}

					if ( streamID == NexPlayer.MEDIA_STREAM_DEFAULT_ID ) {
						streamID = NexContentInfoExtractor.getStreamId(contentInfo, NexContentInfoExtractor.VIDEO, index);
						attrID = NexContentInfoExtractor.getCustomerAttrId(contentInfo, NexContentInfoExtractor.VIDEO, index);
					}

					Log.d(LOG_TAG, "videoStreamDialog onClick whichButton : " + whichButton + " index : " + index + " streamID : " + streamID + " attrID : " + attrID);
					mListener.onVideoStreamDialogUpdated(streamID, attrID);
				}
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		
		mVideoStreamDialog = videoStreamDialog.create();
		mVideoStreamDialog.show();
	}

	private void showTextMultiStream(CaptionStreamList captionStreamList) {
		if(captionStreamList != null) {
			final Builder textStreamDialog = new Builder(mActivity);
			textStreamDialog.setTitle("TextStream");
			final ListView listView = new ListView(mActivity);
			textStreamDialog.setView(listView);

			ArrayList<CaptionInformation> list = captionStreamList.getCaptionGroupList();

			listView.setAdapter(new TextStreamAdapter(mActivity, R.layout.simple_list_item_single_choice, list));
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			int position = 0;
			for (CaptionInformation captionInformation : list) {
				if (captionInformation.target) {
					break;
				}

				position++;
			}

			listView.setItemChecked(position, true);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					final CaptionInformation selectedStream = (CaptionInformation) listView.getAdapter().getItem(position);
					if (mListener != null) {
						mListener.onTextStreamDialogUpdated(selectedStream.streamId, selectedStream.channelId, selectedStream.captionType, position, null != selectedStream.inStreamId);
					}
				}
			});

			mTextStreamDialog = textStreamDialog.create();
			mTextStreamDialog.show();
		}
	}

	public boolean isShowing() {
		boolean isShowing = false;
		if( mMainDialog != null && mMainDialog.isShowing() )
			isShowing = true;
		if( mAudioStreamDialog != null && mAudioStreamDialog.isShowing() )
			isShowing = true;
		if( mVideoStreamDialog != null && mVideoStreamDialog.isShowing() )
			isShowing = true;
		if( mTextStreamDialog != null && mTextStreamDialog.isShowing() )
			isShowing = true;

		return isShowing;
	}

	private class TextStreamAdapter extends ArrayAdapter<CaptionInformation> {

		int mRes = -1;
		int mCount = 0;

		TextStreamAdapter(Context context, int resource, ArrayList<CaptionInformation> objects) {
			super(context, resource, objects);
			mRes = resource;
			this.mCount = objects.size();
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@NonNull
		@Override
		public View getView(int position, View convertView, @NonNull ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				v = View.inflate(getContext(), mRes, null);
			}

			CaptionInformation info = getItem(position);

			if (null != info) {
				String text = info.name;

				if( !TextUtils.isEmpty(info.language) ) {
					text += "[" + info.language + "]";
				}

				if( !TextUtils.isEmpty(info.inStreamId) ) {
					switch (info.captionType) {
						case NexContentInformation.NEX_TEXT_CEA608:
							text = "CEA608";
							break;
						case NexContentInformation.NEX_TEXT_CEA708:
							text = "CEA708";
							break;
					}

					text += "<" + info.inStreamId + ">";
				}

				if (text.isEmpty()) {
					text = "unnamed text";
				}

				TextView textView = (TextView)v.findViewById(android.R.id.text1);
				textView.setText(text);
			}

			return v;
		}
	}
}

class CaptionInformation {
	String name;
	int streamId = NexPlayer.MEDIA_STREAM_DISABLE_ID;
	String language;
	String inStreamId;
	boolean target = false;
	int captionType = NexContentInformation.NEX_TEXT_UNKNOWN;
	int channelId = 0;

	CaptionInformation(String name, int streamId, int captionType, boolean target) {
		this.name = name;
		this.streamId = streamId;
		this.captionType = captionType;
		this.target = target;
	}

	CaptionInformation(String name, int streamId, int channelId, int captionType, boolean target) {
		this(name, streamId, captionType, target);
		this.channelId = channelId;
	}

	CaptionInformation(String name, int streamId, String language, String inStreamId, int captionType, boolean target) {
		this(name, streamId, captionType, target);
		this.language = language;
		this.inStreamId = inStreamId;
	}
}
