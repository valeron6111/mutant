package com.openfeint.internal.analytics.internal;

import com.openfeint.internal.analytics.IAnalyticsLogger;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class BaseAnalyticsDecorator implements IAnalyticsLogger {
    protected IAnalyticsLogger logger;

    public BaseAnalyticsDecorator(IAnalyticsLogger logger) {
        this.logger = logger;
    }

    @Override // com.openfeint.internal.analytics.IAnalyticsLogger
    public Map<String, Object> getMap() {
        return this.logger.getMap();
    }

    @Override // com.openfeint.internal.analytics.IAnalyticsLogger
    public void makeEvent(String key, Object value) {
        this.logger.makeEvent(key, value);
    }
}
