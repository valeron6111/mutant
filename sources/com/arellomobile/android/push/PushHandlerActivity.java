package com.arellomobile.android.push;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/* loaded from: classes.dex */
public class PushHandlerActivity extends Activity {
    public static final String CLASS_TO_START = "CLASS_TO_START";
    private PushManager mPushManager;

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPushManager = new PushManager(this);
        handlePush();
    }

    private void handlePush() {
        this.mPushManager.onHandlePush(this);
        finish();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handlePush();
    }
}
