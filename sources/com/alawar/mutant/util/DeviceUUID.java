package com.alawar.mutant.util;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.alawar.mutant.MutantStats;
import com.alawar.mutant.jni.MutantMessages;
import com.tapjoy.TapjoyConstants;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class DeviceUUID {
    private static final String PEPPER = "mutant";
    private static String ValidChars = "123456789abcdefghjkmnprstuvxyz";
    protected static String installationId;
    protected static String uuid;

    public static String getUuid() {
        return uuid;
    }

    public static String getInstallationId() {
        return installationId;
    }

    public static String getSecretKey() {
        return uuid + "_" + PEPPER;
    }

    public static String sign(String value) {
        return md5(value + "_" + getSecretKey());
    }

    public static void init(Context context) {
        uuid = Settings.Secure.getString(context.getContentResolver(), TapjoyConstants.TJC_ANDROID_ID);
        if (uuid == null || "9774d56d682e549c".equals(uuid)) {
            uuid = "ad" + ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
        } else {
            uuid = "aa" + uuid;
        }
        installationId = MutantStats.getString("installationId");
        if (installationId == null) {
            Random random = new Random(System.currentTimeMillis());
            installationId = MutantMessages.sEmpty;
            for (int i = 0; i < 10; i++) {
                installationId += ValidChars.charAt(random.nextInt(ValidChars.length()));
            }
            MutantStats.setString("installationId", installationId);
        }
    }

    public static String md5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String sha1(String s, String keyString) {
        try {
            SecretKeySpec key = new SecretKeySpec(keyString.getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);
            byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
            return Base64.encode(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
