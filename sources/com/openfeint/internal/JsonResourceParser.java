package com.openfeint.internal;

import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.resource.ArrayResourceProperty;
import com.openfeint.internal.resource.HashIntResourceProperty;
import com.openfeint.internal.resource.NestedResourceProperty;
import com.openfeint.internal.resource.PrimitiveResourceProperty;
import com.openfeint.internal.resource.Resource;
import com.openfeint.internal.resource.ResourceClass;
import com.openfeint.internal.resource.ResourceProperty;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes.dex */
public class JsonResourceParser {
    private static final String TAG = "JsonResourceParser";
    private JsonParser mParser;

    public JsonResourceParser(JsonParser parser) {
        this.mParser = parser;
    }

    public Object parse() throws JsonParseException, IOException {
        Object rv;
        JsonToken firstTok = this.mParser.nextToken();
        if (firstTok == null) {
            return null;
        }
        if (firstTok != JsonToken.START_OBJECT) {
            throw new JsonParseException("Couldn't find toplevel wrapper object.", this.mParser.getTokenLocation());
        }
        if (this.mParser.nextToken() != JsonToken.FIELD_NAME) {
            throw new JsonParseException("Couldn't find toplevel wrapper object.", this.mParser.getTokenLocation());
        }
        String classOrArrayName = this.mParser.getText();
        JsonToken tok = this.mParser.nextToken();
        if (tok == JsonToken.START_ARRAY) {
            rv = parseOpenedArray();
        } else if (tok == JsonToken.START_OBJECT) {
            ResourceClass klass = Resource.getKlass(classOrArrayName);
            if (klass == null) {
                throw new JsonParseException("Unknown toplevel class '" + classOrArrayName + "'.", this.mParser.getTokenLocation());
            }
            rv = parseOpenedResource(klass);
        } else {
            throw new JsonParseException("Expected object or array at top level.", this.mParser.getTokenLocation());
        }
        if (this.mParser.nextToken() != JsonToken.END_OBJECT) {
            throw new JsonParseException("Expected only one k/v in toplevel wrapper object.", this.mParser.getTokenLocation());
        }
        return rv;
    }

    public Object parse(ResourceClass resourceClass) throws JsonParseException, IOException {
        JsonToken firstTok = this.mParser.nextToken();
        if (firstTok == null) {
            return null;
        }
        if (firstTok != JsonToken.START_OBJECT) {
            throw new JsonParseException("Couldn't find toplevel wrapper object.", this.mParser.getTokenLocation());
        }
        return parseOpenedResource(resourceClass);
    }

    private Resource parseResource(ResourceClass resourceClass) throws JsonParseException, IOException {
        JsonToken tok = this.mParser.nextToken();
        if (tok == JsonToken.VALUE_NULL) {
            return null;
        }
        if (tok != JsonToken.START_OBJECT) {
            throw new JsonParseException("Expected START_OBJECT of " + resourceClass.mResourceName, this.mParser.getCurrentLocation());
        }
        return parseOpenedResource(resourceClass);
    }

    private Resource parseOpenedResource(ResourceClass resourceClass) throws JsonParseException, IOException {
        Resource resource = resourceClass.factory();
        while (this.mParser.nextToken() == JsonToken.FIELD_NAME) {
            String name = this.mParser.getText();
            ResourceProperty prop = resourceClass.mProperties.get(name);
            if (prop == null) {
                this.mParser.nextToken();
                this.mParser.skipChildren();
            } else if (prop instanceof PrimitiveResourceProperty) {
                PrimitiveResourceProperty pprop = (PrimitiveResourceProperty) prop;
                this.mParser.nextToken();
                pprop.parse(resource, this.mParser);
            } else if (prop instanceof NestedResourceProperty) {
                NestedResourceProperty nprop = (NestedResourceProperty) prop;
                ResourceClass rc = Resource.getKlass(nprop.getType());
                if (rc != null) {
                    nprop.set(resource, parseResource(rc));
                } else {
                    OFLog.m182e(TAG, "unknown " + nprop.getType());
                }
            } else if (prop instanceof ArrayResourceProperty) {
                ArrayResourceProperty aprop = (ArrayResourceProperty) prop;
                aprop.set(resource, parseArray());
            } else if (prop instanceof HashIntResourceProperty) {
                HashIntResourceProperty hprop = (HashIntResourceProperty) prop;
                hprop.set(resource, parseHash());
            } else {
                OFLog.m182e(TAG, "Totally don't know what to do about this property");
                this.mParser.skipChildren();
            }
        }
        if (this.mParser.getCurrentToken() != JsonToken.END_OBJECT) {
            throw new JsonParseException("Expected END_OBJECT of " + resourceClass.mResourceName, this.mParser.getCurrentLocation());
        }
        return resource;
    }

    private HashMap<String, Integer> parseHash() throws JsonParseException, IOException {
        JsonToken tok = this.mParser.nextToken();
        if (tok == JsonToken.VALUE_NULL) {
            return null;
        }
        if (tok != JsonToken.START_OBJECT) {
            throw new JsonParseException("Expected START_OBJECT", this.mParser.getCurrentLocation());
        }
        return parseOpenedHash();
    }

    private HashMap<String, Integer> parseOpenedHash() throws JsonParseException, IOException {
        HashMap<String, Integer> resources = new HashMap<>();
        while (this.mParser.nextToken() == JsonToken.FIELD_NAME) {
            String className = this.mParser.getText();
            this.mParser.nextToken();
            int v = this.mParser.getIntValue();
            resources.put(className, Integer.valueOf(v));
        }
        return resources;
    }

    private Resource parseResource() throws JsonParseException, IOException {
        if (this.mParser.nextToken() != JsonToken.FIELD_NAME) {
            throw new JsonParseException("Couldn't find wrapper object.", this.mParser.getTokenLocation());
        }
        String className = this.mParser.getText();
        ResourceClass klass = Resource.getKlass(className);
        if (klass == null) {
            throw new JsonParseException("Don't know class '" + className + "'.", this.mParser.getTokenLocation());
        }
        Resource rv = parseResource(klass);
        if (this.mParser.nextToken() != JsonToken.END_OBJECT) {
            throw new JsonParseException("Expected only one k/v in wrapper object.", this.mParser.getTokenLocation());
        }
        return rv;
    }

    private ArrayList<Resource> parseArray() throws JsonParseException, IOException {
        JsonToken tok = this.mParser.nextToken();
        if (tok == JsonToken.VALUE_NULL) {
            return null;
        }
        if (tok != JsonToken.START_ARRAY) {
            throw new JsonParseException("Wanted START_ARRAY", this.mParser.getCurrentLocation());
        }
        return parseOpenedArray();
    }

    private ArrayList<Resource> parseOpenedArray() throws JsonParseException, IOException {
        ArrayList<Resource> resources = new ArrayList<>();
        while (this.mParser.nextToken() != JsonToken.END_ARRAY) {
            resources.add(parseResource());
        }
        return resources;
    }
}
