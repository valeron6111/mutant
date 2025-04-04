package oauth.signpost.basic;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;

/* loaded from: classes.dex */
public class DefaultOAuthProvider extends AbstractOAuthProvider {
    private static final long serialVersionUID = 1;

    public DefaultOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl, String authorizationWebsiteUrl) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
    }

    @Override // oauth.signpost.AbstractOAuthProvider
    protected HttpRequest createRequest(String endpointUrl) throws MalformedURLException, IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(endpointUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Length", "0");
        return new HttpURLConnectionRequestAdapter(connection);
    }

    @Override // oauth.signpost.AbstractOAuthProvider
    protected HttpResponse sendRequest(HttpRequest request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) request.unwrap();
        connection.connect();
        return new HttpURLConnectionResponseAdapter(connection);
    }

    @Override // oauth.signpost.AbstractOAuthProvider
    protected void closeConnection(HttpRequest request, HttpResponse response) {
        HttpURLConnection connection = (HttpURLConnection) request.unwrap();
        if (connection != null) {
            connection.disconnect();
        }
    }
}
