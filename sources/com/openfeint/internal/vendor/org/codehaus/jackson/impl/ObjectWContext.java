package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

/* compiled from: JsonWriteContext.java */
/* loaded from: classes.dex */
final class ObjectWContext extends JsonWriteContext {
    protected String _currentName;
    protected boolean _expectValue;

    public ObjectWContext(JsonWriteContext parent) {
        super(2, parent);
        this._currentName = null;
        this._expectValue = false;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonStreamContext
    public String getCurrentName() {
        return this._currentName;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonWriteContext
    public int writeFieldName(String name) {
        if (this._currentName != null) {
            return 4;
        }
        this._currentName = name;
        return this._index < 0 ? 0 : 1;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonWriteContext
    public int writeValue() {
        if (this._currentName == null) {
            return 5;
        }
        this._currentName = null;
        this._index++;
        return 2;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonWriteContext
    protected void appendDesc(StringBuilder sb) {
        sb.append('{');
        if (this._currentName != null) {
            sb.append('\"');
            sb.append(this._currentName);
            sb.append('\"');
        } else {
            sb.append('?');
        }
        sb.append(']');
    }
}
