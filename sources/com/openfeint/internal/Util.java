package com.openfeint.internal;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.openfeint.api.OpenFeintSettings;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.resource.ResourceClass;
import com.openfeint.internal.vendor.org.apache.commons.codec.binary.Base64;
import com.openfeint.internal.vendor.org.apache.commons.codec.binary.Hex;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonFactory;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class Util {
    private static final String TAG = "Util";
    public static final int VERSION = Integer.valueOf(Build.VERSION.SDK).intValue();

    public static boolean isPadVersion() {
        return VERSION >= 11;
    }

    public static int compareVersionStrings(String lhs, String rhs) {
        Pattern p = Pattern.compile("\\.");
        String[] lhsa = p.split(lhs);
        String[] rhsa = p.split(rhs);
        int lb = Math.min(lhsa.length, rhsa.length);
        for (int i = 0; i < lb; i++) {
            try {
                int leftAsInt = Integer.parseInt(lhsa[i]);
                try {
                    int rightAsInt = Integer.parseInt(rhsa[i]);
                    int delta = leftAsInt - rightAsInt;
                    if (delta != 0) {
                        return delta;
                    }
                } catch (NumberFormatException e) {
                    OFLog.m182e(TAG, "compareVersionStrings(\"" + lhs + "\",\"" + rhs + "\"): Bad version component " + lhsa[i]);
                    return 1;
                }
            } catch (NumberFormatException e2) {
                OFLog.m182e(TAG, "compareVersionStrings(\"" + lhs + "\",\"" + rhs + "\"): Bad version component " + lhsa[i]);
                return -1;
            }
        }
        return lhsa.length - rhsa.length;
    }

    public static void setOrientation(Activity act) {
        Integer orientation = (Integer) OpenFeintInternal.getInstance().getSettings().get(OpenFeintSettings.RequestedOrientation);
        if (orientation != null) {
            act.setRequestedOrientation(orientation.intValue());
        }
    }

    public static final byte[] toByteArray(InputStream is) throws IOException {
        byte[] readBuffer = new byte[4096];
        ByteArrayOutputStream accumulator = new ByteArrayOutputStream();
        while (true) {
            int count = is.read(readBuffer);
            if (count > 0) {
                accumulator.write(readBuffer, 0, count);
            } else {
                accumulator.close();
                return accumulator.toByteArray();
            }
        }
    }

    public static void deleteFiles(File path) {
        if (path.isDirectory()) {
            String[] files = path.list();
            for (String name : files) {
                File child = new File(path, name);
                deleteFiles(child);
            }
        }
        path.delete();
    }

    public static void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir();
            }
            String[] children = srcDir.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(srcDir, children[i]), new File(dstDir, children[i]));
            }
            return;
        }
        copyFile(srcDir, dstDir);
    }

    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        copyStream(in, out);
    }

    public static void copyStreamAndLeaveInputOpen(InputStream in, OutputStream out) throws IOException {
        byte[] copyBuffer = new byte[16384];
        while (true) {
            int len = in.read(copyBuffer);
            if (len > 0) {
                out.write(copyBuffer, 0, len);
            } else {
                out.close();
                return;
            }
        }
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        copyStreamAndLeaveInputOpen(in, out);
        in.close();
    }

    public static void saveFile(byte[] in, String path) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        out.write(in);
        out.close();
    }

    public static void saveStreamAndLeaveInputOpen(InputStream in, String path) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        copyStreamAndLeaveInputOpen(in, out);
    }

    public static void saveStream(InputStream in, String path) throws IOException {
        saveStreamAndLeaveInputOpen(in, path);
        in.close();
    }

    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) OpenFeintInternal.getInstance().getContext().getSystemService("window");
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static void run(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
            OFLog.m183i(TAG, cmd);
        } catch (Exception e) {
            OFLog.m182e(TAG, e.getMessage());
        }
    }

    public static void createSymbolic(String dst, String src) {
        run("ln -s " + dst + " " + src);
    }

    public static boolean isSymblic(File f) {
        try {
            return !f.getCanonicalPath().equals(f.getAbsolutePath());
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean sdcardReady(Context ctx) {
        if (!noSdcardPermission(ctx)) {
            return false;
        }
        String state = Environment.getExternalStorageState();
        return "mounted".equals(state);
    }

    public static void moveWebCache(Context ctx) {
        if (noSdcardPermission(ctx)) {
            File cache = new File(ctx.getCacheDir(), "webviewCache");
            if (!isSymblic(cache)) {
                String state = Environment.getExternalStorageState();
                if ("mounted".equals(state)) {
                    File sdcard = new File(Environment.getExternalStorageDirectory(), "openfeint/cache");
                    if (!sdcard.exists()) {
                        sdcard.mkdirs();
                    }
                    deleteFiles(cache);
                    createSymbolic(sdcard.getAbsolutePath(), cache.getAbsolutePath());
                }
            }
        }
    }

    public static boolean noPermission(String permission, Context ctx) {
        return -1 == ctx.getPackageManager().checkPermission(permission, ctx.getPackageName());
    }

    public static boolean noSdcardPermission() {
        return noSdcardPermission(OpenFeintInternal.getInstance().getContext());
    }

    public static boolean noSdcardPermission(Context ctx) {
        return noPermission("android.permission.WRITE_EXTERNAL_STORAGE", ctx);
    }

    public static byte[] readWholeFile(String path) throws IOException {
        File f = new File(path);
        int len = (int) f.length();
        InputStream in = new FileInputStream(f);
        byte[] b = new byte[len];
        in.read(b);
        in.close();
        return b;
    }

    public static Object getObjFromJsonFile(String path) {
        try {
            InputStream in = new FileInputStream(new File(path));
            return getObjFromJsonStream(in);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getObjFromJsonStream(InputStream in) throws JsonParseException, IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jp = jsonFactory.createJsonParser(in);
        JsonResourceParser jrp = new JsonResourceParser(jp);
        Object obj = jrp.parse();
        in.close();
        return obj;
    }

    public static Object getObjFromJson(byte[] json) {
        JsonFactory jsonFactory = new JsonFactory();
        try {
            JsonParser jp = jsonFactory.createJsonParser(json);
            JsonResourceParser jrp = new JsonResourceParser(jp);
            return jrp.parse();
        } catch (Exception e) {
            OFLog.m182e(TAG, e.getMessage());
            return null;
        }
    }

    public static Object getObjFromJson(byte[] json, ResourceClass resourceClass) {
        JsonFactory jsonFactory = new JsonFactory();
        OFLog.m181d(TAG, new String(json));
        try {
            JsonParser jp = jsonFactory.createJsonParser(json);
            JsonResourceParser jrp = new JsonResourceParser(jp);
            return jrp.parse(resourceClass);
        } catch (Exception e) {
            OFLog.m182e(TAG, e.getMessage() + "json error");
            return null;
        }
    }

    public static String getDpiName(Context ctx) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager winMan = (WindowManager) ctx.getSystemService("window");
        winMan.getDefaultDisplay().getMetrics(metrics);
        if (metrics.density >= 2.0f) {
            return "udpi";
        }
        if (metrics.density >= 1.5d) {
            return "hdpi";
        }
        return "mdpi";
    }

    public static String base64HMACSHA1(String rawKey, String rawString) {
        if (rawKey == null || rawString == null) {
            return null;
        }
        try {
            SecretKeySpec key = new SecretKeySpec(rawKey.getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);
            byte[] bytes = mac.doFinal(rawString.getBytes("UTF-8"));
            String result = new String(Base64.encodeBase64(bytes));
            return result;
        } catch (UnsupportedEncodingException e) {
            return null;
        } catch (InvalidKeyException e2) {
            return null;
        } catch (NoSuchAlgorithmException e3) {
            return null;
        }
    }

    public static String hexSHA1(String rawString) {
        if (rawString == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] bytes = md.digest(rawString.getBytes("UTF-8"));
            String result = new String(Hex.encodeHex(bytes));
            return result;
        } catch (UnsupportedEncodingException e) {
            return null;
        } catch (NoSuchAlgorithmException e2) {
            return null;
        }
    }
}
