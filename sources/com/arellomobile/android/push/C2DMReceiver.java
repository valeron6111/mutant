package com.arellomobile.android.push;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.alawar.mutant.jni.MutantMessages;
import com.google.android.c2dm.C2DMBaseReceiver;
import java.util.List;

/* loaded from: classes.dex */
public class C2DMReceiver extends C2DMBaseReceiver {
    static final String TAG = "C2DMReceiver";

    public C2DMReceiver() {
        super(PushManager.SENDER_ID);
    }

    @Override // com.google.android.c2dm.C2DMBaseReceiver
    public void onRegistered(Context context, String registrationId) {
        DeviceRegistrar.registerWithServer(context, registrationId);
        PushEventsTransmitter.onRegistered(context, registrationId);
    }

    @Override // com.google.android.c2dm.C2DMBaseReceiver
    public void onUnregistered(Context context, String registrationId) {
        DeviceRegistrar.unregisterWithServer(context, registrationId);
        PushEventsTransmitter.onUnregistered(context, registrationId);
    }

    @Override // com.google.android.c2dm.C2DMBaseReceiver
    public void onError(Context context, String errorId) {
        Log.e(TAG, "Messaging registration error: " + errorId);
        PushEventsTransmitter.onRegisterError(context, errorId);
    }

    @Override // com.google.android.c2dm.C2DMBaseReceiver
    protected void onMessage(Context context, Intent intent) {
        Intent notifyIntent;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            extras.putBoolean("foregroud", isAppOnForeground(context));
            String title = (String) extras.get("title");
            String url = (String) extras.get("h");
            String link = (String) extras.get("l");
            if (title == null && url == null && link == null) {
                Intent notifyIntent2 = new Intent(context, (Class<?>) PushHandlerActivity.class);
                notifyIntent2.addFlags(536870912);
                notifyIntent2.putExtra("pushBundle", extras);
            }
            if (link != null) {
                notifyIntent = new Intent("android.intent.action.VIEW", Uri.parse(link));
                notifyIntent.addFlags(268435456);
            } else {
                notifyIntent = new Intent(context, (Class<?>) PushHandlerActivity.class);
                notifyIntent.addFlags(536870912);
                notifyIntent.putExtra("pushBundle", extras);
            }
            NotificationManager manager = (NotificationManager) getSystemService("notification");
            CharSequence appName = context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
            if (appName == null) {
                appName = MutantMessages.sEmpty;
            }
            Notification notification = new Notification(context.getApplicationInfo().icon, ((Object) appName) + ": new message", System.currentTimeMillis());
            notification.flags |= 16;
            PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, notifyIntent, 268435456);
            notification.setLatestEventInfo(context, appName, title, contentIntent);
            manager.notify(PushManager.MESSAGE_ID, notification);
        }
    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == 100 && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
