package com.alawar.mutant.billing;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.alawar.mutant.billing.Consts;
import com.alawar.mutant.billing.Security;
import com.android.vending.billing.IMarketBillingService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class BillingService extends Service implements ServiceConnection {
    private static final String TAG = "BillingService";
    private static LinkedList<BillingRequest> mPendingRequests = new LinkedList<>();
    private static HashMap<Long, BillingRequest> mSentRequests = new HashMap<>();
    private static IMarketBillingService mService;

    abstract class BillingRequest {
        protected long mRequestId;
        private final int mStartId;

        protected abstract long run() throws RemoteException;

        public BillingRequest(int startId) {
            this.mStartId = startId;
        }

        public int getStartId() {
            return this.mStartId;
        }

        public boolean runRequest() {
            if (runIfConnected()) {
                return true;
            }
            if (BillingService.this.bindToMarketBillingService()) {
                BillingService.mPendingRequests.add(this);
                Log.d(BillingService.TAG, "Pending request count: " + BillingService.mPendingRequests.size());
                return true;
            }
            return false;
        }

        public boolean runIfConnected() {
            Log.d(BillingService.TAG, getClass().getSimpleName());
            if (BillingService.mService != null) {
                try {
                    this.mRequestId = run();
                    Log.d(BillingService.TAG, "request id: " + this.mRequestId);
                    if (this.mRequestId >= 0) {
                        BillingService.mSentRequests.put(Long.valueOf(this.mRequestId), this);
                        Log.d(BillingService.TAG, "Sent request count: " + BillingService.mSentRequests.size());
                    }
                    return true;
                } catch (RemoteException e) {
                    onRemoteException(e);
                }
            }
            return false;
        }

        protected void onRemoteException(RemoteException e) {
            Log.w(BillingService.TAG, "remote billing service crashed");
            IMarketBillingService unused = BillingService.mService = null;
        }

        protected void responseCodeReceived(Consts.ResponseCode responseCode) {
        }

        protected Bundle makeRequestBundle(String method) {
            Bundle request = new Bundle();
            request.putString(Consts.BILLING_REQUEST_METHOD, method);
            request.putInt(Consts.BILLING_REQUEST_API_VERSION, 1);
            request.putString(Consts.BILLING_REQUEST_PACKAGE_NAME, BillingService.this.getPackageName());
            return request;
        }

        protected void logResponseCode(String method, Bundle response) {
            Consts.ResponseCode responseCode = Consts.ResponseCode.valueOf(response.getInt(Consts.BILLING_RESPONSE_RESPONSE_CODE));
            Log.e(BillingService.TAG, method + " received " + responseCode.toString());
        }
    }

    class CheckBillingSupported extends BillingRequest {
        public CheckBillingSupported() {
            super(-1);
        }

        @Override // com.alawar.mutant.billing.BillingService.BillingRequest
        protected long run() throws RemoteException {
            Bundle request = makeRequestBundle("CHECK_BILLING_SUPPORTED");
            Bundle response = BillingService.mService.sendBillingRequest(request);
            int responseCode = response.getInt(Consts.BILLING_RESPONSE_RESPONSE_CODE);
            Log.i(BillingService.TAG, "CheckBillingSupported response code: " + Consts.ResponseCode.valueOf(responseCode));
            boolean billingSupported = responseCode == Consts.ResponseCode.RESULT_OK.ordinal();
            ResponseHandler.checkBillingSupportedResponse(billingSupported);
            return Consts.BILLING_RESPONSE_INVALID_REQUEST_ID;
        }
    }

    class RequestPurchase extends BillingRequest {
        public final String mDeveloperPayload;
        public final String mProductId;

        public RequestPurchase(BillingService billingService, String itemId) {
            this(itemId, null);
        }

        public RequestPurchase(String itemId, String developerPayload) {
            super(-1);
            this.mProductId = itemId;
            this.mDeveloperPayload = developerPayload;
        }

        @Override // com.alawar.mutant.billing.BillingService.BillingRequest
        protected long run() throws RemoteException {
            Bundle request = makeRequestBundle("REQUEST_PURCHASE");
            request.putString(Consts.BILLING_REQUEST_ITEM_ID, this.mProductId);
            if (this.mDeveloperPayload != null) {
                request.putString(Consts.BILLING_REQUEST_DEVELOPER_PAYLOAD, this.mDeveloperPayload);
            }
            Bundle response = BillingService.mService.sendBillingRequest(request);
            PendingIntent pendingIntent = (PendingIntent) response.getParcelable(Consts.BILLING_RESPONSE_PURCHASE_INTENT);
            if (pendingIntent == null) {
                Log.e(BillingService.TAG, "Error with requestPurchase");
                return Consts.BILLING_RESPONSE_INVALID_REQUEST_ID;
            }
            Intent intent = new Intent();
            ResponseHandler.buyPageIntentResponse(pendingIntent, intent);
            return response.getLong(Consts.BILLING_RESPONSE_REQUEST_ID, Consts.BILLING_RESPONSE_INVALID_REQUEST_ID);
        }

        @Override // com.alawar.mutant.billing.BillingService.BillingRequest
        protected void responseCodeReceived(Consts.ResponseCode responseCode) {
            ResponseHandler.responseCodeReceived(BillingService.this, this, responseCode);
        }
    }

    class ConfirmNotifications extends BillingRequest {
        final String[] mNotifyIds;

        public ConfirmNotifications(int startId, String[] notifyIds) {
            super(startId);
            this.mNotifyIds = notifyIds;
        }

        @Override // com.alawar.mutant.billing.BillingService.BillingRequest
        protected long run() throws RemoteException {
            Bundle request = makeRequestBundle("CONFIRM_NOTIFICATIONS");
            request.putStringArray(Consts.BILLING_REQUEST_NOTIFY_IDS, this.mNotifyIds);
            Bundle response = BillingService.mService.sendBillingRequest(request);
            logResponseCode("confirmNotifications", response);
            return response.getLong(Consts.BILLING_RESPONSE_REQUEST_ID, Consts.BILLING_RESPONSE_INVALID_REQUEST_ID);
        }
    }

    class GetPurchaseInformation extends BillingRequest {
        long mNonce;
        final String[] mNotifyIds;

        public GetPurchaseInformation(int startId, String[] notifyIds) {
            super(startId);
            this.mNotifyIds = notifyIds;
        }

        @Override // com.alawar.mutant.billing.BillingService.BillingRequest
        protected long run() throws RemoteException {
            this.mNonce = Security.generateNonce();
            Bundle request = makeRequestBundle("GET_PURCHASE_INFORMATION");
            request.putLong(Consts.BILLING_REQUEST_NONCE, this.mNonce);
            request.putStringArray(Consts.BILLING_REQUEST_NOTIFY_IDS, this.mNotifyIds);
            Bundle response = BillingService.mService.sendBillingRequest(request);
            logResponseCode("getPurchaseInformation", response);
            return response.getLong(Consts.BILLING_RESPONSE_REQUEST_ID, Consts.BILLING_RESPONSE_INVALID_REQUEST_ID);
        }

        @Override // com.alawar.mutant.billing.BillingService.BillingRequest
        protected void onRemoteException(RemoteException e) {
            super.onRemoteException(e);
            Security.removeNonce(this.mNonce);
        }
    }

    class RestoreTransactions extends BillingRequest {
        long mNonce;

        public RestoreTransactions() {
            super(-1);
        }

        @Override // com.alawar.mutant.billing.BillingService.BillingRequest
        protected long run() throws RemoteException {
            this.mNonce = Security.generateNonce();
            Bundle request = makeRequestBundle("RESTORE_TRANSACTIONS");
            request.putLong(Consts.BILLING_REQUEST_NONCE, this.mNonce);
            Bundle response = BillingService.mService.sendBillingRequest(request);
            logResponseCode("restoreTransactions", response);
            return response.getLong(Consts.BILLING_RESPONSE_REQUEST_ID, Consts.BILLING_RESPONSE_INVALID_REQUEST_ID);
        }

        @Override // com.alawar.mutant.billing.BillingService.BillingRequest
        protected void onRemoteException(RemoteException e) {
            super.onRemoteException(e);
            Security.removeNonce(this.mNonce);
        }

        @Override // com.alawar.mutant.billing.BillingService.BillingRequest
        protected void responseCodeReceived(Consts.ResponseCode responseCode) {
            ResponseHandler.responseCodeReceived(BillingService.this, this, responseCode);
        }
    }

    public void setContext(Context context) {
        attachBaseContext(context);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onStart(Intent intent, int startId) {
        handleCommand(intent, startId);
    }

    public void handleCommand(Intent intent, int startId) {
        if (intent == null) {
            Log.w(TAG, "handleCommand() null intent");
            return;
        }
        String action = intent.getAction();
        Log.i(TAG, "handleCommand() action: " + action);
        if (Consts.ACTION_CONFIRM_NOTIFICATION.equals(action)) {
            String[] notifyIds = intent.getStringArrayExtra(Consts.NOTIFICATION_ID);
            confirmNotifications(startId, notifyIds);
            return;
        }
        if (Consts.ACTION_GET_PURCHASE_INFORMATION.equals(action)) {
            String notifyId = intent.getStringExtra(Consts.NOTIFICATION_ID);
            getPurchaseInformation(startId, new String[]{notifyId});
            return;
        }
        if (Consts.ACTION_PURCHASE_STATE_CHANGED.equals(action)) {
            String signedData = intent.getStringExtra(Consts.INAPP_SIGNED_DATA);
            String signature = intent.getStringExtra(Consts.INAPP_SIGNATURE);
            purchaseStateChanged(startId, signedData, signature);
        } else if (Consts.ACTION_RESPONSE_CODE.equals(action)) {
            long requestId = intent.getLongExtra(Consts.INAPP_REQUEST_ID, -1L);
            int responseCodeIndex = intent.getIntExtra(Consts.INAPP_RESPONSE_CODE, Consts.ResponseCode.RESULT_ERROR.ordinal());
            Consts.ResponseCode responseCode = Consts.ResponseCode.valueOf(responseCodeIndex);
            checkResponseCode(requestId, responseCode);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean bindToMarketBillingService() {
        boolean bindResult;
        try {
            Log.i(TAG, "binding to Market billing service");
            bindResult = bindService(new Intent(Consts.MARKET_BILLING_SERVICE_ACTION), this, 1);
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception: " + e);
        }
        if (bindResult) {
            return true;
        }
        Log.e(TAG, "Could not bind to service.");
        return false;
    }

    public boolean checkBillingSupported() {
        return new CheckBillingSupported().runRequest();
    }

    public boolean requestPurchase(String productId, String developerPayload) {
        return new RequestPurchase(productId, developerPayload).runRequest();
    }

    public boolean restoreTransactions() {
        return new RestoreTransactions().runRequest();
    }

    private boolean confirmNotifications(int startId, String[] notifyIds) {
        return new ConfirmNotifications(startId, notifyIds).runRequest();
    }

    private boolean getPurchaseInformation(int startId, String[] notifyIds) {
        return new GetPurchaseInformation(startId, notifyIds).runRequest();
    }

    private void purchaseStateChanged(int startId, String signedData, String signature) {
        ArrayList<Security.VerifiedPurchase> purchases = Security.verifyPurchase(signedData, signature);
        if (purchases != null) {
            ArrayList<String> notifyList = new ArrayList<>();
            Iterator i$ = purchases.iterator();
            while (i$.hasNext()) {
                Security.VerifiedPurchase vp = i$.next();
                if (vp.notificationId != null) {
                    notifyList.add(vp.notificationId);
                }
                ResponseHandler.purchaseResponse(this, vp.purchaseState, vp.productId, vp.orderId, vp.purchaseTime, vp.developerPayload);
            }
            if (!notifyList.isEmpty()) {
                String[] notifyIds = (String[]) notifyList.toArray(new String[notifyList.size()]);
                confirmNotifications(startId, notifyIds);
            }
        }
    }

    private void checkResponseCode(long requestId, Consts.ResponseCode responseCode) {
        BillingRequest request = mSentRequests.get(Long.valueOf(requestId));
        if (request != null) {
            Log.d(TAG, request.getClass().getSimpleName() + ": " + responseCode);
            request.responseCodeReceived(responseCode);
        }
        mSentRequests.remove(Long.valueOf(requestId));
    }

    private void runPendingRequests() {
        int maxStartId = -1;
        while (true) {
            BillingRequest request = mPendingRequests.peek();
            if (request != null) {
                if (request.runIfConnected()) {
                    mPendingRequests.remove();
                    if (maxStartId < request.getStartId()) {
                        maxStartId = request.getStartId();
                    }
                } else {
                    bindToMarketBillingService();
                    return;
                }
            } else {
                if (maxStartId >= 0) {
                    Log.i(TAG, "stopping service, startId: " + maxStartId);
                    stopSelf(maxStartId);
                    return;
                }
                return;
            }
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "Billing service connected");
        mService = IMarketBillingService.Stub.asInterface(service);
        runPendingRequests();
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
        Log.w(TAG, "Billing service disconnected");
        mService = null;
    }

    public void unbind() {
        try {
            unbindService(this);
        } catch (IllegalArgumentException e) {
        }
    }
}
