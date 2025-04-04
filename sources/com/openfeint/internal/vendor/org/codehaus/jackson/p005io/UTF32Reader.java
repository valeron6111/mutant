package com.openfeint.internal.vendor.org.codehaus.jackson.p005io;

import com.flurry.android.Constants;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class UTF32Reader extends BaseReader {
    final boolean mBigEndian;
    int mByteCount;
    int mCharCount;
    char mSurrogate;

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.p005io.BaseReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.p005io.BaseReader, java.io.Reader
    public /* bridge */ /* synthetic */ int read() throws IOException {
        return super.read();
    }

    public UTF32Reader(IOContext ctxt, InputStream in, byte[] buf, int ptr, int len, boolean isBigEndian) {
        super(ctxt, in, buf, ptr, len);
        this.mSurrogate = (char) 0;
        this.mCharCount = 0;
        this.mByteCount = 0;
        this.mBigEndian = isBigEndian;
    }

    @Override // java.io.Reader
    public int read(char[] cbuf, int start, int len) throws IOException {
        int outPtr;
        int outPtr2;
        int ch;
        if (this.mBuffer == null) {
            return -1;
        }
        if (len < 1) {
            return len;
        }
        if (start < 0 || start + len > cbuf.length) {
            reportBounds(cbuf, start, len);
        }
        int len2 = len + start;
        if (this.mSurrogate != 0) {
            outPtr = start + 1;
            cbuf[start] = this.mSurrogate;
            this.mSurrogate = (char) 0;
        } else {
            int left = this.mLength - this.mPtr;
            if (left < 4 && !loadMore(left)) {
                return -1;
            }
            outPtr = start;
        }
        while (true) {
            if (outPtr >= len2) {
                outPtr2 = outPtr;
                break;
            }
            int ptr = this.mPtr;
            if (this.mBigEndian) {
                ch = (this.mBuffer[ptr] << 24) | ((this.mBuffer[ptr + 1] & Constants.UNKNOWN) << 16) | ((this.mBuffer[ptr + 2] & Constants.UNKNOWN) << 8) | (this.mBuffer[ptr + 3] & Constants.UNKNOWN);
            } else {
                ch = (this.mBuffer[ptr] & Constants.UNKNOWN) | ((this.mBuffer[ptr + 1] & Constants.UNKNOWN) << 8) | ((this.mBuffer[ptr + 2] & Constants.UNKNOWN) << 16) | (this.mBuffer[ptr + 3] << 24);
            }
            this.mPtr += 4;
            if (ch > 65535) {
                if (ch > 1114111) {
                    reportInvalid(ch, outPtr - start, "(above " + Integer.toHexString(1114111) + ") ");
                }
                int ch2 = ch - 65536;
                outPtr2 = outPtr + 1;
                cbuf[outPtr] = (char) (55296 + (ch2 >> 10));
                ch = 56320 | (ch2 & 1023);
                if (outPtr2 >= len2) {
                    this.mSurrogate = (char) ch;
                    break;
                }
            } else {
                outPtr2 = outPtr;
            }
            outPtr = outPtr2 + 1;
            cbuf[outPtr2] = (char) ch;
            if (this.mPtr >= this.mLength) {
                outPtr2 = outPtr;
                break;
            }
        }
        int len3 = outPtr2 - start;
        this.mCharCount += len3;
        return len3;
    }

    private void reportUnexpectedEOF(int gotBytes, int needed) throws IOException {
        int bytePos = this.mByteCount + gotBytes;
        int charPos = this.mCharCount;
        throw new CharConversionException("Unexpected EOF in the middle of a 4-byte UTF-32 char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private void reportInvalid(int value, int offset, String msg) throws IOException {
        int bytePos = (this.mByteCount + this.mPtr) - 1;
        int charPos = this.mCharCount + offset;
        throw new CharConversionException("Invalid UTF-32 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private boolean loadMore(int available) throws IOException {
        this.mByteCount += this.mLength - available;
        if (available > 0) {
            if (this.mPtr > 0) {
                for (int i = 0; i < available; i++) {
                    this.mBuffer[i] = this.mBuffer[this.mPtr + i];
                }
                this.mPtr = 0;
            }
            this.mLength = available;
        } else {
            this.mPtr = 0;
            int count = this.mIn.read(this.mBuffer);
            if (count < 1) {
                this.mLength = 0;
                if (count < 0) {
                    freeBuffers();
                    return false;
                }
                reportStrangeStream();
            }
            this.mLength = count;
        }
        while (this.mLength < 4) {
            int count2 = this.mIn.read(this.mBuffer, this.mLength, this.mBuffer.length - this.mLength);
            if (count2 < 1) {
                if (count2 < 0) {
                    freeBuffers();
                    reportUnexpectedEOF(this.mLength, 4);
                }
                reportStrangeStream();
            }
            this.mLength += count2;
        }
        return true;
    }
}
