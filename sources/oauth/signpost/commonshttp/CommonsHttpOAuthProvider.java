package oauth.signpost.commonshttp;

import java.io.IOException;
import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

/* loaded from: classes.dex */
public class CommonsHttpOAuthProvider extends AbstractOAuthProvider {
    private static final long serialVersionUID = 1;
    private transient HttpClient httpClient;

    public CommonsHttpOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl, String authorizationWebsiteUrl) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        this.httpClient = new DefaultHttpClient();
    }

    public CommonsHttpOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl, String authorizationWebsiteUrl, HttpClient httpClient) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        this.httpClient = httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override // oauth.signpost.AbstractOAuthProvider
    protected HttpRequest createRequest(String endpointUrl) throws Exception {
        HttpPost request = new HttpPost(endpointUrl);
        return new HttpRequestAdapter(request);
    }

    @Override // oauth.signpost.AbstractOAuthProvider
    protected HttpResponse sendRequest(HttpRequest request) throws Exception {
        org.apache.http.HttpResponse response = this.httpClient.execute((HttpUriRequest) request.unwrap());
        return new HttpResponseAdapter(response);
    }

    @Override // oauth.signpost.AbstractOAuthProvider
    protected void closeConnection(HttpRequest request, HttpResponse response) throws Exception {
        HttpEntity entity;
        if (response != null && (entity = ((org.apache.http.HttpResponse) response.unwrap()).getEntity()) != null) {
            try {
                entity.consumeContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
