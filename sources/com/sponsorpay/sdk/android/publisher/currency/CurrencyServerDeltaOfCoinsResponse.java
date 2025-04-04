package com.sponsorpay.sdk.android.publisher.currency;

import com.sponsorpay.sdk.android.publisher.AbstractResponse;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class CurrencyServerDeltaOfCoinsResponse extends CurrencyServerAbstractResponse {
    private static final String DELTA_OF_COINS_KEY = "delta_of_coins";
    private static final String LATEST_TRANSACTION_ID_KEY = "latest_transaction_id";
    private double mDeltaOfCoins;
    private String mLatestTransactionId;

    public double getDeltaOfCoins() {
        return this.mDeltaOfCoins;
    }

    public String getLatestTransactionId() {
        return this.mLatestTransactionId;
    }

    @Override // com.sponsorpay.sdk.android.publisher.AbstractResponse
    public void parseSuccessfulResponse() {
        try {
            JSONObject responseBodyAsJsonObject = new JSONObject(this.mResponseBody);
            this.mDeltaOfCoins = responseBodyAsJsonObject.getDouble(DELTA_OF_COINS_KEY);
            this.mLatestTransactionId = responseBodyAsJsonObject.getString(LATEST_TRANSACTION_ID_KEY);
            this.mErrorType = AbstractResponse.RequestErrorType.NO_ERROR;
        } catch (Exception e) {
            this.mErrorType = AbstractResponse.RequestErrorType.ERROR_INVALID_RESPONSE;
        }
    }

    @Override // com.sponsorpay.sdk.android.publisher.AbstractResponse
    public void invokeOnSuccessCallback() {
        if (this.mListener != null) {
            this.mListener.onSPCurrencyDeltaReceived(this);
        }
    }
}
