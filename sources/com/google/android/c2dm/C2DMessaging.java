package com.google.android.c2dm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.alawar.mutant.jni.MutantMessages;

/* loaded from: classes.dex */
public class C2DMessaging {
    public static final String BACKOFF = "backoff";
    private static final long DEFAULT_BACKOFF = 30000;
    public static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
    public static final String EXTRA_SENDER = "sender";
    public static final String GSF_PACKAGE = "com.google.android.gsf";
    public static final String LAST_REGISTRATION_CHANGE = "last_registration_change";
    static final String PREFERENCE = "com.google.android.c2dm";
    public static final String REQUEST_REGISTRATION_INTENT = "com.google.android.c2dm.intent.REGISTER";
    public static final String REQUEST_UNREGISTRATION_INTENT = "com.google.android.c2dm.intent.UNREGISTER";

    public static void register(Context context, String senderId) {
        Intent registrationIntent = new Intent(REQUEST_REGISTRATION_INTENT);
        registrationIntent.setPackage(GSF_PACKAGE);
        registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        registrationIntent.putExtra(EXTRA_SENDER, senderId);
        context.startService(registrationIntent);
    }

    public static void unregister(Context context) {
        Intent regIntent = new Intent(REQUEST_UNREGISTRATION_INTENT);
        regIntent.setPackage(GSF_PACKAGE);
        regIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        context.startService(regIntent);
    }

    public static String getRegistrationId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, 0);
        String registrationId = prefs.getString("dm_registration", MutantMessages.sEmpty);
        return registrationId;
    }

    public static long getLastRegistrationChange(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, 0);
        return prefs.getLong(LAST_REGISTRATION_CHANGE, 0L);
    }

    static long getBackoff(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, 0);
        return prefs.getLong(BACKOFF, DEFAULT_BACKOFF);
    }

    static void setBackoff(Context context, long backoff) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(BACKOFF, backoff);
        editor.commit();
    }

    static void clearRegistrationId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("dm_registration", MutantMessages.sEmpty);
        editor.putLong(LAST_REGISTRATION_CHANGE, System.currentTimeMillis());
        editor.commit();
    }

    static void setApplicationId(Context context, String applicationId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("dm_pwapp", applicationId);
        editor.commit();
    }

    public static String getApplicationId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, 0);
        String applicationId = prefs.getString("dm_pwapp", MutantMessages.sEmpty);
        return applicationId;
    }

    static void setRegistrationId(Context context, String registrationId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("dm_registration", registrationId);
        editor.commit();
    }
}
