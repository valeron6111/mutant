package com.openfeint.gamefeed.element.image;

import android.graphics.Bitmap;
import com.openfeint.internal.logcat.OFLog;
import com.tapjoy.TapjoyConstants;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class ImageCacheMap {
    private static long DEFAULT_TIMETOLIVE = TapjoyConstants.THROTTLE_GET_TAP_POINTS_INTERVAL;
    private static long DEFAULT_TRIGGLEINTERVAL = 20000;
    private static final String TAG = "ImageCacheMap";
    private static ConcurrentHashMap<String, Bitmap> sBitmapMap;
    private static ConcurrentHashMap<String, Long> sLastUpdateMap;
    private static long sTimeToLive;
    private static long sTriggleInterval;
    private static UpdateWorkerRunnable updateRunnable;
    private static Thread worker;

    public static void initalize() {
        initialize(DEFAULT_TIMETOLIVE, DEFAULT_TRIGGLEINTERVAL);
    }

    public static synchronized void start() {
        synchronized (ImageCacheMap.class) {
            if (!updateRunnable.running) {
                OFLog.m183i(TAG, "start");
                worker = new Thread(updateRunnable);
                worker.start();
            }
        }
    }

    public static synchronized void stop() {
        synchronized (ImageCacheMap.class) {
            OFLog.m183i(TAG, "stop");
            updateRunnable.running = false;
        }
    }

    private static class UpdateWorkerRunnable implements Runnable {
        public volatile boolean running;

        private UpdateWorkerRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            this.running = true;
            while (this.running) {
                ImageCacheMap.timeToDie();
                try {
                    OFLog.m181d(ImageCacheMap.TAG, "UpdateWorkerRunnable worker sleep now");
                    Thread.sleep(ImageCacheMap.sTriggleInterval);
                } catch (InterruptedException e) {
                    OFLog.m182e(ImageCacheMap.TAG, "UpdateWorkerRunnable sleep failed");
                }
            }
            ImageCacheMap.updateRunnable.running = false;
        }
    }

    public static void initialize(long timeToLive, long triggleInterval) {
        sTimeToLive = timeToLive;
        sTriggleInterval = triggleInterval;
        if (sLastUpdateMap == null) {
            sLastUpdateMap = new ConcurrentHashMap<>(10);
        }
        if (sBitmapMap == null) {
            sBitmapMap = new ConcurrentHashMap<>(10);
        }
        if (updateRunnable == null) {
            updateRunnable = new UpdateWorkerRunnable();
        }
        OFLog.m183i(TAG, "initialization finish");
    }

    public static void put(String url, Bitmap bitmap) {
        sLastUpdateMap.put(url, Long.valueOf(System.currentTimeMillis()));
        sBitmapMap.put(url, bitmap);
    }

    public static void timeToDie() {
        Date now = new Date();
        OFLog.m181d(TAG, "timeToDie start at " + now.toLocaleString());
        Set<String> urls = sLastUpdateMap.keySet();
        for (String url : urls) {
            Long temp = sLastUpdateMap.get(url);
            if (temp == null) {
                sLastUpdateMap.remove(url);
                sBitmapMap.remove(url);
                OFLog.m181d(TAG, "timeToDie remove " + url);
            } else {
                long interveal = now.getTime() - temp.longValue();
                if (interveal >= sTimeToLive) {
                    sLastUpdateMap.remove(url);
                    sBitmapMap.remove(url);
                    OFLog.m181d(TAG, "timeToDie remove " + url);
                }
            }
        }
    }

    public static Bitmap get(String url) {
        sLastUpdateMap.put(url, Long.valueOf(System.currentTimeMillis()));
        Bitmap temp = sBitmapMap.get(url);
        if (temp != null) {
            OFLog.m181d(TAG, "hit! " + url);
        }
        return temp;
    }
}
