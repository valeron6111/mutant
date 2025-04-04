package com.openfeint.gamefeed.internal;

import android.graphics.drawable.Drawable;
import android.view.View;
import java.util.List;

/* loaded from: classes.dex */
public interface IGameFeedView {
    void checkCompleteShown();

    void doDisplay();

    List<View> getChildrenViews();

    int getCurrentIndex();

    void invalidate();

    void postInvalidate();

    void resetView();

    void setBackgroundDrawable(Drawable drawable);

    void setBackgroundResource(int i);

    void setVisibility(int i);
}
