package com.openfeint.internal.resource;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerationException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: classes.dex */
public abstract class DateResourceProperty extends PrimitiveResourceProperty {
    public static DateFormat sDateParser = makeDateParser();

    public abstract Date get(Resource resource);

    public abstract void set(Resource resource, Date date);

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void copy(Resource lhs, Resource rhs) {
        set(lhs, get(rhs));
    }

    static DateFormat makeDateParser() {
        DateFormat p = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        p.setTimeZone(TimeZone.getTimeZone("UTC"));
        return p;
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void parse(Resource obj, JsonParser jp) throws JsonParseException, IOException {
        String text = jp.getText();
        if (text.equals("null")) {
            set(obj, null);
            return;
        }
        try {
            set(obj, sDateParser.parse(text));
        } catch (ParseException e) {
            set(obj, null);
        }
    }

    @Override // com.openfeint.internal.resource.PrimitiveResourceProperty
    public void generate(Resource obj, JsonGenerator generator, String key) throws JsonGenerationException, IOException {
        Date o = get(obj);
        if (o != null) {
            generator.writeFieldName(key);
            generator.writeString(sDateParser.format(o));
        }
    }
}
