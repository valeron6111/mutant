package oauth.signpost.commonshttp;

import java.io.IOException;
import java.io.InputStream;
import oauth.signpost.http.HttpResponse;

/* loaded from: classes.dex */
public class HttpResponseAdapter implements HttpResponse {
    private org.apache.http.HttpResponse response;

    public HttpResponseAdapter(org.apache.http.HttpResponse response) {
        this.response = response;
    }

    @Override // oauth.signpost.http.HttpResponse
    public InputStream getContent() throws IOException {
        return this.response.getEntity().getContent();
    }

    @Override // oauth.signpost.http.HttpResponse
    public int getStatusCode() throws IOException {
        return this.response.getStatusLine().getStatusCode();
    }

    @Override // oauth.signpost.http.HttpResponse
    public String getReasonPhrase() throws Exception {
        return this.response.getStatusLine().getReasonPhrase();
    }

    @Override // oauth.signpost.http.HttpResponse
    public Object unwrap() {
        return this.response;
    }
}
