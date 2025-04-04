package com.flurry.android;

/* renamed from: com.flurry.android.m */
/* loaded from: classes.dex */
final class RunnableC0094m implements Runnable {

    /* renamed from: a */
    private /* synthetic */ String f197a;

    /* renamed from: b */
    private /* synthetic */ RunnableC0081ak f198b;

    RunnableC0094m(RunnableC0081ak runnableC0081ak, String str) {
        this.f198b = runnableC0081ak;
        this.f197a = str;
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (this.f197a != null) {
            ViewOnClickListenerC0102u.m107a(this.f198b.f112d, this.f198b.f110b, this.f197a);
            this.f198b.f111c.m95a(new C0087f((byte) 8, this.f198b.f112d.m146j()));
        } else {
            String str = "Unable to launch in app market: " + this.f198b.f109a;
            C0078ah.m82d(ViewOnClickListenerC0102u.f214a, str);
            this.f198b.f112d.m113e(str);
        }
    }
}
