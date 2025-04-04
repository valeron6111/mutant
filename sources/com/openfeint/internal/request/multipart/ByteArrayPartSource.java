package com.openfeint.internal.request.multipart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class ByteArrayPartSource implements PartSource {
    private byte[] bytes;
    private String fileName;

    public ByteArrayPartSource(String fileName, byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
    }

    @Override // com.openfeint.internal.request.multipart.PartSource
    public long getLength() {
        return this.bytes.length;
    }

    @Override // com.openfeint.internal.request.multipart.PartSource
    public String getFileName() {
        return this.fileName;
    }

    @Override // com.openfeint.internal.request.multipart.PartSource
    public InputStream createInputStream() throws IOException {
        return new ByteArrayInputStream(this.bytes);
    }
}
