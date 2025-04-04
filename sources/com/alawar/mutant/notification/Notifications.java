package com.alawar.mutant.notification;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.arellomobile.android.push.PushManager;

/* loaded from: classes.dex */
public class Notifications {
    public static final String TAG = "Mutant";

    public static void checkMessage(Activity activity, Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
                String string = intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT);
                showMessage(activity, string);
                Log.i(TAG, "PushManager.PUSH_RECEIVE_EVENT: " + string);
            } else if (intent.hasExtra(PushManager.REGISTER_EVENT)) {
                String string2 = intent.getExtras().getString(PushManager.REGISTER_EVENT);
                Log.i(TAG, "PushManager.REGISTER_EVENT: " + string2);
            } else if (intent.hasExtra(PushManager.UNREGISTER_EVENT)) {
                String string3 = intent.getExtras().getString(PushManager.UNREGISTER_EVENT);
                Log.i(TAG, "PushManager.UNREGISTER_EVENT: " + string3);
            } else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
                String string4 = intent.getExtras().getString(PushManager.REGISTER_ERROR_EVENT);
                Log.i(TAG, "PushManager.REGISTER_ERROR_EVENT: " + string4);
            }
        }
    }

    public static void showMessage(Activity activity, String message) {
        Toast.makeText(activity, message, 1).show();
    }
}
