package com.openfeint.internal.resource;

import java.util.HashMap;

/* loaded from: classes.dex */
public abstract class ResourceClass {
    public Class<? extends Resource> mObjectClass;
    public HashMap<String, ResourceProperty> mProperties = new HashMap<>();
    public String mResourceName;

    public abstract Resource factory();

    private void mixinParentProperties(Class<?> objectClass) {
        if (objectClass != Resource.class) {
            Class<?> superClass = objectClass.getSuperclass();
            mixinParentProperties(superClass);
            ResourceClass klass = Resource.getKlass(superClass);
            for (String propName : klass.mProperties.keySet()) {
                this.mProperties.put(propName, klass.mProperties.get(propName));
            }
        }
    }

    public ResourceClass(Class<? extends Resource> objectClass, String resourceName) {
        this.mObjectClass = objectClass;
        this.mResourceName = resourceName;
        mixinParentProperties(objectClass);
    }
}
