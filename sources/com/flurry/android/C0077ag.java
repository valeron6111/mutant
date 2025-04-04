package com.flurry.android;

import android.os.Handler;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.flurry.android.ag */
/* loaded from: classes.dex */
final class C0077ag {

    /* renamed from: b */
    private Handler f102b;

    /* renamed from: d */
    private int f104d;

    /* renamed from: a */
    private List f101a = new ArrayList();

    /* renamed from: c */
    private Handler f103c = new Handler();

    /* renamed from: e */
    private Runnable f105e = new RunnableC0092k(this);

    C0077ag(Handler handler, int i) {
        this.f102b = handler;
        this.f104d = i;
        m70b();
    }

    /* renamed from: a */
    final synchronized void m71a(C0096o c0096o) {
        c0096o.m93a();
        this.f101a.add(new WeakReference(c0096o));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public synchronized void m68a() {
        ArrayList arrayList = new ArrayList();
        Iterator it = this.f101a.iterator();
        while (it.hasNext()) {
            C0096o c0096o = (C0096o) ((WeakReference) it.next()).get();
            if (c0096o != null) {
                arrayList.add(c0096o);
            }
        }
        this.f103c.post(new RunnableC0091j(arrayList));
        m70b();
    }

    /* renamed from: b */
    private synchronized void m70b() {
        Iterator it = this.f101a.iterator();
        while (it.hasNext()) {
            if (((WeakReference) it.next()).get() == null) {
                it.remove();
            }
        }
        this.f102b.removeCallbacks(this.f105e);
        this.f102b.postDelayed(this.f105e, this.f104d);
    }
}
