package com.alawar.mutant.thirdparty.sponsorpay;

import android.app.Activity;
import android.util.Log;
import com.alawar.mutant.Global;
import com.alawar.mutant.billing.MutantPayment;
import com.alawar.mutant.util.DeviceUUID;
import com.sponsorpay.sdk.android.publisher.OfferBanner;
import com.sponsorpay.sdk.android.publisher.OfferBannerRequest;
import com.sponsorpay.sdk.android.publisher.SPOfferBannerListener;
import com.sponsorpay.sdk.android.publisher.SponsorPayPublisher;
import com.sponsorpay.sdk.android.publisher.currency.CurrencyServerAbstractResponse;
import com.sponsorpay.sdk.android.publisher.currency.CurrencyServerDeltaOfCoinsResponse;
import com.sponsorpay.sdk.android.publisher.currency.SPCurrencyServerListener;

/* loaded from: classes.dex */
public class SponsorPayActivity extends Activity implements SPOfferBannerListener {
    public static final String APP_ID = "4804";
    private static final String SECURITY_TOKEN = "bluepointscurrency";

    @Override // com.sponsorpay.sdk.android.publisher.SPOfferBannerListener
    public void onSPOfferBannerAvailable(OfferBanner banner) {
    }

    @Override // com.sponsorpay.sdk.android.publisher.SPOfferBannerListener
    public void onSPOfferBannerNotAvailable(OfferBannerRequest request) {
    }

    @Override // com.sponsorpay.sdk.android.publisher.SPOfferBannerListener
    public void onSPOfferBannerRequestError(OfferBannerRequest request) {
    }

    public static void openOfferWall() {
        try {
            Global.applicationContext.startActivityForResult(SponsorPayPublisher.getIntentForOfferWallActivity(Global.applicationContext, DeviceUUID.getUuid(), false, APP_ID), SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE);
        } catch (RuntimeException ex) {
            Log.e("SponsorPay", "SponsorPay SDK Exception: ", ex);
        }
    }

    public static void requestNewCoins() {
        SponsorPayPublisher.requestNewCoins(Global.applicationContext, DeviceUUID.getUuid(), new SPCurrencyServerListener() { // from class: com.alawar.mutant.thirdparty.sponsorpay.SponsorPayActivity.1
            @Override // com.sponsorpay.sdk.android.publisher.currency.SPCurrencyServerListener
            public void onSPCurrencyServerError(CurrencyServerAbstractResponse response) {
            }

            @Override // com.sponsorpay.sdk.android.publisher.currency.SPCurrencyServerListener
            public void onSPCurrencyDeltaReceived(CurrencyServerDeltaOfCoinsResponse response) {
                if (response.getDeltaOfCoins() >= 1.0d) {
                    String id = "sponsorpay_" + response.getLatestTransactionId();
                    int coins = (int) response.getDeltaOfCoins();
                    Log.i("MutantPayment", String.format("SponsorPay coins (%s): %d", id, Integer.valueOf(coins)));
                    MutantPayment.makePayment(id, coins, "sponsorpay");
                }
            }
        }, null, SECURITY_TOKEN, APP_ID);
    }
}
