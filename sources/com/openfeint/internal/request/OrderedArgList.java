package com.openfeint.internal.request;

import com.openfeint.internal.vendor.com.google.api.client.escape.PercentEscaper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class OrderedArgList {
    ArrayList<NameValuePair> mArgs = new ArrayList<>();

    public OrderedArgList() {
    }

    public NameValuePair remove(String key) {
        Iterator i$ = this.mArgs.iterator();
        while (i$.hasNext()) {
            NameValuePair p = i$.next();
            if (p.getName().equals(key)) {
                this.mArgs.remove(p);
                return p;
            }
        }
        return null;
    }

    public OrderedArgList(Map<String, Object> args) {
        accumulate(args, (String) null);
    }

    public OrderedArgList(JSONObject j) {
        try {
            accumulate(j, (String) null);
        } catch (JSONException e) {
        }
    }

    public OrderedArgList(OrderedArgList rhs) {
        this.mArgs.addAll(rhs.getArgs());
    }

    public List<NameValuePair> getArgs() {
        return this.mArgs;
    }

    public List<NameValuePair> getArgsSorted() {
        List<NameValuePair> s = new ArrayList<>();
        s.addAll(this.mArgs);
        Collections.sort(s, new NVPComparator());
        return s;
    }

    public void put(String k, String v) {
        this.mArgs.add(new BasicNameValuePair(k, v));
    }

    public String getArgString() {
        return getArgString(getArgs());
    }

    public String getArgStringSorted() {
        return getArgString(getArgsSorted());
    }

    private static String getArgString(List<NameValuePair> pairs) {
        StringBuilder accumulator = null;
        PercentEscaper escaper = new PercentEscaper("-_.*", true);
        for (NameValuePair nvp : pairs) {
            if (accumulator == null) {
                accumulator = new StringBuilder();
            } else {
                accumulator.append('&');
            }
            accumulator.append(escaper.escape(nvp.getName()));
            accumulator.append('=');
            if (nvp.getValue() != null) {
                accumulator.append(escaper.escape(nvp.getValue()));
            }
        }
        if (accumulator == null) {
            return null;
        }
        return accumulator.toString();
    }

    private void accumulate(JSONArray jsonArray, String fieldName) throws JSONException {
        int size = jsonArray.length();
        for (int arrayIndex = 0; arrayIndex < size; arrayIndex++) {
            Object arrayEntry = jsonArray.get(arrayIndex);
            if (arrayEntry instanceof JSONObject) {
                accumulate((JSONObject) arrayEntry, fieldName);
            } else if (arrayEntry instanceof JSONArray) {
                accumulate((JSONArray) arrayEntry, fieldName);
            } else {
                put(fieldName, arrayEntry.toString());
            }
        }
    }

    private void accumulate(final JSONObject jsonObject, String prefix) throws JSONException {
        Iterable<String> objectIteratable = new Iterable<String>() { // from class: com.openfeint.internal.request.OrderedArgList.1
            @Override // java.lang.Iterable
            public Iterator<String> iterator() {
                return jsonObject.keys();
            }
        };
        for (String fieldName : objectIteratable) {
            Object fieldObject = jsonObject.get(fieldName);
            String subKey = prefix == null ? fieldName : prefix + "[" + fieldName + "]";
            if (fieldObject instanceof JSONObject) {
                accumulate((JSONObject) fieldObject, subKey);
            } else if (fieldObject instanceof JSONArray) {
                accumulate((JSONArray) fieldObject, subKey);
            } else {
                put(subKey, fieldObject.toString());
            }
        }
    }

    private void accumulate(Map<String, Object> args, String prefix) {
        for (Map.Entry<String, Object> me : args.entrySet()) {
            String k = me.getKey();
            Object v = me.getValue();
            String subKey = prefix == null ? k : prefix + "[" + k + "]";
            if (v instanceof Map) {
                accumulate(stringObjectMap(v), subKey);
            } else {
                put(subKey, v.toString());
            }
        }
    }

    private final Map<String, Object> stringObjectMap(Object v) {
        return (Map) v;
    }

    static class NVPComparator implements Comparator<NameValuePair> {
        NVPComparator() {
        }

        @Override // java.util.Comparator
        public int compare(NameValuePair a, NameValuePair b) {
            int r = a.getName().compareTo(b.getName());
            if (r == 0) {
                return a.getValue().compareTo(b.getValue());
            }
            return r;
        }
    }
}
