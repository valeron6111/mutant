package com.sugree.twitter;

/* loaded from: classes.dex */
public class TwitterError extends Throwable {
    private static final long serialVersionUID = 6626439442641443626L;
    private int mErrorCode;
    private String mErrorType;

    public TwitterError(String message) {
        super(message);
        this.mErrorCode = 0;
    }

    public TwitterError(String message, String errorType, int errorCode) {
        super(message);
        this.mErrorCode = 0;
        this.mErrorType = errorType;
        this.mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public String getErrorType() {
        return this.mErrorType;
    }
}
