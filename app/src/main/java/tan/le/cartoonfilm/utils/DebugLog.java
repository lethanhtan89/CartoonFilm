package tan.le.cartoonfilm.utils;

import android.text.TextUtils;
import android.util.Log;

import tan.le.cartoonfilm.BuildConfig;

public class DebugLog {
    public static final String[] FORMAT_STRING = {
            "%d", "%b", "%f", "%l"};
    private static final String TAG = "CartoonFilm";
    public static boolean is_debug = BuildConfig.DEBUG;

    public static void d(String msg, Object... objects) {
        if (!is_debug)
            return;
        d(String.format(wrapSMS(msg), wrapParams(objects)));
    }


    public static void i(String msg, Object... objects) {
        if (!is_debug)
            return;
        i(String.format(wrapSMS(msg), wrapParams(objects)));
    }

    public static void e(String msg, Object... objects) {
        if (!is_debug)
            return;
        e(String.format(wrapSMS(msg), wrapParams(objects)));
    }

    private static Object[] wrapParams(Object[] objects) {
        for (int i = 0; i < objects.length; i++) {
            objects[i] = String.valueOf(objects[i]);
        }
        return objects;
    }

    public static final void d(String msg) {
        d(TAG, msg);
    }

    public static final void d(String tag, String msg) {
        if (!is_debug)
            return;
        Log.d(tag, formatSMS(msg));
    }

    public static final void i(String msg) {
        if (!is_debug)
            return;

        Log.i(TAG, formatSMS(msg));
    }

    public static final void e(String msg) {
        if (!is_debug)
            return;

        Log.e(TAG, formatSMS(msg));
    }

    private static String wrapSMS(String msg) {
        for (String temp : FORMAT_STRING)
            msg = msg.replaceAll(temp, "%s");
        return msg;
    }

    private static String formatSMS(String sms) {
        String className = null;
        int lineNumber = 0;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        try {
            for (int i = 0; i < stackTrace.length; i++) {
                if (TextUtils.equals(DebugLog.class.getName(), stackTrace[i].getClassName())) {
                    className = stackTrace[i + 1].getFileName();
                    lineNumber = stackTrace[i + 1].getLineNumber();
                }
            }
        } catch (Exception e) {
        }

        return sms + " " + className + ":" + lineNumber;
    }
}
