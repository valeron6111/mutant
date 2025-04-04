package com.flurry.android;

/* renamed from: com.flurry.android.ae */
/* loaded from: classes.dex */
final class RunnableC0075ae implements Runnable {

    /* renamed from: a */
    private /* synthetic */ String f97a;

    /* renamed from: b */
    private /* synthetic */ ViewOnClickListenerC0102u f98b;

    RunnableC0075ae(ViewOnClickListenerC0102u viewOnClickListenerC0102u, String str) {
        this.f98b = viewOnClickListenerC0102u;
        this.f97a = str;
    }

    @Override // java.lang.Runnable
    public final void run() {
        AppCircleCallback appCircleCallback;
        AppCircleCallback appCircleCallback2;
        CallbackEvent callbackEvent = new CallbackEvent(CallbackEvent.ERROR_MARKET_LAUNCH);
        callbackEvent.setMessage(this.f97a);
        appCircleCallback = this.f98b.f239z;
        if (appCircleCallback != null) {
            appCircleCallback2 = this.f98b.f239z;
            appCircleCallback2.onMarketAppLaunchError(callbackEvent);
        }
    }
}
