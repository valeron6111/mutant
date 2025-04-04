package com.sponsorpay.sdk.android.publisher;

/* loaded from: classes.dex */
public interface SPOfferBannerListener {
    void onSPOfferBannerAvailable(OfferBanner offerBanner);

    void onSPOfferBannerNotAvailable(OfferBannerRequest offerBannerRequest);

    void onSPOfferBannerRequestError(OfferBannerRequest offerBannerRequest);
}
