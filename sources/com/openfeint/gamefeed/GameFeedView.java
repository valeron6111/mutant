package com.openfeint.gamefeed;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.openfeint.gamefeed.GameFeedSettings;
import com.openfeint.gamefeed.element.image.ImageCacheMap;
import com.openfeint.gamefeed.internal.GameFeedHelper;
import com.openfeint.gamefeed.internal.GameFeedImpl;
import com.openfeint.gamefeed.internal.IGameFeedView;
import com.openfeint.gamefeed.item.GameFeedItem;
import com.openfeint.gamefeed.item.GameFeedItemBase;
import com.openfeint.gamefeed.item.LeafFeedItem;
import com.openfeint.internal.logcat.OFLog;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class GameFeedView extends HorizontalScrollView implements IGameFeedView {
    private static final String TAG = "GameFeedView";
    private static final int animation_duration_millis = 350;
    private static final int item_padding_unscaled = 3;
    private static final int move_trigger_pxs = 1;
    private static Map<String, Object> sDefaultSettings = null;
    private boolean addedToWindow;
    private List<View> childrenViews;
    private int currentIndex;
    private GameFeedImpl impl;
    private boolean lastVisibility;
    private int[] lefts;

    /* renamed from: ll */
    LinearLayout f269ll;
    Context mContext;
    private Handler mHandler;
    private int[] rights;
    private int windowVisibility;

    static /* synthetic */ int access$108(GameFeedView x0) {
        int i = x0.currentIndex;
        x0.currentIndex = i + 1;
        return i;
    }

    static /* synthetic */ int access$110(GameFeedView x0) {
        int i = x0.currentIndex;
        x0.currentIndex = i - 1;
        return i;
    }

    @Override // com.openfeint.gamefeed.internal.IGameFeedView
    public List<View> getChildrenViews() {
        return this.childrenViews;
    }

    @Override // com.openfeint.gamefeed.internal.IGameFeedView
    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public static void setDefaultSettings(Map<String, Object> settings) {
        sDefaultSettings = settings;
    }

    public static Map<String, Object> getDefaultSettings() {
        return sDefaultSettings;
    }

    public GameFeedView(Context context, Map<String, Object> customSettings) {
        super(context);
        this.mContext = null;
        this.impl = null;
        this.f269ll = null;
        this.addedToWindow = false;
        this.windowVisibility = 8;
        this.lastVisibility = false;
        sharedInit(context, customSettings);
    }

    public GameFeedView(Context context) {
        super(context);
        this.mContext = null;
        this.impl = null;
        this.f269ll = null;
        this.addedToWindow = false;
        this.windowVisibility = 8;
        this.lastVisibility = false;
        sharedInit(context, null);
    }

    public GameFeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = null;
        this.impl = null;
        this.f269ll = null;
        this.addedToWindow = false;
        this.windowVisibility = 8;
        this.lastVisibility = false;
        sharedInit(context, null);
    }

    public GameFeedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = null;
        this.impl = null;
        this.f269ll = null;
        this.addedToWindow = false;
        this.windowVisibility = 8;
        this.lastVisibility = false;
        sharedInit(context, null);
    }

    public void hide() {
        ImageCacheMap.stop();
        if (this.impl.isAnimated()) {
            Animation anim = this.impl.getAlignment() == GameFeedSettings.AlignmentType.BOTTOM ? makeTranslateAnim(0.0f, 1.0f) : makeTranslateAnim(0.0f, -1.0f);
            anim.setAnimationListener(new Animation.AnimationListener() { // from class: com.openfeint.gamefeed.GameFeedView.1
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    GameFeedView.this.setVisibility(8);
                }
            });
            startAnimation(anim);
            setVisibility(8);
            return;
        }
        setVisibility(8);
    }

    public void show() {
        resetView();
        setVisibility(0);
    }

    public void addToLayout(View layout) {
        GameFeedSettings.AlignmentType alignment = this.impl.getAlignment();
        if (layout == null) {
            OFLog.m182e(TAG, "GameFeedView#addToLayout() called with null layout");
        }
        if (layout instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) layout;
            if (linearLayout.getOrientation() != 1) {
                OFLog.m182e(TAG, "GameFeedView#addToLayout() only supports LinearLayout in vertical mode");
            }
            if (alignment == GameFeedSettings.AlignmentType.TOP) {
                linearLayout.addView(this, 0);
                return;
            } else {
                linearLayout.addView(this);
                return;
            }
        }
        if (layout instanceof FrameLayout) {
            FrameLayout frameLayout = (FrameLayout) layout;
            int gravity = alignment == GameFeedSettings.AlignmentType.TOP ? 48 : 80;
            frameLayout.addView(this, new FrameLayout.LayoutParams(-1, -2, gravity));
        } else {
            if (layout instanceof RelativeLayout) {
                RelativeLayout relativeLayout = (RelativeLayout) layout;
                RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(-1, -2);
                para.addRule(alignment == GameFeedSettings.AlignmentType.TOP ? 10 : 12);
                relativeLayout.addView(this, para);
                return;
            }
            if (layout instanceof ViewGroup) {
                OFLog.m182e(TAG, "GameFeedView#addToLayout() doesn't know about layout type " + layout.getClass().getCanonicalName() + ", using default behavior");
                ((ViewGroup) layout).addView(this);
            } else {
                OFLog.m182e(TAG, "GameFeedView#addToLayout() called with non-layout type " + layout.getClass().getCanonicalName());
            }
        }
    }

    private void sharedInit(Context context, Map<String, Object> customSettings) {
        setupStyling();
        this.currentIndex = 0;
        OFLog.m181d(TAG, "init in GameFeedViewHSV");
        this.mContext = context;
        this.mHandler = new Handler();
        this.impl = new GameFeedImpl(context, this, customSettings);
        this.f269ll = new LinearLayout(this.mContext);
        this.f269ll.setOrientation(0);
        addView(this.f269ll);
        setHorizontalScrollBarEnabled(false);
        ImageCacheMap.initalize();
        setOnTouchListener(new View.OnTouchListener() { // from class: com.openfeint.gamefeed.GameFeedView.2
            Number oldx = 0;
            int touchCount = 0;
            boolean recorded = false;

            private int getOffset(int index) {
                int sum = 0;
                for (int i = 0; i < index; i++) {
                    int childWidth = ((View) GameFeedView.this.childrenViews.get(i)).getWidth();
                    sum += childWidth;
                }
                return (int) (sum - Math.abs((GameFeedView.this.getWidth() * 0.5d) - (((View) GameFeedView.this.childrenViews.get(index)).getWidth() * 0.5d)));
            }

            private void trigger(int direction) {
                int len = GameFeedView.this.childrenViews.size();
                if (direction == -1) {
                    OFLog.m181d(GameFeedView.TAG, "trigger: swipe left");
                    if (GameFeedView.this.currentIndex < len - 1) {
                        if (GameFeedView.this.currentIndex == 0 && len > 2) {
                            GameFeedView.access$108(GameFeedView.this);
                        }
                        if (GameFeedView.this.currentIndex == len - 2 && GameFeedView.this.currentIndex > 0) {
                            GameFeedView.access$110(GameFeedView.this);
                        }
                        GameFeedView.access$108(GameFeedView.this);
                        OFLog.m181d(GameFeedView.TAG, "scroll to right!:" + GameFeedView.this.currentIndex);
                        GameFeedView.this.smoothScrollTo(getOffset(GameFeedView.this.currentIndex), 0);
                        return;
                    }
                    return;
                }
                if (direction == 1) {
                    OFLog.m181d(GameFeedView.TAG, "trigger: swipe right");
                    if (GameFeedView.this.currentIndex > 0) {
                        if (GameFeedView.this.currentIndex == 2) {
                            GameFeedView.access$110(GameFeedView.this);
                        }
                        GameFeedView.access$110(GameFeedView.this);
                        OFLog.m181d(GameFeedView.TAG, "scroll to left!:" + GameFeedView.this.currentIndex);
                        GameFeedView.this.smoothScrollTo(getOffset(GameFeedView.this.currentIndex), 0);
                        return;
                    }
                    return;
                }
                OFLog.m181d(GameFeedView.TAG, "trigger:auto adjust");
                int selfWidth = GameFeedView.this.getWidth();
                int currentCenter = GameFeedView.this.getScrollX() + (selfWidth / 2);
                int closestDistance = 10000;
                int closestIndex = -1;
                int closestOffset = -1;
                int scan = 0;
                for (int i = 0; i < len; i++) {
                    int childWidth = ((View) GameFeedView.this.childrenViews.get(i)).getWidth();
                    int currentDistance = Math.abs(((childWidth / 2) + scan) - currentCenter);
                    if (i > 0 && currentDistance < closestDistance) {
                        closestDistance = currentDistance;
                        closestIndex = i;
                        closestOffset = ((childWidth / 2) + scan) - (selfWidth / 2);
                    }
                    scan += childWidth;
                }
                if (closestOffset >= 0) {
                    GameFeedView.this.currentIndex = closestIndex;
                    GameFeedView.this.impl.itemShown(closestIndex);
                    OFLog.m181d(GameFeedView.TAG, "move to " + GameFeedView.this.currentIndex);
                    GameFeedView.this.smoothScrollTo(closestOffset, 0);
                }
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 0) {
                    OFLog.m181d(GameFeedView.TAG, "down" + event.getX());
                }
                if (event.getAction() == 2) {
                    this.touchCount++;
                    OFLog.m181d(GameFeedView.TAG, "move" + event.getX());
                    if (!this.recorded) {
                        OFLog.m181d(GameFeedView.TAG, "-------------------");
                        OFLog.m181d(GameFeedView.TAG, "record!" + event.getX());
                        this.oldx = Float.valueOf(event.getX());
                        this.recorded = true;
                    }
                }
                if (event.getAction() != 1 && event.getAction() != 3) {
                    return false;
                }
                OFLog.m181d(GameFeedView.TAG, "up");
                this.touchCount = 0;
                if (this.oldx != null) {
                    OFLog.m181d(GameFeedView.TAG, "compare:" + this.oldx + "," + event.getX());
                    float delta = Math.abs(this.oldx.floatValue() - event.getX());
                    if (delta <= 1.0f) {
                        trigger(0);
                    } else if (this.oldx.floatValue() < event.getX()) {
                        trigger(1);
                    } else {
                        trigger(-1);
                    }
                    GameFeedView.this.mHandler.postDelayed(new Runnable() { // from class: com.openfeint.gamefeed.GameFeedView.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            GameFeedView.this.checkCompleteShown();
                        }
                    }, 300L);
                    this.oldx = null;
                    this.recorded = false;
                }
                return true;
            }
        });
    }

    private void setupStyling() {
        try {
            Method mSetScrollbarFadingEnabled = getClass().getMethod("setScrollbarFadingEnabled", Boolean.TYPE);
            mSetScrollbarFadingEnabled.invoke(this, false);
        } catch (Exception e) {
        }
        try {
            Method mSetOverScrollMode = getClass().getMethod("setOverScrollMode", Integer.TYPE);
            Field fOverScrollNever = View.class.getField("OVER_SCROLL_NEVER");
            mSetOverScrollMode.invoke(this, Integer.valueOf(fOverScrollNever.getInt(null)));
        } catch (Exception e2) {
        }
    }

    @Override // com.openfeint.gamefeed.internal.IGameFeedView
    public void doDisplay() {
        OFLog.m181d(TAG, "doDisplay");
        this.f269ll.removeAllViews();
        int numItems = this.impl.numItems();
        int item_padding = (int) (3.0f * GameFeedHelper.getScalingFactor());
        int item_top_padding = (int) ((GameFeedHelper.getBarHeight() - this.impl.itemHeight()) / 2.0f);
        this.childrenViews = new ArrayList(10);
        this.lefts = new int[numItems];
        this.rights = new int[numItems];
        for (int i = 0; i < numItems; i++) {
            GameFeedItemBase item = this.impl.getItem(i);
            View v = item.GenerateFeed(this.mContext);
            this.childrenViews.add(v);
            int topPadding = 0;
            if ((item instanceof GameFeedItem) || (item instanceof LeafFeedItem)) {
                topPadding = item_top_padding;
            }
            v.setPadding(item_padding, topPadding, 0, 0);
            this.f269ll.addView(v);
            final int position = i;
            v.setOnClickListener(new View.OnClickListener() { // from class: com.openfeint.gamefeed.GameFeedView.3
                @Override // android.view.View.OnClickListener
                public void onClick(View v2) {
                    GameFeedView.this.impl.itemClicked(position, v2);
                }
            });
        }
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            GameFeedHelper.setupFromContext(this.mContext);
            checkCompleteShown();
        }
    }

    @Override // com.openfeint.gamefeed.internal.IGameFeedView
    public void checkCompleteShown() {
        OFLog.m181d(TAG, "checkVisibility:lastVisibility is:" + this.lastVisibility);
        if (this.childrenViews != null && this.lastVisibility) {
            int acc = 0;
            int len = this.childrenViews.size();
            for (int j = 0; j < len; j++) {
                this.lefts[j] = acc;
                acc += this.childrenViews.get(j).getWidth();
                this.rights[j] = acc;
            }
            for (int j2 = 0; j2 < len; j2++) {
                OFLog.m184v(TAG, String.format("itme %d [%d , %d]", Integer.valueOf(j2), Integer.valueOf(this.lefts[j2]), Integer.valueOf(this.rights[j2])));
            }
            int lowWatermark = getScrollX();
            int highWatermark = lowWatermark + getWidth();
            OFLog.m184v(TAG, "lowWaterMark:" + lowWatermark);
            OFLog.m184v(TAG, "hightWatermark:" + highWatermark);
            for (int i = 0; i < len; i++) {
                if (this.lefts[i] >= lowWatermark && this.rights[i] <= highWatermark) {
                    this.impl.itemShown(i);
                    OFLog.m184v(TAG, i + " showned");
                } else {
                    this.impl.itemUnshown(i);
                    OFLog.m184v(TAG, i + " unshowned");
                }
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        this.addedToWindow = true;
        super.onAttachedToWindow();
        this.windowVisibility = getWindowVisibility();
        visibilityChanged();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        this.addedToWindow = false;
        super.onDetachedFromWindow();
        visibilityChanged();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchWindowVisibilityChanged(int visibility) {
        super.dispatchWindowVisibilityChanged(visibility);
        this.windowVisibility = getWindowVisibility();
        visibilityChanged();
    }

    @Override // android.view.View, com.openfeint.gamefeed.internal.IGameFeedView
    public void setVisibility(int visibility) {
        OFLog.m181d(TAG, "setVisibility");
        super.setVisibility(visibility);
        visibilityChanged();
    }

    private void visibilityChanged() {
        OFLog.m181d(TAG, "visibilityChanged");
        boolean newVisibility = this.addedToWindow && this.windowVisibility == 0 && isShown();
        if (this.lastVisibility != newVisibility) {
            if (newVisibility) {
                OFLog.m181d(TAG, "Visibility changing to ON");
                this.impl.start();
                if (this.impl.isAnimated()) {
                    Animation anim = this.impl.getAlignment() == GameFeedSettings.AlignmentType.BOTTOM ? makeTranslateAnim(1.0f, 0.0f) : makeTranslateAnim(-1.0f, 0.0f);
                    startAnimation(anim);
                }
            } else if (!newVisibility) {
                OFLog.m181d(TAG, "Visibility changing to OFF");
                this.impl.close();
            }
            this.lastVisibility = newVisibility;
            checkCompleteShown();
        }
    }

    private Animation makeTranslateAnim(float yInitial, float yFinal) {
        Animation anim = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, yInitial, 1, yFinal);
        anim.setDuration(350L);
        return anim;
    }

    @Override // com.openfeint.gamefeed.internal.IGameFeedView
    public void resetView() {
        this.currentIndex = 0;
    }

    @Override // android.view.View, com.openfeint.gamefeed.internal.IGameFeedView
    public void invalidate() {
        super.invalidate();
    }

    @Override // android.view.View, com.openfeint.gamefeed.internal.IGameFeedView
    public void postInvalidate() {
        super.postInvalidate();
    }
}
