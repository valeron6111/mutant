package com.openfeint.internal.vendor.org.codehaus.jackson;

/* loaded from: classes.dex */
public class JsonGenerationException extends JsonProcessingException {
    static final long serialVersionUID = 123;

    public JsonGenerationException(Throwable rootCause) {
        super(rootCause);
    }

    public JsonGenerationException(String msg) {
        super(msg, (JsonLocation) null);
    }

    public JsonGenerationException(String msg, Throwable rootCause) {
        super(msg, (JsonLocation) null, rootCause);
    }
}
