package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.IOContext;
import java.io.IOException;
import java.io.Reader;

/* loaded from: classes.dex */
public abstract class ReaderBasedNumericParser extends ReaderBasedParserBase {
    public ReaderBasedNumericParser(IOContext pc, int features, Reader r) {
        super(pc, features, r);
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x0017  */
    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonNumericParserBase
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected final com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken parseNumberText(int r15) throws java.io.IOException, com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException {
        /*
            Method dump skipped, instructions count: 200
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.openfeint.internal.vendor.org.codehaus.jackson.impl.ReaderBasedNumericParser.parseNumberText(int):com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken");
    }

    private final JsonToken parseNumberText2(boolean negative) throws IOException, JsonParseException {
        char c;
        char c2;
        int outPtr;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int outPtr2 = 0;
        if (negative) {
            int outPtr3 = 0 + 1;
            outBuf[0] = '-';
            outPtr2 = outPtr3;
        }
        int intLen = 0;
        boolean eof = false;
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                c = 0;
                eof = true;
                break;
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            c = cArr[i];
            if (c < '0' || c > '9') {
                break;
            }
            intLen++;
            if (intLen == 2 && outBuf[outPtr2 - 1] == '0') {
                reportInvalidNumber("Leading zeroes not allowed");
            }
            if (outPtr2 >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr2 = 0;
            }
            outBuf[outPtr2] = c;
            outPtr2++;
        }
        if (intLen == 0) {
            reportInvalidNumber("Missing integer part (next char " + _getCharDesc(c) + ")");
        }
        int fractLen = 0;
        if (c == '.') {
            int outPtr4 = outPtr2 + 1;
            outBuf[outPtr2] = c;
            while (true) {
                outPtr2 = outPtr4;
                if (this._inputPtr >= this._inputEnd && !loadMore()) {
                    eof = true;
                    break;
                }
                char[] cArr2 = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                c = cArr2[i2];
                if (c < '0' || c > '9') {
                    break;
                }
                fractLen++;
                if (outPtr2 >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr2 = 0;
                }
                outPtr4 = outPtr2 + 1;
                outBuf[outPtr2] = c;
            }
            if (fractLen == 0) {
                reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
            }
        }
        int expLen = 0;
        if (c == 'e' || c == 'E') {
            if (outPtr2 >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr2 = 0;
            }
            int outPtr5 = outPtr2 + 1;
            outBuf[outPtr2] = c;
            if (this._inputPtr < this._inputEnd) {
                char[] cArr3 = this._inputBuffer;
                int i3 = this._inputPtr;
                this._inputPtr = i3 + 1;
                c2 = cArr3[i3];
            } else {
                c2 = getNextChar("expected a digit for number exponent");
            }
            if (c2 == '-' || c2 == '+') {
                if (outPtr5 >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                } else {
                    outPtr = outPtr5;
                }
                int outPtr6 = outPtr + 1;
                outBuf[outPtr] = c2;
                if (this._inputPtr < this._inputEnd) {
                    char[] cArr4 = this._inputBuffer;
                    int i4 = this._inputPtr;
                    this._inputPtr = i4 + 1;
                    c2 = cArr4[i4];
                } else {
                    c2 = getNextChar("expected a digit for number exponent");
                }
                outPtr2 = outPtr6;
            } else {
                outPtr2 = outPtr5;
            }
            while (true) {
                if (c2 > '9' || c2 < '0') {
                    break;
                }
                expLen++;
                if (outPtr2 >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr2 = 0;
                }
                int outPtr7 = outPtr2 + 1;
                outBuf[outPtr2] = c2;
                if (this._inputPtr >= this._inputEnd && !loadMore()) {
                    eof = true;
                    outPtr2 = outPtr7;
                    break;
                }
                char[] cArr5 = this._inputBuffer;
                int i5 = this._inputPtr;
                this._inputPtr = i5 + 1;
                c2 = cArr5[i5];
                outPtr2 = outPtr7;
            }
            if (expLen == 0) {
                reportUnexpectedNumberChar(c2, "Exponent indicator not followed by a digit");
            }
        }
        if (!eof) {
            this._inputPtr--;
        }
        this._textBuffer.setCurrentLength(outPtr2);
        return reset(negative, intLen, fractLen, expLen);
    }
}
