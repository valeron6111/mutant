package com.flurry.android;

import android.content.Context;
import android.view.View;
import com.alawar.mutant.jni.MutantMessages;
import java.util.List;

/* loaded from: classes.dex */
public class AppCircle {
    public void setDefaultNoAdsMessage(String str) {
        FlurryAgent.setDefaultNoAdsMessage(str);
    }

    public void setAppCircleCallback(AppCircleCallback appCircleCallback) {
        FlurryAgent.m13a(appCircleCallback);
    }

    public void launchCatalogOnBannerClicked(boolean z) {
        FlurryAgent.m21a(z);
    }

    public void launchCanvasOnBannerClicked(boolean z) {
        FlurryAgent.m21a(z);
    }

    public boolean isLaunchCanvasOnBannerClicked() {
        return FlurryAgent.m22a();
    }

    public boolean isLaunchCatalogOnBannerClicked() {
        return FlurryAgent.m22a();
    }

    public View getHook(Context context, String str, int i) {
        return FlurryAgent.m6a(context, str, i);
    }

    public void openCatalog(Context context) {
        openCatalog(context, MutantMessages.sEmpty);
    }

    public void openCatalog(Context context, String str) {
        FlurryAgent.m11a(context, str);
    }

    public boolean hasAds() {
        return FlurryAgent.m44d();
    }

    public Offer getOffer(String str) {
        return FlurryAgent.m7a(str);
    }

    public List getAllOffers(String str) {
        return FlurryAgent.m29b(str);
    }

    public void acceptOffer(Context context, long j) {
        FlurryAgent.m10a(context, j);
    }

    public void removeOffers(List list) {
        FlurryAgent.m20a(list);
    }

    public Offer getOffer() {
        return getOffer(MutantMessages.sEmpty);
    }

    public List getAllOffers() {
        return FlurryAgent.m29b(MutantMessages.sEmpty);
    }

    public void addUserCookie(String str, String str2) {
        FlurryAgent.addUserCookie(str, str2);
    }

    public void clearUserCookies() {
        FlurryAgent.clearUserCookies();
    }
}
