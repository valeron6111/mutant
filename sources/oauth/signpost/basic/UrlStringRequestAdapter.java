package oauth.signpost.basic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import oauth.signpost.http.HttpRequest;

/* loaded from: classes.dex */
public class UrlStringRequestAdapter implements HttpRequest {
    private String url;

    public UrlStringRequestAdapter(String url) {
        this.url = url;
    }

    @Override // oauth.signpost.http.HttpRequest
    public String getMethod() {
        return "GET";
    }

    @Override // oauth.signpost.http.HttpRequest
    public String getRequestUrl() {
        return this.url;
    }

    @Override // oauth.signpost.http.HttpRequest
    public void setRequestUrl(String url) {
        this.url = url;
    }

    @Override // oauth.signpost.http.HttpRequest
    public void setHeader(String name, String value) {
    }

    @Override // oauth.signpost.http.HttpRequest
    public String getHeader(String name) {
        return null;
    }

    @Override // oauth.signpost.http.HttpRequest
    public Map<String, String> getAllHeaders() {
        return Collections.emptyMap();
    }

    @Override // oauth.signpost.http.HttpRequest
    public InputStream getMessagePayload() throws IOException {
        return null;
    }

    @Override // oauth.signpost.http.HttpRequest
    public String getContentType() {
        return null;
    }

    @Override // oauth.signpost.http.HttpRequest
    public Object unwrap() {
        return this.url;
    }
}
