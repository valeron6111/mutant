package com.flurry.android;

import android.view.View;
import android.widget.TextView;

/* renamed from: com.flurry.android.ac */
/* loaded from: classes.dex */
final class ViewOnFocusChangeListenerC0073ac implements View.OnFocusChangeListener {

    /* renamed from: a */
    private /* synthetic */ TextView f93a;

    /* renamed from: b */
    private /* synthetic */ C0072ab f94b;

    ViewOnFocusChangeListenerC0073ac(C0072ab c0072ab, TextView textView) {
        this.f94b = c0072ab;
        this.f93a = textView;
    }

    @Override // android.view.View.OnFocusChangeListener
    public final void onFocusChange(View view, boolean z) {
        this.f93a.setText(z ? this.f94b.f92b : this.f94b.f91a);
    }
}
