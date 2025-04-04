package com.openfeint.internal.vendor.org.codehaus.jackson;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: classes.dex */
public abstract class JsonGenerator implements Closeable {
    protected PrettyPrinter _cfgPrettyPrinter;

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public abstract void close() throws IOException;

    public abstract void copyCurrentEvent(JsonParser jsonParser) throws IOException, JsonProcessingException;

    public abstract void copyCurrentStructure(JsonParser jsonParser) throws IOException, JsonProcessingException;

    public abstract JsonGenerator disable(Feature feature);

    public abstract JsonGenerator enable(Feature feature);

    public abstract void flush() throws IOException;

    public abstract ObjectCodec getCodec();

    public abstract JsonStreamContext getOutputContext();

    public abstract boolean isClosed();

    public abstract boolean isEnabled(Feature feature);

    public abstract JsonGenerator setCodec(ObjectCodec objectCodec);

    public abstract JsonGenerator useDefaultPrettyPrinter();

    public abstract void writeBinary(Base64Variant base64Variant, byte[] bArr, int i, int i2) throws IOException, JsonGenerationException;

    public abstract void writeBoolean(boolean z) throws IOException, JsonGenerationException;

    public abstract void writeEndArray() throws IOException, JsonGenerationException;

    public abstract void writeEndObject() throws IOException, JsonGenerationException;

    public abstract void writeFieldName(String str) throws IOException, JsonGenerationException;

    public abstract void writeNull() throws IOException, JsonGenerationException;

    public abstract void writeNumber(double d) throws IOException, JsonGenerationException;

    public abstract void writeNumber(float f) throws IOException, JsonGenerationException;

    public abstract void writeNumber(int i) throws IOException, JsonGenerationException;

    public abstract void writeNumber(long j) throws IOException, JsonGenerationException;

    public abstract void writeNumber(String str) throws IOException, JsonGenerationException, UnsupportedOperationException;

    public abstract void writeNumber(BigDecimal bigDecimal) throws IOException, JsonGenerationException;

    public abstract void writeNumber(BigInteger bigInteger) throws IOException, JsonGenerationException;

    public abstract void writeObject(Object obj) throws IOException, JsonProcessingException;

    public abstract void writeRaw(char c) throws IOException, JsonGenerationException;

    public abstract void writeRaw(String str) throws IOException, JsonGenerationException;

    public abstract void writeRaw(String str, int i, int i2) throws IOException, JsonGenerationException;

    public abstract void writeRaw(char[] cArr, int i, int i2) throws IOException, JsonGenerationException;

    public abstract void writeRawValue(String str) throws IOException, JsonGenerationException;

    public abstract void writeRawValue(String str, int i, int i2) throws IOException, JsonGenerationException;

    public abstract void writeRawValue(char[] cArr, int i, int i2) throws IOException, JsonGenerationException;

    public abstract void writeStartArray() throws IOException, JsonGenerationException;

    public abstract void writeStartObject() throws IOException, JsonGenerationException;

    public abstract void writeString(String str) throws IOException, JsonGenerationException;

    public abstract void writeString(char[] cArr, int i, int i2) throws IOException, JsonGenerationException;

    public abstract void writeTree(JsonNode jsonNode) throws IOException, JsonProcessingException;

    public enum Feature {
        AUTO_CLOSE_TARGET(true),
        AUTO_CLOSE_JSON_CONTENT(true),
        QUOTE_FIELD_NAMES(true),
        QUOTE_NON_NUMERIC_NUMBERS(true),
        WRITE_NUMBERS_AS_STRINGS(false);

        final boolean _defaultState;
        final int _mask = 1 << ordinal();

        public static int collectDefaults() {
            int flags = 0;
            Feature[] arr$ = values();
            for (Feature f : arr$) {
                if (f.enabledByDefault()) {
                    flags |= f.getMask();
                }
            }
            return flags;
        }

        Feature(boolean defaultState) {
            this._defaultState = defaultState;
        }

        public boolean enabledByDefault() {
            return this._defaultState;
        }

        public int getMask() {
            return this._mask;
        }
    }

    protected JsonGenerator() {
    }

    public JsonGenerator configure(Feature f, boolean state) {
        if (state) {
            enable(f);
        } else {
            disable(f);
        }
        return this;
    }

    public void enableFeature(Feature f) {
        enable(f);
    }

    public void disableFeature(Feature f) {
        disable(f);
    }

    public void setFeature(Feature f, boolean state) {
        configure(f, state);
    }

    public boolean isFeatureEnabled(Feature f) {
        return isEnabled(f);
    }

    public JsonGenerator setPrettyPrinter(PrettyPrinter pp) {
        this._cfgPrettyPrinter = pp;
        return this;
    }

    public void writeBinary(byte[] data, int offset, int len) throws IOException, JsonGenerationException {
        writeBinary(Base64Variants.getDefaultVariant(), data, offset, len);
    }

    public void writeBinary(byte[] data) throws IOException, JsonGenerationException {
        writeBinary(Base64Variants.getDefaultVariant(), data, 0, data.length);
    }

    public final void writeStringField(String fieldName, String value) throws IOException, JsonGenerationException {
        writeFieldName(fieldName);
        writeString(value);
    }

    public final void writeBooleanField(String fieldName, boolean value) throws IOException, JsonGenerationException {
        writeFieldName(fieldName);
        writeBoolean(value);
    }

    public final void writeNullField(String fieldName) throws IOException, JsonGenerationException {
        writeFieldName(fieldName);
        writeNull();
    }

    public final void writeNumberField(String fieldName, int value) throws IOException, JsonGenerationException {
        writeFieldName(fieldName);
        writeNumber(value);
    }

    public final void writeNumberField(String fieldName, long value) throws IOException, JsonGenerationException {
        writeFieldName(fieldName);
        writeNumber(value);
    }

    public final void writeNumberField(String fieldName, double value) throws IOException, JsonGenerationException {
        writeFieldName(fieldName);
        writeNumber(value);
    }

    public final void writeNumberField(String fieldName, float value) throws IOException, JsonGenerationException {
        writeFieldName(fieldName);
        writeNumber(value);
    }

    public final void writeNumberField(String fieldName, BigDecimal value) throws IOException, JsonGenerationException {
        writeFieldName(fieldName);
        writeNumber(value);
    }

    public final void writeBinaryField(String fieldName, byte[] data) throws IOException, JsonGenerationException {
        writeFieldName(fieldName);
        writeBinary(data);
    }

    public final void writeArrayFieldStart(String fieldName) throws IOException, JsonGenerationException {
        writeFieldName(fieldName);
        writeStartArray();
    }

    public final void writeObjectFieldStart(String fieldName) throws IOException, JsonGenerationException {
        writeFieldName(fieldName);
        writeStartObject();
    }

    public final void writeObjectField(String fieldName, Object pojo) throws IOException, JsonProcessingException {
        writeFieldName(fieldName);
        writeObject(pojo);
    }
}
