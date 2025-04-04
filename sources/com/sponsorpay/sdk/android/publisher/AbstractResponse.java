package com.sponsorpay.sdk.android.publisher;

import android.util.Log;
import com.alawar.mutant.jni.MutantMessages;
import com.sponsorpay.sdk.android.SignatureTools;
import org.json.JSONObject;

/* loaded from: classes.dex */
public abstract class AbstractResponse {
    protected static final String ERROR_CODE_KEY = "code";
    protected static final String ERROR_MESSAGE_KEY = "message";
    protected String mErrorCode;
    protected String mErrorMessage;
    protected RequestErrorType mErrorType;
    protected String mResponseBody;
    protected String mResponseSignature;
    protected int mResponseStatusCode;

    public enum RequestErrorType {
        NO_ERROR,
        ERROR_NO_INTERNET_CONNECTION,
        ERROR_INVALID_RESPONSE,
        ERROR_INVALID_RESPONSE_SIGNATURE,
        SERVER_RETURNED_ERROR,
        ERROR_OTHER
    }

    public abstract void invokeOnErrorCallback();

    public abstract void invokeOnSuccessCallback();

    public abstract void parseSuccessfulResponse();

    public void setResponseData(int responseStatusCode, String responseBody, String responseSignature) {
        this.mResponseStatusCode = responseStatusCode;
        this.mResponseBody = responseBody;
        this.mResponseSignature = responseSignature;
    }

    public boolean verifySignature(String securityToken) {
        String generatedSignature = SignatureTools.generateSignatureForString(this.mResponseBody, securityToken);
        if (generatedSignature.equals(this.mResponseSignature)) {
            return true;
        }
        this.mErrorType = RequestErrorType.ERROR_INVALID_RESPONSE_SIGNATURE;
        return false;
    }

    public boolean hasErrorStatusCode() {
        return this.mResponseStatusCode < 200 || this.mResponseStatusCode > 299;
    }

    public void parseErrorResponse() {
        try {
            JSONObject responseBodyAsJsonObject = new JSONObject(this.mResponseBody);
            this.mErrorCode = responseBodyAsJsonObject.getString(ERROR_CODE_KEY);
            this.mErrorMessage = responseBodyAsJsonObject.getString(ERROR_MESSAGE_KEY);
            this.mErrorType = RequestErrorType.SERVER_RETURNED_ERROR;
        } catch (Exception e) {
            Log.w(getClass().getSimpleName(), "An exception was triggered while parsing error response", e);
            this.mErrorType = RequestErrorType.ERROR_OTHER;
        }
    }

    public void parseAndCallListener(String securityToken) {
        if (this.mErrorType == RequestErrorType.ERROR_NO_INTERNET_CONNECTION) {
            invokeOnErrorCallback();
            return;
        }
        if (hasErrorStatusCode()) {
            parseErrorResponse();
            invokeOnErrorCallback();
        } else {
            if (!verifySignature(securityToken)) {
                invokeOnErrorCallback();
                return;
            }
            parseSuccessfulResponse();
            if (this.mErrorType == RequestErrorType.NO_ERROR) {
                invokeOnSuccessCallback();
            } else {
                invokeOnErrorCallback();
            }
        }
    }

    public RequestErrorType getErrorType() {
        return this.mErrorType;
    }

    public String getErrorCode() {
        return this.mErrorCode != null ? this.mErrorCode : MutantMessages.sEmpty;
    }

    public String getErrorMessage() {
        return this.mErrorMessage != null ? this.mErrorMessage : MutantMessages.sEmpty;
    }
}
