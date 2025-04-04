package com.flurry.android;

import com.sponsorpay.sdk.android.publisher.SponsorPayPublisher;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/* renamed from: com.flurry.android.i */
/* loaded from: classes.dex */
final class C0090i {

    /* renamed from: a */
    private String f189a;

    /* renamed from: b */
    private Map f190b;

    /* renamed from: c */
    private long f191c;

    /* renamed from: d */
    private boolean f192d;

    /* renamed from: e */
    private long f193e;

    public C0090i(String str, Map map, long j, boolean z) {
        this.f189a = str;
        this.f190b = map;
        this.f191c = j;
        this.f192d = z;
    }

    /* renamed from: a */
    public final boolean m89a(String str) {
        return this.f192d && this.f193e == 0 && this.f189a.equals(str);
    }

    /* renamed from: a */
    public final void m88a(long j) {
        this.f193e = j - this.f191c;
        C0078ah.m72a("FlurryAgent", "Ended event '" + this.f189a + "' (" + this.f191c + ") after " + this.f193e + "ms");
    }

    /* renamed from: a */
    public final byte[] m90a() {
        DataOutputStream dataOutputStream;
        Throwable th;
        DataOutputStream dataOutputStream2;
        byte[] bArr;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeUTF(this.f189a);
                if (this.f190b == null) {
                    dataOutputStream.writeShort(0);
                } else {
                    dataOutputStream.writeShort(this.f190b.size());
                    for (Map.Entry entry : this.f190b.entrySet()) {
                        dataOutputStream.writeUTF(C0099r.m99a((String) entry.getKey(), SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE));
                        dataOutputStream.writeUTF(C0099r.m99a((String) entry.getValue(), SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE));
                    }
                }
                dataOutputStream.writeLong(this.f191c);
                dataOutputStream.writeLong(this.f193e);
                dataOutputStream.flush();
                bArr = byteArrayOutputStream.toByteArray();
                C0099r.m101a(dataOutputStream);
            } catch (IOException e) {
                dataOutputStream2 = dataOutputStream;
                try {
                    bArr = new byte[0];
                    C0099r.m101a(dataOutputStream2);
                    return bArr;
                } catch (Throwable th2) {
                    th = th2;
                    dataOutputStream = dataOutputStream2;
                    C0099r.m101a(dataOutputStream);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                C0099r.m101a(dataOutputStream);
                throw th;
            }
        } catch (IOException e2) {
            dataOutputStream2 = null;
        } catch (Throwable th4) {
            dataOutputStream = null;
            th = th4;
        }
        return bArr;
    }
}
