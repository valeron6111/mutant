package com.openfeint.gamefeed.element;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.openfeint.gamefeed.internal.FontHolder;
import com.openfeint.gamefeed.internal.GameFeedHelper;
import com.openfeint.gamefeed.internal.StringInterpolator;
import com.openfeint.internal.logcat.OFLog;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class TextElement extends GameFeedElement {
    private static final String tag = "TextElement";
    Map<String, Object> attribute;
    Context context;

    /* renamed from: si */
    StringInterpolator f274si;
    TextView view;

    public StringInterpolator getSi() {
        return this.f274si;
    }

    public void setSi(StringInterpolator si) {
        this.f274si = si;
    }

    public TextElement(List<Number> frame, Map<String, Object> attribute, StringInterpolator si) {
        super(frame);
        this.attribute = attribute;
        this.f274si = si;
    }

    @Override // com.openfeint.gamefeed.element.GameFeedElement
    public View getView(Context context) {
        this.view = new TextView(context);
        this.context = context;
        modify();
        return this.view;
    }

    @Override // com.openfeint.gamefeed.element.GameFeedElement
    protected void modify() {
        List<Long> shadow_offset;
        if (this.attribute != null) {
            int size = this.attribute.size();
            if (size != 0) {
                String txt = null;
                float scalingFactor = GameFeedHelper.getScalingFactor();
                this.view.setTextSize(0, 10.5f * scalingFactor);
                this.view.setTextColor(-10987432);
                for (String key : this.attribute.keySet()) {
                    if (key.equals("text")) {
                        Object txtObj = this.attribute.get(key);
                        if (txtObj instanceof String) {
                            txt = this.f274si.interpolateWithoutEscapingSquareBraces((String) txtObj);
                        }
                    } else if (key.equals("font_size")) {
                        Object fontObj = this.attribute.get(key);
                        if (fontObj instanceof Number) {
                            this.view.setTextSize(0, ((Number) fontObj).floatValue() * scalingFactor);
                        }
                    } else if (key.equals("font")) {
                        Object fontObj2 = this.attribute.get(key);
                        if (fontObj2 instanceof String) {
                            String fontStr = (String) fontObj2;
                            this.view.setTypeface(FontHolder.getInstance().getTypeface(fontStr));
                        }
                    } else if (key.equals("alignment")) {
                        String align = (String) this.attribute.get(key);
                        if (align.equals("right")) {
                            this.view.setGravity(5);
                        } else if (align.equals("left")) {
                            this.view.setGravity(3);
                        }
                    } else if (key.equals("color")) {
                        int c = GameFeedHelper.getColor(this.f274si.valueForKeyPath((String) this.attribute.get("color")));
                        if (c != 0) {
                            this.view.setTextColor(c);
                        }
                    } else if (key.equals("shadow_color")) {
                        String colorLookup = (String) this.attribute.get("shadow_color");
                        OFLog.m184v(tag, "before si color : " + colorLookup);
                        String actualColor = (String) this.f274si.valueForKeyPath(colorLookup);
                        OFLog.m184v(tag, "after si color : " + actualColor);
                        int c2 = 0;
                        try {
                            c2 = Color.parseColor(actualColor);
                        } catch (Exception e) {
                            OFLog.m182e(tag, actualColor + " is not color");
                        }
                        if (c2 != 0 && this.attribute.containsKey("shadow_offset") && (shadow_offset = (List) this.attribute.get("shadow_offset")) != null && shadow_offset.size() == 2) {
                            long x = shadow_offset.get(0).longValue();
                            long y = shadow_offset.get(1).longValue();
                            try {
                                this.view.setShadowLayer(0.5f, x, y, c2);
                            } catch (Exception e2) {
                                OFLog.m182e(tag, e2.getLocalizedMessage());
                            }
                        }
                    }
                }
                if (txt != null) {
                    this.view.setText(Html.fromHtml(txt));
                }
            }
        }
    }
}
