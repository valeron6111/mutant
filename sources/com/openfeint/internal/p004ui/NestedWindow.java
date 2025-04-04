package com.openfeint.internal.p004ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.Util;

/* loaded from: classes.dex */
public class NestedWindow extends Activity {
    private boolean mIsVisible = false;
    protected View mLogoImage;
    protected WebView mWebView;

    protected int layoutResource() {
        return C0207RR.layout("of_nested_window");
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setOrientation(this);
        beforeSetContentView();
        setContentView(layoutResource());
        this.mWebView = (WebView) findViewById(C0207RR.m180id("web_view"));
        this.mLogoImage = findViewById(C0207RR.m180id("of_ll_logo_image"));
    }

    private boolean isBigScreen() {
        Display d = getWindowManager().getDefaultDisplay();
        int width = d.getWidth();
        int height = d.getHeight();
        if (height <= width || width < 800 || height < 1000) {
            return width >= 1000 && height >= 800;
        }
        return true;
    }

    protected void beforeSetContentView() {
        if (isBigScreen()) {
            getWindow().requestFeature(1);
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void fade(boolean toVisible) {
        if (this.mWebView != null && this.mIsVisible != toVisible) {
            this.mIsVisible = toVisible;
            AlphaAnimation anim = new AlphaAnimation(toVisible ? 0.0f : 1.0f, toVisible ? 1.0f : 0.0f);
            anim.setDuration(toVisible ? 200L : 0L);
            anim.setFillAfter(true);
            this.mWebView.startAnimation(anim);
            if (this.mWebView.getVisibility() == 4) {
                this.mWebView.setVisibility(0);
                findViewById(C0207RR.m180id("frameLayout")).setVisibility(0);
            }
        }
    }
}
