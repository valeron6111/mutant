package com.arellomobile.android.push;

import android.content.Context;
import android.content.res.Configuration;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.tapjoy.TapjoyConstants;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class DeviceRegistrar {
    private static final String BASE_URL = "https://cp.pushwoosh.com/json";
    private static final String REGISTER_PATH = "/1.1/registerDevice";
    private static final String TAG = "DeviceRegistrar";
    private static final String UNREGISTER_PATH = "/1.1/unregisterDevice";

    public static void registerWithServer(final Context context, final String deviceRegistrationID) {
        new Thread(new Runnable() { // from class: com.arellomobile.android.push.DeviceRegistrar.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    HttpResponse res = DeviceRegistrar.makeRequest(context, deviceRegistrationID, DeviceRegistrar.REGISTER_PATH);
                    if (res.getStatusLine().getStatusCode() != 200) {
                        Log.w(DeviceRegistrar.TAG, "Registration error " + String.valueOf(res.getStatusLine().getStatusCode()));
                    } else {
                        Log.w(DeviceRegistrar.TAG, "Registered for pushes: " + deviceRegistrationID);
                    }
                } catch (Exception e) {
                    Log.w(DeviceRegistrar.TAG, "Registration error " + e.getMessage());
                }
            }
        }).start();
    }

    public static void unregisterWithServer(final Context context, final String deviceRegistrationID) {
        new Thread(new Runnable() { // from class: com.arellomobile.android.push.DeviceRegistrar.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    HttpResponse res = DeviceRegistrar.makeRequest(context, deviceRegistrationID, DeviceRegistrar.UNREGISTER_PATH);
                    if (res.getStatusLine().getStatusCode() != 200) {
                        Log.w(DeviceRegistrar.TAG, "Unregistration error " + String.valueOf(res.getStatusLine().getStatusCode()));
                    }
                } catch (Exception e) {
                    Log.w(DeviceRegistrar.TAG, "Unegistration error " + e.getMessage());
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static HttpResponse makeRequest(Context context, String deviceRegistrationID, String urlPath) throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(BASE_URL + urlPath);
        JSONObject innerRequestJson = new JSONObject();
        String deviceId = getDeviceUUID(context);
        if (deviceId != null) {
            innerRequestJson.put("hw_id", deviceId);
        }
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        innerRequestJson.put(TapjoyConstants.TJC_DEVICE_NAME, isTablet(context) ? "Tablet" : "Phone");
        innerRequestJson.put("application", PushManager.APP_ID);
        innerRequestJson.put(TapjoyConstants.TJC_DEVICE_TYPE_NAME, "3");
        innerRequestJson.put("device_id", deviceRegistrationID);
        innerRequestJson.put("language", language);
        innerRequestJson.put("timezone", Calendar.getInstance().getTimeZone().getRawOffset() / 1000);
        JSONObject requestJson = new JSONObject();
        requestJson.put("request", innerRequestJson);
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.setEntity(new StringEntity(requestJson.toString(), "UTF-8"));
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "POST request: " + requestJson.toString());
        }
        HttpResponse httpResponse = httpClient.execute(httpPost);
        return httpResponse;
    }

    private static String getDeviceUUID(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), TapjoyConstants.TJC_ANDROID_ID);
        if (androidId == null) {
            try {
                String deviceId = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
                if (deviceId != null) {
                    return deviceId;
                }
            } catch (RuntimeException e) {
            }
            return UUID.randomUUID().toString();
        }
        return androidId;
    }

    static boolean isTablet(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return (config.screenLayout & 4) == 4;
    }
}
