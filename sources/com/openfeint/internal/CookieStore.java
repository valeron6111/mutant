package com.openfeint.internal;

import android.os.Bundle;
import com.openfeint.internal.SyncedStore;
import com.openfeint.internal.logcat.OFLog;
import java.util.Date;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

/* loaded from: classes.dex */
public class CookieStore extends BasicCookieStore {
    static final String COOKIE_VALUE_TAG = "value";
    static final String tag = "CookieStore";
    SyncedStore mSharedPreferences;
    static final String COOKIE_PREFIX = "_of_cookie_";
    static final int COOKIE_PREFIX_LEN = COOKIE_PREFIX.length();
    static final int COOKIE_VALUE_TAG_LEN = "value".length();

    public synchronized void saveInstanceState(Bundle outState) {
        for (Cookie c : super.getCookies()) {
            cookieToBundle(c, outState);
        }
    }

    public synchronized void restoreInstanceState(Bundle inState) {
        for (String k : inState.keySet()) {
            if (k.startsWith(COOKIE_PREFIX) && k.endsWith("value")) {
                String cookieName = k.substring(COOKIE_PREFIX_LEN, k.length() - COOKIE_VALUE_TAG_LEN);
                BasicClientCookie c = cookieFromBundle(inState, cookieName);
                super.addCookie(c);
            }
        }
    }

    public CookieStore(SyncedStore sp) {
        this.mSharedPreferences = sp;
        SyncedStore.Reader r = this.mSharedPreferences.read();
        try {
            for (String k : r.keySet()) {
                if (k.startsWith(COOKIE_PREFIX) && k.endsWith("value")) {
                    String cookieName = k.substring(COOKIE_PREFIX_LEN, k.length() - COOKIE_VALUE_TAG_LEN);
                    BasicClientCookie c = cookieFromPrefs(r, cookieName);
                    super.addCookie(c);
                }
            }
        } finally {
            r.complete();
        }
    }

    private BasicClientCookie cookieFromBundle(Bundle b, String cookieName) {
        String prefix = COOKIE_PREFIX + cookieName;
        String cookieValue = b.getString(prefix + "value");
        if (cookieValue == null) {
            return null;
        }
        String cookiePath = b.getString(prefix + "path");
        String cookieDomain = b.getString(prefix + "domain");
        String cookieExpiry = b.getString(prefix + "expiry");
        BasicClientCookie c = new BasicClientCookie(cookieName, cookieValue);
        c.setPath(cookiePath);
        c.setDomain(cookieDomain);
        if (cookieExpiry != null) {
            c.setExpiryDate(dateFromString(cookieExpiry));
        }
        return c;
    }

    private BasicClientCookie cookieFromPrefs(SyncedStore.Reader r, String cookieName) {
        String prefix = COOKIE_PREFIX + cookieName;
        String cookieValue = r.getString(prefix + "value", null);
        if (cookieValue == null) {
            return null;
        }
        String cookiePath = r.getString(prefix + "path", null);
        String cookieDomain = r.getString(prefix + "domain", null);
        String cookieExpiry = r.getString(prefix + "expiry", null);
        BasicClientCookie c = new BasicClientCookie(cookieName, cookieValue);
        c.setPath(cookiePath);
        c.setDomain(cookieDomain);
        c.setExpiryDate(dateFromString(cookieExpiry));
        return c;
    }

    private void cookieToBundle(Cookie cookie, Bundle b) {
        String name = cookie.getName();
        b.putString(COOKIE_PREFIX + name + "value", cookie.getValue());
        b.putString(COOKIE_PREFIX + name + "path", cookie.getPath());
        b.putString(COOKIE_PREFIX + name + "domain", cookie.getDomain());
        Date expiryDate = cookie.getExpiryDate();
        if (expiryDate != null) {
            b.putString(COOKIE_PREFIX + name + "expiry", stringFromDate(expiryDate));
        }
    }

    private void cookieToPrefs(Cookie cookie, SyncedStore.Editor e) {
        String name = cookie.getName();
        e.putString(COOKIE_PREFIX + name + "value", cookie.getValue());
        e.putString(COOKIE_PREFIX + name + "path", cookie.getPath());
        e.putString(COOKIE_PREFIX + name + "domain", cookie.getDomain());
        e.putString(COOKIE_PREFIX + name + "expiry", stringFromDate(cookie.getExpiryDate()));
    }

    @Override // org.apache.http.impl.client.BasicCookieStore, org.apache.http.client.CookieStore
    public synchronized void addCookie(Cookie cookie) {
        super.addCookie(cookie);
        String name = cookie.getName();
        SyncedStore.Reader r = this.mSharedPreferences.read();
        try {
            Cookie existing = cookieFromPrefs(r, name);
            if (existing == null || !existing.getValue().equals(cookie.getValue()) || !existing.getPath().equals(cookie.getPath()) || !existing.getDomain().equals(cookie.getDomain()) || !existing.getExpiryDate().equals(cookie.getExpiryDate())) {
                SyncedStore.Editor e = this.mSharedPreferences.edit();
                try {
                    String prefix = COOKIE_PREFIX + name;
                    for (String k : e.keySet()) {
                        if (k.startsWith(prefix)) {
                            e.remove(k);
                        }
                    }
                    if (cookie.getExpiryDate() != null) {
                        cookieToPrefs(cookie, e);
                    }
                } finally {
                    e.commit();
                }
            }
        } finally {
            r.complete();
        }
    }

    public synchronized void clearCookies(SyncedStore.Editor e) {
        for (String k : e.keySet()) {
            if (k.startsWith(COOKIE_PREFIX)) {
                e.remove(k);
            }
        }
        super.clear();
    }

    private static final String stringFromDate(Date d) {
        return DateUtils.formatDate(d);
    }

    private static final Date dateFromString(String s) {
        try {
            return DateUtils.parseDate(s);
        } catch (DateParseException e) {
            OFLog.m182e(tag, "Couldn't parse date: '" + s + "'");
            return null;
        }
    }
}
