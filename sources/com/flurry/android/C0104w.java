package com.flurry.android;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.flurry.android.w */
/* loaded from: classes.dex */
final class C0104w extends LinearLayout {

    /* renamed from: a */
    private View f248a;

    /* renamed from: b */
    private List f249b;

    /* renamed from: c */
    private boolean f250c;

    /* renamed from: d */
    private /* synthetic */ CatalogActivity f251d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0104w(CatalogActivity catalogActivity, Context context) {
        super(context);
        this.f251d = catalogActivity;
        this.f249b = new ArrayList();
        this.f250c = true;
        setOrientation(1);
        setGravity(48);
        this.f248a = new C0100s(catalogActivity, context);
        this.f248a.setId(10002);
        this.f248a.setOnClickListener(catalogActivity);
        m153a(m154a(context), this.f250c);
    }

    /* renamed from: a */
    final List m154a(Context context) {
        C0097p c0097p;
        C0097p c0097p2;
        C0103v c0103v;
        ViewOnClickListenerC0102u viewOnClickListenerC0102u;
        ArrayList arrayList = new ArrayList();
        for (int i = 1; i <= 3; i++) {
            arrayList.add("Flurry_Canvas_Hook_" + i);
        }
        c0097p = this.f251d.f16f;
        if (c0097p == null) {
            c0103v = null;
        } else {
            c0097p2 = this.f251d.f16f;
            c0103v = c0097p2.f206b;
        }
        Long valueOf = c0103v != null ? Long.valueOf(c0103v.f240a) : null;
        viewOnClickListenerC0102u = this.f251d.f15e;
        List m120a = viewOnClickListenerC0102u.m120a(context, arrayList, valueOf, 1, true);
        Iterator it = m120a.iterator();
        while (it.hasNext()) {
            ((C0106y) it.next()).setOnClickListener(this.f251d);
        }
        return m120a;
    }

    /* renamed from: a */
    final void m155a() {
        this.f250c = !this.f250c;
        m153a(null, this.f250c);
    }

    /* renamed from: a */
    final void m156a(List list) {
        m153a(list, this.f250c);
    }

    /* renamed from: a */
    private void m153a(List list, boolean z) {
        long m146j;
        removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(0, 0, 0, 0);
        addView(this.f248a, layoutParams);
        if (list != null) {
            this.f249b.clear();
            this.f249b.addAll(list);
        }
        if (z) {
            for (C0106y c0106y : this.f249b) {
                addView(c0106y, layoutParams);
                C0097p m159a = c0106y.m159a();
                m146j = this.f251d.f15e.m146j();
                m159a.m95a(new C0087f((byte) 3, m146j));
            }
        }
        refreshDrawableState();
    }

    /* renamed from: b */
    final List m157b() {
        return this.f249b;
    }
}
