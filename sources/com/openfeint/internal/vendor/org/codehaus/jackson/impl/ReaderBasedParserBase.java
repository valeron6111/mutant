package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.IOContext;
import java.io.IOException;
import java.io.Reader;

/* loaded from: classes.dex */
public abstract class ReaderBasedParserBase extends JsonNumericParserBase {
    protected char[] _inputBuffer;
    protected Reader _reader;

    protected ReaderBasedParserBase(IOContext ctxt, int features, Reader r) {
        super(ctxt, features);
        this._reader = r;
        this._inputBuffer = ctxt.allocTokenBuffer();
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonParserBase
    protected final boolean loadMore() throws IOException {
        this._currInputProcessed += this._inputEnd;
        this._currInputRowStart -= this._inputEnd;
        if (this._reader == null) {
            return false;
        }
        int count = this._reader.read(this._inputBuffer, 0, this._inputBuffer.length);
        if (count > 0) {
            this._inputPtr = 0;
            this._inputEnd = count;
            return true;
        }
        _closeInput();
        if (count == 0) {
            throw new IOException("Reader returned 0 characters when trying to read " + this._inputEnd);
        }
        return false;
    }

    protected char getNextChar(String eofMsg) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(eofMsg);
        }
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return cArr[i];
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonParserBase
    protected void _closeInput() throws IOException {
        if (this._reader != null) {
            if (this._ioContext.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
                this._reader.close();
            }
            this._reader = null;
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonParserBase
    protected void _releaseBuffers() throws IOException {
        super._releaseBuffers();
        char[] buf = this._inputBuffer;
        if (buf != null) {
            this._inputBuffer = null;
            this._ioContext.releaseTokenBuffer(buf);
        }
    }
}
