package com.openfeint.gamefeed.item.analytics;

import com.openfeint.internal.analytics.IAnalyticsLogger;
import com.openfeint.internal.analytics.internal.BaseAnalyticsDecorator;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class GameFeedDecorator extends BaseAnalyticsDecorator {
    protected Map<String, Object> game_feed;

    public GameFeedDecorator(IAnalyticsLogger eventLog) {
        super(eventLog);
        this.game_feed = new HashMap();
        eventLog.makeEvent("game_feed", this.game_feed);
    }

    @Override // com.openfeint.internal.analytics.internal.BaseAnalyticsDecorator, com.openfeint.internal.analytics.IAnalyticsLogger
    public Map<String, Object> getMap() {
        return this.logger.getMap();
    }

    @Override // com.openfeint.internal.analytics.internal.BaseAnalyticsDecorator, com.openfeint.internal.analytics.IAnalyticsLogger
    public void makeEvent(String key, Object value) {
        this.game_feed.put(key, value);
    }

    @Override // com.openfeint.internal.analytics.IAnalyticsLogger
    public String getName() {
        return "GameFeedLog";
    }
}
