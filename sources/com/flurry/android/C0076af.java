package com.flurry.android;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/* renamed from: com.flurry.android.af */
/* loaded from: classes.dex */
final class C0076af {

    /* renamed from: b */
    private int f100b = 100;

    /* renamed from: a */
    private LinkedHashMap f99a = new C0089h(this, ((int) Math.ceil(100 / 0.75f)) + 1, 0.75f);

    C0076af(int i) {
    }

    /* renamed from: a */
    final synchronized Object m64a(Object obj) {
        return this.f99a.get(obj);
    }

    /* renamed from: a */
    final synchronized void m65a(Object obj, Object obj2) {
        this.f99a.put(obj, obj2);
    }

    /* renamed from: a */
    final synchronized int m63a() {
        return this.f99a.size();
    }

    /* renamed from: b */
    final synchronized List m66b() {
        return new ArrayList(this.f99a.entrySet());
    }

    /* renamed from: c */
    final synchronized Set m67c() {
        return this.f99a.keySet();
    }
}
