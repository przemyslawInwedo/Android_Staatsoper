package com.nexstreaming.app.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bonnie.kyeon on 2016-04-15.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {

    public interface NetworkListener {
        void onNetworkStateChanged(Context context);
    }

    static private List<NetworkListener> listeners = new ArrayList<NetworkListener>();

    @Override
    public void onReceive(Context context, Intent intent) {
        for (NetworkListener l : listeners) {
            l.onNetworkStateChanged(context);
        }
    }

    static public void addListener(NetworkListener l)
    {
        listeners.add(l);
    }

    static public void removeListener(NetworkListener l)
    {
        listeners.remove(l);
    }
}
