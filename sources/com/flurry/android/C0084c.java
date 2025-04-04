package com.flurry.android;

import java.io.DataInput;
import java.io.DataOutput;

/* renamed from: com.flurry.android.c */
/* loaded from: classes.dex */
final class C0084c extends AbstractC0080aj {

    /* renamed from: A */
    private int f119A;

    /* renamed from: B */
    private int f120B;

    /* renamed from: C */
    private int f121C;

    /* renamed from: D */
    private int f122D;

    /* renamed from: E */
    private int f123E;

    /* renamed from: F */
    private int f124F;

    /* renamed from: G */
    private int f125G;

    /* renamed from: H */
    private int f126H;

    /* renamed from: I */
    private int f127I;

    /* renamed from: J */
    private int f128J;

    /* renamed from: K */
    private int f129K;

    /* renamed from: L */
    private int f130L;

    /* renamed from: M */
    private int f131M;

    /* renamed from: N */
    private int f132N;

    /* renamed from: O */
    private int f133O;

    /* renamed from: P */
    private int f134P;

    /* renamed from: Q */
    private int f135Q;

    /* renamed from: R */
    private int f136R;

    /* renamed from: S */
    private int f137S;

    /* renamed from: T */
    private int f138T;

    /* renamed from: U */
    private int f139U;

    /* renamed from: V */
    private int f140V;

    /* renamed from: W */
    private int f141W;

    /* renamed from: X */
    private int f142X;

    /* renamed from: Y */
    private int f143Y;

    /* renamed from: Z */
    private int f144Z;

    /* renamed from: a */
    byte f145a;

    /* renamed from: aa */
    private int f146aa;

    /* renamed from: ab */
    private int f147ab;

    /* renamed from: ac */
    private int f148ac;

    /* renamed from: ad */
    private int f149ad;

    /* renamed from: ae */
    private int f150ae;

    /* renamed from: af */
    private int f151af;

    /* renamed from: ag */
    private boolean f152ag;

    /* renamed from: b */
    String f153b;

    /* renamed from: c */
    long f154c;

    /* renamed from: d */
    String f155d;

    /* renamed from: e */
    int f156e;

    /* renamed from: f */
    int f157f;

    /* renamed from: g */
    String f158g;

    /* renamed from: h */
    int f159h;

    /* renamed from: i */
    int f160i;

    /* renamed from: j */
    String f161j;

    /* renamed from: k */
    int f162k;

    /* renamed from: l */
    int f163l;

    /* renamed from: m */
    int f164m;

    /* renamed from: n */
    int f165n;

    /* renamed from: o */
    int f166o;

    /* renamed from: p */
    int f167p;

    /* renamed from: q */
    int f168q;

    /* renamed from: r */
    private long f169r;

    /* renamed from: s */
    private String f170s;

    /* renamed from: t */
    private int f171t;

    /* renamed from: u */
    private int f172u;

    /* renamed from: v */
    private String f173v;

    /* renamed from: w */
    private int f174w;

    /* renamed from: x */
    private int f175x;

    /* renamed from: y */
    private String f176y;

    /* renamed from: z */
    private int f177z;

    C0084c() {
    }

    C0084c(DataInput dataInput) {
        m84c(dataInput);
    }

    /* renamed from: c */
    private void m84c(DataInput dataInput) {
        this.f145a = dataInput.readByte();
        this.f153b = dataInput.readUTF();
        this.f154c = dataInput.readLong();
        this.f169r = dataInput.readLong();
        this.f155d = dataInput.readUTF();
        this.f156e = dataInput.readUnsignedShort();
        this.f157f = dataInput.readInt();
        this.f158g = dataInput.readUTF();
        this.f159h = dataInput.readUnsignedShort();
        this.f160i = dataInput.readInt();
        this.f161j = dataInput.readUTF();
        this.f162k = dataInput.readUnsignedShort();
        this.f163l = dataInput.readInt();
    }

    /* renamed from: a */
    final void m85a(DataInput dataInput) {
        this.f170s = dataInput.readUTF();
        this.f171t = dataInput.readUnsignedShort();
        this.f172u = dataInput.readInt();
        this.f173v = dataInput.readUTF();
        this.f174w = dataInput.readUnsignedShort();
        this.f175x = dataInput.readInt();
        this.f176y = dataInput.readUTF();
        this.f177z = dataInput.readUnsignedShort();
        this.f119A = dataInput.readInt();
        this.f120B = dataInput.readInt();
        this.f121C = dataInput.readInt();
        this.f164m = dataInput.readInt();
        this.f165n = dataInput.readInt();
        this.f166o = dataInput.readInt();
        this.f167p = dataInput.readInt();
        this.f122D = dataInput.readInt();
        this.f123E = dataInput.readInt();
        this.f124F = dataInput.readInt();
        this.f125G = dataInput.readInt();
        this.f126H = dataInput.readInt();
        this.f127I = dataInput.readInt();
        this.f128J = dataInput.readInt();
        this.f129K = dataInput.readInt();
        this.f168q = dataInput.readInt();
        this.f130L = dataInput.readInt();
        this.f131M = dataInput.readInt();
        this.f132N = dataInput.readInt();
        this.f133O = dataInput.readInt();
        this.f134P = dataInput.readInt();
        this.f135Q = dataInput.readInt();
        this.f136R = dataInput.readInt();
        this.f137S = dataInput.readInt();
        this.f138T = dataInput.readInt();
        this.f139U = dataInput.readInt();
        this.f140V = dataInput.readInt();
        this.f141W = dataInput.readInt();
        this.f142X = dataInput.readInt();
        this.f143Y = dataInput.readInt();
        this.f144Z = dataInput.readInt();
        this.f146aa = dataInput.readInt();
        this.f147ab = dataInput.readInt();
        this.f148ac = dataInput.readInt();
        this.f149ad = dataInput.readInt();
        this.f150ae = dataInput.readInt();
        this.f151af = dataInput.readInt();
        this.f152ag = true;
    }

    /* renamed from: b */
    final void m87b(DataInput dataInput) {
        m84c(dataInput);
        this.f152ag = dataInput.readBoolean();
        if (this.f152ag) {
            m85a(dataInput);
        }
    }

    /* renamed from: a */
    final void m86a(DataOutput dataOutput) {
        dataOutput.writeByte(this.f145a);
        dataOutput.writeUTF(this.f153b);
        dataOutput.writeLong(this.f154c);
        dataOutput.writeLong(this.f169r);
        dataOutput.writeUTF(this.f155d);
        dataOutput.writeShort(this.f156e);
        dataOutput.writeInt(this.f157f);
        dataOutput.writeUTF(this.f158g);
        dataOutput.writeShort(this.f159h);
        dataOutput.writeInt(this.f160i);
        dataOutput.writeUTF(this.f161j);
        dataOutput.writeShort(this.f162k);
        dataOutput.writeInt(this.f163l);
        dataOutput.writeBoolean(this.f152ag);
        if (this.f152ag) {
            dataOutput.writeUTF(this.f170s);
            dataOutput.writeShort(this.f171t);
            dataOutput.writeInt(this.f172u);
            dataOutput.writeUTF(this.f173v);
            dataOutput.writeShort(this.f174w);
            dataOutput.writeInt(this.f175x);
            dataOutput.writeUTF(this.f176y);
            dataOutput.writeShort(this.f177z);
            dataOutput.writeInt(this.f119A);
            dataOutput.writeInt(this.f120B);
            dataOutput.writeInt(this.f121C);
            dataOutput.writeInt(this.f164m);
            dataOutput.writeInt(this.f165n);
            dataOutput.writeInt(this.f166o);
            dataOutput.writeInt(this.f167p);
            dataOutput.writeInt(this.f122D);
            dataOutput.writeInt(this.f123E);
            dataOutput.writeInt(this.f124F);
            dataOutput.writeInt(this.f125G);
            dataOutput.writeInt(this.f126H);
            dataOutput.writeInt(this.f127I);
            dataOutput.writeInt(this.f128J);
            dataOutput.writeInt(this.f129K);
            dataOutput.writeInt(this.f168q);
            dataOutput.writeInt(this.f130L);
            dataOutput.writeInt(this.f131M);
            dataOutput.writeInt(this.f132N);
            dataOutput.writeInt(this.f133O);
            dataOutput.writeInt(this.f134P);
            dataOutput.writeInt(this.f135Q);
            dataOutput.writeInt(this.f136R);
            dataOutput.writeInt(this.f137S);
            dataOutput.writeInt(this.f138T);
            dataOutput.writeInt(this.f139U);
            dataOutput.writeInt(this.f140V);
            dataOutput.writeInt(this.f141W);
            dataOutput.writeInt(this.f142X);
            dataOutput.writeInt(this.f143Y);
            dataOutput.writeInt(this.f144Z);
            dataOutput.writeInt(this.f146aa);
            dataOutput.writeInt(this.f147ab);
            dataOutput.writeInt(this.f148ac);
            dataOutput.writeInt(this.f149ad);
            dataOutput.writeInt(this.f150ae);
            dataOutput.writeInt(this.f151af);
        }
    }
}
