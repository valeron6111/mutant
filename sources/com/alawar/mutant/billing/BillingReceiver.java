package com.alawar.mutant.billing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.alawar.mutant.billing.Consts;

/* loaded from: classes.dex */
public class BillingReceiver extends BroadcastReceiver {
    private static final String TAG = "BillingReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Consts.ACTION_PURCHASE_STATE_CHANGED.equals(action)) {
            String signedData = intent.getStringExtra(Consts.INAPP_SIGNED_DATA);
            String signature = intent.getStringExtra(Consts.INAPP_SIGNATURE);
            purchaseStateChanged(context, signedData, signature);
        } else if (Consts.ACTION_NOTIFY.equals(action)) {
            String notifyId = intent.getStringExtra(Consts.NOTIFICATION_ID);
            Log.i(TAG, "notifyId: " + notifyId);
            notify(context, notifyId);
        } else {
            if (Consts.ACTION_RESPONSE_CODE.equals(action)) {
                long requestId = intent.getLongExtra(Consts.INAPP_REQUEST_ID, -1L);
                int responseCodeIndex = intent.getIntExtra(Consts.INAPP_RESPONSE_CODE, Consts.ResponseCode.RESULT_ERROR.ordinal());
                checkResponseCode(context, requestId, responseCodeIndex);
                return;
            }
            Log.w(TAG, "unexpected action: " + action);
        }
    }

    private void purchaseStateChanged(Context context, String signedData, String signature) {
        Intent intent = new Intent(Consts.ACTION_PURCHASE_STATE_CHANGED);
        intent.setClass(context, BillingService.class);
        intent.putExtra(Consts.INAPP_SIGNED_DATA, signedData);
        intent.putExtra(Consts.INAPP_SIGNATURE, signature);
        context.startService(intent);
    }

    private void notify(Context context, String notifyId) {
        Intent intent = new Intent(Consts.ACTION_GET_PURCHASE_INFORMATION);
        intent.setClass(context, BillingService.class);
        intent.putExtra(Consts.NOTIFICATION_ID, notifyId);
        context.startService(intent);
    }

    private void checkResponseCode(Context context, long requestId, int responseCodeIndex) {
        Intent intent = new Intent(Consts.ACTION_RESPONSE_CODE);
        intent.setClass(context, BillingService.class);
        intent.putExtra(Consts.INAPP_REQUEST_ID, requestId);
        intent.putExtra(Consts.INAPP_RESPONSE_CODE, responseCodeIndex);
        context.startService(intent);
    }
}
