package com.sponsorpay.sdk.android.publisher;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.openfeint.internal.request.multipart.StringPart;

/* loaded from: classes.dex */
public class OfferBanner {
    public static final String LOG_TAG = "SPOfferBanner";
    public static final AdShape SP_AD_SHAPE_320X50 = new AdShape(320, 50, "SP_AD_SHAPE_320X50");
    private String mBaseUrl;
    private Context mContext;
    private String[] mCookies;
    private String mHtmlContent;
    private View mOfferBannerView;
    private AdShape mShape;

    public static class AdShape {
        private String mDescription;
        private int mHeight;
        private int mWidth;

        protected AdShape(int w, int h, String description) {
            this.mWidth = w;
            this.mHeight = h;
            this.mDescription = description;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public String getDescription() {
            return this.mDescription;
        }

        public String toString() {
            return getDescription();
        }
    }

    public OfferBanner(Context context, String baseUrl, String htmlContent, String[] cookies, AdShape shape) {
        this.mContext = context;
        this.mBaseUrl = baseUrl;
        this.mHtmlContent = htmlContent;
        this.mCookies = cookies;
        this.mShape = shape;
    }

    public View getBannerView(final Activity hostActivity) {
        if (this.mOfferBannerView == null) {
            WebView webView = new WebView(this.mContext);
            webView.loadDataWithBaseURL(this.mBaseUrl, this.mHtmlContent, StringPart.DEFAULT_CONTENT_TYPE, "utf-8", null);
            int width = SponsorPayPublisher.convertDevicePixelsIntoPixelsMeasurement(this.mShape.getWidth(), this.mContext);
            int height = SponsorPayPublisher.convertDevicePixelsIntoPixelsMeasurement(this.mShape.getHeight(), this.mContext);
            webView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            webView.setWebViewClient(new OfferWebClient() { // from class: com.sponsorpay.sdk.android.publisher.OfferBanner.1
                @Override // com.sponsorpay.sdk.android.publisher.OfferWebClient
                protected void onSponsorPayExitScheme(int resultCode, String targetUrl) {
                    launchActivityWithUrl(hostActivity, targetUrl);
                }
            });
            SponsorPayPublisher.setCookiesIntoCookieManagerInstance(this.mCookies, this.mBaseUrl, this.mContext);
            this.mOfferBannerView = webView;
        }
        return this.mOfferBannerView;
    }
}
