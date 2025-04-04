package oauth.signpost.http;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import oauth.signpost.OAuth;

/* loaded from: classes.dex */
public class HttpParameters implements Map<String, SortedSet<String>>, Serializable {
    private TreeMap<String, SortedSet<String>> wrappedMap = new TreeMap<>();

    @Override // java.util.Map
    public SortedSet<String> put(String key, SortedSet<String> value) {
        return this.wrappedMap.put(key, value);
    }

    public SortedSet<String> put(String key, SortedSet<String> values, boolean percentEncode) {
        if (!percentEncode) {
            return this.wrappedMap.put(key, values);
        }
        remove((Object) key);
        for (String v : values) {
            put(key, v, true);
        }
        return get((Object) key);
    }

    public String put(String key, String value) {
        return put(key, value, false);
    }

    public String put(String key, String value, boolean percentEncode) {
        SortedSet<String> values = this.wrappedMap.get(key);
        if (values == null) {
            values = new TreeSet<>();
            TreeMap<String, SortedSet<String>> treeMap = this.wrappedMap;
            if (percentEncode) {
                key = OAuth.percentEncode(key);
            }
            treeMap.put(key, values);
        }
        if (value != null) {
            if (percentEncode) {
                value = OAuth.percentEncode(value);
            }
            values.add(value);
        }
        return value;
    }

    public String putNull(String key, String nullString) {
        return put(key, nullString);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends SortedSet<String>> m) {
        this.wrappedMap.putAll(m);
    }

    public void putAll(Map<? extends String, ? extends SortedSet<String>> m, boolean percentEncode) {
        if (percentEncode) {
            for (String key : m.keySet()) {
                put(key, m.get(key), true);
            }
            return;
        }
        this.wrappedMap.putAll(m);
    }

    public void putAll(String[] keyValuePairs, boolean percentEncode) {
        for (int i = 0; i < keyValuePairs.length - 1; i += 2) {
            put(keyValuePairs[i], keyValuePairs[i + 1], percentEncode);
        }
    }

    public void putMap(Map<String, List<String>> m) {
        for (String key : m.keySet()) {
            SortedSet<String> vals = get((Object) key);
            if (vals == null) {
                vals = new TreeSet<>();
                put(key, vals);
            }
            vals.addAll(m.get(key));
        }
    }

    @Override // java.util.Map
    public SortedSet<String> get(Object key) {
        return this.wrappedMap.get(key);
    }

    public String getFirst(Object key) {
        return getFirst(key, false);
    }

    public String getFirst(Object key, boolean percentDecode) {
        SortedSet<String> values = this.wrappedMap.get(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        String value = values.first();
        return percentDecode ? OAuth.percentDecode(value) : value;
    }

    public String getAsQueryString(Object key) {
        StringBuilder sb = new StringBuilder();
        String key2 = OAuth.percentEncode((String) key);
        Set<String> values = this.wrappedMap.get(key2);
        if (values == null) {
            return ((Object) key2) + "=";
        }
        Iterator<String> iter = values.iterator();
        while (iter.hasNext()) {
            sb.append(((Object) key2) + "=" + iter.next());
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public String getAsHeaderElement(String key) {
        String value = getFirst(key);
        if (value == null) {
            return null;
        }
        return key + "=\"" + value + "\"";
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.wrappedMap.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        for (SortedSet<String> values : this.wrappedMap.values()) {
            if (values.contains(value)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map
    public int size() {
        int count = 0;
        for (String key : this.wrappedMap.keySet()) {
            count += this.wrappedMap.get(key).size();
        }
        return count;
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.wrappedMap.isEmpty();
    }

    @Override // java.util.Map
    public void clear() {
        this.wrappedMap.clear();
    }

    @Override // java.util.Map
    public SortedSet<String> remove(Object key) {
        return this.wrappedMap.remove(key);
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        return this.wrappedMap.keySet();
    }

    @Override // java.util.Map
    public Collection<SortedSet<String>> values() {
        return this.wrappedMap.values();
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, SortedSet<String>>> entrySet() {
        return this.wrappedMap.entrySet();
    }
}
