package com.alawar.mutant.thirdparty.openfeint;

import android.app.Activity;
import android.util.Log;
import com.openfeint.api.OpenFeint;
import com.openfeint.api.OpenFeintDelegate;
import com.openfeint.api.OpenFeintSettings;
import com.openfeint.api.resource.Achievement;
import com.openfeint.api.resource.CurrentUser;
import com.openfeint.api.resource.User;

/* loaded from: classes.dex */
public class OpenFeintClient {
    static OpenFeintClient _instance = null;
    static final String gameID = "490974";
    static final String gameKey = "H9A6vNCb8sfjFrPxOFNuBw";
    static final String gameName = "Mutant";
    static final String gameSecret = "aRZlFur9BjFohYeCrWsY8RTWntU9u53YyrYg5FF1ag";
    boolean isAvailable = false;

    public static OpenFeintClient instance() {
        if (_instance == null) {
            _instance = new OpenFeintClient();
        }
        return _instance;
    }

    public boolean isAvailable() {
        return this.isAvailable;
    }

    public void initialize(Activity context, final OpenFeintClientInitializedCallback callback) {
        OpenFeintSettings settings = new OpenFeintSettings("Mutant", gameKey, gameSecret, gameID);
        OpenFeint.initialize(context, settings, new OpenFeintDelegate() { // from class: com.alawar.mutant.thirdparty.openfeint.OpenFeintClient.1
            @Override // com.openfeint.api.OpenFeintDelegate
            public void userLoggedIn(CurrentUser user) {
                super.userLoggedIn(user);
                OpenFeintClient.this.isAvailable = true;
                Log.i("OpenFeintClient", "Connected!");
                if (callback != null) {
                    callback.onFinish();
                }
            }

            @Override // com.openfeint.api.OpenFeintDelegate
            public void userLoggedOut(User user) {
                super.userLoggedOut(user);
                OpenFeintClient.this.isAvailable = false;
                Log.i("OpenFeintClient", "Disconnected!");
            }
        });
    }

    public void reportAchievement(String achId) {
        new Achievement(achId).unlock(new Achievement.UnlockCB() { // from class: com.alawar.mutant.thirdparty.openfeint.OpenFeintClient.2
            @Override // com.openfeint.api.resource.Achievement.UnlockCB
            public void onSuccess(boolean newUnlock) {
                Log.i("OpenFeintClient", "Achievement reported");
            }

            @Override // com.openfeint.internal.APICallback
            public void onFailure(String exceptionMessage) {
                Log.e("OpenFeintClient", "Error (" + exceptionMessage + ") unlocking achievement.");
            }
        });
    }
}
