package com.flurry.android;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import com.alawar.mutant.database.DbBuilder;
import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.internal.request.multipart.FilePart;
import com.sponsorpay.sdk.android.publisher.SponsorPayPublisher;
import com.tapjoy.TapjoyConstants;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.WeakHashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/* loaded from: classes.dex */
public final class FlurryAgent implements LocationListener {

    /* renamed from: a */
    static String f17a;

    /* renamed from: B */
    private List f34B;

    /* renamed from: C */
    private LocationManager f35C;

    /* renamed from: D */
    private String f36D;

    /* renamed from: E */
    private boolean f37E;

    /* renamed from: F */
    private long f38F;

    /* renamed from: H */
    private long f40H;

    /* renamed from: I */
    private long f41I;

    /* renamed from: J */
    private long f42J;

    /* renamed from: P */
    private Long f48P;

    /* renamed from: Q */
    private int f49Q;

    /* renamed from: R */
    private Location f50R;

    /* renamed from: U */
    private boolean f53U;

    /* renamed from: V */
    private int f54V;

    /* renamed from: X */
    private int f56X;

    /* renamed from: q */
    private final Handler f58q;

    /* renamed from: r */
    private File f59r;

    /* renamed from: v */
    private long f63v;

    /* renamed from: x */
    private String f65x;

    /* renamed from: y */
    private String f66y;

    /* renamed from: z */
    private String f67z;

    /* renamed from: b */
    private static final String[] f18b = {"9774d56d682e549c", "dead00beef"};

    /* renamed from: c */
    private static volatile String f19c = null;
    private static volatile String kInsecureReportUrl = "http://data.flurry.com/aap.do";
    private static volatile String kSecureReportUrl = "https://data.flurry.com/aap.do";

    /* renamed from: d */
    private static volatile String f20d = null;

    /* renamed from: e */
    private static volatile String f21e = "http://ad.flurry.com/getCanvas.do";

    /* renamed from: f */
    private static volatile String f22f = null;

    /* renamed from: g */
    private static volatile String f23g = "http://ad.flurry.com/getAndroidApp.do";

    /* renamed from: h */
    private static final FlurryAgent f24h = new FlurryAgent();

    /* renamed from: i */
    private static long f25i = 10000;

    /* renamed from: j */
    private static boolean f26j = true;

    /* renamed from: k */
    private static boolean f27k = false;

    /* renamed from: l */
    private static boolean f28l = false;

    /* renamed from: m */
    private static boolean f29m = true;

    /* renamed from: n */
    private static Criteria f30n = null;

    /* renamed from: o */
    private static boolean f31o = false;

    /* renamed from: p */
    private static AppCircle f32p = new AppCircle();

    /* renamed from: s */
    private File f60s = null;

    /* renamed from: t */
    private volatile boolean f61t = false;

    /* renamed from: u */
    private volatile boolean f62u = false;

    /* renamed from: w */
    private Map f64w = new WeakHashMap();

    /* renamed from: A */
    private boolean f33A = true;

    /* renamed from: G */
    private List f39G = new ArrayList();

    /* renamed from: K */
    private String f43K = MutantMessages.sEmpty;

    /* renamed from: L */
    private String f44L = MutantMessages.sEmpty;

    /* renamed from: M */
    private byte f45M = -1;

    /* renamed from: N */
    private String f46N = MutantMessages.sEmpty;

    /* renamed from: O */
    private byte f47O = -1;

    /* renamed from: S */
    private Map f51S = new HashMap();

    /* renamed from: T */
    private List f52T = new ArrayList();

    /* renamed from: W */
    private List f55W = new ArrayList();

    /* renamed from: Y */
    private ViewOnClickListenerC0102u f57Y = new ViewOnClickListenerC0102u();

    /* renamed from: a */
    static /* synthetic */ void m15a(FlurryAgent flurryAgent, Context context, boolean z) {
        Location location = null;
        if (z) {
            try {
                location = flurryAgent.m41d(context);
            } catch (Throwable th) {
                C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
                return;
            }
        }
        synchronized (flurryAgent) {
            flurryAgent.f50R = location;
        }
        if (f31o) {
            flurryAgent.f57Y.m137b();
        }
        flurryAgent.m40c(true);
    }

    /* renamed from: b */
    static /* synthetic */ void m32b(FlurryAgent flurryAgent, Context context) {
        boolean z = false;
        try {
            synchronized (flurryAgent) {
                long elapsedRealtime = SystemClock.elapsedRealtime() - flurryAgent.f63v;
                if (!flurryAgent.f61t && elapsedRealtime > f25i && flurryAgent.f39G.size() > 0) {
                    z = true;
                }
            }
            if (z) {
                flurryAgent.m40c(false);
            }
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
        }
    }

    public class FlurryDefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

        /* renamed from: a */
        private Thread.UncaughtExceptionHandler f68a = Thread.getDefaultUncaughtExceptionHandler();

        FlurryDefaultExceptionHandler() {
        }

        @Override // java.lang.Thread.UncaughtExceptionHandler
        public void uncaughtException(Thread thread, Throwable th) {
            try {
                FlurryAgent.f24h.m56a(th);
            } catch (Throwable th2) {
                C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th2);
            }
            if (this.f68a != null) {
                this.f68a.uncaughtException(thread, th);
            }
        }
    }

    /* renamed from: a */
    final void m56a(Throwable th) {
        th.printStackTrace();
        String str = MutantMessages.sEmpty;
        StackTraceElement[] stackTrace = th.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            StackTraceElement stackTraceElement = stackTrace[0];
            StringBuilder sb = new StringBuilder();
            sb.append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append(":").append(stackTraceElement.getLineNumber());
            if (th.getMessage() != null) {
                sb.append(" (" + th.getMessage() + ")");
            }
            str = sb.toString();
        } else if (th.getMessage() != null) {
            str = th.getMessage();
        }
        onError("uncaught", str, th.getClass().toString());
        this.f64w.clear();
        m12a((Context) null, true);
    }

    private FlurryAgent() {
        HandlerThread handlerThread = new HandlerThread("FlurryAgent");
        handlerThread.start();
        this.f58q = new Handler(handlerThread.getLooper());
    }

    public static void setCatalogIntentName(String str) {
        f17a = str;
    }

    public static void enableAppCircle() {
        f31o = true;
    }

    public static AppCircle getAppCircle() {
        return f32p;
    }

    /* renamed from: a */
    static View m6a(Context context, String str, int i) {
        if (!f31o) {
            return null;
        }
        try {
            return f24h.f57Y.m117a(context, str, i);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
            return null;
        }
    }

    /* renamed from: a */
    static void m11a(Context context, String str) {
        if (f31o) {
            f24h.f57Y.m126a(context, str);
        }
    }

    /* renamed from: a */
    static Offer m7a(String str) {
        if (f31o) {
            return f24h.f57Y.m134b(str);
        }
        return null;
    }

    /* renamed from: b */
    static List m29b(String str) {
        if (f31o) {
            return f24h.f57Y.m138c(str);
        }
        return null;
    }

    /* renamed from: a */
    static void m10a(Context context, long j) {
        if (!f31o) {
            C0078ah.m82d("FlurryAgent", "Cannot accept Offer. AppCircle is not enabled");
        }
        f24h.f57Y.m123a(context, j);
    }

    /* renamed from: a */
    static void m20a(List list) {
        if (f31o) {
            f24h.f57Y.m130a(list);
        }
    }

    /* renamed from: a */
    static void m21a(boolean z) {
        if (f31o) {
            f24h.f57Y.m132a(z);
        }
    }

    /* renamed from: a */
    static boolean m22a() {
        return f24h.f57Y.m144h();
    }

    public static void setDefaultNoAdsMessage(String str) {
        if (f31o) {
            if (str == null) {
                str = MutantMessages.sEmpty;
            }
            ViewOnClickListenerC0102u.f215b = str;
        }
    }

    /* renamed from: a */
    static void m13a(AppCircleCallback appCircleCallback) {
        f24h.f57Y.m127a(appCircleCallback);
    }

    public static void addUserCookie(String str, String str2) {
        if (f31o) {
            f24h.f57Y.m129a(str, str2);
        }
    }

    public static void clearUserCookies() {
        if (f31o) {
            f24h.f57Y.m147k();
        }
    }

    public static void setVersionName(String str) {
        synchronized (f24h) {
            f24h.f67z = str;
        }
    }

    public static int getAgentVersion() {
        return 121;
    }

    public static void setReportLocation(boolean z) {
        synchronized (f24h) {
            f24h.f33A = z;
        }
    }

    public static void setLogEnabled(boolean z) {
        synchronized (f24h) {
            if (z) {
                C0078ah.m79b();
            } else {
                C0078ah.m74a();
            }
        }
    }

    public static void setLogLevel(int i) {
        synchronized (f24h) {
            C0078ah.m75a(i);
        }
    }

    public static void setContinueSessionMillis(long j) {
        if (j < 5000) {
            C0078ah.m77b("FlurryAgent", "Invalid time set for session resumption: " + j);
            return;
        }
        synchronized (f24h) {
            f25i = j;
        }
    }

    public static void setLogEvents(boolean z) {
        synchronized (f24h) {
            f26j = z;
        }
    }

    public static void setUseHttps(boolean z) {
        f27k = z;
    }

    public static void setCaptureUncaughtExceptions(boolean z) {
        synchronized (f24h) {
            if (f24h.f61t) {
                C0078ah.m77b("FlurryAgent", "Cannot setCaptureUncaughtExceptions after onSessionStart");
            } else {
                f29m = z;
            }
        }
    }

    public static void onStartSession(Context context, String str) {
        if (context == null) {
            throw new NullPointerException("Null context");
        }
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("Api key not specified");
        }
        try {
            f24h.m30b(context, str);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
        }
    }

    public static void onEndSession(Context context) {
        if (context == null) {
            throw new NullPointerException("Null context");
        }
        try {
            f24h.m12a(context, false);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
        }
    }

    public static void logEvent(String str) {
        try {
            f24h.m19a(str, (Map) null, false);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", "Failed to log event: " + str, th);
        }
    }

    public static void logEvent(String str, Map map) {
        try {
            f24h.m19a(str, map, false);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", "Failed to log event: " + str, th);
        }
    }

    public static void logEvent(String str, boolean z) {
        try {
            f24h.m19a(str, (Map) null, z);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", "Failed to log event: " + str, th);
        }
    }

    public static void logEvent(String str, Map map, boolean z) {
        try {
            f24h.m19a(str, map, z);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", "Failed to log event: " + str, th);
        }
    }

    public static void endTimedEvent(String str) {
        try {
            f24h.m39c(str);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", "Failed to signify the end of event: " + str, th);
        }
    }

    public static void onError(String str, String str2, String str3) {
        try {
            f24h.m18a(str, str2, str3);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
        }
    }

    public static void onEvent(String str) {
        try {
            f24h.m19a(str, (Map) null, false);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
        }
    }

    public static void onEvent(String str, Map map) {
        try {
            f24h.m19a(str, map, false);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
        }
    }

    public static void onPageView() {
        try {
            f24h.m51j();
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
        }
    }

    public static void setReportUrl(String str) {
        f19c = str;
    }

    public static void setCanvasUrl(String str) {
        f20d = str;
    }

    public static void setGetAppUrl(String str) {
        f22f = str;
    }

    public static void setLocationCriteria(Criteria criteria) {
        synchronized (f24h) {
            f30n = criteria;
        }
    }

    public static void setAge(int i) {
        if (i > 0 && i < 110) {
            f24h.f48P = Long.valueOf(new Date(new Date(System.currentTimeMillis() - (i * 31449600000L)).getYear(), 1, 1).getTime());
        }
    }

    public static void setGender(byte b) {
        switch (b) {
            case DbBuilder.ID_COLUMN /* 0 */:
            case 1:
                f24h.f47O = b;
                break;
            default:
                f24h.f47O = (byte) -1;
                break;
        }
    }

    public static void setUserId(String str) {
        synchronized (f24h) {
            f24h.f46N = C0099r.m99a(str, SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE);
        }
    }

    public static boolean getForbidPlaintextFallback() {
        return false;
    }

    protected static boolean isCaptureUncaughtExceptions() {
        return f29m;
    }

    /* renamed from: b */
    static ViewOnClickListenerC0102u m27b() {
        return f24h.f57Y;
    }

    /* renamed from: b */
    private synchronized void m30b(Context context, String str) {
        if (this.f65x != null && !this.f65x.equals(str)) {
            C0078ah.m77b("FlurryAgent", "onStartSession called with different api keys: " + this.f65x + " and " + str);
        }
        if (((Context) this.f64w.put(context, context)) != null) {
            C0078ah.m82d("FlurryAgent", "onStartSession called with duplicate context, use a specific Activity or Service as context instead of using a global context");
        }
        if (!this.f61t) {
            C0078ah.m72a("FlurryAgent", "Initializing Flurry session");
            this.f65x = str;
            this.f60s = context.getFileStreamPath(".flurryagent." + Integer.toString(this.f65x.hashCode(), 16));
            this.f59r = context.getFileStreamPath(".flurryb.");
            if (f29m) {
                Thread.setDefaultUncaughtExceptionHandler(new FlurryDefaultExceptionHandler());
            }
            Context applicationContext = context.getApplicationContext();
            if (this.f67z == null) {
                this.f67z = m36c(applicationContext);
            }
            String packageName = applicationContext.getPackageName();
            if (this.f66y != null && !this.f66y.equals(packageName)) {
                C0078ah.m77b("FlurryAgent", "onStartSession called from different application packages: " + this.f66y + " and " + packageName);
            }
            this.f66y = packageName;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (elapsedRealtime - this.f63v > f25i) {
                C0078ah.m72a("FlurryAgent", "New session");
                this.f40H = System.currentTimeMillis();
                this.f41I = elapsedRealtime;
                this.f42J = -1L;
                this.f46N = MutantMessages.sEmpty;
                this.f49Q = 0;
                this.f50R = null;
                this.f44L = TimeZone.getDefault().getID();
                this.f43K = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
                this.f51S = new HashMap();
                this.f52T = new ArrayList();
                this.f53U = true;
                this.f55W = new ArrayList();
                this.f54V = 0;
                this.f56X = 0;
                if (f31o) {
                    if (!this.f57Y.m133a()) {
                        C0078ah.m72a("FlurryAgent", "Initializing AppCircle");
                        C0070a c0070a = new C0070a();
                        c0070a.f82a = this.f65x;
                        c0070a.f83b = this.f38F;
                        c0070a.f84c = f20d != null ? f20d : f21e;
                        c0070a.f85d = m35c();
                        c0070a.f86e = this.f58q;
                        this.f57Y.m124a(context, c0070a);
                        C0078ah.m72a("FlurryAgent", "AppCircle initialized");
                    }
                    this.f57Y.m122a(this.f40H, this.f41I);
                }
                m17a(new RunnableC0085d(this, applicationContext, this.f33A));
            } else {
                C0078ah.m72a("FlurryAgent", "Continuing previous session");
                if (!this.f39G.isEmpty()) {
                    this.f39G.remove(this.f39G.size() - 1);
                }
            }
            this.f61t = true;
        }
    }

    /* renamed from: a */
    private synchronized void m12a(Context context, boolean z) {
        if (context != null) {
            if (((Context) this.f64w.remove(context)) == null) {
                C0078ah.m82d("FlurryAgent", "onEndSession called without context from corresponding onStartSession");
            }
        }
        if (this.f61t && this.f64w.isEmpty()) {
            C0078ah.m72a("FlurryAgent", "Ending session");
            m54m();
            Context applicationContext = context == null ? null : context.getApplicationContext();
            if (context != null) {
                String packageName = applicationContext.getPackageName();
                if (!this.f66y.equals(packageName)) {
                    C0078ah.m77b("FlurryAgent", "onEndSession called from different application package, expected: " + this.f66y + " actual: " + packageName);
                }
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.f63v = elapsedRealtime;
            this.f42J = elapsedRealtime - this.f41I;
            if (this.f36D == null) {
                C0078ah.m77b("FlurryAgent", "Not creating report because of bad Android ID or generated ID is null");
            }
            m17a(new RunnableC0083b(this, z, applicationContext));
            this.f61t = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: i */
    public synchronized void m50i() {
        DataOutputStream dataOutputStream;
        DataOutputStream dataOutputStream2 = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeShort(1);
                dataOutputStream.writeUTF(this.f67z);
                dataOutputStream.writeLong(this.f40H);
                dataOutputStream.writeLong(this.f42J);
                dataOutputStream.writeLong(0L);
                dataOutputStream.writeUTF(this.f43K);
                dataOutputStream.writeUTF(this.f44L);
                dataOutputStream.writeByte(this.f45M);
                dataOutputStream.writeUTF(this.f46N == null ? MutantMessages.sEmpty : this.f46N);
                if (this.f50R == null) {
                    dataOutputStream.writeBoolean(false);
                } else {
                    dataOutputStream.writeBoolean(true);
                    dataOutputStream.writeDouble(m5a(this.f50R.getLatitude()));
                    dataOutputStream.writeDouble(m5a(this.f50R.getLongitude()));
                    dataOutputStream.writeFloat(this.f50R.getAccuracy());
                }
                dataOutputStream.writeInt(this.f56X);
                dataOutputStream.writeByte(-1);
                dataOutputStream.writeByte(-1);
                dataOutputStream.writeByte(this.f47O);
                if (this.f48P == null) {
                    dataOutputStream.writeBoolean(false);
                } else {
                    dataOutputStream.writeBoolean(true);
                    dataOutputStream.writeLong(this.f48P.longValue());
                }
                dataOutputStream.writeShort(this.f51S.size());
                for (Map.Entry entry : this.f51S.entrySet()) {
                    dataOutputStream.writeUTF((String) entry.getKey());
                    dataOutputStream.writeInt(((C0088g) entry.getValue()).f187a);
                }
                dataOutputStream.writeShort(this.f52T.size());
                Iterator it = this.f52T.iterator();
                while (it.hasNext()) {
                    dataOutputStream.write(((C0090i) it.next()).m90a());
                }
                dataOutputStream.writeBoolean(this.f53U);
                dataOutputStream.writeInt(this.f49Q);
                dataOutputStream.writeShort(this.f55W.size());
                for (C0071aa c0071aa : this.f55W) {
                    dataOutputStream.writeLong(c0071aa.f87a);
                    dataOutputStream.writeUTF(c0071aa.f88b);
                    dataOutputStream.writeUTF(c0071aa.f89c);
                    dataOutputStream.writeUTF(c0071aa.f90d);
                }
                if (f31o) {
                    List m142f = this.f57Y.m142f();
                    dataOutputStream.writeShort(m142f.size());
                    Iterator it2 = m142f.iterator();
                    while (it2.hasNext()) {
                        ((C0097p) it2.next()).m96a(dataOutputStream);
                    }
                } else {
                    dataOutputStream.writeShort(0);
                }
                this.f39G.add(byteArrayOutputStream.toByteArray());
                C0099r.m101a(dataOutputStream);
            } catch (IOException e) {
                e = e;
                dataOutputStream2 = dataOutputStream;
                try {
                    C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, e);
                    C0099r.m101a(dataOutputStream2);
                } catch (Throwable th) {
                    th = th;
                    dataOutputStream = dataOutputStream2;
                    C0099r.m101a(dataOutputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                C0099r.m101a(dataOutputStream);
                throw th;
            }
        } catch (IOException e2) {
            e = e2;
        } catch (Throwable th3) {
            th = th3;
            dataOutputStream = null;
        }
    }

    /* renamed from: a */
    private static double m5a(double d) {
        return Math.round(d * 1000.0d) / 1000.0d;
    }

    /* renamed from: a */
    private void m17a(Runnable runnable) {
        this.f58q.post(runnable);
    }

    /* renamed from: j */
    private synchronized void m51j() {
        this.f56X++;
    }

    /* renamed from: a */
    private synchronized void m19a(String str, Map map, boolean z) {
        if (this.f52T == null) {
            C0078ah.m77b("FlurryAgent", "onEvent called before onStartSession.  Event: " + str);
        } else {
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.f41I;
            String m99a = C0099r.m99a(str, SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE);
            if (m99a.length() != 0) {
                C0088g c0088g = (C0088g) this.f51S.get(m99a);
                if (c0088g == null) {
                    if (this.f51S.size() < 100) {
                        C0088g c0088g2 = new C0088g();
                        c0088g2.f187a = 1;
                        this.f51S.put(m99a, c0088g2);
                        C0078ah.m72a("FlurryAgent", "Event count incremented: " + m99a);
                    } else if (C0078ah.m76a("FlurryAgent")) {
                        C0078ah.m72a("FlurryAgent", "Too many different events. Event not counted: " + m99a);
                    }
                } else {
                    c0088g.f187a++;
                    C0078ah.m72a("FlurryAgent", "Event count incremented: " + m99a);
                }
                if (f26j && this.f52T.size() < 200 && this.f54V < 16000) {
                    Map emptyMap = map == null ? Collections.emptyMap() : map;
                    if (emptyMap.size() > 10) {
                        if (C0078ah.m76a("FlurryAgent")) {
                            C0078ah.m72a("FlurryAgent", "MaxEventParams exceeded: " + emptyMap.size());
                        }
                    } else {
                        C0090i c0090i = new C0090i(m99a, emptyMap, elapsedRealtime, z);
                        if (c0090i.m90a().length + this.f54V <= 16000) {
                            this.f52T.add(c0090i);
                            this.f54V = c0090i.m90a().length + this.f54V;
                            C0078ah.m72a("FlurryAgent", "Logged event: " + m99a);
                        } else {
                            this.f54V = 16000;
                            this.f53U = false;
                            C0078ah.m72a("FlurryAgent", "Event Log size exceeded. No more event details logged.");
                        }
                    }
                } else {
                    this.f53U = false;
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0019, code lost:
    
        r0.m88a(android.os.SystemClock.elapsedRealtime() - r5.f41I);
     */
    /* renamed from: c */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private synchronized void m39c(java.lang.String r6) {
        /*
            r5 = this;
            monitor-enter(r5)
            java.util.List r0 = r5.f52T     // Catch: java.lang.Throwable -> L25
            java.util.Iterator r1 = r0.iterator()     // Catch: java.lang.Throwable -> L25
        L7:
            boolean r0 = r1.hasNext()     // Catch: java.lang.Throwable -> L25
            if (r0 == 0) goto L23
            java.lang.Object r0 = r1.next()     // Catch: java.lang.Throwable -> L25
            com.flurry.android.i r0 = (com.flurry.android.C0090i) r0     // Catch: java.lang.Throwable -> L25
            boolean r2 = r0.m89a(r6)     // Catch: java.lang.Throwable -> L25
            if (r2 == 0) goto L7
            long r1 = android.os.SystemClock.elapsedRealtime()     // Catch: java.lang.Throwable -> L25
            long r3 = r5.f41I     // Catch: java.lang.Throwable -> L25
            long r1 = r1 - r3
            r0.m88a(r1)     // Catch: java.lang.Throwable -> L25
        L23:
            monitor-exit(r5)
            return
        L25:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.flurry.android.FlurryAgent.m39c(java.lang.String):void");
    }

    /* renamed from: a */
    private synchronized void m18a(String str, String str2, String str3) {
        if (this.f55W == null) {
            C0078ah.m77b("FlurryAgent", "onError called before onStartSession.  Error: " + str);
        } else {
            this.f49Q++;
            if (this.f55W.size() < 10) {
                C0071aa c0071aa = new C0071aa();
                c0071aa.f87a = System.currentTimeMillis();
                c0071aa.f88b = C0099r.m99a(str, SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE);
                c0071aa.f89c = C0099r.m99a(str2, 512);
                c0071aa.f90d = C0099r.m99a(str3, SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE);
                this.f55W.add(c0071aa);
                C0078ah.m72a("FlurryAgent", "Error logged: " + c0071aa.f88b);
            } else {
                C0078ah.m72a("FlurryAgent", "Max errors logged. No more errors logged.");
            }
        }
    }

    /* renamed from: b */
    private synchronized byte[] m34b(boolean z) {
        DataOutputStream dataOutputStream;
        byte[] bArr;
        ByteArrayOutputStream byteArrayOutputStream;
        synchronized (this) {
            try {
                try {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                } catch (Throwable th) {
                    th = th;
                    C0099r.m101a((Closeable) null);
                    throw th;
                }
                try {
                    dataOutputStream.writeShort(15);
                    if (f31o && z) {
                        dataOutputStream.writeShort(1);
                    } else {
                        dataOutputStream.writeShort(0);
                    }
                    if (f31o) {
                        dataOutputStream.writeLong(this.f57Y.m140d());
                        Set m141e = this.f57Y.m141e();
                        dataOutputStream.writeShort(m141e.size());
                        Iterator it = m141e.iterator();
                        while (it.hasNext()) {
                            long longValue = ((Long) it.next()).longValue();
                            dataOutputStream.writeByte(1);
                            dataOutputStream.writeLong(longValue);
                        }
                    } else {
                        dataOutputStream.writeLong(0L);
                        dataOutputStream.writeShort(0);
                    }
                    dataOutputStream.writeShort(3);
                    dataOutputStream.writeShort(121);
                    dataOutputStream.writeLong(System.currentTimeMillis());
                    dataOutputStream.writeUTF(this.f65x);
                    dataOutputStream.writeUTF(this.f67z);
                    dataOutputStream.writeShort(0);
                    dataOutputStream.writeUTF(this.f36D);
                    dataOutputStream.writeLong(this.f38F);
                    dataOutputStream.writeLong(this.f40H);
                    dataOutputStream.writeShort(6);
                    dataOutputStream.writeUTF("device.model");
                    dataOutputStream.writeUTF(Build.MODEL);
                    dataOutputStream.writeUTF("build.brand");
                    dataOutputStream.writeUTF(Build.BRAND);
                    dataOutputStream.writeUTF("build.id");
                    dataOutputStream.writeUTF(Build.ID);
                    dataOutputStream.writeUTF("version.release");
                    dataOutputStream.writeUTF(Build.VERSION.RELEASE);
                    dataOutputStream.writeUTF("build.device");
                    dataOutputStream.writeUTF(Build.DEVICE);
                    dataOutputStream.writeUTF("build.product");
                    dataOutputStream.writeUTF(Build.PRODUCT);
                    int size = this.f39G.size();
                    dataOutputStream.writeShort(size);
                    for (int i = 0; i < size; i++) {
                        dataOutputStream.write((byte[]) this.f39G.get(i));
                    }
                    this.f34B = new ArrayList(this.f39G);
                    dataOutputStream.close();
                    bArr = byteArrayOutputStream.toByteArray();
                    C0099r.m101a(dataOutputStream);
                } catch (Throwable th2) {
                    th = th2;
                    C0078ah.m78b("FlurryAgent", "Error when generating report", th);
                    C0099r.m101a(dataOutputStream);
                    bArr = null;
                    return bArr;
                }
            } catch (Throwable th3) {
                th = th3;
                C0099r.m101a((Closeable) null);
                throw th;
            }
        }
        return bArr;
    }

    /* renamed from: k */
    private static String m52k() {
        if (f19c != null) {
            return f19c;
        }
        if (f28l) {
            return kInsecureReportUrl;
        }
        if (f27k) {
            return kSecureReportUrl;
        }
        return kInsecureReportUrl;
    }

    /* renamed from: c */
    static String m35c() {
        return f22f != null ? f22f : f23g;
    }

    /* renamed from: d */
    static boolean m44d() {
        if (f31o) {
            return f24h.f57Y.m149m();
        }
        return false;
    }

    /* renamed from: a */
    private boolean m25a(byte[] bArr) {
        boolean z;
        String m52k = m52k();
        if (m52k == null) {
            return false;
        }
        try {
            z = m26a(bArr, m52k);
        } catch (Exception e) {
            C0078ah.m72a("FlurryAgent", "Sending report exception: " + e.getMessage());
            z = false;
        }
        if (!z && f19c == null && f27k && !f28l) {
            synchronized (f24h) {
                f28l = true;
                String m52k2 = m52k();
                if (m52k2 == null) {
                    z = false;
                } else {
                    try {
                        z = m26a(bArr, m52k2);
                    } catch (Exception e2) {
                    }
                }
            }
            return z;
        }
        return z;
    }

    /* renamed from: a */
    private boolean m26a(byte[] bArr, String str) {
        boolean z = true;
        if (!"local".equals(str)) {
            C0078ah.m72a("FlurryAgent", "Sending report to: " + str);
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bArr);
            byteArrayEntity.setContentType(FilePart.DEFAULT_CONTENT_TYPE);
            HttpPost httpPost = new HttpPost(str);
            httpPost.setEntity(byteArrayEntity);
            BasicHttpParams basicHttpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(basicHttpParams, 10000);
            HttpConnectionParams.setSoTimeout(basicHttpParams, 15000);
            httpPost.getParams().setBooleanParameter("http.protocol.expect-continue", false);
            HttpResponse execute = m8a(basicHttpParams).execute(httpPost);
            int statusCode = execute.getStatusLine().getStatusCode();
            synchronized (this) {
                if (statusCode == 200) {
                    C0078ah.m72a("FlurryAgent", "Report successful");
                    this.f37E = true;
                    this.f39G.removeAll(this.f34B);
                    HttpEntity entity = execute.getEntity();
                    C0078ah.m72a("FlurryAgent", "Processing report response");
                    if (entity != null && entity.getContentLength() != 0) {
                        try {
                            m16a(new DataInputStream(entity.getContent()));
                        } finally {
                            entity.consumeContent();
                        }
                    }
                } else {
                    C0078ah.m72a("FlurryAgent", "Report failed. HTTP response: " + statusCode);
                    z = false;
                }
                this.f34B = null;
            }
        }
        return z;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v13, types: [int] */
    /* renamed from: a */
    private void m16a(DataInputStream dataInputStream) {
        int readUnsignedShort;
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        HashMap hashMap3 = new HashMap();
        HashMap hashMap4 = new HashMap();
        HashMap hashMap5 = new HashMap();
        HashMap hashMap6 = new HashMap();
        do {
            readUnsignedShort = dataInputStream.readUnsignedShort();
            int readInt = dataInputStream.readInt();
            switch (readUnsignedShort) {
                case 258:
                    dataInputStream.readInt();
                    break;
                case 259:
                    byte readByte = dataInputStream.readByte();
                    int readUnsignedShort2 = dataInputStream.readUnsignedShort();
                    C0103v[] c0103vArr = new C0103v[readUnsignedShort2];
                    for (int i = 0; i < readUnsignedShort2; i++) {
                        c0103vArr[i] = new C0103v(dataInputStream);
                    }
                    hashMap.put(Byte.valueOf(readByte), c0103vArr);
                    break;
                case 260:
                case 261:
                case 265:
                case 267:
                default:
                    C0078ah.m72a("FlurryAgent", "Unknown chunkType: " + readUnsignedShort);
                    dataInputStream.skipBytes(readInt);
                    break;
                case 262:
                    int readUnsignedShort3 = dataInputStream.readUnsignedShort();
                    for (int i2 = 0; i2 < readUnsignedShort3; i2++) {
                        AdImage adImage = new AdImage(dataInputStream);
                        hashMap2.put(Long.valueOf(adImage.f3a), adImage);
                    }
                    break;
                case 263:
                    int readInt2 = dataInputStream.readInt();
                    for (int i3 = 0; i3 < readInt2; i3++) {
                        C0086e c0086e = new C0086e(dataInputStream);
                        hashMap4.put(c0086e.f181a, c0086e);
                        C0078ah.m72a("FlurryAgent", "Parsed hook: " + c0086e);
                    }
                    break;
                case 264:
                    break;
                case 266:
                    byte readByte2 = dataInputStream.readByte();
                    for (int i4 = 0; i4 < readByte2; i4++) {
                        C0084c c0084c = new C0084c(dataInputStream);
                        hashMap5.put(Byte.valueOf(c0084c.f145a), c0084c);
                    }
                    break;
                case 268:
                    int readInt3 = dataInputStream.readInt();
                    for (int i5 = 0; i5 < readInt3; i5++) {
                        hashMap6.put(Short.valueOf(dataInputStream.readShort()), Long.valueOf(dataInputStream.readLong()));
                    }
                    break;
                case 269:
                    dataInputStream.skipBytes(readInt);
                    break;
                case 270:
                    dataInputStream.skipBytes(readInt);
                    break;
                case 271:
                    byte readByte3 = dataInputStream.readByte();
                    for (byte b = 0; b < readByte3; b++) {
                        C0084c c0084c2 = (C0084c) hashMap5.get(Byte.valueOf(dataInputStream.readByte()));
                        if (c0084c2 != null) {
                            c0084c2.m85a(dataInputStream);
                        }
                    }
                    break;
                case 272:
                    long readLong = dataInputStream.readLong();
                    C0082al c0082al = (C0082al) hashMap3.get(Long.valueOf(readLong));
                    if (c0082al == null) {
                        c0082al = new C0082al();
                    }
                    c0082al.f113a = dataInputStream.readUTF();
                    c0082al.f115c = dataInputStream.readInt();
                    hashMap3.put(Long.valueOf(readLong), c0082al);
                    break;
                case 273:
                    dataInputStream.skipBytes(readInt);
                    break;
            }
        } while (readUnsignedShort != 264);
        if (f31o) {
            if (hashMap.isEmpty()) {
                C0078ah.m72a("FlurryAgent", "No ads from server");
            }
            this.f57Y.m131a(hashMap, hashMap4, hashMap5, hashMap2, hashMap3, hashMap6);
        }
    }

    /* renamed from: c */
    private void m40c(boolean z) {
        try {
            C0078ah.m72a("FlurryAgent", "generating report");
            byte[] m34b = m34b(z);
            if (m34b != null) {
                if (m25a(m34b)) {
                    C0078ah.m72a("FlurryAgent", "Done sending " + (this.f61t ? "initial " : MutantMessages.sEmpty) + "agent report");
                    m53l();
                }
            } else {
                C0078ah.m72a("FlurryAgent", "Error generating report");
            }
        } catch (IOException e) {
            C0078ah.m73a("FlurryAgent", MutantMessages.sEmpty, e);
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't wrap try/catch for region: R(7:7|8|(4:9|10|(1:12)(1:39)|13)|14|15|(2:17|(1:19)(1:20))|21) */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00f2, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00f3, code lost:
    
        com.flurry.android.C0078ah.m78b("FlurryAgent", com.alawar.mutant.jni.MutantMessages.sEmpty, r0);
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:17:0x004d A[Catch: all -> 0x00e0, Throwable -> 0x00f2, TryCatch #4 {Throwable -> 0x00f2, blocks: (B:15:0x0049, B:17:0x004d, B:19:0x0055, B:20:0x00e9), top: B:14:0x0049, outer: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0060 A[Catch: all -> 0x00e0, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x000f, B:13:0x0046, B:15:0x0049, B:17:0x004d, B:19:0x0055, B:20:0x00e9, B:22:0x005c, B:24:0x0060, B:25:0x006a, B:27:0x006e, B:28:0x00aa, B:30:0x00bb, B:32:0x00c3, B:38:0x00f3, B:42:0x00db, B:45:0x00e5, B:46:0x00e8, B:51:0x00fc), top: B:2:0x0001, inners: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x006e A[Catch: all -> 0x00e0, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x000f, B:13:0x0046, B:15:0x0049, B:17:0x004d, B:19:0x0055, B:20:0x00e9, B:22:0x005c, B:24:0x0060, B:25:0x006a, B:27:0x006e, B:28:0x00aa, B:30:0x00bb, B:32:0x00c3, B:38:0x00f3, B:42:0x00db, B:45:0x00e5, B:46:0x00e8, B:51:0x00fc), top: B:2:0x0001, inners: #4 }] */
    /* JADX WARN: Type inference failed for: r1v10, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r1v7, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r1v8 */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:38:0x00f3 -> B:21:0x005c). Please report as a decompilation issue!!! */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized void m9a(android.content.Context r9) {
        /*
            Method dump skipped, instructions count: 266
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.flurry.android.FlurryAgent.m9a(android.content.Context):void");
    }

    /* renamed from: b */
    private synchronized void m33b(DataInputStream dataInputStream) {
        int i = 0;
        synchronized (this) {
            int readUnsignedShort = dataInputStream.readUnsignedShort();
            if (readUnsignedShort > 2) {
                C0078ah.m77b("FlurryAgent", "Unknown agent file version: " + readUnsignedShort);
                throw new IOException("Unknown agent file version: " + readUnsignedShort);
            }
            if (readUnsignedShort >= 2) {
                String readUTF = dataInputStream.readUTF();
                C0078ah.m72a("FlurryAgent", "Loading API key: " + m43d(this.f65x));
                if (readUTF.equals(this.f65x)) {
                    String readUTF2 = dataInputStream.readUTF();
                    if (this.f36D == null) {
                        C0078ah.m72a("FlurryAgent", "Loading phoneId: " + readUTF2);
                    }
                    this.f36D = readUTF2;
                    this.f37E = dataInputStream.readBoolean();
                    this.f38F = dataInputStream.readLong();
                    C0078ah.m72a("FlurryAgent", "Loading session reports");
                    while (true) {
                        int readUnsignedShort2 = dataInputStream.readUnsignedShort();
                        if (readUnsignedShort2 == 0) {
                            break;
                        }
                        byte[] bArr = new byte[readUnsignedShort2];
                        dataInputStream.readFully(bArr);
                        this.f39G.add(0, bArr);
                        i++;
                        C0078ah.m72a("FlurryAgent", "Session report added: " + i);
                    }
                    C0078ah.m72a("FlurryAgent", "Persistent file loaded");
                    this.f62u = true;
                } else {
                    C0078ah.m72a("FlurryAgent", "Api keys do not match, old: " + m43d(readUTF) + ", new: " + m43d(this.f65x));
                }
            } else {
                C0078ah.m82d("FlurryAgent", "Deleting old file version: " + readUnsignedShort);
            }
        }
    }

    /* renamed from: d */
    private static String m43d(String str) {
        if (str != null && str.length() > 4) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length() - 4; i++) {
                sb.append('*');
            }
            sb.append(str.substring(str.length() - 4));
            return sb.toString();
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: l */
    public synchronized void m53l() {
        DataOutputStream dataOutputStream;
        try {
            if (m24a(this.f60s)) {
                dataOutputStream = new DataOutputStream(new FileOutputStream(this.f60s));
                try {
                    try {
                        dataOutputStream.writeShort(46586);
                        dataOutputStream.writeShort(2);
                        dataOutputStream.writeUTF(this.f65x);
                        dataOutputStream.writeUTF(this.f36D);
                        dataOutputStream.writeBoolean(this.f37E);
                        dataOutputStream.writeLong(this.f38F);
                        int size = this.f39G.size() - 1;
                        while (true) {
                            if (size < 0) {
                                break;
                            }
                            byte[] bArr = (byte[]) this.f39G.get(size);
                            int length = bArr.length;
                            if (length + 2 + dataOutputStream.size() > 50000) {
                                C0078ah.m72a("FlurryAgent", "discarded sessions: " + size);
                                break;
                            } else {
                                dataOutputStream.writeShort(length);
                                dataOutputStream.write(bArr);
                                size--;
                            }
                        }
                        dataOutputStream.writeShort(0);
                        C0099r.m101a(dataOutputStream);
                    } catch (Throwable th) {
                        th = th;
                        C0099r.m101a(dataOutputStream);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
                    C0099r.m101a(dataOutputStream);
                }
            } else {
                C0099r.m101a((Closeable) null);
            }
        } catch (Throwable th3) {
            th = th3;
            dataOutputStream = null;
            C0099r.m101a(dataOutputStream);
            throw th;
        }
    }

    /* renamed from: a */
    private static boolean m24a(File file) {
        File parentFile = file.getParentFile();
        if (parentFile.mkdirs() || parentFile.exists()) {
            return true;
        }
        C0078ah.m77b("FlurryAgent", "Unable to create persistent dir: " + parentFile);
        return false;
    }

    /* renamed from: c */
    private synchronized void m37c(Context context, String str) {
        DataOutputStream dataOutputStream;
        this.f59r = context.getFileStreamPath(".flurryb.");
        if (m24a(this.f59r)) {
            try {
                try {
                    dataOutputStream = new DataOutputStream(new FileOutputStream(this.f59r));
                } catch (Throwable th) {
                    th = th;
                    dataOutputStream = null;
                }
                try {
                    dataOutputStream.writeInt(1);
                    dataOutputStream.writeUTF(str);
                    C0099r.m101a(dataOutputStream);
                } catch (Throwable th2) {
                    th = th2;
                    C0078ah.m78b("FlurryAgent", "Error when saving b file", th);
                    C0099r.m101a(dataOutputStream);
                }
            } catch (Throwable th3) {
                th = th3;
                C0099r.m101a(dataOutputStream);
                throw th;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v16 */
    /* JADX WARN: Type inference failed for: r2v17 */
    /* JADX WARN: Type inference failed for: r2v2, types: [boolean] */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v8 */
    /* renamed from: b */
    private String m28b(Context context) {
        DataInputStream dataInputStream;
        String str = null;
        boolean z = false;
        if (this.f36D != null) {
            return this.f36D;
        }
        String string = Settings.System.getString(context.getContentResolver(), TapjoyConstants.TJC_ANDROID_ID);
        if (string != null && string.length() > 0 && !string.equals("null")) {
            String[] strArr = f18b;
            int length = strArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = true;
                    break;
                }
                if (string.equals(strArr[i])) {
                    break;
                }
                i++;
            }
        }
        if (z) {
            return "AND" + string;
        }
        File fileStreamPath = context.getFileStreamPath(".flurryb.");
        ?? exists = fileStreamPath.exists();
        try {
            if (exists == 0) {
                return null;
            }
            try {
                dataInputStream = new DataInputStream(new FileInputStream(fileStreamPath));
            } catch (Throwable th) {
                th = th;
                dataInputStream = null;
            }
            try {
                dataInputStream.readInt();
                str = dataInputStream.readUTF();
                C0099r.m101a(dataInputStream);
                exists = dataInputStream;
            } catch (Throwable th2) {
                th = th2;
                C0078ah.m78b("FlurryAgent", "Error when loading b file", th);
                C0099r.m101a(dataInputStream);
                exists = dataInputStream;
                return str;
            }
            return str;
        } catch (Throwable th3) {
            th = th3;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0013, code lost:
    
        r0 = "Unknown";
     */
    /* renamed from: c */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.lang.String m36c(android.content.Context r3) {
        /*
            android.content.pm.PackageManager r0 = r3.getPackageManager()     // Catch: java.lang.Throwable -> L1f
            java.lang.String r1 = r3.getPackageName()     // Catch: java.lang.Throwable -> L1f
            r2 = 0
            android.content.pm.PackageInfo r0 = r0.getPackageInfo(r1, r2)     // Catch: java.lang.Throwable -> L1f
            java.lang.String r1 = r0.versionName     // Catch: java.lang.Throwable -> L1f
            if (r1 == 0) goto L14
            java.lang.String r0 = r0.versionName     // Catch: java.lang.Throwable -> L1f
        L13:
            return r0
        L14:
            int r1 = r0.versionCode     // Catch: java.lang.Throwable -> L1f
            if (r1 == 0) goto L27
            int r0 = r0.versionCode     // Catch: java.lang.Throwable -> L1f
            java.lang.String r0 = java.lang.Integer.toString(r0)     // Catch: java.lang.Throwable -> L1f
            goto L13
        L1f:
            r0 = move-exception
            java.lang.String r1 = "FlurryAgent"
            java.lang.String r2 = ""
            com.flurry.android.C0078ah.m78b(r1, r2, r0)
        L27:
            java.lang.String r0 = "Unknown"
            goto L13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.flurry.android.FlurryAgent.m36c(android.content.Context):java.lang.String");
    }

    /* renamed from: d */
    private Location m41d(Context context) {
        if (context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == 0 || context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            LocationManager locationManager = (LocationManager) context.getSystemService("location");
            synchronized (this) {
                if (this.f35C == null) {
                    this.f35C = locationManager;
                } else {
                    locationManager = this.f35C;
                }
            }
            Criteria criteria = f30n;
            if (criteria == null) {
                criteria = new Criteria();
            }
            String bestProvider = locationManager.getBestProvider(criteria, true);
            if (bestProvider != null) {
                locationManager.requestLocationUpdates(bestProvider, 0L, 0.0f, this, Looper.getMainLooper());
                return locationManager.getLastKnownLocation(bestProvider);
            }
        }
        return null;
    }

    /* renamed from: m */
    private synchronized void m54m() {
        if (this.f35C != null) {
            this.f35C.removeUpdates(this);
        }
    }

    /* renamed from: e */
    static String m46e() {
        return f24h.f65x;
    }

    /* renamed from: n */
    private synchronized String m55n() {
        return this.f36D;
    }

    public static String getPhoneId() {
        return f24h.m55n();
    }

    @Override // android.location.LocationListener
    public final synchronized void onLocationChanged(Location location) {
        try {
            this.f50R = location;
            m54m();
        } catch (Throwable th) {
            C0078ah.m78b("FlurryAgent", MutantMessages.sEmpty, th);
        }
    }

    @Override // android.location.LocationListener
    public final void onProviderDisabled(String str) {
    }

    @Override // android.location.LocationListener
    public final void onProviderEnabled(String str) {
    }

    @Override // android.location.LocationListener
    public final void onStatusChanged(String str, int i, Bundle bundle) {
    }

    /* renamed from: a */
    private HttpClient m8a(HttpParams httpParams) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            C0079ai c0079ai = new C0079ai(this, keyStore);
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", c0079ai, 443));
            return new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);
        } catch (Exception e) {
            return new DefaultHttpClient(httpParams);
        }
    }
}
