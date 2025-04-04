package com.openfeint.internal.eventlog;

import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.analytics.AnalyticsManager;
import com.openfeint.internal.analytics.IAnalyticsLogger;
import com.openfeint.internal.analytics.SDKAnalyticsLogFactory;
import com.openfeint.internal.logcat.OFLog;

/* loaded from: classes.dex */
public class SDKEventListener implements IEventListener {
    public static String tag = "SDKEventListener";
    private String name = "SDKEventListener";

    @Override // com.openfeint.internal.eventlog.IEventListener
    public String getName() {
        return this.name;
    }

    @Override // com.openfeint.internal.eventlog.IEventListener
    public void handleEvent(String eventType, Object value) {
        OFLog.m181d(tag, "MakeEvent in SDKEventListener:" + eventType);
        if (eventType.equals(EventLogDispatcher.NEW_USER)) {
            if (this.name.equals(value)) {
                OpenFeintInternal.setUserEnableSource(MutantMessages.sEmpty);
            }
            IAnalyticsLogger event = SDKAnalyticsLogFactory.getNewClientSDKBaseLog("new_user");
            AnalyticsManager.instance().makelog(event, tag);
            return;
        }
        if (eventType.equals(EventLogDispatcher.ENABLED_OF)) {
            OFLog.m181d(tag, "ENABLE OLD USER " + (value == null ? MutantMessages.sEmpty : "from " + value));
            if (this.name.equals(value)) {
                OpenFeintInternal.setUserEnableSource(MutantMessages.sEmpty);
            }
            IAnalyticsLogger event2 = SDKAnalyticsLogFactory.getNewClientSDKBaseLog("enabled_of");
            AnalyticsManager.instance().makelog(event2, tag);
            return;
        }
        if (eventType.equals(EventLogDispatcher.PROMPT_ENABLE_OF)) {
            OFLog.m181d(tag, "PROMPT_ENABLE_OF " + (value == null ? MutantMessages.sEmpty : "from " + value));
            if (this.name.equals(value)) {
                OpenFeintInternal.setUserEnableSource(MutantMessages.sEmpty);
            }
            IAnalyticsLogger event3 = SDKAnalyticsLogFactory.getNewClientSDKBaseLog("prompt_enable_of");
            AnalyticsManager.instance().makelog(event3, tag);
            return;
        }
        if (eventType.equals(EventLogDispatcher.ACCEPTED_OF)) {
            OFLog.m181d(tag, "ACCEPTED_OF " + (value == null ? MutantMessages.sEmpty : "from " + value));
            if (this.name.equals(value)) {
                OpenFeintInternal.setUserEnableSource(MutantMessages.sEmpty);
            }
            IAnalyticsLogger event4 = SDKAnalyticsLogFactory.getNewClientSDKBaseLog("accepted_of");
            AnalyticsManager.instance().makelog(event4, tag);
            return;
        }
        if (eventType.equals(EventLogDispatcher.DECLINED_OF)) {
            OFLog.m181d(tag, "DECLINED_OF " + (value == null ? MutantMessages.sEmpty : "from " + value));
            if (this.name.equals(value)) {
                OpenFeintInternal.setUserEnableSource(MutantMessages.sEmpty);
            }
            IAnalyticsLogger event5 = SDKAnalyticsLogFactory.getNewClientSDKBaseLog("declined_of");
            AnalyticsManager.instance().makelog(event5, tag);
            return;
        }
        if (eventType.equals(EventLogDispatcher.GAME_START)) {
            OFLog.m181d(tag, "GAME_START " + (value == null ? MutantMessages.sEmpty : "from " + value));
            if (this.name.equals(value)) {
                OpenFeintInternal.setUserEnableSource(MutantMessages.sEmpty);
            }
            IAnalyticsLogger event6 = SDKAnalyticsLogFactory.getNewClientSDKBaseLog("game_launch");
            AnalyticsManager.instance().makelog(event6, tag);
            return;
        }
        if (eventType.equals(EventLogDispatcher.GAME_BACKGROUND)) {
            OFLog.m181d(tag, "GAME_BACKGROUND " + (value == null ? MutantMessages.sEmpty : "from " + value));
            if (this.name.equals(value)) {
                OpenFeintInternal.setUserEnableSource(MutantMessages.sEmpty);
            }
            IAnalyticsLogger event7 = SDKAnalyticsLogFactory.getNewClientSDKBaseLog("game_background");
            AnalyticsManager.instance().makelog(event7, tag);
            return;
        }
        if (eventType.equals(EventLogDispatcher.GAME_FOREGROUND)) {
            OFLog.m181d(tag, "GAME_FOREGROUND " + (value == null ? MutantMessages.sEmpty : "from " + value));
            if (this.name.equals(value)) {
                OpenFeintInternal.setUserEnableSource(MutantMessages.sEmpty);
            }
            IAnalyticsLogger event8 = SDKAnalyticsLogFactory.getNewClientSDKBaseLog("game_foreground");
            AnalyticsManager.instance().makelog(event8, tag);
            return;
        }
        if (eventType.equals(EventLogDispatcher.GAME_EXIT)) {
            OFLog.m181d(tag, "GAME_EXIT " + (value == null ? MutantMessages.sEmpty : "from " + value));
            if (this.name.equals(value)) {
                OpenFeintInternal.setUserEnableSource(MutantMessages.sEmpty);
            }
            IAnalyticsLogger event9 = SDKAnalyticsLogFactory.getNewClientSDKBaseLog("game_exit");
            AnalyticsManager.instance().makelog(event9, tag);
        }
    }
}
