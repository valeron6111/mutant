package com.openfeint.internal.vendor.org.codehaus.jackson.p005io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/* loaded from: classes.dex */
public final class UTF8Writer extends Writer {
    static final int SURR1_FIRST = 55296;
    static final int SURR1_LAST = 56319;
    static final int SURR2_FIRST = 56320;
    static final int SURR2_LAST = 57343;
    protected final IOContext mContext;
    OutputStream mOut;
    byte[] mOutBuffer;
    final int mOutBufferLast;
    int mSurrogate = 0;
    int mOutPtr = 0;

    public UTF8Writer(IOContext ctxt, OutputStream out) {
        this.mContext = ctxt;
        this.mOut = out;
        this.mOutBuffer = ctxt.allocWriteEncodingBuffer();
        this.mOutBufferLast = this.mOutBuffer.length - 4;
    }

    @Override // java.io.Writer, java.lang.Appendable
    public Writer append(char c) throws IOException {
        write(c);
        return this;
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.mOut != null) {
            if (this.mOutPtr > 0) {
                this.mOut.write(this.mOutBuffer, 0, this.mOutPtr);
                this.mOutPtr = 0;
            }
            OutputStream out = this.mOut;
            this.mOut = null;
            byte[] buf = this.mOutBuffer;
            if (buf != null) {
                this.mOutBuffer = null;
                this.mContext.releaseWriteEncodingBuffer(buf);
            }
            out.close();
            int code = this.mSurrogate;
            this.mSurrogate = 0;
            if (code > 0) {
                throwIllegal(code);
            }
        }
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() throws IOException {
        if (this.mOutPtr > 0) {
            this.mOut.write(this.mOutBuffer, 0, this.mOutPtr);
            this.mOutPtr = 0;
        }
        this.mOut.flush();
    }

    @Override // java.io.Writer
    public void write(char[] cbuf) throws IOException {
        write(cbuf, 0, cbuf.length);
    }

    @Override // java.io.Writer
    public void write(char[] cbuf, int off, int len) throws IOException {
        int outPtr;
        int off2;
        if (len < 2) {
            if (len == 1) {
                write(cbuf[off]);
                return;
            }
            return;
        }
        if (this.mSurrogate > 0) {
            char second = cbuf[off];
            len--;
            write(convertSurrogate(second));
            off++;
        }
        int outPtr2 = this.mOutPtr;
        byte[] outBuf = this.mOutBuffer;
        int outBufLast = this.mOutBufferLast;
        int len2 = len + off;
        int off3 = off;
        while (true) {
            if (off3 >= len2) {
                break;
            }
            if (outPtr2 >= outBufLast) {
                this.mOut.write(outBuf, 0, outPtr2);
                outPtr2 = 0;
            }
            int off4 = off3 + 1;
            char c = cbuf[off3];
            if (c < 128) {
                outPtr = outPtr2 + 1;
                outBuf[outPtr2] = (byte) c;
                int maxInCount = len2 - off4;
                int maxOutCount = outBufLast - outPtr;
                if (maxInCount > maxOutCount) {
                    maxInCount = maxOutCount;
                }
                int maxInCount2 = maxInCount + off4;
                off3 = off4;
                while (off3 < maxInCount2) {
                    int off5 = off3 + 1;
                    c = cbuf[off3];
                    if (c >= 128) {
                        off3 = off5;
                    } else {
                        outBuf[outPtr] = (byte) c;
                        outPtr++;
                        off3 = off5;
                    }
                }
                outPtr2 = outPtr;
            } else {
                outPtr = outPtr2;
                off3 = off4;
            }
            if (c < 2048) {
                int outPtr3 = outPtr + 1;
                outBuf[outPtr] = (byte) ((c >> 6) | 192);
                outBuf[outPtr3] = (byte) ((c & '?') | 128);
                outPtr2 = outPtr3 + 1;
                off2 = off3;
            } else if (c < SURR1_FIRST || c > SURR2_LAST) {
                int outPtr4 = outPtr + 1;
                outBuf[outPtr] = (byte) ((c >> '\f') | 224);
                int outPtr5 = outPtr4 + 1;
                outBuf[outPtr4] = (byte) (((c >> 6) & 63) | 128);
                outPtr2 = outPtr5 + 1;
                outBuf[outPtr5] = (byte) ((c & '?') | 128);
            } else {
                if (c > SURR1_LAST) {
                    this.mOutPtr = outPtr;
                    throwIllegal(c);
                }
                this.mSurrogate = c;
                if (off3 >= len2) {
                    outPtr2 = outPtr;
                    break;
                }
                off2 = off3 + 1;
                int c2 = convertSurrogate(cbuf[off3]);
                if (c2 > 1114111) {
                    this.mOutPtr = outPtr;
                    throwIllegal(c2);
                }
                int outPtr6 = outPtr + 1;
                outBuf[outPtr] = (byte) ((c2 >> 18) | 240);
                int outPtr7 = outPtr6 + 1;
                outBuf[outPtr6] = (byte) (((c2 >> 12) & 63) | 128);
                int outPtr8 = outPtr7 + 1;
                outBuf[outPtr7] = (byte) (((c2 >> 6) & 63) | 128);
                outBuf[outPtr8] = (byte) ((c2 & 63) | 128);
                outPtr2 = outPtr8 + 1;
            }
            off3 = off2;
        }
        this.mOutPtr = outPtr2;
    }

    @Override // java.io.Writer
    public void write(int c) throws IOException {
        int ptr;
        if (this.mSurrogate > 0) {
            c = convertSurrogate(c);
        } else if (c >= SURR1_FIRST && c <= SURR2_LAST) {
            if (c > SURR1_LAST) {
                throwIllegal(c);
            }
            this.mSurrogate = c;
            return;
        }
        if (this.mOutPtr >= this.mOutBufferLast) {
            this.mOut.write(this.mOutBuffer, 0, this.mOutPtr);
            this.mOutPtr = 0;
        }
        if (c < 128) {
            byte[] bArr = this.mOutBuffer;
            int i = this.mOutPtr;
            this.mOutPtr = i + 1;
            bArr[i] = (byte) c;
            return;
        }
        int ptr2 = this.mOutPtr;
        if (c < 2048) {
            int ptr3 = ptr2 + 1;
            this.mOutBuffer[ptr2] = (byte) ((c >> 6) | 192);
            ptr = ptr3 + 1;
            this.mOutBuffer[ptr3] = (byte) ((c & 63) | 128);
        } else if (c <= 65535) {
            int ptr4 = ptr2 + 1;
            this.mOutBuffer[ptr2] = (byte) ((c >> 12) | 224);
            int ptr5 = ptr4 + 1;
            this.mOutBuffer[ptr4] = (byte) (((c >> 6) & 63) | 128);
            this.mOutBuffer[ptr5] = (byte) ((c & 63) | 128);
            ptr = ptr5 + 1;
        } else {
            if (c > 1114111) {
                throwIllegal(c);
            }
            int ptr6 = ptr2 + 1;
            this.mOutBuffer[ptr2] = (byte) ((c >> 18) | 240);
            int ptr7 = ptr6 + 1;
            this.mOutBuffer[ptr6] = (byte) (((c >> 12) & 63) | 128);
            int ptr8 = ptr7 + 1;
            this.mOutBuffer[ptr7] = (byte) (((c >> 6) & 63) | 128);
            ptr = ptr8 + 1;
            this.mOutBuffer[ptr8] = (byte) ((c & 63) | 128);
        }
        this.mOutPtr = ptr;
    }

    @Override // java.io.Writer
    public void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    @Override // java.io.Writer
    public void write(String str, int off, int len) throws IOException {
        int outPtr;
        int off2;
        if (len < 2) {
            if (len == 1) {
                write(str.charAt(off));
                return;
            }
            return;
        }
        if (this.mSurrogate > 0) {
            char second = str.charAt(off);
            len--;
            write(convertSurrogate(second));
            off++;
        }
        int outPtr2 = this.mOutPtr;
        byte[] outBuf = this.mOutBuffer;
        int outBufLast = this.mOutBufferLast;
        int len2 = len + off;
        int off3 = off;
        while (true) {
            if (off3 >= len2) {
                break;
            }
            if (outPtr2 >= outBufLast) {
                this.mOut.write(outBuf, 0, outPtr2);
                outPtr2 = 0;
            }
            int off4 = off3 + 1;
            int c = str.charAt(off3);
            if (c < 128) {
                outPtr = outPtr2 + 1;
                outBuf[outPtr2] = (byte) c;
                int maxInCount = len2 - off4;
                int maxOutCount = outBufLast - outPtr;
                if (maxInCount > maxOutCount) {
                    maxInCount = maxOutCount;
                }
                int maxInCount2 = maxInCount + off4;
                off3 = off4;
                while (off3 < maxInCount2) {
                    int off5 = off3 + 1;
                    c = str.charAt(off3);
                    if (c >= 128) {
                        off3 = off5;
                    } else {
                        outBuf[outPtr] = (byte) c;
                        outPtr++;
                        off3 = off5;
                    }
                }
                outPtr2 = outPtr;
            } else {
                outPtr = outPtr2;
                off3 = off4;
            }
            if (c < 2048) {
                int outPtr3 = outPtr + 1;
                outBuf[outPtr] = (byte) ((c >> 6) | 192);
                outBuf[outPtr3] = (byte) ((c & 63) | 128);
                outPtr2 = outPtr3 + 1;
                off2 = off3;
            } else if (c < SURR1_FIRST || c > SURR2_LAST) {
                int outPtr4 = outPtr + 1;
                outBuf[outPtr] = (byte) ((c >> 12) | 224);
                int outPtr5 = outPtr4 + 1;
                outBuf[outPtr4] = (byte) (((c >> 6) & 63) | 128);
                outPtr2 = outPtr5 + 1;
                outBuf[outPtr5] = (byte) ((c & 63) | 128);
            } else {
                if (c > SURR1_LAST) {
                    this.mOutPtr = outPtr;
                    throwIllegal(c);
                }
                this.mSurrogate = c;
                if (off3 >= len2) {
                    outPtr2 = outPtr;
                    break;
                }
                off2 = off3 + 1;
                int c2 = convertSurrogate(str.charAt(off3));
                if (c2 > 1114111) {
                    this.mOutPtr = outPtr;
                    throwIllegal(c2);
                }
                int outPtr6 = outPtr + 1;
                outBuf[outPtr] = (byte) ((c2 >> 18) | 240);
                int outPtr7 = outPtr6 + 1;
                outBuf[outPtr6] = (byte) (((c2 >> 12) & 63) | 128);
                int outPtr8 = outPtr7 + 1;
                outBuf[outPtr7] = (byte) (((c2 >> 6) & 63) | 128);
                outBuf[outPtr8] = (byte) ((c2 & 63) | 128);
                outPtr2 = outPtr8 + 1;
            }
            off3 = off2;
        }
        this.mOutPtr = outPtr2;
    }

    private int convertSurrogate(int secondPart) throws IOException {
        int firstPart = this.mSurrogate;
        this.mSurrogate = 0;
        if (secondPart < SURR2_FIRST || secondPart > SURR2_LAST) {
            throw new IOException("Broken surrogate pair: first char 0x" + Integer.toHexString(firstPart) + ", second 0x" + Integer.toHexString(secondPart) + "; illegal combination");
        }
        return 65536 + ((firstPart - SURR1_FIRST) << 10) + (secondPart - SURR2_FIRST);
    }

    private void throwIllegal(int code) throws IOException {
        if (code > 1114111) {
            throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output; max is 0x10FFFF as per RFC 4627");
        }
        if (code >= SURR1_FIRST) {
            if (code <= SURR1_LAST) {
                throw new IOException("Unmatched first part of surrogate pair (0x" + Integer.toHexString(code) + ")");
            }
            throw new IOException("Unmatched second part of surrogate pair (0x" + Integer.toHexString(code) + ")");
        }
        throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output");
    }
}
