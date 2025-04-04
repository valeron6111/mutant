package com.openfeint.internal.analytics;

import java.util.Map;

/* loaded from: classes.dex */
public interface IAnalyticsLogger {
    Map<String, Object> getMap();

    String getName();

    void makeEvent(String str, Object obj);
}
