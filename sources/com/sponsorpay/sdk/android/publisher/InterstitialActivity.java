package com.sponsorpay.sdk.android.publisher;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.openfeint.internal.request.multipart.StringPart;

/* loaded from: classes.dex */
public class InterstitialActivity extends Activity {
    private static final int BACKGROUND_DRAWABLE_ALPHA = 196;
    private static final int BACKGROUND_DRAWABLE_CORNER_RADIUS = 10;
    private static final int BIGGER_SCREEN_LONG_SIDE_RESOLUTION_DP = 800;
    private static final int BIGGER_SCREEN_SHORT_SIDE_RESOLUTION_DP = 480;
    public static final String EXTRA_BASE_DOMAIN_KEY = "EXTRA_INITIAL_BASE_URL";
    public static final String EXTRA_COOKIESTRINGS_KEY = "EXTRA_COOKIESTRINGS";
    public static final String EXTRA_INITIAL_CONTENT_KEY = "EXTRA_INITIAL_CONTENT_KEY";
    public static final String EXTRA_SHOULD_STAY_OPEN_KEY = "EXTRA_SHOULD_REMAIN_OPEN_KEY";
    private static final int INTERSTITIAL_BORDER_BIGGER_DEVICE = 12;
    private static final int INTERSTITIAL_BORDER_SMALLER_DEVICE = 3;
    private static final int LONG_SIDE_SIZE_FOR_BIGGER_SCREEN_DP = 690;
    private static final int LONG_SIDE_SIZE_FOR_SMALLER_SCREEN_DP = 430;
    private static final int SHORT_SIDE_SIZE_FOR_BIGGER_SCREEN_DP = 380;
    private static final int SHORT_SIDE_SIZE_FOR_SMALLER_SCREEN_DP = 280;
    private boolean mShouldStayOpen;
    private WebView mWebView;
    private LinearLayout mWebViewContainer;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String initialInterstitialContent = getIntent().getStringExtra(EXTRA_INITIAL_CONTENT_KEY);
        String baseDomain = getIntent().getStringExtra(EXTRA_BASE_DOMAIN_KEY);
        String[] cookieStrings = getIntent().getStringArrayExtra(EXTRA_COOKIESTRINGS_KEY);
        if (cookieStrings == null || cookieStrings.length == 0) {
            finish();
        }
        this.mShouldStayOpen = getIntent().getBooleanExtra("EXTRA_SHOULD_REMAIN_OPEN_KEY", this.mShouldStayOpen);
        this.mWebView = new WebView(this);
        this.mWebView.setScrollBarStyle(0);
        ViewGroup.LayoutParams interstitialSize = generateLayoutParamsForCurrentDisplay();
        this.mWebView.setLayoutParams(interstitialSize);
        this.mWebView.setScrollBarStyle(0);
        this.mWebViewContainer = new LinearLayout(this);
        int borderWidth = determineInterstitialBorderWidth();
        this.mWebViewContainer.setPadding(borderWidth, borderWidth, borderWidth, borderWidth);
        this.mWebView.setBackgroundColor(0);
        this.mWebViewContainer.addView(this.mWebView);
        this.mWebViewContainer.setBackgroundColor(0);
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.getSettings().setPluginsEnabled(true);
        this.mWebView.setWebViewClient(new ActivityOfferWebClient(this, this.mShouldStayOpen));
        SponsorPayPublisher.setCookiesIntoCookieManagerInstance(cookieStrings, baseDomain, this);
        this.mWebView.loadDataWithBaseURL(baseDomain, initialInterstitialContent, StringPart.DEFAULT_CONTENT_TYPE, "utf-8", null);
        getWindow().requestFeature(1);
        setContentView(this.mWebViewContainer);
        getWindow().getAttributes().dimAmount = 0.0f;
        getWindow().setBackgroundDrawable(generateBackgroundDrawable());
    }

    private static Drawable generateBackgroundDrawable() {
        float[] cornerRadii = {10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f};
        RoundRectShape roundRectBg = new RoundRectShape(cornerRadii, null, null);
        ShapeDrawable roundRectBgDrawable = new ShapeDrawable(roundRectBg);
        roundRectBgDrawable.setAlpha(BACKGROUND_DRAWABLE_ALPHA);
        return roundRectBgDrawable;
    }

    private ViewGroup.LayoutParams generateLayoutParamsForCurrentDisplay() {
        int shortInterstitialSideDp;
        int longInterstitialSideDp;
        int interstitialWidthDp;
        int interstitialHeightDp;
        Context context = getApplicationContext();
        if (isHostBiggerDevice()) {
            shortInterstitialSideDp = SHORT_SIDE_SIZE_FOR_BIGGER_SCREEN_DP;
            longInterstitialSideDp = LONG_SIDE_SIZE_FOR_BIGGER_SCREEN_DP;
        } else {
            shortInterstitialSideDp = SHORT_SIDE_SIZE_FOR_SMALLER_SCREEN_DP;
            longInterstitialSideDp = LONG_SIDE_SIZE_FOR_SMALLER_SCREEN_DP;
        }
        if (isCurrentOrientationPortrait()) {
            interstitialWidthDp = shortInterstitialSideDp;
            interstitialHeightDp = longInterstitialSideDp;
        } else {
            interstitialWidthDp = longInterstitialSideDp;
            interstitialHeightDp = shortInterstitialSideDp;
        }
        int widthPx = SponsorPayPublisher.convertDevicePixelsIntoPixelsMeasurement(interstitialWidthDp, context);
        int heightPx = SponsorPayPublisher.convertDevicePixelsIntoPixelsMeasurement(interstitialHeightDp, context);
        return new ViewGroup.LayoutParams(widthPx, heightPx);
    }

    private boolean isHostBiggerDevice() {
        Context context = getApplicationContext();
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int displayLongSideResolutionPx = Math.max(display.widthPixels, display.heightPixels);
        int displayShortSideResolutionPx = Math.min(display.widthPixels, display.heightPixels);
        int biggerDeviceShortSideResolutionPx = SponsorPayPublisher.convertDevicePixelsIntoPixelsMeasurement(480.0f, context);
        int biggerDeviceLongSideResolutionPx = SponsorPayPublisher.convertDevicePixelsIntoPixelsMeasurement(800.0f, context);
        boolean shorterSidePasses = displayShortSideResolutionPx >= biggerDeviceShortSideResolutionPx;
        boolean longerSidePasses = displayLongSideResolutionPx >= biggerDeviceLongSideResolutionPx;
        return shorterSidePasses && longerSidePasses;
    }

    private int determineInterstitialBorderWidth() {
        int worderWidthDP = isHostBiggerDevice() ? 12 : 3;
        return SponsorPayPublisher.convertDevicePixelsIntoPixelsMeasurement(worderWidthDP, getApplicationContext());
    }

    private boolean isCurrentOrientationPortrait() {
        DisplayMetrics display = getApplicationContext().getResources().getDisplayMetrics();
        return display.heightPixels > display.widthPixels;
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup.LayoutParams interstitialSize = generateLayoutParamsForCurrentDisplay();
        if (this.mWebView != null) {
            this.mWebViewContainer.removeView(this.mWebView);
            this.mWebView.setLayoutParams(interstitialSize);
            this.mWebViewContainer.addView(this.mWebView);
        }
    }
}
