package com.flurry.android;

import java.io.DataOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.flurry.android.p */
/* loaded from: classes.dex */
final class C0097p {

    /* renamed from: a */
    final String f205a;

    /* renamed from: b */
    C0103v f206b;

    /* renamed from: c */
    long f207c;

    /* renamed from: d */
    List f208d;

    /* renamed from: e */
    private byte f209e;

    C0097p(C0097p c0097p, long j) {
        this(c0097p.f205a, c0097p.f209e, j);
        this.f206b = c0097p.f206b;
        this.f207c = c0097p.f207c;
    }

    C0097p(String str, byte b, long j) {
        this.f208d = new ArrayList();
        this.f205a = str;
        this.f209e = b;
        this.f208d.add(new C0087f((byte) 1, j));
    }

    /* renamed from: a */
    final void m95a(C0087f c0087f) {
        this.f208d.add(c0087f);
    }

    /* renamed from: a */
    final long m94a() {
        return ((C0087f) this.f208d.get(0)).f186b;
    }

    /* renamed from: a */
    final void m96a(DataOutput dataOutput) {
        dataOutput.writeUTF(this.f205a);
        dataOutput.writeByte(this.f209e);
        if (this.f206b == null) {
            dataOutput.writeLong(0L);
            dataOutput.writeLong(0L);
            dataOutput.writeByte(0);
        } else {
            dataOutput.writeLong(this.f206b.f240a);
            dataOutput.writeLong(this.f206b.f244e);
            byte[] bArr = this.f206b.f246g;
            dataOutput.writeByte(bArr.length);
            dataOutput.write(bArr);
        }
        dataOutput.writeShort(this.f208d.size());
        for (C0087f c0087f : this.f208d) {
            dataOutput.writeByte(c0087f.f185a);
            dataOutput.writeLong(c0087f.f186b);
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{hook: " + this.f205a + ", ad: " + this.f206b.f243d + ", transitions: [");
        Iterator it = this.f208d.iterator();
        while (it.hasNext()) {
            sb.append((C0087f) it.next());
            sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }
}
