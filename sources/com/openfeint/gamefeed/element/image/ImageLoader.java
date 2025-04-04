package com.openfeint.gamefeed.element.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.openfeint.internal.logcat.OFLog;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/* loaded from: classes.dex */
public class ImageLoader {
    public static final String BITMAP_EXTRA = "of_extra_bitmap";
    static final int FAILED = 1;
    public static final String IMAGE_URL_EXTRA = "of_extra_image_url";
    private static final int NUM_RETRIES = 2;
    private static final int RETRY_HANDLER_SLEEP_TIME = 500;
    static final int SUCCESS = 0;
    private static ImageLoader instance;
    private ThreadPoolExecutor executor;

    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    private ImageLoader() {
        if (this.executor == null) {
            this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        }
    }

    public void downLoadImage(String url, Handler handler) {
        Bitmap bitmap = ImageCacheMap.get(url);
        if (bitmap != null) {
            Message message = new Message();
            message.what = 0;
            Bundle data = new Bundle();
            data.putString(IMAGE_URL_EXTRA, url);
            data.putParcelable(BITMAP_EXTRA, bitmap);
            message.setData(data);
            handler.sendMessage(message);
            return;
        }
        this.executor.execute(new ImageLoaderRunnable(url, handler));
    }

    private class ImageLoaderRunnable implements Runnable {
        private static final String TAG = "ImageLoaderRunnable";
        private Handler handler;
        private String imageUrl;

        public ImageLoaderRunnable(String url, Handler handler) {
            this.handler = handler;
            this.imageUrl = url;
        }

        @Override // java.lang.Runnable
        public void run() {
            OFLog.m181d(TAG, "worker thread begin");
            Bitmap bitmap = ImageCacheMap.get(this.imageUrl);
            if (bitmap == null) {
                bitmap = downloadImage();
            }
            notifyImageLoaded(this.imageUrl, bitmap);
        }

        private void notifyImageLoaded(String url, Bitmap bitmap) {
            Message message = new Message();
            if (bitmap == null) {
                message.what = 1;
            } else {
                message.what = 0;
                Bundle data = new Bundle();
                data.putString(ImageLoader.IMAGE_URL_EXTRA, url);
                data.putParcelable(ImageLoader.BITMAP_EXTRA, bitmap);
                message.setData(data);
            }
            this.handler.sendMessage(message);
        }

        private Bitmap downloadImage() {
            Bitmap bitmap = null;
            for (int timesTried = 1; timesTried <= 2; timesTried++) {
                try {
                    byte[] imageData = retrieveImageData();
                    if (imageData == null) {
                        break;
                    }
                    bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    ImageCacheMap.put(this.imageUrl, bitmap);
                    return bitmap;
                } catch (Exception e) {
                    OFLog.m185w(TAG, "download for " + this.imageUrl + " failed (attempt " + timesTried + ")");
                    e.printStackTrace();
                    SystemClock.sleep(500L);
                }
            }
            return bitmap;
        }

        private byte[] retrieveImageData() throws IOException {
            URL url = new URL(this.imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int fileSize = connection.getContentLength();
            if (fileSize < 0) {
                return null;
            }
            byte[] imageData = new byte[fileSize];
            OFLog.m181d(TAG, "fetching image " + this.imageUrl + " size: (" + fileSize + ")");
            BufferedInputStream istream = new BufferedInputStream(connection.getInputStream());
            int bytesRead = 0;
            int offset = 0;
            while (bytesRead != -1 && offset < fileSize) {
                bytesRead = istream.read(imageData, offset, fileSize - offset);
                offset += bytesRead;
            }
            OFLog.m181d(TAG, "fetching image " + this.imageUrl + " size: (" + fileSize + ") finished! ");
            istream.close();
            connection.disconnect();
            return imageData;
        }
    }
}
