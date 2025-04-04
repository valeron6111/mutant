package com.openfeint.internal.request;

import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.internal.Util;

/* loaded from: classes.dex */
public class Signer {
    private String mAccessToken;
    private String mKey;
    private String mSecret;
    private String mSigningKey;

    public String getKey() {
        return this.mKey;
    }

    public Signer(String key, String secret) {
        this.mKey = key;
        this.mSecret = secret;
        this.mSigningKey = this.mSecret + "&";
    }

    public void setAccessToken(String token, String tokenSecret) {
        this.mAccessToken = token;
        this.mSigningKey = this.mSecret + "&" + tokenSecret;
    }

    public String sign(String path, String method, long secondsSinceEpoch, OrderedArgList unsignedParams) {
        if (this.mAccessToken != null) {
            unsignedParams.put("token", this.mAccessToken);
        }
        StringBuilder sigbase = new StringBuilder();
        sigbase.append(path);
        sigbase.append('+');
        sigbase.append(this.mSecret);
        sigbase.append('+');
        sigbase.append(method);
        sigbase.append('+');
        String argString = unsignedParams.getArgString();
        if (argString == null) {
            argString = MutantMessages.sEmpty;
        }
        sigbase.append(argString);
        return Util.base64HMACSHA1(this.mSigningKey, sigbase.toString());
    }
}
