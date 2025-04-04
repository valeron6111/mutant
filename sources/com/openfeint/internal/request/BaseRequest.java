package com.openfeint.internal.request;

import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.resource.ServerException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Future;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

/* loaded from: classes.dex */
public abstract class BaseRequest {
    private static int DEFAULT_RETRIES = 2;
    private static long DEFAULT_TIMEOUT = 20000;
    protected static String TAG = "Request";
    private static String sBaseServerURL = null;
    protected OrderedArgList mArgs;
    private HttpUriRequest mRequest;
    private byte[] mResponseBody;
    private int mResponseCode;
    private long mSecondsSinceEpoch;
    private HttpResponse response_;
    private boolean mResponded = false;
    private String mResponseEncoding = null;
    private String mResponseType = null;
    private String mSignature = null;
    private String mKey = null;
    private int mRetriesLeft = 0;
    private String mCurrentURL = null;
    private Future<?> mFuture = null;
    private HttpParams mHttpParams = null;

    public abstract String method();

    public abstract void onResponse(int i, byte[] bArr);

    public abstract String path();

    protected String getResponseEncoding() {
        return this.mResponseEncoding;
    }

    protected String getResponseType() {
        return this.mResponseType;
    }

    public int numRetries() {
        return DEFAULT_RETRIES;
    }

    public long timeout() {
        return DEFAULT_TIMEOUT;
    }

    protected String currentURL() {
        return this.mCurrentURL != null ? this.mCurrentURL : url();
    }

    public void setFuture(Future<?> future) {
        this.mFuture = future;
    }

    public Future<?> getFuture() {
        return this.mFuture;
    }

    protected HttpParams getHttpParams() {
        if (this.mHttpParams == null) {
            this.mHttpParams = new BasicHttpParams();
        }
        return this.mHttpParams;
    }

    public boolean wantsLogin() {
        return false;
    }

    public boolean signed() {
        return true;
    }

    public boolean needsDeviceSession() {
        return signed();
    }

    public BaseRequest() {
    }

    public BaseRequest(OrderedArgList args) {
        setArgs(args);
    }

    protected String baseServerURL() {
        if (sBaseServerURL == null) {
            sBaseServerURL = OpenFeintInternal.getInstance().getServerUrl();
        }
        return sBaseServerURL;
    }

    public String url() {
        return baseServerURL() + path();
    }

    public final void sign(Signer authority) {
        if (this.mArgs == null) {
            this.mArgs = new OrderedArgList();
        }
        if (signed()) {
            this.mSecondsSinceEpoch = System.currentTimeMillis() / 1000;
            this.mSignature = authority.sign(path(), method(), this.mSecondsSinceEpoch, this.mArgs);
            this.mKey = authority.getKey();
        }
    }

    public final void setArgs(OrderedArgList args) {
        this.mArgs = args;
    }

    protected HttpEntity genEntity() throws UnsupportedEncodingException {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(this.mArgs.getArgs(), "UTF-8");
        entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
        return entity;
    }

    protected HttpUriRequest generateRequest() {
        HttpEntityEnclosingRequestBase postReq;
        HttpUriRequest retval = null;
        String meth = method();
        if (meth.equals("GET") || meth.equals("DELETE")) {
            String url = url();
            String argString = this.mArgs.getArgString();
            if (argString != null) {
                url = url + "?" + argString;
            }
            if (meth.equals("GET")) {
                retval = new HttpGet(url);
            } else if (meth.equals("DELETE")) {
                retval = new HttpDelete(url);
            }
        } else {
            if (meth.equals("POST")) {
                postReq = new HttpPost(url());
            } else {
                if (!meth.equals("PUT")) {
                    throw new RuntimeException("Unsupported HTTP method: " + meth);
                }
                postReq = new HttpPut(url());
            }
            try {
                postReq.setEntity(genEntity());
            } catch (UnsupportedEncodingException e) {
                OFLog.m182e(TAG, "Unable to encode request.");
                e.printStackTrace(System.err);
            }
            retval = postReq;
        }
        if (signed() && this.mSignature != null && this.mKey != null) {
            retval.addHeader("X-OF-Signature", this.mSignature);
            retval.addHeader("X-OF-Key", this.mKey);
        }
        addParams(retval);
        return retval;
    }

    protected boolean shouldRedirect(String url) {
        return true;
    }

    protected final void addParams(HttpUriRequest retval) {
        if (this.mHttpParams != null) {
            retval.setParams(this.mHttpParams);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x00aa A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0063 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void exec(boolean r12) {
        /*
            r11 = this;
            r10 = 0
            r9 = 0
            org.apache.http.client.methods.HttpUriRequest r6 = r11.generateRequest()
            r11.mRequest = r6
            int r6 = r11.numRetries()
            r11.mRetriesLeft = r6
            r11.mResponseBody = r9
            r11.mResponseCode = r10
            r11.response_ = r9
            org.apache.http.client.methods.HttpUriRequest r6 = r11.mRequest
            java.net.URI r6 = r6.getURI()
            java.lang.String r4 = r6.getPath()
            java.lang.String r6 = "//"
            boolean r6 = r4.contains(r6)
            if (r6 == 0) goto L63
            com.openfeint.internal.resource.ServerException r5 = new com.openfeint.internal.resource.ServerException
            r5.<init>()
            java.lang.String r6 = "RequestError"
            r5.exceptionClass = r6
            java.lang.String r6 = "of_malformed_request_error"
            int r6 = com.openfeint.internal.C0207RR.string(r6)
            java.lang.String r6 = com.openfeint.internal.OpenFeintInternal.getRString(r6)
            r5.message = r6
            r6 = 1
            r5.needsDeveloperAttention = r6
            r11.fakeServerException(r5)
        L41:
            int r6 = r11.mResponseCode
            byte[] r7 = r11.mResponseBody
            r11.onResponseOffMainThread(r6, r7)
            return
        L49:
            com.openfeint.internal.OpenFeintInternal r6 = com.openfeint.internal.OpenFeintInternal.getInstance()     // Catch: java.lang.Exception -> L74
            org.apache.http.impl.client.AbstractHttpClient r0 = r6.getClient()     // Catch: java.lang.Exception -> L74
            org.apache.http.protocol.BasicHttpContext r1 = new org.apache.http.protocol.BasicHttpContext     // Catch: java.lang.Exception -> L74
            r1.<init>()     // Catch: java.lang.Exception -> L74
            com.openfeint.internal.request.BaseRequest$1 r3 = new com.openfeint.internal.request.BaseRequest$1     // Catch: java.lang.Exception -> L74
            r3.<init>()     // Catch: java.lang.Exception -> L74
            org.apache.http.client.methods.HttpUriRequest r6 = r11.mRequest     // Catch: java.lang.Exception -> L74
            r0.execute(r6, r3, r1)     // Catch: java.lang.Exception -> L74
            r6 = 0
            r11.mRequest = r6     // Catch: java.lang.Exception -> L74
        L63:
            byte[] r6 = r11.mResponseBody
            if (r6 != 0) goto L41
            if (r12 == 0) goto L49
            r6 = 0
            r11.mRetriesLeft = r6     // Catch: java.lang.Exception -> L74
            java.lang.Exception r6 = new java.lang.Exception     // Catch: java.lang.Exception -> L74
            java.lang.String r7 = "Forced failure"
            r6.<init>(r7)     // Catch: java.lang.Exception -> L74
            throw r6     // Catch: java.lang.Exception -> L74
        L74:
            r2 = move-exception
            java.lang.String r6 = com.openfeint.internal.request.BaseRequest.TAG
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Error executing request '"
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.String r8 = r11.path()
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.String r8 = "'."
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.String r7 = r7.toString()
            com.openfeint.internal.logcat.OFLog.m182e(r6, r7)
            java.io.PrintStream r6 = java.lang.System.err
            r2.printStackTrace(r6)
            r11.mResponseBody = r9
            r11.mResponseCode = r10
            r11.response_ = r9
            int r6 = r11.mRetriesLeft
            int r6 = r6 + (-1)
            r11.mRetriesLeft = r6
            if (r6 >= 0) goto L63
            com.openfeint.internal.resource.ServerException r5 = new com.openfeint.internal.resource.ServerException
            r5.<init>()
            java.lang.Class r6 = r2.getClass()
            java.lang.String r6 = r6.getName()
            r5.exceptionClass = r6
            java.lang.String r6 = r2.getMessage()
            r5.message = r6
            java.lang.String r6 = r5.message
            if (r6 != 0) goto Lcf
            java.lang.String r6 = "of_unknown_server_error"
            int r6 = com.openfeint.internal.C0207RR.string(r6)
            java.lang.String r6 = com.openfeint.internal.OpenFeintInternal.getRString(r6)
            r5.message = r6
        Lcf:
            r11.fakeServerException(r5)
            goto L41
        */
        throw new UnsupportedOperationException("Method not decompiled: com.openfeint.internal.request.BaseRequest.exec(boolean):void");
    }

    private void fakeServerException(ServerException se) {
        this.mResponseCode = 0;
        this.mResponseBody = se.generate().getBytes();
        this.mResponseType = JSONContentRequest.DESIRED_RESPONSE_PREFIX;
    }

    public HttpResponse getResponse() {
        return this.response_;
    }

    protected void onResponseOffMainThread(int responseCode, byte[] body) {
    }

    public final void onResponse() {
        if (!this.mResponded) {
            this.mResponded = true;
            if (this.mResponseBody == null) {
                this.mResponseCode = 0;
                ServerException se = new ServerException();
                se.exceptionClass = "Unknown";
                se.message = OpenFeintInternal.getRString(C0207RR.string("of_unknown_server_error"));
                fakeServerException(se);
            }
            onResponse(this.mResponseCode, this.mResponseBody);
            this.response_ = null;
        }
    }

    public void launch() {
        OpenFeintInternal.makeRequest(this);
    }

    public void postTimeoutCleanup() {
        final HttpUriRequest req = this.mRequest;
        this.mRequest = null;
        if (req != null) {
            new Thread(new Runnable() { // from class: com.openfeint.internal.request.BaseRequest.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        req.abort();
                    } catch (UnsupportedOperationException e) {
                    }
                }
            }).start();
        }
        ServerException se = new ServerException();
        se.exceptionClass = "Timeout";
        se.message = OpenFeintInternal.getRString(C0207RR.string("of_timeout"));
        fakeServerException(se);
    }
}
