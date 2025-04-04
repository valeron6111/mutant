package com.openfeint.internal.resource;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerationException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class LongResourceProperty extends PrimitiveResourceProperty {
    public abstract long get(Resource resource);

    public abstract void set(Resource resource, long j);

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void copy(Resource lhs, Resource rhs) {
        set(lhs, get(rhs));
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void parse(Resource obj, JsonParser jp) throws JsonParseException, IOException {
        set(obj, jp.getLongValue());
    }

    public void set(Resource obj, String val) {
        set(obj, Long.valueOf(val).longValue());
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void generate(Resource obj, JsonGenerator generator, String key) throws JsonGenerationException, IOException {
        generator.writeFieldName(key);
        generator.writeNumber(get(obj));
    }
}
