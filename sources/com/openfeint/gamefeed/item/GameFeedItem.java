package com.openfeint.gamefeed.item;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import com.openfeint.gamefeed.element.GameFeedElement;
import com.openfeint.gamefeed.element.TextElement;
import com.openfeint.gamefeed.element.image.ImageElement;
import com.openfeint.internal.BaseActionInvoker;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.request.JSONContentRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class GameFeedItem extends GameFeedItemBase {
    static final BaseActionInvoker sInvoker = new ItemActionInvoker();
    static final String tag = "GameFeedItem";
    Map<String, Object> action;
    String analytics_name;
    List<GameFeedElement> elements;
    String impression_path;
    String instance_key;
    String item_type;

    public GameFeedItem() {
        this.elements = new ArrayList(2);
    }

    public GameFeedItem(int numOfElement) {
        this.elements = new ArrayList(numOfElement);
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void addGameBarElement(GameFeedElement element) {
        this.elements.add(element);
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public View GenerateFeed(Context context) {
        RelativeLayout layout = new RelativeLayout(context);
        for (GameFeedElement element : this.elements) {
            if (element instanceof ImageElement) {
                RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(element.f271w, element.f270h);
                imageLayoutParams.leftMargin = element.f272x;
                imageLayoutParams.topMargin = element.f273y;
                layout.addView(element.getView(context), imageLayoutParams);
            } else if (element instanceof TextElement) {
                RelativeLayout.LayoutParams textLayoutparams = new RelativeLayout.LayoutParams(element.f271w, element.f270h);
                textLayoutparams.leftMargin = element.f272x;
                textLayoutparams.topMargin = element.f273y;
                layout.addView(element.getView(context), textLayoutparams);
            } else {
                OFLog.m182e(tag, "not a matching type");
            }
        }
        return layout;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void invokeAction(View v) {
        sInvoker.invokeAction(this.action, v.getContext());
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void itemActuallyShown() {
        if (this.impression_path != null) {
            new JSONContentRequest() { // from class: com.openfeint.gamefeed.item.GameFeedItem.1
                @Override // com.openfeint.internal.request.BaseRequest
                public boolean signed() {
                    return false;
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String method() {
                    return "GET";
                }

                @Override // com.openfeint.internal.request.BaseRequest
                protected String baseServerURL() {
                    return OpenFeintInternal.getInstance().getAdServerUrl();
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String path() {
                    return GameFeedItem.this.impression_path.startsWith("/") ? GameFeedItem.this.impression_path : "/" + GameFeedItem.this.impression_path;
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public void onResponse(int responseCode, byte[] body) {
                    OFLog.m181d(GameFeedItem.tag, String.format("Ad impression %s - responsecode %d", url(), Integer.valueOf(responseCode)));
                }
            }.launch();
        }
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public String getInstance_key() {
        return this.instance_key;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setInstance_key(String instanceKey) {
        this.instance_key = instanceKey;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public String getAnalytics_name() {
        return this.analytics_name;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setAnalytics_name(String analyticsName) {
        this.analytics_name = analyticsName;
    }

    public String getImpressionPath() {
        return this.impression_path;
    }

    public void setImpressionPath(String impressionPath) {
        this.impression_path = impressionPath;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public String getItem_type() {
        return this.item_type;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setItem_type(String itemType) {
        this.item_type = itemType;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setAction(Map<String, Object> action) {
        this.action = action;
    }
}
