package moniter.tyw.com.moniterlibrary.Utils;

import android.util.Log;

import moniter.tyw.com.moniterlibrary.BuildConfig;

public class DebugLog {
    public static boolean DEBUG = BuildConfig.DebugLog;
    public static String TAG = "CLog:Android";

    public static void w(String msg) {
        if (DEBUG) Log.w(TAG, msg);
    }

    public static void e(String msg) {
        if (DEBUG) Log.e(TAG, msg);
    }

    public static void i(String msg) {
        if (DEBUG) Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

    public static void v(String msg) {
        if (DEBUG) Log.v(TAG, msg);
    }

}
