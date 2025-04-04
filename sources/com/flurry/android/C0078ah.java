package com.flurry.android;

import android.util.Log;

/* renamed from: com.flurry.android.ah */
/* loaded from: classes.dex */
final class C0078ah {

    /* renamed from: a */
    private static boolean f106a = false;

    /* renamed from: b */
    private static int f107b = 5;

    C0078ah() {
    }

    /* renamed from: a */
    static void m74a() {
        f106a = true;
    }

    /* renamed from: b */
    static void m79b() {
        f106a = false;
    }

    /* renamed from: a */
    static void m75a(int i) {
        f107b = i;
    }

    /* renamed from: a */
    static boolean m76a(String str) {
        return Log.isLoggable(str, 3);
    }

    /* renamed from: a */
    static int m73a(String str, String str2, Throwable th) {
        if (f106a || f107b <= 3) {
            return 0;
        }
        return Log.d(str, str2, th);
    }

    /* renamed from: a */
    static int m72a(String str, String str2) {
        if (f106a || f107b <= 3) {
            return 0;
        }
        return Log.d(str, str2);
    }

    /* renamed from: b */
    static int m78b(String str, String str2, Throwable th) {
        if (f106a || f107b <= 6) {
            return 0;
        }
        return Log.e(str, str2, th);
    }

    /* renamed from: b */
    static int m77b(String str, String str2) {
        if (f106a || f107b <= 6) {
            return 0;
        }
        return Log.e(str, str2);
    }

    /* renamed from: c */
    static int m81c(String str, String str2, Throwable th) {
        if (f106a || f107b <= 4) {
            return 0;
        }
        return Log.i(str, str2, th);
    }

    /* renamed from: c */
    static int m80c(String str, String str2) {
        if (f106a || f107b <= 4) {
            return 0;
        }
        return Log.i(str, str2);
    }

    /* renamed from: d */
    static int m83d(String str, String str2, Throwable th) {
        if (f106a || f107b <= 5) {
            return 0;
        }
        return Log.w(str, str2, th);
    }

    /* renamed from: d */
    static int m82d(String str, String str2) {
        if (f106a || f107b <= 5) {
            return 0;
        }
        return Log.w(str, str2);
    }
}
