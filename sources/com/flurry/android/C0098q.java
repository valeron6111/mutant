package com.flurry.android;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.openfeint.internal.request.multipart.StringPart;

/* renamed from: com.flurry.android.q */
/* loaded from: classes.dex */
final class C0098q extends WebViewClient {

    /* renamed from: a */
    private /* synthetic */ CatalogActivity f210a;

    C0098q(CatalogActivity catalogActivity) {
        this.f210a = catalogActivity;
    }

    @Override // android.webkit.WebViewClient
    public final boolean shouldOverrideUrlLoading(WebView webView, String str) {
        C0097p c0097p;
        ViewOnClickListenerC0102u viewOnClickListenerC0102u;
        C0097p c0097p2;
        C0097p c0097p3;
        long m146j;
        if (str != null) {
            c0097p = this.f210a.f16f;
            if (c0097p != null) {
                c0097p3 = this.f210a.f16f;
                m146j = this.f210a.f15e.m146j();
                c0097p3.m95a(new C0087f((byte) 6, m146j));
            }
            viewOnClickListenerC0102u = this.f210a.f15e;
            Context context = webView.getContext();
            c0097p2 = this.f210a.f16f;
            viewOnClickListenerC0102u.m125a(context, c0097p2, str);
            return true;
        }
        return false;
    }

    @Override // android.webkit.WebViewClient
    public final void onReceivedError(WebView webView, int i, String str, String str2) {
        C0078ah.m80c("FlurryAgent", "Failed to load url: " + str2 + " with an errorCode of " + i);
        webView.loadData("Cannot find Android Market information. <p>Please check your network", StringPart.DEFAULT_CONTENT_TYPE, "UTF-8");
    }

    @Override // android.webkit.WebViewClient
    public final void onPageFinished(WebView webView, String str) {
        C0097p c0097p;
        long m146j;
        C0097p c0097p2;
        try {
            c0097p = this.f210a.f16f;
            m146j = this.f210a.f15e.m146j();
            C0087f c0087f = new C0087f((byte) 5, m146j);
            c0097p2 = this.f210a.f16f;
            long j = c0097p2.f207c;
            c0097p.f208d.add(c0087f);
            c0097p.f207c = j;
        } catch (Exception e) {
        }
    }
}
