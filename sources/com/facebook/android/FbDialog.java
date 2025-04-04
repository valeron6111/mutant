package com.facebook.android;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.facebook.android.Facebook;
import com.google.android.c2dm.C2DMBaseReceiver;

/* loaded from: classes.dex */
public class FbDialog extends Dialog {
    static final String DISPLAY_STRING = "touch";
    static final int FB_BLUE = -9599820;
    static final String FB_ICON = "icon.png";
    static final int MARGIN = 4;
    static final int PADDING = 2;
    private FrameLayout mContent;
    private ImageView mCrossImage;
    private Facebook.DialogListener mListener;
    private ProgressDialog mSpinner;
    private String mUrl;
    private WebView mWebView;
    static final float[] DIMENSIONS_DIFF_LANDSCAPE = {20.0f, 60.0f};
    static final float[] DIMENSIONS_DIFF_PORTRAIT = {40.0f, 60.0f};
    static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(-1, -1);

    public FbDialog(Context context, String url, Facebook.DialogListener listener) {
        super(context, android.R.style.Theme.Translucent.NoTitleBar);
        this.mUrl = url;
        this.mListener = listener;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSpinner = new ProgressDialog(getContext());
        this.mSpinner.requestWindowFeature(1);
        this.mSpinner.setMessage("Loading...");
        requestWindowFeature(1);
        this.mContent = new FrameLayout(getContext());
        createCrossImage();
        int crossWidth = this.mCrossImage.getDrawable().getIntrinsicWidth();
        setUpWebView(crossWidth / 2);
        this.mContent.addView(this.mCrossImage, new ViewGroup.LayoutParams(-2, -2));
        addContentView(this.mContent, new ViewGroup.LayoutParams(-1, -1));
    }

    private void createCrossImage() {
        this.mCrossImage = new ImageView(getContext());
        this.mCrossImage.setOnClickListener(new View.OnClickListener() { // from class: com.facebook.android.FbDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                FbDialog.this.mListener.onCancel();
                FbDialog.this.dismiss();
            }
        });
        Drawable crossDrawable = getContext().getResources().getDrawable(C0069R.drawable.close);
        this.mCrossImage.setImageDrawable(crossDrawable);
        this.mCrossImage.setVisibility(4);
    }

    private void setUpWebView(int margin) {
        LinearLayout webViewContainer = new LinearLayout(getContext());
        this.mWebView = new WebView(getContext());
        this.mWebView.setVerticalScrollBarEnabled(false);
        this.mWebView.setHorizontalScrollBarEnabled(false);
        this.mWebView.setWebViewClient(new FbWebViewClient());
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.loadUrl(this.mUrl);
        this.mWebView.setLayoutParams(FILL);
        this.mWebView.setVisibility(4);
        webViewContainer.setPadding(margin, margin, margin, margin);
        webViewContainer.addView(this.mWebView);
        this.mContent.addView(webViewContainer);
    }

    private class FbWebViewClient extends WebViewClient {
        private FbWebViewClient() {
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Util.logd("Facebook-WebView", "Redirect URL: " + url);
            if (url.startsWith(Facebook.REDIRECT_URI)) {
                Bundle values = Util.parseUrl(url);
                String error = values.getString(C2DMBaseReceiver.EXTRA_ERROR);
                if (error == null) {
                    error = values.getString("error_type");
                }
                if (error == null) {
                    FbDialog.this.mListener.onComplete(values);
                } else if (error.equals("access_denied") || error.equals("OAuthAccessDeniedException")) {
                    FbDialog.this.mListener.onCancel();
                } else {
                    FbDialog.this.mListener.onFacebookError(new FacebookError(error));
                }
                FbDialog.this.dismiss();
                return true;
            }
            if (url.startsWith(Facebook.CANCEL_URI)) {
                FbDialog.this.mListener.onCancel();
                FbDialog.this.dismiss();
                return true;
            }
            if (url.contains(FbDialog.DISPLAY_STRING)) {
                return false;
            }
            FbDialog.this.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
            return true;
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            FbDialog.this.mListener.onError(new DialogError(description, errorCode, failingUrl));
            FbDialog.this.dismiss();
        }

        @Override // android.webkit.WebViewClient
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Util.logd("Facebook-WebView", "Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            FbDialog.this.mSpinner.show();
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            FbDialog.this.mSpinner.dismiss();
            FbDialog.this.mContent.setBackgroundColor(0);
            FbDialog.this.mWebView.setVisibility(0);
            FbDialog.this.mCrossImage.setVisibility(0);
        }
    }
}
