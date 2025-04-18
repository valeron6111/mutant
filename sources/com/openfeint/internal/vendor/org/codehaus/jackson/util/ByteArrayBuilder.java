package com.openfeint.internal.vendor.org.codehaus.jackson.util;

import com.openfeint.internal.vendor.org.codehaus.jackson.util.BufferRecycler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: classes.dex */
public final class ByteArrayBuilder extends OutputStream {
    static final int DEFAULT_BLOCK_ARRAY_SIZE = 40;
    private static final int INITIAL_BLOCK_SIZE = 500;
    private static final int MAX_BLOCK_SIZE = 262144;
    private static final byte[] NO_BYTES = new byte[0];
    private final BufferRecycler _bufferRecycler;
    private byte[] _currBlock;
    private int _currBlockPtr;
    private final LinkedList<byte[]> _pastBlocks;
    private int _pastLen;

    public ByteArrayBuilder() {
        this((BufferRecycler) null);
    }

    public ByteArrayBuilder(BufferRecycler br) {
        this(br, INITIAL_BLOCK_SIZE);
    }

    public ByteArrayBuilder(int firstBlockSize) {
        this(null, firstBlockSize);
    }

    public ByteArrayBuilder(BufferRecycler br, int firstBlockSize) {
        this._pastBlocks = new LinkedList<>();
        this._bufferRecycler = br;
        if (br == null) {
            this._currBlock = new byte[firstBlockSize];
        } else {
            this._currBlock = br.allocByteBuffer(BufferRecycler.ByteBufferType.WRITE_CONCAT_BUFFER);
        }
    }

    public void reset() {
        this._pastLen = 0;
        this._currBlockPtr = 0;
        if (!this._pastBlocks.isEmpty()) {
            this._currBlock = this._pastBlocks.getLast();
            this._pastBlocks.clear();
        }
    }

    public void release() {
        reset();
        if (this._bufferRecycler != null && this._currBlock != null) {
            this._bufferRecycler.releaseByteBuffer(BufferRecycler.ByteBufferType.WRITE_CONCAT_BUFFER, this._currBlock);
        }
    }

    public void append(int i) {
        byte b = (byte) i;
        if (this._currBlockPtr >= this._currBlock.length) {
            _allocMore();
        }
        byte[] bArr = this._currBlock;
        int i2 = this._currBlockPtr;
        this._currBlockPtr = i2 + 1;
        bArr[i2] = b;
    }

    public void appendTwoBytes(int b16) {
        if (this._currBlockPtr + 1 < this._currBlock.length) {
            byte[] bArr = this._currBlock;
            int i = this._currBlockPtr;
            this._currBlockPtr = i + 1;
            bArr[i] = (byte) (b16 >> 8);
            byte[] bArr2 = this._currBlock;
            int i2 = this._currBlockPtr;
            this._currBlockPtr = i2 + 1;
            bArr2[i2] = (byte) b16;
            return;
        }
        append(b16 >> 8);
        append(b16);
    }

    public void appendThreeBytes(int b24) {
        if (this._currBlockPtr + 2 < this._currBlock.length) {
            byte[] bArr = this._currBlock;
            int i = this._currBlockPtr;
            this._currBlockPtr = i + 1;
            bArr[i] = (byte) (b24 >> 16);
            byte[] bArr2 = this._currBlock;
            int i2 = this._currBlockPtr;
            this._currBlockPtr = i2 + 1;
            bArr2[i2] = (byte) (b24 >> 8);
            byte[] bArr3 = this._currBlock;
            int i3 = this._currBlockPtr;
            this._currBlockPtr = i3 + 1;
            bArr3[i3] = (byte) b24;
            return;
        }
        append(b24 >> 16);
        append(b24 >> 8);
        append(b24);
    }

    public byte[] toByteArray() {
        int totalLen = this._pastLen + this._currBlockPtr;
        if (totalLen == 0) {
            return NO_BYTES;
        }
        byte[] result = new byte[totalLen];
        int offset = 0;
        Iterator i$ = this._pastBlocks.iterator();
        while (i$.hasNext()) {
            byte[] block = i$.next();
            int len = block.length;
            System.arraycopy(block, 0, result, offset, len);
            offset += len;
        }
        System.arraycopy(this._currBlock, 0, result, offset, this._currBlockPtr);
        int offset2 = offset + this._currBlockPtr;
        if (offset2 != totalLen) {
            throw new RuntimeException("Internal error: total len assumed to be " + totalLen + ", copied " + offset2 + " bytes");
        }
        if (!this._pastBlocks.isEmpty()) {
            reset();
            return result;
        }
        return result;
    }

    private void _allocMore() {
        this._pastLen += this._currBlock.length;
        int newSize = Math.max(this._pastLen >> 1, 1000);
        if (newSize > MAX_BLOCK_SIZE) {
            newSize = MAX_BLOCK_SIZE;
        }
        this._pastBlocks.add(this._currBlock);
        this._currBlock = new byte[newSize];
        this._currBlockPtr = 0;
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) {
        write(b, 0, b.length);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) {
        while (true) {
            int max = this._currBlock.length - this._currBlockPtr;
            int toCopy = Math.min(max, len);
            if (toCopy > 0) {
                System.arraycopy(b, off, this._currBlock, this._currBlockPtr, toCopy);
                off += toCopy;
                this._currBlockPtr += toCopy;
                len -= toCopy;
            }
            if (len > 0) {
                _allocMore();
            } else {
                return;
            }
        }
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        append(b);
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() {
    }
}
