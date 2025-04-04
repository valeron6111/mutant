package com.sponsorpay.sdk.android.advertiser;

import android.content.Context;
import com.alawar.mutant.jni.MutantMessages;
import com.sponsorpay.sdk.android.HostInfo;
import com.sponsorpay.sdk.android.UrlBuilder;
import com.sponsorpay.sdk.android.advertiser.AdvertiserCallbackSender;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class SponsorPayAdvertiser implements AdvertiserCallbackSender.APIResultListener {
    private static SponsorPayAdvertiser mInstance;
    private static Map<String, String> sCustomParameters;
    private static boolean sShouldUseStagingUrls = false;
    private AdvertiserCallbackSender mAPICaller;
    private Context mContext;
    private HostInfo mHostInfo;
    private SponsorPayAdvertiserState mPersistedState;

    public static void setShouldUseStagingUrls(boolean value) {
        sShouldUseStagingUrls = value;
    }

    public static boolean shouldUseStagingUrls() {
        return sShouldUseStagingUrls;
    }

    public static void setCustomParameters(Map<String, String> params) {
        sCustomParameters = params;
    }

    public static void setCustomParameters(String[] keys, String[] values) {
        sCustomParameters = UrlBuilder.mapKeysToValues(keys, values);
    }

    public static void clearCustomParameters() {
        sCustomParameters = null;
    }

    private static HashMap<String, String> getCustomParameters(Map<String, String> passedParameters) {
        if (passedParameters != null) {
            HashMap<String, String> retval = new HashMap<>(passedParameters);
            return retval;
        }
        if (sCustomParameters != null) {
            HashMap<String, String> retval2 = new HashMap<>(sCustomParameters);
            return retval2;
        }
        return null;
    }

    private SponsorPayAdvertiser(Context context) {
        this.mContext = context;
        this.mPersistedState = new SponsorPayAdvertiserState(this.mContext);
    }

    public static void register(Context context) {
        register(context, null, null);
    }

    public static void registerWithDelay(Context context, int delayMin) {
        registerWithDelay(context, delayMin, null, null);
    }

    public static void registerWithDelay(Context context, int delayMin, String overrideAppId) {
        registerWithDelay(context, delayMin, overrideAppId, null);
    }

    public static void registerWithDelay(Context context, int delayMin, String overrideAppId, Map<String, String> customParams) {
        SponsorPayCallbackDelayer.callWithDelay(context, overrideAppId, delayMin, getCustomParameters(customParams));
    }

    public static void register(Context context, String overrideAppId) {
        register(context, overrideAppId, null);
    }

    public static void register(Context context, String overrideAppId, Map<String, String> customParams) {
        if (mInstance == null) {
            mInstance = new SponsorPayAdvertiser(context);
        }
        mInstance.register(overrideAppId, getCustomParameters(customParams));
    }

    private void register(String overrideAppId, Map<String, String> customParams) {
        this.mHostInfo = new HostInfo(this.mContext);
        if (overrideAppId != null && !overrideAppId.equals(MutantMessages.sEmpty)) {
            this.mHostInfo.setOverriddenAppId(overrideAppId);
        }
        boolean gotSuccessfulResponseYet = this.mPersistedState.getHasAdvertiserCallbackReceivedSuccessfulResponse();
        this.mAPICaller = new AdvertiserCallbackSender(this.mHostInfo, this);
        this.mAPICaller.setWasAlreadySuccessful(gotSuccessfulResponseYet);
        this.mAPICaller.setInstallSubId(this.mPersistedState.getInstallSubId());
        this.mAPICaller.setCustomParams(customParams);
        this.mAPICaller.trigger();
    }

    @Override // com.sponsorpay.sdk.android.advertiser.AdvertiserCallbackSender.APIResultListener
    public void onAPIResponse(boolean wasSuccessful) {
        if (wasSuccessful) {
            this.mPersistedState.setHasAdvertiserCallbackReceivedSuccessfulResponse(true);
        }
    }
}
