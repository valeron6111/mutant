package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

import com.flurry.android.Constants;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonEncoding;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.ObjectCodec;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.IOContext;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.MergedStream;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.UTF32Reader;
import com.openfeint.internal.vendor.org.codehaus.jackson.sym.BytesToNameCanonicalizer;
import com.openfeint.internal.vendor.org.codehaus.jackson.sym.CharsToNameCanonicalizer;
import com.sponsorpay.sdk.android.publisher.SponsorPayPublisher;
import java.io.ByteArrayInputStream;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/* loaded from: classes.dex */
public final class ByteSourceBootstrapper {
    boolean _bigEndian;
    private final boolean _bufferRecyclable;
    int _bytesPerChar;
    final IOContext _context;
    final InputStream _in;
    final byte[] _inputBuffer;
    private int _inputEnd;
    protected int _inputProcessed;
    private int _inputPtr;

    public ByteSourceBootstrapper(IOContext ctxt, InputStream in) {
        this._bigEndian = true;
        this._bytesPerChar = 0;
        this._context = ctxt;
        this._in = in;
        this._inputBuffer = ctxt.allocReadIOBuffer();
        this._inputPtr = 0;
        this._inputEnd = 0;
        this._inputProcessed = 0;
        this._bufferRecyclable = true;
    }

    public ByteSourceBootstrapper(IOContext ctxt, byte[] inputBuffer, int inputStart, int inputLen) {
        this._bigEndian = true;
        this._bytesPerChar = 0;
        this._context = ctxt;
        this._in = null;
        this._inputBuffer = inputBuffer;
        this._inputPtr = inputStart;
        this._inputEnd = inputStart + inputLen;
        this._inputProcessed = -inputStart;
        this._bufferRecyclable = false;
    }

    public JsonEncoding detectEncoding() throws IOException, JsonParseException {
        JsonEncoding enc;
        boolean foundEncoding = false;
        if (ensureLoaded(4)) {
            int quad = (this._inputBuffer[this._inputPtr] << 24) | ((this._inputBuffer[this._inputPtr + 1] & Constants.UNKNOWN) << 16) | ((this._inputBuffer[this._inputPtr + 2] & Constants.UNKNOWN) << 8) | (this._inputBuffer[this._inputPtr + 3] & Constants.UNKNOWN);
            if (handleBOM(quad)) {
                foundEncoding = true;
            } else if (checkUTF32(quad)) {
                foundEncoding = true;
            } else if (checkUTF16(quad >>> 16)) {
                foundEncoding = true;
            }
        } else if (ensureLoaded(2)) {
            int i16 = ((this._inputBuffer[this._inputPtr] & Constants.UNKNOWN) << 8) | (this._inputBuffer[this._inputPtr + 1] & Constants.UNKNOWN);
            if (checkUTF16(i16)) {
                foundEncoding = true;
            }
        }
        if (!foundEncoding) {
            enc = JsonEncoding.UTF8;
        } else if (this._bytesPerChar == 2) {
            enc = this._bigEndian ? JsonEncoding.UTF16_BE : JsonEncoding.UTF16_LE;
        } else if (this._bytesPerChar == 4) {
            enc = this._bigEndian ? JsonEncoding.UTF32_BE : JsonEncoding.UTF32_LE;
        } else {
            throw new RuntimeException("Internal error");
        }
        this._context.setEncoding(enc);
        return enc;
    }

    public Reader constructReader() throws IOException {
        InputStream in;
        JsonEncoding enc = this._context.getEncoding();
        switch (enc) {
            case UTF32_BE:
            case UTF32_LE:
                return new UTF32Reader(this._context, this._in, this._inputBuffer, this._inputPtr, this._inputEnd, this._context.getEncoding().isBigEndian());
            case UTF16_BE:
            case UTF16_LE:
            case UTF8:
                InputStream in2 = this._in;
                if (in2 == null) {
                    in = new ByteArrayInputStream(this._inputBuffer, this._inputPtr, this._inputEnd);
                } else {
                    in = this._inputPtr < this._inputEnd ? new MergedStream(this._context, in2, this._inputBuffer, this._inputPtr, this._inputEnd) : in2;
                }
                return new InputStreamReader(in, enc.getJavaName());
            default:
                throw new RuntimeException("Internal error");
        }
    }

    public JsonParser constructParser(int features, ObjectCodec codec, BytesToNameCanonicalizer rootByteSymbols, CharsToNameCanonicalizer rootCharSymbols) throws IOException, JsonParseException {
        JsonEncoding enc = detectEncoding();
        boolean canonicalize = JsonParser.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(features);
        boolean intern = JsonParser.Feature.INTERN_FIELD_NAMES.enabledIn(features);
        if (enc != JsonEncoding.UTF8 || !canonicalize) {
            return new ReaderBasedParser(this._context, features, constructReader(), codec, rootCharSymbols.makeChild(canonicalize, intern));
        }
        BytesToNameCanonicalizer can = rootByteSymbols.makeChild(canonicalize, intern);
        return new Utf8StreamParser(this._context, features, this._in, codec, can, this._inputBuffer, this._inputPtr, this._inputEnd, this._bufferRecyclable);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:5:0x000e  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x003a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean handleBOM(int r7) throws java.io.IOException {
        /*
            r6 = this;
            r5 = 4
            r4 = 2
            r2 = 0
            r1 = 1
            switch(r7) {
                case -16842752: goto L34;
                case -131072: goto L24;
                case 65279: goto L19;
                case 65534: goto L2f;
                default: goto L7;
            }
        L7:
            int r0 = r7 >>> 16
            r3 = 65279(0xfeff, float:9.1475E-41)
            if (r0 != r3) goto L3a
            int r2 = r6._inputPtr
            int r2 = r2 + 2
            r6._inputPtr = r2
            r6._bytesPerChar = r4
            r6._bigEndian = r1
        L18:
            return r1
        L19:
            r6._bigEndian = r1
            int r2 = r6._inputPtr
            int r2 = r2 + 4
            r6._inputPtr = r2
            r6._bytesPerChar = r5
            goto L18
        L24:
            int r3 = r6._inputPtr
            int r3 = r3 + 4
            r6._inputPtr = r3
            r6._bytesPerChar = r5
            r6._bigEndian = r2
            goto L18
        L2f:
            java.lang.String r3 = "2143"
            r6.reportWeirdUCS4(r3)
        L34:
            java.lang.String r3 = "3412"
            r6.reportWeirdUCS4(r3)
            goto L7
        L3a:
            r3 = 65534(0xfffe, float:9.1833E-41)
            if (r0 != r3) goto L4a
            int r3 = r6._inputPtr
            int r3 = r3 + 2
            r6._inputPtr = r3
            r6._bytesPerChar = r4
            r6._bigEndian = r2
            goto L18
        L4a:
            int r3 = r7 >>> 8
            r4 = 15711167(0xefbbbf, float:2.2016034E-38)
            if (r3 != r4) goto L5c
            int r2 = r6._inputPtr
            int r2 = r2 + 3
            r6._inputPtr = r2
            r6._bytesPerChar = r1
            r6._bigEndian = r1
            goto L18
        L5c:
            r1 = r2
            goto L18
        */
        throw new UnsupportedOperationException("Method not decompiled: com.openfeint.internal.vendor.org.codehaus.jackson.impl.ByteSourceBootstrapper.handleBOM(int):boolean");
    }

    private boolean checkUTF32(int quad) throws IOException {
        if ((quad >> 8) == 0) {
            this._bigEndian = true;
        } else if ((16777215 & quad) == 0) {
            this._bigEndian = false;
        } else if (((-16711681) & quad) == 0) {
            reportWeirdUCS4("3412");
        } else {
            if (((-65281) & quad) != 0) {
                return false;
            }
            reportWeirdUCS4("2143");
        }
        this._bytesPerChar = 4;
        return true;
    }

    private boolean checkUTF16(int i16) {
        if ((65280 & i16) == 0) {
            this._bigEndian = true;
        } else {
            if ((i16 & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE) != 0) {
                return false;
            }
            this._bigEndian = false;
        }
        this._bytesPerChar = 2;
        return true;
    }

    private void reportWeirdUCS4(String type) throws IOException {
        throw new CharConversionException("Unsupported UCS-4 endianness (" + type + ") detected");
    }

    protected boolean ensureLoaded(int minimum) throws IOException {
        int count;
        int gotten = this._inputEnd - this._inputPtr;
        while (gotten < minimum) {
            if (this._in == null) {
                count = -1;
            } else {
                count = this._in.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
            }
            if (count < 1) {
                return false;
            }
            this._inputEnd += count;
            gotten += count;
        }
        return true;
    }
}
