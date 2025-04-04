package com.sponsorpay.sdk.android.advertiser;

import android.os.AsyncTask;
import android.util.Log;
import com.alawar.mutant.jni.MutantMessages;
import com.sponsorpay.sdk.android.HostInfo;
import com.sponsorpay.sdk.android.UrlBuilder;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

/* loaded from: classes.dex */
public class AdvertiserCallbackSender extends AsyncTask<String, Void, Boolean> {
    private static final String API_PRODUCTION_RESOURCE_URL = "http://service.sponsorpay.com/installs/v2";
    private static final String API_STAGING_RESOURCE_URL = "http://staging.sws.sponsorpay.com/installs/v2";
    private static final String INSTALL_SUBID_KEY = "subid";
    private static final int SUCCESFUL_HTTP_STATUS_CODE = 200;
    private static final String SUCCESSFUL_ANSWER_RECEIVED_KEY = "answer_received";
    private Map<String, String> mCustomParams;
    private HostInfo mHostInfo;
    private HttpClient mHttpClient;
    private HttpUriRequest mHttpRequest;
    private HttpResponse mHttpResponse;
    private String mInstallSubId;
    private APIResultListener mListener;
    private boolean mWasAlreadySuccessful = false;

    public interface APIResultListener {
        void onAPIResponse(boolean z);
    }

    public AdvertiserCallbackSender(HostInfo hostInfo, APIResultListener listener) {
        this.mListener = listener;
        this.mHostInfo = hostInfo;
    }

    public void setCustomParams(Map<String, String> customParams) {
        this.mCustomParams = customParams;
    }

    public void setInstallSubId(String subIdValue) {
        Log.d(AdvertiserCallbackSender.class.getSimpleName(), "SubID value set to " + subIdValue);
        this.mInstallSubId = subIdValue;
    }

    public void setWasAlreadySuccessful(boolean value) {
        this.mWasAlreadySuccessful = value;
    }

    public void trigger() {
        execute(buildUrl());
    }

    private String buildUrl() {
        String baseUrl = SponsorPayAdvertiser.shouldUseStagingUrls() ? API_STAGING_RESOURCE_URL : API_PRODUCTION_RESOURCE_URL;
        String[] strArr = {SUCCESSFUL_ANSWER_RECEIVED_KEY};
        String[] strArr2 = new String[1];
        strArr2[0] = this.mWasAlreadySuccessful ? "1" : "0";
        Map<String, String> extraParams = UrlBuilder.mapKeysToValues(strArr, strArr2);
        if (this.mInstallSubId != null && !MutantMessages.sEmpty.equals(this.mInstallSubId)) {
            extraParams.put(INSTALL_SUBID_KEY, this.mInstallSubId);
        }
        if (this.mCustomParams != null) {
            extraParams.putAll(this.mCustomParams);
        }
        String callbackUrl = UrlBuilder.buildUrl(baseUrl, this.mHostInfo, extraParams);
        Log.d(AdvertiserCallbackSender.class.getSimpleName(), "Advertiser callback will be sent to: " + callbackUrl);
        return callbackUrl;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Boolean doInBackground(String... params) {
        boolean returnValue;
        String callbackUrl = params[0];
        this.mHttpRequest = new HttpGet(callbackUrl);
        this.mHttpClient = new DefaultHttpClient();
        try {
            this.mHttpResponse = this.mHttpClient.execute(this.mHttpRequest);
            int responseStatusCode = this.mHttpResponse.getStatusLine().getStatusCode();
            if (responseStatusCode == SUCCESFUL_HTTP_STATUS_CODE) {
                returnValue = true;
            } else {
                returnValue = false;
            }
            Log.d(AdvertiserCallbackSender.class.getSimpleName(), "Server returned status code: " + responseStatusCode);
            return returnValue;
        } catch (Exception e) {
            Log.e(AdvertiserCallbackSender.class.getSimpleName(), "An exception occurred when trying to send advertiser callback: " + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Boolean requestWasSuccessful) {
        super.onPostExecute((AdvertiserCallbackSender) requestWasSuccessful);
        if (this.mListener != null) {
            this.mListener.onAPIResponse(requestWasSuccessful.booleanValue());
        }
    }
}
