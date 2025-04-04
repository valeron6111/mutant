package com.openfeint.internal.analytics.internal;

import com.openfeint.internal.analytics.IAnalyticsLogger;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class BaseAnalyticsLog implements IAnalyticsLogger {
    protected Map<String, Object> base = new HashMap();

    @Override // com.openfeint.internal.analytics.IAnalyticsLogger
    public Map<String, Object> getMap() {
        return this.base;
    }

    @Override // com.openfeint.internal.analytics.IAnalyticsLogger
    public void makeEvent(String key, Object value) {
        this.base.put(key, value);
    }

    @Override // com.openfeint.internal.analytics.IAnalyticsLogger
    public String getName() {
        return "BaseEventLog";
    }
}
