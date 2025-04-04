package com.openfeint.internal.p004ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class PadLogoImageView extends ImageView {
    private final String tag;

    public PadLogoImageView(Context context) {
        super(context);
        this.tag = "ImageView2";
    }

    public PadLogoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.tag = "ImageView2";
    }

    public PadLogoImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.tag = "ImageView2";
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e("ImageView2", String.valueOf(changed));
        Log.e("ImageView2", String.valueOf(top));
        if (changed) {
            if (top < 350) {
                setVisibility(4);
            } else {
                setVisibility(0);
            }
        }
    }
}
