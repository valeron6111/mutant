package com.flurry.android;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/* renamed from: com.flurry.android.n */
/* loaded from: classes.dex */
final class C0095n implements X509TrustManager {
    C0095n() {
    }

    @Override // javax.net.ssl.X509TrustManager
    public final void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
    }

    @Override // javax.net.ssl.X509TrustManager
    public final void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
    }

    @Override // javax.net.ssl.X509TrustManager
    public final X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
