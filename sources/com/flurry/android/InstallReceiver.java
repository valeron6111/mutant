package com.flurry.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import com.alawar.mutant.jni.MutantMessages;
import com.flurry.android.FlurryAgent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class InstallReceiver extends BroadcastReceiver {

    /* renamed from: a */
    private final Handler f69a;

    /* renamed from: b */
    private File f70b = null;

    private InstallReceiver() {
        HandlerThread handlerThread = new HandlerThread("InstallReceiver");
        handlerThread.start();
        this.f69a = new Handler(handlerThread.getLooper());
    }

    @Override // android.content.BroadcastReceiver
    public final void onReceive(Context context, Intent intent) {
        this.f70b = context.getFileStreamPath(".flurryinstallreceiver." + Integer.toString(FlurryAgent.m46e().hashCode(), 16));
        if (FlurryAgent.isCaptureUncaughtExceptions()) {
            Thread.setDefaultUncaughtExceptionHandler(new FlurryAgent.FlurryDefaultExceptionHandler());
        }
        String stringExtra = intent.getStringExtra("referrer");
        if (stringExtra != null && "com.android.vending.INSTALL_REFERRER".equals(intent.getAction())) {
            try {
                m59a(m58a(stringExtra));
            } catch (IllegalArgumentException e) {
                C0078ah.m80c("InstallReceiver", "Invalid referrer Tag: " + e.getMessage());
            }
        }
    }

    /* renamed from: a */
    private static Map m58a(String str) {
        if (str == null || str.trim().equals(MutantMessages.sEmpty)) {
            throw new IllegalArgumentException("Referrer is null or empty");
        }
        HashMap hashMap = new HashMap();
        String[] split = str.split("&");
        int length = split.length;
        for (int i = 0; i < length; i++) {
            String[] split2 = split[i].split("=");
            if (split2.length != 2) {
                C0078ah.m72a("InstallReceiver", "Invalid referrer Element: " + split[i] + " in referrer tag " + str);
            } else {
                hashMap.put(split2[0], split2[1]);
            }
        }
        StringBuilder sb = new StringBuilder();
        if (hashMap.get("utm_source") == null) {
            sb.append("Campaign Source is missing.\n");
        }
        if (hashMap.get("utm_medium") == null) {
            sb.append("Campaign Medium is missing.\n");
        }
        if (hashMap.get("utm_campaign") == null) {
            sb.append("Campaign Name is missing.\n");
        }
        if (sb.length() > 0) {
            throw new IllegalArgumentException(sb.toString());
        }
        return hashMap;
    }

    /* renamed from: a */
    private synchronized void m59a(Map map) {
        this.f69a.post(new RunnableC0101t(this, map));
    }
}
