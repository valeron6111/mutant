package com.flurry.android;

import android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import com.openfeint.internal.request.multipart.StringPart;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CatalogActivity extends Activity implements View.OnClickListener {

    /* renamed from: a */
    private static volatile String f11a = "<html><body><table height='100%' width='100%' border='0'><tr><td style='vertical-align:middle;text-align:center'>No recommendations available<p><button type='input' onClick='activity.finish()'>Back</button></td></tr></table></body></html>";

    /* renamed from: b */
    private WebView f12b;

    /* renamed from: c */
    private C0104w f13c;

    /* renamed from: d */
    private List f14d = new ArrayList();

    /* renamed from: e */
    private ViewOnClickListenerC0102u f15e;

    /* renamed from: f */
    private C0097p f16f;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        Long valueOf;
        setTheme(R.style.Theme.Translucent);
        super.onCreate(bundle);
        this.f15e = FlurryAgent.m27b();
        Intent intent = getIntent();
        if (intent.getExtras() != null && (valueOf = Long.valueOf(intent.getExtras().getLong("o"))) != null) {
            this.f16f = this.f15e.m135b(valueOf.longValue());
        }
        C0072ab c0072ab = new C0072ab(this, this);
        c0072ab.setId(1);
        c0072ab.setBackgroundColor(-16777216);
        this.f12b = new WebView(this);
        this.f12b.setId(2);
        this.f12b.setScrollBarStyle(0);
        this.f12b.setBackgroundColor(-1);
        if (this.f16f != null) {
            this.f12b.setWebViewClient(new C0098q(this));
        }
        this.f12b.getSettings().setJavaScriptEnabled(true);
        this.f12b.addJavascriptInterface(this, "activity");
        this.f13c = new C0104w(this, this);
        this.f13c.setId(3);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams.addRule(10, c0072ab.getId());
        relativeLayout.addView(c0072ab, layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams2.addRule(3, c0072ab.getId());
        layoutParams2.addRule(2, this.f13c.getId());
        relativeLayout.addView(this.f12b, layoutParams2);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams3.addRule(12, c0072ab.getId());
        relativeLayout.addView(this.f13c, layoutParams3);
        Bundle extras = getIntent().getExtras();
        String string = extras == null ? null : extras.getString("u");
        if (string == null) {
            this.f12b.loadDataWithBaseURL(null, f11a, StringPart.DEFAULT_CONTENT_TYPE, "utf-8", null);
        } else {
            this.f12b.loadUrl(string);
        }
        setContentView(relativeLayout);
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.f15e.m143g();
        super.onDestroy();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view instanceof C0106y) {
            C0105x c0105x = new C0105x();
            c0105x.f252a = this.f12b.getUrl();
            c0105x.f253b = new ArrayList(this.f13c.m157b());
            this.f14d.add(c0105x);
            if (this.f14d.size() > 5) {
                this.f14d.remove(0);
            }
            C0105x c0105x2 = new C0105x();
            C0106y c0106y = (C0106y) view;
            this.f16f = this.f15e.m136b(c0106y.m159a());
            c0106y.m160a(this.f16f);
            c0105x2.f252a = this.f15e.m145i() + this.f15e.m119a(c0106y.m159a());
            c0105x2.f253b = this.f13c.m154a(view.getContext());
            m2a(c0105x2);
            return;
        }
        if (view.getId() == 10000) {
            finish();
            return;
        }
        if (view.getId() == 10002) {
            this.f13c.m155a();
        } else if (this.f14d.isEmpty()) {
            finish();
        } else {
            m2a((C0105x) this.f14d.remove(this.f14d.size() - 1));
        }
    }

    /* renamed from: a */
    private void m2a(C0105x c0105x) {
        try {
            this.f12b.loadUrl(c0105x.f252a);
            this.f13c.m156a(c0105x.f253b);
        } catch (Exception e) {
            C0078ah.m72a("FlurryAgent", "Error loading url: " + c0105x.f252a);
        }
    }
}
