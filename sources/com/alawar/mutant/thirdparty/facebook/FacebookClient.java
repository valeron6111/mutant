package com.alawar.mutant.thirdparty.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.alawar.mutant.MutantStats;
import com.alawar.mutant.Settings;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

/* loaded from: classes.dex */
public class FacebookClient {
    static FacebookClient inst;
    Facebook facebook = new Facebook(Settings.facebookAppId);

    public static FacebookClient instance() {
        if (inst == null) {
            inst = new FacebookClient();
        }
        return inst;
    }

    FacebookClient() {
    }

    public void post(final Activity context, final String message) {
        String accessToken = MutantStats.getString("facebook:accessToken");
        long accessExpires = Long.parseLong(MutantStats.getString("facebook:accessExpires", "0"));
        if (accessToken != null) {
            this.facebook.setAccessToken(accessToken);
        }
        if (accessExpires != 0) {
            this.facebook.setAccessExpires(accessExpires);
        }
        if (this.facebook.isSessionValid()) {
            tryPost(context, message);
        } else {
            this.facebook.authorize(context, new Facebook.DialogListener() { // from class: com.alawar.mutant.thirdparty.facebook.FacebookClient.1
                @Override // com.facebook.android.Facebook.DialogListener
                public void onComplete(Bundle values) {
                    Log.i("MutantFacebookClient", "Posting to facebook: " + message);
                    MutantStats.setString("facebook:accessToken", FacebookClient.this.facebook.getAccessToken());
                    MutantStats.setString("facebook:accessExpires", Long.toString(FacebookClient.this.facebook.getAccessExpires()));
                    FacebookClient.this.tryPost(context, message);
                }

                @Override // com.facebook.android.Facebook.DialogListener
                public void onFacebookError(FacebookError error) {
                }

                @Override // com.facebook.android.Facebook.DialogListener
                public void onError(DialogError e) {
                }

                @Override // com.facebook.android.Facebook.DialogListener
                public void onCancel() {
                }
            });
        }
    }

    void tryPost(Activity context, String message) {
        Bundle params = new Bundle();
        params.putString("description", message);
        params.putString("link", Settings.appMarketUrl);
        this.facebook.dialog(context, "feed", params, new ClientDialogListener(context) { // from class: com.alawar.mutant.thirdparty.facebook.FacebookClient.2
            @Override // com.facebook.android.Facebook.DialogListener
            public void onComplete(Bundle values) {
                Log.i("MutantFacebookClient", "Post succeeds");
            }
        });
    }

    public void authorized(int requestCode, int resultCode, Intent data) {
        this.facebook.authorizeCallback(requestCode, resultCode, data);
    }

    public void extendAccessTokenIfNeeded(Activity context) {
        this.facebook.extendAccessToken(context, null);
    }

    private abstract class ClientDialogListener implements Facebook.DialogListener {
        private final Context context;

        public ClientDialogListener(Context context) {
            this.context = context;
        }

        @Override // com.facebook.android.Facebook.DialogListener
        public void onFacebookError(FacebookError e) {
        }

        @Override // com.facebook.android.Facebook.DialogListener
        public void onError(DialogError e) {
        }

        @Override // com.facebook.android.Facebook.DialogListener
        public void onCancel() {
        }
    }
}
