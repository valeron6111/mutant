package com.alawar.mutant.network;

import android.util.Log;
import com.alawar.common.event.EventBus;
import com.alawar.mutant.MutantStats;
import com.alawar.mutant.network.http.RestClient;
import com.alawar.mutant.notification.NotificationsUpdatedEvent;
import com.alawar.mutant.thirdparty.sponsorpay.SponsorPayActivity;
import com.alawar.mutant.util.DeviceUUID;
import com.alawar.mutant.util.Signer;
import com.tapjoy.TapjoyConstants;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NetworkService {
    private static final String BaseURL = "http://news.tut.by/";
    static final int PING_INTERVAL = 30;
    private static final String clientKey = "_client";
    static final NetworkService inst = new NetworkService();
    ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(1);

    public static NetworkService instance() {
        return inst;
    }

    private NetworkService() {
    }

    public static void initialize() {
        Runnable t = new Runnable() { // from class: com.alawar.mutant.network.NetworkService.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (NetworkService.class) {
                    try {
                        NetworkService.instance().ping();
                    } catch (Exception e) {
                        Log.e("NetworkService", "Ping failed", e);
                    }
                    try {
                        SponsorPayActivity.requestNewCoins();
                    } catch (Exception e2) {
                        Log.e("NetworkService", "SponsorPayActivity.requestNewCoins failed", e2);
                    }
                }
            }
        };
        instance().timer.scheduleAtFixedRate(t, 5L, 30L, TimeUnit.SECONDS);
    }

    void ping() throws JSONException, NumberFormatException {
        Log.i("NetworkService", "PING!");
        HashMap<String, String> params = new HashMap<>();
        String lu = MutantStats.getString("last_update");
        Long lastUpdateMillis = Long.valueOf(lu == null ? Calendar.getInstance().getTimeInMillis() : Long.parseLong(lu));
        params.put("u", DeviceUUID.getUuid());
        params.put("t", lastUpdateMillis.toString());
        String response = executeRequest(MutantUrl.ping(), params);
        if (response != null) {
            JSONArray result = new JSONArray(response);
            EventBus.publish(new NotificationsUpdatedEvent(result));
            if (result.length() > 0) {
                JSONObject obj = result.getJSONObject(result.length() - 1);
                MutantStats.setString("last_update", Long.toString(obj.getLong(TapjoyConstants.TJC_TIMESTAMP)));
            }
        }
    }

    public String executeRequest(String uri, HashMap<String, String> params) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpParams rqParams = new BasicHttpParams();
        StringBuilder sigSrc = new StringBuilder();
        StringBuilder uriB = new StringBuilder(uri);
        HttpConnectionParams.setConnectionTimeout(rqParams, 3000);
        uriB.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            uriB.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            sigSrc.append(entry.getKey());
            sigSrc.append("_");
            sigSrc.append(entry.getValue());
        }
        uriB.append("sig=").append(Signer.md5(sigSrc.toString() + "_" + DeviceUUID.getUuid() + clientKey));
        HttpGet get = new HttpGet(uriB.toString());
        try {
            HttpResponse response = client.execute(get);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                Header[] headers = response.getHeaders("x-sig");
                if (headers.length == 0) {
                    return null;
                }
                String sig = headers[0].getValue();
                String result = EntityUtils.toString(response.getEntity());
                String expSig = Signer.md5(result.toLowerCase().trim() + "_" + DeviceUUID.getUuid() + "_secret");
                if (!sig.equalsIgnoreCase(expSig)) {
                    Log.w("NetworkService", "Incorrect response signature, got " + sig + ", expected " + expSig);
                    return result;
                }
                return result;
            }
        } catch (UnknownHostException e) {
            Log.w("NetworkService", "Host unreachable");
        } catch (Exception e2) {
            Log.e("NetworkService", "Error executing request", e2);
        }
        return null;
    }

    public Map<String, String> execute(String uri, Map<String, String> params) {
        RestClient restClient = new RestClient(BaseURL + uri);
        String sig = signature(params);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            restClient.addParam(entry.getKey(), entry.getValue());
        }
        restClient.addParam("u", DeviceUUID.getUuid());
        restClient.addParam("i", DeviceUUID.getInstallationId());
        restClient.addParam("sig", sig);
        restClient.execute(RequestMethod.GET);
        Map<String, String> result = new HashMap<>();
        String response = restClient.getResponse();
        if (response == null) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(response);
            Iterator iterator = json.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if ("sig".equals(key)) {
                    json.getString(key);
                } else {
                    result.put(key, json.getString(key));
                }
            }
            return result;
        } catch (JSONException e) {
            Log.e("NETWORK", "Cannot parse JSON", e);
            return null;
        }
    }

    private String signature(String s) {
        return DeviceUUID.md5(s);
    }

    private String signature(Map<String, String> params) {
        List<String> paramNames = new ArrayList<>(params.keySet());
        Collections.sort(paramNames);
        StringBuilder sb = new StringBuilder(50);
        for (String paramName : paramNames) {
            sb.append(paramName).append("=").append(params.get(paramName));
        }
        return DeviceUUID.sign(sb.toString());
    }
}
