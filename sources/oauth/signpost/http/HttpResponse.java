package oauth.signpost.http;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public interface HttpResponse {
    InputStream getContent() throws IOException;

    String getReasonPhrase() throws Exception;

    int getStatusCode() throws IOException;

    Object unwrap();
}
