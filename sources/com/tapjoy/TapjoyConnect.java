package com.tapjoy;

import android.content.Context;
import android.util.Log;

/* loaded from: classes.dex */
public final class TapjoyConnect {
    public static final String TAPJOY_CONNECT = "TapjoyConnect";
    private static TapjoyConnect tapjoyConnectInstance = null;

    public static void requestTapjoyConnect(Context context, String appID, String secretKey) {
        TapjoyConnectCore.setSDKType(TapjoyConstants.TJC_SDK_TYPE_CONNECT);
        TapjoyConnectCore.setPlugin(TapjoyConstants.TJC_PLUGIN_NATIVE);
        tapjoyConnectInstance = new TapjoyConnect(context, appID, secretKey);
    }

    public static TapjoyConnect getTapjoyConnectInstance() {
        if (tapjoyConnectInstance == null) {
            Log.e("TapjoyConnect", "----------------------------------------");
            Log.e("TapjoyConnect", "ERROR -- call requestTapjoyConnect before any other Tapjoy methods");
            Log.e("TapjoyConnect", "----------------------------------------");
        }
        return tapjoyConnectInstance;
    }

    private TapjoyConnect(Context context, String appID, String secretKey) {
        TapjoyConnectCore.requestTapjoyConnect(context, appID, secretKey);
    }

    public void enablePaidAppWithActionID(String paidAppPayPerActionID) {
        TapjoyConnectCore.getInstance().enablePaidAppWithActionID(paidAppPayPerActionID);
    }

    public void actionComplete(String actionID) {
        TapjoyConnectCore.getInstance().actionComplete(actionID);
    }
}
