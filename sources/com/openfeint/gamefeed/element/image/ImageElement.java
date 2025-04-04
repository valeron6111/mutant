package com.openfeint.gamefeed.element.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import com.openfeint.gamefeed.element.GameFeedElement;
import com.openfeint.gamefeed.internal.StringInterpolator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class ImageElement extends GameFeedElement {
    static final String TAG = "ImageElement";
    private Map<String, Object> attribute;
    private String imageSrc;
    private LinearLayout imageWrapper;

    /* renamed from: si */
    private StringInterpolator f275si;
    private Drawable suppliedDrawable;
    private ImageType type;

    public enum ImageType {
        BUNDLE,
        REMOTE,
        DRAWABLE,
        LOADER,
        UNKNOWN
    }

    public ImageElement(int x, int y, int w, int h, String imageSrc, ImageType type, Map<String, Object> attribute, StringInterpolator si) {
        super(x, y, w, h);
        this.type = type;
        this.imageSrc = imageSrc;
        this.attribute = attribute;
        this.f275si = si;
    }

    public ImageElement(List<Number> frame, String imageSrc, ImageType type, Map<String, Object> attribute, StringInterpolator si) {
        super(frame);
        this.type = type;
        this.imageSrc = imageSrc;
        this.attribute = attribute;
        this.f275si = si;
    }

    public ImageElement(List<Number> frame, Drawable suppliedDrawable, Map<String, Object> attribute, StringInterpolator si) {
        super(frame);
        this.type = ImageType.DRAWABLE;
        this.suppliedDrawable = suppliedDrawable;
        this.attribute = attribute;
        this.f275si = si;
    }

    @Override // com.openfeint.gamefeed.element.GameFeedElement
    public View getView(Context context) {
        if (this.type == ImageType.REMOTE) {
            this.imageWrapper = new RemoteImage(context, this.imageSrc, this.attribute, this.f275si, this.f271w, this.f270h);
        } else {
            this.imageWrapper = new LocalImageView(context, this.imageSrc, this.suppliedDrawable, this.type, this.attribute, this.f275si, this.f271w, this.f270h);
        }
        return this.imageWrapper;
    }

    @Override // com.openfeint.gamefeed.element.GameFeedElement
    protected void modify() {
    }
}
