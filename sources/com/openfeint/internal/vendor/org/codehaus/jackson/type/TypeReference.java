package com.openfeint.internal.vendor.org.codehaus.jackson.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/* loaded from: classes.dex */
public abstract class TypeReference<T> implements Comparable<TypeReference<T>> {
    final Type _type;

    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }
        this._type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return this._type;
    }

    @Override // java.lang.Comparable
    public int compareTo(TypeReference<T> o) {
        return 0;
    }
}
