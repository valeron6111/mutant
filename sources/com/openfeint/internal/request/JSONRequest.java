package com.openfeint.internal.request;

import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.Util;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.resource.ServerException;

/* loaded from: classes.dex */
public abstract class JSONRequest extends JSONContentRequest {
    public void onSuccess(Object responseBody) {
    }

    public void onFailure(String exceptionMessage) {
    }

    public JSONRequest() {
    }

    public JSONRequest(OrderedArgList args) {
        super(args);
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public void onResponse(int responseCode, byte[] bodyStream) {
        if (bodyStream.length == 0 || ((bodyStream.length == 1 && bodyStream[0] == 32) || isResponseJSON())) {
            Object responseBody = parseJson(bodyStream);
            onResponse(responseCode, responseBody);
        } else {
            onResponse(responseCode, notJSONError(responseCode));
        }
    }

    protected Object parseJson(byte[] bodyStream) {
        return Util.getObjFromJson(bodyStream);
    }

    protected void onResponse(int responseCode, Object responseBody) {
        if (200 <= responseCode && responseCode < 300 && (responseBody == null || !(responseBody instanceof ServerException))) {
            onSuccess(responseBody);
        } else {
            onFailure(responseBody);
        }
    }

    protected void onFailure(Object responseBody) {
        String exceptionMessage = OpenFeintInternal.getRString(C0207RR.string("of_unknown_server_error"));
        if (responseBody != null && (responseBody instanceof ServerException)) {
            ServerException e = (ServerException) responseBody;
            exceptionMessage = e.message;
            if (e.needsDeveloperAttention) {
                OFLog.m182e("ServerException", exceptionMessage);
                OpenFeintInternal.getInstance().displayErrorDialog(exceptionMessage);
            }
        }
        onFailure(exceptionMessage);
    }
}
