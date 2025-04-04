package com.alawar.mutant.billing;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;
import com.alawar.common.event.EventBus;
import com.alawar.common.event.EventHandler;
import com.alawar.mutant.billing.BillingService;
import com.alawar.mutant.billing.Consts;
import com.alawar.mutant.database.DbBuilder;
import com.alawar.mutant.database.DbItem;
import com.alawar.mutant.jni.MutantMessages;
import com.alawar.mutant.notification.Notification;
import com.alawar.mutant.notification.NotificationsUpdatedEvent;
import com.alawar.mutant.util.DeviceUUID;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MutantPayment extends MutantMessages {
    private static final String DB_INITIALIZED = "db_initialized";
    private static final String TAG = "MutantPayment";
    public static final int queryBalance = 10;
    private static final int queryMakePayment = 12;
    private static final int queryPay150BluePoints = 2;
    private static final int queryPay500BluePoints = 3;
    private static final int queryPay50BluePoints = 1;
    private static final int queryPaymentList = 0;
    private static final int querySpendCoins = 11;
    private static final String sku150BluePoints = "150blue";
    private static final String sku500BluePoints = "500blue";
    private static final String sku50BluePoints = "50blue";
    private static final String skuTestPurchased = "android.test.purchased";
    private static Activity m_context = null;
    private static BillingService m_billingService = null;
    private static MutantPurchaseObserver m_mutantPurchaseObserver = null;
    private static DbBuilder m_paymentDatabase = null;
    private static Handler m_Handler = null;
    private static boolean m_realPaymentMode = false;
    private static boolean m_payCached = false;
    private static int m_pay = 0;

    private static native boolean isFakePayment();

    private static class MutantPurchaseObserver extends PurchaseObserver {
        public MutantPurchaseObserver(Activity activity, Handler handler) {
            super(activity, handler);
        }

        @Override // com.alawar.mutant.billing.PurchaseObserver
        public void onBillingSupported(boolean supported) {
            Log.i(MutantPayment.TAG, "supported: " + supported);
            if (supported) {
                SharedPreferences prefs = MutantPayment.m_context.getPreferences(0);
                boolean initialized = prefs.getBoolean(MutantPayment.DB_INITIALIZED, false);
                if (!initialized) {
                    MutantPayment.m_billingService.restoreTransactions();
                }
            }
        }

        @Override // com.alawar.mutant.billing.PurchaseObserver
        public void onPurchaseStateChange(Consts.PurchaseState purchaseState, String itemId, int quantity, long purchaseTime, String developerPayload) {
            Log.i(MutantPayment.TAG, "@PurchaseStateChange for itemId: " + itemId + " " + purchaseState);
            if (purchaseState == Consts.PurchaseState.PURCHASED) {
                if (MutantPayment.sku50BluePoints.equals(itemId)) {
                    MutantPayment.makePayment(50, MutantPayment.sku50BluePoints);
                    return;
                }
                if (MutantPayment.sku150BluePoints.equals(itemId)) {
                    MutantPayment.makePayment(150, MutantPayment.sku150BluePoints);
                } else if (MutantPayment.sku500BluePoints.equals(itemId)) {
                    MutantPayment.makePayment(500, MutantPayment.sku500BluePoints);
                } else {
                    Log.e(MutantPayment.TAG, "onPurchaseStateChange: Unknown product " + itemId);
                }
            }
        }

        @Override // com.alawar.mutant.billing.PurchaseObserver
        public void onRequestPurchaseResponse(BillingService.RequestPurchase request, Consts.ResponseCode responseCode) {
            Log.d(MutantPayment.TAG, request.mProductId + ": " + responseCode);
            if (responseCode == Consts.ResponseCode.RESULT_OK) {
                Log.i(MutantPayment.TAG, "purchase was successfully sent to server");
            } else if (responseCode == Consts.ResponseCode.RESULT_USER_CANCELED) {
                Log.i(MutantPayment.TAG, "user canceled purchase");
            } else {
                Log.i(MutantPayment.TAG, "purchase failed");
            }
        }

        @Override // com.alawar.mutant.billing.PurchaseObserver
        public void onRestoreTransactionsResponse(BillingService.RestoreTransactions request, Consts.ResponseCode responseCode) {
            if (responseCode == Consts.ResponseCode.RESULT_OK) {
                Log.d(MutantPayment.TAG, "completed RestoreTransactions request");
                SharedPreferences prefs = MutantPayment.m_context.getPreferences(0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(MutantPayment.DB_INITIALIZED, true);
                edit.commit();
                return;
            }
            Log.d(MutantPayment.TAG, "RestoreTransactions error: " + responseCode);
        }
    }

    public static void initialize(Activity activity) {
        m_context = activity;
        m_Handler = new Handler();
        m_mutantPurchaseObserver = new MutantPurchaseObserver(activity, m_Handler);
        m_billingService = new BillingService();
        m_billingService.setContext(activity);
        try {
            m_realPaymentMode = !isFakePayment();
        } catch (Throwable th) {
            m_realPaymentMode = false;
        }
        ensureDb();
        EventBus.addHandler(NotificationsUpdatedEvent.class, new EventHandler<NotificationsUpdatedEvent>() { // from class: com.alawar.mutant.billing.MutantPayment.1
            @Override // com.alawar.common.event.EventHandler
            public void onEvent(NotificationsUpdatedEvent event) {
                Iterator i$ = event.notifications.iterator();
                while (i$.hasNext()) {
                    Notification n = i$.next();
                    if (n.notifyType == Notification.NotifyTypes.balance) {
                        DbItem item = new DbItem(DeviceUUID.getUuid(), "balance", n.data);
                        MutantPayment.ensureDb().safeAddItem(item);
                        return;
                    }
                }
            }
        });
    }

    public static void destroy(Activity activity) {
        m_billingService.unbind();
        if (m_paymentDatabase != null) {
            m_paymentDatabase.stop();
            m_paymentDatabase = null;
        }
    }

    public static void start(Activity activity) {
        ResponseHandler.register(m_mutantPurchaseObserver);
    }

    public static void stop(Activity activity) {
        ResponseHandler.unregister(m_mutantPurchaseObserver);
    }

    static String getPays() {
        return MutantMessages.sEmpty;
    }

    static boolean requestPurchase(String productId, String developerPayload) {
        return m_billingService.requestPurchase(productId, developerPayload);
    }

    static String pay50BluePoints(String tag) {
        return requestPurchase(sku50BluePoints, tag) ? MutantMessages.sSuccess : MutantMessages.sFail;
    }

    static String pay150BluePoints(String tag) {
        return requestPurchase(sku150BluePoints, tag) ? MutantMessages.sSuccess : MutantMessages.sFail;
    }

    static String pay500BluePoints(String tag) {
        return requestPurchase(sku500BluePoints, tag) ? MutantMessages.sSuccess : MutantMessages.sFail;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static DbBuilder ensureDb() {
        if (m_paymentDatabase != null && !m_paymentDatabase.isOpen()) {
            m_paymentDatabase.stop();
            m_paymentDatabase = null;
        }
        if (m_paymentDatabase == null) {
            m_paymentDatabase = new DbBuilder(m_context, "mutant_payment");
        }
        return m_paymentDatabase;
    }

    static String balance() {
        if (!m_payCached) {
            m_pay = 0;
            Cursor cursor = ensureDb().queryAllItems();
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String v = MutantMessages.sEmpty;
                    try {
                        v = cursor.getString(cursor.getColumnIndex(DbBuilder.KEY_VALUE));
                        m_pay += Integer.valueOf(v).intValue();
                    } catch (NumberFormatException e) {
                        Log.w("balance", "invalid balance entry recorded - '" + v + "', skipping");
                    }
                }
            }
            cursor.close();
            m_payCached = true;
        }
        return String.valueOf(m_pay);
    }

    static String spendCoins(int amount, String tag) {
        if (ensureDb().addItem(new DbItem(tag, String.valueOf(-amount))) == -1) {
            return MutantMessages.sFail;
        }
        m_payCached = false;
        return MutantMessages.sSuccess;
    }

    static String makePayment(int amount, String tag) {
        if (ensureDb().addItem(new DbItem(tag, String.valueOf(amount))) == -1) {
            return MutantMessages.sFail;
        }
        m_payCached = false;
        return MutantMessages.sSuccess;
    }

    public static String makePayment(String key, int amount, String tag) {
        if (ensureDb().addItem(new DbItem(key, tag, String.valueOf(amount))) == -1) {
            return MutantMessages.sFail;
        }
        m_payCached = false;
        return MutantMessages.sSuccess;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static String payProcess(int id, String args) {
        String makePayment;
        try {
            switch (id) {
                case 0:
                    makePayment = getPays();
                    break;
                case 1:
                    makePayment = pay50BluePoints(args);
                    break;
                case 2:
                    makePayment = pay150BluePoints(args);
                    break;
                case 3:
                    makePayment = pay500BluePoints(args);
                    break;
                case 4:
                case 5:
                case 6:
                case MutantMessages.cShareWithFriends /* 7 */:
                case 8:
                case MutantMessages.cProgress /* 9 */:
                default:
                    makePayment = MutantMessages.sFail;
                    break;
                case 10:
                    makePayment = balance();
                    break;
                case 11:
                    String[] splits = args.split(":");
                    if (splits.length == 2) {
                        makePayment = spendCoins(Integer.parseInt(splits[0]), splits[1]);
                        break;
                    }
                    makePayment = MutantMessages.sFail;
                    break;
                case 12:
                    String[] ksplits = args.split("=");
                    if (ksplits.length == 2) {
                        String[] splits2 = ksplits[1].split(":");
                        if (splits2.length == 2) {
                            makePayment = makePayment(ksplits[0], Integer.parseInt(splits2[0]), splits2[1]);
                            break;
                        }
                    }
                    makePayment = MutantMessages.sFail;
                    break;
            }
            return makePayment;
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
            return e.toString();
        }
    }
}
