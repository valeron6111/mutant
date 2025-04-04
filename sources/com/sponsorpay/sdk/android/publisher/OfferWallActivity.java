package com.sponsorpay.sdk.android.publisher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;
import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.internal.vendor.org.codehaus.jackson.Base64Variant;
import com.sponsorpay.sdk.android.HostInfo;
import com.sponsorpay.sdk.android.UrlBuilder;
import com.sponsorpay.sdk.android.publisher.SponsorPayPublisher;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class OfferWallActivity extends Activity {
    public static final String EXTRA_KEYS_VALUES_MAP = "EXTRA_KEY_VALUES_MAP";
    public static final String EXTRA_OFFERWALL_TYPE = "EXTRA_OFFERWALL_TEMPLATE_KEY";
    public static final String EXTRA_OVERRIDEN_APP_ID = "EXTRA_OVERRIDEN_APP_ID";
    public static final String EXTRA_SHOULD_STAY_OPEN_KEY = "EXTRA_SHOULD_REMAIN_OPEN_KEY";
    public static final String EXTRA_USERID_KEY = "EXTRA_USERID_KEY";
    public static final String OFFERWALL_TYPE_MOBILE = "OFFERWALL_TYPE_MOBILE";
    public static final String OFFERWALL_TYPE_UNLOCK = "OFFERWALL_TYPE_UNLOCK";
    public static final int RESULT_CODE_NO_STATUS_CODE = -10;
    protected Map<String, String> mCustomKeysValues;
    private AlertDialog mErrorDialog;
    protected HostInfo mHostInfo;
    private ProgressDialog mProgressDialog;
    private boolean mShouldStayOpen;
    private OfferWallTemplate mTemplate;
    protected UserId mUserId;
    protected WebView mWebView;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(1);
        this.mProgressDialog = new ProgressDialog(this);
        this.mProgressDialog.setOwnerActivity(this);
        this.mProgressDialog.setIndeterminate(true);
        this.mProgressDialog.setMessage(SponsorPayPublisher.getUIString(SponsorPayPublisher.UIStringIdentifier.LOADING_OFFERWALL));
        this.mProgressDialog.show();
        this.mHostInfo = new HostInfo(getApplicationContext());
        instantiateTemplate();
        fetchPassedExtras();
        this.mWebView = new WebView(this);
        this.mWebView.setScrollBarStyle(0);
        setContentView(this.mWebView);
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.getSettings().setPluginsEnabled(true);
        this.mWebView.setWebViewClient(new ActivityOfferWebClient(this, this.mShouldStayOpen) { // from class: com.sponsorpay.sdk.android.publisher.OfferWallActivity.1
            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView view, String url) {
                if (OfferWallActivity.this.mProgressDialog != null) {
                    OfferWallActivity.this.mProgressDialog.dismiss();
                    OfferWallActivity.this.mProgressDialog = null;
                }
                super.onPageFinished(view, url);
            }

            @Override // android.webkit.WebViewClient
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                SponsorPayPublisher.UIStringIdentifier error;
                switch (errorCode) {
                    case -7:
                    case Base64Variant.BASE64_VALUE_PADDING /* -2 */:
                        error = SponsorPayPublisher.UIStringIdentifier.ERROR_LOADING_OFFERWALL_NO_INTERNET_CONNECTION;
                        break;
                    default:
                        error = SponsorPayPublisher.UIStringIdentifier.ERROR_LOADING_OFFERWALL;
                        break;
                }
                OfferWallActivity.this.showErrorDialog(error);
            }
        });
    }

    private void instantiateTemplate() {
        String templateName = getIntent().getStringExtra(EXTRA_OFFERWALL_TYPE);
        if (OFFERWALL_TYPE_UNLOCK.equals(templateName)) {
            this.mTemplate = new UnlockOfferWallTemplate();
        } else {
            this.mTemplate = new MobileOfferWallTemplate();
        }
    }

    protected void fetchPassedExtras() {
        String passedUserId = getIntent().getStringExtra(EXTRA_USERID_KEY);
        this.mUserId = UserId.make(getApplicationContext(), passedUserId);
        this.mShouldStayOpen = getIntent().getBooleanExtra("EXTRA_SHOULD_REMAIN_OPEN_KEY", this.mTemplate.shouldStayOpenByDefault());
        Serializable inflatedKvMap = getIntent().getSerializableExtra(EXTRA_KEYS_VALUES_MAP);
        if (inflatedKvMap instanceof HashMap) {
            this.mCustomKeysValues = (HashMap) inflatedKvMap;
        }
        String overridenAppId = getIntent().getStringExtra(EXTRA_OVERRIDEN_APP_ID);
        if (overridenAppId != null && !overridenAppId.equals(MutantMessages.sEmpty)) {
            this.mHostInfo.setOverriddenAppId(overridenAppId);
        }
        this.mTemplate.fetchAdditionalExtras();
    }

    @Override // android.app.Activity
    protected void onPause() {
        if (this.mErrorDialog != null) {
            this.mErrorDialog.dismiss();
            this.mErrorDialog = null;
        }
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        try {
            String offerwallUrl = generateUrl();
            Log.d(getClass().getSimpleName(), "Offerwall request url: " + offerwallUrl);
            this.mWebView.loadUrl(offerwallUrl);
        } catch (RuntimeException ex) {
            showErrorDialog(ex.getMessage());
        }
    }

    private String generateUrl() {
        this.mCustomKeysValues = this.mTemplate.addAdditionalParameters(this.mCustomKeysValues);
        String baseUrl = this.mTemplate.getBaseUrl();
        return UrlBuilder.buildUrl(baseUrl, this.mUserId.toString(), this.mHostInfo, this.mCustomKeysValues, null);
    }

    protected void showErrorDialog(SponsorPayPublisher.UIStringIdentifier error) {
        String errorMessage = SponsorPayPublisher.getUIString(error);
        showErrorDialog(errorMessage);
    }

    protected void showErrorDialog(String error) {
        String errorDialogTitle = SponsorPayPublisher.getUIString(SponsorPayPublisher.UIStringIdentifier.ERROR_DIALOG_TITLE);
        String dismissButtonCaption = SponsorPayPublisher.getUIString(SponsorPayPublisher.UIStringIdentifier.DISMISS_ERROR_DIALOG);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(errorDialogTitle);
        dialogBuilder.setMessage(error);
        dialogBuilder.setNegativeButton(dismissButtonCaption, new DialogInterface.OnClickListener() { // from class: com.sponsorpay.sdk.android.publisher.OfferWallActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OfferWallActivity.this.mErrorDialog = null;
                OfferWallActivity.this.finish();
            }
        });
        this.mErrorDialog = dialogBuilder.create();
        this.mErrorDialog.setOwnerActivity(this);
        try {
            this.mErrorDialog.show();
        } catch (WindowManager.BadTokenException e) {
            Log.e(getClass().getSimpleName(), "Couldn't show error dialog. Not displayed error message is: " + error, e);
        }
    }

    public abstract class OfferWallTemplate {
        public abstract Map<String, String> addAdditionalParameters(Map<String, String> map);

        public abstract void fetchAdditionalExtras();

        public abstract String getBaseUrl();

        public abstract boolean shouldStayOpenByDefault();

        public OfferWallTemplate() {
        }
    }

    public class MobileOfferWallTemplate extends OfferWallTemplate {
        private static final String OFFERWALL_PRODUCTION_BASE_URL = "http://iframe.sponsorpay.com/mobile?";
        private static final String OFFERWALL_STAGING_BASE_URL = "http://staging.iframe.sponsorpay.com/mobile?";

        public MobileOfferWallTemplate() {
            super();
        }

        @Override // com.sponsorpay.sdk.android.publisher.OfferWallActivity.OfferWallTemplate
        public void fetchAdditionalExtras() {
        }

        @Override // com.sponsorpay.sdk.android.publisher.OfferWallActivity.OfferWallTemplate
        public String getBaseUrl() {
            return SponsorPayPublisher.shouldUseStagingUrls() ? OFFERWALL_STAGING_BASE_URL : OFFERWALL_PRODUCTION_BASE_URL;
        }

        @Override // com.sponsorpay.sdk.android.publisher.OfferWallActivity.OfferWallTemplate
        public Map<String, String> addAdditionalParameters(Map<String, String> params) {
            return params;
        }

        @Override // com.sponsorpay.sdk.android.publisher.OfferWallActivity.OfferWallTemplate
        public boolean shouldStayOpenByDefault() {
            return true;
        }
    }

    public class UnlockOfferWallTemplate extends OfferWallTemplate {
        public static final String EXTRA_UNLOCK_ITEM_ID_KEY = "EXTRA_UNLOCK_ITEM_ID_KEY";
        public static final String PARAM_UNLOCK_ITEM_ID_KEY = "itemid";
        private static final String UNLOCK_OFFERWALL_PRODUCTION_BASE_URL = "http://iframe.sponsorpay.com/unlock?";
        private static final String UNLOCK_OFFERWALL_STAGING_BASE_URL = "http://staging.iframe.sponsorpay.com/unlock?";
        private String mUnlockItemId;

        public UnlockOfferWallTemplate() {
            super();
        }

        @Override // com.sponsorpay.sdk.android.publisher.OfferWallActivity.OfferWallTemplate
        public void fetchAdditionalExtras() {
            this.mUnlockItemId = OfferWallActivity.this.getIntent().getStringExtra(EXTRA_UNLOCK_ITEM_ID_KEY);
        }

        @Override // com.sponsorpay.sdk.android.publisher.OfferWallActivity.OfferWallTemplate
        public String getBaseUrl() {
            return SponsorPayPublisher.shouldUseStagingUrls() ? UNLOCK_OFFERWALL_STAGING_BASE_URL : UNLOCK_OFFERWALL_PRODUCTION_BASE_URL;
        }

        @Override // com.sponsorpay.sdk.android.publisher.OfferWallActivity.OfferWallTemplate
        public Map<String, String> addAdditionalParameters(Map<String, String> params) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(PARAM_UNLOCK_ITEM_ID_KEY, this.mUnlockItemId);
            return params;
        }

        @Override // com.sponsorpay.sdk.android.publisher.OfferWallActivity.OfferWallTemplate
        public boolean shouldStayOpenByDefault() {
            return false;
        }
    }
}
