package com.alawar.mutant.billing;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.alawar.mutant.billing.BillingService;
import com.alawar.mutant.billing.Consts;

/* loaded from: classes.dex */
public class ResponseHandler {
    private static final String TAG = "ResponseHandler";
    private static PurchaseObserver sPurchaseObserver;

    public static synchronized void register(PurchaseObserver observer) {
        synchronized (ResponseHandler.class) {
            sPurchaseObserver = observer;
        }
    }

    public static synchronized void unregister(PurchaseObserver observer) {
        synchronized (ResponseHandler.class) {
            sPurchaseObserver = null;
        }
    }

    public static void checkBillingSupportedResponse(boolean supported) {
        if (sPurchaseObserver != null) {
            sPurchaseObserver.onBillingSupported(supported);
        }
    }

    public static void buyPageIntentResponse(PendingIntent pendingIntent, Intent intent) {
        if (sPurchaseObserver == null) {
            Log.d(TAG, "UI is not running");
        } else {
            sPurchaseObserver.startBuyPageActivity(pendingIntent, intent);
        }
    }

    public static void purchaseResponse(final Context context, final Consts.PurchaseState purchaseState, final String productId, final String orderId, final long purchaseTime, final String developerPayload) {
        new Thread(new Runnable() { // from class: com.alawar.mutant.billing.ResponseHandler.1
            @Override // java.lang.Runnable
            public void run() {
                PurchaseDatabase db = new PurchaseDatabase(context);
                int quantity = db.updatePurchase(orderId, productId, purchaseState, purchaseTime, developerPayload);
                db.close();
                synchronized (ResponseHandler.class) {
                    if (ResponseHandler.sPurchaseObserver != null) {
                        ResponseHandler.sPurchaseObserver.postPurchaseStateChange(purchaseState, productId, quantity, purchaseTime, developerPayload);
                    }
                }
            }
        }).start();
    }

    public static void responseCodeReceived(Context context, BillingService.RequestPurchase request, Consts.ResponseCode responseCode) {
        if (sPurchaseObserver != null) {
            sPurchaseObserver.onRequestPurchaseResponse(request, responseCode);
        }
    }

    public static void responseCodeReceived(Context context, BillingService.RestoreTransactions request, Consts.ResponseCode responseCode) {
        if (sPurchaseObserver != null) {
            sPurchaseObserver.onRestoreTransactionsResponse(request, responseCode);
        }
    }
}
