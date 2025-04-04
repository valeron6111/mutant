package com.sponsorpay.sdk.android.publisher.currency;

import com.sponsorpay.sdk.android.publisher.AbstractResponse;

/* loaded from: classes.dex */
public class RequestErrorResponse extends CurrencyServerAbstractResponse {
    public RequestErrorResponse() {
        this.mErrorType = AbstractResponse.RequestErrorType.ERROR_NO_INTERNET_CONNECTION;
    }

    @Override // com.sponsorpay.sdk.android.publisher.AbstractResponse
    public void parseSuccessfulResponse() {
    }

    @Override // com.sponsorpay.sdk.android.publisher.AbstractResponse
    public void invokeOnSuccessCallback() {
    }
}
