package com.openfeint.gamefeed.item;

import android.content.Context;
import android.view.View;
import com.openfeint.gamefeed.element.GameFeedElement;
import com.openfeint.internal.analytics.IAnalyticsLogger;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class GameFeedItemBase {
    private boolean shown = false;
    private int position = -1;

    public abstract View GenerateFeed(Context context);

    public abstract void addGameBarElement(GameFeedElement gameFeedElement);

    public abstract String getAnalytics_name();

    public abstract String getInstance_key();

    public abstract String getItem_type();

    public abstract void invokeAction(View view);

    protected abstract void itemActuallyShown();

    public abstract void setAction(Map<String, Object> map);

    public abstract void setAnalytics_name(String str);

    public abstract void setInstance_key(String str);

    public abstract void setItem_type(String str);

    public final boolean isItemShown() {
        return this.shown;
    }

    public final void itemShown() {
        if (!this.shown) {
            this.shown = true;
            itemActuallyShown();
        }
    }

    public final void itemUnshown() {
        this.shown = false;
    }

    public void addAnalyticsParams(IAnalyticsLogger logger) {
        logger.makeEvent("item_type", getItem_type());
        logger.makeEvent("analytics_name", getAnalytics_name());
        logger.makeEvent("instance_key", getInstance_key());
        if (this.position >= 1) {
            logger.makeEvent("feed_position", Integer.valueOf(this.position - 1));
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }
}
