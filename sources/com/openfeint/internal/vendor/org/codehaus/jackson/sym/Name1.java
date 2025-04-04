package com.openfeint.internal.vendor.org.codehaus.jackson.sym;

import com.alawar.mutant.jni.MutantMessages;

/* loaded from: classes.dex */
public final class Name1 extends Name {
    static final Name1 sEmptyName = new Name1(MutantMessages.sEmpty, 0, 0);
    final int mQuad;

    Name1(String name, int hash, int quad) {
        super(name, hash);
        this.mQuad = quad;
    }

    static final Name1 getEmptyName() {
        return sEmptyName;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.sym.Name
    public boolean equals(int quad) {
        return quad == this.mQuad;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.sym.Name
    public boolean equals(int quad1, int quad2) {
        return quad1 == this.mQuad && quad2 == 0;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.sym.Name
    public boolean equals(int[] quads, int qlen) {
        return qlen == 1 && quads[0] == this.mQuad;
    }
}
