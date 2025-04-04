package com.arellomobile.android.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.alawar.mutant.jni.MutantMessages;
import com.google.android.c2dm.C2DMessaging;

/* loaded from: classes.dex */
public class PushManager {
    public static final String APP_ID = "4f7d365672d188.82585098";
    private static String HTML_URL_FORMAT = "https://cp.pushwoosh.com/content/%s";
    static final String INFO_MESSAGE_KEY = "com.alawar.mutant.MESSAGE";
    public static final int MESSAGE_ID = 1001;
    public static final String PUSH_RECEIVE_EVENT = "PUSH_RECEIVE_EVENT";
    public static final String REGISTER_ERROR_EVENT = "REGISTER_ERROR_EVENT";
    public static final String REGISTER_EVENT = "REGISTER_EVENT";
    public static final String SENDER_ID = "mobsterandroid@gmail.com";
    public static final String UNREGISTER_EVENT = "UNREGISTER_EVENT";
    private Context context;
    private Bundle lastPush;

    public PushManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context can't be null");
        }
        this.context = context;
    }

    public void onStartup(Bundle savedInstanceState, Context context) {
        Intent intent = new Intent(context, (Class<?>) PushHandlerActivity.class);
        this.context.startActivity(intent);
        if (savedInstanceState == null) {
            if (!(context instanceof Activity) || !((Activity) context).getIntent().hasExtra(PUSH_RECEIVE_EVENT)) {
                C2DMessaging.register(context, SENDER_ID);
                return;
            }
            return;
        }
        String appId = C2DMessaging.getApplicationId(context);
        String id = C2DMessaging.getRegistrationId(context);
        if (id == null || id.equals(MutantMessages.sEmpty) || appId == null || !appId.equals(APP_ID)) {
            C2DMessaging.register(context, SENDER_ID);
        }
    }

    public void unregister() {
        C2DMessaging.unregister(this.context);
    }

    public String getCustomData() {
        if (this.lastPush == null) {
            return null;
        }
        return (String) this.lastPush.get("u");
    }

    public boolean onHandlePush(Activity activity) {
        Bundle pushBundle = activity.getIntent().getBundleExtra("pushBundle");
        if (pushBundle == null || this.context == null) {
            return false;
        }
        this.lastPush = pushBundle;
        String url = (String) pushBundle.get("h");
        if (url != null) {
            String url2 = String.format(HTML_URL_FORMAT, url);
            Intent intent = new Intent(activity, (Class<?>) PushWebview.class);
            intent.putExtra("url", url2);
            activity.startActivity(intent);
        }
        String userData = (String) pushBundle.get("u");
        if (userData == null) {
            userData = (String) pushBundle.get("title");
        }
        PushEventsTransmitter.onMessageReceive(this.context, userData);
        return true;
    }
}
