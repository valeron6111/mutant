package oauth.signpost.basic;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oauth.signpost.http.HttpRequest;

/* loaded from: classes.dex */
public class HttpURLConnectionRequestAdapter implements HttpRequest {
    protected HttpURLConnection connection;

    public HttpURLConnectionRequestAdapter(HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override // oauth.signpost.http.HttpRequest
    public String getMethod() {
        return this.connection.getRequestMethod();
    }

    @Override // oauth.signpost.http.HttpRequest
    public String getRequestUrl() {
        return this.connection.getURL().toExternalForm();
    }

    @Override // oauth.signpost.http.HttpRequest
    public void setRequestUrl(String url) {
    }

    @Override // oauth.signpost.http.HttpRequest
    public void setHeader(String name, String value) {
        this.connection.setRequestProperty(name, value);
    }

    @Override // oauth.signpost.http.HttpRequest
    public String getHeader(String name) {
        return this.connection.getRequestProperty(name);
    }

    @Override // oauth.signpost.http.HttpRequest
    public Map<String, String> getAllHeaders() {
        Map<String, List<String>> origHeaders = this.connection.getRequestProperties();
        Map<String, String> headers = new HashMap<>(origHeaders.size());
        for (String name : origHeaders.keySet()) {
            List<String> values = origHeaders.get(name);
            if (!values.isEmpty()) {
                headers.put(name, values.get(0));
            }
        }
        return headers;
    }

    @Override // oauth.signpost.http.HttpRequest
    public InputStream getMessagePayload() throws IOException {
        return null;
    }

    @Override // oauth.signpost.http.HttpRequest
    public String getContentType() {
        return this.connection.getRequestProperty("Content-Type");
    }

    @Override // oauth.signpost.http.HttpRequest
    public HttpURLConnection unwrap() {
        return this.connection;
    }
}
