package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonStreamContext;

/* loaded from: classes.dex */
public abstract class JsonWriteContext extends JsonStreamContext {
    public static final int STATUS_EXPECT_NAME = 5;
    public static final int STATUS_EXPECT_VALUE = 4;
    public static final int STATUS_OK_AFTER_COLON = 2;
    public static final int STATUS_OK_AFTER_COMMA = 1;
    public static final int STATUS_OK_AFTER_SPACE = 3;
    public static final int STATUS_OK_AS_IS = 0;
    JsonWriteContext _childArray;
    JsonWriteContext _childObject;
    protected final JsonWriteContext _parent;

    protected abstract void appendDesc(StringBuilder sb);

    public abstract int writeFieldName(String str);

    public abstract int writeValue();

    protected JsonWriteContext(int type, JsonWriteContext parent) {
        super(type);
        this._childArray = null;
        this._childObject = null;
        this._parent = parent;
    }

    public static JsonWriteContext createRootContext() {
        return new RootWContext();
    }

    public final JsonWriteContext createChildArrayContext() {
        JsonWriteContext ctxt = this._childArray;
        if (ctxt == null) {
            JsonWriteContext ctxt2 = new ArrayWContext(this);
            this._childArray = ctxt2;
            return ctxt2;
        }
        ctxt._index = -1;
        return ctxt;
    }

    public final JsonWriteContext createChildObjectContext() {
        JsonWriteContext ctxt = this._childObject;
        if (ctxt == null) {
            JsonWriteContext ctxt2 = new ObjectWContext(this);
            this._childObject = ctxt2;
            return ctxt2;
        }
        ctxt._index = -1;
        return ctxt;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonStreamContext
    public final JsonWriteContext getParent() {
        return this._parent;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(64);
        appendDesc(sb);
        return sb.toString();
    }
}
