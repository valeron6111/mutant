package com.flurry.android;

import android.content.Context;
import android.os.Handler;

/* renamed from: com.flurry.android.b */
/* loaded from: classes.dex */
final class RunnableC0083b implements Runnable {

    /* renamed from: a */
    final /* synthetic */ Context f116a;

    /* renamed from: b */
    final /* synthetic */ FlurryAgent f117b;

    /* renamed from: c */
    private /* synthetic */ boolean f118c;

    RunnableC0083b(FlurryAgent flurryAgent, boolean z, Context context) {
        this.f117b = flurryAgent;
        this.f118c = z;
        this.f116a = context;
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean z;
        ViewOnClickListenerC0102u viewOnClickListenerC0102u;
        Handler handler;
        long j;
        this.f117b.m50i();
        this.f117b.m53l();
        if (!this.f118c) {
            handler = this.f117b.f58q;
            RunnableC0093l runnableC0093l = new RunnableC0093l(this);
            j = FlurryAgent.f25i;
            handler.postDelayed(runnableC0093l, j);
        }
        z = FlurryAgent.f31o;
        if (z) {
            viewOnClickListenerC0102u = this.f117b.f57Y;
            viewOnClickListenerC0102u.m139c();
        }
    }
}
