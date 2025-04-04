package com.sponsorpay.sdk.android;

import android.util.Log;
import com.alawar.mutant.jni.MutantMessages;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/* loaded from: classes.dex */
public class SignatureTools {
    public static final String NO_SHA1_RESULT = "nosha1";
    private static final String SHA1_ALGORITHM = "SHA1";

    public static String generateSignatureForParameters(Map<String, String> parameters, String secretToken) {
        TreeSet<String> orderedKeys = new TreeSet<>();
        orderedKeys.addAll(parameters.keySet());
        Iterator<String> orderedKeysIterator = orderedKeys.iterator();
        String concatenatedOrderedParams = MutantMessages.sEmpty;
        while (orderedKeysIterator.hasNext()) {
            String key = orderedKeysIterator.next();
            String value = parameters.get(key);
            concatenatedOrderedParams = concatenatedOrderedParams + String.format("%s=%s&", key, value);
        }
        return generateSignatureForString(concatenatedOrderedParams, secretToken);
    }

    public static String generateSignatureForString(String text, String secretToken) {
        String textPlusKey = text + secretToken;
        return generateSHA1ForString(textPlusKey);
    }

    public static String generateSHA1ForString(String text) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance(SHA1_ALGORITHM);
            byte[] digestBytes = sha1.digest(text.getBytes());
            String digestString = byteArray2Hex(digestBytes);
            return digestString;
        } catch (NoSuchAlgorithmException e) {
            Log.e("UrlBuilder", "SHA1 algorithm not available.");
            e.printStackTrace();
            return NO_SHA1_RESULT;
        }
    }

    public static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", Byte.valueOf(b));
        }
        return formatter.toString();
    }
}
