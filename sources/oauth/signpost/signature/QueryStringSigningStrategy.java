package oauth.signpost.signature;

import oauth.signpost.OAuth;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;

/* loaded from: classes.dex */
public class QueryStringSigningStrategy implements SigningStrategy {
    private static final long serialVersionUID = 1;

    @Override // oauth.signpost.signature.SigningStrategy
    public String writeSignature(String signature, HttpRequest request, HttpParameters requestParameters) {
        StringBuilder sb = new StringBuilder(OAuth.addQueryParameters(request.getRequestUrl(), OAuth.OAUTH_SIGNATURE, signature));
        if (requestParameters.containsKey(OAuth.OAUTH_TOKEN)) {
            sb.append("&");
            sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_TOKEN));
        }
        if (requestParameters.containsKey(OAuth.OAUTH_CALLBACK)) {
            sb.append("&");
            sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_CALLBACK));
        }
        if (requestParameters.containsKey(OAuth.OAUTH_VERIFIER)) {
            sb.append("&");
            sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_VERIFIER));
        }
        sb.append("&");
        sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_CONSUMER_KEY));
        sb.append("&");
        sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_VERSION));
        sb.append("&");
        sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_SIGNATURE_METHOD));
        sb.append("&");
        sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_TIMESTAMP));
        sb.append("&");
        sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_NONCE));
        String signedUrl = sb.toString();
        request.setRequestUrl(signedUrl);
        return signedUrl;
    }
}
