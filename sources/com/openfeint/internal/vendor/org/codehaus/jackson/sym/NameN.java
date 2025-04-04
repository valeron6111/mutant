package com.openfeint.internal.vendor.org.codehaus.jackson.sym;

/* loaded from: classes.dex */
public final class NameN extends Name {
    final int mQuadLen;
    final int[] mQuads;

    NameN(String name, int hash, int[] quads, int quadLen) {
        super(name, hash);
        if (quadLen < 3) {
            throw new IllegalArgumentException("Qlen must >= 3");
        }
        this.mQuads = quads;
        this.mQuadLen = quadLen;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.sym.Name
    public boolean equals(int quad) {
        return false;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.sym.Name
    public boolean equals(int quad1, int quad2) {
        return false;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.sym.Name
    public boolean equals(int[] quads, int qlen) {
        if (qlen != this.mQuadLen) {
            return false;
        }
        for (int i = 0; i < qlen; i++) {
            if (quads[i] != this.mQuads[i]) {
                return false;
            }
        }
        return true;
    }
}
