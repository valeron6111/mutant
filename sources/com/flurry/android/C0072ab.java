package com.flurry.android;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Html;
import android.text.SpannedString;
import android.widget.RelativeLayout;
import android.widget.TextView;

/* renamed from: com.flurry.android.ab */
/* loaded from: classes.dex */
final class C0072ab extends RelativeLayout {

    /* renamed from: a */
    private final SpannedString f91a;

    /* renamed from: b */
    private final SpannedString f92b;

    public C0072ab(CatalogActivity catalogActivity, Context context) {
        super(context);
        this.f91a = new SpannedString(Html.fromHtml("<html><div='style:font-size:7px'>&lt; Previous</div></html>"));
        this.f92b = new SpannedString(Html.fromHtml("<html><div='style:font-size:7px;color:#ffA500'>&lt; Previous</div></html>"));
        setBackgroundColor(-16777216);
        TextView textView = new TextView(context);
        textView.setTextColor(ColorStateList.valueOf(-1));
        textView.setId(10001);
        textView.setText(this.f91a);
        textView.setPadding(5, 2, 5, 2);
        textView.setFocusable(true);
        textView.setOnFocusChangeListener(new ViewOnFocusChangeListenerC0073ac(this, textView));
        textView.setOnClickListener(catalogActivity);
        textView.setEnabled(true);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.setMargins(0, 0, 0, 0);
        setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.setMargins(2, 0, 0, 0);
        layoutParams2.addRule(4);
        addView(textView, layoutParams2);
    }
}
