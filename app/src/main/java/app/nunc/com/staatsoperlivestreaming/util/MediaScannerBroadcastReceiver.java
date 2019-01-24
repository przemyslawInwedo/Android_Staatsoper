package app.nunc.com.staatsoperlivestreaming.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class MediaScannerBroadcastReceiver extends BroadcastReceiver {
	public interface MediaScanFinishedListener {
		void onMediaScanFinished();
	}
	
	static private List<MediaScanFinishedListener> listeners = new ArrayList<MediaScanFinishedListener>();
    @Override
    public void onReceive(Context context, Intent intent) {
    	if (intent.getAction().equals(Intent.ACTION_MEDIA_SCANNER_FINISHED))
    	{
    		for (MediaScanFinishedListener l : listeners)
    		{
    			l.onMediaScanFinished();
    		}
    	}
    }
    
    static public void addListener(MediaScanFinishedListener l)
    {
    	listeners.add(l);
    }
    
    static public void removeListener(MediaScanFinishedListener l)
    {
    	listeners.remove(l);
    }
}
