package com.sponsorpay.sdk.android.advertiser;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;
import com.alawar.mutant.jni.MutantMessages;
import com.sponsorpay.sdk.android.HostInfo;
import com.sponsorpay.sdk.android.UrlBuilder;
import com.tapjoy.TapjoyConstants;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class SponsorPayCallbackDelayer extends BroadcastReceiver {
    public static final String ACTION_TRIGGER_SPONSORPAY_CALLBACK = "ACTION_TRIGGER_SPONSORPAY_CALLBACK";
    public static final String EXTRA_APPID_KEY = "EXTRA_APPID_KEY";
    public static final String EXTRA_CUSTOM_PARAMETERS = "EXTRA_CUSTOM_PARAMETERS";
    public static final int MILLISECONDS_IN_MINUTE = 60000;

    public static void callWithDelay(Context context, String appId, long delayMinutes) {
        callWithDelay(context, appId, delayMinutes, null);
    }

    public static void callWithDelay(Context context, String appId, long delayMinutes, HashMap<String, String> customParams) {
        Log.d(SponsorPayCallbackDelayer.class.toString(), "callWithDelay called");
        if (appId == null || appId.equals(MutantMessages.sEmpty)) {
            HostInfo hostInfo = new HostInfo(context);
            hostInfo.getAppId();
        }
        SponsorPayCallbackDelayer delayerInstance = new SponsorPayCallbackDelayer();
        IntentFilter checkInFilter = new IntentFilter(ACTION_TRIGGER_SPONSORPAY_CALLBACK);
        context.registerReceiver(delayerInstance, checkInFilter);
        Intent intent = new Intent(ACTION_TRIGGER_SPONSORPAY_CALLBACK);
        intent.putExtra(EXTRA_APPID_KEY, appId);
        if (customParams != null) {
            UrlBuilder.validateKeyValueParams(customParams);
            intent.putExtra(EXTRA_CUSTOM_PARAMETERS, customParams);
        }
        PendingIntent triggerCallbackPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 1073741824);
        long timeForCheckInAlarm = SystemClock.elapsedRealtime() + (TapjoyConstants.THROTTLE_GET_TAP_POINTS_INTERVAL * delayMinutes);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
        alarmManager.set(2, timeForCheckInAlarm, triggerCallbackPendingIntent);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().toString(), "Calling SponsorPayAdvertiser.register");
        Map<String, String> customParams = null;
        Serializable inflatedKvMap = intent.getSerializableExtra(EXTRA_CUSTOM_PARAMETERS);
        if (inflatedKvMap instanceof HashMap) {
            customParams = (HashMap) inflatedKvMap;
        }
        SponsorPayAdvertiser.register(context, intent.getStringExtra(EXTRA_APPID_KEY), customParams);
        context.unregisterReceiver(this);
    }
}
