package com.openfeint.internal.logcat;

import android.util.Log;

/* loaded from: classes.dex */
public class OFLog {
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    public static int LEVEL = 4;
    public static final int VERBOSE = 2;
    public static final int WARNING = 5;

    public static boolean willLog(int logLevel) {
        return LEVEL <= logLevel;
    }

    /* renamed from: e */
    public static void m182e(String tag, String msg) {
        if (LEVEL <= 6) {
            StringBuilder append = new StringBuilder().append(tag).append(':');
            if (msg == null) {
                msg = "(null)";
            }
            Log.e("Openfeint", append.append(msg).toString());
        }
    }

    /* renamed from: w */
    public static void m185w(String tag, String msg) {
        if (LEVEL <= 5) {
            StringBuilder append = new StringBuilder().append(tag).append(':');
            if (msg == null) {
                msg = "(null)";
            }
            Log.w("Openfeint", append.append(msg).toString());
        }
    }

    /* renamed from: i */
    public static void m183i(String tag, String msg) {
        if (LEVEL <= 4) {
            StringBuilder append = new StringBuilder().append(tag).append(':');
            if (msg == null) {
                msg = "(null)";
            }
            Log.i("Openfeint", append.append(msg).toString());
        }
    }

    /* renamed from: d */
    public static void m181d(String tag, String msg) {
        if (LEVEL <= 3) {
            StringBuilder append = new StringBuilder().append(tag).append(':');
            if (msg == null) {
                msg = "(null)";
            }
            Log.d("Openfeint", append.append(msg).toString());
        }
    }

    /* renamed from: v */
    public static void m184v(String tag, String msg) {
        if (LEVEL <= 2) {
            StringBuilder append = new StringBuilder().append(tag).append(':');
            if (msg == null) {
                msg = "(null)";
            }
            Log.v("Openfeint", append.append(msg).toString());
        }
    }
}
