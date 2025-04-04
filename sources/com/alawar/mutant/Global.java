package com.alawar.mutant;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import com.alawar.mutant.notification.Notifications;

/* loaded from: classes.dex */
public class Global {
    public static Activity applicationContext;

    public static void initialize(Activity context) {
        applicationContext = context;
        Log.i(Notifications.TAG, String.format("Device: [%s], Brand: [%s], Model: [%s], Manufacturer: [%s]", Build.DEVICE, Build.BRAND, Build.MODEL, Build.MANUFACTURER));
    }
}
