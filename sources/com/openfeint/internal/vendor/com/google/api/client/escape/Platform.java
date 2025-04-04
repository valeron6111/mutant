package com.openfeint.internal.vendor.com.google.api.client.escape;

/* loaded from: classes.dex */
final class Platform {
    private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>() { // from class: com.openfeint.internal.vendor.com.google.api.client.escape.Platform.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public char[] initialValue() {
            return new char[1024];
        }
    };

    private Platform() {
    }

    static char[] charBufferFromThreadLocal() {
        return DEST_TL.get();
    }
}
