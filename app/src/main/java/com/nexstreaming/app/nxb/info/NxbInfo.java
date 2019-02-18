package com.nexstreaming.app.nxb.info;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.nexstreaming.app.util.NexFileIO;

import java.util.ArrayList;
import java.util.List;

public class NxbInfo implements Parcelable {

	private String mUrl = null;
	private String mTitle = null;
	private String mType = DEFAULT;
	private String mExtra = null;
	private List<String> mListSubtitle = new ArrayList<String>();

	public static final String DEFAULT = "title";
    public static final String VM = "vm";
    public static final String MEDIADRM = "mediadrm";
	public static final String WVDRM = "wvdrm";

	public static final int VM_ADDRESS_INDEX = 0;
	public static final int VM_COMPANY_INDEX = 1;
	public static final int MEDIA_DRM_SERVER_KEY_INDEX = 0;
	public static final int WV_DRM_PROXY_SERVER_INDEX = 0;

	public NxbInfo() {
	}

	public NxbInfo(String url, String title, String type, String extra) {
		mUrl = url;
		mTitle = title;
		mType = type;
		mExtra = extra;
	}

	public NxbInfo(Parcel src) {
		mUrl = src.readString();
		mTitle = src.readString();
		mType = src.readString();
		mExtra = src.readString();
		src.readStringList(mListSubtitle);
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setType(String type) {
		mType = type;
	}

	public String getType() {
		return mType;
	}

	public void setExtra(String extra) {
		mExtra = extra;
	}

	public void addExtra(String extra) {
		if( extra != null )
			mExtra = mExtra == null ? extra : (mExtra + "&&" + extra);
	}

	public String getExtra() {
		return mExtra;
	}

	public String getExtra(int index) {
		String extra = null;
		if( hasExtraField() ) {
			String[] extraArray = mExtra.split("&&");
			if( extraArray.length > index )
				extra = extraArray[index];
		}

		return extra;
	}

	public void addSubtitle(String subtitle) {
		if( !TextUtils.isEmpty(subtitle) )
			mListSubtitle.add(subtitle);
	}

	public void addSubtitle(int index, String subtitle) {
		if( !TextUtils.isEmpty(subtitle) )
			mListSubtitle.add(index, subtitle);
	}

	public List<String> getSubtitle()
	{
		return mListSubtitle;
	}

	public boolean hasExtraField() {
		return mExtra != null;
	}

	@Override
	public String toString() {
		String str = "URL : " + mUrl +
				", Type : " + mType +
				", Title : " + mTitle +
				", Extra : " + mExtra;
		for( int i = 0; i < mListSubtitle.size(); i++ )
			str += ", Subtitle_" + i + " : " + mListSubtitle.get(i);

		return  str;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mUrl);
		dest.writeString(mTitle);
		dest.writeString(mType);
		dest.writeString(mExtra);
		dest.writeStringList(mListSubtitle);
	}

	public static final Creator<NxbInfo> CREATOR = new Creator<NxbInfo>() {
		@Override
		public NxbInfo createFromParcel(Parcel source) {
			return new NxbInfo(source);
		}

		@Override
		public NxbInfo[] newArray(int size) {
			return new NxbInfo[size];
		}
	};

	public static NxbInfo getNxbInfo(ArrayList<NxbInfo> nxbList, String currentPath, int index) {
		NxbInfo info = null;
		if( nxbList != null )
			info = nxbList.get(index);
		else if( currentPath != null )
			info = new NxbInfo(currentPath, NexFileIO.getContentTitle(currentPath), NxbInfo.DEFAULT, null);

		return info;
	}
}


