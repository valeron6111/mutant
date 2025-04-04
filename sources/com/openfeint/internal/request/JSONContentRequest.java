package com.openfeint.internal.request;

import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.resource.ServerException;
import org.apache.http.client.methods.HttpUriRequest;

/* loaded from: classes.dex */
public abstract class JSONContentRequest extends BaseRequest {
    private static final String CONTENT_TYPE = "application/json";
    public static final String DESIRED_RESPONSE_PREFIX = "application/json;";

    protected static ServerException notJSONError(int responseCode) {
        return new ServerException("ServerError", String.format(OpenFeintInternal.getRString(C0207RR.string("of_server_error_code_format")), Integer.valueOf(responseCode)));
    }

    public JSONContentRequest() {
    }

    @Override // com.openfeint.internal.request.BaseRequest
    protected HttpUriRequest generateRequest() {
        HttpUriRequest req = super.generateRequest();
        req.addHeader("Accept", CONTENT_TYPE);
        return req;
    }

    protected boolean isResponseJSON() {
        String responseType = getResponseType();
        return responseType != null && responseType.startsWith(DESIRED_RESPONSE_PREFIX);
    }

    public JSONContentRequest(OrderedArgList args) {
        super(args);
    }
}
