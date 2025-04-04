package com.sponsorpay.sdk.android.publisher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.alawar.mutant.jni.MutantMessages;
import com.sponsorpay.sdk.android.HostInfo;
import com.sponsorpay.sdk.android.UrlBuilder;
import com.sponsorpay.sdk.android.publisher.AsyncRequest;
import com.sponsorpay.sdk.android.publisher.OfferBanner;
import java.util.Map;

/* loaded from: classes.dex */
public class OfferBannerRequest implements AsyncRequest.AsyncRequestResultListener {
    private static final String OFFERBANNER_PRODUCTION_BASE_URL = "http://iframe.sponsorpay.com/mobile";
    private static final String OFFERBANNER_PRODUCTION_DOMAIN = "http://iframe.sponsorpay.com";
    private static final String OFFERBANNER_STAGING_BASE_URL = "http://staging.iframe.sponsorpay.com/mobile";
    private static final String OFFERBANNER_STAGING_DOMAIN = "http://staging.iframe.sponsorpay.com";
    private static final String STATE_OFFSET_COUNT_KEY = "OFFERBANNER_AVAILABLE_RESPONSE_COUNT";
    private static final String URL_PARAM_OFFERBANNER_KEY = "banner";
    private AsyncRequest mAsyncRequest;
    private String mBaseDomain;
    private String mBaseUrl;
    private Context mContext;
    private String mCurrencyName;
    private Map<String, String> mCustomParams;
    private HostInfo mHostInfo;
    private SPOfferBannerListener mListener;
    private OfferBanner.AdShape mOfferBannerAdShape;
    private UserId mUserId;

    public OfferBannerRequest(Context context, String userId, HostInfo hostInfo, SPOfferBannerListener listener, OfferBanner.AdShape offerBannerAdShape, String currencyName, Map<String, String> customParams) {
        this.mContext = context;
        this.mListener = listener;
        this.mUserId = UserId.make(context, userId);
        this.mOfferBannerAdShape = offerBannerAdShape;
        this.mHostInfo = hostInfo;
        this.mCurrencyName = currencyName;
        this.mCustomParams = customParams;
        requestOfferBanner();
    }

    private void requestOfferBanner() {
        String[] offerBannerUrlExtraKeys = {URL_PARAM_OFFERBANNER_KEY, UrlBuilder.URL_PARAM_ALLOW_CAMPAIGN_KEY, UrlBuilder.URL_PARAM_OFFSET_KEY};
        String[] offerBannerUrlExtraValues = {UrlBuilder.URL_PARAM_VALUE_ON, UrlBuilder.URL_PARAM_VALUE_ON, String.valueOf(fetchPersistedBannerOffset())};
        Map<String, String> extraKeysValues = UrlBuilder.mapKeysToValues(offerBannerUrlExtraKeys, offerBannerUrlExtraValues);
        if (this.mCurrencyName != null && !MutantMessages.sEmpty.equals(this.mCurrencyName)) {
            extraKeysValues.put(UrlBuilder.URL_PARAM_CURRENCY_NAME_KEY, this.mCurrencyName);
        }
        if (this.mCustomParams != null) {
            extraKeysValues.putAll(this.mCustomParams);
        }
        if (SponsorPayPublisher.shouldUseStagingUrls()) {
            this.mBaseUrl = OFFERBANNER_STAGING_BASE_URL;
            this.mBaseDomain = OFFERBANNER_STAGING_DOMAIN;
        } else {
            this.mBaseUrl = OFFERBANNER_PRODUCTION_BASE_URL;
            this.mBaseDomain = OFFERBANNER_PRODUCTION_DOMAIN;
        }
        String offerBannerUrl = UrlBuilder.buildUrl(this.mBaseUrl, this.mUserId.toString(), this.mHostInfo, extraKeysValues);
        Log.i(OfferBanner.LOG_TAG, "Offer Banner Request URL: " + offerBannerUrl);
        this.mAsyncRequest = new AsyncRequest(offerBannerUrl, this);
        this.mAsyncRequest.execute(new Void[0]);
    }

    @Override // com.sponsorpay.sdk.android.publisher.AsyncRequest.AsyncRequestResultListener
    public void onAsyncRequestComplete(AsyncRequest request) {
        Log.i(OfferBanner.LOG_TAG, "onAsyncRequestComplete, returned status code: " + request.getHttpStatusCode());
        if (this.mAsyncRequest.hasSucessfulStatusCode()) {
            OfferBanner banner = new OfferBanner(this.mContext, this.mBaseDomain, this.mAsyncRequest.getResponseBody(), this.mAsyncRequest.getCookieStrings(), this.mOfferBannerAdShape);
            incrementPersistedBannerOffset();
            this.mListener.onSPOfferBannerAvailable(banner);
        } else if (this.mAsyncRequest.didRequestThrowError()) {
            this.mListener.onSPOfferBannerRequestError(this);
        } else {
            this.mListener.onSPOfferBannerNotAvailable(this);
        }
    }

    private void incrementPersistedBannerOffset() {
        SharedPreferences prefs = fetchSharedPreferences();
        int bannerOffset = fetchPersistedBannerOffset(prefs);
        prefs.edit().putInt(STATE_OFFSET_COUNT_KEY, bannerOffset + 1).commit();
    }

    private SharedPreferences fetchSharedPreferences() {
        return this.mContext.getSharedPreferences(SponsorPayPublisher.PREFERENCES_FILENAME, 0);
    }

    private int fetchPersistedBannerOffset() {
        return fetchPersistedBannerOffset(fetchSharedPreferences());
    }

    private int fetchPersistedBannerOffset(SharedPreferences prefs) {
        return prefs.getInt(STATE_OFFSET_COUNT_KEY, 0);
    }

    public int getHttpStatusCode() {
        if (this.mAsyncRequest != null) {
            return this.mAsyncRequest.getHttpStatusCode();
        }
        return -1;
    }

    public Exception getRequestException() {
        Throwable requestError;
        if (this.mAsyncRequest == null || (requestError = this.mAsyncRequest.getRequestThrownError()) == null || !Exception.class.isAssignableFrom(requestError.getClass())) {
            return null;
        }
        Exception retval = (Exception) requestError;
        return retval;
    }

    public Throwable getRequestThrownError() {
        if (this.mAsyncRequest != null) {
            return this.mAsyncRequest.getRequestThrownError();
        }
        return null;
    }
}
