package app.nunc.com.staatsoperlivestreaming.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by bonnie.kyeon on 2016-04-15.
 */
public class NetworkUtils {

    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        int ret = TYPE_NOT_CONNECTED;
        if( activeNetwork != null ) {
            if( activeNetwork.getType() == ConnectivityManager.TYPE_WIFI )
                ret = TYPE_WIFI;
            else if( activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE )
                ret = TYPE_MOBILE;
        }

        Log.d("test", "getConnectivityStatus ret : " + ret);
        return ret;
    }

}
