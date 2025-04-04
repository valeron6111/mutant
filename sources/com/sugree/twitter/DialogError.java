package com.sugree.twitter;

/* loaded from: classes.dex */
public class DialogError extends Throwable {
    private static final long serialVersionUID = -992704825747001028L;
    private int mErrorCode;
    private String mFailingUrl;

    public DialogError(String message, int errorCode, String failingUrl) {
        super(message);
        this.mErrorCode = errorCode;
        this.mFailingUrl = failingUrl;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public String getFailingUrl() {
        return this.mFailingUrl;
    }
}
