package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

/* compiled from: JsonWriteContext.java */
/* loaded from: classes.dex */
final class RootWContext extends JsonWriteContext {
    public RootWContext() {
        super(0, null);
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
        this._index++;
        return this._index == 0 ? 0 : 3;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonWriteContext
    protected void appendDesc(StringBuilder sb) {
        sb.append("/");
    }
}
