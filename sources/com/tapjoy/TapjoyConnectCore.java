package com.tapjoy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.alawar.mutant.jni.MutantMessages;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import org.w3c.dom.Document;

/* loaded from: classes.dex */
public class TapjoyConnectCore {
    public static final String TAPJOY_CONNECT = "TapjoyConnect";
    private long elapsed_time = 0;
    private Timer timer = null;
    private static Context context = null;
    private static TapjoyConnectCore tapjoyConnectCore = null;
    private static TapjoyURLConnection tapjoyURLConnection = null;
    private static String androidID = MutantMessages.sEmpty;
    private static String deviceID = MutantMessages.sEmpty;
    private static String deviceModel = MutantMessages.sEmpty;
    private static String deviceManufacturer = MutantMessages.sEmpty;
    private static String deviceType = MutantMessages.sEmpty;
    private static String deviceOSVersion = MutantMessages.sEmpty;
    private static String deviceCountryCode = MutantMessages.sEmpty;
    private static String deviceLanguage = MutantMessages.sEmpty;
    private static String appID = MutantMessages.sEmpty;
    private static String appVersion = MutantMessages.sEmpty;
    private static String libraryVersion = MutantMessages.sEmpty;
    private static String deviceScreenDensity = MutantMessages.sEmpty;
    private static String deviceScreenLayoutSize = MutantMessages.sEmpty;
    private static String userID = MutantMessages.sEmpty;
    private static String platformName = MutantMessages.sEmpty;
    private static String carrierName = MutantMessages.sEmpty;
    private static String carrierCountryCode = MutantMessages.sEmpty;
    private static String mobileCountryCode = MutantMessages.sEmpty;
    private static String mobileNetworkCode = MutantMessages.sEmpty;
    private static String connectionType = MutantMessages.sEmpty;
    private static String secretKey = MutantMessages.sEmpty;
    private static String clientPackage = MutantMessages.sEmpty;
    private static String referralURL = MutantMessages.sEmpty;
    private static String plugin = TapjoyConstants.TJC_PLUGIN_NATIVE;
    private static String sdkType = MutantMessages.sEmpty;
    private static boolean videoEnabled = false;
    private static boolean enableVideoCache = true;
    private static String videoIDs = MutantMessages.sEmpty;
    private static float currencyMultiplier = 1.0f;
    private static String paidAppActionID = null;
    private static String matchingPackageNames = MutantMessages.sEmpty;

    static /* synthetic */ long access$014(TapjoyConnectCore x0, long x1) {
        long j = x0.elapsed_time + x1;
        x0.elapsed_time = j;
        return j;
    }

    public static TapjoyConnectCore getInstance() {
        return tapjoyConnectCore;
    }

    public static void requestTapjoyConnect(Context applicationContext, String app_ID, String secret_Key) {
        appID = app_ID;
        secretKey = secret_Key;
        tapjoyConnectCore = new TapjoyConnectCore(applicationContext);
    }

    public TapjoyConnectCore(Context applicationContext) {
        context = applicationContext;
        tapjoyURLConnection = new TapjoyURLConnection();
        init();
        TapjoyLog.m190i("TapjoyConnect", "URL parameters: " + getURLParams());
        new Thread(new ConnectThread()).start();
    }

    public void callConnect() {
        new Thread(new ConnectThread()).start();
    }

    public static String getURLParams() {
        String urlParams = getGenericURLParams() + "&";
        long time = System.currentTimeMillis() / 1000;
        String verifier = getVerifier(time);
        return (urlParams + "timestamp=" + time + "&") + "verifier=" + verifier;
    }

    public static String getGenericURLParams() {
        String urlParams = MutantMessages.sEmpty + "app_id=" + Uri.encode(appID) + "&";
        return urlParams + getParamsWithoutAppID();
    }

    private static String getParamsWithoutAppID() {
        String urlParams = (((((((((((MutantMessages.sEmpty + "android_id=" + androidID + "&") + "udid=" + Uri.encode(deviceID) + "&") + "device_name=" + Uri.encode(deviceModel) + "&") + "device_manufacturer=" + Uri.encode(deviceManufacturer) + "&") + "device_type=" + Uri.encode(deviceType) + "&") + "os_version=" + Uri.encode(deviceOSVersion) + "&") + "country_code=" + Uri.encode(deviceCountryCode) + "&") + "language_code=" + Uri.encode(deviceLanguage) + "&") + "app_version=" + Uri.encode(appVersion) + "&") + "library_version=" + Uri.encode(libraryVersion) + "&") + "platform=" + Uri.encode(platformName) + "&") + "display_multiplier=" + Uri.encode(Float.toString(currencyMultiplier));
        if (carrierName.length() > 0) {
            urlParams = (urlParams + "&") + "carrier_name=" + Uri.encode(carrierName);
        }
        if (carrierCountryCode.length() > 0) {
            urlParams = (urlParams + "&") + "carrier_country_code=" + Uri.encode(carrierCountryCode);
        }
        if (mobileCountryCode.length() > 0) {
            urlParams = (urlParams + "&") + "mobile_country_code=" + Uri.encode(mobileCountryCode);
        }
        if (mobileNetworkCode.length() > 0) {
            urlParams = (urlParams + "&") + "mobile_network_code=" + Uri.encode(mobileNetworkCode);
        }
        if (deviceScreenDensity.length() > 0 && deviceScreenLayoutSize.length() > 0) {
            urlParams = ((urlParams + "&") + "screen_density=" + Uri.encode(deviceScreenDensity) + "&") + "screen_layout_size=" + Uri.encode(deviceScreenLayoutSize);
        }
        connectionType = getConnectionType();
        if (connectionType.length() > 0) {
            urlParams = (urlParams + "&") + "connection_type=" + Uri.encode(connectionType);
        }
        if (plugin.length() > 0) {
            urlParams = (urlParams + "&") + "plugin=" + Uri.encode(plugin);
        }
        if (sdkType.length() > 0) {
            return (urlParams + "&") + "sdk_type=" + Uri.encode(sdkType);
        }
        return urlParams;
    }

    private void init() {
        PackageManager manager = context.getPackageManager();
        try {
            androidID = Settings.Secure.getString(context.getContentResolver(), TapjoyConstants.TJC_ANDROID_ID);
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            appVersion = packageInfo.versionName;
            deviceType = TapjoyConstants.TJC_DEVICE_PLATFORM_TYPE;
            platformName = TapjoyConstants.TJC_DEVICE_PLATFORM_TYPE;
            deviceModel = Build.MODEL;
            deviceManufacturer = Build.MANUFACTURER;
            deviceOSVersion = Build.VERSION.RELEASE;
            deviceCountryCode = Locale.getDefault().getCountry();
            deviceLanguage = Locale.getDefault().getLanguage();
            libraryVersion = TapjoyConstants.TJC_LIBRARY_VERSION_NUMBER;
            SharedPreferences settings = context.getSharedPreferences(TapjoyConstants.TJC_PREFERENCE, 0);
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                if (telephonyManager != null) {
                    deviceID = telephonyManager.getDeviceId();
                    carrierName = telephonyManager.getNetworkOperatorName();
                    carrierCountryCode = telephonyManager.getNetworkCountryIso();
                    if (telephonyManager.getNetworkOperator() != null && (telephonyManager.getNetworkOperator().length() == 5 || telephonyManager.getNetworkOperator().length() == 6)) {
                        mobileCountryCode = telephonyManager.getNetworkOperator().substring(0, 3);
                        mobileNetworkCode = telephonyManager.getNetworkOperator().substring(3);
                    }
                }
                TapjoyLog.m190i("TapjoyConnect", "deviceID: " + deviceID);
                boolean invalidDeviceID = false;
                if (deviceID == null) {
                    TapjoyLog.m189e("TapjoyConnect", "Device id is null.");
                    invalidDeviceID = true;
                } else if (deviceID.length() == 0 || deviceID.equals("000000000000000") || deviceID.equals("0")) {
                    TapjoyLog.m189e("TapjoyConnect", "Device id is empty or an emulator.");
                    invalidDeviceID = true;
                } else {
                    deviceID = deviceID.toLowerCase();
                }
                TapjoyLog.m190i("TapjoyConnect", "ANDROID SDK VERSION: " + Build.VERSION.SDK);
                if (invalidDeviceID && Integer.parseInt(Build.VERSION.SDK) >= 9) {
                    TapjoyLog.m190i("TapjoyConnect", "TRYING TO GET SERIAL OF 2.3+ DEVICE...");
                    TapjoyHardwareUtil hardware = new TapjoyHardwareUtil();
                    deviceID = hardware.getSerial();
                    TapjoyLog.m190i("TapjoyConnect", "====================");
                    TapjoyLog.m190i("TapjoyConnect", "SERIAL: deviceID: [" + deviceID + "]");
                    TapjoyLog.m190i("TapjoyConnect", "====================");
                    if (deviceID == null) {
                        TapjoyLog.m189e("TapjoyConnect", "SERIAL: Device id is null.");
                        invalidDeviceID = true;
                    } else if (deviceID.length() == 0 || deviceID.equals("000000000000000") || deviceID.equals("0") || deviceID.equals("unknown")) {
                        TapjoyLog.m189e("TapjoyConnect", "SERIAL: Device id is empty or an emulator.");
                        invalidDeviceID = true;
                    } else {
                        deviceID = deviceID.toLowerCase();
                        invalidDeviceID = false;
                    }
                }
                if (invalidDeviceID) {
                    StringBuffer buff = new StringBuffer();
                    buff.append("EMULATOR");
                    String deviceId = settings.getString(TapjoyConstants.PREF_EMULATOR_DEVICE_ID, null);
                    if (deviceId != null && !deviceId.equals(MutantMessages.sEmpty)) {
                        deviceID = deviceId;
                    } else {
                        for (int i = 0; i < 32; i++) {
                            int randomChar = (int) (Math.random() * 100.0d);
                            int ch = randomChar % 30;
                            buff.append("1234567890abcdefghijklmnopqrstuvw".charAt(ch));
                        }
                        deviceID = buff.toString().toLowerCase();
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(TapjoyConstants.PREF_EMULATOR_DEVICE_ID, deviceID);
                        editor.commit();
                    }
                }
            } catch (Exception e) {
                TapjoyLog.m189e("TapjoyConnect", "Error getting deviceID. e: " + e.toString());
                deviceID = null;
            }
            if (userID.length() == 0) {
                userID = deviceID;
            }
            try {
                if (Integer.parseInt(Build.VERSION.SDK) > 3) {
                    TapjoyDisplayMetricsUtil displayMetricsUtil = new TapjoyDisplayMetricsUtil(context);
                    deviceScreenDensity = MutantMessages.sEmpty + displayMetricsUtil.getScreenDensity();
                    deviceScreenLayoutSize = MutantMessages.sEmpty + displayMetricsUtil.getScreenLayoutSize();
                }
            } catch (Exception e2) {
                TapjoyLog.m189e("TapjoyConnect", "Error getting screen density/dimensions/layout: " + e2.toString());
            }
            String tempReferralURL = settings.getString(TapjoyConstants.PREF_REFERRAL_URL, null);
            if (tempReferralURL != null && !tempReferralURL.equals(MutantMessages.sEmpty)) {
                referralURL = tempReferralURL;
            }
            clientPackage = context.getPackageName();
            TapjoyLog.m190i("TapjoyConnect", "Metadata successfully loaded");
            TapjoyLog.m190i("TapjoyConnect", "APP_ID = [" + appID + "]");
            TapjoyLog.m190i("TapjoyConnect", "ANDROID_ID: [" + androidID + "]");
            TapjoyLog.m190i("TapjoyConnect", "CLIENT_PACKAGE = [" + clientPackage + "]");
            TapjoyLog.m190i("TapjoyConnect", "deviceID: [" + deviceID + "]");
            TapjoyLog.m190i("TapjoyConnect", "deviceName: [" + deviceModel + "]");
            TapjoyLog.m190i("TapjoyConnect", "deviceManufacturer: [" + deviceManufacturer + "]");
            TapjoyLog.m190i("TapjoyConnect", "deviceType: [" + deviceType + "]");
            TapjoyLog.m190i("TapjoyConnect", "libraryVersion: [" + libraryVersion + "]");
            TapjoyLog.m190i("TapjoyConnect", "deviceOSVersion: [" + deviceOSVersion + "]");
            TapjoyLog.m190i("TapjoyConnect", "COUNTRY_CODE: [" + deviceCountryCode + "]");
            TapjoyLog.m190i("TapjoyConnect", "LANGUAGE_CODE: [" + deviceLanguage + "]");
            TapjoyLog.m190i("TapjoyConnect", "density: [" + deviceScreenDensity + "]");
            TapjoyLog.m190i("TapjoyConnect", "screen_layout: [" + deviceScreenLayoutSize + "]");
            TapjoyLog.m190i("TapjoyConnect", "carrier_name: [" + carrierName + "]");
            TapjoyLog.m190i("TapjoyConnect", "carrier_country_code: [" + carrierCountryCode + "]");
            TapjoyLog.m190i("TapjoyConnect", "mobile_country_code: [" + mobileCountryCode + "]");
            TapjoyLog.m190i("TapjoyConnect", "mobile_network_code: [" + mobileNetworkCode + "]");
            TapjoyLog.m190i("TapjoyConnect", "referralURL: [" + referralURL + "]");
        } catch (Exception e3) {
            TapjoyLog.m189e("TapjoyConnect", "Error initializing Tapjoy parameters.");
        }
    }

    private class PaidAppTimerTask extends TimerTask {
        private PaidAppTimerTask() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            TapjoyConnectCore.access$014(TapjoyConnectCore.this, 10000L);
            TapjoyLog.m190i("TapjoyConnect", "elapsed_time: " + TapjoyConnectCore.this.elapsed_time + " (" + ((TapjoyConnectCore.this.elapsed_time / 1000) / 60) + "m " + ((TapjoyConnectCore.this.elapsed_time / 1000) % 60) + "s)");
            SharedPreferences prefs = TapjoyConnectCore.context.getSharedPreferences(TapjoyConstants.TJC_PREFERENCE, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(TapjoyConstants.PREF_ELAPSED_TIME, TapjoyConnectCore.this.elapsed_time);
            editor.commit();
            if (TapjoyConnectCore.this.elapsed_time >= TapjoyConstants.PAID_APP_TIME) {
                TapjoyLog.m190i("TapjoyConnect", "timer done...");
                if (TapjoyConnectCore.paidAppActionID != null && TapjoyConnectCore.paidAppActionID.length() > 0) {
                    TapjoyLog.m190i("TapjoyConnect", "Calling PPA actionComplete...");
                    TapjoyConnectCore.this.actionComplete(TapjoyConnectCore.paidAppActionID);
                }
                cancel();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean handleConnectResponse(String response) {
        Document document = TapjoyUtil.buildDocument(response);
        if (document != null) {
            String nodeValue = TapjoyUtil.getNodeTrimValue(document.getElementsByTagName("PackageNames"));
            if (nodeValue != null && nodeValue.length() > 0) {
                Vector<String> allPackageNames = new Vector<>();
                int current = 0;
                while (true) {
                    int index = nodeValue.indexOf(44, current);
                    if (index == -1) {
                        break;
                    }
                    TapjoyLog.m190i("TapjoyConnect", "parse: " + nodeValue.substring(current, index).trim());
                    allPackageNames.add(nodeValue.substring(current, index).trim());
                    current = index + 1;
                }
                TapjoyLog.m190i("TapjoyConnect", "parse: " + nodeValue.substring(current).trim());
                allPackageNames.add(nodeValue.substring(current).trim());
                matchingPackageNames = MutantMessages.sEmpty;
                List<ApplicationInfo> applications = context.getPackageManager().getInstalledApplications(0);
                for (ApplicationInfo appInfo : applications) {
                    if ((appInfo.flags & 1) != 1 && allPackageNames.contains(appInfo.packageName)) {
                        TapjoyLog.m190i("TapjoyConnect", "MATCH: installed packageName: " + appInfo.packageName);
                        if (matchingPackageNames.length() > 0) {
                            matchingPackageNames += ",";
                        }
                        matchingPackageNames += appInfo.packageName;
                    }
                }
            }
            String nodeValue2 = TapjoyUtil.getNodeTrimValue(document.getElementsByTagName("Success"));
            if (nodeValue2 == null || nodeValue2.equals("true")) {
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handlePayPerActionResponse(String response) {
        Document document = TapjoyUtil.buildDocument(response);
        if (document != null) {
            String nodeValue = TapjoyUtil.getNodeTrimValue(document.getElementsByTagName("Success"));
            if (nodeValue != null && nodeValue.equals("true")) {
                TapjoyLog.m190i("TapjoyConnect", "Successfully sent completed Pay-Per-Action to Tapjoy server.");
                return true;
            }
            TapjoyLog.m189e("TapjoyConnect", "Completed Pay-Per-Action call failed.");
        }
        return false;
    }

    public void release() {
        tapjoyConnectCore = null;
        tapjoyURLConnection = null;
        TapjoyLog.m190i("TapjoyConnect", "Releasing core static instance.");
    }

    public static String getAppID() {
        return appID;
    }

    public static String getDeviceID() {
        return deviceID;
    }

    public static String getUserID() {
        return userID;
    }

    public static String getVideoIDs() {
        return videoIDs;
    }

    public static String getCarrierName() {
        return carrierName;
    }

    public static String getConnectionType() {
        String type = MutantMessages.sEmpty;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            if (connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null) {
                switch (connectivityManager.getActiveNetworkInfo().getType()) {
                    case 1:
                    case 6:
                        type = "wifi";
                        break;
                    default:
                        type = "mobile";
                        break;
                }
                TapjoyLog.m190i("TapjoyConnect", "connectivity: " + connectivityManager.getActiveNetworkInfo().getType());
                TapjoyLog.m190i("TapjoyConnect", "connection_type: " + type);
            }
        } catch (Exception e) {
            TapjoyLog.m189e("TapjoyConnect", "getConnectionType error: " + e.toString());
        }
        return type;
    }

    public static String getClientPackage() {
        return clientPackage;
    }

    public static Context getContext() {
        return context;
    }

    public static String getVerifier(long time) {
        try {
            String verifier = TapjoyUtil.SHA256(appID + ":" + deviceID + ":" + time + ":" + secretKey);
            return verifier;
        } catch (Exception e) {
            TapjoyLog.m189e("TapjoyConnect", "getVerifier ERROR: " + e.toString());
            return MutantMessages.sEmpty;
        }
    }

    public static String getAwardPointsVerifier(long time, int amount, String guid) {
        try {
            String verifier = TapjoyUtil.SHA256(appID + ":" + deviceID + ":" + time + ":" + secretKey + ":" + amount + ":" + guid);
            return verifier;
        } catch (Exception e) {
            TapjoyLog.m189e("TapjoyConnect", "getAwardPointsVerifier ERROR: " + e.toString());
            return MutantMessages.sEmpty;
        }
    }

    public static String getPackageNamesVerifier(long time, String packageNames) {
        try {
            String verifier = TapjoyUtil.SHA256(appID + ":" + deviceID + ":" + time + ":" + secretKey + ":" + packageNames);
            return verifier;
        } catch (Exception e) {
            TapjoyLog.m189e("TapjoyConnect", "getVerifier ERROR: " + e.toString());
            return MutantMessages.sEmpty;
        }
    }

    public static void setPlugin(String name) {
        plugin = name;
    }

    public static void setSDKType(String name) {
        sdkType = name;
    }

    public static void setUserID(String id) {
        userID = id;
        TapjoyLog.m190i("TapjoyConnect", "URL parameters: " + getURLParams());
        new Thread(new Runnable() { // from class: com.tapjoy.TapjoyConnectCore.1
            @Override // java.lang.Runnable
            public void run() {
                TapjoyLog.m190i("TapjoyConnect", "setUserID...");
                String connectURLParams = TapjoyConnectCore.getURLParams() + "&publisher_user_id=" + TapjoyConnectCore.getUserID();
                if (!TapjoyConnectCore.referralURL.equals(MutantMessages.sEmpty)) {
                    connectURLParams = connectURLParams + "&" + TapjoyConnectCore.referralURL;
                }
                String result = TapjoyConnectCore.tapjoyURLConnection.connectToURL("https://ws.tapjoyads.com/set_publisher_user_id?", connectURLParams);
                if (result != null) {
                    if (TapjoyConnectCore.handleConnectResponse(result)) {
                    }
                    TapjoyLog.m190i("TapjoyConnect", "setUserID successful...");
                }
            }
        }).start();
    }

    public static void setVideoIDs(String ids) {
        videoIDs = ids;
    }

    public static void setVideoEnabled(boolean enabled) {
        videoEnabled = enabled;
    }

    public static void enableVideoCache(boolean enable) {
        enableVideoCache = enable;
    }

    public static boolean isVideoCacheEnabled() {
        return enableVideoCache;
    }

    public static boolean isVideoEnabled() {
        return videoEnabled;
    }

    public static void setDebugDeviceID(String id) {
        deviceID = id;
        SharedPreferences settings = context.getSharedPreferences(TapjoyConstants.TJC_PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(TapjoyConstants.PREF_EMULATOR_DEVICE_ID, deviceID);
        editor.commit();
    }

    public static void saveTapPointsTotal(int total) {
        SharedPreferences settings = context.getSharedPreferences(TapjoyConstants.TJC_PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(TapjoyConstants.PREF_LAST_TAP_POINTS, total);
        editor.commit();
    }

    public static int getLocalTapPointsTotal() {
        SharedPreferences settings = context.getSharedPreferences(TapjoyConstants.TJC_PREFERENCE, 0);
        int total = settings.getInt(TapjoyConstants.PREF_LAST_TAP_POINTS, -9999);
        return total;
    }

    public void actionComplete(String actionID) {
        TapjoyLog.m190i("TapjoyConnect", "actionComplete: " + actionID);
        String actionURLParams = "app_id=" + actionID + "&";
        String actionURLParams2 = ((actionURLParams + getParamsWithoutAppID()) + "&publisher_user_id=" + getUserID()) + "&";
        long time = System.currentTimeMillis() / 1000;
        String actionURLParams3 = (actionURLParams2 + "timestamp=" + time + "&") + "verifier=" + getVerifier(time);
        TapjoyLog.m190i("TapjoyConnect", "PPA URL parameters: " + actionURLParams3);
        new Thread(new PPAThread(actionURLParams3)).start();
    }

    public void enablePaidAppWithActionID(String paidAppPayPerActionID) {
        TapjoyLog.m190i("TapjoyConnect", "enablePaidAppWithActionID: " + paidAppPayPerActionID);
        paidAppActionID = paidAppPayPerActionID;
        SharedPreferences prefs = context.getSharedPreferences(TapjoyConstants.TJC_PREFERENCE, 0);
        this.elapsed_time = prefs.getLong(TapjoyConstants.PREF_ELAPSED_TIME, 0L);
        TapjoyLog.m190i("TapjoyConnect", "paidApp elapsed: " + this.elapsed_time);
        if (this.elapsed_time >= TapjoyConstants.PAID_APP_TIME) {
            if (paidAppActionID != null && paidAppActionID.length() > 0) {
                TapjoyLog.m190i("TapjoyConnect", "Calling PPA actionComplete...");
                actionComplete(paidAppActionID);
                return;
            }
            return;
        }
        if (this.timer == null) {
            this.timer = new Timer();
            this.timer.schedule(new PaidAppTimerTask(), 10000L, 10000L);
        }
    }

    public class ConnectThread implements Runnable {
        public ConnectThread() {
        }

        @Override // java.lang.Runnable
        public void run() {
            TapjoyLog.m190i("TapjoyConnect", "starting connect call...");
            String connectURLParams = TapjoyConnectCore.getURLParams();
            TapjoyHttpURLResponse httpResponse = TapjoyConnectCore.tapjoyURLConnection.getResponseFromURL("https://ws.tapjoyads.com/connect?", connectURLParams);
            if (httpResponse != null && httpResponse.statusCode == 200) {
                if (TapjoyConnectCore.handleConnectResponse(httpResponse.response)) {
                    TapjoyLog.m190i("TapjoyConnect", "Successfully connected to tapjoy site.");
                }
                if (TapjoyConnectCore.matchingPackageNames.length() > 0) {
                    String params = TapjoyConnectCore.getGenericURLParams() + "&" + TapjoyConstants.TJC_PACKAGE_NAMES + "=" + TapjoyConnectCore.matchingPackageNames + "&";
                    long time = System.currentTimeMillis() / 1000;
                    String verifier = TapjoyConnectCore.getPackageNamesVerifier(time, TapjoyConnectCore.matchingPackageNames);
                    TapjoyHttpURLResponse httpResponse2 = TapjoyConnectCore.tapjoyURLConnection.getResponseFromURL("https://ws.tapjoyads.com/apps_installed?", (params + "timestamp=" + time + "&") + "verifier=" + verifier);
                    if (httpResponse2 != null && httpResponse2.statusCode == 200) {
                        TapjoyLog.m190i("TapjoyConnect", "Successfully pinged sdkless api.");
                    }
                }
            }
        }
    }

    public class PPAThread implements Runnable {
        private String params;

        public PPAThread(String urlParams) {
            this.params = urlParams;
        }

        @Override // java.lang.Runnable
        public void run() {
            String result = TapjoyConnectCore.tapjoyURLConnection.connectToURL("https://ws.tapjoyads.com/connect?", this.params);
            if (result != null) {
                TapjoyConnectCore.this.handlePayPerActionResponse(result);
            }
        }
    }

    public void setCurrencyMultiplier(float multiplier) {
        TapjoyLog.m190i("TapjoyConnect", "setVirtualCurrencyMultiplier: " + multiplier);
        currencyMultiplier = multiplier;
    }

    public float getCurrencyMultiplier() {
        return currencyMultiplier;
    }
}
