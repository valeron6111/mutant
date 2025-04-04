package com.openfeint.internal.vendor.org.codehaus.jackson.util;

/* loaded from: classes.dex */
public final class BufferRecycler {
    public static final int DEFAULT_WRITE_CONCAT_BUFFER_LEN = 2000;
    protected final byte[][] mByteBuffers = new byte[ByteBufferType.values().length][];
    protected final char[][] mCharBuffers = new char[CharBufferType.values().length][];

    public enum ByteBufferType {
        READ_IO_BUFFER(4000),
        WRITE_ENCODING_BUFFER(4000),
        WRITE_CONCAT_BUFFER(BufferRecycler.DEFAULT_WRITE_CONCAT_BUFFER_LEN);

        private final int size;

        ByteBufferType(int size) {
            this.size = size;
        }
    }

    public enum CharBufferType {
        TOKEN_BUFFER(BufferRecycler.DEFAULT_WRITE_CONCAT_BUFFER_LEN),
        CONCAT_BUFFER(BufferRecycler.DEFAULT_WRITE_CONCAT_BUFFER_LEN),
        TEXT_BUFFER(200),
        NAME_COPY_BUFFER(200);

        private final int size;

        CharBufferType(int size) {
            this.size = size;
        }
    }

    public byte[] allocByteBuffer(ByteBufferType type) {
        int ix = type.ordinal();
        byte[] buffer = this.mByteBuffers[ix];
        if (buffer == null) {
            return balloc(type.size);
        }
        this.mByteBuffers[ix] = null;
        return buffer;
    }

    public void releaseByteBuffer(ByteBufferType type, byte[] buffer) {
        this.mByteBuffers[type.ordinal()] = buffer;
    }

    public char[] allocCharBuffer(CharBufferType type) {
        return allocCharBuffer(type, 0);
    }

    public char[] allocCharBuffer(CharBufferType type, int minSize) {
        if (type.size > minSize) {
            minSize = type.size;
        }
        int ix = type.ordinal();
        char[] buffer = this.mCharBuffers[ix];
        if (buffer == null || buffer.length < minSize) {
            return calloc(minSize);
        }
        this.mCharBuffers[ix] = null;
        return buffer;
    }

    public void releaseCharBuffer(CharBufferType type, char[] buffer) {
        this.mCharBuffers[type.ordinal()] = buffer;
    }

    private byte[] balloc(int size) {
        return new byte[size];
    }

    private char[] calloc(int size) {
        return new char[size];
    }
}
