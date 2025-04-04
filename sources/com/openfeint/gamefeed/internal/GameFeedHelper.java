package com.openfeint.gamefeed.internal;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.WindowManager;
import com.openfeint.api.p001ui.Dashboard;
import com.openfeint.internal.logcat.OFLog;
import java.util.Date;
import java.util.Map;

/* loaded from: classes.dex */
public class GameFeedHelper {
    private static final float MAX_SCALING_FACTOR = 1.5f;
    private static Date feedBeginTime = null;
    private static Date gameFeedAdsFinishTime = null;
    private static final String tag = "GameFeedHelper";
    private static final PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private static float factor = 1.0f;
    private static float windowWidth = 320.0f;
    private static boolean landscape = false;
    private static float barHeight = 76.0f;

    public static void tick(String tag2) {
        if (feedBeginTime != null) {
            OFLog.m181d(tag2, String.format("ticked, %f second from start", Float.valueOf((new Date().getTime() - feedBeginTime.getTime()) / 1000.0f)));
        }
    }

    public static String extension(String fullPath, String extensionSeparator) {
        int dot = fullPath.lastIndexOf(extensionSeparator);
        return fullPath.substring(dot + 1);
    }

    public static String filename(String fullPath, String extensionSeparator) {
        int dot = fullPath.lastIndexOf(extensionSeparator);
        return fullPath.substring(0, dot);
    }

    public static void showMapV(Map<String, Object> map, String tag2) {
        OFLog.m184v(tag2, "---key:value---------");
        for (String key : map.keySet()) {
            Object value = map.get(key);
            OFLog.m184v(tag2, key + ":" + value.toString());
        }
        OFLog.m184v(tag2, "---------");
    }

    public static void showMapD(Map<String, Object> map, String tag2) {
        if (map == null) {
            OFLog.m181d(tag2, "map is null");
            return;
        }
        OFLog.m181d(tag2, "---key:value---------");
        for (String key : map.keySet()) {
            Object value = map.get(key);
            OFLog.m181d(tag2, key + ":" + value.toString());
        }
        OFLog.m181d(tag2, "---------");
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx, int finalWidth, int finalHeight) {
        Bitmap output = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dstRect = new Rect(0, 0, finalWidth, finalHeight);
        RectF rectF = new RectF(dstRect);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(-16777216);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(porterDuffXfermode);
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        return output;
    }

    public static void OpenDashboadrFromGameFeed(String para) {
        Dashboard.setOpenfrom("gamefeed");
        if (para == null) {
            Dashboard.open();
        } else {
            Dashboard.openPath(para);
        }
    }

    public static int getColor(Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof String) {
            String colorString = (String) o;
            try {
                return Color.parseColor(colorString);
            } catch (Exception e) {
                OFLog.m182e(tag, colorString + " is not parseable as a color");
                return 0;
            }
        }
        if (o instanceof Integer) {
            return ((Integer) o).intValue();
        }
        OFLog.m182e(tag, "no idea on this color, dude");
        return 0;
    }

    public void initHelper(Configuration config) {
    }

    public static void setupFromContext(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        landscape = configuration.orientation == 2;
        WindowManager wm = (WindowManager) context.getSystemService("window");
        windowWidth = wm.getDefaultDisplay().getWidth();
        factor = windowWidth / (landscape ? 480.0f : 320.0f);
        if (factor > MAX_SCALING_FACTOR) {
            factor = MAX_SCALING_FACTOR;
        }
        barHeight = (landscape ? 62.0f : 76.0f) * factor;
    }

    public static float getScalingFactor() {
        return factor;
    }

    public static float getBarWidth() {
        return windowWidth;
    }

    public static float getBarHeight() {
        return barHeight;
    }

    public static boolean isLandscape() {
        return landscape;
    }

    public static Date getFeedBeginTime() {
        return feedBeginTime;
    }

    public static void setFeedBeginTime(Date feedBeginTime2) {
        feedBeginTime = feedBeginTime2;
    }

    public static void setGameFeedAdsFinishTime(Date gameFeedAdsFinishTime2) {
        gameFeedAdsFinishTime = gameFeedAdsFinishTime2;
    }

    public static Date getGameFeedAdsFinishTime() {
        return gameFeedAdsFinishTime;
    }
}
