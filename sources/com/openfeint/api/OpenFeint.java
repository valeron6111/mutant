package com.openfeint.api;

import android.content.Context;
import com.openfeint.api.resource.CurrentUser;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.analytics.internal.TimeUtil;
import com.openfeint.internal.eventlog.EventLogDispatcher;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.offline.OfflineSupport;
import java.util.Date;

/* loaded from: classes.dex */
public class OpenFeint {
    private static final String TAG = "OpenFeint";

    public static CurrentUser getCurrentUser() {
        return OpenFeintInternal.getInstance().getCurrentUser();
    }

    public static boolean isUserLoggedIn() {
        return OpenFeintInternal.getInstance().isUserLoggedIn();
    }

    public static void initialize(Context ctx, OpenFeintSettings settings, OpenFeintDelegate delegate) {
        OpenFeintInternal.initialize(ctx, settings, delegate);
    }

    public static void initializeWithoutLoggingIn(Context ctx, OpenFeintSettings settings, OpenFeintDelegate delegate) {
        OpenFeintInternal.initializeWithoutLoggingIn(ctx, settings, delegate);
    }

    public static void setDelegate(OpenFeintDelegate delegate) {
        OpenFeintInternal.getInstance().setDelegate(delegate);
    }

    public static void login() {
        OpenFeintInternal.getInstance().login(false);
    }

    public static boolean isNetworkConnected() {
        return OpenFeintInternal.getInstance().isFeintServerReachable();
    }

    public static void userApprovedFeint() {
        OpenFeintInternal.getInstance().userApprovedFeint();
    }

    public static void userDeclinedFeint() {
        OpenFeintInternal.getInstance().userDeclinedFeint();
    }

    public static void logoutUser() {
        OpenFeintInternal.getInstance().logoutUser(null);
    }

    public static void trySubmitOfflineData() {
        OfflineSupport.trySubmitOfflineData();
    }

    public static void onPause() {
        OFLog.m181d(TAG, "onPause");
        Date start = TimeUtil.getActivity_start_time();
        if (start != null) {
            long plus = System.currentTimeMillis() - start.getTime();
            TimeUtil.addAccumulated_ms(plus);
        }
        TimeUtil.setActivity_start_time(null);
        EventLogDispatcher.getInstance().postEvent(EventLogDispatcher.GAME_BACKGROUND, null);
    }

    public static void onResume() {
        OFLog.m181d(TAG, "onResume");
        TimeUtil.setActivity_start_time(new Date());
        EventLogDispatcher.getInstance().postEvent(EventLogDispatcher.GAME_FOREGROUND, null);
    }

    public static void onExit() {
        OFLog.m181d(TAG, "onExit");
        EventLogDispatcher.getInstance().postEvent(EventLogDispatcher.GAME_EXIT, null);
    }
}
