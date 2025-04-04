package com.openfeint.gamefeed.item;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.openfeint.gamefeed.element.GameFeedElement;
import com.openfeint.gamefeed.internal.GameFeedHelper;
import com.openfeint.gamefeed.internal.GameFeedImpl;
import com.openfeint.gamefeed.item.analytics.GameFeedAnalyticsLogFactory;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.analytics.AnalyticsManager;
import com.openfeint.internal.logcat.OFLog;
import java.util.Map;

/* loaded from: classes.dex */
public class LeafFeedItem extends GameFeedItemBase {
    private static final int HIT_STATE_TIME = 500;
    public static final String tag = "LeafFeedItem";
    String analytics_name;

    /* renamed from: h */
    private int f284h;
    String instance_key;

    /* renamed from: w */
    private int f285w;
    private Handler mHandler = new Handler();
    String item_type = "openfeint_leaf";

    public LeafFeedItem(GameFeedImpl impl, int w, int h) {
        this.f285w = w;
        this.f284h = h;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public View GenerateFeed(Context context) {
        RelativeLayout layout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(this.f285w, this.f284h);
        params.leftMargin = 0;
        params.topMargin = 0;
        final ImageView imageView = new ImageView(context);
        imageView.setOnTouchListener(new View.OnTouchListener() { // from class: com.openfeint.gamefeed.item.LeafFeedItem.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 0) {
                    imageView.setAlpha(125);
                    LeafFeedItem.this.mHandler.postDelayed(LeafFeedItem.this.new recoverRunable(imageView), 500L);
                    return false;
                }
                return false;
            }
        });
        imageView.setImageResource(C0207RR.drawable("ofgamefeedbadgeicon"));
        layout.addView(imageView, params);
        return layout;
    }

    class recoverRunable implements Runnable {
        ImageView imageView;

        public recoverRunable(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override // java.lang.Runnable
        public void run() {
            OFLog.m184v(LeafFeedItem.tag, "hit timer: recover!");
            this.imageView.setAlpha(225);
            LeafFeedItem.this.mHandler.postDelayed(LeafFeedItem.this.new recoverRunable(this.imageView), 500L);
        }
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void addGameBarElement(GameFeedElement element) {
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void invokeAction(View v) {
        AnalyticsManager.instance().makelog(GameFeedAnalyticsLogFactory.getGameFeedBaseLog("leaf_item_clicked"), tag);
        GameFeedHelper.OpenDashboadrFromGameFeed(null);
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void itemActuallyShown() {
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public String getAnalytics_name() {
        return this.analytics_name;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public String getInstance_key() {
        return this.instance_key;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public String getItem_type() {
        return this.item_type;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setAction(Map<String, Object> action) {
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setAnalytics_name(String analyticsName) {
        this.analytics_name = analyticsName;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setInstance_key(String instanceKey) {
        this.instance_key = instanceKey;
    }

    @Override // com.openfeint.gamefeed.item.GameFeedItemBase
    public void setItem_type(String itemType) {
        this.item_type = itemType;
    }
}
