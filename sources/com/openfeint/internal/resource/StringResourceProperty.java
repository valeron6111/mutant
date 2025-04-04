package com.openfeint.internal.resource;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerationException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class StringResourceProperty extends PrimitiveResourceProperty {
    public abstract String get(Resource resource);

    public abstract void set(Resource resource, String str);

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void copy(Resource lhs, Resource rhs) {
        set(lhs, get(rhs));
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void parse(Resource obj, JsonParser jp) throws JsonParseException, IOException {
        if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
            set(obj, null);
        } else {
            set(obj, jp.getText());
        }
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void generate(Resource obj, JsonGenerator generator, String key) throws JsonGenerationException, IOException {
        String o = get(obj);
        if (o != null) {
            generator.writeFieldName(key);
            generator.writeString(o);
        }
    }
}
