package com.openfeint.gamefeed.element.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.openfeint.gamefeed.element.image.ImageElement;
import com.openfeint.gamefeed.internal.CustomizedSetting;
import com.openfeint.gamefeed.internal.GameFeedHelper;
import com.openfeint.gamefeed.internal.StringInterpolator;
import com.openfeint.internal.logcat.OFLog;
import java.util.Map;

/* loaded from: classes.dex */
public class LocalImageView extends LinearLayout {
    private static final int HIT_STATE_TIME = 300;
    private static final String tag = "LocalImageView";
    private Map<String, Object> attribute;

    /* renamed from: h */
    private int f276h;
    private String imageUrl;
    private boolean isHitPicture;
    private Context mContext;
    private Drawable mDrawable;
    private Handler mHandler;
    private ImageView mImage;
    private ProgressBar mSpinner;

    /* renamed from: si */
    private StringInterpolator f277si;
    private ImageElement.ImageType type;

    /* renamed from: w */
    private int f278w;

    public LocalImageView(Context context, String imageUrl, Drawable suppliedDrawable, ImageElement.ImageType type, Map<String, Object> attribute, StringInterpolator si, int w, int h) {
        super(context);
        Drawable background;
        Drawable progress;
        this.type = type;
        this.attribute = attribute;
        this.f277si = si;
        this.imageUrl = imageUrl;
        this.mDrawable = suppliedDrawable;
        this.f278w = w == 0 ? 1 : w;
        this.f276h = h == 0 ? 1 : h;
        this.isHitPicture = false;
        this.mHandler = new Handler();
        this.mContext = context;
        this.mImage = new ImageView(this.mContext);
        this.mImage.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        if (type == ImageElement.ImageType.LOADER) {
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
        }
        addView(this.mImage);
        display();
    }

    private void display() {
        if (this.type == ImageElement.ImageType.DRAWABLE) {
            this.mImage.setImageDrawable(this.mDrawable);
            if (this.mDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) this.mDrawable).start();
            }
            modified();
            if (!this.isHitPicture) {
                this.mImage.setVisibility(0);
                return;
            } else {
                this.mImage.setVisibility(4);
                return;
            }
        }
        if (this.imageUrl == null) {
            if (this.type != ImageElement.ImageType.LOADER) {
                OFLog.m182e(tag, "no imageURL for picture");
                return;
            } else {
                OFLog.m184v(tag, "loader,no need to load picture, here");
                return;
            }
        }
        if (this.type == ImageElement.ImageType.BUNDLE) {
            this.mDrawable = null;
            String justName = GameFeedHelper.filename(this.imageUrl, ".").toLowerCase();
            String packageName = this.mContext.getPackageName();
            int resID = getResources().getIdentifier(justName, "drawable", packageName);
            if (resID == 0) {
                OFLog.m182e(tag, "Load Local image failed on:" + justName);
            } else {
                OFLog.m184v(tag, "Load Local image success on:" + justName);
            }
            this.mImage.setImageResource(resID);
            this.mDrawable = this.mImage.getDrawable();
            modified();
            if (!this.isHitPicture) {
                this.mImage.setVisibility(0);
                return;
            } else {
                this.mImage.setVisibility(4);
                return;
            }
        }
        OFLog.m182e(tag, "unkonw image type");
    }

    private void modified() {
        if (this.mDrawable == null || this.mImage == null) {
            OFLog.m182e(tag, "drawable or image view is null, abort modified");
            return;
        }
        if (this.attribute != null) {
            int size = this.attribute.size();
            if (size != 0) {
                for (String key : this.attribute.keySet()) {
                    if (key.equals("color")) {
                        String colorLookup = (String) this.attribute.get("color");
                        Object actualColor = this.f277si.valueForKeyPath(colorLookup);
                        int c = GameFeedHelper.getColor(actualColor);
                        if (c != 0) {
                            PorterDuffColorFilter filter = new PorterDuffColorFilter(c, PorterDuff.Mode.MULTIPLY);
                            this.mImage.setColorFilter(filter);
                        }
                    } else if (key.equals("scale_to_fill")) {
                        Boolean scale_to_fill = (Boolean) this.attribute.get("scale_to_fill");
                        if (scale_to_fill != null && scale_to_fill.booleanValue()) {
                            OFLog.m184v(tag, "scale_to_fill:" + this.imageUrl);
                            this.mImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                    } else if (key.equals("sharp_corners")) {
                        Boolean sharp_corners = (Boolean) this.attribute.get("sharp_corners");
                        if (sharp_corners != null && !sharp_corners.booleanValue()) {
                            Number corner_radius = (Number) this.attribute.get("corner_radius");
                            float corner_radius_unboxed = corner_radius != null ? corner_radius.floatValue() : 5.0f;
                            if (this.mDrawable == null) {
                                OFLog.m182e(tag, "null drawable in sharp_corners");
                            } else if (this.mDrawable instanceof BitmapDrawable) {
                                Bitmap bitmap = ((BitmapDrawable) this.mDrawable).getBitmap();
                                Bitmap newBitMap = GameFeedHelper.getRoundedCornerBitmap(bitmap, corner_radius_unboxed, this.f278w, this.f276h);
                                this.mDrawable = new BitmapDrawable(newBitMap);
                                this.mImage.setImageDrawable(this.mDrawable);
                            }
                        }
                    } else if (key.equals("hit_state")) {
                        Boolean hit_state = (Boolean) this.attribute.get("hit_state");
                        OFLog.m184v(tag, "hit_state:" + String.valueOf(hit_state) + ":" + this.imageUrl);
                        this.isHitPicture = hit_state.booleanValue();
                    }
                }
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isHitPicture && event.getAction() == 0) {
            this.mImage.setVisibility(0);
            this.mImage.bringToFront();
            this.mHandler.postDelayed(new recoverRunable(this.mImage), 300L);
        }
        return super.onTouchEvent(event);
    }

    private class recoverRunable implements Runnable {
        ImageView imageView;

        public recoverRunable(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override // java.lang.Runnable
        public void run() {
            OFLog.m184v(LocalImageView.tag, "hit timer: recover!");
            this.imageView.setVisibility(4);
        }
    }
}
