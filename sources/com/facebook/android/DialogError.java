package com.facebook.android;

/* loaded from: classes.dex */
public class DialogError extends Throwable {
    private static final long serialVersionUID = 1;
    private int mErrorCode;
    private String mFailingUrl;

    public DialogError(String message, int errorCode, String failingUrl) {
        super(message);
        this.mErrorCode = errorCode;
        this.mFailingUrl = failingUrl;
    }

    int getErrorCode() {
        return this.mErrorCode;
    }

    String getFailingUrl() {
        return this.mFailingUrl;
    }
}
