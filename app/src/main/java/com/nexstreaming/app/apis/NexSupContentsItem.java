package com.nexstreaming.app.apis;

import android.os.Parcel;
import android.os.Parcelable;

public class NexSupContentsItem implements Parcelable {

	private static final String LOG_TAG = "[ContentsItem] ";
	
	private String mTitle;
	private String mUrl;
	private int 	mType; 
	private long 	mLastModifiedTime;
	private int 	misChecked; 
	private long 	mDuration;
	private int 	misDirectory; 
	
	public NexSupContentsItem()
	{
		this.mTitle = null; 
		this.mUrl = null; 
		this.mType = 0; 
		this.mLastModifiedTime = 0; 
		this.misChecked = 0; 
		this.mDuration = 0; 
		this.misDirectory = 0; 
	}
	
	public NexSupContentsItem(Parcel in)
	{
		readFromParcel(in); 
	}
	
	public NexSupContentsItem(String title, String url, int type, long time, int checked, long duration, int isDir)
	{
		this.mTitle = title; 
		this.mUrl = url; 
		this.mType = type; 
		this.mLastModifiedTime = time;
		this.misChecked = checked; 
		this.mDuration = duration; 
		this.misDirectory = isDir; 
		
	}
	
	// Getter Section 
	public String getTitle()
	{
		return this.mTitle; 
	}
	
	public String getUrl()
	{
		return this.mUrl; 
	}
	
	public int getType()
	{
		return this.mType; 
	}
	
	public long getLastModifiedTime()
	{
		return this.mLastModifiedTime; 
	}
	
	public int getChecked()
	{
		return this.misChecked; 
	}
	
	public long getDuration()
	{
		return this.mDuration; 
	}
	
	public int getIsDirectory()
	{
		return this.misDirectory; 
	}
	
	// Setter Section 
	public void setTitle(String title)
	{
		this.mTitle = title; 
	}
	
	public void setUrl(String url)
	{
		this.mUrl = url; 
	}
	
	public void setType(int type)
	{
		this.mType = type; 
	}
	
	public void setLastModifiedTime(long time)
	{
		this.mLastModifiedTime = time; 
	}
	
	public void setChecked(int checked)
	{
		this.misChecked = checked; 
	}
	
	public void setDuration(long duration)
	{
		this.mDuration = duration; 
	}
	
	public void setIsDirectory(int isDir)
	{
		this.misDirectory = isDir; 
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mTitle);
		dest.writeString(mUrl); 
		dest.writeInt(mType); 
		dest.writeLong(mLastModifiedTime); 
		dest.writeInt(misChecked); 
		dest.writeLong(mDuration); 
	}

	private void readFromParcel(Parcel in) {
		mTitle = in.readString(); 
		mUrl = in.readString(); 
		mType = in.readInt();
		mLastModifiedTime = in.readLong(); 
		misChecked = in.readInt(); 
		mDuration = in.readLong(); 
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public NexSupContentsItem createFromParcel(Parcel in)
		{
			return new NexSupContentsItem(in);
		}
		
		public NexSupContentsItem[] newArray(int size) {
			return new NexSupContentsItem[size];
		}
	};
}
