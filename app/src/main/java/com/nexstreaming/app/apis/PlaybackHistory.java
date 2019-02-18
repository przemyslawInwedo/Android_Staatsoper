package com.nexstreaming.app.apis;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nexstreaming.app.nxb.info.NxbInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class PlaybackHistory {
	private static final String DATABASE_NAME = "nex_player_sample_log.db";
	private static final String LOG_TAG = "PlaybackHistory";
	private static final int DATABASE_VERSION = 2;

	public static final String STREAMING_TABLE_NAME = "streaming_list_table";
	private static final String _ID = "_id";
	private static final String URL = "url";
	private static final String TYPE = "type";
	private static final String EXTRA = "extra";
	private static final String UPDATED_TIME = "updated_time";
	private static final String WVDRM_OPTIONAL = "optional_hedaders";
	private static final String STORE_PATH = "store_path";
	private static final String STORE_BANDWIDTH = "store_bandwidth";
	private static final String STORE_PERCENT = "store_percent";

	private static final String CREATE_STREAMING_TABLE =
			"CREATE TABLE " + STREAMING_TABLE_NAME + " (" +
					_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
					URL + " TEXT ," +
					TYPE + " TEXT ," +
					EXTRA + " TEXT ," +
					UPDATED_TIME + " TEXT ," +
					WVDRM_OPTIONAL + " TEXT" +
					");";

	private SQLiteDatabase mDB;
	private DBOpenHelper mDBHelper;
    private Context mContext;
    
    private static class DBOpenHelper extends SQLiteOpenHelper {
    	
    	public static DBOpenHelper mDBHelperInstance = null;
    	
    	public static DBOpenHelper getInstance(Context context) {
    		if(mDBHelperInstance == null)
    			mDBHelperInstance = new DBOpenHelper(context);
    		
    		return mDBHelperInstance;
    	}
    	
		public DBOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_STREAMING_TABLE);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		Log.d(LOG_TAG, "onUpgrade : oldVersion(" + oldVersion + ") newVersion("+ newVersion + ")");
    		if(newVersion > oldVersion){
				db.execSQL("DROP TABLE IF EXISTS " + STREAMING_TABLE_NAME);
				onCreate(db);
    		}
		}


    }

    public PlaybackHistory(Context context) {
    	mContext = context;
    }

    private PlaybackHistory open() throws SQLException {
        mDBHelper = DBOpenHelper.getInstance(mContext);
	    mDB = mDBHelper.getWritableDatabase();
        
        return this;
    }
 
    private void close() {
    	mDB.close();
    }

	public synchronized static void addHistory(Context context, NxbInfo info) {
		PlaybackHistory dbOpenHelper = new PlaybackHistory(context);
		if( dbOpenHelper != null ) {
			dbOpenHelper.open();
			String tableName = STREAMING_TABLE_NAME;

			ContentValues values = new ContentValues();
			values.put(URL, info.getUrl());
			values.put(TYPE, info.getType());
			values.put(EXTRA, info.getExtra());
			values.put(UPDATED_TIME, getDateTime());

			dbOpenHelper.mDB.insert(tableName, null, values);
			dbOpenHelper.close();
		}
	}

	public synchronized static void addHistory(Context context, NxbInfo info, String WVDRMOptionalHeaders) {
		PlaybackHistory dbOpenHelper = new PlaybackHistory(context);
		if( dbOpenHelper != null ) {
			dbOpenHelper.open();
			String tableName = STREAMING_TABLE_NAME;

			ContentValues values = new ContentValues();
			values.put(URL, info.getUrl());
			values.put(TYPE, info.getType());
			values.put(EXTRA, info.getExtra());
			values.put(UPDATED_TIME, getDateTime());
			values.put(WVDRM_OPTIONAL, WVDRMOptionalHeaders);

			dbOpenHelper.mDB.insert(tableName, null, values);
			dbOpenHelper.close();
		}
	}

	public synchronized static void updateHistory(Context context, NxbInfo info) {
		PlaybackHistory dbOpenHelper = new PlaybackHistory(context);
		if( dbOpenHelper != null ) {
			dbOpenHelper.open();
			String tableName = STREAMING_TABLE_NAME;
			String where = URL + " = '" + info.getUrl() + "'";

			ContentValues values = new ContentValues();
			values.put(UPDATED_TIME, getDateTime());

			dbOpenHelper.mDB.update(tableName, values, where, null);
			dbOpenHelper.close();
		}
	}

	public synchronized static void updateHistory(Context context, NxbInfo info, String WVDRMOptionalHeaders) {
		PlaybackHistory dbOpenHelper = new PlaybackHistory(context);
		if( dbOpenHelper != null ) {
			dbOpenHelper.open();
			String tableName = STREAMING_TABLE_NAME;
			String where = URL + " = '" + info.getUrl() + "'";

			ContentValues values = new ContentValues();
			values.put(UPDATED_TIME, getDateTime());
			values.put(WVDRM_OPTIONAL, WVDRMOptionalHeaders);

			dbOpenHelper.mDB.update(tableName, values, where, null);
			dbOpenHelper.close();
		}
	}

	private static String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Calendar date = Calendar.getInstance();
		return dateFormat.format(date.getTime());
	}

	public synchronized static boolean isExist(Context context, NxbInfo info) {
		PlaybackHistory dbOpenHelper = new PlaybackHistory(context);
		int count = 0;
		if( dbOpenHelper != null ) {
			dbOpenHelper.open();
			String tableName = STREAMING_TABLE_NAME;
			dbOpenHelper.mDB = dbOpenHelper.mDBHelper.getReadableDatabase();
			String where = URL + " = '" + info.getUrl() + "'";
			String select = "SELECT * FROM " + tableName + " WHERE " + where;

			Log.d(LOG_TAG, "isExist info : " + info);
			Log.d(LOG_TAG, "isExist where : " + where);
			Cursor cursor = dbOpenHelper.mDB.rawQuery(select, null);
			count = cursor.getCount();
			dbOpenHelper.close();
		}
		return count > 0;
	}

 	public synchronized static void deleteHistory(Context context, NxbInfo info) {
	    PlaybackHistory dbOpenHelper = new PlaybackHistory(context);
	    if( dbOpenHelper != null ) {
		    dbOpenHelper.open();

		    String tableName = STREAMING_TABLE_NAME;
		    String where = URL + " = '" + info.getUrl() + "'";

		    dbOpenHelper.mDB.delete(tableName, where, null);
		    dbOpenHelper.close();
	    }
 	}

	public synchronized static void deleteHistoryAll(Context context, String tableName) {
		PlaybackHistory dbOpenHelper = new PlaybackHistory(context);
		if( dbOpenHelper != null ) {
			dbOpenHelper.open();
			dbOpenHelper.mDB.delete(tableName, null, null);
			dbOpenHelper.close();
		}
	}

	public synchronized static ArrayList<NxbInfo> getStreamingPlaybackList(Context context) {
		ArrayList<NxbInfo> labels = new ArrayList<NxbInfo>();

		PlaybackHistory dbOpenHelper = new PlaybackHistory(context);
		if( dbOpenHelper != null ) {
			dbOpenHelper.open();
			dbOpenHelper.mDB = dbOpenHelper.mDBHelper.getReadableDatabase();
			String selectQuery = "SELECT * FROM " + STREAMING_TABLE_NAME + " ORDER BY DATETIME(" + UPDATED_TIME + ") DESC";
			Cursor cursor = dbOpenHelper.mDB.rawQuery(selectQuery, null);

			if( cursor.moveToFirst() ) {
				do {
					NxbInfo info = new NxbInfo();
					info.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
					info.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
					info.setExtra(cursor.getString(cursor.getColumnIndex(EXTRA)));

					labels.add(info);
				} while( cursor.moveToNext() );
			}
			cursor.close();
		}
		return labels;
	}

	public synchronized static ArrayList<String> getStreamingPlaybackOptionalHeader(Context context){
		ArrayList<String> labels = new ArrayList<>();

		PlaybackHistory dbOpenHelper = new PlaybackHistory(context);
		if( dbOpenHelper != null ) {
			dbOpenHelper.open();
			dbOpenHelper.mDB = dbOpenHelper.mDBHelper.getReadableDatabase();
			String selectQuery = "SELECT * FROM " + STREAMING_TABLE_NAME + " ORDER BY DATETIME(" + UPDATED_TIME + ") DESC";
			Cursor cursor = dbOpenHelper.mDB.rawQuery(selectQuery, null);

			if( cursor.moveToFirst() ) {
				do {
					labels.add(cursor.getString(cursor.getColumnIndex(WVDRM_OPTIONAL)));
				} while( cursor.moveToNext() );
			}
			cursor.close();
		}
		return labels;

	}

	public synchronized static void checkTableAndReCreateDB(Context context){
    	String dropDBQuery = "DROP TABLE " + STREAMING_TABLE_NAME;
		String selectQuery = "SELECT * FROM " + STREAMING_TABLE_NAME + " ORDER BY DATETIME(" + UPDATED_TIME + ") DESC";

		PlaybackHistory dbOpenHelper = new PlaybackHistory(context);
		if( dbOpenHelper != null ) {

			dbOpenHelper.open();
			dbOpenHelper.mDB = dbOpenHelper.mDBHelper.getReadableDatabase();

			try{
				//Check column index(optional_hedaders)
				Cursor cursor = dbOpenHelper.mDB.rawQuery(selectQuery, null);
				if(cursor.getColumnIndex(WVDRM_OPTIONAL) == -1) {
					Log.d(LOG_TAG, "Drop and recreate table  " + STREAMING_TABLE_NAME);
					dbOpenHelper.mDB.execSQL(dropDBQuery);
					dbOpenHelper.mDB.execSQL(CREATE_STREAMING_TABLE);
				}
			} catch(SQLiteException e){
				if(e.getMessage().contains("no such table")){
					Log.e(LOG_TAG, "Creating table  " + STREAMING_TABLE_NAME );
					dbOpenHelper.mDB.execSQL(CREATE_STREAMING_TABLE);
				}
			}
		}
	}
}
