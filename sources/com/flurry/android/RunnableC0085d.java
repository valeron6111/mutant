package com.flurry.android;

import android.content.Context;

/* renamed from: com.flurry.android.d */
/* loaded from: classes.dex */
final class RunnableC0085d implements Runnable {

    /* renamed from: a */
    private /* synthetic */ Context f178a;

    /* renamed from: b */
    private /* synthetic */ boolean f179b;

    /* renamed from: c */
    private /* synthetic */ FlurryAgent f180c;

    RunnableC0085d(FlurryAgent flurryAgent, Context context, boolean z) {
        this.f180c = flurryAgent;
        this.f178a = context;
        this.f179b = z;
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean z;
        z = this.f180c.f62u;
        if (!z) {
            this.f180c.m9a(this.f178a);
        }
        FlurryAgent.m15a(this.f180c, this.f178a, this.f179b);
    }
}
