package com.openfeint.gamefeed.item.analytics;

import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.api.p001ui.Dashboard;
import com.openfeint.gamefeed.element.image.ImageCacheMap;
import com.openfeint.gamefeed.internal.GameFeedImpl;
import com.openfeint.internal.analytics.AnalyticsManager;
import com.openfeint.internal.analytics.IAnalyticsLogger;
import com.openfeint.internal.eventlog.EventLogDispatcher;
import com.openfeint.internal.eventlog.IEventListener;
import com.openfeint.internal.logcat.OFLog;
import java.util.Date;

/* loaded from: classes.dex */
public class GameFeedEventListener implements IEventListener {
    public static String tag = "GgmeFeedDEventListener";
    private Date dashBoardOpenTime;
    private GameFeedImpl impl;
    private String name = "SDKEventListener";

    public GameFeedEventListener(GameFeedImpl _impl) {
        this.impl = _impl;
    }

    @Override // com.openfeint.internal.eventlog.IEventListener
    public String getName() {
        return this.name;
    }

    @Override // com.openfeint.internal.eventlog.IEventListener
    public void handleEvent(String eventType, Object value) {
        OFLog.m181d(tag, "GET Event " + eventType + (value == null ? MutantMessages.sEmpty : " from " + value));
        if (eventType.equals(EventLogDispatcher.LOGIN_SUCESS)) {
            OFLog.m183i(tag, "LOGIN_SUCESS reloaded view");
            this.impl.reload();
            return;
        }
        if (eventType.equals(EventLogDispatcher.DASHBOARD_START)) {
            OFLog.m183i(tag, "DASHBOARD_START " + (value == null ? MutantMessages.sEmpty : "from " + value));
            if ("gamefeed".equals(value)) {
                AnalyticsManager.instance().makelog(GameFeedAnalyticsLogFactory.getGameFeedBaseLog("dashboard_start"), tag);
                this.dashBoardOpenTime = new Date();
                return;
            }
            return;
        }
        if (eventType.equals(EventLogDispatcher.DASHBOARD_END)) {
            OFLog.m183i(tag, "DASHBOARD_END " + (value == null ? MutantMessages.sEmpty : "from " + value));
            if ("gamefeed".equals(value)) {
                Dashboard.setOpenfrom(MutantMessages.sEmpty);
                IAnalyticsLogger event = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("dashboard_end");
                if (this.dashBoardOpenTime != null) {
                    Date endtime = new Date();
                    Long duration = Long.valueOf(endtime.getTime() - this.dashBoardOpenTime.getTime());
                    float durationSecond = duration.longValue() / 1000.0f;
                    event.makeEvent("duration", Float.valueOf(durationSecond));
                }
                AnalyticsManager.instance().makelog(event, tag);
                return;
            }
            return;
        }
        if (eventType.equals(EventLogDispatcher.GAME_BACKGROUND)) {
            ImageCacheMap.stop();
        }
    }
}
