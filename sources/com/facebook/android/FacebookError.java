package com.facebook.android;

/* loaded from: classes.dex */
public class FacebookError extends Throwable {
    private static final long serialVersionUID = 1;
    private int mErrorCode;
    private String mErrorType;

    public FacebookError(String message) {
        super(message);
        this.mErrorCode = 0;
    }

    public FacebookError(String message, String type, int code) {
        super(message);
        this.mErrorCode = 0;
        this.mErrorType = type;
        this.mErrorCode = code;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public String getErrorType() {
        return this.mErrorType;
    }
}
