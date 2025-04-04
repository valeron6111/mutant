package com.sponsorpay.sdk.android.publisher.currency;

/* loaded from: classes.dex */
public interface SPCurrencyServerListener {
    void onSPCurrencyDeltaReceived(CurrencyServerDeltaOfCoinsResponse currencyServerDeltaOfCoinsResponse);

    void onSPCurrencyServerError(CurrencyServerAbstractResponse currencyServerAbstractResponse);
}
