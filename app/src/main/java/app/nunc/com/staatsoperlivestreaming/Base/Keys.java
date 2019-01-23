package app.nunc.com.staatsoperlivestreaming.Base;

import android.os.Build;

import app.nunc.com.staatsoperlivestreaming.BuildConfig;
import app.nunc.com.staatsoperlivestreaming.R;

public class Keys {

    public static final String SERVICE_ENDPOINT = "https://dev.performa.nunc.at/api/v0.3/";
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm";
    public static final String X_DEVICE_SYSTEM_VERSION = "ANDROID: " + BuildConfig.VERSION_NAME;
    public static final String X_DEVICE_APP_NAME = "Staatsoper";
    public static final String X_DEVICE_TYPE = Build.PRODUCT;
    public static final String X_DEVICE_MODEL = Build.MODEL;
    public static final String X_DEVICE_IDENTIFIER = BuildConfig.APPLICATION_ID;

}
