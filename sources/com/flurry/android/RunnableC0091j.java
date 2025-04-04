package com.flurry.android;

import java.util.Iterator;
import java.util.List;

/* renamed from: com.flurry.android.j */
/* loaded from: classes.dex */
final class RunnableC0091j implements Runnable {

    /* renamed from: a */
    private /* synthetic */ List f194a;

    RunnableC0091j(List list) {
        this.f194a = list;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Iterator it = this.f194a.iterator();
        while (it.hasNext()) {
            ((C0096o) it.next()).m93a();
        }
    }
}
