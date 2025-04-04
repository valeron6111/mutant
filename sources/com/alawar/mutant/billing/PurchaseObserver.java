package com.alawar.mutant.billing;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Handler;
import android.util.Log;
import com.alawar.mutant.billing.BillingService;
import com.alawar.mutant.billing.Consts;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public abstract class PurchaseObserver {
    private static final Class[] START_INTENT_SENDER_SIG = {IntentSender.class, Intent.class, Integer.TYPE, Integer.TYPE, Integer.TYPE};
    private static final String TAG = "PurchaseObserver";
    private final Activity mActivity;
    private final Handler mHandler;
    private Method mStartIntentSender;
    private Object[] mStartIntentSenderArgs = new Object[5];

    public abstract void onBillingSupported(boolean z);

    public abstract void onPurchaseStateChange(Consts.PurchaseState purchaseState, String str, int i, long j, String str2);

    public abstract void onRequestPurchaseResponse(BillingService.RequestPurchase requestPurchase, Consts.ResponseCode responseCode);

    public abstract void onRestoreTransactionsResponse(BillingService.RestoreTransactions restoreTransactions, Consts.ResponseCode responseCode);

    public PurchaseObserver(Activity activity, Handler handler) {
        this.mActivity = activity;
        this.mHandler = handler;
        initCompatibilityLayer();
    }

    private void initCompatibilityLayer() {
        try {
            this.mStartIntentSender = this.mActivity.getClass().getMethod("startIntentSender", START_INTENT_SENDER_SIG);
        } catch (NoSuchMethodException e) {
            this.mStartIntentSender = null;
        } catch (SecurityException e2) {
            this.mStartIntentSender = null;
        }
    }

    void startBuyPageActivity(PendingIntent pendingIntent, Intent intent) {
        if (this.mStartIntentSender != null) {
            try {
                this.mStartIntentSenderArgs[0] = pendingIntent.getIntentSender();
                this.mStartIntentSenderArgs[1] = intent;
                this.mStartIntentSenderArgs[2] = 0;
                this.mStartIntentSenderArgs[3] = 0;
                this.mStartIntentSenderArgs[4] = 0;
                this.mStartIntentSender.invoke(this.mActivity, this.mStartIntentSenderArgs);
                return;
            } catch (Exception e) {
                Log.e(TAG, "error starting activity", e);
                return;
            }
        }
        try {
            pendingIntent.send(this.mActivity, 0, intent);
        } catch (PendingIntent.CanceledException e2) {
            Log.e(TAG, "error starting activity", e2);
        }
    }

    void postPurchaseStateChange(final Consts.PurchaseState purchaseState, final String itemId, final int quantity, final long purchaseTime, final String developerPayload) {
        this.mHandler.post(new Runnable() { // from class: com.alawar.mutant.billing.PurchaseObserver.1
            @Override // java.lang.Runnable
            public void run() {
                PurchaseObserver.this.onPurchaseStateChange(purchaseState, itemId, quantity, purchaseTime, developerPayload);
            }
        });
    }
}
