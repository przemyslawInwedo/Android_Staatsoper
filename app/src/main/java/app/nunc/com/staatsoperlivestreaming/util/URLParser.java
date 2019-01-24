package app.nunc.com.staatsoperlivestreaming.util;


import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;
import java.util.Set;

import app.nunc.com.staatsoperlivestreaming.info.NxbInfo;

public class URLParser {

    public static final String PLAYER_TYPE_BASE = "base";
    public static final String PLAYER_TYPE_VM = "vm";
    public static final String PLAYER_TYPE_STATIC = "static";
    public static final String PLAYER_TYPE_HEADPHONEX = "headphonex";
    public static final String PLAYER_TYPE_VIDEOVIEW = "videoview";
    public static final String PLAYER_TYPE_VAST = "vast";
    public static final String PLAYER_TYPE_MULTI_INSTANCE = "multiinstance";

    private static final String DRM_TYPE_VM = "VM";
    private static final String DRM_TYPE_WV = "WVDRM";
    private static final String DRM_TYPE_MEDIA_DRM = "MEDIADRM";

    private static final String KEY_PLAYER_TYPE = "NEXTARGETPLAYERTYPE";
    private static final String KEY_DRM_TYPE = "NEXDRMTYPE";
    private static final String KEY_VCAS = "NEXVCAS";
    private static final String KEY_VCAS_COMPANY = "NEXVCAS_COMPANY";
    private static final String KEY_LAURL = "NEXLAURL";
    private static final String KEY_SUBTITLE = "NEXSUBTITLE";
    private static final String KEY_OFFLINE_STORE = "NEXOFFLINESTORE";

    public static NxbInfo toNxbInfo(String url) {
        NxbInfo info = null;

        if( !TextUtils.isEmpty(url) ) {
            Uri uri = Uri.parse(url);

            if( uri != null ) {
                String playerType = getPlayerType(uri);
                if( playerType != null ) {

                    Uri.Builder uriBuilder = uri.buildUpon().clearQuery();
                    Set<String> params = uri.getQueryParameterNames();
                    for (String paramName : params) {
                        if (!paramName.equalsIgnoreCase(KEY_PLAYER_TYPE) &&
                                !paramName.equalsIgnoreCase(KEY_SUBTITLE) &&
                                !paramName.equalsIgnoreCase(KEY_DRM_TYPE) &&
                                !paramName.equalsIgnoreCase(KEY_LAURL) &&
                                !paramName.equalsIgnoreCase(KEY_DRM_TYPE) &&
                                !paramName.equalsIgnoreCase(KEY_VCAS_COMPANY) &&
                                !paramName.equalsIgnoreCase(KEY_VCAS) &&
                                !paramName.equalsIgnoreCase(KEY_OFFLINE_STORE))
                            uriBuilder.appendQueryParameter(paramName, uri.getQueryParameter(paramName));
                    }

                    info = new NxbInfo();

                    info.setUrl(uriBuilder.build().toString());

                    String drmType = getStringParameter(uri, KEY_DRM_TYPE);
                    if( !TextUtils.isEmpty(drmType) ) {
                        info.setType(drmType);
                        if( drmType.equalsIgnoreCase(DRM_TYPE_VM) ) {
                            info.addExtra(getStringParameter(uri, KEY_VCAS));
                            info.addExtra(getStringParameter(uri, KEY_VCAS_COMPANY));
                        } else if( drmType.equalsIgnoreCase(DRM_TYPE_WV) || drmType.equalsIgnoreCase(DRM_TYPE_MEDIA_DRM) )
                            info.setExtra(getStringParameter(uri, KEY_LAURL));
                    }

                    List<String> subtitles = null;
                    subtitles = uri.getQueryParameters(KEY_SUBTITLE);
                    if( subtitles.size() == 0 ) {
                        subtitles = uri.getQueryParameters(KEY_SUBTITLE.toLowerCase());
                    }

                    for( String subtitle : subtitles ){
                        info.addSubtitle(subtitle);
                    }
                }
            }
        }

        Log.d("URLParser", "toNxbInfo : " + info.toString());
        return info;
    }

    private static String getStringParameter(Uri uri, String key) {
        String value = uri.getQueryParameter(key);
        if( TextUtils.isEmpty(value) )
            value = uri.getQueryParameter(key.toLowerCase());
        return value;
    }

    public static String getPlayerType(Uri uri) {
        String playerType = null;
        if( uri != null ) {
            playerType = getStringParameter(uri, KEY_PLAYER_TYPE);
        }
        Log.d("URLParser", "getPlayerType : " + playerType);
        return playerType;
    }

    public static boolean shouldPlay(Uri uri) {
        boolean ret = true;
        String retStr = null;
        if( !TextUtils.isEmpty(retStr = getStringParameter(uri, KEY_OFFLINE_STORE)) ) {
            ret = !Boolean.parseBoolean(retStr);
        }
        return ret;
    }
}
