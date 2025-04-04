package com.openfeint.internal.request;

import com.alawar.mutant.jni.MutantMessages;

/* loaded from: classes.dex */
public class ExternalBitmapRequest extends BitmapRequest {
    private String mURL;

    public ExternalBitmapRequest(String url) {
        this.mURL = url;
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public boolean signed() {
        return false;
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public String url() {
        return this.mURL;
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public String path() {
        return MutantMessages.sEmpty;
    }
}
