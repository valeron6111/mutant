package com.flurry.android;

import android.content.Context;
import android.widget.ImageView;
import com.alawar.mutant.jni.MutantMessages;
import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/* renamed from: com.flurry.android.r */
/* loaded from: classes.dex */
final class C0099r {
    C0099r() {
    }

    /* renamed from: a */
    static String m99a(String str, int i) {
        if (str == null) {
            return MutantMessages.sEmpty;
        }
        return str.length() > i ? str.substring(0, i) : str;
    }

    /* renamed from: a */
    static String m98a(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            C0078ah.m82d("FlurryAgent", "Cannot encode '" + str + "'");
            return MutantMessages.sEmpty;
        }
    }

    /* renamed from: a */
    static void m101a(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable th) {
            }
        }
    }

    /* renamed from: a */
    static void m100a(Context context, ImageView imageView, int i, int i2) {
        imageView.setAdjustViewBounds(true);
        imageView.setMinimumWidth(m97a(context, i));
        imageView.setMinimumHeight(m97a(context, i2));
        imageView.setMaxWidth(m97a(context, i));
        imageView.setMaxHeight(m97a(context, i2));
    }

    /* renamed from: a */
    static int m97a(Context context, int i) {
        return (int) ((context.getResources().getDisplayMetrics().density * i) + 0.5f);
    }
}
