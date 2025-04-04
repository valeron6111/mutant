package com.openfeint.internal.analytics;

import com.openfeint.api.resource.User;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.analytics.internal.BaseAnalyticsLog;
import com.openfeint.internal.analytics.internal.Client_sdk_AnalyticsDecorator;
import com.openfeint.internal.analytics.internal.EventDecorator;
import com.openfeint.internal.analytics.internal.TimeUtil;
import java.util.Date;

/* loaded from: classes.dex */
public class SDKAnalyticsLogFactory {
    public static IAnalyticsLogger getNewClientSDKBaseLog(String action) {
        IAnalyticsLogger base = new BaseAnalyticsLog();
        IAnalyticsLogger sdk = new Client_sdk_AnalyticsDecorator(base);
        IAnalyticsLogger event = new EventDecorator(sdk);
        addBasicEvent(event);
        event.makeEvent("action", action);
        return event;
    }

    public static void addBasicEvent(IAnalyticsLogger event) {
        if (event != null) {
            event.makeEvent("gsdi", OpenFeintInternal.getInstance().getGSDI());
            event.makeEvent("session_id", OpenFeintInternal.getInstance().getSessionID());
            Date date = OpenFeintInternal.getInstance().getSessionStartDate();
            event.makeEvent("session_length", Float.valueOf((System.currentTimeMillis() - date.getTime()) / 1000.0f));
            User currentUser = OpenFeintInternal.getInstance().getCurrentUser();
            String userId = currentUser != null ? currentUser.userID() : "0";
            event.makeEvent("user_id", userId);
            long session_play_time_ms = TimeUtil.getAccumulated_ms();
            float session_play_time_second = session_play_time_ms / 1000.0f;
            event.makeEvent("session_play_time", Float.valueOf(session_play_time_second));
        }
    }
}
