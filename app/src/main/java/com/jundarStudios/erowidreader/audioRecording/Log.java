package com.jundarStudios.erowidreader.audioRecording;

import java.util.List;

public class Log {

    public static final boolean DEBUG = true;

    public static final String LOG_TAG = "speechutils";

    public static void i(String msg) {
        if (DEBUG) android.util.Log.i(LOG_TAG, msg);
    }

    public static void i(List<String> msgs) {
        if (DEBUG) {
            for (String msg : msgs) {
                if (msg == null) {
                    msg = "<null>";
                }
                android.util.Log.i(LOG_TAG, msg);
            }
        }
    }

    public static void e(String msg) {
        if (DEBUG) android.util.Log.e(LOG_TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (DEBUG) android.util.Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (DEBUG) android.util.Log.e(tag, msg);
    }
}
