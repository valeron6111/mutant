package com.flurry.android;

import java.net.Socket;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;

/* renamed from: com.flurry.android.ai */
/* loaded from: classes.dex */
final class C0079ai extends SSLSocketFactory {

    /* renamed from: a */
    private SSLContext f108a;

    public C0079ai(FlurryAgent flurryAgent, KeyStore keyStore) {
        super(keyStore);
        this.f108a = SSLContext.getInstance("TLS");
        this.f108a.init(null, new TrustManager[]{new C0095n()}, null);
    }

    @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.LayeredSocketFactory
    public final Socket createSocket(Socket socket, String str, int i, boolean z) {
        return this.f108a.getSocketFactory().createSocket(socket, str, i, z);
    }

    @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.SocketFactory
    public final Socket createSocket() {
        return this.f108a.getSocketFactory().createSocket();
    }
}
