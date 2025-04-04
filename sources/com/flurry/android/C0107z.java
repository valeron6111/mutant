package com.flurry.android;

import android.content.Context;
import com.alawar.mutant.jni.MutantMessages;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* renamed from: com.flurry.android.z */
/* loaded from: classes.dex */
final class C0107z {

    /* renamed from: a */
    private Context f257a;

    /* renamed from: b */
    private ViewOnClickListenerC0102u f258b;

    /* renamed from: c */
    private C0070a f259c;

    /* renamed from: d */
    private volatile long f260d;

    /* renamed from: e */
    private C0076af f261e = new C0076af(100);

    /* renamed from: f */
    private C0076af f262f = new C0076af(100);

    /* renamed from: g */
    private Map f263g = new HashMap();

    /* renamed from: h */
    private Map f264h = new HashMap();

    /* renamed from: i */
    private Map f265i = new HashMap();

    /* renamed from: j */
    private Map f266j = new HashMap();

    /* renamed from: k */
    private volatile boolean f267k;

    C0107z() {
    }

    /* renamed from: a */
    final void m171a(Context context, ViewOnClickListenerC0102u viewOnClickListenerC0102u, C0070a c0070a) {
        this.f257a = context;
        this.f258b = viewOnClickListenerC0102u;
        this.f259c = c0070a;
    }

    /* renamed from: a */
    final synchronized C0103v[] m173a(String str) {
        C0103v[] c0103vArr;
        c0103vArr = (C0103v[]) this.f263g.get(str);
        if (c0103vArr == null) {
            c0103vArr = (C0103v[]) this.f263g.get(MutantMessages.sEmpty);
        }
        return c0103vArr;
    }

    /* renamed from: a */
    final synchronized C0082al m169a(long j) {
        return (C0082al) this.f262f.m64a(Long.valueOf(j));
    }

    /* renamed from: a */
    final synchronized Set m170a() {
        return this.f261e.m67c();
    }

    /* renamed from: b */
    final synchronized AdImage m174b(long j) {
        return (AdImage) this.f261e.m64a(Long.valueOf(j));
    }

    /* renamed from: a */
    final synchronized AdImage m168a(short s) {
        Long l;
        l = (Long) this.f266j.get((short) 1);
        return l == null ? null : m174b(l.longValue());
    }

    /* renamed from: b */
    final synchronized C0086e m175b(String str) {
        C0086e c0086e;
        c0086e = (C0086e) this.f264h.get(str);
        if (c0086e == null) {
            c0086e = (C0086e) this.f264h.get(MutantMessages.sEmpty);
        }
        return c0086e;
    }

    /* renamed from: b */
    final boolean m176b() {
        return this.f267k;
    }

    /* renamed from: a */
    private synchronized C0084c m161a(byte b) {
        return (C0084c) this.f265i.get(Byte.valueOf(b));
    }

    /* renamed from: a */
    final synchronized void m172a(Map map, Map map2, Map map3, Map map4, Map map5, Map map6) {
        this.f260d = System.currentTimeMillis();
        for (Map.Entry entry : map4.entrySet()) {
            if (entry.getValue() != null) {
                this.f261e.m65a(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry entry2 : map5.entrySet()) {
            if (entry2.getValue() != null) {
                this.f262f.m65a(entry2.getKey(), entry2.getValue());
            }
        }
        if (map2 != null && !map2.isEmpty()) {
            this.f264h = map2;
        }
        if (map3 != null && !map3.isEmpty()) {
            this.f265i = map3;
        }
        if (map6 != null && !map6.isEmpty()) {
            this.f266j = map6;
        }
        this.f263g = new HashMap();
        for (Map.Entry entry3 : map2.entrySet()) {
            C0086e c0086e = (C0086e) entry3.getValue();
            C0103v[] c0103vArr = (C0103v[]) map.get(Byte.valueOf(c0086e.f182b));
            if (c0103vArr != null) {
                this.f263g.put(entry3.getKey(), c0103vArr);
            }
            C0084c c0084c = (C0084c) map3.get(Byte.valueOf(c0086e.f183c));
            if (c0084c != null) {
                c0086e.f184d = c0084c;
            }
        }
        m166f();
        m162a(CallbackEvent.ADS_UPDATED);
    }

    /* renamed from: c */
    final long m177c() {
        return this.f260d;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context] */
    /* JADX WARN: Type inference failed for: r1v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7, types: [java.io.Closeable] */
    /* renamed from: d */
    final synchronized void m178d() {
        DataInputStream dataInputStream;
        ?? r0 = this.f257a;
        ?? m167g = m167g();
        File fileStreamPath = r0.getFileStreamPath(m167g);
        if (fileStreamPath.exists()) {
            try {
                try {
                    dataInputStream = new DataInputStream(new FileInputStream(fileStreamPath));
                } catch (Throwable th) {
                    th = th;
                    dataInputStream = null;
                }
                try {
                    if (dataInputStream.readUnsignedShort() == 46587) {
                        m163a(dataInputStream);
                        m162a(CallbackEvent.ADS_LOADED_FROM_CACHE);
                    } else {
                        m165a(fileStreamPath);
                    }
                    C0099r.m101a(dataInputStream);
                } catch (Throwable th2) {
                    th = th2;
                    C0078ah.m73a("FlurryAgent", "Discarding cache", th);
                    m165a(fileStreamPath);
                    C0099r.m101a(dataInputStream);
                }
            } catch (Throwable th3) {
                th = th3;
                C0099r.m101a((Closeable) m167g);
                throw th;
            }
        } else {
            C0078ah.m80c("FlurryAgent", "cache file does not exist, path=" + fileStreamPath.getAbsolutePath());
        }
    }

    /* renamed from: a */
    private static void m165a(File file) {
        if (!file.delete()) {
            C0078ah.m77b("FlurryAgent", "Cannot delete cached ads");
        }
    }

    /* renamed from: f */
    private void m166f() {
        Iterator it = this.f265i.values().iterator();
        while (it.hasNext()) {
            it.next();
        }
        for (C0103v[] c0103vArr : this.f263g.values()) {
            if (c0103vArr != null) {
                for (C0103v c0103v : c0103vArr) {
                    c0103v.f247h = m174b(c0103v.f245f.longValue());
                    if (c0103v.f247h == null) {
                        C0078ah.m77b("FlurryAgent", "Ad " + c0103v.f243d + " has no image");
                    }
                    if (m169a(c0103v.f240a) == null) {
                        C0078ah.m77b("FlurryAgent", "Ad " + c0103v.f243d + " has no pricing");
                    }
                }
            }
        }
        for (C0086e c0086e : this.f264h.values()) {
            c0086e.f184d = m161a(c0086e.f183c);
            if (c0086e.f184d == null) {
                C0078ah.m82d("FlurryAgent", "No ad theme found for " + ((int) c0086e.f183c));
            }
        }
    }

    /* renamed from: e */
    final synchronized void m179e() {
        DataOutputStream dataOutputStream = null;
        synchronized (this) {
            try {
                try {
                    File fileStreamPath = this.f257a.getFileStreamPath(m167g());
                    File parentFile = fileStreamPath.getParentFile();
                    if (!parentFile.mkdirs() && !parentFile.exists()) {
                        C0078ah.m77b("FlurryAgent", "Unable to create persistent dir: " + parentFile);
                        C0099r.m101a((Closeable) null);
                    } else {
                        DataOutputStream dataOutputStream2 = new DataOutputStream(new FileOutputStream(fileStreamPath));
                        try {
                            dataOutputStream2.writeShort(46587);
                            m164a(dataOutputStream2);
                            C0099r.m101a(dataOutputStream2);
                        } catch (Throwable th) {
                            th = th;
                            dataOutputStream = dataOutputStream2;
                            C0099r.m101a(dataOutputStream);
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }
    }

    /* renamed from: a */
    private void m163a(DataInputStream dataInputStream) {
        C0078ah.m72a("FlurryAgent", "Reading cache");
        if (dataInputStream.readUnsignedShort() == 2) {
            this.f260d = dataInputStream.readLong();
            int readUnsignedShort = dataInputStream.readUnsignedShort();
            this.f261e = new C0076af(100);
            for (int i = 0; i < readUnsignedShort; i++) {
                long readLong = dataInputStream.readLong();
                AdImage adImage = new AdImage();
                adImage.m0a(dataInputStream);
                this.f261e.m65a(Long.valueOf(readLong), adImage);
            }
            int readUnsignedShort2 = dataInputStream.readUnsignedShort();
            this.f262f = new C0076af(100);
            for (int i2 = 0; i2 < readUnsignedShort2; i2++) {
                long readLong2 = dataInputStream.readLong();
                C0082al c0082al = new C0082al();
                if (dataInputStream.readBoolean()) {
                    c0082al.f113a = dataInputStream.readUTF();
                }
                if (dataInputStream.readBoolean()) {
                    c0082al.f114b = dataInputStream.readUTF();
                }
                c0082al.f115c = dataInputStream.readInt();
                this.f262f.m65a(Long.valueOf(readLong2), c0082al);
            }
            int readUnsignedShort3 = dataInputStream.readUnsignedShort();
            this.f264h = new HashMap(readUnsignedShort3);
            for (int i3 = 0; i3 < readUnsignedShort3; i3++) {
                this.f264h.put(dataInputStream.readUTF(), new C0086e(dataInputStream));
            }
            int readUnsignedShort4 = dataInputStream.readUnsignedShort();
            this.f263g = new HashMap(readUnsignedShort4);
            for (int i4 = 0; i4 < readUnsignedShort4; i4++) {
                String readUTF = dataInputStream.readUTF();
                int readUnsignedShort5 = dataInputStream.readUnsignedShort();
                C0103v[] c0103vArr = new C0103v[readUnsignedShort5];
                for (int i5 = 0; i5 < readUnsignedShort5; i5++) {
                    C0103v c0103v = new C0103v();
                    c0103v.m152a(dataInputStream);
                    c0103vArr[i5] = c0103v;
                }
                this.f263g.put(readUTF, c0103vArr);
            }
            int readUnsignedShort6 = dataInputStream.readUnsignedShort();
            this.f265i = new HashMap();
            for (int i6 = 0; i6 < readUnsignedShort6; i6++) {
                byte readByte = dataInputStream.readByte();
                C0084c c0084c = new C0084c();
                c0084c.m87b(dataInputStream);
                this.f265i.put(Byte.valueOf(readByte), c0084c);
            }
            int readUnsignedShort7 = dataInputStream.readUnsignedShort();
            this.f266j = new HashMap(readUnsignedShort7);
            for (int i7 = 0; i7 < readUnsignedShort7; i7++) {
                this.f266j.put(Short.valueOf(dataInputStream.readShort()), Long.valueOf(dataInputStream.readLong()));
            }
            m166f();
            C0078ah.m72a("FlurryAgent", "Cache read, num images: " + this.f261e.m63a());
        }
    }

    /* renamed from: a */
    private void m164a(DataOutputStream dataOutputStream) {
        dataOutputStream.writeShort(2);
        dataOutputStream.writeLong(this.f260d);
        List<Map.Entry> m66b = this.f261e.m66b();
        dataOutputStream.writeShort(m66b.size());
        for (Map.Entry entry : m66b) {
            dataOutputStream.writeLong(((Long) entry.getKey()).longValue());
            AdImage adImage = (AdImage) entry.getValue();
            dataOutputStream.writeLong(adImage.f3a);
            dataOutputStream.writeInt(adImage.f4b);
            dataOutputStream.writeInt(adImage.f5c);
            dataOutputStream.writeUTF(adImage.f6d);
            dataOutputStream.writeInt(adImage.f7e.length);
            dataOutputStream.write(adImage.f7e);
        }
        List<Map.Entry> m66b2 = this.f262f.m66b();
        dataOutputStream.writeShort(m66b2.size());
        for (Map.Entry entry2 : m66b2) {
            dataOutputStream.writeLong(((Long) entry2.getKey()).longValue());
            C0082al c0082al = (C0082al) entry2.getValue();
            boolean z = c0082al.f113a != null;
            dataOutputStream.writeBoolean(z);
            if (z) {
                dataOutputStream.writeUTF(c0082al.f113a);
            }
            boolean z2 = c0082al.f114b != null;
            dataOutputStream.writeBoolean(z2);
            if (z2) {
                dataOutputStream.writeUTF(c0082al.f114b);
            }
            dataOutputStream.writeInt(c0082al.f115c);
        }
        dataOutputStream.writeShort(this.f264h.size());
        for (Map.Entry entry3 : this.f264h.entrySet()) {
            dataOutputStream.writeUTF((String) entry3.getKey());
            C0086e c0086e = (C0086e) entry3.getValue();
            dataOutputStream.writeUTF(c0086e.f181a);
            dataOutputStream.writeByte(c0086e.f182b);
            dataOutputStream.writeByte(c0086e.f183c);
        }
        dataOutputStream.writeShort(this.f263g.size());
        for (Map.Entry entry4 : this.f263g.entrySet()) {
            dataOutputStream.writeUTF((String) entry4.getKey());
            C0103v[] c0103vArr = (C0103v[]) entry4.getValue();
            int length = c0103vArr == null ? 0 : c0103vArr.length;
            dataOutputStream.writeShort(length);
            for (int i = 0; i < length; i++) {
                C0103v c0103v = c0103vArr[i];
                dataOutputStream.writeLong(c0103v.f240a);
                dataOutputStream.writeLong(c0103v.f241b);
                dataOutputStream.writeUTF(c0103v.f243d);
                dataOutputStream.writeUTF(c0103v.f242c);
                dataOutputStream.writeLong(c0103v.f244e);
                dataOutputStream.writeLong(c0103v.f245f.longValue());
                dataOutputStream.writeByte(c0103v.f246g.length);
                dataOutputStream.write(c0103v.f246g);
            }
        }
        dataOutputStream.writeShort(this.f265i.size());
        for (Map.Entry entry5 : this.f265i.entrySet()) {
            dataOutputStream.writeByte(((Byte) entry5.getKey()).byteValue());
            ((C0084c) entry5.getValue()).m86a(dataOutputStream);
        }
        dataOutputStream.writeShort(this.f266j.size());
        for (Map.Entry entry6 : this.f266j.entrySet()) {
            dataOutputStream.writeShort(((Short) entry6.getKey()).shortValue());
            dataOutputStream.writeLong(((Long) entry6.getValue()).longValue());
        }
    }

    /* renamed from: g */
    private String m167g() {
        return ".flurryappcircle." + Integer.toString(this.f259c.f82a.hashCode(), 16);
    }

    /* renamed from: a */
    private void m162a(int i) {
        this.f267k = !this.f263g.isEmpty();
        if (this.f267k) {
            this.f258b.m121a(i);
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("adImages (" + this.f261e.m66b().size() + "),\n");
        sb.append("adBlock (" + this.f263g.size() + "):").append(",\n");
        for (Map.Entry entry : this.f263g.entrySet()) {
            sb.append("\t" + ((String) entry.getKey()) + ": " + Arrays.toString((Object[]) entry.getValue()));
        }
        sb.append("adHooks (" + this.f264h.size() + "):" + this.f264h).append(",\n");
        sb.append("adThemes (" + this.f265i.size() + "):" + this.f265i).append(",\n");
        sb.append("auxMap (" + this.f266j.size() + "):" + this.f266j).append(",\n");
        sb.append("}");
        return sb.toString();
    }
}
