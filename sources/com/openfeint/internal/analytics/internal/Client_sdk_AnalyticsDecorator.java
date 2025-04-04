package com.openfeint.internal.analytics.internal;

import com.openfeint.internal.analytics.IAnalyticsLogger;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class Client_sdk_AnalyticsDecorator extends BaseAnalyticsDecorator {
    protected Map<String, Object> client_sdk;

    public Client_sdk_AnalyticsDecorator(IAnalyticsLogger eventLog) {
        super(eventLog);
        this.client_sdk = new HashMap();
        eventLog.makeEvent("client_sdk", this.client_sdk);
    }

    @Override // com.openfeint.internal.analytics.internal.BaseAnalyticsDecorator, com.openfeint.internal.analytics.IAnalyticsLogger
    public Map<String, Object> getMap() {
        return this.logger.getMap();
    }

    @Override // com.openfeint.internal.analytics.internal.BaseAnalyticsDecorator, com.openfeint.internal.analytics.IAnalyticsLogger
    public void makeEvent(String key, Object value) {
        this.client_sdk.put(key, value);
    }

    @Override // com.openfeint.internal.analytics.IAnalyticsLogger
    public String getName() {
        return "client_sdk";
    }
}
