package com.openfeint.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.openfeint.api.p001ui.Dashboard;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.p004ui.Settings;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/* loaded from: classes.dex */
public class BaseActionInvoker {
    private static final String TAG = "BaseActionInvoker";

    public void login(Object args, Context ctx) {
        OpenFeintInternal.getInstance().launchIntroFlow(false);
    }

    public void dashboard(Object args, Context ctx) {
        Dashboard.openPath((String) args);
    }

    public void settings(Object args, Context ctx) {
        Settings.open((String) args);
    }

    public void intent(Object args, Context ctx) throws Exception {
        Field f;
        Map<String, Object> params = (Map) args;
        Intent i = new Intent();
        String actionType = (String) params.get("action");
        if (actionType != null && (f = Intent.class.getField(actionType)) != null) {
            i.setAction((String) f.get(null));
        }
        String uri = (String) params.get("uri");
        if (uri != null) {
            Uri parsedUri = Uri.parse(uri);
            if (parsedUri != null) {
                if (i.getAction() == null) {
                    i.setAction("android.intent.action.VIEW");
                    i.setData(parsedUri);
                }
                i.addFlags(268435456);
                Map<String, Object> extras = (Map) params.get("extras");
                if (extras != null) {
                    for (String k : extras.keySet()) {
                        Object o = extras.get(k);
                        if (o instanceof String) {
                            i.putExtra(k, (String) o);
                        } else if (o instanceof Number) {
                            i.putExtra(k, ((Number) o).intValue());
                        }
                    }
                }
                ctx.startActivity(i);
                return;
            }
            throw new Exception(String.format("parse url:{%s} failed", uri));
        }
        throw new Exception("url is null");
    }

    public final void invokeAction(Map<String, Object> action, Context ctx) {
        try {
            if (action == null) {
                OFLog.m185w(TAG, "action to invoke is null");
                return;
            }
            for (String actionKey : action.keySet()) {
                Object args = action.get(actionKey);
                Method m = getClass().getMethod(actionKey, Object.class, Context.class);
                if (m != null) {
                    m.invoke(this, args, ctx);
                } else {
                    OFLog.m181d(TAG, "method is null");
                }
            }
        } catch (Exception e) {
            OFLog.m182e(TAG, "Error invoking action: " + action.toString() + e.getMessage());
        }
    }
}
