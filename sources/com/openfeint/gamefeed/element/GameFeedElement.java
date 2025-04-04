package com.openfeint.gamefeed.element;

import android.content.Context;
import android.view.View;
import com.openfeint.gamefeed.internal.GameFeedHelper;
import com.openfeint.internal.logcat.OFLog;
import java.util.List;

/* loaded from: classes.dex */
public abstract class GameFeedElement {
    private static final String TAG = "GameBarElement";

    /* renamed from: h */
    public int f270h;

    /* renamed from: w */
    public int f271w;

    /* renamed from: x */
    public int f272x;

    /* renamed from: y */
    public int f273y;

    public enum type {
        IMAGE,
        TEXT
    }

    public abstract View getView(Context context);

    protected abstract void modify();

    public GameFeedElement(int x, int y, int w, int h) {
        float factor = GameFeedHelper.getScalingFactor();
        this.f271w = (int) (w * factor);
        this.f270h = (int) (h * factor);
        this.f272x = (int) (x * factor);
        this.f273y = (int) (y * factor);
    }

    public GameFeedElement(List<Number> frame) {
        try {
            float factor = GameFeedHelper.getScalingFactor();
            this.f272x = (int) (frame.get(0).intValue() * factor);
            this.f273y = (int) (frame.get(1).intValue() * factor);
            this.f271w = (int) (frame.get(2).intValue() * factor);
            this.f270h = (int) (frame.get(3).intValue() * factor);
        } catch (Exception e) {
            OFLog.m182e(TAG, "GameBarElement init failed");
        }
    }
}
