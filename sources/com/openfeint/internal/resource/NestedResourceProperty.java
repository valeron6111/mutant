package com.openfeint.internal.resource;

/* loaded from: classes.dex */
public abstract class NestedResourceProperty extends ResourceProperty {
    private Class<? extends Resource> mType;

    public abstract Resource get(Resource resource);

    public abstract void set(Resource resource, Resource resource2);

    public NestedResourceProperty(Class<? extends Resource> type) {
        this.mType = type;
    }

    public Class<? extends Resource> getType() {
        return this.mType;
    }
}
