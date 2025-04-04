package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerationException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonNode;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonProcessingException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken;
import com.openfeint.internal.vendor.org.codehaus.jackson.ObjectCodec;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes.dex */
public abstract class JsonGeneratorBase extends JsonGenerator {
    protected boolean _closed;
    protected int _features;
    protected ObjectCodec _objectCodec;
    protected JsonWriteContext _writeContext = JsonWriteContext.createRootContext();
    protected boolean _cfgNumbersAsStrings = isEnabled(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);

    protected abstract void _releaseBuffers();

    protected abstract void _verifyValueWrite(String str) throws IOException, JsonGenerationException;

    protected abstract void _writeEndArray() throws IOException, JsonGenerationException;

    protected abstract void _writeEndObject() throws IOException, JsonGenerationException;

    protected abstract void _writeFieldName(String str, boolean z) throws IOException, JsonGenerationException;

    protected abstract void _writeStartArray() throws IOException, JsonGenerationException;

    protected abstract void _writeStartObject() throws IOException, JsonGenerationException;

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public abstract void flush() throws IOException;

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public abstract void writeBoolean(boolean z) throws IOException, JsonGenerationException;

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public abstract void writeNull() throws IOException, JsonGenerationException;

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public abstract void writeNumber(double d) throws IOException, JsonGenerationException;

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public abstract void writeNumber(float f) throws IOException, JsonGenerationException;

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public abstract void writeNumber(int i) throws IOException, JsonGenerationException;

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public abstract void writeNumber(long j) throws IOException, JsonGenerationException;

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public abstract void writeNumber(BigDecimal bigDecimal) throws IOException, JsonGenerationException;

    protected JsonGeneratorBase(int features, ObjectCodec codec) {
        this._features = features;
        this._objectCodec = codec;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public JsonGenerator enable(JsonGenerator.Feature f) {
        this._features |= f.getMask();
        if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
            this._cfgNumbersAsStrings = true;
        }
        return this;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public JsonGenerator disable(JsonGenerator.Feature f) {
        this._features &= f.getMask() ^ (-1);
        if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
            this._cfgNumbersAsStrings = false;
        }
        return this;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final boolean isEnabled(JsonGenerator.Feature f) {
        return (this._features & f.getMask()) != 0;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final JsonGenerator useDefaultPrettyPrinter() {
        return setPrettyPrinter(new DefaultPrettyPrinter());
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final JsonGenerator setCodec(ObjectCodec oc) {
        this._objectCodec = oc;
        return this;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final ObjectCodec getCodec() {
        return this._objectCodec;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final JsonWriteContext getOutputContext() {
        return this._writeContext;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final void writeStartArray() throws IOException, JsonGenerationException {
        _verifyValueWrite("start an array");
        this._writeContext = this._writeContext.createChildArrayContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartArray(this);
        } else {
            _writeStartArray();
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final void writeEndArray() throws IOException, JsonGenerationException {
        if (!this._writeContext.inArray()) {
            _reportError("Current context not an ARRAY but " + this._writeContext.getTypeDesc());
        }
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeEndArray(this, this._writeContext.getEntryCount());
        } else {
            _writeEndArray();
        }
        this._writeContext = this._writeContext.getParent();
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final void writeStartObject() throws IOException, JsonGenerationException {
        _verifyValueWrite("start an object");
        this._writeContext = this._writeContext.createChildObjectContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartObject(this);
        } else {
            _writeStartObject();
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final void writeEndObject() throws IOException, JsonGenerationException {
        if (!this._writeContext.inObject()) {
            _reportError("Current context not an object but " + this._writeContext.getTypeDesc());
        }
        this._writeContext = this._writeContext.getParent();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeEndObject(this, this._writeContext.getEntryCount());
        } else {
            _writeEndObject();
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final void writeFieldName(String name) throws IOException, JsonGenerationException {
        int status = this._writeContext.writeFieldName(name);
        if (status == 4) {
            _reportError("Can not write a field name, expecting a value");
        }
        _writeFieldName(name, status == 1);
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeObject(Object value) throws IOException, JsonProcessingException {
        if (value == null) {
            writeNull();
        } else if (this._objectCodec != null) {
            this._objectCodec.writeValue(this, value);
        } else {
            _writeSimpleObject(value);
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public void writeTree(JsonNode rootNode) throws IOException, JsonProcessingException {
        if (rootNode == null) {
            writeNull();
        } else {
            if (this._objectCodec == null) {
                throw new IllegalStateException("No ObjectCodec defined for the generator, can not serialize JsonNode-based trees");
            }
            this._objectCodec.writeTree(this, rootNode);
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this._closed = true;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public boolean isClosed() {
        return this._closed;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final void copyCurrentEvent(JsonParser jp) throws IOException, JsonProcessingException {
        switch (C02781.f293xb4e83216[jp.getCurrentToken().ordinal()]) {
            case 1:
                writeStartObject();
                break;
            case 2:
                writeEndObject();
                break;
            case 3:
                writeStartArray();
                break;
            case 4:
                writeEndArray();
                break;
            case 5:
                writeFieldName(jp.getCurrentName());
                break;
            case 6:
                writeString(jp.getTextCharacters(), jp.getTextOffset(), jp.getTextLength());
                break;
            case MutantMessages.cShareWithFriends /* 7 */:
                switch (jp.getNumberType()) {
                    case INT:
                        writeNumber(jp.getIntValue());
                        break;
                    case BIG_INTEGER:
                        writeNumber(jp.getBigIntegerValue());
                        break;
                    default:
                        writeNumber(jp.getLongValue());
                        break;
                }
            case 8:
                switch (jp.getNumberType()) {
                    case BIG_DECIMAL:
                        writeNumber(jp.getDecimalValue());
                        break;
                    case FLOAT:
                        writeNumber(jp.getFloatValue());
                        break;
                    default:
                        writeNumber(jp.getDoubleValue());
                        break;
                }
            case MutantMessages.cProgress /* 9 */:
                writeBoolean(true);
                break;
            case 10:
                writeBoolean(false);
                break;
            case MutantMessages.cShareWithFriendsImmediate /* 11 */:
                writeNull();
                break;
            case MutantMessages.cRateApp /* 12 */:
                writeObject(jp.getEmbeddedObject());
                break;
            default:
                _cantHappen();
                break;
        }
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator
    public final void copyCurrentStructure(JsonParser jp) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.FIELD_NAME) {
            writeFieldName(jp.getCurrentName());
            t = jp.nextToken();
        }
        switch (t) {
            case START_OBJECT:
                writeStartObject();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    copyCurrentStructure(jp);
                }
                writeEndObject();
                break;
            case END_OBJECT:
            default:
                copyCurrentEvent(jp);
                break;
            case START_ARRAY:
                writeStartArray();
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    copyCurrentStructure(jp);
                }
                writeEndArray();
                break;
        }
    }

    protected void _reportError(String msg) throws JsonGenerationException {
        throw new JsonGenerationException(msg);
    }

    protected void _cantHappen() {
        throw new RuntimeException("Internal error: should never end up through this code path");
    }

    protected void _writeSimpleObject(Object value) throws IOException, JsonGenerationException {
        if (value == null) {
            writeNull();
            return;
        }
        if (value instanceof String) {
            writeString((String) value);
            return;
        }
        if (value instanceof Number) {
            Number n = (Number) value;
            if (n instanceof Integer) {
                writeNumber(n.intValue());
                return;
            }
            if (n instanceof Long) {
                writeNumber(n.longValue());
                return;
            }
            if (n instanceof Double) {
                writeNumber(n.doubleValue());
                return;
            }
            if (n instanceof Float) {
                writeNumber(n.floatValue());
                return;
            }
            if (n instanceof Short) {
                writeNumber((int) n.shortValue());
                return;
            }
            if (n instanceof Byte) {
                writeNumber((int) n.byteValue());
                return;
            }
            if (n instanceof BigInteger) {
                writeNumber((BigInteger) n);
                return;
            }
            if (n instanceof BigDecimal) {
                writeNumber((BigDecimal) n);
                return;
            } else if (n instanceof AtomicInteger) {
                writeNumber(((AtomicInteger) n).get());
                return;
            } else if (n instanceof AtomicLong) {
                writeNumber(((AtomicLong) n).get());
                return;
            }
        } else if (value instanceof byte[]) {
            writeBinary((byte[]) value);
            return;
        } else if (value instanceof Boolean) {
            writeBoolean(((Boolean) value).booleanValue());
            return;
        } else if (value instanceof AtomicBoolean) {
            writeBoolean(((AtomicBoolean) value).get());
            return;
        }
        throw new IllegalStateException("No ObjectCodec defined for the generator, can only serialize simple wrapper types (type passed " + value.getClass().getName() + ")");
    }
}
