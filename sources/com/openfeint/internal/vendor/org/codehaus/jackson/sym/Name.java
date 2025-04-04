package com.openfeint.internal.vendor.org.codehaus.jackson.sym;

/* loaded from: classes.dex */
public abstract class Name {
    protected final int mHashCode;
    protected final String mName;

    public abstract boolean equals(int i);

    public abstract boolean equals(int i, int i2);

    public abstract boolean equals(int[] iArr, int i);

    protected Name(String name, int hashCode) {
        this.mName = name;
        this.mHashCode = hashCode;
    }

    public String getName() {
        return this.mName;
    }

    public String toString() {
        return this.mName;
    }

    public final int hashCode() {
        return this.mHashCode;
    }

    public boolean equals(Object o) {
        return o == this;
    }
}
