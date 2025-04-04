package com.openfeint.internal.request;

import android.os.Bundle;
import android.os.Handler;
import com.openfeint.internal.CookieStore;
import com.openfeint.internal.SyncedStore;
import com.openfeint.internal.logcat.OFLog;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HttpContext;

/* loaded from: classes.dex */
public class Client extends DefaultHttpClient {
    private static final int EXCESS_THREAD_LIFETIME = 30;
    private static final int MAX_THREADS = 4;
    private static final int MIN_THREADS = 2;
    private static final String TAG = "HTTPClient";
    private CookieStore mCookieStore;
    final ExecutorService mExecutor;
    private boolean mForceOffline;
    private Handler mMainThreadHandler;
    private Signer mSigner;

    public void saveInstanceState(Bundle outState) {
        this.mCookieStore.saveInstanceState(outState);
        outState.putBoolean("mForceOffline", this.mForceOffline);
    }

    public void restoreInstanceState(Bundle inState) {
        this.mCookieStore.restoreInstanceState(inState);
        this.mForceOffline = inState.getBoolean("mForceOffline");
    }

    public boolean toggleForceOffline() {
        if (this.mForceOffline) {
            this.mForceOffline = false;
            OFLog.m183i(TAG, "forceOffline = FALSE");
        } else {
            this.mForceOffline = true;
            OFLog.m183i(TAG, "forceOffline = TRUE");
        }
        return this.mForceOffline;
    }

    private static class GzipDecompressingEntity extends HttpEntityWrapper {
        public GzipDecompressingEntity(HttpEntity entity) {
            super(entity);
        }

        @Override // org.apache.http.entity.HttpEntityWrapper, org.apache.http.HttpEntity
        public InputStream getContent() throws IOException, IllegalStateException {
            InputStream wrappedin = this.wrappedEntity.getContent();
            return new GZIPInputStream(wrappedin);
        }

        @Override // org.apache.http.entity.HttpEntityWrapper, org.apache.http.HttpEntity
        public long getContentLength() {
            return -1L;
        }
    }

    static final ClientConnectionManager makeCCM() {
        SchemeRegistry sr = new SchemeRegistry();
        sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        sr.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(new BasicHttpParams(), sr);
        return ccm;
    }

    private final class Executor extends ThreadPoolExecutor {
        Executor() {
            super(2, 4, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new RejectedExecutionHandler() { // from class: com.openfeint.internal.request.Client.Executor.1
                @Override // java.util.concurrent.RejectedExecutionHandler
                public void rejectedExecution(Runnable arg0, ThreadPoolExecutor arg1) {
                    OFLog.m182e(Client.TAG, "Can't submit runnable " + arg0.toString());
                }
            });
        }
    }

    public Client(String key, String secret, SyncedStore prefs) {
        super(makeCCM(), new BasicHttpParams());
        this.mExecutor = new Executor();
        this.mSigner = new Signer(key, secret);
        this.mMainThreadHandler = new Handler();
        this.mCookieStore = new CookieStore(prefs);
        setCookieStore(this.mCookieStore);
        addRequestInterceptor(new HttpRequestInterceptor() { // from class: com.openfeint.internal.request.Client.1
            @Override // org.apache.http.HttpRequestInterceptor
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
            }
        });
        addResponseInterceptor(new HttpResponseInterceptor() { // from class: com.openfeint.internal.request.Client.2
            @Override // org.apache.http.HttpResponseInterceptor
            public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                Header ceheader;
                HeaderElement[] codecs;
                HttpEntity entity = response.getEntity();
                if (entity != null && (ceheader = entity.getContentEncoding()) != null && (codecs = ceheader.getElements()) != null) {
                    for (HeaderElement headerElement : codecs) {
                        if (headerElement.getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }
        });
    }

    public final void makeRequest(BaseRequest req) {
        makeRequest(req, req.timeout());
    }

    public final void makeRequest(final BaseRequest req, final long timeoutMillis) {
        final Runnable onResponse = new Runnable() { // from class: com.openfeint.internal.request.Client.3
            @Override // java.lang.Runnable
            public void run() {
                req.onResponse();
            }
        };
        final Runnable onTimeout = new Runnable() { // from class: com.openfeint.internal.request.Client.4
            @Override // java.lang.Runnable
            public void run() {
                if (req.getResponse() == null) {
                    boolean victory = req.getFuture().cancel(true);
                    if (victory) {
                        req.postTimeoutCleanup();
                        Client.this.mMainThreadHandler.post(onResponse);
                    }
                }
            }
        };
        Runnable requestRunnable = new Runnable() { // from class: com.openfeint.internal.request.Client.5
            @Override // java.lang.Runnable
            public void run() {
                Client.this.mMainThreadHandler.postDelayed(onTimeout, timeoutMillis);
                req.sign(Client.this.mSigner);
                req.exec(Client.this.mForceOffline);
                Client.this.mMainThreadHandler.removeCallbacks(onTimeout);
                Client.this.mMainThreadHandler.post(onResponse);
            }
        };
        req.setFuture(this.mExecutor.submit(requestRunnable));
    }
}
