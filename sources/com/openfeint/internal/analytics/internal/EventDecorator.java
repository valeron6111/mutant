package com.openfeint.internal.analytics.internal;

import com.openfeint.internal.analytics.IAnalyticsLogger;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class EventDecorator extends BaseAnalyticsDecorator {
    protected Map<String, Object> event;

    public EventDecorator(IAnalyticsLogger eventLog) {
        super(eventLog);
        this.event = new HashMap();
        eventLog.makeEvent("event", this.event);
    }

    @Override // com.openfeint.internal.analytics.internal.BaseAnalyticsDecorator, com.openfeint.internal.analytics.IAnalyticsLogger
    public Map<String, Object> getMap() {
        return this.logger.getMap();
    }

    @Override // com.openfeint.internal.analytics.internal.BaseAnalyticsDecorator, com.openfeint.internal.analytics.IAnalyticsLogger
    public void makeEvent(String key, Object value) {
        this.event.put(key, value);
    }

    @Override // com.openfeint.internal.analytics.IAnalyticsLogger
    public String getName() {
        return "EventLogDecorator";
    }
}
