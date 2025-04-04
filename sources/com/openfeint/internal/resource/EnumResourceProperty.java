package com.openfeint.internal.resource;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerationException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import java.io.IOException;
import java.lang.Enum;

/* loaded from: classes.dex */
public abstract class EnumResourceProperty<T extends Enum<T>> extends PrimitiveResourceProperty {
    Class<T> mEnumClass;

    public abstract T get(Resource resource);

    public abstract void set(Resource resource, T t);

    public EnumResourceProperty(Class<T> enumClass) {
        this.mEnumClass = enumClass;
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void copy(Resource lhs, Resource rhs) {
        set(lhs, get(rhs));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void parse(Resource obj, JsonParser jp) throws JsonParseException, IOException {
        set(obj, Enum.valueOf(this.mEnumClass, jp.getText()));
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void generate(Resource obj, JsonGenerator generator, String key) throws JsonGenerationException, IOException {
        T val = get(obj);
        generator.writeFieldName(key);
        generator.writeString(val.toString());
    }
}
