package com.sponsorpay.sdk.android.advertiser;

import android.content.Context;
import android.content.SharedPreferences;
import com.alawar.mutant.jni.MutantMessages;

/* loaded from: classes.dex */
public class SponsorPayAdvertiserState {
    private static final String PREFERENCES_FILE_NAME = "SponsorPayAdvertiserState";
    private static final String STATE_GOT_SUCCESSFUL_RESPONSE_KEY = "SponsorPayAdvertiserState";
    private static final String STATE_INSTALL_REFERRER_KEY = "InstallReferrer";
    private static final String STATE_INSTALL_SUBID_KEY = "InstallSubId";
    private SharedPreferences mPrefs;

    public SponsorPayAdvertiserState(Context context) {
        this.mPrefs = context.getSharedPreferences("SponsorPayAdvertiserState", 0);
    }

    public void setHasAdvertiserCallbackReceivedSuccessfulResponse(boolean value) {
        SharedPreferences.Editor prefsEditor = this.mPrefs.edit();
        prefsEditor.putBoolean("SponsorPayAdvertiserState", value);
        prefsEditor.commit();
    }

    public boolean getHasAdvertiserCallbackReceivedSuccessfulResponse() {
        return this.mPrefs.getBoolean("SponsorPayAdvertiserState", false);
    }

    public void setInstallSubId(String subIdValue) {
        SharedPreferences.Editor prefsEditor = this.mPrefs.edit();
        prefsEditor.putString(STATE_INSTALL_SUBID_KEY, subIdValue);
        prefsEditor.commit();
    }

    public String getInstallSubId() {
        return this.mPrefs.getString(STATE_INSTALL_SUBID_KEY, MutantMessages.sEmpty);
    }

    public void setInstallReferrer(String value) {
        SharedPreferences.Editor prefsEditor = this.mPrefs.edit();
        prefsEditor.putString(STATE_INSTALL_REFERRER_KEY, value);
        prefsEditor.commit();
    }

    public String getInstallReferrer() {
        return this.mPrefs.getString(STATE_INSTALL_REFERRER_KEY, MutantMessages.sEmpty);
    }
}
