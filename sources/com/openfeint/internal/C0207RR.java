package com.openfeint.internal;

import android.content.res.Resources;

/* renamed from: com.openfeint.internal.RR */
/* loaded from: classes.dex */
public final class C0207RR {
    private static Resources _resources = null;
    private static String _packageName = null;

    private static final Resources resources() {
        if (_resources == null) {
            _resources = OpenFeintInternal.getInstance().getContext().getResources();
        }
        return _resources;
    }

    private static final String packageName() {
        if (_packageName == null) {
            _packageName = OpenFeintInternal.getInstance().getContext().getPackageName();
        }
        return _packageName;
    }

    private static final int identifier(String name, String type) {
        return resources().getIdentifier(name, type, packageName());
    }

    public static final int string(String name) {
        return identifier(name, "string");
    }

    public static final int drawable(String name) {
        return identifier(name, "drawable");
    }

    /* renamed from: id */
    public static final int m180id(String name) {
        return identifier(name, "id");
    }

    public static final int layout(String name) {
        return identifier(name, "layout");
    }

    public static final int menu(String name) {
        return identifier(name, "menu");
    }

    public static final int style(String name) {
        return identifier(name, "style");
    }
}
