package com.sponsorpay.sdk.android;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.alawar.mutant.jni.MutantMessages;
import com.tapjoy.TapjoyConstants;
import java.lang.reflect.Field;
import java.util.Locale;

/* loaded from: classes.dex */
public class HostInfo {
    private static final String ANDROID_OS_PREFIX = "Android OS ";
    private static final String SPONSORPAY_APP_ID_KEY = "SPONSORPAY_APP_ID";
    private String mAndroidId;
    private String mAppId;
    private Context mContext;
    private String mHardwareSerialNumber;
    private String mLanguageSetting;
    private String mOsVersion;
    private String mPhoneVersion;
    private String mUDID;
    private String mWifiMacAddress;
    private static boolean sSimulateNoReadPhoneStatePermission = false;
    private static boolean sSimulateNoAccessWifiStatePermission = false;
    private static boolean sSimulateInvalidAndroidId = false;
    private static boolean sSimulateNoHardwareSerialNumber = false;

    public static void setSimulateNoReadPhoneStatePermission(boolean value) {
        sSimulateNoReadPhoneStatePermission = value;
    }

    public static void setSimulateNoAccessWifiStatePermission(boolean value) {
        sSimulateNoAccessWifiStatePermission = value;
    }

    public static void setSimulateInvalidAndroidId(boolean value) {
        sSimulateInvalidAndroidId = value;
    }

    public static void setSimulateNoHardwareSerialNumber(boolean value) {
        sSimulateNoHardwareSerialNumber = value;
    }

    public String getUDID() {
        return this.mUDID;
    }

    public String getOsVersion() {
        return this.mOsVersion;
    }

    public String getPhoneVersion() {
        return this.mPhoneVersion;
    }

    public String getHardwareSerialNumber() {
        if (this.mHardwareSerialNumber == null) {
            if (!sSimulateNoHardwareSerialNumber) {
                try {
                    Field serialField = Build.class.getField("SERIAL");
                    Object serialValue = serialField.get(null);
                    if (serialValue != null && serialValue.getClass().equals(String.class)) {
                        this.mHardwareSerialNumber = (String) serialValue;
                    }
                } catch (Exception e) {
                    this.mHardwareSerialNumber = MutantMessages.sEmpty;
                }
            } else {
                this.mHardwareSerialNumber = MutantMessages.sEmpty;
            }
        }
        return this.mHardwareSerialNumber;
    }

    public String getLanguageSetting() {
        return this.mLanguageSetting;
    }

    public String getAndroidId() {
        return this.mAndroidId;
    }

    public String getWifiMacAddress() {
        return this.mWifiMacAddress;
    }

    public HostInfo(Context context) {
        this.mContext = context;
        if (!sSimulateNoReadPhoneStatePermission) {
            TelephonyManager tManager = (TelephonyManager) context.getSystemService("phone");
            try {
                this.mUDID = tManager.getDeviceId();
            } catch (SecurityException e) {
                this.mUDID = MutantMessages.sEmpty;
            }
        } else {
            this.mUDID = MutantMessages.sEmpty;
        }
        this.mLanguageSetting = Locale.getDefault().toString();
        this.mOsVersion = ANDROID_OS_PREFIX + Build.VERSION.RELEASE;
        this.mPhoneVersion = Build.MANUFACTURER + "_" + Build.MODEL;
        if (!sSimulateInvalidAndroidId) {
            this.mAndroidId = Settings.Secure.getString(this.mContext.getContentResolver(), TapjoyConstants.TJC_ANDROID_ID);
            if (this.mAndroidId == null) {
                this.mAndroidId = MutantMessages.sEmpty;
            }
        } else {
            this.mAndroidId = MutantMessages.sEmpty;
        }
        if (!sSimulateNoAccessWifiStatePermission) {
            try {
                WifiManager wifiMan = (WifiManager) this.mContext.getSystemService("wifi");
                WifiInfo wifiInf = wifiMan.getConnectionInfo();
                this.mWifiMacAddress = wifiInf.getMacAddress();
                return;
            } catch (RuntimeException e2) {
                this.mWifiMacAddress = MutantMessages.sEmpty;
                return;
            }
        }
        this.mWifiMacAddress = MutantMessages.sEmpty;
    }

    private String getValueFromAppMetadata(String key) {
        Object retrievedValue;
        try {
            ApplicationInfo ai = this.mContext.getPackageManager().getApplicationInfo(this.mContext.getPackageName(), 128);
            Bundle appMetadata = ai.metaData;
            if (appMetadata == null || (retrievedValue = appMetadata.get(key)) == null) {
                return null;
            }
            return retrievedValue.toString();
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public String getAppId() {
        if (this.mAppId == null || this.mAppId.equals(MutantMessages.sEmpty)) {
            this.mAppId = getValueFromAppMetadata(SPONSORPAY_APP_ID_KEY);
            if (this.mAppId == null || this.mAppId.equals(MutantMessages.sEmpty)) {
                throw new RuntimeException("SponsorPay SDK: no valid App ID has been provided. Please set a valid App ID in your application manifest or provide one at runtime. See the integration guide or the SDK javadoc for more information.");
            }
        }
        return this.mAppId;
    }

    public void setOverriddenAppId(String appId) {
        this.mAppId = appId;
    }
}
