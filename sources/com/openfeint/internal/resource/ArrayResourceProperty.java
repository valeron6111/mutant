package com.openfeint.internal.resource;

import java.util.List;

/* loaded from: classes.dex */
public abstract class ArrayResourceProperty extends ResourceProperty {
    private Class<? extends Resource> mElementType;

    public abstract List<? extends Resource> get(Resource resource);

    public abstract void set(Resource resource, List<?> list);

    public ArrayResourceProperty(Class<? extends Resource> elementType) {
        this.mElementType = elementType;
    }

    public Class<? extends Resource> elementType() {
        return this.mElementType;
    }
}
