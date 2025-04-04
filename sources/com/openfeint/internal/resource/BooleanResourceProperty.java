package com.openfeint.internal.resource;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerationException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class BooleanResourceProperty extends PrimitiveResourceProperty {
    public abstract boolean get(Resource resource);

    public abstract void set(Resource resource, boolean z);

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void copy(Resource lhs, Resource rhs) {
        set(lhs, get(rhs));
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void parse(Resource obj, JsonParser jp) throws JsonParseException, IOException {
        if (jp.getCurrentToken() == JsonToken.VALUE_TRUE || jp.getText().equalsIgnoreCase("true") || jp.getText().equalsIgnoreCase("1") || jp.getText().equalsIgnoreCase("YES")) {
            set(obj, true);
        } else {
            set(obj, false);
        }
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void generate(Resource obj, JsonGenerator generator, String key) throws JsonGenerationException, IOException {
        generator.writeFieldName(key);
        generator.writeBoolean(get(obj));
    }
}
