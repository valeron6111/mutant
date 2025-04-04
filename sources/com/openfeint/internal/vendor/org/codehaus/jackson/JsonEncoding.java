package com.openfeint.internal.vendor.org.codehaus.jackson;

import com.openfeint.internal.vendor.org.apache.commons.codec.CharEncoding;

/* loaded from: classes.dex */
public enum JsonEncoding {
    UTF8("UTF-8", false),
    UTF16_BE(CharEncoding.UTF_16BE, true),
    UTF16_LE(CharEncoding.UTF_16LE, false),
    UTF32_BE("UTF-32BE", true),
    UTF32_LE("UTF-32LE", false);

    final boolean mBigEndian;
    final String mJavaName;

    JsonEncoding(String javaName, boolean bigEndian) {
        this.mJavaName = javaName;
        this.mBigEndian = bigEndian;
    }

    public String getJavaName() {
        return this.mJavaName;
    }

    public boolean isBigEndian() {
        return this.mBigEndian;
    }
}
