package com.sponsorpay.sdk.android.publisher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.sponsorpay.sdk.android.HostInfo;
import com.sponsorpay.sdk.android.UrlBuilder;
import com.sponsorpay.sdk.android.publisher.InterstitialLoader;
import com.sponsorpay.sdk.android.publisher.OfferBanner;
import com.sponsorpay.sdk.android.publisher.OfferWallActivity;
import com.sponsorpay.sdk.android.publisher.currency.SPCurrencyServerListener;
import com.sponsorpay.sdk.android.publisher.currency.VirtualCurrencyConnector;
import com.sponsorpay.sdk.android.publisher.unlock.ItemIdValidator;
import com.sponsorpay.sdk.android.publisher.unlock.SPUnlockResponseListener;
import com.sponsorpay.sdk.android.publisher.unlock.SponsorPayUnlockConnector;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class SponsorPayPublisher {
    public static final int DEFAULT_OFFERWALL_REQUEST_CODE = 255;
    public static final int DEFAULT_UNLOCK_OFFERWALL_REQUEST_CODE = 254;
    public static final String PREFERENCES_FILENAME = "SponsorPayPublisherState";
    private static Map<String, String> sCustomKeysValues;
    private static OfferBanner.AdShape sDefaultOfferBannerAdShape = OfferBanner.SP_AD_SHAPE_320X50;
    private static boolean sShouldUseStagingUrls = false;
    private static EnumMap<UIStringIdentifier, String> sUIStrings;

    public enum UIStringIdentifier {
        ERROR_DIALOG_TITLE,
        DISMISS_ERROR_DIALOG,
        GENERIC_ERROR,
        ERROR_LOADING_OFFERWALL,
        ERROR_LOADING_OFFERWALL_NO_INTERNET_CONNECTION,
        LOADING_INTERSTITIAL,
        LOADING_OFFERWALL
    }

    private static void initUIStrings() {
        sUIStrings = new EnumMap<>(UIStringIdentifier.class);
        sUIStrings.put((EnumMap<UIStringIdentifier, String>) UIStringIdentifier.ERROR_DIALOG_TITLE, (UIStringIdentifier) "Error");
        sUIStrings.put((EnumMap<UIStringIdentifier, String>) UIStringIdentifier.DISMISS_ERROR_DIALOG, (UIStringIdentifier) "Dismiss");
        sUIStrings.put((EnumMap<UIStringIdentifier, String>) UIStringIdentifier.GENERIC_ERROR, (UIStringIdentifier) "An error happened when performing this operation");
        sUIStrings.put((EnumMap<UIStringIdentifier, String>) UIStringIdentifier.ERROR_LOADING_OFFERWALL, (UIStringIdentifier) "An error happened when loading the offer wall");
        sUIStrings.put((EnumMap<UIStringIdentifier, String>) UIStringIdentifier.ERROR_LOADING_OFFERWALL_NO_INTERNET_CONNECTION, (UIStringIdentifier) "An error happened when loading the offer wall (no internet connection)");
        sUIStrings.put((EnumMap<UIStringIdentifier, String>) UIStringIdentifier.LOADING_INTERSTITIAL, (UIStringIdentifier) "Loading...");
        sUIStrings.put((EnumMap<UIStringIdentifier, String>) UIStringIdentifier.LOADING_OFFERWALL, (UIStringIdentifier) "Loading...");
    }

    public static String getUIString(UIStringIdentifier identifier) {
        if (sUIStrings == null) {
            initUIStrings();
        }
        return sUIStrings.get(identifier);
    }

    public static void setCustomUIString(UIStringIdentifier identifier, String message) {
        if (sUIStrings == null) {
            initUIStrings();
        }
        sUIStrings.put((EnumMap<UIStringIdentifier, String>) identifier, (UIStringIdentifier) message);
    }

    public static void setCustomUIStrings(EnumMap<UIStringIdentifier, String> messages) {
        UIStringIdentifier[] arr$ = UIStringIdentifier.values();
        for (UIStringIdentifier condition : arr$) {
            if (messages.containsKey(condition)) {
                setCustomUIString(condition, messages.get(condition));
            }
        }
    }

    public static void setCustomUIString(UIStringIdentifier identifier, int message, Context context) {
        setCustomUIString(identifier, context.getString(message));
    }

    public static void setCustomUIStrings(EnumMap<UIStringIdentifier, Integer> messages, Context context) {
        UIStringIdentifier[] arr$ = UIStringIdentifier.values();
        for (UIStringIdentifier condition : arr$) {
            if (messages.containsKey(condition)) {
                setCustomUIString(condition, messages.get(condition).intValue(), context);
            }
        }
    }

    public static void setCustomParameters(Map<String, String> params) {
        sCustomKeysValues = params;
    }

    public static void setCustomParameters(String[] keys, String[] values) {
        sCustomKeysValues = UrlBuilder.mapKeysToValues(keys, values);
    }

    public static void clearCustomParameters() {
        sCustomKeysValues = null;
    }

    private static HashMap<String, String> getCustomParameters(Map<String, String> passedParameters) {
        if (passedParameters != null) {
            HashMap<String, String> retval = new HashMap<>(passedParameters);
            return retval;
        }
        if (sCustomKeysValues != null) {
            HashMap<String, String> retval2 = new HashMap<>(sCustomKeysValues);
            return retval2;
        }
        return null;
    }

    public static void setShouldUseStagingUrls(boolean value) {
        sShouldUseStagingUrls = value;
    }

    public static boolean shouldUseStagingUrls() {
        return sShouldUseStagingUrls;
    }

    public static Intent getIntentForOfferWallActivity(Context context, String userId) {
        return getIntentForOfferWallActivity(context, userId, null, null, null);
    }

    public static Intent getIntentForOfferWallActivity(Context context, String userId, boolean shouldStayOpen) {
        return getIntentForOfferWallActivity(context, userId, Boolean.valueOf(shouldStayOpen), null, null);
    }

    public static Intent getIntentForOfferWallActivity(Context context, String userId, boolean shouldStayOpen, String overrideAppId) {
        return getIntentForOfferWallActivity(context, userId, Boolean.valueOf(shouldStayOpen), overrideAppId, null);
    }

    public static Intent getIntentForOfferWallActivity(Context context, String userId, Boolean shouldStayOpen, String overrideAppId, HashMap<String, String> customParams) {
        Intent intent = new Intent(context, (Class<?>) OfferWallActivity.class);
        intent.putExtra(OfferWallActivity.EXTRA_USERID_KEY, userId);
        if (shouldStayOpen != null) {
            intent.putExtra("EXTRA_SHOULD_REMAIN_OPEN_KEY", shouldStayOpen);
        }
        if (overrideAppId != null) {
            intent.putExtra(OfferWallActivity.EXTRA_OVERRIDEN_APP_ID, overrideAppId);
        }
        intent.putExtra(OfferWallActivity.EXTRA_KEYS_VALUES_MAP, getCustomParameters(customParams));
        return intent;
    }

    public static Intent getIntentForUnlockOfferWallActivity(Context context, String userId, String unlockItemId, String unlockItemName) {
        return getIntentForUnlockOfferWallActivity(context, userId, unlockItemId, unlockItemName, null, null);
    }

    public static Intent getIntentForUnlockOfferWallActivity(Context context, String userId, String unlockItemId, String unlockItemName, String overrideAppId, HashMap<String, String> customParams) {
        ItemIdValidator itemIdValidator = new ItemIdValidator(unlockItemId);
        if (!itemIdValidator.validate()) {
            throw new RuntimeException("The provided Unlock Item ID is not valid. " + itemIdValidator.getValidationDescription());
        }
        Intent intent = new Intent(context, (Class<?>) OfferWallActivity.class);
        intent.putExtra(OfferWallActivity.EXTRA_USERID_KEY, userId);
        intent.putExtra(OfferWallActivity.EXTRA_OFFERWALL_TYPE, OfferWallActivity.OFFERWALL_TYPE_UNLOCK);
        intent.putExtra(OfferWallActivity.UnlockOfferWallTemplate.EXTRA_UNLOCK_ITEM_ID_KEY, unlockItemId);
        if (overrideAppId != null) {
            intent.putExtra(OfferWallActivity.EXTRA_OVERRIDEN_APP_ID, overrideAppId);
        }
        intent.putExtra(OfferWallActivity.EXTRA_KEYS_VALUES_MAP, getCustomParameters(customParams));
        return intent;
    }

    public static void loadShowInterstitial(Activity callingActivity, String userId, InterstitialLoader.InterstitialLoadingStatusListener loadingStatusListener, Boolean shouldStayOpen, String backgroundUrl, String skinName, int loadingTimeoutSecs, String overriddenAppId) {
        loadShowInterstitial(callingActivity, userId, loadingStatusListener, shouldStayOpen, backgroundUrl, skinName, loadingTimeoutSecs, overriddenAppId, null);
    }

    public static void loadShowInterstitial(Activity callingActivity, String userId, InterstitialLoader.InterstitialLoadingStatusListener loadingStatusListener, Boolean shouldStayOpen, String backgroundUrl, String skinName, int loadingTimeoutSecs, String overriddenAppId, Map<String, String> customParams) {
        HostInfo hostInfo = new HostInfo(callingActivity);
        if (overriddenAppId != null) {
            hostInfo.setOverriddenAppId(overriddenAppId);
        }
        InterstitialLoader il = new InterstitialLoader(callingActivity, userId, hostInfo, loadingStatusListener);
        if (shouldStayOpen != null) {
            il.setShouldStayOpen(shouldStayOpen.booleanValue());
        }
        if (backgroundUrl != null) {
            il.setBackgroundUrl(backgroundUrl);
        }
        if (skinName != null) {
            il.setSkinName(skinName);
        }
        if (loadingTimeoutSecs > 0) {
            il.setLoadingTimeoutSecs(loadingTimeoutSecs);
        }
        Map<String, String> extraParams = getCustomParameters(customParams);
        if (extraParams != null) {
            il.setCustomParameters(extraParams);
        }
        il.startLoading();
    }

    public static void loadShowInterstitial(Activity callingActivity, String userId, InterstitialLoader.InterstitialLoadingStatusListener loadingStatusListener, Boolean shouldStayOpen, String backgroundUrl, String skinName, int loadingTimeoutSecs) {
        loadShowInterstitial(callingActivity, userId, loadingStatusListener, shouldStayOpen, backgroundUrl, skinName, loadingTimeoutSecs, null, null);
    }

    public static void loadShowInterstitial(Activity callingActivity, String userId, InterstitialLoader.InterstitialLoadingStatusListener loadingStatusListener, Boolean shouldStayOpen, String backgroundUrl, String skinName) {
        loadShowInterstitial(callingActivity, userId, loadingStatusListener, shouldStayOpen, backgroundUrl, skinName, 0, null, null);
    }

    public static void loadShowInterstitial(Activity callingActivity, String userId, InterstitialLoader.InterstitialLoadingStatusListener loadingStatusListener, Boolean shouldStayOpen) {
        loadShowInterstitial(callingActivity, userId, loadingStatusListener, shouldStayOpen, null, null, 0, null, null);
    }

    public static void loadShowInterstitial(Activity callingActivity, String userId, InterstitialLoader.InterstitialLoadingStatusListener loadingStatusListener) {
        loadShowInterstitial(callingActivity, userId, loadingStatusListener, null, null, null, 0, null, null);
    }

    public static void requestNewCoins(Context context, String userId, SPCurrencyServerListener listener, String transactionId, String securityToken, String applicationId) {
        requestNewCoins(context, userId, listener, transactionId, securityToken, applicationId, null);
    }

    public static void requestNewCoins(Context context, String userId, SPCurrencyServerListener listener, String transactionId, String securityToken, String applicationId, Map<String, String> customParams) {
        HostInfo hostInfo = new HostInfo(context);
        if (applicationId != null) {
            hostInfo.setOverriddenAppId(applicationId);
        }
        VirtualCurrencyConnector vcc = new VirtualCurrencyConnector(context, userId, listener, hostInfo, securityToken);
        vcc.setCustomParameters(getCustomParameters(customParams));
        vcc.fetchDeltaOfCoins();
    }

    public static void requestUnlockItemsStatus(Context context, String userId, SPUnlockResponseListener listener, String securityToken) {
        requestUnlockItemsStatus(context, userId, listener, securityToken, null, null);
    }

    public static void requestUnlockItemsStatus(Context context, String userId, SPUnlockResponseListener listener, String securityToken, String applicationId, Map<String, String> customParams) {
        HostInfo hostInfo = new HostInfo(context);
        if (applicationId != null) {
            hostInfo.setOverriddenAppId(applicationId);
        }
        SponsorPayUnlockConnector uc = new SponsorPayUnlockConnector(context, userId, listener, hostInfo, securityToken);
        uc.setCustomParameters(getCustomParameters(customParams));
        uc.fetchItemsStatus();
    }

    public static OfferBannerRequest requestOfferBanner(Context context, String userId, SPOfferBannerListener listener, OfferBanner.AdShape offerBannerAdShape, String currencyName, String applicationId) {
        return requestOfferBanner(context, userId, listener, offerBannerAdShape, currencyName, applicationId, null);
    }

    public static OfferBannerRequest requestOfferBanner(Context context, String userId, SPOfferBannerListener listener, OfferBanner.AdShape offerBannerAdShape, String currencyName, String applicationId, Map<String, String> customParams) {
        HostInfo hostInfo = new HostInfo(context);
        if (applicationId != null) {
            hostInfo.setOverriddenAppId(applicationId);
        }
        if (offerBannerAdShape == null) {
            offerBannerAdShape = sDefaultOfferBannerAdShape;
        }
        OfferBannerRequest bannerRequest = new OfferBannerRequest(context, userId, hostInfo, listener, offerBannerAdShape, currencyName, getCustomParameters(customParams));
        return bannerRequest;
    }

    static void setCookiesIntoCookieManagerInstance(String[] cookies, String baseUrl, Context context) {
        if (cookies != null && cookies.length != 0) {
            try {
                CookieSyncManager.getInstance();
            } catch (IllegalStateException e) {
                CookieSyncManager.createInstance(context);
            }
            CookieManager instance = CookieManager.getInstance();
            Log.v(AsyncRequest.LOG_TAG, "Setting the following cookies into CookieManager instance " + instance + " for base URL " + baseUrl + ": ");
            for (String cookieString : cookies) {
                instance.setCookie(baseUrl, cookieString);
                Log.v(AsyncRequest.LOG_TAG, cookieString);
            }
        }
    }

    static int convertDevicePixelsIntoPixelsMeasurement(float dps, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) ((dps * scale) + 0.5f);
        return pixels;
    }
}
