package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

import com.alawar.mutant.database.DbBuilder;
import com.flurry.android.Constants;
import com.openfeint.internal.vendor.org.codehaus.jackson.Base64Variant;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerationException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonStreamContext;
import com.openfeint.internal.vendor.org.codehaus.jackson.ObjectCodec;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.IOContext;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.NumberOutput;
import com.openfeint.internal.vendor.org.codehaus.jackson.util.CharTypes;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: classes.dex */
public final class WriterBasedGenerator extends JsonGeneratorBase {
    static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    static final int SHORT_WRITE = 32;
    protected char[] _entityBuffer;
    protected final IOContext _ioContext;
    protected char[] _outputBuffer;
    protected int _outputEnd;
    protected int _outputHead;
    protected int _outputTail;
    protected final Writer _writer;

    public WriterBasedGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer w) {
        super(features, codec);
        this._outputHead = 0;
        this._outputTail = 0;
        this._ioContext = ctxt;
        this._writer = w;
        this._outputBuffer = ctxt.allocConcatBuffer();
        this._outputEnd = this._outputBuffer.length;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase
    protected void _writeStartArray() throws IOException, JsonGenerationException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '[';
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase
    protected void _writeEndArray() throws IOException, JsonGenerationException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = ']';
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase
    protected void _writeStartObject() throws IOException, JsonGenerationException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '{';
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase
    protected void _writeEndObject() throws IOException, JsonGenerationException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '}';
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase
    protected void _writeFieldName(String name, boolean commaBefore) throws IOException, JsonGenerationException {
        if (this._cfgPrettyPrinter != null) {
            _writePPFieldName(name, commaBefore);
            return;
        }
        if (this._outputTail + 1 >= this._outputEnd) {
            _flushBuffer();
        }
        if (commaBefore) {
            char[] cArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            cArr[i] = ',';
        }
        if (!isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
            _writeString(name);
            return;
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = '\"';
        _writeString(name);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr3 = this._outputBuffer;
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        cArr3[i3] = '\"';
    }

    protected final void _writePPFieldName(String name, boolean commaBefore) throws IOException, JsonGenerationException {
        if (commaBefore) {
            this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
        } else {
            this._cfgPrettyPrinter.beforeObjectEntries(this);
        }
        if (isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            char[] cArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            cArr[i] = '\"';
            _writeString(name);
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            char[] cArr2 = this._outputBuffer;
            int i2 = this._outputTail;
            this._outputTail = i2 + 1;
            cArr2[i2] = '\"';
            return;
        }
        _writeString(name);
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeString(String text) throws IOException, JsonGenerationException {
        _verifyValueWrite("write text value");
        if (text == null) {
            _writeNull();
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '\"';
        _writeString(text);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = '\"';
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeString(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        _verifyValueWrite("write text value");
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '\"';
        _writeString(text, offset, len);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = '\"';
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeRaw(String text) throws IOException, JsonGenerationException {
        int len = text.length();
        int room = this._outputEnd - this._outputTail;
        if (room == 0) {
            _flushBuffer();
            room = this._outputEnd - this._outputTail;
        }
        if (room >= len) {
            text.getChars(0, len, this._outputBuffer, this._outputTail);
            this._outputTail += len;
        } else {
            writeRawLong(text);
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeRaw(String text, int start, int len) throws IOException, JsonGenerationException {
        int room = this._outputEnd - this._outputTail;
        if (room < len) {
            _flushBuffer();
            room = this._outputEnd - this._outputTail;
        }
        if (room >= len) {
            text.getChars(start, start + len, this._outputBuffer, this._outputTail);
            this._outputTail += len;
        } else {
            writeRawLong(text.substring(start, start + len));
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeRaw(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        if (len < SHORT_WRITE) {
            int room = this._outputEnd - this._outputTail;
            if (len > room) {
                _flushBuffer();
            }
            System.arraycopy(text, offset, this._outputBuffer, this._outputTail, len);
            this._outputTail += len;
            return;
        }
        _flushBuffer();
        this._writer.write(text, offset, len);
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeRaw(char c) throws IOException, JsonGenerationException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = c;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeRawValue(String text) throws IOException, JsonGenerationException {
        _verifyValueWrite("write raw value");
        writeRaw(text);
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeRawValue(String text, int offset, int len) throws IOException, JsonGenerationException {
        _verifyValueWrite("write raw value");
        writeRaw(text, offset, len);
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeRawValue(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        _verifyValueWrite("write raw value");
        writeRaw(text, offset, len);
    }

    private void writeRawLong(String text) throws IOException, JsonGenerationException {
        int room = this._outputEnd - this._outputTail;
        text.getChars(0, room, this._outputBuffer, this._outputTail);
        this._outputTail += room;
        _flushBuffer();
        int offset = room;
        int len = text.length() - room;
        while (len > this._outputEnd) {
            int amount = this._outputEnd;
            text.getChars(offset, offset + amount, this._outputBuffer, 0);
            this._outputHead = 0;
            this._outputTail = amount;
            _flushBuffer();
            offset += amount;
            len -= amount;
        }
        text.getChars(offset, offset + len, this._outputBuffer, 0);
        this._outputHead = 0;
        this._outputTail = len;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
        _verifyValueWrite("write binary value");
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '\"';
        _writeBinary(b64variant, data, offset, offset + len);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = '\"';
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase, com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeNumber(int i) throws IOException, JsonGenerationException {
        _verifyValueWrite("write number");
        if (this._outputTail + 11 >= this._outputEnd) {
            _flushBuffer();
        }
        if (this._cfgNumbersAsStrings) {
            _writeQuotedInt(i);
        } else {
            this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
        }
    }

    private final void _writeQuotedInt(int i) throws IOException {
        if (this._outputTail + 13 >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr[i2] = '\"';
        this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
        char[] cArr2 = this._outputBuffer;
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        cArr2[i3] = '\"';
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase, com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeNumber(long l) throws IOException, JsonGenerationException {
        _verifyValueWrite("write number");
        if (this._cfgNumbersAsStrings) {
            _writeQuotedLong(l);
            return;
        }
        if (this._outputTail + 21 >= this._outputEnd) {
            _flushBuffer();
        }
        this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
    }

    private final void _writeQuotedLong(long l) throws IOException {
        if (this._outputTail + 23 >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '\"';
        this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = '\"';
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeNumber(BigInteger value) throws IOException, JsonGenerationException {
        _verifyValueWrite("write number");
        if (value == null) {
            _writeNull();
        } else if (this._cfgNumbersAsStrings) {
            _writeQuotedRaw(value);
        } else {
            writeRaw(value.toString());
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase, com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeNumber(double d) throws IOException, JsonGenerationException {
        if (this._cfgNumbersAsStrings || ((Double.isNaN(d) || Double.isInfinite(d)) && isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))) {
            writeString(String.valueOf(d));
        } else {
            _verifyValueWrite("write number");
            writeRaw(String.valueOf(d));
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase, com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeNumber(float f) throws IOException, JsonGenerationException {
        if (this._cfgNumbersAsStrings || ((Float.isNaN(f) || Float.isInfinite(f)) && isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))) {
            writeString(String.valueOf(f));
        } else {
            _verifyValueWrite("write number");
            writeRaw(String.valueOf(f));
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase, com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeNumber(BigDecimal value) throws IOException, JsonGenerationException {
        _verifyValueWrite("write number");
        if (value == null) {
            _writeNull();
        } else if (this._cfgNumbersAsStrings) {
            _writeQuotedRaw(value);
        } else {
            writeRaw(value.toString());
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeNumber(String encodedValue) throws IOException, JsonGenerationException {
        _verifyValueWrite("write number");
        if (this._cfgNumbersAsStrings) {
            _writeQuotedRaw(encodedValue);
        } else {
            writeRaw(encodedValue);
        }
    }

    private final void _writeQuotedRaw(Object value) throws IOException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '\"';
        writeRaw(value.toString());
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = '\"';
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase, com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeBoolean(boolean state) throws IOException, JsonGenerationException {
        int ptr;
        _verifyValueWrite("write boolean value");
        if (this._outputTail + 5 >= this._outputEnd) {
            _flushBuffer();
        }
        int ptr2 = this._outputTail;
        char[] buf = this._outputBuffer;
        if (state) {
            buf[ptr2] = 't';
            int ptr3 = ptr2 + 1;
            buf[ptr3] = 'r';
            int ptr4 = ptr3 + 1;
            buf[ptr4] = 'u';
            ptr = ptr4 + 1;
            buf[ptr] = 'e';
        } else {
            buf[ptr2] = 'f';
            int ptr5 = ptr2 + 1;
            buf[ptr5] = 'a';
            int ptr6 = ptr5 + 1;
            buf[ptr6] = 'l';
            int ptr7 = ptr6 + 1;
            buf[ptr7] = 's';
            ptr = ptr7 + 1;
            buf[ptr] = 'e';
        }
        this._outputTail = ptr + 1;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase, com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeNull() throws IOException, JsonGenerationException {
        _verifyValueWrite("write null value");
        _writeNull();
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase
    protected final void _verifyValueWrite(String typeMsg) throws IOException, JsonGenerationException {
        char c;
        int status = this._writeContext.writeValue();
        if (status == 5) {
            _reportError("Can not " + typeMsg + ", expecting field name");
        }
        if (this._cfgPrettyPrinter == null) {
            switch (status) {
                case 1:
                    c = ',';
                    break;
                case 2:
                    c = ':';
                    break;
                case 3:
                    c = ' ';
                    break;
                default:
                    return;
            }
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            this._outputBuffer[this._outputTail] = c;
            this._outputTail++;
            return;
        }
        _verifyPrettyValueWrite(typeMsg, status);
    }

    protected final void _verifyPrettyValueWrite(String typeMsg, int status) throws IOException, JsonGenerationException {
        switch (status) {
            case DbBuilder.ID_COLUMN /* 0 */:
                if (this._writeContext.inArray()) {
                    this._cfgPrettyPrinter.beforeArrayValues(this);
                    break;
                } else if (this._writeContext.inObject()) {
                    this._cfgPrettyPrinter.beforeObjectEntries(this);
                    break;
                }
                break;
            case 1:
                this._cfgPrettyPrinter.writeArrayValueSeparator(this);
                break;
            case 2:
                this._cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
                break;
            case 3:
                this._cfgPrettyPrinter.writeRootValueSeparator(this);
                break;
            default:
                _cantHappen();
                break;
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase, com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final void flush() throws IOException {
        _flushBuffer();
        this._writer.flush();
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase, com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
        if (this._outputBuffer != null && isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
            while (true) {
                JsonStreamContext ctxt = getOutputContext();
                if (ctxt.inArray()) {
                    writeEndArray();
                } else if (!ctxt.inObject()) {
                    break;
                } else {
                    writeEndObject();
                }
            }
        }
        _flushBuffer();
        if (this._ioContext.isResourceManaged() || isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
            this._writer.close();
        } else {
            this._writer.flush();
        }
        _releaseBuffers();
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.impl.JsonGeneratorBase
    protected void _releaseBuffers() {
        char[] buf = this._outputBuffer;
        if (buf != null) {
            this._outputBuffer = null;
            this._ioContext.releaseConcatBuffer(buf);
        }
    }

    private void _writeString(String text) throws IOException, JsonGenerationException {
        int i;
        int len = text.length();
        if (len > this._outputEnd) {
            _writeLongString(text);
            return;
        }
        if (this._outputTail + len > this._outputEnd) {
            _flushBuffer();
        }
        text.getChars(0, len, this._outputBuffer, this._outputTail);
        int end = this._outputTail + len;
        int[] escCodes = CharTypes.getOutputEscapes();
        int escLen = escCodes.length;
        while (this._outputTail < end) {
            do {
                char c = this._outputBuffer[this._outputTail];
                if (c >= escLen || escCodes[c] == 0) {
                    i = this._outputTail + 1;
                    this._outputTail = i;
                } else {
                    int flushLen = this._outputTail - this._outputHead;
                    if (flushLen > 0) {
                        this._writer.write(this._outputBuffer, this._outputHead, flushLen);
                    }
                    int escCode = escCodes[this._outputBuffer[this._outputTail]];
                    this._outputTail++;
                    int needLen = escCode < 0 ? 6 : 2;
                    if (needLen > this._outputTail) {
                        this._outputHead = this._outputTail;
                        _writeSingleEscape(escCode);
                    } else {
                        int ptr = this._outputTail - needLen;
                        this._outputHead = ptr;
                        _appendSingleEscape(escCode, this._outputBuffer, ptr);
                    }
                }
            } while (i < end);
            return;
        }
    }

    private void _writeLongString(String text) throws IOException, JsonGenerationException {
        _flushBuffer();
        int textLen = text.length();
        int offset = 0;
        do {
            int max = this._outputEnd;
            int segmentLen = offset + max > textLen ? textLen - offset : max;
            text.getChars(offset, offset + segmentLen, this._outputBuffer, 0);
            _writeSegment(segmentLen);
            offset += segmentLen;
        } while (offset < textLen);
    }

    private final void _writeSegment(int end) throws IOException, JsonGenerationException {
        int[] escCodes = CharTypes.getOutputEscapes();
        int escLen = escCodes.length;
        int ptr = 0;
        while (ptr < end) {
            int start = ptr;
            do {
                char c = this._outputBuffer[ptr];
                if (c < escLen && escCodes[c] != 0) {
                    break;
                } else {
                    ptr++;
                }
            } while (ptr < end);
            int flushLen = ptr - start;
            if (flushLen > 0) {
                this._writer.write(this._outputBuffer, start, flushLen);
                if (ptr >= end) {
                    return;
                }
            }
            int escCode = escCodes[this._outputBuffer[ptr]];
            ptr++;
            int needLen = escCode < 0 ? 6 : 2;
            if (needLen > this._outputTail) {
                _writeSingleEscape(escCode);
            } else {
                ptr -= needLen;
                _appendSingleEscape(escCode, this._outputBuffer, ptr);
            }
        }
    }

    private void _writeString(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        int len2 = len + offset;
        int[] escCodes = CharTypes.getOutputEscapes();
        int escLen = escCodes.length;
        while (offset < len2) {
            int start = offset;
            do {
                char c = text[offset];
                if (c < escLen && escCodes[c] != 0) {
                    break;
                } else {
                    offset++;
                }
            } while (offset < len2);
            int newAmount = offset - start;
            if (newAmount < SHORT_WRITE) {
                if (this._outputTail + newAmount > this._outputEnd) {
                    _flushBuffer();
                }
                if (newAmount > 0) {
                    System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
                    this._outputTail += newAmount;
                }
            } else {
                _flushBuffer();
                this._writer.write(text, start, newAmount);
            }
            if (offset < len2) {
                int escCode = escCodes[text[offset]];
                offset++;
                int needLen = escCode < 0 ? 6 : 2;
                if (this._outputTail + needLen > this._outputEnd) {
                    _flushBuffer();
                }
                _appendSingleEscape(escCode, this._outputBuffer, this._outputTail);
                this._outputTail += needLen;
            } else {
                return;
            }
        }
    }

    protected void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd) throws IOException, JsonGenerationException {
        int safeInputEnd = inputEnd - 3;
        int safeOutputEnd = this._outputEnd - 6;
        int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
        int inputPtr2 = inputPtr;
        while (inputPtr2 <= safeInputEnd) {
            if (this._outputTail > safeOutputEnd) {
                _flushBuffer();
            }
            int inputPtr3 = inputPtr2 + 1;
            int b24 = input[inputPtr2] << 8;
            int inputPtr4 = inputPtr3 + 1;
            int i = (b24 | (input[inputPtr3] & Constants.UNKNOWN)) << 8;
            int inputPtr5 = inputPtr4 + 1;
            this._outputTail = b64variant.encodeBase64Chunk(i | (input[inputPtr4] & Constants.UNKNOWN), this._outputBuffer, this._outputTail);
            chunksBeforeLF--;
            if (chunksBeforeLF <= 0) {
                char[] cArr = this._outputBuffer;
                int i2 = this._outputTail;
                this._outputTail = i2 + 1;
                cArr[i2] = '\\';
                char[] cArr2 = this._outputBuffer;
                int i3 = this._outputTail;
                this._outputTail = i3 + 1;
                cArr2[i3] = 'n';
                chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
            }
            inputPtr2 = inputPtr5;
        }
        int inputLeft = inputEnd - inputPtr2;
        if (inputLeft > 0) {
            if (this._outputTail > safeOutputEnd) {
                _flushBuffer();
            }
            int inputPtr6 = inputPtr2 + 1;
            int b242 = input[inputPtr2] << 16;
            if (inputLeft == 2) {
                int i4 = inputPtr6 + 1;
                b242 |= (input[inputPtr6] & Constants.UNKNOWN) << 8;
            }
            this._outputTail = b64variant.encodeBase64Partial(b242, inputLeft, this._outputBuffer, this._outputTail);
        }
    }

    private final void _writeNull() throws IOException {
        if (this._outputTail + 4 >= this._outputEnd) {
            _flushBuffer();
        }
        int ptr = this._outputTail;
        char[] buf = this._outputBuffer;
        buf[ptr] = 'n';
        int ptr2 = ptr + 1;
        buf[ptr2] = 'u';
        int ptr3 = ptr2 + 1;
        buf[ptr3] = 'l';
        int ptr4 = ptr3 + 1;
        buf[ptr4] = 'l';
        this._outputTail = ptr4 + 1;
    }

    private void _writeSingleEscape(int escCode) throws IOException {
        char[] buf = this._entityBuffer;
        if (buf == null) {
            buf = new char[]{'\\', 0, '0', '0', 0, 0};
        }
        if (escCode < 0) {
            int value = -(escCode + 1);
            buf[1] = 'u';
            buf[4] = HEX_CHARS[value >> 4];
            buf[5] = HEX_CHARS[value & 15];
            this._writer.write(buf, 0, 6);
            return;
        }
        buf[1] = (char) escCode;
        this._writer.write(buf, 0, 2);
    }

    private void _appendSingleEscape(int escCode, char[] buf, int ptr) {
        if (escCode < 0) {
            int value = -(escCode + 1);
            buf[ptr] = '\\';
            int ptr2 = ptr + 1;
            buf[ptr2] = 'u';
            int ptr3 = ptr2 + 1;
            buf[ptr3] = '0';
            int ptr4 = ptr3 + 1;
            buf[ptr4] = '0';
            int ptr5 = ptr4 + 1;
            buf[ptr5] = HEX_CHARS[value >> 4];
            buf[ptr5 + 1] = HEX_CHARS[value & 15];
            return;
        }
        buf[ptr] = '\\';
        buf[ptr + 1] = (char) escCode;
    }

    protected final void _flushBuffer() throws IOException {
        int len = this._outputTail - this._outputHead;
        if (len > 0) {
            int offset = this._outputHead;
            this._outputHead = 0;
            this._outputTail = 0;
            this._writer.write(this._outputBuffer, offset, len);
        }
    }
}
