package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

import com.alawar.mutant.jni.MutantMessages;
import com.flurry.android.Constants;
import com.openfeint.internal.vendor.org.codehaus.jackson.Base64Variant;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken;
import com.openfeint.internal.vendor.org.codehaus.jackson.ObjectCodec;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.IOContext;
import com.openfeint.internal.vendor.org.codehaus.jackson.sym.BytesToNameCanonicalizer;
import com.openfeint.internal.vendor.org.codehaus.jackson.sym.Name;
import com.openfeint.internal.vendor.org.codehaus.jackson.util.ByteArrayBuilder;
import com.openfeint.internal.vendor.org.codehaus.jackson.util.CharTypes;
import com.sponsorpay.sdk.android.publisher.SponsorPayPublisher;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class Utf8StreamParser extends Utf8NumericParser {
    static final byte BYTE_LF = 10;
    protected ObjectCodec _objectCodec;
    protected int[] _quadBuffer;
    protected final BytesToNameCanonicalizer _symbols;

    public Utf8StreamParser(IOContext ctxt, int features, InputStream in, ObjectCodec codec, BytesToNameCanonicalizer sym, byte[] inputBuffer, int start, int end, boolean bufferRecyclable) {
        super(ctxt, features, in, inputBuffer, start, end, bufferRecyclable);
        this._quadBuffer = new int[32];
        this._objectCodec = codec;
        this._symbols = sym;
        if (!JsonParser.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(features)) {
            _throwInternal();
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser
    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser
    public void setCodec(ObjectCodec c) {
        this._objectCodec = c;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonParserBase, com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser
    public JsonToken nextToken() throws IOException, JsonParseException {
        JsonToken t;
        if (this._currToken == JsonToken.FIELD_NAME) {
            return _nextAfterName();
        }
        if (this._tokenIncomplete) {
            _skipString();
        }
        int i = _skipWSOrEnd();
        if (i < 0) {
            close();
            this._currToken = null;
            return null;
        }
        this._tokenInputTotal = (this._currInputProcessed + this._inputPtr) - 1;
        this._tokenInputRow = this._currInputRow;
        this._tokenInputCol = (this._inputPtr - this._currInputRowStart) - 1;
        this._binaryValue = null;
        if (i == 93) {
            if (!this._parsingContext.inArray()) {
                _reportMismatchedEndMarker(i, '}');
            }
            this._parsingContext = this._parsingContext.getParent();
            JsonToken jsonToken = JsonToken.END_ARRAY;
            this._currToken = jsonToken;
            return jsonToken;
        }
        if (i == 125) {
            if (!this._parsingContext.inObject()) {
                _reportMismatchedEndMarker(i, ']');
            }
            this._parsingContext = this._parsingContext.getParent();
            JsonToken jsonToken2 = JsonToken.END_OBJECT;
            this._currToken = jsonToken2;
            return jsonToken2;
        }
        if (this._parsingContext.expectComma()) {
            if (i != 44) {
                _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.getTypeDesc() + " entries");
            }
            i = _skipWS();
        }
        boolean inObject = this._parsingContext.inObject();
        if (inObject) {
            Name n = _parseFieldName(i);
            this._parsingContext.setCurrentName(n.getName());
            this._currToken = JsonToken.FIELD_NAME;
            int i2 = _skipWS();
            if (i2 != 58) {
                _reportUnexpectedChar(i2, "was expecting a colon to separate field name and value");
            }
            i = _skipWS();
        }
        switch (i) {
            case 34:
                this._tokenIncomplete = true;
                t = JsonToken.VALUE_STRING;
                break;
            case 45:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                t = parseNumberText(i);
                break;
            case 91:
                if (!inObject) {
                    this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                }
                t = JsonToken.START_ARRAY;
                break;
            case 93:
            case 125:
                _reportUnexpectedChar(i, "expected a value");
                _matchToken(JsonToken.VALUE_TRUE);
                t = JsonToken.VALUE_TRUE;
                break;
            case 102:
                _matchToken(JsonToken.VALUE_FALSE);
                t = JsonToken.VALUE_FALSE;
                break;
            case 110:
                _matchToken(JsonToken.VALUE_NULL);
                t = JsonToken.VALUE_NULL;
                break;
            case 116:
                _matchToken(JsonToken.VALUE_TRUE);
                t = JsonToken.VALUE_TRUE;
                break;
            case 123:
                if (!inObject) {
                    this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                }
                t = JsonToken.START_OBJECT;
                break;
            default:
                t = _handleUnexpectedValue(i);
                break;
        }
        if (inObject) {
            this._nextToken = t;
            JsonToken t2 = this._currToken;
            return t2;
        }
        this._currToken = t;
        return t;
    }

    private final JsonToken _nextAfterName() {
        this._nameCopied = false;
        JsonToken t = this._nextToken;
        this._nextToken = null;
        if (t == JsonToken.START_ARRAY) {
            this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
        } else if (t == JsonToken.START_OBJECT) {
            this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
        }
        this._currToken = t;
        return t;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonParserBase, com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
        this._symbols.release();
    }

    protected final Name _parseFieldName(int i) throws IOException, JsonParseException {
        if (i != 34) {
            return _handleUnusualFieldName(i);
        }
        if (this._inputEnd - this._inputPtr < 9) {
            return slowParseFieldName();
        }
        int[] codes = CharTypes.getInputCodeLatin1();
        byte[] bArr = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int q = bArr[i2] & Constants.UNKNOWN;
        if (codes[q] != 0) {
            if (q == 34) {
                return BytesToNameCanonicalizer.getEmptyName();
            }
            return parseFieldName(0, q, 0);
        }
        byte[] bArr2 = this._inputBuffer;
        int i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        int i4 = bArr2[i3] & Constants.UNKNOWN;
        if (codes[i4] != 0) {
            if (i4 == 34) {
                return findName(q, 1);
            }
            return parseFieldName(q, i4, 1);
        }
        int q2 = (q << 8) | i4;
        byte[] bArr3 = this._inputBuffer;
        int i5 = this._inputPtr;
        this._inputPtr = i5 + 1;
        int i6 = bArr3[i5] & Constants.UNKNOWN;
        if (codes[i6] != 0) {
            if (i6 == 34) {
                return findName(q2, 2);
            }
            return parseFieldName(q2, i6, 2);
        }
        int q3 = (q2 << 8) | i6;
        byte[] bArr4 = this._inputBuffer;
        int i7 = this._inputPtr;
        this._inputPtr = i7 + 1;
        int i8 = bArr4[i7] & Constants.UNKNOWN;
        if (codes[i8] != 0) {
            if (i8 == 34) {
                return findName(q3, 3);
            }
            return parseFieldName(q3, i8, 3);
        }
        int q4 = (q3 << 8) | i8;
        byte[] bArr5 = this._inputBuffer;
        int i9 = this._inputPtr;
        this._inputPtr = i9 + 1;
        int i10 = bArr5[i9] & Constants.UNKNOWN;
        if (codes[i10] != 0) {
            if (i10 == 34) {
                return findName(q4, 4);
            }
            return parseFieldName(q4, i10, 4);
        }
        return parseMediumFieldName(q4, i10);
    }

    protected Name parseMediumFieldName(int q1, int q2) throws IOException, JsonParseException {
        int[] codes = CharTypes.getInputCodeLatin1();
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int i2 = bArr[i] & Constants.UNKNOWN;
        if (codes[i2] != 0) {
            if (i2 == 34) {
                return findName(q1, q2, 1);
            }
            return parseFieldName(q1, q2, i2, 1);
        }
        int q22 = (q2 << 8) | i2;
        byte[] bArr2 = this._inputBuffer;
        int i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        int i4 = bArr2[i3] & Constants.UNKNOWN;
        if (codes[i4] != 0) {
            if (i4 == 34) {
                return findName(q1, q22, 2);
            }
            return parseFieldName(q1, q22, i4, 2);
        }
        int q23 = (q22 << 8) | i4;
        byte[] bArr3 = this._inputBuffer;
        int i5 = this._inputPtr;
        this._inputPtr = i5 + 1;
        int i6 = bArr3[i5] & Constants.UNKNOWN;
        if (codes[i6] != 0) {
            if (i6 == 34) {
                return findName(q1, q23, 3);
            }
            return parseFieldName(q1, q23, i6, 3);
        }
        int q24 = (q23 << 8) | i6;
        byte[] bArr4 = this._inputBuffer;
        int i7 = this._inputPtr;
        this._inputPtr = i7 + 1;
        int i8 = bArr4[i7] & Constants.UNKNOWN;
        if (codes[i8] != 0) {
            if (i8 == 34) {
                return findName(q1, q24, 4);
            }
            return parseFieldName(q1, q24, i8, 4);
        }
        this._quadBuffer[0] = q1;
        this._quadBuffer[1] = q24;
        return parseLongFieldName(i8);
    }

    protected Name parseLongFieldName(int q) throws IOException, JsonParseException {
        int[] codes = CharTypes.getInputCodeLatin1();
        int qlen = 2;
        while (this._inputEnd - this._inputPtr >= 4) {
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int i2 = bArr[i] & Constants.UNKNOWN;
            if (codes[i2] != 0) {
                if (i2 == 34) {
                    return findName(this._quadBuffer, qlen, q, 1);
                }
                return parseEscapedFieldName(this._quadBuffer, qlen, q, i2, 1);
            }
            int q2 = (q << 8) | i2;
            byte[] bArr2 = this._inputBuffer;
            int i3 = this._inputPtr;
            this._inputPtr = i3 + 1;
            int i4 = bArr2[i3] & Constants.UNKNOWN;
            if (codes[i4] != 0) {
                if (i4 == 34) {
                    return findName(this._quadBuffer, qlen, q2, 2);
                }
                return parseEscapedFieldName(this._quadBuffer, qlen, q2, i4, 2);
            }
            int q3 = (q2 << 8) | i4;
            byte[] bArr3 = this._inputBuffer;
            int i5 = this._inputPtr;
            this._inputPtr = i5 + 1;
            int i6 = bArr3[i5] & Constants.UNKNOWN;
            if (codes[i6] != 0) {
                if (i6 == 34) {
                    return findName(this._quadBuffer, qlen, q3, 3);
                }
                return parseEscapedFieldName(this._quadBuffer, qlen, q3, i6, 3);
            }
            int q4 = (q3 << 8) | i6;
            byte[] bArr4 = this._inputBuffer;
            int i7 = this._inputPtr;
            this._inputPtr = i7 + 1;
            int i8 = bArr4[i7] & Constants.UNKNOWN;
            if (codes[i8] != 0) {
                if (i8 == 34) {
                    return findName(this._quadBuffer, qlen, q4, 4);
                }
                return parseEscapedFieldName(this._quadBuffer, qlen, q4, i8, 4);
            }
            if (qlen >= this._quadBuffer.length) {
                this._quadBuffer = growArrayBy(this._quadBuffer, qlen);
            }
            this._quadBuffer[qlen] = q4;
            q = i8;
            qlen++;
        }
        return parseEscapedFieldName(this._quadBuffer, qlen, 0, q, 0);
    }

    protected Name slowParseFieldName() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(": was expecting closing '\"' for name");
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int i2 = bArr[i] & Constants.UNKNOWN;
        return i2 == 34 ? BytesToNameCanonicalizer.getEmptyName() : parseEscapedFieldName(this._quadBuffer, 0, 0, i2, 0);
    }

    private final Name parseFieldName(int q1, int ch, int lastQuadBytes) throws IOException, JsonParseException {
        return parseEscapedFieldName(this._quadBuffer, 0, q1, ch, lastQuadBytes);
    }

    private final Name parseFieldName(int q1, int q2, int ch, int lastQuadBytes) throws IOException, JsonParseException {
        this._quadBuffer[0] = q1;
        return parseEscapedFieldName(this._quadBuffer, 1, q2, ch, lastQuadBytes);
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x0060  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00b3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected com.openfeint.internal.vendor.org.codehaus.jackson.sym.Name parseEscapedFieldName(int[] r8, int r9, int r10, int r11, int r12) throws java.io.IOException, com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException {
        /*
            Method dump skipped, instructions count: 202
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.openfeint.internal.vendor.org.codehaus.jackson.impl.Utf8StreamParser.parseEscapedFieldName(int[], int, int, int, int):com.openfeint.internal.vendor.org.codehaus.jackson.sym.Name");
    }

    protected final Name _handleUnusualFieldName(int ch) throws IOException, JsonParseException {
        if (ch == 39 && isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            return _parseApostropheFieldName();
        }
        if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
            _reportUnexpectedChar(ch, "was expecting double-quote to start field name");
        }
        int[] codes = CharTypes.getInputCodeUtf8JsNames();
        if (codes[ch] != 0) {
            _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
        }
        int[] quads = this._quadBuffer;
        int qlen = 0;
        int currQuad = 0;
        int currQuadBytes = 0;
        while (true) {
            int qlen2 = qlen;
            if (currQuadBytes < 4) {
                currQuadBytes++;
                currQuad = (currQuad << 8) | ch;
                qlen = qlen2;
            } else {
                if (qlen2 >= quads.length) {
                    quads = growArrayBy(quads, quads.length);
                    this._quadBuffer = quads;
                }
                qlen = qlen2 + 1;
                quads[qlen2] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
            }
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(" in field name");
            }
            ch = this._inputBuffer[this._inputPtr] & Constants.UNKNOWN;
            if (codes[ch] != 0) {
                break;
            }
            this._inputPtr++;
        }
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                quads = growArrayBy(quads, quads.length);
                this._quadBuffer = quads;
            }
            quads[qlen] = currQuad;
            qlen++;
        }
        Name name = this._symbols.findName(quads, qlen);
        if (name == null) {
            return addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    protected final Name _parseApostropheFieldName() throws IOException, JsonParseException {
        int qlen;
        int qlen2;
        int qlen3;
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(": was expecting closing ''' for name");
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int ch = bArr[i] & Constants.UNKNOWN;
        if (ch == 39) {
            return BytesToNameCanonicalizer.getEmptyName();
        }
        int[] quads = this._quadBuffer;
        int currQuad = 0;
        int currQuadBytes = 0;
        int[] codes = CharTypes.getInputCodeLatin1();
        int qlen4 = 0;
        while (ch != 39) {
            if (ch != 34 && codes[ch] != 0) {
                if (ch != 92) {
                    _throwUnquotedSpace(ch, "name");
                } else {
                    ch = _decodeEscaped();
                }
                if (ch > 127) {
                    if (currQuadBytes >= 4) {
                        if (qlen4 >= quads.length) {
                            quads = growArrayBy(quads, quads.length);
                            this._quadBuffer = quads;
                        }
                        quads[qlen4] = currQuad;
                        currQuad = 0;
                        currQuadBytes = 0;
                        qlen4++;
                    }
                    if (ch < 2048) {
                        currQuad = (currQuad << 8) | (ch >> 6) | 192;
                        currQuadBytes++;
                        qlen3 = qlen4;
                    } else {
                        int currQuad2 = (currQuad << 8) | (ch >> 12) | 224;
                        int currQuadBytes2 = currQuadBytes + 1;
                        if (currQuadBytes2 >= 4) {
                            if (qlen4 >= quads.length) {
                                quads = growArrayBy(quads, quads.length);
                                this._quadBuffer = quads;
                            }
                            qlen3 = qlen4 + 1;
                            quads[qlen4] = currQuad2;
                            currQuad2 = 0;
                            currQuadBytes2 = 0;
                        } else {
                            qlen3 = qlen4;
                        }
                        currQuad = (currQuad2 << 8) | ((ch >> 6) & 63) | 128;
                        currQuadBytes = currQuadBytes2 + 1;
                    }
                    ch = (ch & 63) | 128;
                    qlen4 = qlen3;
                }
            }
            if (currQuadBytes < 4) {
                currQuadBytes++;
                currQuad = (currQuad << 8) | ch;
                qlen2 = qlen4;
            } else {
                if (qlen4 >= quads.length) {
                    quads = growArrayBy(quads, quads.length);
                    this._quadBuffer = quads;
                }
                qlen2 = qlen4 + 1;
                quads[qlen4] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
            }
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(" in field name");
            }
            byte[] bArr2 = this._inputBuffer;
            int i2 = this._inputPtr;
            this._inputPtr = i2 + 1;
            ch = bArr2[i2] & Constants.UNKNOWN;
            qlen4 = qlen2;
        }
        if (currQuadBytes > 0) {
            if (qlen4 >= quads.length) {
                quads = growArrayBy(quads, quads.length);
                this._quadBuffer = quads;
            }
            qlen = qlen4 + 1;
            quads[qlen4] = currQuad;
        } else {
            qlen = qlen4;
        }
        Name name = this._symbols.findName(quads, qlen);
        if (name == null) {
            return addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    private final Name findName(int q1, int lastQuadBytes) throws JsonParseException {
        Name name = this._symbols.findName(q1);
        if (name == null) {
            this._quadBuffer[0] = q1;
            return addName(this._quadBuffer, 1, lastQuadBytes);
        }
        return name;
    }

    private final Name findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
        Name name = this._symbols.findName(q1, q2);
        if (name == null) {
            this._quadBuffer[0] = q1;
            this._quadBuffer[1] = q2;
            return addName(this._quadBuffer, 2, lastQuadBytes);
        }
        return name;
    }

    private final Name findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException {
        if (qlen >= quads.length) {
            quads = growArrayBy(quads, quads.length);
            this._quadBuffer = quads;
        }
        int qlen2 = qlen + 1;
        quads[qlen] = lastQuad;
        Name name = this._symbols.findName(quads, qlen2);
        if (name == null) {
            return addName(quads, qlen2, lastQuadBytes);
        }
        return name;
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x00ca  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00d0 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final com.openfeint.internal.vendor.org.codehaus.jackson.sym.Name addName(int[] r15, int r16, int r17) throws com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException {
        /*
            Method dump skipped, instructions count: 273
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.openfeint.internal.vendor.org.codehaus.jackson.impl.Utf8StreamParser.addName(int[], int, int):com.openfeint.internal.vendor.org.codehaus.jackson.sym.Name");
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonParserBase
    protected void _finishString() throws IOException, JsonParseException {
        int ptr;
        int outPtr;
        int outPtr2;
        int outPtr3 = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = CharTypes.getInputCodeUtf8();
        byte[] inputBuffer = this._inputBuffer;
        while (true) {
            int ptr2 = this._inputPtr;
            if (ptr2 >= this._inputEnd) {
                loadMoreGuaranteed();
                ptr2 = this._inputPtr;
            }
            if (outPtr3 >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr3 = 0;
            }
            int max = this._inputEnd;
            int max2 = ptr2 + (outBuf.length - outPtr3);
            if (max2 < max) {
                max = max2;
                ptr = ptr2;
                outPtr = outPtr3;
            } else {
                ptr = ptr2;
                outPtr = outPtr3;
            }
            while (true) {
                if (ptr < max) {
                    int ptr3 = ptr + 1;
                    int c = inputBuffer[ptr] & Constants.UNKNOWN;
                    if (codes[c] != 0) {
                        this._inputPtr = ptr3;
                        if (c != 34) {
                            switch (codes[c]) {
                                case 1:
                                    c = _decodeEscaped();
                                    outPtr2 = outPtr;
                                    break;
                                case 2:
                                    c = _decodeUtf8_2(c);
                                    outPtr2 = outPtr;
                                    break;
                                case 3:
                                    if (this._inputEnd - this._inputPtr >= 2) {
                                        c = _decodeUtf8_3fast(c);
                                        outPtr2 = outPtr;
                                        break;
                                    } else {
                                        c = _decodeUtf8_3(c);
                                        outPtr2 = outPtr;
                                        break;
                                    }
                                case 4:
                                    int c2 = _decodeUtf8_4(c);
                                    outPtr2 = outPtr + 1;
                                    outBuf[outPtr] = (char) (55296 | (c2 >> 10));
                                    if (outPtr2 >= outBuf.length) {
                                        outBuf = this._textBuffer.finishCurrentSegment();
                                        outPtr2 = 0;
                                    }
                                    c = 56320 | (c2 & 1023);
                                    break;
                                default:
                                    if (c < 32) {
                                        _throwUnquotedSpace(c, "string value");
                                        outPtr2 = outPtr;
                                        break;
                                    } else {
                                        _reportInvalidChar(c);
                                        outPtr2 = outPtr;
                                        break;
                                    }
                            }
                            if (outPtr2 >= outBuf.length) {
                                outBuf = this._textBuffer.finishCurrentSegment();
                                outPtr2 = 0;
                            }
                            outBuf[outPtr2] = (char) c;
                            outPtr3 = outPtr2 + 1;
                        } else {
                            this._textBuffer.setCurrentLength(outPtr);
                            return;
                        }
                    } else {
                        outBuf[outPtr] = (char) c;
                        ptr = ptr3;
                        outPtr++;
                    }
                } else {
                    this._inputPtr = ptr;
                    outPtr3 = outPtr;
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:43:0x0017, code lost:
    
        r5 = r4;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void _skipString() throws java.io.IOException, com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException {
        /*
            r7 = this;
            r6 = 0
            r7._tokenIncomplete = r6
            int[] r1 = com.openfeint.internal.vendor.org.codehaus.jackson.util.CharTypes.getInputCodeUtf8()
            byte[] r2 = r7._inputBuffer
        L9:
            int r4 = r7._inputPtr
            int r3 = r7._inputEnd
            if (r4 < r3) goto L50
            r7.loadMoreGuaranteed()
            int r4 = r7._inputPtr
            int r3 = r7._inputEnd
            r5 = r4
        L17:
            if (r5 >= r3) goto L2a
            int r4 = r5 + 1
            r6 = r2[r5]
            r0 = r6 & 255(0xff, float:3.57E-43)
            r6 = r1[r0]
            if (r6 == 0) goto L50
            r7._inputPtr = r4
            r6 = 34
            if (r0 != r6) goto L2d
            return
        L2a:
            r7._inputPtr = r5
            goto L9
        L2d:
            r6 = r1[r0]
            switch(r6) {
                case 1: goto L3c;
                case 2: goto L40;
                case 3: goto L44;
                case 4: goto L48;
                default: goto L32;
            }
        L32:
            r6 = 32
            if (r0 >= r6) goto L4c
            java.lang.String r6 = "string value"
            r7._throwUnquotedSpace(r0, r6)
            goto L9
        L3c:
            r7._decodeEscaped()
            goto L9
        L40:
            r7._skipUtf8_2(r0)
            goto L9
        L44:
            r7._skipUtf8_3(r0)
            goto L9
        L48:
            r7._skipUtf8_4(r0)
            goto L9
        L4c:
            r7._reportInvalidChar(r0)
            goto L9
        L50:
            r5 = r4
            goto L17
        */
        throw new UnsupportedOperationException("Method not decompiled: com.openfeint.internal.vendor.org.codehaus.jackson.impl.Utf8StreamParser._skipString():void");
    }

    protected final JsonToken _handleUnexpectedValue(int c) throws IOException, JsonParseException {
        if (c != 39 || !isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            _reportUnexpectedChar(c, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
        }
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = CharTypes.getInputCodeUtf8();
        byte[] inputBuffer = this._inputBuffer;
        while (true) {
            if (this._inputPtr >= this._inputEnd) {
                loadMoreGuaranteed();
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int max = this._inputEnd;
            int max2 = this._inputPtr + (outBuf.length - outPtr);
            if (max2 < max) {
                max = max2;
            }
            while (this._inputPtr < max) {
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int c2 = inputBuffer[i] & Constants.UNKNOWN;
                if (c2 != 39 && codes[c2] == 0) {
                    outBuf[outPtr] = (char) c2;
                    outPtr++;
                } else if (c2 != 39) {
                    switch (codes[c2]) {
                        case 1:
                            if (c2 != 34) {
                                c2 = _decodeEscaped();
                                break;
                            }
                            break;
                        case 2:
                            c2 = _decodeUtf8_2(c2);
                            break;
                        case 3:
                            if (this._inputEnd - this._inputPtr >= 2) {
                                c2 = _decodeUtf8_3fast(c2);
                                break;
                            } else {
                                c2 = _decodeUtf8_3(c2);
                                break;
                            }
                        case 4:
                            int c3 = _decodeUtf8_4(c2);
                            int outPtr2 = outPtr + 1;
                            outBuf[outPtr] = (char) (55296 | (c3 >> 10));
                            if (outPtr2 >= outBuf.length) {
                                outBuf = this._textBuffer.finishCurrentSegment();
                                outPtr = 0;
                            } else {
                                outPtr = outPtr2;
                            }
                            c2 = 56320 | (c3 & 1023);
                            break;
                        default:
                            if (c2 < 32) {
                                _throwUnquotedSpace(c2, "string value");
                            }
                            _reportInvalidChar(c2);
                            break;
                    }
                    if (outPtr >= outBuf.length) {
                        outBuf = this._textBuffer.finishCurrentSegment();
                        outPtr = 0;
                    }
                    outBuf[outPtr] = (char) c2;
                    outPtr++;
                } else {
                    this._textBuffer.setCurrentLength(outPtr);
                    return JsonToken.VALUE_STRING;
                }
            }
        }
    }

    protected void _matchToken(JsonToken token) throws IOException, JsonParseException {
        byte[] matchBytes = token.asByteArray();
        int len = matchBytes.length;
        for (int i = 1; i < len; i++) {
            if (this._inputPtr >= this._inputEnd) {
                loadMoreGuaranteed();
            }
            if (matchBytes[i] != this._inputBuffer[this._inputPtr]) {
                _reportInvalidToken(token.asString().substring(0, i));
            }
            this._inputPtr++;
        }
    }

    private void _reportInvalidToken(String matchedPart) throws IOException, JsonParseException {
        StringBuilder sb = new StringBuilder(matchedPart);
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                break;
            }
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int i2 = bArr[i];
            char c = (char) _decodeCharForError(i2);
            if (!Character.isJavaIdentifierPart(c)) {
                break;
            }
            this._inputPtr++;
            sb.append(c);
        }
        _reportError("Unrecognized token '" + sb.toString() + "': was expecting 'null', 'true' or 'false'");
    }

    private final int _skipWS() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = bArr[i] & Constants.UNKNOWN;
                if (i2 > 32) {
                    if (i2 != 47) {
                        return i2;
                    }
                    _skipComment();
                } else if (i2 != 32) {
                    if (i2 == 10) {
                        _skipLF();
                    } else if (i2 == 13) {
                        _skipCR();
                    } else if (i2 != 9) {
                        _throwInvalidSpace(i2);
                    }
                }
            } else {
                throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.getTypeDesc() + " entries");
            }
        }
    }

    private final int _skipWSOrEnd() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = bArr[i] & Constants.UNKNOWN;
                if (i2 > 32) {
                    if (i2 == 47) {
                        _skipComment();
                    } else {
                        return i2;
                    }
                } else if (i2 != 32) {
                    if (i2 == 10) {
                        _skipLF();
                    } else if (i2 == 13) {
                        _skipCR();
                    } else if (i2 != 9) {
                        _throwInvalidSpace(i2);
                    }
                }
            } else {
                _handleEOF();
                return -1;
            }
        }
    }

    private final void _skipComment() throws IOException, JsonParseException {
        if (!isEnabled(JsonParser.Feature.ALLOW_COMMENTS)) {
            _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
        }
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(" in a comment");
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int c = bArr[i] & Constants.UNKNOWN;
        if (c == 47) {
            _skipCppComment();
        } else if (c == 42) {
            _skipCComment();
        } else {
            _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
        }
    }

    private final void _skipCComment() throws IOException, JsonParseException {
        int[] codes = CharTypes.getInputCodeComment();
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = bArr[i] & Constants.UNKNOWN;
                int code = codes[i2];
                if (code != 0) {
                    switch (code) {
                        case 10:
                            _skipLF();
                            break;
                        case MutantMessages.cOpenAchievements /* 13 */:
                            _skipCR();
                            break;
                        case 42:
                            if (this._inputBuffer[this._inputPtr] != 47) {
                                break;
                            } else {
                                this._inputPtr++;
                                return;
                            }
                        default:
                            _reportInvalidChar(i2);
                            break;
                    }
                }
            } else {
                _reportInvalidEOF(" in a comment");
                return;
            }
        }
    }

    private final void _skipCppComment() throws IOException, JsonParseException {
        int[] codes = CharTypes.getInputCodeComment();
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = bArr[i] & Constants.UNKNOWN;
                int code = codes[i2];
                if (code != 0) {
                    switch (code) {
                        case 10:
                            _skipLF();
                            return;
                        case MutantMessages.cOpenAchievements /* 13 */:
                            _skipCR();
                            return;
                        case 42:
                            break;
                        default:
                            _reportInvalidChar(i2);
                            break;
                    }
                }
            } else {
                return;
            }
        }
    }

    protected final char _decodeEscaped() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(" in character escape sequence");
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int c = bArr[i];
        switch (c) {
            case 34:
            case 47:
            case 92:
                return (char) c;
            case 98:
                return '\b';
            case 102:
                return '\f';
            case 110:
                return '\n';
            case 114:
                return '\r';
            case 116:
                return '\t';
            case 117:
                break;
            default:
                _reportError("Unrecognized character escape (\\ followed by " + _getCharDesc(_decodeCharForError(c)) + ")");
                break;
        }
        int value = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(" in character escape sequence");
            }
            byte[] bArr2 = this._inputBuffer;
            int i3 = this._inputPtr;
            this._inputPtr = i3 + 1;
            int ch = bArr2[i3];
            int digit = CharTypes.charToHex(ch);
            if (digit < 0) {
                _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
            }
            value = (value << 4) | digit;
        }
        return (char) value;
    }

    protected int _decodeCharForError(int firstByte) throws IOException, JsonParseException {
        int needed;
        int c = firstByte;
        if (c < 0) {
            if ((c & 224) == 192) {
                c &= 31;
                needed = 1;
            } else if ((c & 240) == 224) {
                c &= 15;
                needed = 2;
            } else if ((c & 248) == 240) {
                c &= 7;
                needed = 3;
            } else {
                _reportInvalidInitial(c & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE);
                needed = 1;
            }
            int d = nextByte();
            if ((d & 192) != 128) {
                _reportInvalidOther(d & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE);
            }
            int c2 = (c << 6) | (d & 63);
            if (needed > 1) {
                int d2 = nextByte();
                if ((d2 & 192) != 128) {
                    _reportInvalidOther(d2 & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE);
                }
                int c3 = (c2 << 6) | (d2 & 63);
                if (needed > 2) {
                    int d3 = nextByte();
                    if ((d3 & 192) != 128) {
                        _reportInvalidOther(d3 & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE);
                    }
                    return (c3 << 6) | (d3 & 63);
                }
                return c3;
            }
            return c2;
        }
        return c;
    }

    private final int _decodeUtf8_2(int c) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int d = bArr[i];
        if ((d & 192) != 128) {
            _reportInvalidOther(d & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
        return ((c & 31) << 6) | (d & 63);
    }

    private final int _decodeUtf8_3(int c1) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        int c12 = c1 & 15;
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int d = bArr[i];
        if ((d & 192) != 128) {
            _reportInvalidOther(d & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
        int c = (c12 << 6) | (d & 63);
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int d2 = bArr2[i2];
        if ((d2 & 192) != 128) {
            _reportInvalidOther(d2 & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
        return (c << 6) | (d2 & 63);
    }

    private final int _decodeUtf8_3fast(int c1) throws IOException, JsonParseException {
        int c12 = c1 & 15;
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int d = bArr[i];
        if ((d & 192) != 128) {
            _reportInvalidOther(d & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
        int c = (c12 << 6) | (d & 63);
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int d2 = bArr2[i2];
        if ((d2 & 192) != 128) {
            _reportInvalidOther(d2 & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
        return (c << 6) | (d2 & 63);
    }

    private final int _decodeUtf8_4(int c) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int d = bArr[i];
        if ((d & 192) != 128) {
            _reportInvalidOther(d & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
        int c2 = ((c & 7) << 6) | (d & 63);
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int d2 = bArr2[i2];
        if ((d2 & 192) != 128) {
            _reportInvalidOther(d2 & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
        int c3 = (c2 << 6) | (d2 & 63);
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr3 = this._inputBuffer;
        int i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        int d3 = bArr3[i3];
        if ((d3 & 192) != 128) {
            _reportInvalidOther(d3 & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
        return ((c3 << 6) | (d3 & 63)) - 65536;
    }

    private final void _skipUtf8_2(int c) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int c2 = bArr[i];
        if ((c2 & 192) != 128) {
            _reportInvalidOther(c2 & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
    }

    private final void _skipUtf8_3(int c) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int c2 = bArr[i];
        if ((c2 & 192) != 128) {
            _reportInvalidOther(c2 & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int c3 = bArr2[i2];
        if ((c3 & 192) != 128) {
            _reportInvalidOther(c3 & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
    }

    private final void _skipUtf8_4(int c) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int d = bArr[i];
        if ((d & 192) != 128) {
            _reportInvalidOther(d & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        if ((d & 192) != 128) {
            _reportInvalidOther(d & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int d2 = bArr2[i2];
        if ((d2 & 192) != 128) {
            _reportInvalidOther(d2 & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE, this._inputPtr);
        }
    }

    protected final void _skipCR() throws IOException {
        if ((this._inputPtr < this._inputEnd || loadMore()) && this._inputBuffer[this._inputPtr] == 10) {
            this._inputPtr++;
        }
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    protected final void _skipLF() throws IOException {
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    private int nextByte() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return bArr[i] & Constants.UNKNOWN;
    }

    protected void _reportInvalidChar(int c) throws JsonParseException {
        if (c < 32) {
            _throwInvalidSpace(c);
        }
        _reportInvalidInitial(c);
    }

    protected void _reportInvalidInitial(int mask) throws JsonParseException {
        _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
    }

    protected void _reportInvalidOther(int mask) throws JsonParseException {
        _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
    }

    protected void _reportInvalidOther(int mask, int ptr) throws JsonParseException {
        this._inputPtr = ptr;
        _reportInvalidOther(mask);
    }

    public static int[] growArrayBy(int[] arr, int more) {
        if (arr == null) {
            return new int[more];
        }
        int len = arr.length;
        int[] arr2 = new int[len + more];
        System.arraycopy(arr, 0, arr2, 0, len);
        return arr2;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonParserBase
    protected byte[] _decodeBase64(Base64Variant b64variant) throws IOException, JsonParseException {
        ByteArrayBuilder builder = _getByteArrayBuilder();
        while (true) {
            if (this._inputPtr >= this._inputEnd) {
                loadMoreGuaranteed();
            }
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int ch = bArr[i] & Constants.UNKNOWN;
            if (ch > 32) {
                int bits = b64variant.decodeBase64Char(ch);
                if (bits < 0) {
                    if (ch == 34) {
                        return builder.toByteArray();
                    }
                    throw reportInvalidChar(b64variant, ch, 0);
                }
                if (this._inputPtr >= this._inputEnd) {
                    loadMoreGuaranteed();
                }
                byte[] bArr2 = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                int ch2 = bArr2[i2] & Constants.UNKNOWN;
                int bits2 = b64variant.decodeBase64Char(ch2);
                if (bits2 < 0) {
                    throw reportInvalidChar(b64variant, ch2, 1);
                }
                int decodedData = (bits << 6) | bits2;
                if (this._inputPtr >= this._inputEnd) {
                    loadMoreGuaranteed();
                }
                byte[] bArr3 = this._inputBuffer;
                int i3 = this._inputPtr;
                this._inputPtr = i3 + 1;
                int ch3 = bArr3[i3] & Constants.UNKNOWN;
                int bits3 = b64variant.decodeBase64Char(ch3);
                if (bits3 < 0) {
                    if (bits3 != -2) {
                        throw reportInvalidChar(b64variant, ch3, 2);
                    }
                    if (this._inputPtr >= this._inputEnd) {
                        loadMoreGuaranteed();
                    }
                    byte[] bArr4 = this._inputBuffer;
                    int i4 = this._inputPtr;
                    this._inputPtr = i4 + 1;
                    int ch4 = bArr4[i4] & Constants.UNKNOWN;
                    if (!b64variant.usesPaddingChar(ch4)) {
                        throw reportInvalidChar(b64variant, ch4, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                    }
                    builder.append(decodedData >> 4);
                } else {
                    int decodedData2 = (decodedData << 6) | bits3;
                    if (this._inputPtr >= this._inputEnd) {
                        loadMoreGuaranteed();
                    }
                    byte[] bArr5 = this._inputBuffer;
                    int i5 = this._inputPtr;
                    this._inputPtr = i5 + 1;
                    int ch5 = bArr5[i5] & Constants.UNKNOWN;
                    int bits4 = b64variant.decodeBase64Char(ch5);
                    if (bits4 < 0) {
                        if (bits4 != -2) {
                            throw reportInvalidChar(b64variant, ch5, 3);
                        }
                        builder.appendTwoBytes(decodedData2 >> 2);
                    } else {
                        builder.appendThreeBytes((decodedData2 << 6) | bits4);
                    }
                }
            }
        }
    }

    protected IllegalArgumentException reportInvalidChar(Base64Variant b64variant, int ch, int bindex) throws IllegalArgumentException {
        return reportInvalidChar(b64variant, ch, bindex, null);
    }

    protected IllegalArgumentException reportInvalidChar(Base64Variant b64variant, int ch, int bindex, String msg) throws IllegalArgumentException {
        String base;
        if (ch <= 32) {
            base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units";
        } else if (b64variant.usesPaddingChar(ch)) {
            base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
        } else if (!Character.isDefined(ch) || Character.isISOControl(ch)) {
            base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
        } else {
            base = "Illegal character '" + ((char) ch) + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
        }
        if (msg != null) {
            base = base + ": " + msg;
        }
        return new IllegalArgumentException(base);
    }
}
