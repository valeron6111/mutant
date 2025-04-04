package com.openfeint.internal.resource;

import com.openfeint.api.resource.Achievement;
import com.openfeint.api.resource.CurrentUser;
import com.openfeint.api.resource.Leaderboard;
import com.openfeint.api.resource.Score;
import com.openfeint.api.resource.ServerTimestamp;
import com.openfeint.api.resource.User;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonEncoding;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonFactory;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerationException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class Resource {
    private static final String TAG = "Resource";
    String mID;
    private static HashMap<Class<? extends Resource>, ResourceClass> sSubclassMap = new HashMap<>();
    private static HashMap<String, ResourceClass> sNameMap = new HashMap<>();

    public String resourceID() {
        return this.mID;
    }

    public void setResourceID(String id) {
        this.mID = id;
    }

    static {
        registerSubclass(getResourceClass());
        registerSubclass(ServerException.getResourceClass());
        registerSubclass(Device.getResourceClass());
        registerSubclass(ParentalControl.getResourceClass());
        registerSubclass(ServerTimestamp.getResourceClass());
        registerSubclass(BlobUploadParameters.getResourceClass());
        registerSubclass(Achievement.getResourceClass());
        registerSubclass(Leaderboard.getResourceClass());
        registerSubclass(Score.getResourceClass());
        registerSubclass(User.getResourceClass());
        registerSubclass(CurrentUser.getResourceClass());
    }

    public static void registerSubclass(ResourceClass klass) {
        sSubclassMap.put(klass.mObjectClass, klass);
        if (klass.mResourceName != null) {
            sNameMap.put(klass.mResourceName, klass);
        }
    }

    public static ResourceClass getKlass(Class<?> subclass) {
        return sSubclassMap.get(subclass);
    }

    public static ResourceClass getKlass(String klassName) {
        return sNameMap.get(klassName);
    }

    public static ResourceClass getResourceClass() {
        ResourceClass klass = new ResourceClass(Resource.class, null) { // from class: com.openfeint.internal.resource.Resource.1
            @Override // com.openfeint.internal.resource.ResourceClass
            public Resource factory() {
                return null;
            }
        };
        klass.mProperties.put("id", new StringResourceProperty() { // from class: com.openfeint.internal.resource.Resource.2
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return obj.mID;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                obj.mID = val;
            }
        });
        return klass;
    }

    public final void generateToStream(OutputStream out) throws IOException {
        JsonFactory jf = new JsonFactory();
        JsonGenerator jg = jf.createJsonGenerator(out, JsonEncoding.UTF8);
        generate(jg);
        jg.close();
    }

    public final String generate() {
        StringWriter sw = new StringWriter();
        JsonFactory jf = new JsonFactory();
        try {
            JsonGenerator jg = jf.createJsonGenerator(sw);
            generate(jg);
            jg.close();
            return sw.toString();
        } catch (IOException e) {
            OFLog.m182e(TAG, e.getMessage());
            return null;
        }
    }

    public final void generate(JsonGenerator generator) throws JsonGenerationException, IOException {
        HashMap<String, Integer> r;
        ResourceClass rc = getKlass(getClass());
        generator.writeStartObject();
        generator.writeFieldName(rc.mResourceName);
        generator.writeStartObject();
        for (Map.Entry<String, ResourceProperty> e : rc.mProperties.entrySet()) {
            ResourceProperty rp = e.getValue();
            if (rp instanceof PrimitiveResourceProperty) {
                ((PrimitiveResourceProperty) rp).generate(this, generator, e.getKey());
            } else if (rp instanceof ArrayResourceProperty) {
                ArrayResourceProperty arp = (ArrayResourceProperty) rp;
                List<? extends Resource> resources = arp.get(this);
                if (resources != null) {
                    generator.writeFieldName(e.getKey());
                    ResourceClass erc = getKlass(arp.elementType());
                    generator.writeStartObject();
                    generator.writeFieldName(erc.mResourceName + "s");
                    generator.writeStartArray();
                    Iterator i$ = resources.iterator();
                    while (i$.hasNext()) {
                        i$.next().generate(generator);
                    }
                    generator.writeEndArray();
                    generator.writeEndObject();
                }
            } else if (rp instanceof NestedResourceProperty) {
                Resource r2 = ((NestedResourceProperty) rp).get(this);
                if (r2 != null) {
                    generator.writeFieldName(e.getKey());
                    r2.generate(generator);
                }
            } else if ((rp instanceof HashIntResourceProperty) && (r = ((HashIntResourceProperty) rp).get(this)) != null && r.size() > 0) {
                generator.writeFieldName(e.getKey());
                generator.writeStartObject();
                for (Map.Entry<String, Integer> entry : r.entrySet()) {
                    generator.writeFieldName(entry.getKey());
                    generator.writeNumber(entry.getValue().intValue());
                }
                generator.writeEndObject();
            }
        }
        generator.writeEndObject();
        generator.writeEndObject();
    }

    public final void shallowCopy(Resource otherResource) {
        if (otherResource.getClass() != getClass()) {
            throw new UnsupportedOperationException("You can only shallowCopy the same type of resource");
        }
        unguardedShallowCopy(otherResource);
    }

    public final void shallowCopyAncestorType(Resource otherResource) {
        Class<?> c = getClass();
        Class<?> o = otherResource.getClass();
        if (o != Resource.class) {
            while (c != o && c != Resource.class) {
                c = c.getSuperclass();
            }
            if (c == Resource.class) {
                throw new UnsupportedOperationException(o.getName() + " is not a superclass of " + getClass().getName());
            }
        }
        unguardedShallowCopy(otherResource);
    }

    private final void unguardedShallowCopy(Resource otherResource) {
        ResourceClass rc = getKlass(otherResource.getClass());
        for (Map.Entry<String, ResourceProperty> e : rc.mProperties.entrySet()) {
            ResourceProperty rp = e.getValue();
            if (rp instanceof PrimitiveResourceProperty) {
                ((PrimitiveResourceProperty) rp).copy(this, otherResource);
            } else if (rp instanceof ArrayResourceProperty) {
                ((ArrayResourceProperty) rp).set(this, ((ArrayResourceProperty) rp).get(otherResource));
            } else if (rp instanceof NestedResourceProperty) {
                ((NestedResourceProperty) rp).set(this, ((NestedResourceProperty) rp).get(otherResource));
            }
        }
    }
}
