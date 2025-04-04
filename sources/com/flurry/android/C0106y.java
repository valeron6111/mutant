package com.flurry.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/* renamed from: com.flurry.android.y */
/* loaded from: classes.dex */
final class C0106y extends RelativeLayout {

    /* renamed from: a */
    private ViewOnClickListenerC0102u f254a;

    /* renamed from: b */
    private C0097p f255b;

    /* renamed from: c */
    private int f256c;

    public C0106y(Context context, ViewOnClickListenerC0102u viewOnClickListenerC0102u, C0097p c0097p, C0086e c0086e, int i, boolean z) {
        super(context);
        this.f254a = viewOnClickListenerC0102u;
        this.f255b = c0097p;
        C0103v c0103v = c0097p.f206b;
        this.f256c = i;
        switch (this.f256c) {
            case 2:
                if (z) {
                    m158a(context, c0086e, c0103v, false);
                } else {
                    m158a(context, c0086e, c0103v, true);
                }
            case 1:
                if (!z) {
                    m158a(context, c0086e, c0103v, true);
                    break;
                } else {
                    m158a(context, c0086e, c0103v, false);
                    break;
                }
        }
        setFocusable(true);
    }

    /* renamed from: a */
    private void m158a(Context context, C0086e c0086e, C0103v c0103v, boolean z) {
        Drawable bitmapDrawable;
        Bitmap bitmap;
        setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        C0084c c0084c = c0086e.f184d;
        ImageView imageView = new ImageView(context);
        imageView.setId(1);
        AdImage adImage = c0103v.f247h;
        if (adImage != null) {
            byte[] bArr = adImage.f7e;
            Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
            if (decodeByteArray == null) {
                C0078ah.m72a("FlurryAgent", "Ad with bad image: " + c0103v.f243d + ", data: " + bArr);
            }
            if (decodeByteArray != null) {
                Bitmap createBitmap = Bitmap.createBitmap(decodeByteArray.getWidth(), decodeByteArray.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                Paint paint = new Paint();
                Rect rect = new Rect(0, 0, decodeByteArray.getWidth(), decodeByteArray.getHeight());
                RectF rectF = new RectF(rect);
                float m97a = C0099r.m97a(context, 8);
                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(-16777216);
                canvas.drawRoundRect(rectF, m97a, m97a, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(decodeByteArray, rect, rect, paint);
                if (Integer.parseInt(Build.VERSION.SDK) > 4) {
                    BlurMaskFilter blurMaskFilter = new BlurMaskFilter(3.0f, BlurMaskFilter.Blur.OUTER);
                    Paint paint2 = new Paint();
                    paint2.setMaskFilter(blurMaskFilter);
                    bitmap = createBitmap.extractAlpha(paint2, new int[2]).copy(Bitmap.Config.ARGB_8888, true);
                    new Canvas(bitmap).drawBitmap(createBitmap, -r6[0], -r6[1], (Paint) null);
                } else {
                    bitmap = createBitmap;
                }
                imageView.setImageBitmap(bitmap);
                C0099r.m100a(context, imageView, C0099r.m97a(context, c0084c.f164m), C0099r.m97a(context, c0084c.f165n));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
        AdImage m118a = this.f254a.m118a(c0084c.f154c);
        if (m118a != null) {
            byte[] bArr2 = m118a.f7e;
            Bitmap decodeByteArray2 = BitmapFactory.decodeByteArray(bArr2, 0, bArr2.length);
            if (NinePatch.isNinePatchChunk(decodeByteArray2.getNinePatchChunk())) {
                bitmapDrawable = new NinePatchDrawable(decodeByteArray2, decodeByteArray2.getNinePatchChunk(), new Rect(0, 0, 0, 0), null);
            } else {
                bitmapDrawable = new BitmapDrawable(decodeByteArray2);
            }
            setBackgroundDrawable(bitmapDrawable);
        }
        TextView textView = new TextView(context);
        textView.setId(5);
        textView.setPadding(0, 0, 0, 0);
        TextView textView2 = new TextView(context);
        textView2.setId(3);
        textView2.setPadding(0, 0, 0, 0);
        if (z) {
            textView.setTextColor(c0084c.f157f);
            textView.setTextSize(c0084c.f156e);
            textView.setText(new String("• " + c0084c.f153b));
            textView.setTypeface(Typeface.create(c0084c.f155d, 0));
            textView2.setTextColor(c0084c.f160i);
            textView2.setTextSize(c0084c.f159h);
            textView2.setTypeface(Typeface.create(c0084c.f158g, 0));
            textView2.setText(c0103v.f243d);
        } else {
            textView.setId(3);
            textView.setText(c0103v.f243d);
            textView.setTextColor(c0084c.f160i);
            textView.setTextSize(c0084c.f159h);
            textView.setTypeface(Typeface.create(c0084c.f158g, 0));
            textView2.setId(4);
            textView2.setText(c0103v.f242c);
            textView2.setTextColor(c0084c.f163l);
            textView2.setTextSize(c0084c.f162k);
            textView2.setTypeface(Typeface.create(c0084c.f161j, 0));
        }
        setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        addView(new ImageView(context), new RelativeLayout.LayoutParams(-1, -2));
        int i = (c0084c.f168q - (c0084c.f166o << 1)) - c0084c.f164m;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(9);
        layoutParams.setMargins(c0084c.f166o, c0084c.f167p, i, 0);
        addView(imageView, layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.addRule(6, imageView.getId());
        layoutParams2.addRule(1, imageView.getId());
        layoutParams2.setMargins(0, 0, 0, 0);
        addView(textView, layoutParams2);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams3.addRule(1, imageView.getId());
        layoutParams3.addRule(3, textView.getId());
        layoutParams3.setMargins(0, -2, 0, 0);
        addView(textView2, layoutParams3);
    }

    /* renamed from: a */
    final C0097p m159a() {
        return this.f255b;
    }

    /* renamed from: a */
    final void m160a(C0097p c0097p) {
        this.f255b = c0097p;
    }
}
