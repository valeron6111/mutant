package com.openfeint.gamefeed.element.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.alawar.mutant.database.DbBuilder;
import com.openfeint.gamefeed.internal.CustomizedSetting;
import com.openfeint.gamefeed.internal.GameFeedHelper;
import com.openfeint.gamefeed.internal.StringInterpolator;
import com.openfeint.internal.logcat.OFLog;
import java.util.Map;

/* loaded from: classes.dex */
public class RemoteImage extends LinearLayout {
    private static final String tag = "RemoteImage";
    private Map<String, Object> attribute;

    /* renamed from: h */
    private int f279h;
    private Handler imageLoadedHandler;
    private String imageUrl;
    private Bitmap mBitmap;
    private Context mContext;
    private ImageView mImageView;
    private ProgressBar mSpinner;

    /* renamed from: si */
    private StringInterpolator f280si;

    /* renamed from: w */
    private int f281w;

    private class LoadedHandler extends Handler {
        private LoadedHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DbBuilder.ID_COLUMN /* 0 */:
                    Bundle data = msg.getData();
                    Bitmap bitmap = (Bitmap) data.getParcelable(ImageLoader.BITMAP_EXTRA);
                    RemoteImage.this.mImageView.setImageBitmap(bitmap);
                    RemoteImage.this.mBitmap = bitmap;
                    RemoteImage.this.modifyImage();
                    RemoteImage.this.mImageView.setVisibility(0);
                    RemoteImage.this.mSpinner.setVisibility(8);
                    break;
                case 1:
                    Object[] objArr = new Object[1];
                    objArr[0] = RemoteImage.this.imageUrl == null ? "null" : RemoteImage.this.imageUrl;
                    OFLog.m185w(RemoteImage.tag, String.format("Failed download remote picture:%s, use blank", objArr));
                    RemoteImage.this.mImageView.setVisibility(8);
                    RemoteImage.this.mSpinner.setVisibility(8);
                    break;
            }
        }
    }

    public RemoteImage(Context context, String imageUrl, Map<String, Object> attribute, StringInterpolator si, int w, int h) {
        super(context);
        Drawable background;
        Drawable progress;
        this.attribute = attribute;
        this.f280si = si;
        this.imageUrl = imageUrl;
        this.f281w = w == 0 ? 1 : w;
        this.f279h = h == 0 ? 1 : h;
        this.imageLoadedHandler = new LoadedHandler();
        this.mContext = context;
        this.mImageView = new ImageView(this.mContext);
        this.mImageView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        this.mSpinner = new ProgressBar(this.mContext);
        this.mSpinner.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        this.mSpinner.setIndeterminate(true);
        setGravity(17);
        if (CustomizedSetting.get("image_loading_progress") != null && (CustomizedSetting.get("image_loading_progress") instanceof Integer) && (progress = this.mContext.getResources().getDrawable(((Integer) CustomizedSetting.get("image_loading_progress")).intValue())) != null) {
            this.mSpinner.setIndeterminateDrawable(progress);
        }
        if (CustomizedSetting.get("image_loading_background") != null && (CustomizedSetting.get("image_loading_background") instanceof Integer) && (background = this.mContext.getResources().getDrawable(((Integer) CustomizedSetting.get("image_loading_background")).intValue())) != null) {
            this.mSpinner.setBackgroundDrawable(background);
        }
        addView(this.mSpinner);
        addView(this.mImageView);
        this.mSpinner.setVisibility(0);
        this.mImageView.setVisibility(8);
        ImageLoader.getInstance().downLoadImage(imageUrl, this.imageLoadedHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void modifyImage() {
        Boolean sharp_corners;
        if (this.attribute != null) {
            int size = this.attribute.size();
            if (size != 0) {
                for (String key : this.attribute.keySet()) {
                    if (key.equals("color")) {
                        String colorLookup = (String) this.attribute.get("color");
                        Object actualColor = this.f280si.valueForKeyPath(colorLookup);
                        int c = GameFeedHelper.getColor(actualColor);
                        if (c != 0) {
                            PorterDuffColorFilter filter = new PorterDuffColorFilter(c, PorterDuff.Mode.MULTIPLY);
                            this.mImageView.setColorFilter(filter);
                        }
                    } else if (key.equals("scale_to_fill")) {
                        Boolean scale_to_fill = (Boolean) this.attribute.get("scale_to_fill");
                        if (scale_to_fill != null && scale_to_fill.booleanValue()) {
                            OFLog.m184v(tag, "scale_to_fill:" + this.imageUrl);
                            this.mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                    } else if (key.equals("sharp_corners") && (sharp_corners = (Boolean) this.attribute.get("sharp_corners")) != null && !sharp_corners.booleanValue()) {
                        Number corner_radius = (Number) this.attribute.get("corner_radius");
                        float corner_radius_unboxed = corner_radius != null ? corner_radius.floatValue() : 5.0f;
                        if (this.mBitmap != null) {
                            Bitmap newBitMap = GameFeedHelper.getRoundedCornerBitmap(this.mBitmap, corner_radius_unboxed, this.f281w, this.f279h);
                            this.mImageView.setImageBitmap(newBitMap);
                        }
                    }
                }
            }
        }
    }
}
