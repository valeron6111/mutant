package com.flurry.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import com.alawar.mutant.jni.MutantMessages;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/* renamed from: com.flurry.android.u */
/* loaded from: classes.dex */
final class ViewOnClickListenerC0102u implements View.OnClickListener {

    /* renamed from: A */
    private static volatile long f213A;

    /* renamed from: b */
    static String f215b;

    /* renamed from: g */
    private static int f220g;

    /* renamed from: h */
    private String f221h;

    /* renamed from: i */
    private String f222i;

    /* renamed from: j */
    private String f223j;

    /* renamed from: k */
    private long f224k;

    /* renamed from: l */
    private long f225l;

    /* renamed from: m */
    private long f226m;

    /* renamed from: n */
    private long f227n;

    /* renamed from: q */
    private volatile boolean f230q;

    /* renamed from: r */
    private String f231r;

    /* renamed from: t */
    private Handler f233t;

    /* renamed from: u */
    private boolean f234u;

    /* renamed from: w */
    private C0077ag f236w;

    /* renamed from: z */
    private AppCircleCallback f239z;

    /* renamed from: c */
    private static volatile String f216c = "market://";

    /* renamed from: d */
    private static volatile String f217d = "market://details?id=";

    /* renamed from: e */
    private static volatile String f218e = "https://market.android.com/details?id=";

    /* renamed from: f */
    private static String f219f = "com.flurry.android.ACTION_CATALOG";

    /* renamed from: a */
    static String f214a = "FlurryAgent";

    /* renamed from: p */
    private boolean f229p = true;

    /* renamed from: s */
    private Map f232s = new HashMap();

    /* renamed from: v */
    private transient Map f235v = new HashMap();

    /* renamed from: x */
    private List f237x = new ArrayList();

    /* renamed from: y */
    private Map f238y = new HashMap();

    /* renamed from: o */
    private C0107z f228o = new C0107z();

    /* renamed from: a */
    static /* synthetic */ void m107a(ViewOnClickListenerC0102u viewOnClickListenerC0102u, Context context, String str) {
        if (!str.startsWith(f217d)) {
            C0078ah.m82d(f214a, "Unexpected android market url scheme: " + str);
            return;
        }
        String substring = str.substring(f217d.length());
        if (!viewOnClickListenerC0102u.f229p) {
            C0078ah.m72a(f214a, "Launching Android Market website for app " + substring);
            context.startActivity(new Intent("android.intent.action.VIEW").setData(Uri.parse(f218e + substring)));
        } else {
            try {
                C0078ah.m72a(f214a, "Launching Android Market for app " + substring);
                context.startActivity(new Intent("android.intent.action.VIEW").setData(Uri.parse(str)));
            } catch (Exception e) {
                C0078ah.m81c(f214a, "Cannot launch Marketplace url " + str, e);
            }
        }
    }

    static {
        new Random(System.currentTimeMillis());
        f220g = 5000;
        f215b = MutantMessages.sEmpty;
        f213A = 0L;
    }

    /* renamed from: a */
    final synchronized void m124a(Context context, C0070a c0070a) {
        synchronized (this) {
            if (!this.f230q) {
                this.f221h = c0070a.f84c;
                this.f222i = c0070a.f85d;
                this.f223j = c0070a.f82a;
                this.f224k = c0070a.f83b;
                this.f233t = c0070a.f86e;
                this.f236w = new C0077ag(this.f233t, f220g);
                context.getResources().getDisplayMetrics();
                this.f238y.clear();
                this.f235v.clear();
                this.f228o.m171a(context, this, c0070a);
                this.f232s.clear();
                PackageManager packageManager = context.getPackageManager();
                String str = f217d + context.getPackageName();
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(str));
                this.f229p = packageManager.queryIntentActivities(intent, 65536).size() > 0;
                this.f230q = true;
            }
        }
    }

    /* renamed from: a */
    final synchronized void m122a(long j, long j2) {
        this.f225l = j;
        this.f226m = j2;
        this.f227n = 0L;
        this.f237x.clear();
    }

    /* renamed from: a */
    final boolean m133a() {
        return this.f230q;
    }

    /* renamed from: a */
    final void m128a(String str) {
        this.f231r = str;
    }

    /* renamed from: b */
    final synchronized void m137b() {
        if (m116p()) {
            this.f228o.m178d();
        }
    }

    /* renamed from: c */
    final synchronized void m139c() {
        if (m116p()) {
            this.f228o.m179e();
        }
    }

    /* renamed from: a */
    final synchronized void m131a(Map map, Map map2, Map map3, Map map4, Map map5, Map map6) {
        if (m116p()) {
            this.f228o.m172a(map, map2, map3, map4, map5, map6);
            Log.i("FlurryAgent", this.f228o.toString());
        }
    }

    /* renamed from: d */
    final synchronized long m140d() {
        return !m116p() ? 0L : this.f228o.m177c();
    }

    /* renamed from: e */
    final synchronized Set m141e() {
        return !m116p() ? Collections.emptySet() : this.f228o.m170a();
    }

    /* renamed from: a */
    final synchronized AdImage m118a(long j) {
        return !m116p() ? null : this.f228o.m174b(j);
    }

    /* renamed from: n */
    private synchronized AdImage m114n() {
        return !m116p() ? null : this.f228o.m168a((short) 1);
    }

    /* renamed from: f */
    final synchronized List m142f() {
        return this.f237x;
    }

    /* renamed from: b */
    final synchronized C0097p m135b(long j) {
        return (C0097p) this.f235v.get(Long.valueOf(j));
    }

    /* renamed from: g */
    final synchronized void m143g() {
        this.f235v.clear();
    }

    /* renamed from: a */
    final synchronized void m126a(Context context, String str) {
        if (m116p()) {
            try {
                List m106a = m106a(Arrays.asList(str), (Long) null);
                if (m106a != null && !m106a.isEmpty()) {
                    C0097p c0097p = new C0097p(str, (byte) 2, m146j());
                    c0097p.f206b = (C0103v) m106a.get(0);
                    m111c(c0097p);
                    m109b(context, c0097p, this.f221h + m119a(c0097p));
                } else {
                    Intent intent = new Intent(m115o());
                    intent.addCategory("android.intent.category.DEFAULT");
                    context.startActivity(intent);
                }
            } catch (Exception e) {
                C0078ah.m83d(f214a, "Failed to launch promotional canvas for hook: " + str, e);
            }
        }
    }

    /* renamed from: a */
    final void m127a(AppCircleCallback appCircleCallback) {
        this.f239z = appCircleCallback;
    }

    /* renamed from: a */
    final void m132a(boolean z) {
        this.f234u = z;
    }

    /* renamed from: h */
    final boolean m144h() {
        return this.f234u;
    }

    /* renamed from: i */
    final String m145i() {
        return this.f221h;
    }

    /* renamed from: a */
    final synchronized void m125a(Context context, C0097p c0097p, String str) {
        if (m116p()) {
            this.f233t.post(new RunnableC0081ak(this, str, context, c0097p));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    public String m112d(String str) {
        try {
            if (!str.startsWith(f216c)) {
                HttpResponse execute = new DefaultHttpClient().execute(new HttpGet(str));
                int statusCode = execute.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    str = EntityUtils.toString(execute.getEntity());
                    if (!str.startsWith(f216c)) {
                        str = m112d(str);
                    }
                } else {
                    C0078ah.m80c(f214a, "Cannot process with responseCode " + statusCode);
                    m113e("Error when fetching application's android market ID, responseCode " + statusCode);
                }
            }
            return str;
        } catch (UnknownHostException e) {
            C0078ah.m80c(f214a, "Unknown host: " + e.getMessage());
            if (this.f239z != null) {
                m113e("Unknown host: " + e.getMessage());
            }
            return null;
        } catch (Exception e2) {
            C0078ah.m81c(f214a, "Failed on url: " + str, e2);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: e */
    public void m113e(String str) {
        m108a(new RunnableC0075ae(this, str));
    }

    /* renamed from: b */
    final synchronized Offer m134b(String str) {
        List m106a;
        Offer offer = null;
        synchronized (this) {
            if (m116p() && (m106a = m106a(Arrays.asList(str), (Long) null)) != null && !m106a.isEmpty()) {
                offer = m103a(str, (C0103v) m106a.get(0));
                C0078ah.m72a(f214a, "Impression for offer with ID " + offer.getId());
            }
        }
        return offer;
    }

    /* renamed from: a */
    final synchronized void m123a(Context context, long j) {
        if (m116p()) {
            OfferInSdk offerInSdk = (OfferInSdk) this.f238y.get(Long.valueOf(j));
            if (offerInSdk == null) {
                C0078ah.m77b(f214a, "Cannot find offer " + j);
            } else {
                C0097p m136b = m136b(offerInSdk.f77b);
                offerInSdk.f77b = m136b;
                String str = FlurryAgent.m35c() + m119a(m136b);
                C0078ah.m72a(f214a, "Offer " + offerInSdk.f76a + " accepted. Sent with cookies: " + this.f232s);
                m125a(context, m136b, str);
            }
        }
    }

    /* renamed from: c */
    final synchronized List m138c(String str) {
        List arrayList;
        if (!m116p()) {
            arrayList = Collections.emptyList();
        } else if (!this.f228o.m176b()) {
            arrayList = Collections.emptyList();
        } else {
            C0103v[] m173a = this.f228o.m173a(str);
            arrayList = new ArrayList();
            if (m173a != null && m173a.length > 0) {
                for (C0103v c0103v : m173a) {
                    arrayList.add(m103a(str, c0103v));
                }
            }
            C0078ah.m72a(f214a, "Impressions for " + arrayList.size() + " offers.");
        }
        return arrayList;
    }

    /* renamed from: a */
    final synchronized void m130a(List list) {
        if (m116p()) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                this.f238y.remove((Long) it.next());
            }
        }
    }

    /* renamed from: a */
    private Offer m103a(String str, C0103v c0103v) {
        C0097p c0097p = new C0097p(str, (byte) 3, m146j());
        m111c(c0097p);
        c0097p.m95a(new C0087f((byte) 2, m146j()));
        c0097p.f206b = c0103v;
        C0082al m169a = this.f228o.m169a(c0103v.f240a);
        String str2 = m169a == null ? MutantMessages.sEmpty : m169a.f113a;
        int i = m169a == null ? 0 : m169a.f115c;
        long j = f213A + 1;
        f213A = j;
        OfferInSdk offerInSdk = new OfferInSdk(j, c0097p, c0103v.f247h, c0103v.f243d, str2, i);
        this.f238y.put(Long.valueOf(offerInSdk.f76a), offerInSdk);
        return new Offer(offerInSdk.f76a, offerInSdk.f81f, offerInSdk.f78c, offerInSdk.f79d, offerInSdk.f80e);
    }

    /* renamed from: a */
    final synchronized List m120a(Context context, List list, Long l, int i, boolean z) {
        List emptyList;
        if (!m116p()) {
            emptyList = Collections.emptyList();
        } else if (this.f228o.m176b() && list != null) {
            List m106a = m106a(list, l);
            int min = Math.min(list.size(), m106a.size());
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < min; i2++) {
                String str = (String) list.get(i2);
                C0086e m175b = this.f228o.m175b(str);
                if (m175b != null) {
                    C0097p c0097p = new C0097p((String) list.get(i2), (byte) 1, m146j());
                    m111c(c0097p);
                    if (i2 < m106a.size()) {
                        c0097p.f206b = (C0103v) m106a.get(i2);
                        c0097p.m95a(new C0087f((byte) 2, m146j()));
                        arrayList.add(new C0106y(context, this, c0097p, m175b, i, z));
                    }
                } else {
                    C0078ah.m82d(f214a, "Cannot find hook: " + str);
                }
            }
            emptyList = arrayList;
        } else {
            emptyList = Collections.emptyList();
        }
        return emptyList;
    }

    /* renamed from: a */
    final synchronized View m117a(Context context, String str, int i) {
        C0096o c0096o;
        if (!m116p()) {
            c0096o = null;
        } else {
            c0096o = new C0096o(this, context, str, i);
            this.f236w.m71a(c0096o);
        }
        return c0096o;
    }

    /* renamed from: c */
    private void m111c(C0097p c0097p) {
        if (this.f237x.size() < 32767) {
            this.f237x.add(c0097p);
            this.f235v.put(Long.valueOf(c0097p.m94a()), c0097p);
        }
    }

    /* renamed from: a */
    private List m106a(List list, Long l) {
        if (list == null || list.isEmpty() || !this.f228o.m176b()) {
            return Collections.emptyList();
        }
        C0103v[] m173a = this.f228o.m173a((String) list.get(0));
        if (m173a != null && m173a.length > 0) {
            ArrayList arrayList = new ArrayList(Arrays.asList(m173a));
            Collections.shuffle(arrayList);
            if (l != null) {
                Iterator it = arrayList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    if (((C0103v) it.next()).f240a == l.longValue()) {
                        it.remove();
                        break;
                    }
                }
            }
            return arrayList.subList(0, Math.min(arrayList.size(), list.size()));
        }
        return Collections.emptyList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: j */
    public final synchronized long m146j() {
        long elapsedRealtime = SystemClock.elapsedRealtime() - this.f226m;
        if (elapsedRealtime <= this.f227n) {
            elapsedRealtime = this.f227n + 1;
            this.f227n = elapsedRealtime;
        }
        this.f227n = elapsedRealtime;
        return this.f227n;
    }

    @Override // android.view.View.OnClickListener
    public final synchronized void onClick(View view) {
        C0106y c0106y = (C0106y) view;
        C0097p m136b = m136b(c0106y.m159a());
        c0106y.m160a(m136b);
        String m119a = m119a(m136b);
        if (this.f234u) {
            m109b(view.getContext(), m136b, this.f221h + m119a);
        } else {
            m125a(view.getContext(), m136b, this.f222i + m119a);
        }
    }

    /* renamed from: a */
    final synchronized void m129a(String str, String str2) {
        this.f232s.put(str, str2);
    }

    /* renamed from: k */
    final synchronized void m147k() {
        this.f232s.clear();
    }

    /* renamed from: b */
    private void m109b(Context context, C0097p c0097p, String str) {
        Intent intent = new Intent(m115o());
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra("u", str);
        if (c0097p != null) {
            intent.putExtra("o", c0097p.m94a());
        }
        context.startActivity(intent);
    }

    /* renamed from: o */
    private static String m115o() {
        return FlurryAgent.f17a != null ? FlurryAgent.f17a : f219f;
    }

    /* renamed from: a */
    final synchronized String m119a(C0097p c0097p) {
        StringBuilder append;
        C0103v c0103v = c0097p.f206b;
        append = new StringBuilder().append("?apik=").append(this.f223j).append("&cid=").append(c0103v.f244e).append("&adid=").append(c0103v.f240a).append("&pid=").append(this.f231r).append("&iid=").append(this.f224k).append("&sid=").append(this.f225l).append("&its=").append(c0097p.m94a()).append("&hid=").append(C0099r.m98a(c0097p.f205a)).append("&ac=").append(m105a(c0103v.f246g));
        if (this.f232s != null && !this.f232s.isEmpty()) {
            for (Map.Entry entry : this.f232s.entrySet()) {
                append.append("&").append("c_" + C0099r.m98a((String) entry.getKey())).append("=").append(C0099r.m98a((String) entry.getValue()));
            }
        }
        append.append("&ats=").append(System.currentTimeMillis());
        return append.toString();
    }

    /* renamed from: a */
    private static String m105a(byte[] bArr) {
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

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[adLogs=").append(this.f237x).append("]");
        return sb.toString();
    }

    /* renamed from: l */
    final synchronized AdImage m148l() {
        return !m116p() ? null : m114n();
    }

    /* renamed from: b */
    final synchronized C0097p m136b(C0097p c0097p) {
        if (!this.f237x.contains(c0097p)) {
            C0097p c0097p2 = new C0097p(c0097p, m146j());
            this.f237x.add(c0097p2);
            c0097p = c0097p2;
        }
        c0097p.m95a(new C0087f((byte) 4, m146j()));
        return c0097p;
    }

    /* renamed from: a */
    private static void m108a(Runnable runnable) {
        new Handler().post(runnable);
    }

    /* renamed from: a */
    final synchronized void m121a(int i) {
        if (this.f239z != null) {
            m108a(new RunnableC0074ad(this, i));
        }
    }

    /* renamed from: m */
    final synchronized boolean m149m() {
        return !m116p() ? false : this.f228o.m176b();
    }

    /* renamed from: p */
    private boolean m116p() {
        if (!this.f230q) {
            C0078ah.m82d(f214a, "AppCircle is not initialized");
        }
        if (this.f231r == null) {
            C0078ah.m82d(f214a, "Cannot identify UDID.");
        }
        return this.f230q;
    }
}
