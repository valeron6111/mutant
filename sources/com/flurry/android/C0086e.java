package com.flurry.android;

import java.io.DataInput;

/* renamed from: com.flurry.android.e */
/* loaded from: classes.dex */
final class C0086e extends AbstractC0080aj {

    /* renamed from: a */
    String f181a;

    /* renamed from: b */
    byte f182b;

    /* renamed from: c */
    byte f183c;

    /* renamed from: d */
    C0084c f184d;

    C0086e() {
    }

    C0086e(DataInput dataInput) {
        this.f181a = dataInput.readUTF();
        this.f182b = dataInput.readByte();
        this.f183c = dataInput.readByte();
    }

    public final String toString() {
        return "{name: " + this.f181a + ", blockId: " + ((int) this.f182b) + ", themeId: " + ((int) this.f183c);
    }
}
