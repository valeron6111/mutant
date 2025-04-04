package com.tapjoy;

import android.util.Log;

/* loaded from: classes.dex */
public class TapjoyLog {
    private static boolean showLog = false;

    public static void enableLogging(boolean enable) {
        Log.i("TapjoyLog", "enableLogging: " + enable);
        showLog = enable;
    }

    /* renamed from: i */
    public static void m190i(String tag, String msg) {
        if (showLog) {
            Log.i(tag, msg);
        }
    }

    /* renamed from: e */
    public static void m189e(String tag, String msg) {
        if (showLog) {
            Log.e(tag, msg);
        }
    }

    /* renamed from: w */
    public static void m192w(String tag, String msg) {
        if (showLog) {
            Log.w(tag, msg);
        }
    }

    /* renamed from: d */
    public static void m188d(String tag, String msg) {
        if (showLog) {
            Log.d(tag, msg);
        }
    }

    /* renamed from: v */
    public static void m191v(String tag, String msg) {
        if (showLog) {
            Log.v(tag, msg);
        }
    }
}
