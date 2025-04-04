package com.flurry.android;

import android.content.Context;
import android.os.Handler;

/* renamed from: com.flurry.android.ak */
/* loaded from: classes.dex */
final class RunnableC0081ak implements Runnable {

    /* renamed from: a */
    final /* synthetic */ String f109a;

    /* renamed from: b */
    final /* synthetic */ Context f110b;

    /* renamed from: c */
    final /* synthetic */ C0097p f111c;

    /* renamed from: d */
    final /* synthetic */ ViewOnClickListenerC0102u f112d;

    RunnableC0081ak(ViewOnClickListenerC0102u viewOnClickListenerC0102u, String str, Context context, C0097p c0097p) {
        this.f112d = viewOnClickListenerC0102u;
        this.f109a = str;
        this.f110b = context;
        this.f111c = c0097p;
    }

    @Override // java.lang.Runnable
    public final void run() {
        String m112d;
        m112d = this.f112d.m112d(this.f109a);
        new Handler().post(new RunnableC0094m(this, m112d));
    }
}
