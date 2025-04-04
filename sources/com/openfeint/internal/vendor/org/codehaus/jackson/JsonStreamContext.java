package com.openfeint.internal.vendor.org.codehaus.jackson;

import com.alawar.mutant.database.DbBuilder;

/* loaded from: classes.dex */
public abstract class JsonStreamContext {
    protected static final int TYPE_ARRAY = 1;
    protected static final int TYPE_OBJECT = 2;
    protected static final int TYPE_ROOT = 0;
    protected int _index = -1;
    protected int _type;

    public abstract String getCurrentName();

    public abstract JsonStreamContext getParent();

    public JsonStreamContext(int type) {
        this._type = type;
    }

    public final boolean inArray() {
        return this._type == 1;
    }

    public final boolean inRoot() {
        return this._type == 0;
    }

    public final boolean inObject() {
        return this._type == 2;
    }

    public final String getTypeDesc() {
        switch (this._type) {
            case DbBuilder.ID_COLUMN /* 0 */:
                return "ROOT";
            case 1:
                return "ARRAY";
            case 2:
                return "OBJECT";
            default:
                return "?";
        }
    }

    public final int getEntryCount() {
        return this._index + 1;
    }

    public final int getCurrentIndex() {
        if (this._index < 0) {
            return 0;
        }
        return this._index;
    }
}
