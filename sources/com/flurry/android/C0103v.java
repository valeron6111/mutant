package com.flurry.android;

import java.io.DataInput;

/* renamed from: com.flurry.android.v */
/* loaded from: classes.dex */
final class C0103v extends AbstractC0080aj {

    /* renamed from: a */
    long f240a;

    /* renamed from: b */
    long f241b;

    /* renamed from: c */
    String f242c;

    /* renamed from: d */
    String f243d;

    /* renamed from: e */
    long f244e;

    /* renamed from: f */
    Long f245f;

    /* renamed from: g */
    byte[] f246g;

    /* renamed from: h */
    AdImage f247h;

    C0103v() {
    }

    C0103v(DataInput dataInput) {
        m151b(dataInput);
    }

    /* renamed from: a */
    final void m152a(DataInput dataInput) {
        m151b(dataInput);
    }

    /* renamed from: b */
    private void m151b(DataInput dataInput) {
        this.f240a = dataInput.readLong();
        this.f241b = dataInput.readLong();
        this.f243d = dataInput.readUTF();
        this.f242c = dataInput.readUTF();
        this.f244e = dataInput.readLong();
        this.f245f = Long.valueOf(dataInput.readLong());
        this.f246g = new byte[dataInput.readUnsignedByte()];
        dataInput.readFully(this.f246g);
    }

    public final String toString() {
        return "ad {id=" + this.f240a + ", name='" + this.f243d + "', cookie: '" + m150a(this.f246g) + "'}";
    }

    /* renamed from: a */
    private static String m150a(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length; i++) {
            int i2 = (bArr[i] >> 4) & 15;
            if (i2 < 10) {
                sb.append((char) (i2 + 48));
            } else {
                sb.append((char) ((i2 + 65) - 10));
            }
            int i3 = bArr[i] & 15;
            if (i3 < 10) {
                sb.append((char) (i3 + 48));
            } else {
                sb.append((char) ((i3 + 65) - 10));
            }
        }
        return sb.toString();
    }
}
