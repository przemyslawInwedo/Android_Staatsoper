package app.nunc.com.staatsoperlivestreaming.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.apis.NexContentInfoExtractor;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexContentInformation;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStreamInformation;


/**
 * Created by bonnie.kyeon on 2016-05-18.
 */
public class NexContentInfoDialog {

	Dialog mDialog = null;
	IListener mListener = null;
	private double mOriginalChannelCount = 0;
	private static final Handler mHandler = new Handler();

	public NexContentInfoDialog (Context context, IListener l) {
		mListener = l;
		mDialog = new Dialog(context);
		mDialog.setContentView(R.layout.content_info_dialog);
		mDialog.setTitle(context.getString(R.string.content_info));
		mDialog.getWindow().setGravity(Gravity.CENTER);

		TextView text = (TextView) mDialog.findViewById(R.id.content_info_text);
		text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public interface IListener {
		int getAC3DecoderType();
	}

	public void setContentInfo(NexContentInformation contentInfo) {
		updateContentInfoText(contentInfo);
	}

	private String makeContentInfoText(NexContentInformation info) {
		String strContentInfo = "";
		String strTmp;

		// change the codec name.
		String strVideoCodec = NexContentInfoExtractor.getVideoCodecString(info);
		String strAudioCodec = NexContentInfoExtractor.getAudioCodecString(info);
		String strCaptionType = NexContentInfoExtractor.getCaptionTypeString(info);

		int isIFrameTrack = 0;
		for( int i = 0; i < info.mStreamNum; i++ ) {
			if( info.mArrStreamInformation[i].mIsIframeTrack == 1 ) {
				isIFrameTrack = 1;
				break;
			}
		}

		if(info.mVideoCodec != 0 && info.mVideoCodecClass == 0)
			strVideoCodec += "(SW)";
		else if(info.mVideoCodec != 0 && info.mVideoCodecClass == 1)
			strVideoCodec += "(HW)";

		strTmp = "  [Video]  Codec:" + strVideoCodec + "   W:"
				+ info.mVideoWidth + "   H:" + info.mVideoHeight + "\n" + "    IsIFrameTrack : " + isIFrameTrack;

		if((info.mVideoCodec == NexContentInformation.NEXOTI_H264) || (info.mVideoCodec == NexContentInformation.NEXOTI_HEVC))
			strTmp += "  Profile: " + info.mVideoProfile + "  Level: " + info.mVideoLevel +"\n";

		strContentInfo += strTmp;
		//Log.d(LOG_TAG, "(mNexPlayer.getProperties(DolbyAC3Dialog.AC3_PROPERTY_DECODER_TYPE) : " + mNexPlayer.getProperties(DolbyAC3Dialog.AC3_PROPERTY_DECODER_TYPE));

		if(info.mAudioCodec != 0 && info.mAudioCodecClass == 0)
			strAudioCodec += "(SW)";
		else if(info.mAudioCodec != 0 && info.mAudioCodecClass == 1)
			strAudioCodec += "(HW)";
	
		if(mOriginalChannelCount > info.mAudioNumOfChannel) {
			if(mOriginalChannelCount==6) mOriginalChannelCount=5.1;
			if(mOriginalChannelCount==7) mOriginalChannelCount=6.1;
			strTmp = "  [Audio]  Codec: " + strAudioCodec + (mListener.getAC3DecoderType() == 1 ? "(Dolby ATMOS)" : "") + "   SR:"
					+ info.mAudioSamplingRate + "\n              CN:"
					+ mOriginalChannelCount + "->" + info.mAudioNumOfChannel+"(DownMixed)"+ "\n";
		} else {
			strTmp = "  [Audio]  Codec: " + strAudioCodec + (mListener.getAC3DecoderType() == 1 ? "(Dolby ATMOS)" : "") +"   SR:"
					+ info.mAudioSamplingRate + "   CN:"
					+ info.mAudioNumOfChannel + "\n";
			mOriginalChannelCount = info.mAudioNumOfChannel;
		}

		strContentInfo += strTmp;
		strTmp = "    AVType:" + info.mMediaType + "   TotalTime:"
				+ info.mMediaDuration + "\n";

		strContentInfo += strTmp;

		strTmp = "    CaptionType:" + strCaptionType + "\n";

		strContentInfo += strTmp;
		strTmp = "    Pause:" + info.mIsPausable + "   Seek:"
				+ info.mIsSeekable + "\n";
		strContentInfo += strTmp;

		strTmp = "    Stream Num:" + info.mStreamNum + "\n";
		strContentInfo += strTmp;

		strTmp = "";

		for( NexStreamInformation streamInfo : info.mArrStreamInformation ) {
			if (streamInfo.mType == NexPlayer.MEDIA_STREAM_TYPE_AUDIO && info.mCurrAudioStreamID == streamInfo.mID) {
				strTmp = "    Audio current Stream ID:"
						+ info.mCurrAudioStreamID
						+ "   Track  ID:"
						+ streamInfo.mCurrTrackID
						+ "   AttrID:"
						+ streamInfo.mCurrCustomAttrID
						+ "\n";
				break;
			}
		}
		strContentInfo += strTmp;

		strTmp = "";
		for( NexStreamInformation streamInfo : info.mArrStreamInformation ) {
			if (streamInfo.mType == NexPlayer.MEDIA_STREAM_TYPE_VIDEO && info.mCurrVideoStreamID == streamInfo.mID) {
				strTmp = "    Video current Stream ID:"
						+ info.mCurrVideoStreamID
						+ "   Track  ID:"
						+ streamInfo.mCurrTrackID
						+ "   AttrID:"
						+ streamInfo.mCurrCustomAttrID
						+ "\n";
				break;
			}
		}
		strContentInfo += strTmp;

		strTmp = "";
		for( NexStreamInformation streamInfo : info.mArrStreamInformation ) {
			if (streamInfo.mType == NexPlayer.MEDIA_STREAM_TYPE_TEXT && info.mCurrTextStreamID == streamInfo.mID) {
				if(streamInfo.mName != null && streamInfo.mName.getTextData() != null) {
					strTmp = "    Text current Stream ID:"
							+ info.mCurrTextStreamID
							+ "   Track  ID:"
							+ streamInfo.mCurrTrackID
							+ "   AttrID:"
							+ streamInfo.mCurrCustomAttrID;

					if( !TextUtils.isEmpty(streamInfo.mInStreamID) ) {
						strTmp += "   InStream ID :" + streamInfo.mInStreamID;
					}
					strTmp += "\n";
					break;
				}
			}
		}

		strContentInfo += strTmp;

		for (int i = 0; i < info.mStreamNum; i++) {
			NexStreamInformation streamInfo = info.mArrStreamInformation[i];
			strTmp = "       Stream[id:" + streamInfo.mID +
					"]  type: "	+ NexContentInfoExtractor.getStreamTypeString(streamInfo.mType);

			Log.d("test", strTmp + " inStreamID : " + streamInfo.mInStreamID);
			if( streamInfo.mType == NexPlayer.MEDIA_STREAM_TYPE_TEXT && !TextUtils.isEmpty(streamInfo.mInStreamID) ) {
				strTmp += "  InStream ID :" + streamInfo.mInStreamID;
			}
			strTmp += "\n";

			strContentInfo += strTmp;

			for (int j = 0; j < streamInfo.mTrackCount; j++) {
				if(streamInfo.mCurrTrackID == streamInfo.mArrTrackInformation[j].mTrackID) {
					strTmp = "*        ";
				} else {
					strTmp = "          ";
				}

				strTmp += "Track[id:"
						+ streamInfo.mArrTrackInformation[j].mTrackID
						+ "/"
						+ streamInfo.mArrTrackInformation[j].mCustomAttribID
						+ "] BW:"
						+ streamInfo.mArrTrackInformation[j].mBandWidth
						+ "  Type: "
						+ NexContentInfoExtractor.getTrackTypeString(streamInfo.mArrTrackInformation[j].mType)
						+ "  CodecType:"
						+ streamInfo.mArrTrackInformation[j].mCodecType
						+ "  Valid:"
						+ streamInfo.mArrTrackInformation[j].mValid
						+ "  Rsn:"
						+ streamInfo.mArrTrackInformation[j].mReason
						+ "  IFrame:"
						+ streamInfo.mArrTrackInformation[j].mIFrameTrack
						+ "\n";
				strContentInfo += strTmp;
				strTmp = "";
			}
		}

		return strContentInfo;
	}

	private void updateContentInfoText(final NexContentInformation contentInfo) {
		final TextView text = (TextView) mDialog.findViewById(R.id.content_info_text);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				text.setText(makeContentInfoText(contentInfo));
			}
		});
	}

	public void show() {
		if( !mDialog.isShowing() )
			mDialog.show();
	}

	public void dismiss() {
		if( mDialog.isShowing() )
			mDialog.dismiss();
	}
}
