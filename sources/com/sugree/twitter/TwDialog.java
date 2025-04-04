package com.sugree.twitter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sugree.twitter.Twitter;
import oauth.signpost.OAuth;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/* loaded from: classes.dex */
public class TwDialog extends Dialog {
    static final float[] DIMENSIONS_LANDSCAPE = {640.0f, 400.0f};
    static final float[] DIMENSIONS_PORTRAIT = {400.0f, 640.0f};
    static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(-1, -1);
    static final int MARGIN = 4;
    static final int PADDING = 2;
    public static final String TAG = "MutantTwitter";
    static final int TW_BLUE = -4137235;
    private CommonsHttpOAuthConsumer mConsumer;
    private LinearLayout mContent;
    private Handler mHandler;
    private int mIcon;
    private Twitter.DialogListener mListener;
    private CommonsHttpOAuthProvider mProvider;
    private ProgressDialog mSpinner;
    private TextView mTitle;
    private String mUrl;
    private WebView mWebView;

    public TwDialog(Context context, CommonsHttpOAuthProvider provider, CommonsHttpOAuthConsumer consumer, Twitter.DialogListener listener, int icon) {
        super(context);
        this.mProvider = provider;
        this.mConsumer = consumer;
        this.mListener = listener;
        this.mIcon = icon;
        this.mHandler = new Handler();
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSpinner = new ProgressDialog(getContext());
        this.mSpinner.requestWindowFeature(1);
        this.mSpinner.setMessage("Loading...");
        this.mContent = new LinearLayout(getContext());
        this.mContent.setOrientation(1);
        setUpTitle();
        setUpWebView();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        float scale = getContext().getResources().getDisplayMetrics().density;
        float[] dimensions = display.getWidth() < display.getHeight() ? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;
        addContentView(this.mContent, new FrameLayout.LayoutParams((int) ((dimensions[0] * scale) + 0.5f), (int) ((dimensions[1] * scale) + 0.5f)));
        retrieveRequestToken();
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
        this.mSpinner.show();
    }

    private void setUpTitle() {
        requestWindowFeature(1);
        Drawable icon = getContext().getResources().getDrawable(this.mIcon);
        this.mTitle = new TextView(getContext());
        this.mTitle.setText("Twitter");
        this.mTitle.setTextColor(-1);
        this.mTitle.setTypeface(Typeface.DEFAULT_BOLD);
        this.mTitle.setBackgroundColor(TW_BLUE);
        this.mTitle.setPadding(6, 4, 4, 4);
        this.mTitle.setCompoundDrawablePadding(6);
        this.mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, (Drawable) null, (Drawable) null, (Drawable) null);
        this.mContent.addView(this.mTitle);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [com.sugree.twitter.TwDialog$1] */
    private void retrieveRequestToken() {
        this.mSpinner.show();
        new Thread() { // from class: com.sugree.twitter.TwDialog.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    TwDialog.this.mUrl = TwDialog.this.mProvider.retrieveRequestToken(TwDialog.this.mConsumer, Twitter.CALLBACK_URI);
                    TwDialog.this.mWebView.loadUrl(TwDialog.this.mUrl);
                } catch (OAuthCommunicationException e) {
                    TwDialog.this.mListener.onError(new DialogError(e.getMessage(), -1, Twitter.OAUTH_REQUEST_TOKEN));
                } catch (OAuthExpectationFailedException e2) {
                    TwDialog.this.mListener.onError(new DialogError(e2.getMessage(), -1, Twitter.OAUTH_REQUEST_TOKEN));
                } catch (OAuthMessageSignerException e3) {
                    TwDialog.this.mListener.onError(new DialogError(e3.getMessage(), -1, Twitter.OAUTH_REQUEST_TOKEN));
                } catch (OAuthNotAuthorizedException e4) {
                    TwDialog.this.mListener.onError(new DialogError(e4.getMessage(), -1, Twitter.OAUTH_REQUEST_TOKEN));
                }
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v1, types: [com.sugree.twitter.TwDialog$2] */
    public void retrieveAccessToken(final String url) {
        this.mSpinner.show();
        new Thread() { // from class: com.sugree.twitter.TwDialog.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Uri uri = Uri.parse(url);
                String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
                Log.d(TwDialog.TAG, "Verifier: " + verifier);
                Bundle values = new Bundle();
                try {
                    TwDialog.this.mProvider.retrieveAccessToken(TwDialog.this.mConsumer, verifier);
                    values.putString("access_token", TwDialog.this.mConsumer.getToken());
                    values.putString(Twitter.SECRET_TOKEN, TwDialog.this.mConsumer.getTokenSecret());
                    TwDialog.this.mListener.onComplete(values);
                } catch (OAuthCommunicationException e) {
                    TwDialog.this.mListener.onError(new DialogError(e.getMessage(), -1, verifier));
                } catch (OAuthExpectationFailedException e2) {
                    TwDialog.this.mListener.onTwitterError(new TwitterError(e2.getMessage()));
                } catch (OAuthMessageSignerException e3) {
                    TwDialog.this.mListener.onError(new DialogError(e3.getMessage(), -1, verifier));
                } catch (OAuthNotAuthorizedException e4) {
                    TwDialog.this.mListener.onTwitterError(new TwitterError(e4.getMessage()));
                }
                TwDialog.this.mHandler.post(new Runnable() { // from class: com.sugree.twitter.TwDialog.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        TwDialog.this.mSpinner.dismiss();
                        TwDialog.this.dismiss();
                    }
                });
            }
        }.start();
    }

    private void setUpWebView() {
        this.mWebView = new WebView(getContext());
        this.mWebView.setVerticalScrollBarEnabled(false);
        this.mWebView.setHorizontalScrollBarEnabled(false);
        this.mWebView.setWebViewClient(new TwWebViewClient());
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.setLayoutParams(FILL);
        this.mContent.addView(this.mWebView);
    }

    private class TwWebViewClient extends WebViewClient {
        private TwWebViewClient() {
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TwDialog.TAG, "Redirect URL: " + url);
            if (url.startsWith(Twitter.CALLBACK_URI)) {
                TwDialog.this.retrieveAccessToken(url);
            } else if (url.startsWith(Twitter.CANCEL_URI)) {
                TwDialog.this.mListener.onCancel();
                TwDialog.this.dismiss();
            } else {
                TwDialog.this.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
            }
            return true;
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            TwDialog.this.mListener.onError(new DialogError(description, errorCode, failingUrl));
            TwDialog.this.dismiss();
        }

        @Override // android.webkit.WebViewClient
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TwDialog.TAG, "WebView loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            if (TwDialog.this.mSpinner.isShowing()) {
                TwDialog.this.mSpinner.dismiss();
            }
            TwDialog.this.mSpinner.show();
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String title = TwDialog.this.mWebView.getTitle();
            if (title != null && title.length() > 0) {
                TwDialog.this.mTitle.setText(title);
            }
            TwDialog.this.mSpinner.dismiss();
        }
    }
}
