package com.arellomobile.android.push;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/* loaded from: classes.dex */
public class PushWebview extends Activity {
    private WebView webView;

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.webView = new WebView(this);
        String url = getIntent().getStringExtra("url");
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebViewClient(new HelloWebViewClient());
        this.webView.loadUrl(url);
        setContentView(this.webView);
    }

    private class HelloWebViewClient extends WebViewClient {
        private HelloWebViewClient() {
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            PushWebview.this.finish();
            Uri uri = Uri.parse(url);
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            PushWebview.this.startActivity(intent);
            return false;
        }
    }
}
