package com.openfeint.internal.vendor.org.codehaus.jackson;

import com.openfeint.internal.vendor.org.codehaus.jackson.type.JavaType;
import com.openfeint.internal.vendor.org.codehaus.jackson.type.TypeReference;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class ObjectCodec {
    public abstract JsonNode createArrayNode();

    public abstract JsonNode createObjectNode();

    public abstract JsonNode readTree(JsonParser jsonParser) throws IOException, JsonProcessingException;

    public abstract <T> T readValue(JsonParser jsonParser, JavaType javaType) throws IOException, JsonProcessingException;

    public abstract <T> T readValue(JsonParser jsonParser, TypeReference<?> typeReference) throws IOException, JsonProcessingException;

    public abstract <T> T readValue(JsonParser jsonParser, Class<T> cls) throws IOException, JsonProcessingException;

    public abstract JsonParser treeAsTokens(JsonNode jsonNode);

    public abstract <T> T treeToValue(JsonNode jsonNode, Class<T> cls) throws IOException, JsonProcessingException;

    public abstract void writeTree(JsonGenerator jsonGenerator, JsonNode jsonNode) throws IOException, JsonProcessingException;

    public abstract void writeValue(JsonGenerator jsonGenerator, Object obj) throws IOException, JsonProcessingException;

    protected ObjectCodec() {
    }
}
