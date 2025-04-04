package com.openfeint.internal.request;

import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.internal.analytics.p002db.AnalyticsDBManager;
import java.util.Map;
import org.apache.http.params.HttpConnectionParams;

/* loaded from: classes.dex */
public class GenericRequest extends JSONContentRequest {
    private IRawRequestDelegate mDelegate;
    final String mMethod;
    final String mPath;
    private int mRetries;
    private long mTimeout;

    @Override // com.openfeint.internal.request.BaseRequest
    public long timeout() {
        return this.mTimeout;
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public int numRetries() {
        return this.mRetries;
    }

    public GenericRequest(String path, String method, Map<String, Object> args, Map<String, Object> httpParams, IRawRequestDelegate delegate) {
        this.mTimeout = super.timeout();
        this.mRetries = super.numRetries();
        if (httpParams != null) {
            for (Map.Entry<String, Object> e : httpParams.entrySet()) {
                String k = e.getKey();
                String v = e.getValue().toString();
                int i = Integer.parseInt(v);
                if (k.equals("connectionTimeout")) {
                    HttpConnectionParams.setConnectionTimeout(getHttpParams(), i);
                } else if (k.equals("socketTimeout")) {
                    HttpConnectionParams.setSoTimeout(getHttpParams(), i);
                } else if (k.equals("lingerTimeout")) {
                    HttpConnectionParams.setLinger(getHttpParams(), i);
                } else if (k.equals("timeout")) {
                    this.mTimeout = i;
                } else if (k.equals("retries")) {
                    this.mRetries = i;
                }
            }
        }
        OrderedArgList argList = new OrderedArgList(args);
        argList.put("format", AnalyticsDBManager.KEY_JSON);
        setArgs(argList);
        this.mMethod = method;
        this.mPath = path;
        setDelegate(delegate);
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public String method() {
        return this.mMethod;
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public String path() {
        return this.mPath;
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public void onResponse(int responseCode, byte[] body) {
        String respText;
        try {
            if (!isResponseJSON()) {
                respText = notJSONError(responseCode).generate();
            } else {
                respText = body != null ? new String(body) : MutantMessages.sEmpty;
            }
            if (this.mDelegate != null) {
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
