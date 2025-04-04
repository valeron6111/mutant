package com.flurry.android;

/* loaded from: classes.dex */
public final class OfferInSdk {

    /* renamed from: a */
    long f76a;

    /* renamed from: b */
    C0097p f77b;

    /* renamed from: c */
    String f78c;

    /* renamed from: d */
    String f79d;

    /* renamed from: e */
    int f80e;

    /* renamed from: f */
    AdImage f81f;

    OfferInSdk(long j, C0097p c0097p, AdImage adImage, String str, String str2, int i) {
        this.f76a = j;
        this.f77b = c0097p;
        this.f78c = str;
        this.f81f = adImage;
        this.f79d = str2;
        this.f80e = i;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[id=" + this.f76a).append(",name=" + this.f78c + "]");
        return sb.toString();
    }
}
