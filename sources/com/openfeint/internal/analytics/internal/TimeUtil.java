package com.openfeint.internal.analytics.internal;

import java.util.Date;

/* loaded from: classes.dex */
public class TimeUtil {
    private static long accumulated_ms;
    private static Date activity_start_time;

    public static void addAccumulated_ms(long addValue) {
        accumulated_ms += addValue;
    }

    public static long getAccumulated_ms() {
        return accumulated_ms;
    }

    public static void setActivity_start_time(Date activity_start_time2) {
        activity_start_time = activity_start_time2;
    }

    public static Date getActivity_start_time() {
        return activity_start_time;
    }
}
