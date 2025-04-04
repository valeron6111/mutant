package com.openfeint.internal;

import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonEncoding;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonFactory;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerationException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class JsonCoder {
    private static final String TAG = "JsonCoder";

    public static String generateJson(Object objectToGenerate) {
        StringWriter stringWriter = new StringWriter();
        JsonFactory jsonFactory = new JsonFactory();
        try {
            JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(stringWriter);
            generate(objectToGenerate, jsonGenerator);
            jsonGenerator.close();
            return stringWriter.toString();
        } catch (Exception exception) {
            OFLog.m182e(TAG, exception.getMessage());
            return null;
        }
    }

    public static void generateJson(Object objectToGenerate, OutputStream output) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = null;
        try {
            try {
                jsonGenerator = jsonFactory.createJsonGenerator(output, JsonEncoding.UTF8);
                generate(objectToGenerate, jsonGenerator);
            } catch (JsonGenerationException jsonException) {
                OFLog.m182e(TAG, jsonException.getMessage());
                try {
                    jsonGenerator.close();
                } catch (Exception e) {
                }
            } catch (ClassCastException castException) {
                OFLog.m182e(TAG, castException.getMessage());
                try {
                    jsonGenerator.close();
                } catch (Exception e2) {
                }
            }
        } finally {
            try {
                jsonGenerator.close();
            } catch (Exception e3) {
            }
        }
    }

    private static void generate(Object objectToGenerate, JsonGenerator jsonGenerator) throws IOException, ClassCastException {
        if (objectToGenerate == null) {
            jsonGenerator.writeNull();
            return;
        }
        if (objectToGenerate instanceof Date) {
            jsonGenerator.writeString(makeDateParser().format(objectToGenerate));
            return;
        }
        if (objectToGenerate instanceof String) {
            jsonGenerator.writeString((String) objectToGenerate);
            return;
        }
        if (objectToGenerate instanceof Map) {
            jsonGenerator.writeStartObject();
            Map<String, Object> objectAsMap = (Map) objectToGenerate;
            for (String fieldName : objectAsMap.keySet()) {
                jsonGenerator.writeFieldName(fieldName);
                generate(objectAsMap.get(fieldName), jsonGenerator);
            }
            jsonGenerator.writeEndObject();
            return;
        }
        if (objectToGenerate instanceof List) {
            jsonGenerator.writeStartArray();
            List<Object> objectAsList = (List) objectToGenerate;
            for (Object nestedObject : objectAsList) {
                generate(nestedObject, jsonGenerator);
            }
            jsonGenerator.writeEndArray();
            return;
        }
        if (objectToGenerate instanceof Object[]) {
            jsonGenerator.writeStartArray();
            Object[] objectAsArray = (Object[]) objectToGenerate;
            for (Object nestedObject2 : objectAsArray) {
                generate(nestedObject2, jsonGenerator);
            }
            jsonGenerator.writeEndArray();
            return;
        }
        if (objectToGenerate instanceof Number) {
            Number objectAsNumber = (Number) objectToGenerate;
            if (!(objectAsNumber instanceof Double)) {
                if (!(objectAsNumber instanceof Float)) {
                    if (!(objectAsNumber instanceof Integer)) {
                        if (!(objectAsNumber instanceof Long)) {
                            if (!(objectAsNumber instanceof Byte)) {
                                if (!(objectAsNumber instanceof Short)) {
                                    throw new JsonGenerationException("Unhandled number type " + objectAsNumber.getClass().getCanonicalName());
                                }
                                jsonGenerator.writeNumber(objectAsNumber.longValue());
                                return;
                            }
                            jsonGenerator.writeNumber(objectAsNumber.longValue());
                            return;
                        }
                        jsonGenerator.writeNumber(objectAsNumber.longValue());
                        return;
                    }
                    jsonGenerator.writeNumber(objectAsNumber.intValue());
                    return;
                }
                jsonGenerator.writeNumber(objectAsNumber.floatValue());
                return;
            }
            jsonGenerator.writeNumber(objectAsNumber.doubleValue());
            return;
        }
        if (objectToGenerate instanceof Boolean) {
            jsonGenerator.writeBoolean(((Boolean) objectToGenerate).booleanValue());
            return;
        }
        throw new JsonGenerationException("Can't generate JSON for object of class " + objectToGenerate.getClass().getCanonicalName());
    }

    static DateFormat makeDateParser() {
        DateFormat p = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        p.setTimeZone(TimeZone.getTimeZone("Zulu"));
        return p;
    }

    public static Object parse(String json) {
        try {
            JsonParser jsonParser = new JsonFactory().createJsonParser(json);
            jsonParser.nextToken();
            return parse(jsonParser);
        } catch (IOException ioException) {
            OFLog.m182e(TAG, ioException.getMessage());
            return null;
        }
    }

    public static Object parse(byte[] data) {
        try {
            JsonParser jsonParser = new JsonFactory().createJsonParser(data);
            jsonParser.nextToken();
            return parse(jsonParser);
        } catch (IOException ioException) {
            OFLog.m182e(TAG, ioException.getMessage());
            return null;
        }
    }

    private static Object parse(JsonParser jsonParser) throws IOException, JsonParseException {
        JsonToken currToken = jsonParser.getCurrentToken();
        if (JsonToken.START_ARRAY == currToken) {
            ArrayList<Object> listToReturn = new ArrayList<>();
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                listToReturn.add(parse(jsonParser));
            }
            return listToReturn;
        }
        if (JsonToken.START_OBJECT == currToken) {
            HashMap<String, Object> mapToReturn = new HashMap<>();
            while (jsonParser.nextToken() == JsonToken.FIELD_NAME) {
                String fieldName = jsonParser.getCurrentName();
                jsonParser.nextToken();
                Object fieldValue = parse(jsonParser);
                mapToReturn.put(fieldName, fieldValue);
            }
            if (jsonParser.getCurrentToken() != JsonToken.END_OBJECT) {
                throw new JsonParseException("Garbage in object", jsonParser.getCurrentLocation());
            }
            return mapToReturn;
        }
        if (JsonToken.VALUE_FALSE == currToken) {
            return new Boolean(false);
        }
        if (JsonToken.VALUE_TRUE == currToken) {
            return new Boolean(true);
        }
        if (JsonToken.VALUE_NULL == currToken) {
            return null;
        }
        if (JsonToken.VALUE_NUMBER_FLOAT == currToken) {
            return new Double(jsonParser.getDoubleValue());
        }
        if (JsonToken.VALUE_NUMBER_INT == currToken) {
            return new Long(jsonParser.getLongValue());
        }
        if (JsonToken.VALUE_STRING == currToken) {
            return jsonParser.getText();
        }
        OFLog.m182e(TAG, " no idea dude " + jsonParser.getText());
        return null;
    }
}
