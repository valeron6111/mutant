package com.openfeint.internal.vendor.org.codehaus.jackson.p005io;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonEncoding;
import com.openfeint.internal.vendor.org.codehaus.jackson.util.BufferRecycler;
import com.openfeint.internal.vendor.org.codehaus.jackson.util.TextBuffer;

/* loaded from: classes.dex */
public final class IOContext {
    final BufferRecycler _bufferRecycler;
    protected JsonEncoding _encoding;
    protected final boolean _managedResource;
    final Object _sourceRef;
    protected byte[] _readIOBuffer = null;
    protected byte[] _writeEncodingBuffer = null;
    protected char[] _tokenCBuffer = null;
    protected char[] _concatCBuffer = null;
    protected char[] _nameCopyBuffer = null;

    public IOContext(BufferRecycler br, Object sourceRef, boolean managedResource) {
        this._bufferRecycler = br;
        this._sourceRef = sourceRef;
        this._managedResource = managedResource;
    }

    public void setEncoding(JsonEncoding enc) {
        this._encoding = enc;
    }

    public Object getSourceReference() {
        return this._sourceRef;
    }

    public JsonEncoding getEncoding() {
        return this._encoding;
    }

    public boolean isResourceManaged() {
        return this._managedResource;
    }

    public TextBuffer constructTextBuffer() {
        return new TextBuffer(this._bufferRecycler);
    }

    public byte[] allocReadIOBuffer() {
        if (this._readIOBuffer != null) {
            throw new IllegalStateException("Trying to call allocReadIOBuffer() second time");
        }
        this._readIOBuffer = this._bufferRecycler.allocByteBuffer(BufferRecycler.ByteBufferType.READ_IO_BUFFER);
        return this._readIOBuffer;
    }

    public byte[] allocWriteEncodingBuffer() {
        if (this._writeEncodingBuffer != null) {
            throw new IllegalStateException("Trying to call allocWriteEncodingBuffer() second time");
        }
        this._writeEncodingBuffer = this._bufferRecycler.allocByteBuffer(BufferRecycler.ByteBufferType.WRITE_ENCODING_BUFFER);
        return this._writeEncodingBuffer;
    }

    public char[] allocTokenBuffer() {
        if (this._tokenCBuffer != null) {
            throw new IllegalStateException("Trying to call allocTokenBuffer() second time");
        }
        this._tokenCBuffer = this._bufferRecycler.allocCharBuffer(BufferRecycler.CharBufferType.TOKEN_BUFFER);
        return this._tokenCBuffer;
    }

    public char[] allocConcatBuffer() {
        if (this._concatCBuffer != null) {
            throw new IllegalStateException("Trying to call allocConcatBuffer() second time");
        }
        this._concatCBuffer = this._bufferRecycler.allocCharBuffer(BufferRecycler.CharBufferType.CONCAT_BUFFER);
        return this._concatCBuffer;
    }

    public char[] allocNameCopyBuffer(int minSize) {
        if (this._nameCopyBuffer != null) {
            throw new IllegalStateException("Trying to call allocNameCopyBuffer() second time");
        }
        this._nameCopyBuffer = this._bufferRecycler.allocCharBuffer(BufferRecycler.CharBufferType.NAME_COPY_BUFFER, minSize);
        return this._nameCopyBuffer;
    }

    public void releaseReadIOBuffer(byte[] buf) {
        if (buf != null) {
            if (buf != this._readIOBuffer) {
                throw new IllegalArgumentException("Trying to release buffer not owned by the context");
            }
            this._readIOBuffer = null;
            this._bufferRecycler.releaseByteBuffer(BufferRecycler.ByteBufferType.READ_IO_BUFFER, buf);
        }
    }

    public void releaseWriteEncodingBuffer(byte[] buf) {
        if (buf != null) {
            if (buf != this._writeEncodingBuffer) {
                throw new IllegalArgumentException("Trying to release buffer not owned by the context");
            }
            this._writeEncodingBuffer = null;
            this._bufferRecycler.releaseByteBuffer(BufferRecycler.ByteBufferType.WRITE_ENCODING_BUFFER, buf);
        }
    }

    public void releaseTokenBuffer(char[] buf) {
        if (buf != null) {
            if (buf != this._tokenCBuffer) {
                throw new IllegalArgumentException("Trying to release buffer not owned by the context");
            }
            this._tokenCBuffer = null;
            this._bufferRecycler.releaseCharBuffer(BufferRecycler.CharBufferType.TOKEN_BUFFER, buf);
        }
    }

    public void releaseConcatBuffer(char[] buf) {
        if (buf != null) {
            if (buf != this._concatCBuffer) {
                throw new IllegalArgumentException("Trying to release buffer not owned by the context");
            }
            this._concatCBuffer = null;
            this._bufferRecycler.releaseCharBuffer(BufferRecycler.CharBufferType.CONCAT_BUFFER, buf);
        }
    }

    public void releaseNameCopyBuffer(char[] buf) {
        if (buf != null) {
            if (buf != this._nameCopyBuffer) {
                throw new IllegalArgumentException("Trying to release buffer not owned by the context");
            }
            this._nameCopyBuffer = null;
            this._bufferRecycler.releaseCharBuffer(BufferRecycler.CharBufferType.NAME_COPY_BUFFER, buf);
        }
    }
}
