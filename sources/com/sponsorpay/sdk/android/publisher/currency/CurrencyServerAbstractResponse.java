package com.sponsorpay.sdk.android.publisher.currency;

import com.sponsorpay.sdk.android.publisher.AbstractResponse;
import com.sponsorpay.sdk.android.publisher.currency.VirtualCurrencyConnector;

/* loaded from: classes.dex */
public abstract class CurrencyServerAbstractResponse extends AbstractResponse {
    protected SPCurrencyServerListener mListener;

    public void setResponseListener(SPCurrencyServerListener listener) {
        this.mListener = listener;
    }

    @Override // com.sponsorpay.sdk.android.publisher.AbstractResponse
    public void invokeOnErrorCallback() {
        if (this.mListener != null) {
            this.mListener.onSPCurrencyServerError(this);
        }
    }

    public static CurrencyServerAbstractResponse getParsingInstance(VirtualCurrencyConnector.RequestType requestType) {
        switch (requestType) {
            case DELTA_COINS:
                CurrencyServerAbstractResponse instanceToReturn = new CurrencyServerDeltaOfCoinsResponse();
                return instanceToReturn;
            default:
                return null;
        }
    }
}
