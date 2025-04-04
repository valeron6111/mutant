package com.sugree.twitter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieSyncManager;
import java.io.IOException;
import java.net.MalformedURLException;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

/* loaded from: classes.dex */
public class Twitter {
    public static final String ACCESS_TOKEN = "access_token";
    public static final String CALLBACK_URI = "twitter://callback";
    public static final String CANCEL_URI = "twitter://cancel";
    public static final String SECRET_TOKEN = "secret_token";
    public static final String TAG = "twitter";
    private CommonsHttpOAuthConsumer mHttpOauthConsumer;
    private CommonsHttpOAuthProvider mHttpOauthProvider;
    private int mIcon;
    protected static String OAUTH_REQUEST_TOKEN = "https://twitter.com/oauth/request_token";
    protected static String OAUTH_ACCESS_TOKEN = "https://twitter.com/oauth/access_token";
    protected static String OAUTH_AUTHORIZE = "https://twitter.com/oauth/authorize";
    private String mAccessToken = null;
    private String mSecretToken = null;

    public interface DialogListener {
        void onCancel();

        void onComplete(Bundle bundle);

        void onError(DialogError dialogError);

        void onTwitterError(TwitterError twitterError);
    }

    public Twitter(int icon) {
        this.mIcon = icon;
    }

    public void authorize(Context ctx, Handler handler, String consumerKey, String consumerSecret, final DialogListener listener) {
        this.mHttpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        this.mHttpOauthProvider = new CommonsHttpOAuthProvider(OAUTH_REQUEST_TOKEN, OAUTH_ACCESS_TOKEN, OAUTH_AUTHORIZE);
        this.mHttpOauthProvider.setOAuth10a(true);
        CookieSyncManager.createInstance(ctx);
        dialog(ctx, handler, new DialogListener() { // from class: com.sugree.twitter.Twitter.1
            @Override // com.sugree.twitter.Twitter.DialogListener
            public void onComplete(Bundle values) {
                CookieSyncManager.getInstance().sync();
                Twitter.this.setAccessToken(values.getString("access_token"));
                Twitter.this.setSecretToken(values.getString(Twitter.SECRET_TOKEN));
                if (Twitter.this.isSessionValid()) {
                    Log.d(Twitter.TAG, "token " + Twitter.this.getAccessToken() + " " + Twitter.this.getSecretToken());
                    listener.onComplete(values);
                } else {
                    onTwitterError(new TwitterError("failed to receive oauth token"));
                }
            }

            @Override // com.sugree.twitter.Twitter.DialogListener
            public void onTwitterError(TwitterError e) {
                Log.w(Twitter.TAG, "Login failed: " + e);
                listener.onTwitterError(e);
            }

            @Override // com.sugree.twitter.Twitter.DialogListener
            public void onError(DialogError e) {
                Log.w(Twitter.TAG, "Login failed: " + e);
                listener.onError(e);
            }

            @Override // com.sugree.twitter.Twitter.DialogListener
            public void onCancel() {
                Log.d(Twitter.TAG, "Login cancelled");
                listener.onCancel();
            }
        });
    }

    public String logout(Context context) throws MalformedURLException, IOException {
        return "true";
    }

    public void dialog(Context ctx, Handler handler, DialogListener listener) {
        if (ctx.checkCallingOrSelfPermission("android.permission.INTERNET") != 0) {
            Util.showAlert(ctx, "Error", "Application requires permission to access the Internet");
        } else {
            new TwDialog(ctx, this.mHttpOauthProvider, this.mHttpOauthConsumer, listener, this.mIcon).show();
        }
    }

    public boolean isSessionValid() {
        return (getAccessToken() == null || getSecretToken() == null) ? false : true;
    }

    public String getAccessToken() {
        return this.mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        this.mAccessToken = accessToken;
    }

    public String getSecretToken() {
        return this.mSecretToken;
    }

    public void setSecretToken(String secretToken) {
        this.mSecretToken = secretToken;
    }
}
