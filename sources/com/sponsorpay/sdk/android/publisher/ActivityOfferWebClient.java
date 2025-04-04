package com.sponsorpay.sdk.android.publisher;

import android.app.Activity;
import android.util.Log;

/* loaded from: classes.dex */
public class ActivityOfferWebClient extends OfferWebClient {
    private Activity mHostActivity;
    private boolean mShouldHostActivityStayOpen;

    public ActivityOfferWebClient(Activity hostActivity, boolean shouldStayOpen) {
        this.mHostActivity = hostActivity;
        this.mShouldHostActivityStayOpen = shouldStayOpen;
    }

    @Override // com.sponsorpay.sdk.android.publisher.OfferWebClient
    protected void onSponsorPayExitScheme(int resultCode, String targetUrl) {
        boolean willCloseHostActivity;
        this.mHostActivity.setResult(resultCode);
        if (targetUrl == null) {
            willCloseHostActivity = true;
        } else {
            willCloseHostActivity = !this.mShouldHostActivityStayOpen;
            launchActivityWithUrl(this.mHostActivity, targetUrl);
        }
        Log.i(OfferWebClient.LOG_TAG, "Should stay open: " + this.mShouldHostActivityStayOpen + ", will close activity: " + willCloseHostActivity);
        if (willCloseHostActivity) {
            this.mHostActivity.finish();
        }
    }
}
