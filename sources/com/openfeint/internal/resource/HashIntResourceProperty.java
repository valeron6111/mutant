package com.openfeint.internal.resource;

import java.util.HashMap;

/* loaded from: classes.dex */
public abstract class HashIntResourceProperty extends ResourceProperty {
    public abstract HashMap<String, Integer> get(Resource resource);

    public abstract void set(Resource resource, HashMap<String, Integer> hashMap);
}
