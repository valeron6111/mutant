package com.alawar.mutant.thirdparty.twitter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.alawar.mutant.C0019R;
import com.alawar.mutant.MutantStats;
import com.alawar.mutant.Settings;
import com.alawar.mutant.network.RequestMethod;
import com.alawar.mutant.network.http.RestClient;
import com.alawar.mutant.thirdparty.FeedPostResult;
import com.google.android.c2dm.C2DMBaseReceiver;
import com.openfeint.internal.JsonCoder;
import com.sugree.twitter.DialogError;
import com.sugree.twitter.Twitter;
import com.sugree.twitter.TwitterError;
import com.sugree.twitter.Util;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.HttpRequestAdapter;
import oauth.signpost.http.HttpRequest;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

/* loaded from: classes.dex */
public class TwitterClient {
    public static final String ACCESS_TOKEN_KEY = "twitter:accessToken";
    public static final String CONSUMER_KEY = "JdVXciTAihJcP33DIVGGQ";
    public static final String CONSUMER_SECRET = "eWKkULgtVACuWxf8OQjjWKNImkWXxzQ0FqcfmzDqhwQ";
    public static final String SECRET_TOKEN_KEY = "twitter:secretToken";
    private Twitter mTwitter = new Twitter(C0019R.drawable.icon);

    private static String getMessageSuffix() {
        return Settings.appMarketShortUrl + " #mutantgame #android";
    }

    public TwitterClient() {
        this.mTwitter.setAccessToken(MutantStats.getString(ACCESS_TOKEN_KEY));
        this.mTwitter.setSecretToken(MutantStats.getString(SECRET_TOKEN_KEY));
    }

    private void doLogin(Context context, final ClientDialogListener listener) {
        this.mTwitter.authorize(context, new Handler(), CONSUMER_KEY, CONSUMER_SECRET, new Twitter.DialogListener() { // from class: com.alawar.mutant.thirdparty.twitter.TwitterClient.1
            @Override // com.sugree.twitter.Twitter.DialogListener
            public void onComplete(Bundle values) {
                MutantStats.setString(TwitterClient.ACCESS_TOKEN_KEY, TwitterClient.this.mTwitter.getAccessToken());
                MutantStats.setString(TwitterClient.SECRET_TOKEN_KEY, TwitterClient.this.mTwitter.getSecretToken());
                listener.onComplete(values);
            }

            @Override // com.sugree.twitter.Twitter.DialogListener
            public void onTwitterError(TwitterError e) {
                listener.onTwitterError(e);
            }

            @Override // com.sugree.twitter.Twitter.DialogListener
            public void onError(DialogError e) {
                listener.onError(e);
            }

            @Override // com.sugree.twitter.Twitter.DialogListener
            public void onCancel() {
                listener.onCancel();
            }
        });
    }

    public void post(final Activity context, final String message) {
        if (!this.mTwitter.isSessionValid() || tryPost(context, message) == FeedPostResult.AUTH_ERROR) {
            doLogin(context, new ClientDialogListener(context) { // from class: com.alawar.mutant.thirdparty.twitter.TwitterClient.2
                @Override // com.sugree.twitter.Twitter.DialogListener
                public void onComplete(Bundle values) {
                    TwitterClient.this.tryPost(context, message);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public FeedPostResult tryPost(Context context, String text) {
        String message = text + " " + getMessageSuffix();
        try {
            CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
            consumer.setTokenWithSecret(this.mTwitter.getAccessToken(), this.mTwitter.getSecretToken());
            HttpPost httpPost = new HttpPost("http://api.twitter.com/1/statuses/update.json");
            List<BasicNameValuePair> params = Arrays.asList(new BasicNameValuePair("status", message));
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpRequest request = consumer.sign((HttpRequest) new HttpRequestAdapter(httpPost));
            RestClient restClient = new RestClient(request.getRequestUrl());
            for (Map.Entry<String, String> entry : request.getAllHeaders().entrySet()) {
                restClient.addHeader(entry.getKey(), entry.getValue());
            }
            for (BasicNameValuePair param : params) {
                restClient.addParam(param.getName(), param.getValue());
            }
            restClient.execute(RequestMethod.POST);
            String response = restClient.getResponse();
            if (response == null) {
                Log.w("MutantTwitterClient", "No response");
                return FeedPostResult.ERROR;
            }
            Log.d("MutantTwitterClient", "Code: " + restClient.getResponseCode());
            Log.d("MutantTwitterClient", "Response: " + response);
            Object result = JsonCoder.parse(response);
            if (result instanceof Map) {
                Map map = (Map) result;
                Object error = map.get(C2DMBaseReceiver.EXTRA_ERROR);
                if ((error instanceof String) && ((String) error).contains("authenticate")) {
                    return FeedPostResult.AUTH_ERROR;
                }
            }
            return FeedPostResult.OK;
        } catch (Exception e) {
            Log.w("MutantTwitterClient", "Exception: " + e.getMessage());
            return FeedPostResult.ERROR;
        }
    }

    private abstract class ClientDialogListener implements Twitter.DialogListener {
        private final Activity context;

        public ClientDialogListener(Activity context) {
            this.context = context;
        }

        private void showError(final Throwable e) {
            this.context.runOnUiThread(new Runnable() { // from class: com.alawar.mutant.thirdparty.twitter.TwitterClient.ClientDialogListener.1
                @Override // java.lang.Runnable
                public void run() {
                    Util.showAlert(ClientDialogListener.this.context, "Twitter", "onTwitterError: " + e.getLocalizedMessage());
                }
            });
        }

        @Override // com.sugree.twitter.Twitter.DialogListener
        public void onTwitterError(TwitterError e) {
            showError(e);
        }

        @Override // com.sugree.twitter.Twitter.DialogListener
        public void onError(DialogError e) {
            showError(e);
        }

        @Override // com.sugree.twitter.Twitter.DialogListener
        public void onCancel() {
        }
    }
}
