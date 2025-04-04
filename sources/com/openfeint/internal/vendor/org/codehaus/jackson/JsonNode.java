package com.openfeint.internal.vendor.org.codehaus.jackson;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public abstract class JsonNode implements Iterable<JsonNode> {
    static final List<JsonNode> NO_NODES = Collections.emptyList();
    static final List<String> NO_STRINGS = Collections.emptyList();

    public abstract JsonToken asToken();

    public abstract boolean equals(Object obj);

    public abstract JsonParser.NumberType getNumberType();

    public abstract String getValueAsText();

    public abstract JsonNode path(int i);

    public abstract JsonNode path(String str);

    public abstract String toString();

    public abstract JsonParser traverse();

    public abstract void writeTo(JsonGenerator jsonGenerator) throws IOException, JsonGenerationException;

    protected JsonNode() {
    }

    public boolean isValueNode() {
        return false;
    }

    public boolean isContainerNode() {
        return false;
    }

    public boolean isMissingNode() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isObject() {
        return false;
    }

    public boolean isPojo() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isIntegralNumber() {
        return false;
    }

    public boolean isFloatingPointNumber() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isLong() {
        return false;
    }

    public boolean isDouble() {
        return false;
    }

    public boolean isBigDecimal() {
        return false;
    }

    public boolean isBigInteger() {
        return false;
    }

    public boolean isTextual() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isBinary() {
        return false;
    }

    public String getTextValue() {
        return null;
    }

    public byte[] getBinaryValue() throws IOException {
        return null;
    }

    public boolean getBooleanValue() {
        return false;
    }

    public Number getNumberValue() {
        return null;
    }

    public int getIntValue() {
        return 0;
    }

    public long getLongValue() {
        return 0L;
    }

    public double getDoubleValue() {
        return 0.0d;
    }

    public BigDecimal getDecimalValue() {
        return BigDecimal.ZERO;
    }

    public BigInteger getBigIntegerValue() {
        return BigInteger.ZERO;
    }

    public JsonNode get(int index) {
        return null;
    }

    public JsonNode get(String fieldName) {
        return null;
    }

    @Deprecated
    public final JsonNode getFieldValue(String fieldName) {
        return get(fieldName);
    }

    @Deprecated
    public final JsonNode getElementValue(int index) {
        return get(index);
    }

    public int size() {
        return 0;
    }

    @Override // java.lang.Iterable
    public final Iterator<JsonNode> iterator() {
        return getElements();
    }

    public Iterator<JsonNode> getElements() {
        return NO_NODES.iterator();
    }

    public Iterator<String> getFieldNames() {
        return NO_STRINGS.iterator();
    }

    @Deprecated
    public final JsonNode getPath(String fieldName) {
        return path(fieldName);
    }

    @Deprecated
    public final JsonNode getPath(int index) {
        return path(index);
    }
}
