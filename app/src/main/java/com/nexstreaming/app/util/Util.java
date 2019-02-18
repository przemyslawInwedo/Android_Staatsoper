package com.nexstreaming.app.util;


import android.content.Context;

public class Util {

    public static String formatTime(final int timeMs) {
        if (timeMs < 1000) {
            return "00:00";
        }
        String result = "";
        final int totalSec = timeMs / 1000;
        final int hours = totalSec / 3600;
        final int minutes = (totalSec / 60) % 60;
        final int seconds = totalSec % 60;

        if (hours > 0) {
            result += hours + ":";
        }
        if (minutes >= 10) {
            result += minutes + ":";
        } else {
            result += "0" + minutes + ":";
        }
        if (seconds >= 10) {
            result += seconds;
        } else {
            result += "0" + seconds;
        }
        return result;
    }

    public static String getEnginePath(Context context) {
        String engine = "libnexplayerengine.so";

        String ret = context.getApplicationInfo().dataDir + "/lib/" + engine;
        return ret;
    }

}