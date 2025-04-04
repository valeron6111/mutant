package com.openfeint.internal;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.logcat.OFLog;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/* loaded from: classes.dex */
public class ImagePicker {
    public static final int IMAGE_PICKER_REQ_ID = 10009;
    protected static final String TAG = "ImagePicker";

    public static void show(Activity currentActivity) {
        ActivityManager am = (ActivityManager) currentActivity.getSystemService("activity");
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        Intent intent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        currentActivity.startActivityForResult(intent, IMAGE_PICKER_REQ_ID);
    }

    public static boolean isImagePickerActivityResult(int requestCode) {
        return requestCode == 10009;
    }

    public static Bitmap onImagePickerActivityResult(Activity currentActivity, int resultCode, int maxLength, Intent returnedIntent) {
        if (resultCode == -1) {
            Uri selectedImage = returnedIntent.getData();
            String[] columns = {"_data", "orientation"};
            Cursor cursor = currentActivity.getContentResolver().query(selectedImage, columns, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(columns[0]);
                String filePath = cursor.getString(columnIndex);
                int rotation = cursor.getInt(cursor.getColumnIndex(columns[1]));
                cursor.close();
                Bitmap image = resize(filePath, maxLength, rotation);
                OFLog.m182e(TAG, "image! " + image.getWidth() + "x" + image.getHeight());
                return image;
            }
            String msg = OpenFeintInternal.getRString(C0207RR.string("of_profile_picture_download_failed"));
            Toast.makeText(OpenFeintInternal.getInstance().getContext(), msg, 1).show();
        }
        return null;
    }

    public static void compressAndUpload(Bitmap image, String apiPath, OpenFeintInternal.IUploadDelegate delegate) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, out);
        upload(apiPath, out, delegate);
    }

    private static Bitmap resize(String filePath, int maxLength, int rotation) {
        Bitmap image = preScaleImage(filePath, maxLength);
        int width = image.getWidth();
        int height = image.getHeight();
        boolean tall = height > width;
        int _x = tall ? 0 : (width - height) / 2;
        int _y = tall ? (height - width) / 2 : 0;
        int _length = tall ? width : height;
        float scale = maxLength / _length;
        Matrix transform = new Matrix();
        transform.postScale(scale, scale);
        transform.postRotate(rotation);
        return Bitmap.createBitmap(image, _x, _y, _length, _length, transform, false);
    }

    private static Bitmap preScaleImage(String filePath, int maxLength) {
        File f = new File(filePath);
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            int minDim = Math.min(o.outWidth, o.outHeight);
            int scale = 1;
            while (minDim / 2 > maxLength) {
                minDim /= 2;
                scale++;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            OFLog.m182e(TAG, e.toString());
            return null;
        }
    }

    private static void upload(String apiPath, ByteArrayOutputStream stream, OpenFeintInternal.IUploadDelegate delegate) {
        OpenFeintInternal.getInstance().uploadFile(apiPath, "profile.png", stream.toByteArray(), "image/png", delegate);
    }
}
