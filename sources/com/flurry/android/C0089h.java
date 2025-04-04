package com.flurry.android;

import java.util.LinkedHashMap;
import java.util.Map;

/* renamed from: com.flurry.android.h */
/* loaded from: classes.dex */
final class C0089h extends LinkedHashMap {

    /* renamed from: a */
    private /* synthetic */ C0076af f188a;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    C0089h(C0076af c0076af, int i, float f) {
        super(i, f, true);
        this.f188a = c0076af;
    }

    @Override // java.util.LinkedHashMap
    protected final boolean removeEldestEntry(Map.Entry entry) {
        int i;
        int size = size();
        i = this.f188a.f100b;
        return size > i;
    }
}
