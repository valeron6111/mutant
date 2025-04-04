package com.alawar.mutant.thirdparty.flurry;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import com.alawar.mutant.database.DbBuilder;
import com.alawar.mutant.jni.MutantMessages;
import com.alawar.mutant.thirdparty.sponsorpay.SponsorPayActivity;
import com.alawar.mutant.util.DeviceUUID;
import com.flurry.android.AppCircle;
import com.flurry.android.FlurryAgent;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MutantFlurry extends MutantMessages {
    private static final String hookName = "mutant_00010AFH";
    private static Activity mActivity = null;
    private static AppCircle mAppCircle = null;
    private static View mPromoView = null;
    private static final String projectApiKey = "KQM76K4XH4LWGUHU1U68";
    private static final int queryDisableBanner = 1;
    private static final int queryEnableBanner = 0;
    private static final int queryEvent = 3;
    private static final int queryOpenCatalog = 2;

    private enum EventType {
        untimedEvent,
        beginEvent,
        endEvent
    }

    public static void start(Activity activity) {
        mActivity = activity;
        FlurryAgent.setCatalogIntentName("com.alawar.mutant.CALL");
        FlurryAgent.enableAppCircle();
        FlurryAgent.onStartSession(activity, projectApiKey);
        mAppCircle = FlurryAgent.getAppCircle();
        mPromoView = mAppCircle.getHook(activity, hookName, 2);
        mAppCircle.addUserCookie("muiid", DeviceUUID.getUuid());
    }

    public static void stop(Activity activity) {
        mActivity = activity;
        FlurryAgent.onEndSession(activity);
    }

    public static void enableBanner() {
    }

    public static void disableBanner() {
    }

    public static void openCatalog() {
        mActivity.runOnUiThread(new Runnable() { // from class: com.alawar.mutant.thirdparty.flurry.MutantFlurry.1
            @Override // java.lang.Runnable
            public void run() {
                SponsorPayActivity.openOfferWall();
            }
        });
    }

    static void logEvent(String category, String event, HashMap<String, String> params, EventType eventType) {
        params.put("category", category);
        if (eventType == EventType.untimedEvent) {
            FlurryAgent.logEvent(event, params);
        } else if (eventType == EventType.beginEvent) {
            FlurryAgent.logEvent(event, params, true);
        } else if (eventType == EventType.endEvent) {
            FlurryAgent.endTimedEvent(event);
        }
    }

    public static boolean logEvent(String paramString) {
        Log.i("MutantFlurry", "Params: " + paramString);
        String[] params = paramString.split("_");
        if (params.length != 4) {
            Log.e("MutantFlurry", "Invalid param count: " + params.length + ", must be 3");
            return false;
        }
        String category = params[0];
        String event = params[1];
        EventType eventType = EventType.untimedEvent;
        if (params[2].equalsIgnoreCase("begin")) {
            eventType = EventType.beginEvent;
        } else if (params[2].equalsIgnoreCase("end")) {
            eventType = EventType.endEvent;
        }
        String jsonString = params[3];
        HashMap<String, String> eventParams = new HashMap<>();
        try {
            JSONArray jsonParams = new JSONArray(jsonString);
            for (int i = 0; i < jsonParams.length(); i++) {
                JSONObject pair = (JSONObject) jsonParams.get(i);
                eventParams.put(pair.get("name").toString(), pair.get(DbBuilder.KEY_VALUE).toString());
            }
            logEvent(category, event, eventParams, eventType);
            return true;
        } catch (NullPointerException e) {
            Log.e("MutantFlurry", "NullPointerException parsing JSON", e);
            return false;
        } catch (JSONException e2) {
            Log.e("MutantFlurry", "Error parsing JSON", e2);
            return false;
        }
    }

    public static String fluProcess(int id, String args) {
        switch (id) {
            case 0:
                enableBanner();
                return MutantMessages.sSuccess;
            case 1:
                disableBanner();
                return MutantMessages.sSuccess;
            case 2:
                openCatalog();
                return MutantMessages.sSuccess;
            case 3:
                if (logEvent(args)) {
                    return MutantMessages.sSuccess;
                }
            default:
                return MutantMessages.sFail;
        }
    }
}
