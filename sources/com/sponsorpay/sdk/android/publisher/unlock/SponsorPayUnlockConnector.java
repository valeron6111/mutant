package com.sponsorpay.sdk.android.publisher.unlock;

import android.content.Context;
import android.util.Log;
import com.sponsorpay.sdk.android.HostInfo;
import com.sponsorpay.sdk.android.UrlBuilder;
import com.sponsorpay.sdk.android.publisher.AbstractConnector;
import com.sponsorpay.sdk.android.publisher.AbstractResponse;
import com.sponsorpay.sdk.android.publisher.AsyncRequest;
import com.sponsorpay.sdk.android.publisher.SponsorPayPublisher;
import com.tapjoy.TapjoyConstants;
import java.util.Map;

/* loaded from: classes.dex */
public class SponsorPayUnlockConnector extends AbstractConnector implements SPUnlockResponseListener {
    private static final String SP_UNLOCK_REQUEST_RESOURCE = "items.json";
    private static final String SP_UNLOCK_SERVER_PRODUCTION_BASE_URL = "http://api.sponsorpay.com/vcs/v1/";
    private static final String SP_UNLOCK_SERVER_STAGING_BASE_URL = "http://staging.iframe.sponsorpay.com/vcs/v1/";
    private SPUnlockResponseListener mUserListener;

    public SponsorPayUnlockConnector(Context context, String userId, SPUnlockResponseListener userListener, HostInfo hostInfo, String securityToken) {
        super(context, userId, hostInfo, securityToken);
        this.mUserListener = userListener;
    }

    public void fetchItemsStatus() {
        String[] requestUrlExtraKeys = {TapjoyConstants.TJC_TIMESTAMP};
        String[] requestUrlExtraValues = {getCurrentUnixTimestampAsString()};
        Map<String, String> extraKeysValues = UrlBuilder.mapKeysToValues(requestUrlExtraKeys, requestUrlExtraValues);
        if (this.mCustomParameters != null) {
            extraKeysValues.putAll(this.mCustomParameters);
        }
        String baseUrl = SponsorPayPublisher.shouldUseStagingUrls() ? SP_UNLOCK_SERVER_STAGING_BASE_URL : SP_UNLOCK_SERVER_PRODUCTION_BASE_URL;
        String requestUrl = UrlBuilder.buildUrl(baseUrl + SP_UNLOCK_REQUEST_RESOURCE, this.mUserId.toString(), this.mHostInfo, extraKeysValues, this.mSecurityToken);
        Log.d(getClass().getSimpleName(), "Unlock items status request will be sent to URL + params: " + requestUrl);
        AsyncRequest requestTask = new AsyncRequest(requestUrl, this);
        requestTask.execute(new Void[0]);
    }

    @Override // com.sponsorpay.sdk.android.publisher.AsyncRequest.AsyncRequestResultListener
    public void onAsyncRequestComplete(AsyncRequest requestTask) {
        Log.d(getClass().getSimpleName(), String.format("SP Unlock server Response, status code: %d, response body: %s, signature: %s", Integer.valueOf(requestTask.getHttpStatusCode()), requestTask.getResponseBody(), requestTask.getResponseSignature()));
        UnlockedItemsResponse response = new UnlockedItemsResponse();
        if (requestTask.didRequestThrowError()) {
            response.setErrorType(AbstractResponse.RequestErrorType.ERROR_NO_INTERNET_CONNECTION);
        } else {
            response.setResponseData(requestTask.getHttpStatusCode(), requestTask.getResponseBody(), requestTask.getResponseSignature());
        }
        response.setResponseListener(this);
        response.parseAndCallListener(this.mSecurityToken);
    }

    @Override // com.sponsorpay.sdk.android.publisher.unlock.SPUnlockResponseListener
    public void onSPUnlockRequestError(AbstractResponse response) {
        this.mUserListener.onSPUnlockRequestError(response);
    }

    @Override // com.sponsorpay.sdk.android.publisher.unlock.SPUnlockResponseListener
    public void onSPUnlockItemsStatusResponseReceived(UnlockedItemsResponse response) {
        this.mUserListener.onSPUnlockItemsStatusResponseReceived(response);
    }
}
