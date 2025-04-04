package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerationException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import java.io.IOException;

/* loaded from: classes.dex */
public interface Indenter {
    boolean isInline();

    void writeIndentation(JsonGenerator jsonGenerator, int i) throws IOException, JsonGenerationException;
}
