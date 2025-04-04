package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

/* compiled from: JsonWriteContext.java */
/* loaded from: classes.dex */
final class ArrayWContext extends JsonWriteContext {
    public ArrayWContext(JsonWriteContext parent) {
        super(1, parent);
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonStreamContext
    public String getCurrentName() {
        return null;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonWriteContext
    public int writeFieldName(String name) {
        return 4;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonWriteContext
    public int writeValue() {
        int ix = this._index;
        this._index++;
        return ix < 0 ? 0 : 1;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonWriteContext
    protected void appendDesc(StringBuilder sb) {
        sb.append('[');
        sb.append(getCurrentIndex());
        sb.append(']');
    }
}
