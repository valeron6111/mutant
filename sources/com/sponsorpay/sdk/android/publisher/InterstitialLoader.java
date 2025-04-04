package com.sponsorpay.sdk.android.publisher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.alawar.mutant.jni.MutantMessages;
import com.sponsorpay.sdk.android.HostInfo;
import com.sponsorpay.sdk.android.UrlBuilder;
import com.sponsorpay.sdk.android.publisher.AsyncRequest;
import com.sponsorpay.sdk.android.publisher.SponsorPayPublisher;
import java.util.Map;

/* loaded from: classes.dex */
public class InterstitialLoader implements AsyncRequest.AsyncRequestResultListener {
    private static final String INTERSTITIAL_PRODUCTION_BASE_URL = "http://iframe.sponsorpay.com/mobile";
    private static final String INTERSTITIAL_PRODUCTION_DOMAIN = "http://iframe.sponsorpay.com";
    private static final String INTERSTITIAL_STAGING_BASE_URL = "http://staging.iframe.sponsorpay.com/mobile";
    private static final String INTERSTITIAL_STAGING_DOMAIN = "http://staging.iframe.sponsorpay.com";
    private static final int LOADING_TIMEOUT_SECONDS_DEFAULT = 5;
    public static final String LOG_TAG = "InterstitialLoader";
    private static final int MILLISECONDS_IN_SECOND = 1000;
    private static final boolean SHOULD_INTERSTITIAL_REMAIN_OPEN_DEFAULT = false;
    private static final String SKIN_NAME_DEFAULT = "DEFAULT";
    private static final String URL_PARAM_BACKGROUND_KEY = "background";
    private static final String URL_PARAM_INTERSTITIAL_KEY = "interstitial";
    private static final String URL_PARAM_SKIN_KEY = "skin";
    private static int sInterstitialAvailableResponseCount = 0;
    private AsyncRequest mAsyncRequest;
    private Activity mCallingActivity;
    private Runnable mCancelLoadingOnTimeOut;
    private Map<String, String> mCustomParams;
    private HostInfo mHostInfo;
    private InterstitialLoadingStatusListener mLoadingStatusListener;
    private ProgressDialog mProgressDialog;
    private UserId mUserId;
    private String mBackgroundUrl = MutantMessages.sEmpty;
    private String mSkinName = SKIN_NAME_DEFAULT;
    private boolean mShouldStayOpen = false;
    private int mLoadingTimeoutSecs = 5;
    private Handler mHandler = new Handler();

    public interface InterstitialLoadingStatusListener {
        void onInterstitialLoadingTimeOut();

        void onInterstitialRequestError();

        void onNoInterstitialAvailable();

        void onWillShowInterstitial();
    }

    public InterstitialLoader(Activity callingActivity, String userId, HostInfo hostInfo, InterstitialLoadingStatusListener loadingStatusListener) {
        this.mCallingActivity = callingActivity;
        this.mUserId = UserId.make(callingActivity.getApplicationContext(), userId);
        this.mHostInfo = hostInfo;
        this.mLoadingStatusListener = loadingStatusListener;
    }

    public void setCustomParameters(Map<String, String> customParams) {
        this.mCustomParams = customParams;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.mBackgroundUrl = backgroundUrl;
    }

    public void setShouldStayOpen(boolean shouldStayOpen) {
        this.mShouldStayOpen = shouldStayOpen;
    }

    public void setSkinName(String skinName) {
        this.mSkinName = skinName;
    }

    public void setLoadingTimeoutSecs(int loadingTimeoutSecs) {
        if (loadingTimeoutSecs <= 0) {
            loadingTimeoutSecs = 5;
        }
        this.mLoadingTimeoutSecs = loadingTimeoutSecs;
    }

    public void startLoading() {
        cancelInterstitialLoading();
        String[] interstitialUrlExtraKeys = {URL_PARAM_INTERSTITIAL_KEY, UrlBuilder.URL_PARAM_ALLOW_CAMPAIGN_KEY, UrlBuilder.URL_PARAM_OFFSET_KEY};
        String[] interstitialUrlExtraValues = {UrlBuilder.URL_PARAM_VALUE_ON, UrlBuilder.URL_PARAM_VALUE_ON, String.valueOf(sInterstitialAvailableResponseCount)};
        Map<String, String> keysValues = UrlBuilder.mapKeysToValues(interstitialUrlExtraKeys, interstitialUrlExtraValues);
        if (this.mSkinName != null && !MutantMessages.sEmpty.equals(this.mSkinName)) {
            keysValues.put(URL_PARAM_SKIN_KEY, this.mSkinName);
        }
        if (this.mBackgroundUrl != null && !MutantMessages.sEmpty.equals(this.mBackgroundUrl)) {
            keysValues.put(URL_PARAM_BACKGROUND_KEY, this.mBackgroundUrl);
        }
        if (this.mCustomParams != null) {
            keysValues.putAll(this.mCustomParams);
        }
        String interstitialBaseUrl = SponsorPayPublisher.shouldUseStagingUrls() ? INTERSTITIAL_STAGING_BASE_URL : INTERSTITIAL_PRODUCTION_BASE_URL;
        String interstitialUrl = UrlBuilder.buildUrl(interstitialBaseUrl, this.mUserId.toString(), this.mHostInfo, keysValues);
        Log.i(URL_PARAM_INTERSTITIAL_KEY, "url: " + interstitialUrl);
        this.mAsyncRequest = new AsyncRequest(interstitialUrl, this);
        this.mAsyncRequest.execute(new Void[0]);
        if (this.mCancelLoadingOnTimeOut != null) {
            this.mHandler.removeCallbacks(this.mCancelLoadingOnTimeOut);
            this.mCancelLoadingOnTimeOut = null;
        }
        this.mCancelLoadingOnTimeOut = new Runnable() { // from class: com.sponsorpay.sdk.android.publisher.InterstitialLoader.1
            @Override // java.lang.Runnable
            public void run() {
                InterstitialLoader.this.cancelInterstitialLoading();
                if (InterstitialLoader.this.mLoadingStatusListener != null) {
                    InterstitialLoader.this.mLoadingStatusListener.onInterstitialLoadingTimeOut();
                }
            }
        };
        this.mHandler.postDelayed(this.mCancelLoadingOnTimeOut, this.mLoadingTimeoutSecs * MILLISECONDS_IN_SECOND);
        this.mProgressDialog = new ProgressDialog(this.mCallingActivity);
        this.mProgressDialog.setOwnerActivity(this.mCallingActivity);
        this.mProgressDialog.setIndeterminate(true);
        this.mProgressDialog.setMessage(SponsorPayPublisher.getUIString(SponsorPayPublisher.UIStringIdentifier.LOADING_INTERSTITIAL));
        this.mProgressDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelInterstitialLoading() {
        if (this.mAsyncRequest != null && !this.mAsyncRequest.isCancelled()) {
            this.mAsyncRequest.cancel(false);
        }
        dismissProgressDialog();
        this.mAsyncRequest = null;
    }

    private void dismissProgressDialog() {
        Activity ownerActivity;
        if (this.mProgressDialog != null && this.mProgressDialog.isShowing() && (ownerActivity = this.mProgressDialog.getOwnerActivity()) != null && !ownerActivity.isFinishing()) {
            try {
                this.mProgressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                Log.w(LOG_TAG, "Exception thrown when closing progress dialog.  Changing configurations: " + ownerActivity.getChangingConfigurations(), e);
            }
        }
        this.mProgressDialog = null;
    }

    private void launchInterstitialActivity(AsyncRequest request) {
        Intent interstitialIntent = new Intent(this.mCallingActivity, (Class<?>) InterstitialActivity.class);
        interstitialIntent.putExtra(InterstitialActivity.EXTRA_INITIAL_CONTENT_KEY, request.getResponseBody());
        interstitialIntent.putExtra(InterstitialActivity.EXTRA_COOKIESTRINGS_KEY, request.getCookieStrings());
        interstitialIntent.putExtra("EXTRA_SHOULD_REMAIN_OPEN_KEY", this.mShouldStayOpen);
        String interstitialDomain = SponsorPayPublisher.shouldUseStagingUrls() ? INTERSTITIAL_STAGING_DOMAIN : INTERSTITIAL_PRODUCTION_DOMAIN;
        interstitialIntent.putExtra(InterstitialActivity.EXTRA_BASE_DOMAIN_KEY, interstitialDomain);
        this.mCallingActivity.startActivity(interstitialIntent);
    }

    @Override // com.sponsorpay.sdk.android.publisher.AsyncRequest.AsyncRequestResultListener
    public void onAsyncRequestComplete(AsyncRequest request) {
        Log.v(LOG_TAG, "Interstitial request completed with status code: " + request.getHttpStatusCode() + ", did trigger exception: " + request.didRequestThrowError());
        if (this.mCancelLoadingOnTimeOut != null) {
            this.mHandler.removeCallbacks(this.mCancelLoadingOnTimeOut);
            this.mCancelLoadingOnTimeOut = null;
        }
        dismissProgressDialog();
        if (request.hasSucessfulStatusCode() && request.hasCookies()) {
            sInterstitialAvailableResponseCount++;
            if (this.mLoadingStatusListener != null) {
                this.mLoadingStatusListener.onWillShowInterstitial();
            }
            launchInterstitialActivity(request);
            return;
        }
        if (request.didRequestThrowError()) {
            if (this.mLoadingStatusListener != null) {
                this.mLoadingStatusListener.onInterstitialRequestError();
            }
        } else if (this.mLoadingStatusListener != null) {
            this.mLoadingStatusListener.onNoInterstitialAvailable();
        }
    }
}
