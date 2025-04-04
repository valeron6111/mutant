package com.openfeint.internal.request;

/* loaded from: classes.dex */
public abstract class RawRequest extends JSONRequest {
    private IRawRequestDelegate mDelegate;

    public RawRequest() {
    }

    public RawRequest(OrderedArgList args) {
        super(args);
    }

    @Override // com.openfeint.internal.request.JSONRequest, com.openfeint.internal.request.BaseRequest
    public void onResponse(int responseCode, byte[] body) {
        String respText;
        try {
            super.onResponse(responseCode, body);
            if (this.mDelegate != null) {
                if (!isResponseJSON()) {
                    respText = notJSONError(responseCode).generate();
                } else {
                    respText = new String(body);
                }
                this.mDelegate.onResponse(responseCode, respText);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void setDelegate(IRawRequestDelegate mDelegate) {
        this.mDelegate = mDelegate;
    }
}
