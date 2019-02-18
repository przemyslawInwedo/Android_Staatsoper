package com.nexstreaming.app.apis;

import com.nexstreaming.nexplayerengine.NexContentInformation;
import com.nexstreaming.nexplayerengine.NexCustomAttribInformation;
import com.nexstreaming.nexplayerengine.NexID3TagText;
import com.nexstreaming.nexplayerengine.NexPlayer;
import com.nexstreaming.nexplayerengine.NexStreamInformation;

import java.io.UnsupportedEncodingException;

public class NexContentInfoExtractor {
	
	public static final int VIDEO	= 2;
	public static final int AUDIO	= 1;
	public static final int TEXT	= 0;
	
	private static final int STREAM_OFF = -2;
	
	public static class StreamStatus {
		int streamCount;
		int streamIndex;
		
		public StreamStatus() {
			this.streamCount = 0;
			this.streamIndex = 0;
		}
	}

	private NexContentInfoExtractor() {}
	
	public static int getStreamId(final NexContentInformation contentInfo, int mediaType, int streamIndex) {
		int index = -1;
		if( mediaType == VIDEO ) {
			index = getVideoStreamId(contentInfo, streamIndex);
		}
		else if(mediaType == AUDIO ) {
			index = getAudioStreamId(contentInfo, streamIndex);
		}
		else if(mediaType == TEXT) {
			index = getTextStreamId(contentInfo, streamIndex);
		}
		return index;
	}
	
	public static int getCustomerAttrId(final NexContentInformation contentInfo, int mediaType, int streamIndex) {
		int index = -1;

		if( mediaType == VIDEO ) {
			index = getCustomAttrId(contentInfo, streamIndex);
		}
		return index;
	}
	
	public static StreamStatus getCurStreamStatus(final NexContentInformation contentInfo, int mediaType) {
		
		StreamStatus streamStatus = null;
		if( mediaType == VIDEO ) {
			streamStatus = getCurVideoStreamStatus(contentInfo);
		}
		else if(mediaType == AUDIO ) {
			streamStatus = getCurAudioStreamStatus(contentInfo);
		}
		else if(mediaType == TEXT) {
			streamStatus = getCurTextStreamStatus(contentInfo);
		}
		else {
			streamStatus = new StreamStatus();
		}
		return streamStatus;
	}
	
	public static String[] getStreamDescList(final NexContentInformation contentInfo, int mediaType, int count) {
		String[] desc = null;
		if( mediaType == VIDEO ) {
			desc = getVideoStreamDescList(contentInfo, count);
		}
		else if(mediaType == AUDIO ) {
			desc = getAudioStreamDescList(contentInfo, count);
		}
		else if(mediaType == TEXT) {
			desc = getTextStreamDescList(contentInfo, count);
		}
		else {
			desc = new String[0];
		}
		return desc;
	}
	
	public static  boolean isStreamExist(NexContentInformation contentInfo, int mediaType) {
		boolean ret = false;
		
		if(mediaType == AUDIO) {
			int audioCount = 0;
			for(int i = 0 ; i < contentInfo.mStreamNum; i++) {
				if(contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_AUDIO) {
					audioCount++;
				}
			}
			if( audioCount > 0 ) {
				ret = true;
			}
		} 
		else if( mediaType == VIDEO ) {
			int videoCount = 0;
			for(int i = 0 ; i < contentInfo.mStreamNum; i++) {
				if(contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_VIDEO) {
					videoCount++;
				}
			}
			if( videoCount > 0 ) {
				ret = true;
			}
		}
		else if( mediaType == TEXT ) {
			int textCount = 0;
			for(int i = 0 ; i < contentInfo.mStreamNum; i++) {
				if(contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_TEXT) {
					textCount++;
				}
			}
			if( textCount > 0 ) {
				ret = true;
			}
		}
		return ret;
	}

	public static String getContentInfoSummary(final NexContentInformation contentInfo, final double preAudioChannelCount ) {
		
		String contentSummary	= null;
		String videoCodecInfo	= "Unknown";
		String audioCodecInfo	= "Unknown";
		String captionTypeInfo	= "Unknown";
		
		videoCodecInfo = getVideoCodecString( contentInfo );
		audioCodecInfo = getAudioCodecString( contentInfo );
		captionTypeInfo = getCaptionTypeString( contentInfo );

		if( (contentInfo.mVideoCodec != 0) && (contentInfo.mVideoCodecClass == 0) ) {
			videoCodecInfo += "(SW)";
		}
		else if( (contentInfo.mVideoCodec != 0) && (contentInfo.mVideoCodecClass == 1) ) {
			videoCodecInfo += "(HW)";
		}
		contentSummary = "  [Video]  Codec:" + videoCodecInfo + "   W:" + contentInfo.mVideoWidth + "   H:" + contentInfo.mVideoHeight + "\n";
		
		if( contentInfo.mVideoCodec == NexContentInformation.NEXOTI_H264 )
			contentSummary +=	"    Profile: " + contentInfo.mVideoProfile + "  Level: " + contentInfo.mVideoLevel + "\n";
		

		if( preAudioChannelCount > contentInfo.mAudioNumOfChannel ) {
			contentSummary +=	"  [Audio]  Codec: " + audioCodecInfo + "   SR:" + contentInfo.mAudioSamplingRate + "\n" + 
								"              CN:" + preAudioChannelCount + "->" + contentInfo.mAudioNumOfChannel+"(DownMixed)"+ "\n";
		}
		else {
			contentSummary +=	"  [Audio]  Codec: " + audioCodecInfo + "  SR:" + contentInfo.mAudioSamplingRate + "  CN:" + contentInfo.mAudioNumOfChannel + "\n";
		}
		
		contentSummary +=	"   AVType:" + contentInfo.mMediaType + "  TotalTime:" + contentInfo.mMediaDuration + "\n" +
							"   CaptionType:" + captionTypeInfo + "\n" +
							"  Pause:" + contentInfo.mIsPausable +  "   Seek:" + contentInfo.mIsSeekable + "\n" +
							"   Stream Num:" + contentInfo.mStreamNum + "\n";


		for( int i = 0; i < contentInfo.mStreamNum; i++ ) {
			if (contentInfo.mCurrAudioStreamID == contentInfo.mArrStreamInformation[i].mID) {
				contentSummary +=	"    Audio current Stream ID:" + contentInfo.mCurrAudioStreamID + 
									"   Track  ID:" + contentInfo.mArrStreamInformation[i].mCurrTrackID + 
									"   AttrID:" + contentInfo.mArrStreamInformation[i].mCurrCustomAttrID + "\n";
			}
		}
		
		for( int i = 0; i < contentInfo.mStreamNum; i++ ) {
			if ( contentInfo.mCurrVideoStreamID == contentInfo.mArrStreamInformation[i].mID ) {
				contentSummary += "    Video current Stream ID:"
						+ contentInfo.mCurrVideoStreamID
						+ "   Track  ID:"
						+ contentInfo.mArrStreamInformation[i].mCurrTrackID
						+ "   AttrID:"
						+ contentInfo.mArrStreamInformation[i].mCurrCustomAttrID
						+ "\n";
			}
		}

		for( int i = 0; i < contentInfo.mStreamNum; i++ ) {
			if( contentInfo.mCurrTextStreamID == contentInfo.mArrStreamInformation[i].mID ) {
				if( (contentInfo.mArrStreamInformation[i].mName != null) &&
						(contentInfo.mArrStreamInformation[i].mName.getTextData() != null) ) {
					contentSummary +=	"    Text current Stream ID:" + contentInfo.mCurrTextStreamID + 
										"   Track  ID:" + contentInfo.mArrStreamInformation[i].mCurrTrackID + 
										"   AttrID:" + contentInfo.mArrStreamInformation[i].mCurrCustomAttrID + "\n";
					
				}
			}
		}

		for (int i = 0; i < contentInfo.mStreamNum; i++) {
			contentSummary +=	"       Stream[id:" + contentInfo.mArrStreamInformation[i].mID + "]  type:" + contentInfo.mArrStreamInformation[i].mType + "\n";

			for( int j = 0; j < contentInfo.mArrStreamInformation[i].mTrackCount; j++ ) {
				
				if(contentInfo.mArrStreamInformation[i].mCurrTrackID == contentInfo.mArrStreamInformation[i].mArrTrackInformation[j].mTrackID) {
					contentSummary += "*";
				}
				contentSummary +=	"          Track[id:" + contentInfo.mArrStreamInformation[i].mArrTrackInformation[j].mTrackID + "/" + 
															contentInfo.mArrStreamInformation[i].mArrTrackInformation[j].mCustomAttribID + "]" + 
									"  BW:" + contentInfo.mArrStreamInformation[i].mArrTrackInformation[j].mBandWidth + 
									"  Type:" + contentInfo.mArrStreamInformation[i].mArrTrackInformation[j].mType + 
									"  Valid:" + contentInfo.mArrStreamInformation[i].mArrTrackInformation[j].mValid + 
									"  Rsn:" + contentInfo.mArrStreamInformation[i].mArrTrackInformation[j].mReason + "\n";
			}
		}
		return contentSummary;
	}

	
	private static String getVideoStreamName(NexID3TagText name) {
		String text = "unnamed Video";
		if( (name != null) && (name.getTextData() != null) ) {
			try {
				text = new String( name.getTextData(), 0, name.getTextData().length, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return text;
	}

	public static int getCurrTrackID(int type, NexContentInformation info) {
		int ret = NexPlayer.MEDIA_STREAM_DEFAULT_ID;
		if( info != null ) {
			int currStreamID = NexPlayer.MEDIA_STREAM_DEFAULT_ID;

			switch (type) {
				case NexPlayer.MEDIA_STREAM_TYPE_AUDIO:
					currStreamID = info.mCurrAudioStreamID;
					break;
				case NexPlayer.MEDIA_STREAM_TYPE_VIDEO:
					currStreamID = info.mCurrVideoStreamID;
					break;
				case NexPlayer.MEDIA_STREAM_TYPE_TEXT:
					currStreamID = info.mCurrTextStreamID;
					break;
			}

			for( NexStreamInformation streamInfo : info.mArrStreamInformation ) {
				if (streamInfo.mType == type && currStreamID == streamInfo.mID) {
					ret = streamInfo.mCurrTrackID;
					break;
				}
			}
		}
		return ret;
	}

	private static String getCustomAttrInfo(NexCustomAttribInformation streamInfo ) {
		String text = "(attr : none)";
		
		if (streamInfo.mName != null) {
			NexID3TagText attrName = streamInfo.mName;
			NexID3TagText attrValue = streamInfo.mValue;
			
			try {
				String tmpName = new String(attrName.getTextData(), 0, attrName.getTextData().length, "UTF-8");
				String tmpValue = new String(attrValue.getTextData(), 0, attrValue.getTextData().length, "UTF-8");
				text += "(" + tmpName + " : " + tmpValue + ")";
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} 
		return text;
	}
	
	// Get Current Stream Status
	private static StreamStatus getCurVideoStreamStatus( final NexContentInformation contentInfo ) {

		StreamStatus streamStatus = new StreamStatus();

		int videoCount = 0;
		for( int i=0 ; i<contentInfo.mStreamNum; i++ ) {
			if(contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_VIDEO) {
				if (contentInfo.mCurrVideoStreamID == contentInfo.mArrStreamInformation[i].mID) {
					int j = 0;
					for (j = 0; j < contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length; j++) {
						if (contentInfo.mArrStreamInformation[i].mCurrCustomAttrID == contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation[j].mID) {
							break;
						}
					}
					streamStatus.streamIndex = videoCount + j;
				}
				if(contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length > 0) {
					videoCount += contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length;
				}
				else {
					videoCount++;
				}
			}
		}
		streamStatus.streamCount = videoCount;

		return streamStatus;
	}
	
	private static StreamStatus getCurAudioStreamStatus(final NexContentInformation contentInfo) {
		StreamStatus streamStatus = new StreamStatus();
		
		for( int i = 0 ; i < contentInfo.mStreamNum; i++) {
			if (contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_AUDIO) {
				if (contentInfo.mCurrAudioStreamID == contentInfo.mArrStreamInformation[i].mID) {
					streamStatus.streamIndex = streamStatus.streamCount; 
				}
				streamStatus.streamCount++;
			}
		}
		return streamStatus;
	}
	
	private static StreamStatus getCurTextStreamStatus(final NexContentInformation contentInfo) {
		StreamStatus streamStatus = new StreamStatus();
		
		for (int i = 0; i < contentInfo.mStreamNum; i++) {
			if (contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_TEXT) {
				if (contentInfo.mCurrTextStreamID == contentInfo.mArrStreamInformation[i].mID) {
					streamStatus.streamIndex = streamStatus.streamCount;
				}
				streamStatus.streamCount++;
			}
		}
		return streamStatus;
	}
	
	
	// Get Stream Description List
	private static String[] getVideoStreamDescList(final NexContentInformation contentInfo, int count) {
		String[] arrStrVideoStream;
		arrStrVideoStream = new String[count];
		int i, j, k = 0;
		for (i = 0; i < contentInfo.mStreamNum; i++) {
			if (contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_VIDEO) {
				if (contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length > 0) {
					for (j = 0; j < contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length; j++) {
						arrStrVideoStream[k] = getVideoStreamName(contentInfo.mArrStreamInformation[i].mName) + 
								getCustomAttrInfo(contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation[j]);
						k++;
					}
				}
				else {
					arrStrVideoStream[k] = getVideoStreamName(contentInfo.mArrStreamInformation[i].mName);
					k++;
				}
			}
		}
		return arrStrVideoStream;
	}
	
	private static String[] getAudioStreamDescList(final NexContentInformation contentInfo, int count) {
		
		String[] arrStrAudioStream = new String[count];

		int j=0;
		for (int i = 0; i < contentInfo.mStreamNum; i++) {
			if (contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_AUDIO) {
				
				NexID3TagText name = contentInfo.mArrStreamInformation[i].mName;
				NexID3TagText lang =contentInfo.mArrStreamInformation[i].mLanguage;
				if( (name == null) || (name.getTextData() == null) ) {
					if( lang == null )
						arrStrAudioStream[j++] = "unnamed Audio";
					else{
						try{
							arrStrAudioStream[j++] =  "[ LANG : " + new String( lang.getTextData(), 0, lang.getTextData().length, "UTF-8") + "]";
						}
						catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
				else {
					try {
						if( lang != null ) {
							arrStrAudioStream[j++] = 
									new String( name.getTextData(), 0, name.getTextData().length, "UTF-8") +
									"[ LANG : " + new String( lang.getTextData(), 0, lang.getTextData().length, "UTF-8") + "]";
						}
						else {
							arrStrAudioStream[j++] = 
									new String( name.getTextData(), 0, name.getTextData().length, "UTF-8");
						}
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return arrStrAudioStream;
	}

	private static String[] getTextStreamDescList(final NexContentInformation contentInfo, int count) {
		String[] arrStrTextStream = new String[count];
		int j = 0;
		for (int i = 0; i < contentInfo.mStreamNum; i++) {
			if(contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_TEXT) {
				NexID3TagText name = contentInfo.mArrStreamInformation[i].mName;
				NexID3TagText lang = contentInfo.mArrStreamInformation[i].mLanguage;

				if(name == null || name.getTextData() == null || name.getTextData().length <= 0 ) {
					if( lang == null )
						arrStrTextStream[j++] = "unnamed Text";
					else {
						try {
							arrStrTextStream[j++] = "[ LANG : "
									+ new String(lang.getTextData(), 0, lang.getTextData().length, "UTF-8") + "]";
							;
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
				else {
					try {
						if( lang != null){
							arrStrTextStream[j++] = 
								new String( name.getTextData(), 0, name.getTextData().length, "UTF-8") +"[ LANG : "
								+ new String( lang.getTextData(), 0, lang.getTextData().length, "UTF-8") + "]";
						}
						else{
							arrStrTextStream[j++] = new String( name.getTextData(), 0, name.getTextData().length, "UTF-8");
						}
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return arrStrTextStream;
	}
	
	// Get Stream Id
	private static int getVideoStreamId(NexContentInformation contentInfo, int streamIndex) {
		int streamId = -1;
		int logicalVideoStreamIndex = 0;

		for (int i = 0; i < contentInfo.mStreamNum; i++) {
			if(contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_VIDEO) {
				if( contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length> 0) {
					for( int j = 0 ; j < contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length; j++ ) {
						if( streamIndex == logicalVideoStreamIndex ) {
							streamId = contentInfo.mArrStreamInformation[i].mID;
							break;
						}
						logicalVideoStreamIndex++;
					}
				}
				else {
					if( streamIndex == logicalVideoStreamIndex) {
						streamId = contentInfo.mArrStreamInformation[i].mID;;
						break;
					}
					logicalVideoStreamIndex++;
				}
			}
		}
		return streamId;
	}
	
	private static int getAudioStreamId(NexContentInformation contentInfo, int streamIndex) {
		int streamId = -1;
		int logicalAudioStreamIndex = 0;
		for (int i = 0; i < contentInfo.mStreamNum; i++) {
			if(contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_AUDIO) {
				if( streamIndex == logicalAudioStreamIndex) {
					streamId = contentInfo.mArrStreamInformation[i].mID;
					break;
				}
				logicalAudioStreamIndex++;
			}
		}
		return streamId;
	}
	
	private static int getTextStreamId(NexContentInformation contentInfo, int streamIndex) {
		int streamId = -1;
		int textStreamLogicalIndex = 0;
		for (int i = 0; i < contentInfo.mStreamNum; i++) {
			if( contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_TEXT ) {
				if( streamIndex == textStreamLogicalIndex ) {
					streamId = contentInfo.mArrStreamInformation[i].mID;
					break;
				}
				textStreamLogicalIndex++;
			}
		}
		return streamId; 
	}
	
	private static int getCustomAttrId(NexContentInformation contentInfo, int logicalIndex) {
		int streamId = -1;
		int logicalVideoStreamIndex = 0;
		for (int i = 0; i < contentInfo.mStreamNum; i++) {
			if(contentInfo.mArrStreamInformation[i].mType == NexPlayer.MEDIA_STREAM_TYPE_VIDEO) {
				if( contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length> 0) {
					for( int j = 0 ; j < contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation.length; j++ ) {
						if( logicalIndex == logicalVideoStreamIndex ) {
							streamId = contentInfo.mArrStreamInformation[i].mArrCustomAttribInformation[j].mID;
							break;
						}
						logicalVideoStreamIndex++;
					}
				}
				else {
					logicalVideoStreamIndex++;
				}
			}
		}
		return streamId;
	}
	
	public static String getCaptionTypeString(NexContentInformation info) {
		return getCaptionTypeString(info.mCaptionType);
	}

	public static String getCaptionTypeString(int captionType) {
		switch(captionType) {
			case NexContentInformation.NEX_TEXT_UNKNOWN:
				return "UNKNOWN";
			case NexContentInformation.NEX_TEXT_EXTERNAL_SMI:
				return "SMI";
			case NexContentInformation.NEX_TEXT_EXTERNAL_SRT:
				return "SRT";
			case NexContentInformation.NEX_TEXT_EXTERNAL_SUB:
				return "SUB";
			case NexContentInformation.NEX_TEXT_3GPP_TIMEDTEXT:
				return "3GPP";
			case NexContentInformation.NEX_TEXT_WEBVTT:
				return "WEBVTT";
			case NexContentInformation.NEX_TEXT_TTML:
				return "TTML";
			case NexContentInformation.NEX_TEXT_CEA:
				return "CEA";
			case NexContentInformation.NEX_TEXT_CEA608:
				return "CEA608";
			case NexContentInformation.NEX_TEXT_CEA708:
				return "CEA708";
			default :
				return Integer.toString( captionType );
		}
	}

	public static String getAudioCodecString(NexContentInformation info ) {
		switch(info.mAudioCodec) {
			case NexContentInformation.NEXOTI_AAC:
				return "AAC";
			case NexContentInformation.NEXOTI_AAC_PLUS:
				return "AAC Plus";
			case NexContentInformation.NEXOTI_MPEG2AAC:
				return "MPEG2AAC";
			case NexContentInformation.NEXOTI_MP3inMP4:
				return "MP3inMP4";
			case NexContentInformation.NEXOTI_MP2:
				return "MP2";
			case NexContentInformation.NEXOTI_MP3:
				return "MP3";
			case NexContentInformation.NEXOTI_BSAC:
				return "BSAC";
			case NexContentInformation.NEXOTI_WMA:
				return "WMA";
			case NexContentInformation.NEXOTI_RA:
				return "RA";
			case NexContentInformation.NEXOTI_AC3:
				return "AC3";
			case NexContentInformation.NEXOTI_EC3:
				return "EC3";
			case NexContentInformation.NEXOTI_AC4:
				return "AC4";
			case NexContentInformation.NEXOTI_DRA:
				return "DRA";
			case NexContentInformation.NEXOTI_DTS:
				return "DTS";
			default :
				return Integer.toString(info.mAudioCodec);
		}
	}

	public static String getVideoCodecString(NexContentInformation info ) {
		switch( info.mVideoCodec ) {
			case NexContentInformation.NEXOTI_MPEG4V:
				return "MPEG4V";
			case NexContentInformation.NEXOTI_H263:
				return "H263";
			case NexContentInformation.NEXOTI_H264:
				return "H264";
			case NexContentInformation.NEXOTI_HEVC:
				return "HEVC";
			case NexContentInformation.NEXOTI_WMV:
				return "WMV";
			case NexContentInformation.NEXOTI_RV:
				return "RV";
			case NexContentInformation.NEXOTI_MPEG1:
				return "MPEG1";
			case NexContentInformation.NEXOTI_MPEG2:
				return "MPEG2";
			case NexContentInformation.NEXOTI_MPEG4Sv1:
			case NexContentInformation.NEXOTI_MPEG4Sv2:
				return "MPEG4V";
			case NexContentInformation.NEXOTI_S263:
				return "S263";
			case NexContentInformation.NEXOTI_WMV1:
				return "WMV1";
			case NexContentInformation.NEXOTI_WMV2:
				return "WMV2";
			case NexContentInformation.NEXOTI_WMV3:
				return "WMV3";
			case NexContentInformation.NEXOTI_WVC1:
				return "WVC1";
			case NexContentInformation.NEXOTI_MP43:
				return "MP43";
			default:
				return Integer.toString(info.mVideoCodec);
		}
	}

	public static String getStreamTypeString(int type) {
		switch( type ) {
			case NexPlayer.MEDIA_STREAM_TYPE_AUDIO:
				return "Audio";
			case NexPlayer.MEDIA_STREAM_TYPE_VIDEO:
				return "Video";
			case NexPlayer.MEDIA_STREAM_TYPE_TEXT:
				return "Text";
			default:
				return Integer.toString(type);
		}
	}

	public static String getTrackTypeString(int type) {
		switch( type ) {
			case 1:
				return "Audio";
			case 2:
				return "Video";
			case 3:
				return "Audio/Video";
			case 4:
				return "Text";
			default:
				return Integer.toString(type);
		}
	}

	public static int getCurCustomAttributeID(NexContentInformation info, int type) {
		int attrId = NexPlayer.MEDIA_STREAM_DEFAULT_ID;
		for( NexStreamInformation streamInfo : info.mArrStreamInformation ) {
			if( streamInfo.mType == type ) {
				attrId = streamInfo.mCurrCustomAttrID;
			}
		}
		return attrId;
	}
}