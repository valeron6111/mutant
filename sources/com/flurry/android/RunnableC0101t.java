package com.flurry.android;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/* renamed from: com.flurry.android.t */
/* loaded from: classes.dex */
final class RunnableC0101t implements Runnable {

    /* renamed from: a */
    private /* synthetic */ Map f211a;

    /* renamed from: b */
    private /* synthetic */ InstallReceiver f212b;

    RunnableC0101t(InstallReceiver installReceiver, Map map) {
        this.f212b = installReceiver;
        this.f211a = map;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v8, types: [java.util.Map$Entry] */
    @Override // java.lang.Runnable
    public final void run() {
        File file;
        File file2;
        DataOutputStream dataOutputStream = null;
        dataOutputStream = null;
        DataOutputStream dataOutputStream2 = null;
        dataOutputStream = null;
        try {
            try {
                file = this.f212b.f70b;
                File parentFile = file.getParentFile();
                if (parentFile.mkdirs() || parentFile.exists()) {
                    file2 = this.f212b.f70b;
                    DataOutputStream dataOutputStream3 = new DataOutputStream(new FileOutputStream(file2));
                    try {
                        boolean z = true;
                        for (?? r2 : this.f211a.entrySet()) {
                            if (z) {
                                z = false;
                            } else {
                                dataOutputStream3.writeUTF("&");
                            }
                            dataOutputStream3.writeUTF((String) r2.getKey());
                            dataOutputStream3.writeUTF("=");
                            dataOutputStream3.writeUTF((String) r2.getValue());
                            dataOutputStream2 = r2;
                            z = z;
                        }
                        dataOutputStream3.writeShort(0);
                        C0099r.m101a(dataOutputStream3);
                        dataOutputStream = dataOutputStream2;
                    } catch (Throwable th) {
                        th = th;
                        dataOutputStream = dataOutputStream3;
                        C0099r.m101a(dataOutputStream);
                        throw th;
                    }
                } else {
                    C0078ah.m77b("InstallReceiver", "Unable to create persistent dir: " + parentFile);
                    C0099r.m101a((Closeable) null);
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }
}
