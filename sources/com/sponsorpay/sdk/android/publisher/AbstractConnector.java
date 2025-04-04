package com.sponsorpay.sdk.android.publisher;

import android.content.Context;
import com.sponsorpay.sdk.android.HostInfo;
import com.sponsorpay.sdk.android.publisher.AsyncRequest;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class AbstractConnector implements AsyncRequest.AsyncRequestResultListener {
    protected static final String URL_PARAM_KEY_TIMESTAMP = "timestamp";
    protected Context mContext;
    protected Map<String, String> mCustomParameters;
    protected HostInfo mHostInfo;
    protected String mSecurityToken;
    protected UserId mUserId;
    protected String remoteResourceUrl;

    protected AbstractConnector(Context context, String userId, HostInfo hostInfo, String securityToken) {
        this.mContext = context;
        this.mUserId = UserId.make(context, userId);
        this.mHostInfo = hostInfo;
        this.mSecurityToken = securityToken;
    }

    public void setCustomParameters(Map<String, String> customParams) {
        this.mCustomParameters = customParams;
    }

    protected String getCurrentUnixTimestampAsString() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }
}
