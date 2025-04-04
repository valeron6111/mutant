package com.alawar.mutant.network.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import com.alawar.mutant.jni.MutantMessages;
import com.alawar.mutant.network.RequestMethod;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/* loaded from: classes.dex */
public class RestClient {
    static Context appContext = null;
    static boolean isInitialized = false;
    private ArrayList<NameValuePair> headers;
    private String message;
    private ArrayList<NameValuePair> params;
    private InputStream payload = null;
    private String response;
    private int responseCode;
    private String sig;
    private String url;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
        isInitialized = true;
    }

    public static boolean hasInternetAccess() {
        ConnectivityManager conMgr = (ConnectivityManager) appContext.getSystemService("connectivity");
        if (conMgr == null || conMgr.getNetworkInfo(0) == null) {
            return false;
        }
        if (conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return conMgr.getNetworkInfo(1) != null && conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
    }

    public String getResponse() {
        return this.response;
    }

    public boolean hasResponse() {
        return this.response != null;
    }

    public String getErrorMessage() {
        return this.message;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public String getSig() {
        return this.sig;
    }

    public RestClient(String url) {
        if (!isInitialized) {
            throw new RuntimeException("RestClient.initialize has not been called");
        }
        this.url = url;
        this.params = new ArrayList<>();
        this.headers = new ArrayList<>();
    }

    public void addParam(String name, String value) {
        this.params.add(new BasicNameValuePair(name, value));
    }

    public void addHeader(String name, String value) {
        this.headers.add(new BasicNameValuePair(name, value));
    }

    public void executeAsync(final RequestMethod method, final RequestExecutedCallback callback) {
        AsyncTask<Object, Object, Boolean> t = new AsyncTask<Object, Object, Boolean>() { // from class: com.alawar.mutant.network.http.RestClient.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Object... objects) {
                try {
                    RestClient.this.execute(method);
                    return true;
                } catch (Throwable e) {
                    Log.e("RestClient", "Error in executeAsync: " + e.toString());
                    return false;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean o) {
                super.onPostExecute((AsyncTaskC00291) o);
                if (o.booleanValue()) {
                    if (callback != null) {
                        callback.onSuccess(RestClient.this);
                    }
                } else if (callback != null) {
                    callback.onFailure(RestClient.this);
                }
            }
        };
        t.execute(new Object[0]);
    }

    public void execute(RequestMethod method) {
        this.response = null;
        if (!hasInternetAccess()) {
            Log.w("RestClient", "Attempt to execute request with an inactive internet connection");
            return;
        }
        try {
            switch (method) {
                case GET:
                    String combinedParams = MutantMessages.sEmpty;
                    if (!this.params.isEmpty()) {
                        combinedParams = MutantMessages.sEmpty + "?";
                        Iterator i$ = this.params.iterator();
                        while (i$.hasNext()) {
                            NameValuePair p = i$.next();
                            String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                            if (combinedParams.length() > 1) {
                                combinedParams = combinedParams + "&" + paramString;
                            } else {
                                combinedParams = combinedParams + paramString;
                            }
                        }
                    }
                    HttpGet request = new HttpGet(this.url + combinedParams);
                    Iterator i$2 = this.headers.iterator();
                    while (i$2.hasNext()) {
                        NameValuePair h = i$2.next();
                        request.addHeader(h.getName(), h.getValue());
                    }
                    executeRequest(request, this.url);
                    return;
                case POST:
                    HttpPost request2 = new HttpPost(this.url);
                    Iterator i$3 = this.headers.iterator();
                    while (i$3.hasNext()) {
                        NameValuePair h2 = i$3.next();
                        request2.addHeader(h2.getName(), h2.getValue());
                    }
                    if (!this.params.isEmpty()) {
                        request2.setEntity(new UrlEncodedFormEntity(this.params, "UTF-8"));
                    }
                    if (this.payload != null) {
                        try {
                            byte[] bytesRead = new byte[8000];
                            int len = this.payload.read(bytesRead);
                            if (len > 0) {
                                byte[] bytes = new byte[len];
                                System.arraycopy(bytesRead, 0, bytes, 0, len);
                                request2.setEntity(new ByteArrayEntity(bytes));
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    executeRequest(request2, this.url);
                    return;
                default:
                    return;
            }
        } catch (UnsupportedEncodingException e2) {
            throw new RuntimeException(e2);
        }
        throw new RuntimeException(e2);
    }

    private void executeRequest(HttpUriRequest request, String url) {
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = client.execute(request);
            this.responseCode = httpResponse.getStatusLine().getStatusCode();
            Header[] arr$ = httpResponse.getHeaders("x-sig");
            for (Header header : arr$) {
                this.sig = header.getValue();
            }
            this.message = httpResponse.getStatusLine().getReasonPhrase();
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                InputStream in = entity.getContent();
                this.response = convertStreamToString(in);
                in.close();
            }
        } catch (Exception e) {
            Log.e("NETWORK", "Cannot execute request", e);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        while (true) {
            try {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        sb.append(line).append("\n");
                    } else {
                        try {
                            break;
                        } catch (IOException e) {
                        }
                    }
                } catch (IOException e2) {
                    Log.e("NETWORK", "Cannot read response", e2);
                    try {
                        is.close();
                        return null;
                    } catch (IOException e3) {
                        return null;
                    }
                }
            } finally {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
        }
        return sb.toString();
    }

    public void setPayload(InputStream payload) {
        this.payload = payload;
    }
}
