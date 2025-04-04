package com.sponsorpay.sdk.android.publisher.unlock;

import com.sponsorpay.sdk.android.publisher.AbstractResponse;

/* loaded from: classes.dex */
public interface SPUnlockResponseListener {
    void onSPUnlockItemsStatusResponseReceived(UnlockedItemsResponse unlockedItemsResponse);

    void onSPUnlockRequestError(AbstractResponse abstractResponse);
}
