package oauth.signpost.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/* loaded from: classes.dex */
public interface HttpRequest {
    Map<String, String> getAllHeaders();

    String getContentType();

    String getHeader(String str);

    InputStream getMessagePayload() throws IOException;

    String getMethod();

    String getRequestUrl();

    void setHeader(String str, String str2);

    void setRequestUrl(String str);

    Object unwrap();
}
