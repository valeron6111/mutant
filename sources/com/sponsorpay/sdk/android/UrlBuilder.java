package com.sponsorpay.sdk.android;

import android.net.Uri;
import com.alawar.mutant.jni.MutantMessages;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class UrlBuilder {
    private static final String ANDROID_ID_KEY = "android_id";
    private static final String APPID_KEY = "appid";
    private static final String LANGUAGE_KEY = "language";
    private static final String OS_VERSION_KEY = "os_version";
    private static final String PHONE_VERSION_KEY = "phone_version";
    private static final String SDK_RELEASE_VERSION_KEY = "sdk_version";
    private static final String UDID_KEY = "device_id";
    public static final String URL_PARAM_ALLOW_CAMPAIGN_KEY = "allow_campaign";
    public static final String URL_PARAM_CURRENCY_NAME_KEY = "currency";
    public static final String URL_PARAM_OFFSET_KEY = "offset";
    private static final String URL_PARAM_SIGNATURE = "signature";
    public static final String URL_PARAM_VALUE_ON = "on";
    private static final String USERID_KEY = "uid";
    private static final String WIFI_MAC_ADDRESS_KEY = "mac_address";

    public static String buildUrl(String resourceUrl, String userId, HostInfo hostInfo, Map<String, String> extraKeysValues) {
        return buildUrl(resourceUrl, userId, hostInfo, extraKeysValues, null);
    }

    public static String buildUrl(String resourceUrl, HostInfo hostInfo, Map<String, String> extraKeysValues) {
        return buildUrl(resourceUrl, null, hostInfo, extraKeysValues, null);
    }

    public static String buildUrl(String resourceUrl, String userId, HostInfo hostInfo, Map<String, String> extraKeysValues, String secretKey) {
        HashMap<String, String> keyValueParams = new HashMap<>();
        if (userId != null) {
            keyValueParams.put(USERID_KEY, userId);
        }
        keyValueParams.put(UDID_KEY, hostInfo.getUDID());
        keyValueParams.put(APPID_KEY, String.valueOf(hostInfo.getAppId()));
        keyValueParams.put("os_version", hostInfo.getOsVersion());
        keyValueParams.put(PHONE_VERSION_KEY, hostInfo.getPhoneVersion());
        keyValueParams.put(LANGUAGE_KEY, hostInfo.getLanguageSetting());
        keyValueParams.put(SDK_RELEASE_VERSION_KEY, SponsorPay.RELEASE_VERSION_STRING);
        keyValueParams.put("android_id", hostInfo.getAndroidId());
        keyValueParams.put(WIFI_MAC_ADDRESS_KEY, hostInfo.getWifiMacAddress());
        if (extraKeysValues != null) {
            validateKeyValueParams(extraKeysValues);
            keyValueParams.putAll(extraKeysValues);
        }
        Uri uri = Uri.parse(resourceUrl);
        Uri.Builder builder = uri.buildUpon();
        Set<String> keySet = keyValueParams.keySet();
        for (String key : keySet) {
            builder.appendQueryParameter(key, keyValueParams.get(key));
        }
        if (secretKey != null) {
            builder.appendQueryParameter(URL_PARAM_SIGNATURE, SignatureTools.generateSignatureForParameters(keyValueParams, secretKey));
        }
        Uri uri2 = builder.build();
        return uri2.toString();
    }

    public static void validateKeyValueParams(Map<String, String> kvParams) {
        if (kvParams != null) {
            Set<String> extraKeySet = kvParams.keySet();
            for (String k : extraKeySet) {
                String v = kvParams.get(k);
                if (k == null || MutantMessages.sEmpty.equals(k) || v == null || MutantMessages.sEmpty.equals(v)) {
                    throw new IllegalArgumentException("SponsorPay SDK: Custom Parameters cannot have an empty or null Key or Value.");
                }
            }
        }
    }

    public static Map<String, String> mapKeysToValues(String[] keys, String[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException("SponsorPay SDK: When specifying Custom Parameters using two arrays of Keys and Values, both must have the same length.");
        }
        HashMap<String, String> retval = new HashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            String k = keys[i];
            String v = values[i];
            if (k == null || MutantMessages.sEmpty.equals(k) || v == null || MutantMessages.sEmpty.equals(v)) {
                throw new IllegalArgumentException("SponsorPay SDK: When specifying Custom Parameters using two arrays of Keys and Values, none of their elements can be empty or null.");
            }
            retval.put(k, v);
        }
        return retval;
    }
}
