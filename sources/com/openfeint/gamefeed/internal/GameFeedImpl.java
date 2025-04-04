package com.openfeint.gamefeed.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import com.openfeint.gamefeed.GameFeedSettings;
import com.openfeint.gamefeed.GameFeedView;
import com.openfeint.gamefeed.element.GameFeedElement;
import com.openfeint.gamefeed.element.TextElement;
import com.openfeint.gamefeed.element.image.ImageCacheMap;
import com.openfeint.gamefeed.element.image.ImageElement;
import com.openfeint.gamefeed.item.DummyItem;
import com.openfeint.gamefeed.item.GameFeedItem;
import com.openfeint.gamefeed.item.GameFeedItemBase;
import com.openfeint.gamefeed.item.LeafFeedItem;
import com.openfeint.gamefeed.item.analytics.GameFeedAnalyticsLogFactory;
import com.openfeint.gamefeed.item.analytics.GameFeedEventListener;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.JsonCoder;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.Util;
import com.openfeint.internal.analytics.AnalyticsManager;
import com.openfeint.internal.analytics.IAnalyticsLogger;
import com.openfeint.internal.eventlog.EventLogDispatcher;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.p004ui.WebViewCache;
import com.openfeint.internal.p004ui.WebViewCacheCallback;
import com.openfeint.internal.request.BaseRequest;
import com.openfeint.internal.request.JSONContentRequest;
import com.openfeint.internal.request.OrderedArgList;
import com.tapjoy.TapjoyConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class GameFeedImpl {
    private static final Map<String, Object> defaultCustomization = parseDefaultCustomization();
    private static final String defaultCustomizationJSON = "{\"icon_color\":\"#0DA840\",\"profile_frame_image\":{\"bundle\":\"OFGameBarProfileFrame.png\"},\"cell_background_image_landscape\":{\"bundle\":\"OFRegularFullLandscape.png\"},\"icon_color_negative\":\"#FFAC11\",\"cell_divider_image_landscape\":{\"bundle\":\"OFGameBarCellDividerLandscape.png\"},\"cell_hit_image_landscape\":{\"bundle\":\"OFGFIHitLandscape.png\"},\"cell_divider_image_portrait\":{\"bundle\":\"OFGameBarCellDividerPortrait.png\"},\"tab_left_image\":{\"bundle\":\"OFGameBarCustomizeBackLeft.png\"},\"username_color\":\"#098130\",\"highlighted_color\":\"#FFFF00\",\"shadow_color\":\"#000000\",\"title_color\":\"#098130\",\"text_color\":\"#585858\",\"disclosure_color\":\"#585858\",\"tab_right_image\":{\"bundle\":\"OFGameBarCustomizeBackRight.png\"},\"cell_hit_image_portrait\":{\"bundle\":\"OFGFIHitPortrait.png\"},\"call_out_color\":\"#585858\",\"small_profile_frame_image\":{\"bundle\":\"OFGameBarProfileFrameSmall.png\"},\"icon_color_positive\":\"#0DA840\",\"cell_background_image_portrait\":{\"bundle\":\"OFRegularFullPortrait.png\"}}";
    private static final String errorLandscapeJSON = "[{\"type\":\"image\",\"frame\":[0,0,304,54],\"scale_to_fill\":true,\"image\":{\"bundle\":\"OFRegularFullLandscape.png\"}},{\"type\":\"image\",\"frame\":[13,13,28,27],\"framed\":false,\"scale_to_fill\":true,\"image\":{\"bundle\":\"OFGameBarServerErrorIcon.png\"},\"color\":\"custom.icon_color_negative\",\"sharp_corners\":false},{\"type\":\"label\",\"frame\":[56,7,243,12],\"font\":\"Helvetica-Bold\",\"text\":\"Oops! Something went wrong.\"},{\"type\":\"label\",\"frame\":[56,20,243,29],\"font\":\"Helvetica\",\"text\":\"We can't connect to the server right now. Please try again later.\"}]";
    private static final String errorPortraitJSON = "[{\"type\":\"image\",\"frame\":[0,0,229,68],\"scale_to_fill\":true,\"image\":{\"bundle\":\"OFRegularFullPortrait.png\"}},{\"type\":\"image\",\"frame\":[6,20,28,27],\"framed\":false,\"scale_to_fill\":true,\"image\":{\"bundle\":\"OFGameBarServerErrorIcon.png\"},\"color\":\"custom.icon_color_negative\",\"sharp_corners\":false},{\"type\":\"label\",\"frame\":[41,6,180,16],\"font\":\"Helvetica-Bold\",\"text\":\"Oops! Something went wrong.\"},{\"type\":\"label\",\"frame\":[41,20,180,41],\"font\":\"Helvetica\",\"text\":\"We can't connect to the server right now. Please try again later.\"}]";
    private static final String loadingLandscapeJSON = "[{\"type\":\"image\",\"frame\":[0,0,304,54],\"scale_to_fill\":true,\"image\":{\"bundle\":\"OFRegularFullLandscape.png\"}},{\"type\":\"image\",\"frame\":[143,16,20,20],\"framed\":false,\"scale_to_fill\":true,\"image\":{\"loader\":true}}]";
    private static final String loadingPortraitJSON = "[{\"type\":\"image\",\"frame\":[0,0,229,68],\"scale_to_fill\":true,\"image\":{\"bundle\":\"OFRegularFullPortrait.png\"}},{\"type\":\"image\",\"frame\":[105,24,20,20],\"framed\":false,\"scale_to_fill\":true,\"image\":{\"loader\":true}}]";
    public static final String loadingWarningItemType = "loading_warning";
    public static final String networkErrorWarningItemType = "network_error_warning";
    private static final int networkRetryInteval = 15000;
    private static final String offlineLandscapeJSON = "[{\"type\":\"image\",\"frame\":[0,0,304,54],\"scale_to_fill\":true,\"image\":{\"bundle\":\"OFRegularFullLandscape.png\"}},{\"type\":\"image\",\"frame\":[5,10,44,30],\"framed\":false,\"scale_to_fill\":true,\"image\":{\"bundle\":\"OFGameBarOffline.png\"},\"sharp_corners\":false},{\"type\":\"label\",\"frame\":[58,13,241,12],\"font\":\"Helvetica-Bold\",\"text\":\"You're not connected to the internet!\"},{\"type\":\"label\",\"frame\":[58,28,241,12],\"font\":\"Helvetica\",\"text\":\"OpenFeint requires an internet connection.\"}]";
    private static final String offlinePortraitJSON = "[{\"type\":\"image\",\"frame\":[0,0,229,68],\"scale_to_fill\":true,\"image\":{\"bundle\":\"OFRegularFullPortrait.png\"}},{\"type\":\"image\",\"frame\":[4,12,44,30],\"framed\":false,\"scale_to_fill\":true,\"image\":{\"bundle\":\"OFGameBarOffline.png\"},\"sharp_corners\":false},{\"type\":\"label\",\"frame\":[57,4,166,29],\"font\":\"Helvetica-Bold\",\"text\":\"You're not connected to the internet!\"},{\"type\":\"label\",\"frame\":[57,34,172,29],\"font\":\"Helvetica\",\"text\":\"OpenFeint requires an internet connection.\"}]";
    public static final String serverErrorWarningItemType = "server_error_warning";
    private boolean configureLoaded;
    private GameFeedItemBase dummy;
    private List<GameFeedItemBase> feedsPointer;
    private Map<String, Object> itemConfigs;
    private Map<String, Object> layouts;
    private GameFeedItemBase leaf;
    private List<GameFeedItemBase> loadingFeeds;
    private Context mContext;
    private Map<String, Object> mDeveloperCustomSettings;
    private IGameFeedView mGameFeedView;
    private Handler mHandler;
    private Runnable mUpdateTimeTask;
    private List<GameFeedItemBase> netWorkErrorFeeds;
    private List<GameFeedItemBase> pendingAds;
    private List<GameFeedItemBase> pendingFeeds;
    private List<GameFeedItemBase> serverErrorFeeds;
    private final String tag = "GameFeedImpl";
    private byte[] configBody = null;
    private byte[] feedBody = null;

    private static final Map<String, Object> parseDefaultCustomization() {
        try {
            return (Map) JsonCoder.parse(defaultCustomizationJSON);
        } catch (Exception e) {
            return new HashMap();
        }
    }

    public GameFeedImpl(Context context, IGameFeedView gameFeedView, Map<String, Object> developerCustomSettings) {
        this.mGameFeedView = gameFeedView;
        this.mContext = context;
        this.mDeveloperCustomSettings = developerCustomSettings;
        CustomizedSetting.clear();
        CustomizedSetting.putAll(defaultCustomization);
        CustomizedSetting.put("dpi", Util.getDpiName(OpenFeintInternal.getInstance().getContext()));
        CustomizedSetting.put("server_url", OpenFeintInternal.getInstance().getServerUrl() + "/");
        CustomizedSetting.put("game_id", OpenFeintInternal.getInstance().getAppID());
        GameFeedHelper.setupFromContext(context);
        this.mHandler = new Handler();
        this.mUpdateTimeTask = new Runnable() { // from class: com.openfeint.gamefeed.internal.GameFeedImpl.1
            @Override // java.lang.Runnable
            public void run() {
                OFLog.m181d("GameFeedImpl", "Timer!Trying to test the network");
                boolean netWorkable = OpenFeintInternal.getInstance().isFeintServerReachable();
                if (netWorkable) {
                    OFLog.m181d("GameFeedImpl", "Timer!network available now");
                } else {
                    OFLog.m182e("GameFeedImpl", "Timer!network is still not available");
                    GameFeedImpl.this.mHandler.postDelayed(this, 15000L);
                }
            }
        };
        setViewProperty();
        this.leaf = new LeafFeedItem(this, itemHeight(), itemHeight());
        this.configureLoaded = false;
        GameFeedEventListener eventListener = new GameFeedEventListener(this);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.LOGIN_SUCESS, eventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.DASHBOARD_START, eventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.DASHBOARD_END, eventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.GAME_BACKGROUND, eventListener);
    }

    public void itemClicked(int position, View v) {
        GameFeedItemBase item = this.feedsPointer.get(position);
        if (item instanceof DummyItem) {
            OFLog.m184v("GameFeedImpl", "dummy, item shown");
            return;
        }
        item.invokeAction(v);
        if (!(item instanceof LeafFeedItem)) {
            IAnalyticsLogger logger = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("click");
            GameFeedItemBase feed = this.feedsPointer.get(position);
            feed.addAnalyticsParams(logger);
            AnalyticsManager.instance(this.mContext).makelog(logger, "GameFeedImpl");
        }
    }

    public void itemShown(int position) {
        if (position >= 0 && this.feedsPointer != null && position < this.feedsPointer.size()) {
            GameFeedItemBase feed = this.feedsPointer.get(position);
            if (feed != null && !(feed instanceof DummyItem) && !(feed instanceof LeafFeedItem)) {
                if (feed instanceof GameFeedItem) {
                    String type = feed.getItem_type();
                    if (networkErrorWarningItemType.equals(type) || serverErrorWarningItemType.equals(type) || loadingWarningItemType.equals(type)) {
                        return;
                    }
                }
                if (!feed.isItemShown()) {
                    IAnalyticsLogger logger = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("feed_item_show");
                    feed.addAnalyticsParams(logger);
                    AnalyticsManager.instance(this.mContext).makelog(logger, "GameFeedImpl");
                    feed.itemShown();
                    return;
                }
                return;
            }
            return;
        }
        OFLog.m182e("GameFeedImpl", "Wrong index, please check the index");
    }

    public void itemUnshown(int position) {
        this.feedsPointer.get(position).itemUnshown();
    }

    private void setViewProperty() {
        int d;
        boolean landscape = GameFeedHelper.isLandscape();
        Object alignmentObj = getSetting(GameFeedSettings.Alignment);
        boolean top = alignmentObj != null && (alignmentObj instanceof GameFeedSettings.AlignmentType) && ((GameFeedSettings.AlignmentType) alignmentObj) == GameFeedSettings.AlignmentType.TOP;
        Object bgImage = getSetting(landscape ? GameFeedSettings.FeedBackgroundImageLandscape : GameFeedSettings.FeedBackgroundImagePortrait);
        if (bgImage != null && (bgImage instanceof Drawable)) {
            Drawable bgDrawable = (Drawable) bgImage;
            Bitmap bmp = ((BitmapDrawable) bgDrawable).getBitmap();
            BitmapDrawable TileMe = new BitmapDrawable(bmp);
            TileMe.setTileModeX(Shader.TileMode.REPEAT);
            this.mGameFeedView.setBackgroundDrawable(TileMe);
            return;
        }
        if (landscape) {
            if (top) {
                d = C0207RR.drawable("ofgamefeedviewbackgroundtoplandscape");
            } else {
                d = C0207RR.drawable("ofgamefeedbackgroundbottomlandscape");
            }
        } else if (top) {
            d = C0207RR.drawable("ofgamefeedviewbackgroundtopportrait");
        } else {
            d = C0207RR.drawable("ofgamefeedbackgroundbottom");
        }
        if (d != 0) {
            Bitmap bmp2 = BitmapFactory.decodeResource(this.mContext.getResources(), d);
            BitmapDrawable TileMe2 = new BitmapDrawable(bmp2);
            TileMe2.setTileModeX(Shader.TileMode.REPEAT);
            this.mGameFeedView.setBackgroundDrawable(TileMe2);
        }
    }

    public GameFeedItemBase getItem(int position) {
        return this.feedsPointer.get(position);
    }

    public int numItems() {
        if (this.feedsPointer != null) {
            return this.feedsPointer.size();
        }
        return 0;
    }

    public int itemWidth() {
        return (int) ((GameFeedHelper.isLandscape() ? 304 : 229) * GameFeedHelper.getScalingFactor());
    }

    public int itemHeight() {
        return (int) ((GameFeedHelper.isLandscape() ? 54 : 68) * GameFeedHelper.getScalingFactor());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadFailure() {
        this.configBody = null;
        this.feedBody = null;
        showNetworkUnavailable();
    }

    private GameFeedItem loadingItem(StringInterpolator si) {
        GameFeedItem item = new GameFeedItem();
        List<Object> views = (List) JsonCoder.parse(GameFeedHelper.isLandscape() ? loadingLandscapeJSON : loadingPortraitJSON);
        buildViews(si, views, item);
        item.setItem_type(loadingWarningItemType);
        return item;
    }

    private GameFeedItem serverErrorItem(StringInterpolator si) {
        GameFeedItem item = new GameFeedItem();
        List<Object> views = (List) JsonCoder.parse(GameFeedHelper.isLandscape() ? errorLandscapeJSON : errorPortraitJSON);
        buildViews(si, views, item);
        item.setItem_type(serverErrorWarningItemType);
        return item;
    }

    private GameFeedItem netWorkUnavailableItem(StringInterpolator si) {
        GameFeedItem item = new GameFeedItem();
        List<Object> views = (List) JsonCoder.parse(GameFeedHelper.isLandscape() ? offlineLandscapeJSON : offlinePortraitJSON);
        buildViews(si, views, item);
        item.setItem_type(networkErrorWarningItemType);
        return item;
    }

    private class loadingAds implements Runnable {
        private OrderedArgList args;

        public loadingAds(OrderedArgList arg_) {
            this.args = arg_;
        }

        @Override // java.lang.Runnable
        public void run() {
            final boolean forceErr = GameFeedImpl.this.forceAdServerError();
            final int requiredAdsCount = GameFeedImpl.this.pendingAds.size();
            new JSONContentRequest(this.args) { // from class: com.openfeint.gamefeed.internal.GameFeedImpl.loadingAds.1
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
                    return forceErr ? super.baseServerURL() : OpenFeintInternal.getInstance().getAdServerUrl();
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String path() {
                    return forceErr ? "/testing/errors/immediate" : String.format("/ads/%d.json", Integer.valueOf(requiredAdsCount));
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public void onResponse(int responseCode, byte[] body) {
                    if (200 <= responseCode && responseCode < 300) {
                        try {
                            OFLog.m183i("GameFeedImpl", "Download ads success");
                            GameFeedHelper.tick("GameFeedImpl");
                            OFLog.m181d(TAG, "ad body is:\n" + new String(body));
                            Map<String, Object> root = (Map) JsonCoder.parse(body);
                            List<Map<String, Object>> adFeed = (List) root.get("ads");
                            List<GameFeedItemBase> actualAds = new ArrayList<>();
                            GameFeedImpl.this.genItemsFromFeed(adFeed, actualAds, new ArrayList());
                            int cramp = Math.min(actualAds.size(), GameFeedImpl.this.pendingAds.size());
                            for (int i = 0; i < cramp; i++) {
                                int replacementIndex = GameFeedImpl.this.pendingFeeds.indexOf(GameFeedImpl.this.pendingAds.get(i));
                                GameFeedImpl.this.pendingFeeds.set(replacementIndex, actualAds.get(i));
                                GameFeedItemBase item = (GameFeedItemBase) GameFeedImpl.this.pendingAds.get(i);
                                View v = item.GenerateFeed(GameFeedImpl.this.mContext);
                                OFLog.m181d("GameFeedImpl", "replace ad in:" + replacementIndex);
                                GameFeedImpl.this.mGameFeedView.getChildrenViews().set(replacementIndex, v);
                            }
                            IAnalyticsLogger logger = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("game_feed_ads_shown");
                            if (GameFeedHelper.getFeedBeginTime() != null) {
                                Date now = new Date();
                                Long duration = Long.valueOf(now.getTime() - GameFeedHelper.getFeedBeginTime().getTime());
                                float durationSecond = duration.longValue() / 1000.0f;
                                logger.makeEvent("duration", Float.valueOf(durationSecond));
                            } else {
                                OFLog.m182e("GameFeedImpl", "StartTime is null");
                            }
                            AnalyticsManager.instance(GameFeedImpl.this.mContext).makelog(logger, "GameFeedImpl");
                            GameFeedHelper.setGameFeedAdsFinishTime(new Date());
                            for (int i2 = cramp; i2 < GameFeedImpl.this.pendingAds.size(); i2++) {
                                int indexInFeed = GameFeedImpl.this.pendingFeeds.indexOf(GameFeedImpl.this.pendingAds.get(i2));
                                GameFeedImpl.this.pendingFeeds.remove(GameFeedImpl.this.pendingAds.get(i2));
                                OFLog.m181d("GameFeedImpl", "remove ad in:" + indexInFeed);
                                GameFeedImpl.this.mGameFeedView.getChildrenViews().remove(indexInFeed);
                            }
                        } catch (Exception e) {
                            OFLog.m182e(TAG, "Couldn't parse ad feed");
                        }
                    } else {
                        OFLog.m182e(TAG, String.format("Error response from ad server: code %d", Integer.valueOf(responseCode)));
                        IAnalyticsLogger event = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("game_feed_ads_load_failed");
                        AnalyticsManager.instance(GameFeedImpl.this.mContext).makelog(event, "GameFeedImpl");
                        GameFeedHelper.setGameFeedAdsFinishTime(null);
                        for (GameFeedItemBase pendingAd : GameFeedImpl.this.pendingAds) {
                            int indexInFeed2 = GameFeedImpl.this.pendingFeeds.indexOf(pendingAd);
                            GameFeedImpl.this.pendingFeeds.remove(pendingAd);
                            OFLog.m181d("GameFeedImpl", "remove ad in:" + indexInFeed2);
                            GameFeedImpl.this.mGameFeedView.getChildrenViews().remove(indexInFeed2);
                        }
                    }
                    GameFeedImpl.this.updateFeedItemsPosition();
                    GameFeedImpl.this.mGameFeedView.doDisplay();
                }
            }.launch();
        }
    }

    private void buildFeed() {
        Map<String, Object> feedBase = (Map) JsonCoder.parse(this.feedBody);
        List<Map<String, Object>> feed = (List) feedBase.get("game_feed");
        this.pendingFeeds.clear();
        this.pendingFeeds.add(this.leaf);
        this.pendingAds.clear();
        genItemsFromFeed(feed, this.pendingFeeds, this.pendingAds);
        if (!this.pendingAds.isEmpty()) {
            OFLog.m183i("GameFeedImpl", "have ads,downloading...");
            GameFeedHelper.tick("GameFeedImpl");
            OrderedArgList args = new OrderedArgList();
            args.put("game_id", OpenFeintInternal.getInstance().getAppID());
            args.put(TapjoyConstants.TJC_PLATFORM, TapjoyConstants.TJC_DEVICE_PLATFORM_TYPE);
            new Thread(new loadingAds(args)).start();
        } else {
            IAnalyticsLogger event = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("game_feed_no_ads");
            AnalyticsManager.instance(this.mContext).makelog(event, "GameFeedImpl");
            GameFeedHelper.setGameFeedAdsFinishTime(null);
            OFLog.m181d("GameFeedImpl", "no pending ads..");
            GameFeedHelper.tick("GameFeedImpl");
        }
        IAnalyticsLogger event2 = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("game_feed_items_shown");
        AnalyticsManager.instance(this.mContext).makelog(event2, "GameFeedImpl");
        display();
    }

    private void displayServerError() {
        this.feedsPointer = this.serverErrorFeeds;
        OFLog.m182e("GameFeedImpl", "Server Error");
        displayFeedItems();
    }

    private void displayBadInternet() {
        this.feedsPointer = this.netWorkErrorFeeds;
        OFLog.m182e("GameFeedImpl", "Internet Error");
        IAnalyticsLogger event = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("game_feed_offline");
        AnalyticsManager.instance(this.mContext).makelog(event, "GameFeedImpl");
        displayFeedItems();
    }

    private void displayMainLoading() {
        if (this.loadingFeeds == null) {
            this.loadingFeeds = new ArrayList();
            this.loadingFeeds.add(this.leaf);
            GameFeedItem loading = loadingItem(new StringInterpolator(CustomizedSetting.getMap(), new HashMap()));
            this.loadingFeeds.add(loading);
        }
        this.feedsPointer = this.loadingFeeds;
        OFLog.m181d("GameFeedImpl", "Main Loading item pop out ");
        IAnalyticsLogger event = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("game_feed_begin_loading");
        AnalyticsManager.instance(this.mContext).makelog(event, "GameFeedImpl");
        displayFeedItems();
    }

    private void display() {
        if (this.pendingFeeds.size() > 1) {
            OFLog.m183i("GameFeedImpl", "--------------Great! Main Loading Items will fades, Feeds will pop out!------------");
            GameFeedHelper.tick("GameFeedImpl");
            this.dummy = new DummyItem(((int) (GameFeedHelper.getBarWidth() / 2.0f)) - (itemWidth() / 2), itemHeight());
            this.feedsPointer = this.pendingFeeds;
            this.feedsPointer.add(this.dummy);
            displayFeedItems();
            return;
        }
        OFLog.m182e("GameFeedImpl", "no feeds here");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFeedItemsPosition() {
        OFLog.m181d("GameFeedImpl", "updateFeedItemsPosition..");
        int position = 0;
        for (GameFeedItemBase item : this.feedsPointer) {
            if (item instanceof DummyItem) {
                item.setPosition(-1);
            } else {
                item.setPosition(position);
                position++;
            }
        }
    }

    private void displayFeedItems() {
        updateFeedItemsPosition();
        this.mGameFeedView.doDisplay();
        this.mHandler.postDelayed(new Runnable() { // from class: com.openfeint.gamefeed.internal.GameFeedImpl.2
            @Override // java.lang.Runnable
            public void run() {
                GameFeedImpl.this.mGameFeedView.checkCompleteShown();
            }
        }, 3000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void genItemsFromFeed(List<Map<String, Object>> feed, List<GameFeedItemBase> outFeeds, List<GameFeedItemBase> outAds) {
        for (Map<String, Object> itemData : feed) {
            String itemType = (String) itemData.get("type");
            if ("ad".equals(itemType)) {
                GameFeedItem placeholder = loadingItem(new StringInterpolator(CustomizedSetting.getMap(), itemData));
                outFeeds.add(placeholder);
                outAds.add(placeholder);
            } else {
                try {
                    List<Map<String, Object>> alternatives = (List) this.itemConfigs.get(itemType);
                    Map<String, Object> config = alternatives.get((int) Math.floor(Math.random() * alternatives.size()));
                    StringInterpolator si = new StringInterpolator(CustomizedSetting.getMap(), (Map) config.get("configs"), itemData);
                    GameFeedItem item = new GameFeedItem();
                    List<Object> views = null;
                    if (GameFeedHelper.isLandscape()) {
                        views = (List) config.get("views_landscape");
                    }
                    if (views == null) {
                        views = (List) config.get("views");
                    }
                    buildViews(si, views, item);
                    String analytics_name = si.interpolate((String) config.get("analytics_name"));
                    item.setAnalytics_name(analytics_name);
                    String instance_key = si.interpolate((String) config.get("instance_key"));
                    item.setInstance_key(instance_key);
                    item.setImpressionPath(si.interpolate((String) config.get("impression_path")));
                    Object actionObject = config.get("action");
                    if (actionObject instanceof String) {
                        actionObject = JsonCoder.parse(si.interpolate((String) actionObject));
                    }
                    if (actionObject != null && (actionObject instanceof Map)) {
                        Map map = (Map) si.recursivelyInterpolate(actionObject);
                        item.setAction(map);
                    } else {
                        Object origAction = config.get("action");
                        OFLog.m182e("GameFeedImpl", "unable to determine action for: " + (origAction != null ? origAction.toString() : "null"));
                    }
                    item.setItem_type(itemType);
                    outFeeds.add(item);
                } catch (Exception e) {
                    OFLog.m182e("GameFeedImpl", "Couldn't build feed item: " + itemData.toString() + " because: " + e.getLocalizedMessage());
                }
            }
        }
    }

    private void buildViews(StringInterpolator si, List<Object> views, GameFeedItem item) {
        for (Object viewOrLayout : views) {
            if (viewOrLayout instanceof String) {
                String layoutName = (String) viewOrLayout;
                List<Object> layout = (List) this.layouts.get(layoutName);
                if (layout != null) {
                    buildViews(si, layout, item);
                }
            } else if (viewOrLayout instanceof Map) {
                buildElement(si, (Map) viewOrLayout, item);
            }
        }
    }

    private void buildElement(StringInterpolator si, Map<String, Object> view, GameFeedItem item) {
        GameFeedElement elem = null;
        String viewType = (String) view.get("type");
        if (viewType == null) {
            OFLog.m182e("GameFeedImpl", "null view type!");
        } else if ("label".equals(viewType)) {
            elem = new TextElement((List) view.get("frame"), view, si);
        } else if ("image".equals(viewType)) {
            List<Number> frame = (List) view.get("frame");
            Object imageSource = view.get("image");
            if (imageSource instanceof String) {
                imageSource = si.valueForKeyPath((String) imageSource);
            }
            if (imageSource instanceof Map) {
                Map<String, String> asMap = (Map) imageSource;
                if (asMap.containsKey("bundle")) {
                    String resourceIdentifier = si.interpolate(asMap.get("bundle"));
                    if (resourceIdentifier != null) {
                        elem = new ImageElement(frame, resourceIdentifier, ImageElement.ImageType.BUNDLE, view, si);
                    }
                } else if (asMap.containsKey("url")) {
                    String urlLocator = si.interpolate(asMap.get("url"));
                    if (urlLocator != null) {
                        elem = new ImageElement(frame, urlLocator, ImageElement.ImageType.REMOTE, view, si);
                    }
                } else if (asMap.containsKey("manifest")) {
                    String manifestLocator = si.interpolate(asMap.get("manifest"));
                    if (manifestLocator != null) {
                        try {
                            String fullPath = WebViewCache.getItemAbsolutePath(manifestLocator);
                            Bitmap pic = BitmapFactory.decodeFile(fullPath);
                            if (pic != null) {
                                elem = new ImageElement(frame, new BitmapDrawable(pic), view, si);
                            }
                        } catch (Exception e) {
                            elem = null;
                        }
                    }
                } else if (asMap.containsKey("loader")) {
                    elem = new ImageElement(frame, null, ImageElement.ImageType.LOADER, view, si);
                }
            } else if (imageSource instanceof Drawable) {
                elem = new ImageElement(frame, (Drawable) imageSource, view, si);
            }
        } else {
            OFLog.m182e("GameFeedImpl", "unknown view type '" + viewType + "'");
        }
        if (elem != null) {
            item.addGameBarElement(elem);
        }
    }

    private void overrideColor(String dictionaryKey, String customizationKey) {
        Object c = getSetting(dictionaryKey);
        if (c != null) {
            if ((c instanceof Integer) || (c instanceof String)) {
                CustomizedSetting.put(customizationKey, c);
            }
        }
    }

    private void overrideDrawable(String dictionaryKey, String customizationKey) {
        Object c = getSetting(dictionaryKey);
        if (c != null && (c instanceof Drawable)) {
            CustomizedSetting.put(customizationKey, c);
        }
    }

    private void overrideRscID(String dictionaryKey, String customizationKey) {
        Object c = getSetting(dictionaryKey);
        if (c != null && (c instanceof Integer)) {
            CustomizedSetting.put(customizationKey, c);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void processConfig() throws IOException {
        if (!this.configureLoaded) {
            Date configParseBeginTime = new Date();
            OFLog.m181d("GameFeedImpl", "configureLoaded is false,processConfig is called");
            Map<String, Object> config = (Map) JsonCoder.parse(this.configBody);
            this.layouts = (Map) config.get("game_feed_layouts");
            CustomizedSetting.putAll((Map) config.get("default_customization"));
            try {
                Number analytics_report_frequency = (Number) config.get("analytics_report_frequency");
                if (analytics_report_frequency != null && analytics_report_frequency.intValue() != 0) {
                    OFLog.m181d("GameFeedImpl", "get analytics_report_frequency" + analytics_report_frequency.intValue());
                    AnalyticsManager.setBatch_num_trigger(analytics_report_frequency.intValue());
                } else {
                    OFLog.m181d("GameFeedImpl", "not get analytics_report_frequency, use default value:" + AnalyticsManager.getBatch_num_trigger());
                }
            } catch (Exception e) {
                OFLog.m185w("GameFeedImpl", "exception in get analytics_report_frequency, use default value");
            }
            overrideColor(GameFeedSettings.UsernameColor, "username_color");
            overrideColor(GameFeedSettings.TitleColor, "title_color");
            overrideColor(GameFeedSettings.MessageTextColor, "text_color");
            overrideColor(GameFeedSettings.IconPositiveColor, "icon_color_positive");
            overrideColor(GameFeedSettings.IconNegativeColor, "icon_color_negative");
            overrideColor(GameFeedSettings.IconNeutralColor, "icon_color");
            overrideColor(GameFeedSettings.DisclosureColor, "disclosure_color");
            overrideColor(GameFeedSettings.CalloutTextColor, "call_out_color");
            overrideColor(GameFeedSettings.FrameColor, "frame_color");
            overrideColor(GameFeedSettings.HighlightedTextColor, "highlighted_color");
            overrideDrawable(GameFeedSettingsInternal.TabLeftImage, "tab_left_image");
            overrideDrawable(GameFeedSettingsInternal.TabRightImage, "tab_right_image");
            overrideDrawable(GameFeedSettings.CellBackgroundImageLandscape, "cell_background_image_landscape");
            overrideDrawable(GameFeedSettings.CellBackgroundImagePortrait, "cell_background_image_portrait");
            overrideDrawable(GameFeedSettings.CellHitImageLandscape, "cell_hit_image_landscape");
            overrideDrawable(GameFeedSettings.CellHitImagePortrait, "cell_hit_image_portrait");
            overrideDrawable(GameFeedSettings.CellDividerImageLandscape, "cell_divider_image_landscape");
            overrideDrawable(GameFeedSettings.CellDividerImagePortrait, "cell_divider_image_portrait");
            overrideDrawable(GameFeedSettings.ProfileFrameImage, "profile_frame_image");
            overrideDrawable(GameFeedSettings.SmallProfileFrameImage, "small_profile_frame_image");
            overrideRscID(GameFeedSettings.ImageLoadingProgressBar, "image_loading_progress");
            overrideRscID(GameFeedSettings.ImageLoadingBackground, "image_loading_background");
            this.itemConfigs = (Map) config.get("game_feed_config");
            for (String itemConfigKey : this.itemConfigs.keySet()) {
                List<Map<String, Object>> alternatives = (List) this.itemConfigs.get(itemConfigKey);
                String bestVersion = null;
                String ofVersion = OpenFeintInternal.getInstance().getOFVersion();
                Iterator i$ = alternatives.iterator();
                while (i$.hasNext()) {
                    String thisMCV = (String) i$.next().get("min_client_version");
                    if (thisMCV != null && Util.compareVersionStrings(thisMCV, ofVersion) <= 0 && (bestVersion == null || Util.compareVersionStrings(bestVersion, thisMCV) <= 0)) {
                        bestVersion = thisMCV;
                    }
                }
                List<Map<String, Object>> filteredAlternatives = new ArrayList<>();
                for (Map<String, Object> alternative : alternatives) {
                    String thisMCV2 = (String) alternative.get("min_client_version");
                    if ((bestVersion == null && thisMCV2 == null) || (bestVersion != null && Util.compareVersionStrings(bestVersion, thisMCV2) == 0)) {
                        filteredAlternatives.add(alternative);
                    }
                }
                this.itemConfigs.put(itemConfigKey, filteredAlternatives);
            }
            this.configureLoaded = true;
            Date configParseEndTime = new Date();
            OFLog.m181d("GameFeedImpl", String.format("config parse finished using %d ms", Long.valueOf(configParseEndTime.getTime() - configParseBeginTime.getTime())));
        } else {
            OFLog.m181d("GameFeedImpl", "configureLoaded is true,processConfig SKIPED");
        }
    }

    private void tryLoadRemoteJson() {
        loadFeedRemote();
        loadConfigRemote();
    }

    private void loadConfigRemote() {
        OFLog.m181d("GameFeedImpl", "loadConfigRemote");
        this.configBody = null;
        WebViewCache.trackPath("gamefeed/android/config.json", new WebViewCacheCallback() { // from class: com.openfeint.gamefeed.internal.GameFeedImpl.3
            @Override // com.openfeint.internal.p004ui.WebViewCacheCallback
            public void failLoaded() {
                OFLog.m182e("GameFeedImpl", "failed initManifestJson");
                GameFeedImpl.this.loadFailure();
            }

            /* JADX WARN: Type inference failed for: r2v4, types: [com.openfeint.gamefeed.internal.GameFeedImpl$3$1] */
            @Override // com.openfeint.internal.p004ui.WebViewCacheCallback
            public void pathLoaded(String path) {
                try {
                    InputStream configStream = new FileInputStream(new File(WebViewCache.getItemAbsolutePath(path)));
                    GameFeedImpl.this.configBody = Util.toByteArray(configStream);
                    configStream.close();
                    if (GameFeedImpl.this.configBody != null) {
                        OFLog.m183i("GameFeedImpl", "load config.json successfully");
                    }
                    new Thread() { // from class: com.openfeint.gamefeed.internal.GameFeedImpl.3.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            OFLog.m181d("GameFeedImpl", "parsing config process begin");
                            try {
                                GameFeedImpl.this.processConfig();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    GameFeedImpl.this.initFromJson();
                } catch (Exception e) {
                    e.printStackTrace();
                    failLoaded();
                }
            }
        });
    }

    private final boolean forceServerError() {
        return "true".equals(OpenFeintInternal.getInstance().getInternalProperties().get("force_game_feed_server_error"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean forceAdServerError() {
        return "true".equals(OpenFeintInternal.getInstance().getInternalProperties().get("force_ad_server_error"));
    }

    private void loadFeedRemote() {
        OFLog.m181d("GameFeedImpl", "loadFeedRemote");
        this.feedBody = null;
        boolean netWorkable = OpenFeintInternal.getInstance().isFeintServerReachable();
        if (netWorkable) {
            OFLog.m181d("GameFeedImpl", "network is good, begin to load feed.json");
            final String path = forceServerError() ? "/testing/errors/immediate" : "/xp/games/" + OpenFeintInternal.getInstance().getAppID() + "/feed.json";
            new BaseRequest() { // from class: com.openfeint.gamefeed.internal.GameFeedImpl.4
                @Override // com.openfeint.internal.request.BaseRequest
                public String method() {
                    return "GET";
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String path() {
                    return path;
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public void onResponse(int responseCode, byte[] body) {
                    if (200 <= responseCode && responseCode < 300) {
                        GameFeedImpl.this.feedBody = body;
                        OFLog.m183i("GameFeedImpl", "get feed json successfully");
                        OFLog.m181d(TAG, "Feed body is:\n" + new String(body));
                        GameFeedImpl.this.initFromJson();
                        return;
                    }
                    IAnalyticsLogger event = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("game_feed_items_load_failed");
                    AnalyticsManager.instance(GameFeedImpl.this.mContext).makelog(event, "GameFeedImpl");
                    OFLog.m185w("GameFeedImpl", "Failed to get feed.json, response is:" + responseCode);
                    GameFeedImpl.this.showServerError();
                }
            }.launch();
        } else {
            OFLog.m182e("GameFeedImpl", "network failed, failed to load feed.json");
            showNetworkUnavailable();
        }
    }

    private void loadLocalJson() {
        try {
            InputStream configStream = OpenFeintInternal.getInstance().getContext().getAssets().open("of_game_bar_config.json");
            this.configBody = Util.toByteArray(configStream);
            configStream.close();
            InputStream feedStream = OpenFeintInternal.getInstance().getContext().getAssets().open("of_game_bar_test_items.json");
            this.feedBody = Util.toByteArray(feedStream);
            feedStream.close();
            initFromJson();
        } catch (IOException e) {
            OFLog.m182e("GameFeedImpl", "Unable to read test config/feed");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initFromJson() {
        if (this.configBody == null) {
            OFLog.m181d("GameFeedImpl", "Still waiting for configBody.");
            return;
        }
        if (this.feedBody == null) {
            OFLog.m181d("GameFeedImpl", "Still waiting on feedBody.");
            return;
        }
        try {
            processConfig();
            buildFeed();
        } catch (Exception e) {
            OFLog.m182e("GameFeedImpl", "failed to initFromJson:" + e.getLocalizedMessage());
            loadFailure();
        }
    }

    public void reload() {
        if ("local".equals(OpenFeintInternal.getInstance().getInternalProperties().get("game-bar-mode"))) {
            loadLocalJson();
        } else {
            displayMainLoading();
            tryLoadRemoteJson();
        }
        this.mGameFeedView.resetView();
    }

    public void close() {
        IAnalyticsLogger event = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("game_feed_end");
        Date now = new Date();
        if (GameFeedHelper.getGameFeedAdsFinishTime() != null) {
            Long duration = Long.valueOf(now.getTime() - GameFeedHelper.getGameFeedAdsFinishTime().getTime());
            float durationSecond = duration.longValue() / 1000.0f;
            event.makeEvent("duration", Float.valueOf(durationSecond));
        } else {
            OFLog.m182e("GameFeedImpl", "StartTime is null");
        }
        AnalyticsManager.instance(this.mContext).makelog(event, "GameFeedImpl");
        uploadLog();
        removeTimer();
        this.mGameFeedView.resetView();
        OFLog.m183i("GameFeedImpl", "Gamefeed closed");
    }

    public void uploadLog() {
        AnalyticsManager.instance(this.mContext).upload();
    }

    public void start() {
        IAnalyticsLogger event = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("game_feed_begin");
        GameFeedHelper.setFeedBeginTime(new Date());
        AnalyticsManager.instance(this.mContext).makelog(event, "GameFeedImpl");
        popOutInNotTime();
        boolean netWorkable = OpenFeintInternal.getInstance().isFeintServerReachable();
        if (netWorkable) {
            tryShow();
        } else {
            showNetworkUnavailable();
        }
        ImageCacheMap.start();
    }

    public GameFeedSettings.AlignmentType getAlignment() {
        Object alignment = getSetting(GameFeedSettings.Alignment);
        return (alignment == null || !(alignment instanceof GameFeedSettings.AlignmentType)) ? GameFeedSettings.AlignmentType.BOTTOM : (GameFeedSettings.AlignmentType) alignment;
    }

    public boolean isAnimated() {
        Object animated = getSetting(GameFeedSettings.AnimateIn);
        return animated != null && (animated instanceof Boolean) && ((Boolean) animated).booleanValue();
    }

    private void showNetworkUnavailable() {
        addTimer();
        if (this.netWorkErrorFeeds == null) {
            this.netWorkErrorFeeds = new ArrayList();
            this.netWorkErrorFeeds.add(this.leaf);
            GameFeedItem warning = netWorkUnavailableItem(new StringInterpolator(CustomizedSetting.getMap(), new HashMap()));
            OFLog.m182e("GameFeedImpl", "showNetworkUnavailable waring sign's feed type:" + warning.getItem_type());
            this.netWorkErrorFeeds.add(warning);
        }
        displayBadInternet();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showServerError() {
        addTimer();
        if (this.serverErrorFeeds == null) {
            this.serverErrorFeeds = new ArrayList();
            this.serverErrorFeeds.add(this.leaf);
            GameFeedItem warning = serverErrorItem(new StringInterpolator(CustomizedSetting.getMap(), new HashMap()));
            OFLog.m182e("GameFeedImpl", "showServerError waring sign's feed type:" + warning.getItem_type());
            this.serverErrorFeeds.add(warning);
        }
        displayServerError();
    }

    private void popOutInNotTime() {
        addTimer();
        displayMainLoading();
    }

    private void tryShow() {
        removeTimer();
        IAnalyticsLogger event = GameFeedAnalyticsLogFactory.getGameFeedBaseLog("initialized");
        GameFeedSettings.AlignmentType alignment = getAlignment();
        switch (alignment) {
            case CUSTOM:
                event.makeEvent("placement", "CUSTOM");
                break;
            case BOTTOM:
                event.makeEvent("placement", "BOTTOM");
                break;
            case TOP:
                event.makeEvent("placement", "TOP");
                break;
        }
        event.makeEvent("animation", Boolean.valueOf(isAnimated()));
        String orientation = GameFeedHelper.isLandscape() ? "landscape" : "portrait";
        event.makeEvent("orientation", orientation);
        AnalyticsManager.instance(this.mContext).makelog(event, "GameFeedImpl");
        if (this.pendingFeeds == null) {
            this.pendingFeeds = new ArrayList();
        } else {
            this.pendingFeeds.clear();
        }
        if (this.pendingAds == null) {
            this.pendingAds = new ArrayList();
        } else {
            this.pendingAds.clear();
        }
        this.pendingFeeds.add(this.leaf);
        if ("local".equals(OpenFeintInternal.getInstance().getInternalProperties().get("game-bar-mode"))) {
            OFLog.m184v("GameFeedImpl", "using from local");
            loadLocalJson();
        } else {
            OFLog.m184v("GameFeedImpl", "using from remote");
            tryLoadRemoteJson();
        }
    }

    private void removeTimer() {
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
        OFLog.m181d("GameFeedImpl", "Timer are removed");
    }

    private void addTimer() {
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
        this.mHandler.postDelayed(this.mUpdateTimeTask, 15000L);
        OFLog.m181d("GameFeedImpl", "Timer started");
    }

    private Object getSetting(String key) {
        Object rv = null;
        if (this.mDeveloperCustomSettings != null) {
            rv = this.mDeveloperCustomSettings.get(key);
        }
        if (rv == null && GameFeedView.getDefaultSettings() != null) {
            return GameFeedView.getDefaultSettings().get(key);
        }
        return rv;
    }
}
