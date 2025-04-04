package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

import com.openfeint.internal.vendor.org.codehaus.jackson.Base64Variant;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken;
import com.openfeint.internal.vendor.org.codehaus.jackson.ObjectCodec;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.IOContext;
import com.openfeint.internal.vendor.org.codehaus.jackson.sym.CharsToNameCanonicalizer;
import com.openfeint.internal.vendor.org.codehaus.jackson.util.ByteArrayBuilder;
import com.openfeint.internal.vendor.org.codehaus.jackson.util.CharTypes;
import com.openfeint.internal.vendor.org.codehaus.jackson.util.TextBuffer;
import java.io.IOException;
import java.io.Reader;

/* loaded from: classes.dex */
public final class ReaderBasedParser extends ReaderBasedNumericParser {
    protected ObjectCodec _objectCodec;
    protected final CharsToNameCanonicalizer _symbols;

    public ReaderBasedParser(IOContext ioCtxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st) {
        super(ioCtxt, features, r);
        this._objectCodec = codec;
        this._symbols = st;
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
            String name = _parseFieldName(i);
            this._parsingContext.setCurrentName(name);
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

    protected final String _parseFieldName(int i) throws IOException, JsonParseException {
        if (i != 34) {
            return _handleUnusualFieldName(i);
        }
        int ptr = this._inputPtr;
        int hash = 0;
        int inputLen = this._inputEnd;
        if (ptr < inputLen) {
            int[] codes = CharTypes.getInputCodeLatin1();
            int maxCode = codes.length;
            while (true) {
                char c = this._inputBuffer[ptr];
                if (c < maxCode && codes[c] != 0) {
                    if (c == '\"') {
                        int start = this._inputPtr;
                        this._inputPtr = ptr + 1;
                        return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
                    }
                } else {
                    hash = (hash * 31) + c;
                    ptr++;
                    if (ptr >= inputLen) {
                        break;
                    }
                }
            }
        }
        int start2 = this._inputPtr;
        this._inputPtr = ptr;
        return _parseFieldName2(start2, hash, 34);
    }

    private String _parseFieldName2(int startPtr, int hash, int endChar) throws IOException, JsonParseException {
        this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
        char[] outBuf = this._textBuffer.getCurrentSegment();
        int outPtr = this._textBuffer.getCurrentSegmentSize();
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(": was expecting closing '" + ((char) endChar) + "' for name");
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = cArr[i];
            if (c <= '\\') {
                if (c == '\\') {
                    c = _decodeEscaped();
                } else if (c <= endChar) {
                    if (c != endChar) {
                        if (c < ' ') {
                            _throwUnquotedSpace(c, "name");
                        }
                    } else {
                        this._textBuffer.setCurrentLength(outPtr);
                        TextBuffer tb = this._textBuffer;
                        char[] buf = tb.getTextBuffer();
                        int start = tb.getTextOffset();
                        int len = tb.size();
                        return this._symbols.findSymbol(buf, start, len, hash);
                    }
                }
            }
            hash = (hash * 31) + c;
            int outPtr2 = outPtr + 1;
            outBuf[outPtr] = c;
            if (outPtr2 >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            } else {
                outPtr = outPtr2;
            }
        }
    }

    protected final String _handleUnusualFieldName(int i) throws IOException, JsonParseException {
        boolean firstOk;
        if (i == 39 && isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            return _parseApostropheFieldName();
        }
        if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
            _reportUnexpectedChar(i, "was expecting double-quote to start field name");
        }
        int[] codes = CharTypes.getInputCodeLatin1JsNames();
        int maxCode = codes.length;
        if (i < maxCode) {
            firstOk = codes[i] == 0 && (i < 48 || i > 57);
        } else {
            firstOk = Character.isJavaIdentifierPart((char) i);
        }
        if (!firstOk) {
            _reportUnexpectedChar(i, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
        }
        int ptr = this._inputPtr;
        int hash = 0;
        int inputLen = this._inputEnd;
        if (ptr < inputLen) {
            do {
                char c = this._inputBuffer[ptr];
                if (c < maxCode) {
                    if (codes[c] != 0) {
                        int start = this._inputPtr - 1;
                        this._inputPtr = ptr;
                        return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
                    }
                } else if (!Character.isJavaIdentifierPart(c)) {
                    int start2 = this._inputPtr - 1;
                    this._inputPtr = ptr;
                    return this._symbols.findSymbol(this._inputBuffer, start2, ptr - start2, hash);
                }
                hash = (hash * 31) + c;
                ptr++;
            } while (ptr < inputLen);
        }
        int start3 = this._inputPtr - 1;
        this._inputPtr = ptr;
        return _parseUnusualFieldName2(start3, hash, codes);
    }

    protected final String _parseApostropheFieldName() throws IOException, JsonParseException {
        int ptr = this._inputPtr;
        int hash = 0;
        int inputLen = this._inputEnd;
        if (ptr < inputLen) {
            int[] codes = CharTypes.getInputCodeLatin1();
            int maxCode = codes.length;
            do {
                char c = this._inputBuffer[ptr];
                if (c == '\'') {
                    int start = this._inputPtr;
                    this._inputPtr = ptr + 1;
                    return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
                }
                if (c < maxCode && codes[c] != 0) {
                    break;
                }
                hash = (hash * 31) + c;
                ptr++;
            } while (ptr < inputLen);
        }
        int start2 = this._inputPtr;
        this._inputPtr = ptr;
        return _parseFieldName2(start2, hash, 39);
    }

    protected final JsonToken _handleUnexpectedValue(int i) throws IOException, JsonParseException {
        if (i != 39 || !isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            _reportUnexpectedChar(i, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
        }
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int outPtr = this._textBuffer.getCurrentSegmentSize();
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(": was expecting closing quote for a string value");
            }
            char[] cArr = this._inputBuffer;
            int i2 = this._inputPtr;
            this._inputPtr = i2 + 1;
            char c = cArr[i2];
            if (c <= '\\') {
                if (c == '\\') {
                    c = _decodeEscaped();
                } else if (c <= '\'') {
                    if (c != '\'') {
                        if (c < ' ') {
                            _throwUnquotedSpace(c, "string value");
                        }
                    } else {
                        this._textBuffer.setCurrentLength(outPtr);
                        return JsonToken.VALUE_STRING;
                    }
                }
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr] = c;
            outPtr++;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x006e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x005f A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.String _parseUnusualFieldName2(int r15, int r16, int[] r17) throws java.io.IOException, com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException {
        /*
            r14 = this;
            com.openfeint.internal.vendor.org.codehaus.jackson.util.TextBuffer r11 = r14._textBuffer
            char[] r12 = r14._inputBuffer
            int r13 = r14._inputPtr
            int r13 = r13 - r15
            r11.resetWithShared(r12, r15, r13)
            com.openfeint.internal.vendor.org.codehaus.jackson.util.TextBuffer r11 = r14._textBuffer
            char[] r6 = r11.getCurrentSegment()
            com.openfeint.internal.vendor.org.codehaus.jackson.util.TextBuffer r11 = r14._textBuffer
            int r7 = r11.getCurrentSegmentSize()
            r0 = r17
            int r5 = r0.length
        L19:
            int r11 = r14._inputPtr
            int r12 = r14._inputEnd
            if (r11 < r12) goto L41
            boolean r11 = r14.loadMore()
            if (r11 != 0) goto L41
        L25:
            com.openfeint.internal.vendor.org.codehaus.jackson.util.TextBuffer r11 = r14._textBuffer
            r11.setCurrentLength(r7)
            com.openfeint.internal.vendor.org.codehaus.jackson.util.TextBuffer r10 = r14._textBuffer
            char[] r1 = r10.getTextBuffer()
            int r9 = r10.getTextOffset()
            int r4 = r10.size()
            com.openfeint.internal.vendor.org.codehaus.jackson.sym.CharsToNameCanonicalizer r11 = r14._symbols
            r0 = r16
            java.lang.String r11 = r11.findSymbol(r1, r9, r4, r0)
            return r11
        L41:
            char[] r11 = r14._inputBuffer
            int r12 = r14._inputPtr
            char r2 = r11[r12]
            r3 = r2
            if (r3 > r5) goto L67
            r11 = r17[r3]
            if (r11 != 0) goto L25
        L4e:
            int r11 = r14._inputPtr
            int r11 = r11 + 1
            r14._inputPtr = r11
            int r11 = r16 * 31
            int r16 = r11 + r3
            int r8 = r7 + 1
            r6[r7] = r2
            int r11 = r6.length
            if (r8 < r11) goto L6e
            com.openfeint.internal.vendor.org.codehaus.jackson.util.TextBuffer r11 = r14._textBuffer
            char[] r6 = r11.finishCurrentSegment()
            r7 = 0
            goto L19
        L67:
            boolean r11 = java.lang.Character.isJavaIdentifierPart(r2)
            if (r11 != 0) goto L4e
            goto L25
        L6e:
            r7 = r8
            goto L19
        */
        throw new UnsupportedOperationException("Method not decompiled: com.openfeint.internal.vendor.org.codehaus.jackson.impl.ReaderBasedParser._parseUnusualFieldName2(int, int, int[]):java.lang.String");
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonParserBase
    protected void _finishString() throws IOException, JsonParseException {
        int ptr = this._inputPtr;
        int inputLen = this._inputEnd;
        if (ptr < inputLen) {
            int[] codes = CharTypes.getInputCodeLatin1();
            int maxCode = codes.length;
            while (true) {
                char c = this._inputBuffer[ptr];
                if (c < maxCode && codes[c] != 0) {
                    if (c == '\"') {
                        this._textBuffer.resetWithShared(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
                        this._inputPtr = ptr + 1;
                        return;
                    }
                } else {
                    ptr++;
                    if (ptr >= inputLen) {
                        break;
                    }
                }
            }
        }
        this._textBuffer.resetWithCopy(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
        this._inputPtr = ptr;
        _finishString2();
    }

    protected void _finishString2() throws IOException, JsonParseException {
        char[] outBuf = this._textBuffer.getCurrentSegment();
        int outPtr = this._textBuffer.getCurrentSegmentSize();
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(": was expecting closing quote for a string value");
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = cArr[i];
            if (c <= '\\') {
                if (c == '\\') {
                    c = _decodeEscaped();
                } else if (c <= '\"') {
                    if (c != '\"') {
                        if (c < ' ') {
                            _throwUnquotedSpace(c, "string value");
                        }
                    } else {
                        this._textBuffer.setCurrentLength(outPtr);
                        return;
                    }
                }
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr] = c;
            outPtr++;
        }
    }

    protected void _skipString() throws IOException, JsonParseException {
        this._tokenIncomplete = false;
        int inputPtr = this._inputPtr;
        int inputLen = this._inputEnd;
        char[] inputBuffer = this._inputBuffer;
        while (true) {
            if (inputPtr >= inputLen) {
                this._inputPtr = inputPtr;
                if (!loadMore()) {
                    _reportInvalidEOF(": was expecting closing quote for a string value");
                }
                inputPtr = this._inputPtr;
                inputLen = this._inputEnd;
            }
            int inputPtr2 = inputPtr + 1;
            char c = inputBuffer[inputPtr];
            if (c <= '\\') {
                if (c == '\\') {
                    this._inputPtr = inputPtr2;
                    _decodeEscaped();
                    inputPtr = this._inputPtr;
                    inputLen = this._inputEnd;
                } else if (c <= '\"') {
                    if (c != '\"') {
                        if (c < ' ') {
                            this._inputPtr = inputPtr2;
                            _throwUnquotedSpace(c, "string value");
                        }
                    } else {
                        this._inputPtr = inputPtr2;
                        return;
                    }
                }
            }
            inputPtr = inputPtr2;
        }
    }

    protected void _matchToken(JsonToken token) throws IOException, JsonParseException {
        String matchStr = token.asString();
        int len = matchStr.length();
        for (int i = 1; i < len; i++) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(" in a value");
            }
            char c = this._inputBuffer[this._inputPtr];
            if (c != matchStr.charAt(i)) {
                _reportInvalidToken(matchStr.substring(0, i));
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
            char c = this._inputBuffer[this._inputPtr];
            if (!Character.isJavaIdentifierPart(c)) {
                break;
            }
            this._inputPtr++;
            sb.append(c);
        }
        _reportError("Unrecognized token '" + sb.toString() + "': was expecting 'null', 'true' or 'false'");
    }

    protected final void _skipCR() throws IOException {
        if ((this._inputPtr < this._inputEnd || loadMore()) && this._inputBuffer[this._inputPtr] == '\n') {
            this._inputPtr++;
        }
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    protected final void _skipLF() throws IOException {
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    private final int _skipWS() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                char[] cArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                char c = cArr[i];
                if (c > ' ') {
                    if (c != '/') {
                        return c;
                    }
                    _skipComment();
                } else if (c != ' ') {
                    if (c == '\n') {
                        _skipLF();
                    } else if (c == '\r') {
                        _skipCR();
                    } else if (c != '\t') {
                        _throwInvalidSpace(c);
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
                char[] cArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                char c = cArr[i];
                if (c > ' ') {
                    if (c == '/') {
                        _skipComment();
                    } else {
                        return c;
                    }
                } else if (c != ' ') {
                    if (c == '\n') {
                        _skipLF();
                    } else if (c == '\r') {
                        _skipCR();
                    } else if (c != '\t') {
                        _throwInvalidSpace(c);
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
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        char c = cArr[i];
        if (c == '/') {
            _skipCppComment();
        } else if (c == '*') {
            _skipCComment();
        } else {
            _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x0028, code lost:
    
        _reportInvalidEOF(" in a comment");
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x002d, code lost:
    
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void _skipCComment() throws java.io.IOException, com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException {
        /*
            r5 = this;
            r4 = 42
        L2:
            int r1 = r5._inputPtr
            int r2 = r5._inputEnd
            if (r1 < r2) goto Le
            boolean r1 = r5.loadMore()
            if (r1 == 0) goto L28
        Le:
            char[] r1 = r5._inputBuffer
            int r2 = r5._inputPtr
            int r3 = r2 + 1
            r5._inputPtr = r3
            char r0 = r1[r2]
            if (r0 > r4) goto L2
            if (r0 != r4) goto L3f
            int r1 = r5._inputPtr
            int r2 = r5._inputEnd
            if (r1 < r2) goto L2e
            boolean r1 = r5.loadMore()
            if (r1 != 0) goto L2e
        L28:
            java.lang.String r1 = " in a comment"
            r5._reportInvalidEOF(r1)
        L2d:
            return
        L2e:
            char[] r1 = r5._inputBuffer
            int r2 = r5._inputPtr
            char r1 = r1[r2]
            r2 = 47
            if (r1 != r2) goto L2
            int r1 = r5._inputPtr
            int r1 = r1 + 1
            r5._inputPtr = r1
            goto L2d
        L3f:
            r1 = 32
            if (r0 >= r1) goto L2
            r1 = 10
            if (r0 != r1) goto L4b
            r5._skipLF()
            goto L2
        L4b:
            r1 = 13
            if (r0 != r1) goto L53
            r5._skipCR()
            goto L2
        L53:
            r1 = 9
            if (r0 == r1) goto L2
            r5._throwInvalidSpace(r0)
            goto L2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.openfeint.internal.vendor.org.codehaus.jackson.impl.ReaderBasedParser._skipCComment():void");
    }

    private final void _skipCppComment() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                char[] cArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                char c = cArr[i];
                if (c < ' ') {
                    if (c == '\n') {
                        _skipLF();
                        return;
                    } else if (c == '\r') {
                        _skipCR();
                        return;
                    } else if (c != '\t') {
                        _throwInvalidSpace(c);
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
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        char c = cArr[i];
        switch (c) {
            case '\"':
            case '/':
            case '\\':
                return c;
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'u':
                break;
            default:
                _reportError("Unrecognized character escape " + _getCharDesc(c));
                break;
        }
        int value = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(" in character escape sequence");
            }
            char[] cArr2 = this._inputBuffer;
            int i3 = this._inputPtr;
            this._inputPtr = i3 + 1;
            char c2 = cArr2[i3];
            int digit = CharTypes.charToHex(c2);
            if (digit < 0) {
                _reportUnexpectedChar(c2, "expected a hex-digit for character escape sequence");
            }
            value = (value << 4) | digit;
        }
        return (char) value;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonParserBase
    protected byte[] _decodeBase64(Base64Variant b64variant) throws IOException, JsonParseException {
        ByteArrayBuilder builder = _getByteArrayBuilder();
        while (true) {
            if (this._inputPtr >= this._inputEnd) {
                loadMoreGuaranteed();
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char ch = cArr[i];
            if (ch > ' ') {
                int bits = b64variant.decodeBase64Char(ch);
                if (bits < 0) {
                    if (ch == '\"') {
                        return builder.toByteArray();
                    }
                    throw reportInvalidChar(b64variant, ch, 0);
                }
                if (this._inputPtr >= this._inputEnd) {
                    loadMoreGuaranteed();
                }
                char[] cArr2 = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                char ch2 = cArr2[i2];
                int bits2 = b64variant.decodeBase64Char(ch2);
                if (bits2 < 0) {
                    throw reportInvalidChar(b64variant, ch2, 1);
                }
                int decodedData = (bits << 6) | bits2;
                if (this._inputPtr >= this._inputEnd) {
                    loadMoreGuaranteed();
                }
                char[] cArr3 = this._inputBuffer;
                int i3 = this._inputPtr;
                this._inputPtr = i3 + 1;
                char ch3 = cArr3[i3];
                int bits3 = b64variant.decodeBase64Char(ch3);
                if (bits3 < 0) {
                    if (bits3 != -2) {
                        throw reportInvalidChar(b64variant, ch3, 2);
                    }
                    if (this._inputPtr >= this._inputEnd) {
                        loadMoreGuaranteed();
                    }
                    char[] cArr4 = this._inputBuffer;
                    int i4 = this._inputPtr;
                    this._inputPtr = i4 + 1;
                    char ch4 = cArr4[i4];
                    if (!b64variant.usesPaddingChar(ch4)) {
                        throw reportInvalidChar(b64variant, ch4, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                    }
                    builder.append(decodedData >> 4);
                } else {
                    int decodedData2 = (decodedData << 6) | bits3;
                    if (this._inputPtr >= this._inputEnd) {
                        loadMoreGuaranteed();
                    }
                    char[] cArr5 = this._inputBuffer;
                    int i5 = this._inputPtr;
                    this._inputPtr = i5 + 1;
                    char ch5 = cArr5[i5];
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

    protected IllegalArgumentException reportInvalidChar(Base64Variant b64variant, char ch, int bindex) throws IllegalArgumentException {
        return reportInvalidChar(b64variant, ch, bindex, null);
    }

    protected IllegalArgumentException reportInvalidChar(Base64Variant b64variant, char ch, int bindex, String msg) throws IllegalArgumentException {
        String base;
        if (ch <= ' ') {
            base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units";
        } else if (b64variant.usesPaddingChar(ch)) {
            base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
        } else if (!Character.isDefined(ch) || Character.isISOControl(ch)) {
            base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
        } else {
            base = "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
        }
        if (msg != null) {
            base = base + ": " + msg;
        }
        return new IllegalArgumentException(base);
    }
}
