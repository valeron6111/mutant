package com.arellomobile.android.push;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/* loaded from: classes.dex */
public class MessageActivity extends Activity {
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent notifyIntent = new Intent();
        notifyIntent.setAction("com.alawar.mutant.MESSAGE");
        notifyIntent.setFlags(603979776);
        notifyIntent.putExtras(getIntent().getExtras());
        try {
            startActivity(notifyIntent);
        } catch (ActivityNotFoundException e) {
            Log.e(getClass().getSimpleName(), "Cannot launch activity: " + notifyIntent.getAction(), e);
        }
        finish();
    }
}
