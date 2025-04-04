package com.flurry.android;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;

/* renamed from: com.flurry.android.s */
/* loaded from: classes.dex */
final class C0100s extends LinearLayout {
    public C0100s(CatalogActivity catalogActivity, Context context) {
        super(context);
        ViewOnClickListenerC0102u viewOnClickListenerC0102u;
        setBackgroundColor(-1);
        viewOnClickListenerC0102u = catalogActivity.f15e;
        AdImage m148l = viewOnClickListenerC0102u.m148l();
        if (m148l != null) {
            ImageView imageView = new ImageView(context);
            imageView.setId(10000);
            byte[] bArr = m148l.f7e;
            if (bArr != null) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bArr, 0, bArr.length));
            }
            C0099r.m100a(context, imageView, C0099r.m97a(context, m148l.f4b), C0099r.m97a(context, m148l.f5c));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            layoutParams.setMargins(0, 0, 0, -3);
            setGravity(3);
            addView(imageView, layoutParams);
        }
    }
}
