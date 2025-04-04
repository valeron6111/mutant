package oauth.signpost;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;

/* loaded from: classes.dex */
public abstract class AbstractOAuthProvider implements OAuthProvider {
    private static final long serialVersionUID = 1;
    private String accessTokenEndpointUrl;
    private String authorizationWebsiteUrl;
    private boolean isOAuth10a;
    private transient OAuthProviderListener listener;
    private String requestTokenEndpointUrl;
    private HttpParameters responseParameters = new HttpParameters();
    private Map<String, String> defaultHeaders = new HashMap();

    protected abstract HttpRequest createRequest(String str) throws Exception;

    protected abstract HttpResponse sendRequest(HttpRequest httpRequest) throws Exception;

    public AbstractOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl, String authorizationWebsiteUrl) {
        this.requestTokenEndpointUrl = requestTokenEndpointUrl;
        this.accessTokenEndpointUrl = accessTokenEndpointUrl;
        this.authorizationWebsiteUrl = authorizationWebsiteUrl;
    }

    @Override // oauth.signpost.OAuthProvider
    public String retrieveRequestToken(OAuthConsumer consumer, String callbackUrl) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        consumer.setTokenWithSecret(null, null);
        retrieveToken(consumer, this.requestTokenEndpointUrl, OAuth.OAUTH_CALLBACK, callbackUrl);
        String callbackConfirmed = this.responseParameters.getFirst(OAuth.OAUTH_CALLBACK_CONFIRMED);
        this.responseParameters.remove((Object) OAuth.OAUTH_CALLBACK_CONFIRMED);
        this.isOAuth10a = Boolean.TRUE.toString().equals(callbackConfirmed);
        return this.isOAuth10a ? OAuth.addQueryParameters(this.authorizationWebsiteUrl, OAuth.OAUTH_TOKEN, consumer.getToken()) : OAuth.addQueryParameters(this.authorizationWebsiteUrl, OAuth.OAUTH_TOKEN, consumer.getToken(), OAuth.OAUTH_CALLBACK, callbackUrl);
    }

    @Override // oauth.signpost.OAuthProvider
    public void retrieveAccessToken(OAuthConsumer consumer, String oauthVerifier) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        if (consumer.getToken() == null || consumer.getTokenSecret() == null) {
            throw new OAuthExpectationFailedException("Authorized request token or token secret not set. Did you retrieve an authorized request token before?");
        }
        if (this.isOAuth10a && oauthVerifier != null) {
            retrieveToken(consumer, this.accessTokenEndpointUrl, OAuth.OAUTH_VERIFIER, oauthVerifier);
        } else {
            retrieveToken(consumer, this.accessTokenEndpointUrl, new String[0]);
        }
    }

    protected void retrieveToken(OAuthConsumer consumer, String endpointUrl, String... additionalParameters) throws OAuthMessageSignerException, OAuthCommunicationException, OAuthNotAuthorizedException, OAuthExpectationFailedException {
        Map<String, String> defaultHeaders = getRequestHeaders();
        if (consumer.getConsumerKey() == null || consumer.getConsumerSecret() == null) {
            throw new OAuthExpectationFailedException("Consumer key or secret not set");
        }
        try {
            try {
                try {
                    try {
                        HttpRequest request = createRequest(endpointUrl);
                        for (String header : defaultHeaders.keySet()) {
                            request.setHeader(header, defaultHeaders.get(header));
                        }
                        if (additionalParameters != null) {
                            HttpParameters httpParams = new HttpParameters();
                            httpParams.putAll(additionalParameters, true);
                            consumer.setAdditionalParameters(httpParams);
                        }
                        if (this.listener != null) {
                            this.listener.prepareRequest(request);
                        }
                        consumer.sign(request);
                        if (this.listener != null) {
                            this.listener.prepareSubmission(request);
                        }
                        HttpResponse response = sendRequest(request);
                        int statusCode = response.getStatusCode();
                        boolean requestHandled = this.listener != null ? this.listener.onResponseReceived(request, response) : false;
                        if (requestHandled) {
                            try {
                                closeConnection(request, response);
                                return;
                            } catch (Exception e) {
                                throw new OAuthCommunicationException(e);
                            }
                        }
                        if (statusCode >= 300) {
                            handleUnexpectedResponse(statusCode, response);
                        }
                        HttpParameters responseParams = OAuth.decodeForm(response.getContent());
                        String token = responseParams.getFirst(OAuth.OAUTH_TOKEN);
                        String secret = responseParams.getFirst(OAuth.OAUTH_TOKEN_SECRET);
                        responseParams.remove(OAuth.OAUTH_TOKEN);
                        responseParams.remove(OAuth.OAUTH_TOKEN_SECRET);
                        setResponseParameters(responseParams);
                        if (token == null || secret == null) {
                            throw new OAuthExpectationFailedException("Request token or token secret not set in server reply. The service provider you use is probably buggy.");
                        }
                        consumer.setTokenWithSecret(token, secret);
                        try {
                            closeConnection(request, response);
                        } catch (Exception e2) {
                            throw new OAuthCommunicationException(e2);
                        }
                    } catch (OAuthNotAuthorizedException e3) {
                        throw e3;
                    }
                } catch (Exception e4) {
                    throw new OAuthCommunicationException(e4);
                }
            } catch (OAuthExpectationFailedException e5) {
                throw e5;
            }
        } catch (Throwable th) {
            try {
                closeConnection(null, null);
                throw th;
            } catch (Exception e6) {
                throw new OAuthCommunicationException(e6);
            }
        }
    }

    protected void handleUnexpectedResponse(int statusCode, HttpResponse response) throws Exception {
        if (response == null) {
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent()));
        StringBuilder responseBody = new StringBuilder();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            responseBody.append(line);
        }
        switch (statusCode) {
            case 401:
                throw new OAuthNotAuthorizedException(responseBody.toString());
            default:
                throw new OAuthCommunicationException("Service provider responded in error: " + statusCode + " (" + response.getReasonPhrase() + ")", responseBody.toString());
        }
    }

    protected void closeConnection(HttpRequest request, HttpResponse response) throws Exception {
    }

    @Override // oauth.signpost.OAuthProvider
    public HttpParameters getResponseParameters() {
        return this.responseParameters;
    }

    protected String getResponseParameter(String key) {
        return this.responseParameters.getFirst(key);
    }

    @Override // oauth.signpost.OAuthProvider
    public void setResponseParameters(HttpParameters parameters) {
        this.responseParameters = parameters;
    }

    @Override // oauth.signpost.OAuthProvider
    public void setOAuth10a(boolean isOAuth10aProvider) {
        this.isOAuth10a = isOAuth10aProvider;
    }

    @Override // oauth.signpost.OAuthProvider
    public boolean isOAuth10a() {
        return this.isOAuth10a;
    }

    @Override // oauth.signpost.OAuthProvider
    public String getRequestTokenEndpointUrl() {
        return this.requestTokenEndpointUrl;
    }

    @Override // oauth.signpost.OAuthProvider
    public String getAccessTokenEndpointUrl() {
        return this.accessTokenEndpointUrl;
    }

    @Override // oauth.signpost.OAuthProvider
    public String getAuthorizationWebsiteUrl() {
        return this.authorizationWebsiteUrl;
    }

    @Override // oauth.signpost.OAuthProvider
    public void setRequestHeader(String header, String value) {
        this.defaultHeaders.put(header, value);
    }

    @Override // oauth.signpost.OAuthProvider
    public Map<String, String> getRequestHeaders() {
        return this.defaultHeaders;
    }

    @Override // oauth.signpost.OAuthProvider
    public void setListener(OAuthProviderListener listener) {
        this.listener = listener;
    }

    @Override // oauth.signpost.OAuthProvider
    public void removeListener(OAuthProviderListener listener) {
        this.listener = null;
    }
}
