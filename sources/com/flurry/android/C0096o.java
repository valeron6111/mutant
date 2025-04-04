package com.flurry.android;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;

/* renamed from: com.flurry.android.o */
/* loaded from: classes.dex */
final class C0096o extends RelativeLayout {

    /* renamed from: a */
    private ViewOnClickListenerC0102u f199a;

    /* renamed from: b */
    private Context f200b;

    /* renamed from: c */
    private String f201c;

    /* renamed from: d */
    private int f202d;

    /* renamed from: e */
    private boolean f203e;

    /* renamed from: f */
    private boolean f204f;

    C0096o(ViewOnClickListenerC0102u viewOnClickListenerC0102u, Context context, String str, int i) {
        super(context);
        this.f199a = viewOnClickListenerC0102u;
        this.f200b = context;
        this.f201c = str;
        this.f202d = i;
    }

    /* renamed from: a */
    final void m93a() {
        if (!this.f203e) {
            C0106y m92c = m92c();
            if (m92c != null) {
                removeAllViews();
                addView(m92c, m91b());
                m92c.m159a().m95a(new C0087f((byte) 3, this.f199a.m146j()));
                this.f203e = true;
            } else if (!this.f204f) {
                TextView textView = new TextView(this.f200b);
                textView.setText(ViewOnClickListenerC0102u.f215b);
                textView.setTextSize(1, 20.0f);
                addView(textView, m91b());
            }
            this.f204f = true;
        }
    }

    /* renamed from: b */
    private static RelativeLayout.LayoutParams m91b() {
        return new RelativeLayout.LayoutParams(-1, -1);
    }

    /* renamed from: c */
    private synchronized C0106y m92c() {
        C0106y c0106y;
        List m120a = this.f199a.m120a(this.f200b, Arrays.asList(this.f201c), null, this.f202d, false);
        if (m120a.isEmpty()) {
            c0106y = null;
        } else {
            c0106y = (C0106y) m120a.get(0);
            c0106y.setOnClickListener(this.f199a);
        }
        return c0106y;
    }
}
