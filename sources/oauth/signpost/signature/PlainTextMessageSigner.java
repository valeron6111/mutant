package oauth.signpost.signature;

import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;

/* loaded from: classes.dex */
public class PlainTextMessageSigner extends OAuthMessageSigner {
    @Override // oauth.signpost.signature.OAuthMessageSigner
    public String getSignatureMethod() {
        return "PLAINTEXT";
    }

    @Override // oauth.signpost.signature.OAuthMessageSigner
    public String sign(HttpRequest request, HttpParameters requestParams) throws OAuthMessageSignerException {
        return OAuth.percentEncode(getConsumerSecret()) + '&' + OAuth.percentEncode(getTokenSecret());
    }
}
