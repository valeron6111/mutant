package com.openfeint.internal.vendor.org.codehaus.jackson.type;

import java.lang.reflect.Modifier;

/* loaded from: classes.dex */
public abstract class JavaType {
    protected final Class<?> _class;
    protected int _hashCode;
    protected Object _typeHandler;
    protected Object _valueHandler;

    protected abstract JavaType _narrow(Class<?> cls);

    public abstract boolean equals(Object obj);

    public abstract boolean isContainerType();

    public abstract JavaType narrowContentsBy(Class<?> cls);

    public abstract String toCanonical();

    public abstract String toString();

    protected JavaType(Class<?> clz) {
        this._class = clz;
        String name = clz.getName();
        this._hashCode = name.hashCode();
    }

    public final JavaType narrowBy(Class<?> subclass) {
        if (subclass != this._class) {
            _assertSubclass(subclass, this._class);
            JavaType result = _narrow(subclass);
            if (this._valueHandler != null) {
                result.setValueHandler(this._valueHandler);
            }
            if (this._typeHandler != null) {
                result.setTypeHandler(this._typeHandler);
            }
            return result;
        }
        return this;
    }

    public final JavaType forcedNarrowBy(Class<?> subclass) {
        if (subclass != this._class) {
            JavaType result = _narrow(subclass);
            if (this._valueHandler != null) {
                result.setValueHandler(this._valueHandler);
            }
            if (this._typeHandler != null) {
                result.setTypeHandler(this._typeHandler);
            }
            return result;
        }
        return this;
    }

    public final JavaType widenBy(Class<?> superclass) {
        if (superclass != this._class) {
            _assertSubclass(this._class, superclass);
            return _widen(superclass);
        }
        return this;
    }

    protected JavaType _widen(Class<?> superclass) {
        return _narrow(superclass);
    }

    public void setValueHandler(Object h) {
        if (h != null && this._valueHandler != null) {
            throw new IllegalStateException("Trying to reset value handler for type [" + toString() + "]; old handler of type " + this._valueHandler.getClass().getName() + ", new handler of type " + h.getClass().getName());
        }
        this._valueHandler = h;
    }

    public void setTypeHandler(Object h) {
        if (h != null && this._typeHandler != null) {
            throw new IllegalStateException("Trying to reset type handler for type [" + toString() + "]; old handler of type " + this._typeHandler.getClass().getName() + ", new handler of type " + h.getClass().getName());
        }
        this._typeHandler = h;
    }

    public final Class<?> getRawClass() {
        return this._class;
    }

    public final boolean hasRawClass(Class<?> clz) {
        return this._class == clz;
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this._class.getModifiers());
    }

    public boolean isConcrete() {
        int mod = this._class.getModifiers();
        return (mod & 1536) == 0 || this._class.isPrimitive();
    }

    public boolean isThrowable() {
        return Throwable.class.isAssignableFrom(this._class);
    }

    public boolean isArrayType() {
        return false;
    }

    public final boolean isEnumType() {
        return this._class.isEnum();
    }

    public final boolean isInterface() {
        return this._class.isInterface();
    }

    public final boolean isPrimitive() {
        return this._class.isPrimitive();
    }

    public final boolean isFinal() {
        return Modifier.isFinal(this._class.getModifiers());
    }

    public JavaType getKeyType() {
        return null;
    }

    public JavaType getContentType() {
        return null;
    }

    public int containedTypeCount() {
        return 0;
    }

    public JavaType containedType(int index) {
        return null;
    }

    public String containedTypeName(int index) {
        return null;
    }

    public <T> T getValueHandler() {
        return (T) this._valueHandler;
    }

    public <T> T getTypeHandler() {
        return (T) this._typeHandler;
    }

    protected void _assertSubclass(Class<?> subclass, Class<?> superClass) {
        if (!this._class.isAssignableFrom(subclass)) {
            throw new IllegalArgumentException("Class " + subclass.getName() + " is not assignable to " + this._class.getName());
        }
    }

    public final int hashCode() {
        return this._hashCode;
    }
}
