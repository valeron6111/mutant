package com.flurry.android;

import com.alawar.mutant.jni.MutantMessages;

/* loaded from: classes.dex */
public final class Offer {

    /* renamed from: a */
    private long f71a;

    /* renamed from: b */
    private String f72b;

    /* renamed from: c */
    private String f73c;

    /* renamed from: d */
    private int f74d;

    /* renamed from: e */
    private AdImage f75e;

    Offer(long j, AdImage adImage, String str, String str2, int i) {
        this.f71a = j;
        this.f72b = str;
        this.f75e = adImage;
        this.f73c = str2;
        this.f74d = i;
    }

    public final long getId() {
        return this.f71a;
    }

    public final String getName() {
        return this.f72b;
    }

    public final String getDescription() {
        return this.f73c;
    }

    public final int getPrice() {
        return this.f74d;
    }

    public final String getUrl() {
        return MutantMessages.sEmpty;
    }

    public final AdImage getImage() {
        return this.f75e;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[id=" + this.f71a + ",name=" + this.f72b + ",price=" + this.f74d + ", image size: " + this.f75e.f7e.length);
        return sb.toString();
    }
}
