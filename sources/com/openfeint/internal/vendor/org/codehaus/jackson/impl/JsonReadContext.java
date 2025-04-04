package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

import com.alawar.mutant.database.DbBuilder;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonLocation;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonStreamContext;
import com.openfeint.internal.vendor.org.codehaus.jackson.util.CharTypes;

/* loaded from: classes.dex */
public final class JsonReadContext extends JsonStreamContext {
    JsonReadContext _child;
    protected int _columnNr;
    protected String _currentName;
    protected int _lineNr;
    protected final JsonReadContext _parent;

    public JsonReadContext(JsonReadContext parent, int type, int lineNr, int colNr) {
        super(type);
        this._child = null;
        this._parent = parent;
        this._lineNr = lineNr;
        this._columnNr = colNr;
    }

    protected final void reset(int type, int lineNr, int colNr) {
        this._type = type;
        this._index = -1;
        this._lineNr = lineNr;
        this._columnNr = colNr;
        this._currentName = null;
    }

    public static JsonReadContext createRootContext(int lineNr, int colNr) {
        return new JsonReadContext(null, 0, lineNr, colNr);
    }

    public final JsonReadContext createChildArrayContext(int lineNr, int colNr) {
        JsonReadContext ctxt = this._child;
        if (ctxt == null) {
            JsonReadContext ctxt2 = new JsonReadContext(this, 1, lineNr, colNr);
            this._child = ctxt2;
            return ctxt2;
        }
        ctxt.reset(1, lineNr, colNr);
        return ctxt;
    }

    public final JsonReadContext createChildObjectContext(int lineNr, int colNr) {
        JsonReadContext ctxt = this._child;
        if (ctxt == null) {
            JsonReadContext ctxt2 = new JsonReadContext(this, 2, lineNr, colNr);
            this._child = ctxt2;
            return ctxt2;
        }
        ctxt.reset(2, lineNr, colNr);
        return ctxt;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonStreamContext
    public final String getCurrentName() {
        return this._currentName;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonStreamContext
    public final JsonReadContext getParent() {
        return this._parent;
    }

    public final JsonLocation getStartLocation(Object srcRef) {
        return new JsonLocation(srcRef, -1L, this._lineNr, this._columnNr);
    }

    public final boolean expectComma() {
        int ix = this._index + 1;
        this._index = ix;
        return this._type != 0 && ix > 0;
    }

    public void setCurrentName(String name) {
        this._currentName = name;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(64);
        switch (this._type) {
            case DbBuilder.ID_COLUMN /* 0 */:
                sb.append("/");
                break;
            case 1:
                sb.append('[');
                sb.append(getCurrentIndex());
                sb.append(']');
                break;
            case 2:
                sb.append('{');
                if (this._currentName != null) {
                    sb.append('\"');
                    CharTypes.appendQuoted(sb, this._currentName);
                    sb.append('\"');
                } else {
                    sb.append('?');
                }
                sb.append(']');
                break;
        }
        return sb.toString();
    }
}
