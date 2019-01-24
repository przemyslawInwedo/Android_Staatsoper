package app.nunc.com.staatsoperlivestreaming.apis;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import app.nunc.com.staatsoperlivestreaming.R;


public class VideoOnOffPlayerService extends Service {

	private static final String tag = "VideoOnOffPlayerService";
	private static final int NOTIFICATION = R.string.notification;
	
	public static final String INTENT_ACTION_PREV = "intent.action.nexstreaming.apis.prev_background_play";
	public static final String INTENT_ACTION_PLAY_PAUSE = "intent.action.nexstreaming.apis.play_background_play";
	public static final String INTENT_ACTION_NEXT = "intent.action.nexstreaming.apis.next_background_play";
	
	public static final String INTENT_ACTION_START = "intent.action.nexstreaming.apis.start_background_play";

	private static String mCurrentTitle = null;
		
	private NotificationManager mNotiManager;
	

	@Override
	public void onCreate() {
		Log.e(tag, "VideoOnOffPlayerService::onCreate()");
		
		mNotiManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		Log.e(tag, "VideoOnOffPlayerService  onDestroy ");
		mNotiManager.cancel(NOTIFICATION);
		mNotiManager = null;
		
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		Log.e(tag, "VideoOnOffPlayerService  onStart .. action ");
	}

	private void showNotification(Intent intent) {
		String title = intent.getStringExtra("title");
		boolean isPlay = intent.getBooleanExtra("isPlay", true);
		
		if(title == null) 
			title = mCurrentTitle;
		else 
			mCurrentTitle = title;
		
		Log.e(tag, "notiContents :  " + title);
		Log.e(tag, "isPlay :  " + isPlay);
		
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher,
        		getString(R.string.app_name), System.currentTimeMillis());
 
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.noti);
        remoteViews.setTextViewText(R.id.noti_title, title);
        
        Intent prevIntent = new Intent(INTENT_ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0 , prevIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.noti_prev_button, prevPendingIntent);
        
        Intent playPauseIntent = new Intent(INTENT_ACTION_PLAY_PAUSE);
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0 , playPauseIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.noti_play_pause_button, playPausePendingIntent);
        
        if(isPlay)
        	remoteViews.setImageViewResource(R.id.noti_play_pause_button, android.R.drawable.ic_media_pause);
        else
        	remoteViews.setImageViewResource(R.id.noti_play_pause_button, android.R.drawable.ic_media_play);
        	
        Intent nextIntent = new Intent(INTENT_ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0 , nextIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.noti_next_button, nextPendingIntent);
        
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent returnIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NexVideoOnOffSample.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.contentView = remoteViews;
        notification.contentIntent = returnIntent;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        // Send the notification.
        mNotiManager.notify(NOTIFICATION, notification);
	}
	
	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(tag, "VideoOnOffPlayerService  onStartCommand flags:" + flags + " startId:" + startId);
		
		showNotification(intent);
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}