package com.arellomobile.android.push;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/* loaded from: classes.dex */
public class PushEventsTransmitter {
    private static final String TAG = "MutantNotifications";

    private static void transmit(Context context, String stringToShow, String messageKey, String alertString) {
        Log.i(TAG, alertString);
        Intent notifyIntent = new Intent(context, (Class<?>) MessageActivity.class);
        notifyIntent.putExtra(messageKey, stringToShow);
        notifyIntent.setFlags(268435456);
        context.startActivity(notifyIntent);
    }

    public static void onRegistered(Context context, String registrationId) {
        String alertString = "Registered. RegistrationId is " + registrationId;
        transmit(context, registrationId, PushManager.REGISTER_EVENT, alertString);
    }

    public static void onRegisterError(Context context, String errorId) {
        String alertString = "Register error. Error message is " + errorId;
        transmit(context, errorId, PushManager.REGISTER_ERROR_EVENT, alertString);
    }

    public static void onUnregistered(Context context, String registrationId) {
        String alertString = "Unregistered. RegistrationId is " + registrationId;
        transmit(context, registrationId, PushManager.UNREGISTER_EVENT, alertString);
    }

    public static void onMessageReceive(Context context, String message) {
        String alertString = "Message received: " + message;
        transmit(context, message, PushManager.PUSH_RECEIVE_EVENT, alertString);
    }
}
