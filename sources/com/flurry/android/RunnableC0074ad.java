package com.flurry.android;

/* renamed from: com.flurry.android.ad */
/* loaded from: classes.dex */
final class RunnableC0074ad implements Runnable {

    /* renamed from: a */
    private /* synthetic */ int f95a;

    /* renamed from: b */
    private /* synthetic */ ViewOnClickListenerC0102u f96b;

    RunnableC0074ad(ViewOnClickListenerC0102u viewOnClickListenerC0102u, int i) {
        this.f96b = viewOnClickListenerC0102u;
        this.f95a = i;
    }

    @Override // java.lang.Runnable
    public final void run() {
        AppCircleCallback appCircleCallback;
        CallbackEvent callbackEvent = new CallbackEvent(this.f95a);
        appCircleCallback = this.f96b.f239z;
        appCircleCallback.onAdsUpdated(callbackEvent);
    }
}
