package com.openfeint.internal.p004ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public class NativeBrowser extends NestedWindow {
    public static final String INTENT_ARG_PREFIX = "com.openfeint.internal.ui.NativeBrowser.argument.";
    private AtomicBoolean mFinished = new AtomicBoolean(false);
    private Handler mHandler;
    private Runnable mTimeoutWatchdog;

    public final class JSInterface {
        public JSInterface() {
        }

        public void returnValue(final String returnValue) {
            NativeBrowser.this.runOnUiThread(new Runnable() { // from class: com.openfeint.internal.ui.NativeBrowser.JSInterface.1
                @Override // java.lang.Runnable
                public void run() {
                    if (NativeBrowser.this.mFinished.compareAndSet(false, true)) {
                        Intent returnIntent = new Intent();
                        if (returnValue != null) {
                            returnIntent.putExtra("com.openfeint.internal.ui.NativeBrowser.argument.result", returnValue);
                        }
                        NativeBrowser.this.setResult(-1, returnIntent);
                        NativeBrowser.this.finish();
                    }
                }
            });
        }
    }

    @Override // com.openfeint.internal.p004ui.NestedWindow, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String src = extras.getString("com.openfeint.internal.ui.NativeBrowser.argument.src");
        String timeout = extras.getString("com.openfeint.internal.ui.NativeBrowser.argument.timeout");
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.addJavascriptInterface(new JSInterface(), "NativeBrowser");
        this.mWebView.setWebViewClient(new WebViewClient() { // from class: com.openfeint.internal.ui.NativeBrowser.1
            private void clearTimeout() {
                if (NativeBrowser.this.mHandler != null && NativeBrowser.this.mTimeoutWatchdog != null) {
                    NativeBrowser.this.mHandler.removeCallbacks(NativeBrowser.this.mTimeoutWatchdog);
                    NativeBrowser.this.mHandler = null;
                    NativeBrowser.this.mTimeoutWatchdog = null;
                }
            }

            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView view, String url) {
                clearTimeout();
                super.onPageFinished(view, url);
            }

            @Override // android.webkit.WebViewClient
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                clearTimeout();
                super.onReceivedError(view, errorCode, description, failingUrl);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("com.openfeint.internal.ui.NativeBrowser.argument.failed", true);
                returnIntent.putExtra("com.openfeint.internal.ui.NativeBrowser.argument.failure_code", errorCode);
                returnIntent.putExtra("com.openfeint.internal.ui.NativeBrowser.argument.failure_desc", description);
                NativeBrowser.this.setResult(-1, returnIntent);
                NativeBrowser.this.finish();
            }
        });
        this.mWebView.setWebChromeClient(new WebChromeClient());
        this.mWebView.loadUrl(src);
        if (timeout != null) {
            this.mHandler = new Handler();
            this.mTimeoutWatchdog = new Runnable() { // from class: com.openfeint.internal.ui.NativeBrowser.2
                @Override // java.lang.Runnable
                public void run() {
                    if (NativeBrowser.this.mFinished.compareAndSet(false, true)) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("com.openfeint.internal.ui.NativeBrowser.argument.failed", true);
                        returnIntent.putExtra("com.openfeint.internal.ui.NativeBrowser.argument.failure_code", 0);
                        returnIntent.putExtra("com.openfeint.internal.ui.NativeBrowser.argument.failure_desc", "Timeout");
                        NativeBrowser.this.setResult(-1, returnIntent);
                        NativeBrowser.this.finish();
                    }
                }
            };
            this.mHandler.postDelayed(this.mTimeoutWatchdog, Integer.parseInt(timeout));
        }
        fade(true);
    }
}
