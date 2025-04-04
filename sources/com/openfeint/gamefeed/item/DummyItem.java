package com.openfeint.gamefeed.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.openfeint.gamefeed.element.GameFeedElement;
import com.openfeint.internal.logcat.OFLog;
import java.util.Map;

/* loaded from: classes.dex */
public class DummyItem extends GameFeedItemBase {
    static final String tag = "DummyItem";

    /* renamed from: h */
    private int f282h;

    /* renamed from: w */
    private int f283w;

    public DummyItem(int w, int h) {
        this.f283w = w;
        this.f282h = h;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public View GenerateFeed(Context context) {
        RelativeLayout layout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(this.f283w, this.f282h);
        params.leftMargin = 0;
        params.topMargin = 0;
        ImageView imageView = new ImageView(context);
        layout.addView(imageView, params);
        return layout;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void addGameBarElement(GameFeedElement element) {
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void invokeAction(View v) {
        OFLog.m181d(tag, "nothing will happened clicked on ");
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void itemActuallyShown() {
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public String getAnalytics_name() {
        return "dummy";
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public String getInstance_key() {
        return "dummy";
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public String getItem_type() {
        return "dummy";
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setAction(Map<String, Object> action) {
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setAnalytics_name(String analyticsName) {
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setInstance_key(String instanceKey) {
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setItem_type(String itemType) {
    }
}
