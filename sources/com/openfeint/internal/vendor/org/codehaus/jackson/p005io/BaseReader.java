package com.openfeint.internal.vendor.org.codehaus.jackson.p005io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/* loaded from: classes.dex */
abstract class BaseReader extends Reader {
    protected static final int LAST_VALID_UNICODE_CHAR = 1114111;
    protected static final char NULL_BYTE = 0;
    protected static final char NULL_CHAR = 0;
    protected byte[] mBuffer;
    protected final IOContext mContext;
    protected InputStream mIn;
    protected int mLength;
    protected int mPtr;
    char[] mTmpBuf = null;

    protected BaseReader(IOContext context, InputStream in, byte[] buf, int ptr, int len) {
        this.mContext = context;
        this.mIn = in;
        this.mBuffer = buf;
        this.mPtr = ptr;
        this.mLength = len;
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        InputStream in = this.mIn;
        if (in != null) {
            this.mIn = null;
            freeBuffers();
            in.close();
        }
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        if (this.mTmpBuf == null) {
            this.mTmpBuf = new char[1];
        }
        if (read(this.mTmpBuf, 0, 1) < 1) {
            return -1;
        }
        return this.mTmpBuf[0];
    }

    public final void freeBuffers() {
        byte[] buf = this.mBuffer;
        if (buf != null) {
            this.mBuffer = null;
            this.mContext.releaseReadIOBuffer(buf);
        }
    }

    protected void reportBounds(char[] cbuf, int start, int len) throws IOException {
        throw new ArrayIndexOutOfBoundsException("read(buf," + start + "," + len + "), cbuf[" + cbuf.length + "]");
    }

    protected void reportStrangeStream() throws IOException {
        throw new IOException("Strange I/O stream, returned 0 bytes on read");
    }
}
