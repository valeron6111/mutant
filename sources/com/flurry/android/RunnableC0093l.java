package com.flurry.android;

/* renamed from: com.flurry.android.l */
/* loaded from: classes.dex */
final class RunnableC0093l implements Runnable {

    /* renamed from: a */
    private /* synthetic */ RunnableC0083b f196a;

    RunnableC0093l(RunnableC0083b runnableC0083b) {
        this.f196a = runnableC0083b;
    }

    @Override // java.lang.Runnable
    public final void run() {
        FlurryAgent.m32b(this.f196a.f117b, this.f196a.f116a);
    }
}
