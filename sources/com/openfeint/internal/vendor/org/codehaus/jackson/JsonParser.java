package com.openfeint.internal.vendor.org.codehaus.jackson;

import com.openfeint.internal.vendor.org.codehaus.jackson.type.TypeReference;
import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: classes.dex */
public abstract class JsonParser implements Closeable {
    private static final int MAX_BYTE_I = 127;
    private static final int MAX_SHORT_I = 32767;
    private static final int MIN_BYTE_I = -128;
    private static final int MIN_SHORT_I = -32768;
    protected JsonToken _currToken;
    protected int _features;
    protected JsonToken _lastClearedToken;

    public enum NumberType {
        INT,
        LONG,
        BIG_INTEGER,
        FLOAT,
        DOUBLE,
        BIG_DECIMAL
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public abstract void close() throws IOException;

    public abstract BigInteger getBigIntegerValue() throws IOException, JsonParseException;

    public abstract byte[] getBinaryValue(Base64Variant base64Variant) throws IOException, JsonParseException;

    public abstract ObjectCodec getCodec();

    public abstract JsonLocation getCurrentLocation();

    public abstract String getCurrentName() throws IOException, JsonParseException;

    public abstract BigDecimal getDecimalValue() throws IOException, JsonParseException;

    public abstract double getDoubleValue() throws IOException, JsonParseException;

    public abstract float getFloatValue() throws IOException, JsonParseException;

    public abstract int getIntValue() throws IOException, JsonParseException;

    public abstract long getLongValue() throws IOException, JsonParseException;

    public abstract NumberType getNumberType() throws IOException, JsonParseException;

    public abstract Number getNumberValue() throws IOException, JsonParseException;

    public abstract JsonStreamContext getParsingContext();

    public abstract String getText() throws IOException, JsonParseException;

    public abstract char[] getTextCharacters() throws IOException, JsonParseException;

    public abstract int getTextLength() throws IOException, JsonParseException;

    public abstract int getTextOffset() throws IOException, JsonParseException;

    public abstract JsonLocation getTokenLocation();

    public abstract boolean isClosed();

    public abstract JsonToken nextToken() throws IOException, JsonParseException;

    public abstract void setCodec(ObjectCodec objectCodec);

    public abstract JsonParser skipChildren() throws IOException, JsonParseException;

    public enum Feature {
        AUTO_CLOSE_SOURCE(true),
        ALLOW_COMMENTS(false),
        ALLOW_UNQUOTED_FIELD_NAMES(false),
        ALLOW_SINGLE_QUOTES(false),
        ALLOW_UNQUOTED_CONTROL_CHARS(false),
        INTERN_FIELD_NAMES(true),
        CANONICALIZE_FIELD_NAMES(true);

        final boolean _defaultState;

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

        public boolean enabledIn(int flags) {
            return (getMask() & flags) != 0;
        }

        public int getMask() {
            return 1 << ordinal();
        }
    }

    protected JsonParser() {
    }

    public JsonParser enable(Feature f) {
        this._features |= f.getMask();
        return this;
    }

    public JsonParser disable(Feature f) {
        this._features &= f.getMask() ^ (-1);
        return this;
    }

    public JsonParser configure(Feature f, boolean state) {
        if (state) {
            enableFeature(f);
        } else {
            disableFeature(f);
        }
        return this;
    }

    public boolean isEnabled(Feature f) {
        return (this._features & f.getMask()) != 0;
    }

    public void setFeature(Feature f, boolean state) {
        configure(f, state);
    }

    public void enableFeature(Feature f) {
        enable(f);
    }

    public void disableFeature(Feature f) {
        disable(f);
    }

    public final boolean isFeatureEnabled(Feature f) {
        return isEnabled(f);
    }

    public JsonToken nextValue() throws IOException, JsonParseException {
        JsonToken t = nextToken();
        if (t == JsonToken.FIELD_NAME) {
            return nextToken();
        }
        return t;
    }

    public JsonToken getCurrentToken() {
        return this._currToken;
    }

    public boolean hasCurrentToken() {
        return this._currToken != null;
    }

    public void clearCurrentToken() {
        if (this._currToken != null) {
            this._lastClearedToken = this._currToken;
            this._currToken = null;
        }
    }

    public JsonToken getLastClearedToken() {
        return this._lastClearedToken;
    }

    public byte getByteValue() throws IOException, JsonParseException {
        int value = getIntValue();
        if (value < MIN_BYTE_I || value > MAX_BYTE_I) {
            throw _constructError("Numeric value (" + getText() + ") out of range of Java byte");
        }
        return (byte) value;
    }

    public short getShortValue() throws IOException, JsonParseException {
        int value = getIntValue();
        if (value < MIN_SHORT_I || value > MAX_SHORT_I) {
            throw _constructError("Numeric value (" + getText() + ") out of range of Java short");
        }
        return (short) value;
    }

    public boolean getBooleanValue() throws IOException, JsonParseException {
        if (this._currToken == JsonToken.VALUE_TRUE) {
            return true;
        }
        if (this._currToken == JsonToken.VALUE_FALSE) {
            return false;
        }
        throw new JsonParseException("Current token (" + this._currToken + ") not of boolean type", getCurrentLocation());
    }

    public Object getEmbeddedObject() throws IOException, JsonParseException {
        return null;
    }

    public byte[] getBinaryValue() throws IOException, JsonParseException {
        return getBinaryValue(Base64Variants.getDefaultVariant());
    }

    public <T> T readValueAs(Class<T> cls) throws IOException, JsonProcessingException {
        ObjectCodec codec = getCodec();
        if (codec == null) {
            throw new IllegalStateException("No ObjectCodec defined for the parser, can not deserialize JSON into Java objects");
        }
        return (T) codec.readValue(this, cls);
    }

    public <T> T readValueAs(TypeReference<?> typeReference) throws IOException, JsonProcessingException {
        ObjectCodec codec = getCodec();
        if (codec == null) {
            throw new IllegalStateException("No ObjectCodec defined for the parser, can not deserialize JSON into Java objects");
        }
        return (T) codec.readValue(this, typeReference);
    }

    public JsonNode readValueAsTree() throws IOException, JsonProcessingException {
        ObjectCodec codec = getCodec();
        if (codec == null) {
            throw new IllegalStateException("No ObjectCodec defined for the parser, can not deserialize JSON into JsonNode tree");
        }
        return codec.readTree(this);
    }

    protected JsonParseException _constructError(String msg) {
        return new JsonParseException(msg, getCurrentLocation());
    }
}
