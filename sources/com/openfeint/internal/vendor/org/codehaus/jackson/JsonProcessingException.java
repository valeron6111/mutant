package com.openfeint.internal.vendor.org.codehaus.jackson;

import java.io.IOException;

/* loaded from: classes.dex */
public class JsonProcessingException extends IOException {
    static final long serialVersionUID = 123;
    protected JsonLocation mLocation;

    protected JsonProcessingException(String msg, JsonLocation loc, Throwable rootCause) {
        super(msg);
        if (rootCause != null) {
            initCause(rootCause);
        }
        this.mLocation = loc;
    }

    protected JsonProcessingException(String msg) {
        super(msg);
    }

    protected JsonProcessingException(String msg, JsonLocation loc) {
        this(msg, loc, null);
    }

    protected JsonProcessingException(String msg, Throwable rootCause) {
        this(msg, null, rootCause);
    }

    protected JsonProcessingException(Throwable rootCause) {
        this(null, null, rootCause);
    }

    public JsonLocation getLocation() {
        return this.mLocation;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        String msg = super.getMessage();
        if (msg == null) {
            msg = "N/A";
        }
        JsonLocation loc = getLocation();
        if (loc != null) {
            return msg + "\n at " + loc.toString();
        }
        return msg;
    }

    @Override // java.lang.Throwable
    public String toString() {
        return getClass().getName() + ": " + getMessage();
    }
}
