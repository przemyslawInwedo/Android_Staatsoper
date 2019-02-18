package com.nexstreaming.app.apis;

import android.text.TextUtils;
import android.util.Log;

import com.nexstreaming.nexplayerengine.NexContentInformation;
import com.nexstreaming.nexplayerengine.NexID3TagText;
import com.nexstreaming.nexplayerengine.NexPlayer;
import com.nexstreaming.nexplayerengine.NexStreamInformation;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

class CaptionStreamList {
    private ArrayList<CaptionInformation> mGroupList = new ArrayList<>();
    private boolean mCEA608Exist = false, mCEA708Exist = false;
    private static final String LOG_TAG = "CaptionStreamList";
    private int mEnabledIndex;
    private int mPreferCCType;

    boolean checkClosedCaptionInCaptionStreams(int captionType) {
        boolean inStream = false;

        for (int i = 0; i < mGroupList.size(); ++i) {
            if (captionType == mGroupList.get(i).captionType) {
                if (null != mGroupList.get(i).inStreamId) {
                    inStream = true;
                    break;
                }
            }
        }

        return inStream;
    }

    void clear() {
        mGroupList.clear();
        mCEA608Exist = false;
        mCEA708Exist = false;
        mEnabledIndex = -1;
    }

    int getEnabledIndex() {
        return mEnabledIndex;
    }

    void setPreferCCType(int preferCCType) {
        mPreferCCType = preferCCType;
    }

    void maybeActivateCCStreams(int streamId, int currentCaptionType, int captionType) {
        if ((NexContentInformation.NEX_TEXT_CEA608 == captionType || NexContentInformation.NEX_TEXT_CEA708 == captionType) && (!mCEA608Exist || !mCEA708Exist)) {
            if (!checkClosedCaptionInCaptionStreams(captionType)) {
                if (mGroupList.isEmpty()) {
                    mGroupList.add(new CaptionInformation("Disabled", NexPlayer.MEDIA_STREAM_DISABLE_ID, NexContentInformation.NEX_TEXT_UNKNOWN, false));
                }

                for (int i = 0; i < mGroupList.size(); ++i) {
                    if (NexContentInformation.NEX_TEXT_CEA == mGroupList.get(i).captionType && null == mGroupList.get(i).inStreamId) {
                        mEnabledIndex = -1;
                        mGroupList.remove(i);
                        break;
                    }
                }

                boolean target = false;
                int position = mGroupList.size();
                if (NexContentInformation.NEX_TEXT_CEA608 == captionType && !mCEA608Exist) {
                    mCEA608Exist = true;
                    String[] arrStrTextStream = new String[]{"Embedded-CEA608", "Embedded-CEA608-TEXTMODE"};
                    if (currentCaptionType == NexContentInformation.NEX_TEXT_CEA608 && mPreferCCType == NexContentInformation.NEX_TEXT_CEA608) {
                        target = true;
                    }

                    int DEFAULT_CEA608_CHANNEL = 1, DEFAULT_CEA608_TEXTMODE = 4;
                    for (String anArrStrTextStream : arrStrTextStream) {
                        mGroupList.add(new CaptionInformation(anArrStrTextStream, streamId, DEFAULT_CEA608_CHANNEL, captionType, false));
                        DEFAULT_CEA608_CHANNEL += DEFAULT_CEA608_TEXTMODE;
                    }
                } else if (NexContentInformation.NEX_TEXT_CEA708 == captionType && !mCEA708Exist) {
                    mCEA708Exist = true;
                    if (currentCaptionType == NexContentInformation.NEX_TEXT_CEA708 && mPreferCCType == NexContentInformation.NEX_TEXT_CEA708) {
                        target = true;
                    }

                    mGroupList.add(new CaptionInformation("Embedded-CEA708", streamId, 0, captionType, false));
                }

                if (target) {
                    setEnable(position);
                }
            }
        }
    }

    ArrayList<CaptionInformation> getCaptionGroupList() {
        return mGroupList;
    }

    private boolean existStreamId(int streamId) {
        boolean exist = false;
        for (int i = 0; i < mGroupList.size(); ++i) {
            if (streamId == mGroupList.get(i).streamId) {
                exist = true;
                break;
            }
        }

        return exist;
    }

    boolean updateCaptionType(NexContentInformation contentInfo) {
        boolean update = false;
        for (CaptionInformation captionStream : mGroupList) {
            if (contentInfo.mCurrTextStreamID == captionStream.streamId) {
                if (contentInfo.mCaptionType != captionStream.captionType && contentInfo.mCaptionType != NexContentInformation.NEX_TEXT_CEA &&
                        captionStream.captionType == NexContentInformation.NEX_TEXT_UNKNOWN) {
                    captionStream.captionType = contentInfo.mCaptionType;
                    update = true;
                    Log.d(LOG_TAG, "streamId : " + captionStream.streamId + " , updateCaptionType : " + captionStream.captionType);
                }
                break;
            }
        }

        return update;
    }

    void setCaptionStreams(NexContentInformation contentInformation) {
        if (null != contentInformation) {
            for (NexStreamInformation streamInfo : contentInformation.mArrStreamInformation) {
                if (streamInfo.mType == NexPlayer.MEDIA_STREAM_TYPE_TEXT) {
                    if (existStreamId(streamInfo.mID)) {
                        continue;
                    }

                    boolean target = streamInfo.mID == contentInformation.mCurrTextStreamID;
                    switch (streamInfo.mRepresentCodecType) {
                        case NexContentInformation.NEX_TEXT_CEA:
                            if (!TextUtils.isEmpty(streamInfo.mInStreamID)) {
                                final String KEYWORD_CEA608 = "CC";
                                final String KEYWORD_CEA708 = "SERVICE";

                                if (streamInfo.mInStreamID.toUpperCase().startsWith(KEYWORD_CEA608)) {
                                    mGroupList.add(new CaptionInformation(getStringByID3TagInfo(streamInfo.mName), streamInfo.mID, getStringByID3TagInfo(streamInfo.mLanguage), streamInfo.mInStreamID, NexContentInformation.NEX_TEXT_CEA608, target));
                                    mCEA608Exist = true;
                                } else if (streamInfo.mInStreamID.toUpperCase().startsWith(KEYWORD_CEA708)) {
                                    mGroupList.add(new CaptionInformation(getStringByID3TagInfo(streamInfo.mName), streamInfo.mID, getStringByID3TagInfo(streamInfo.mLanguage), streamInfo.mInStreamID, NexContentInformation.NEX_TEXT_CEA708, target));
                                    mCEA708Exist = true;
                                }
                            } else {
                                mGroupList.add(new CaptionInformation("Embedded-CEAX08", streamInfo.mID, NexContentInformation.NEX_TEXT_CEA, target));
                            }
                            break;
                        default:
                            mGroupList.add(new CaptionInformation(getStringByID3TagInfo(streamInfo.mName), streamInfo.mID, getStringByID3TagInfo(streamInfo.mLanguage), streamInfo.mInStreamID, streamInfo.mRepresentCodecType, target));
                            break;
                    }

                    Log.d(LOG_TAG, "changed target : " + target);
                }
            }

            if (mGroupList.isEmpty() && NexContentInformation.NEX_TEXT_UNKNOWN != contentInformation.mCaptionType && NexContentInformation.NEX_TEXT_CEA != contentInformation.mCaptionType) {
                if (!existStreamId(NexPlayer.MEDIA_STREAM_DEFAULT_ID)) {
                    mGroupList.add(new CaptionInformation("Enabled", NexPlayer.MEDIA_STREAM_DEFAULT_ID, contentInformation.mCaptionType, true));
                    mEnabledIndex = 1;
                }
            }

            if (!mGroupList.isEmpty()) {
                if (!existStreamId(NexPlayer.MEDIA_STREAM_DISABLE_ID)) {
                    mGroupList.add(0, new CaptionInformation("Disabled", NexPlayer.MEDIA_STREAM_DISABLE_ID, NexContentInformation.NEX_TEXT_UNKNOWN, false));
                }
            }
        }
    }

    void setEnable(int captionIndex) {
        boolean changed = false;
        if (mGroupList.size() > captionIndex && 0 <= captionIndex) {
            for (CaptionInformation captionInformation : mGroupList) {
                captionInformation.target = false;
            }

            mGroupList.get(captionIndex).target = true;
            changed = true;
            mEnabledIndex = captionIndex;
        }

        Log.d(LOG_TAG, "changed target : " + changed + " index : " + captionIndex);
    }

    private String getStringByID3TagInfo(NexID3TagText text) {
        String ret = "";
        final String STRING_UTF8 = "UTF-8";
        if( text != null && text.getTextData() != null ) {
            try {
                ret = new String(text.getTextData(), 0, text.getTextData().length, STRING_UTF8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}