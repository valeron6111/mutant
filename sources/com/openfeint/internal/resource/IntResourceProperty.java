package com.openfeint.internal.resource;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerationException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class IntResourceProperty extends PrimitiveResourceProperty {
    public abstract int get(Resource resource);

    public abstract void set(Resource resource, int i);

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void copy(Resource lhs, Resource rhs) {
        set(lhs, get(rhs));
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void parse(Resource obj, JsonParser jp) throws JsonParseException, IOException {
        int value;
        try {
            value = jp.getIntValue();
        } catch (Exception e) {
            value = 0;
        }
        set(obj, value);
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void generate(Resource obj, JsonGenerator generator, String key) throws JsonGenerationException, IOException {
        generator.writeFieldName(key);
        generator.writeNumber(get(obj));
    }
}
