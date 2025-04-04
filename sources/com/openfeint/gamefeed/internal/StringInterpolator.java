package com.openfeint.gamefeed.internal;

import android.text.TextUtils;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.vendor.com.google.api.client.escape.PercentEscaper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class StringInterpolator {
    private static final String tag = "StringInterpolator";
    private Map<String, Object> combined;
    private static Pattern square = Pattern.compile("\\[([^\\[]+)\\]");
    private static Pattern curly = Pattern.compile("\\{([^}]+)\\}");
    private static Pattern dot = Pattern.compile("\\.");
    private static PercentEscaper escaper = new PercentEscaper("-_.*", true);
    private static PatternProcessor curlyProcessor = new PatternProcessor() { // from class: com.openfeint.gamefeed.internal.StringInterpolator.1
        @Override // com.openfeint.gamefeed.internal.StringInterpolator.PatternProcessor
        public Pattern pattern() {
            return StringInterpolator.curly;
        }

        @Override // com.openfeint.gamefeed.internal.StringInterpolator.PatternProcessor
        public String process(String s) {
            return s;
        }
    };
    private static PatternProcessor squareURIEscapingProcessor = new PatternProcessor() { // from class: com.openfeint.gamefeed.internal.StringInterpolator.2
        @Override // com.openfeint.gamefeed.internal.StringInterpolator.PatternProcessor
        public Pattern pattern() {
            return StringInterpolator.square;
        }

        @Override // com.openfeint.gamefeed.internal.StringInterpolator.PatternProcessor
        public String process(String s) {
            return StringInterpolator.escaper.escape(s);
        }
    };
    private static PatternProcessor squareNonEscapingProcessor = new PatternProcessor() { // from class: com.openfeint.gamefeed.internal.StringInterpolator.3
        @Override // com.openfeint.gamefeed.internal.StringInterpolator.PatternProcessor
        public Pattern pattern() {
            return StringInterpolator.square;
        }

        @Override // com.openfeint.gamefeed.internal.StringInterpolator.PatternProcessor
        public String process(String s) {
            return s;
        }
    };
    private static PatternProcessor squareHTMLEscapingProcessor = new PatternProcessor() { // from class: com.openfeint.gamefeed.internal.StringInterpolator.4
        @Override // com.openfeint.gamefeed.internal.StringInterpolator.PatternProcessor
        public Pattern pattern() {
            return StringInterpolator.square;
        }

        @Override // com.openfeint.gamefeed.internal.StringInterpolator.PatternProcessor
        public String process(String s) {
            return TextUtils.htmlEncode(s);
        }
    };

    private interface PatternProcessor {
        Pattern pattern();

        String process(String str);
    }

    public StringInterpolator(Map<String, Object> _custom, Map<String, Object> _itemData) {
        this.combined = new HashMap(_itemData);
        this.combined.put("custom", _custom);
    }

    public StringInterpolator(Map<String, Object> _custom, Map<String, Object> _configs, Map<String, Object> _itemData) {
        this.combined = new HashMap(_itemData);
        this.combined.put("custom", _custom);
        this.combined.put("configs", _configs);
    }

    public String interpolate(String s) {
        if (s == null) {
            return null;
        }
        return process(process(s, curlyProcessor), squareURIEscapingProcessor);
    }

    public String interpolateIgnoringSquareBraces(String s) {
        if (s == null) {
            return null;
        }
        return process(s, curlyProcessor);
    }

    public String interpolateWithoutEscapingSquareBraces(String s) {
        if (s == null) {
            return null;
        }
        return process(process(s, curlyProcessor), squareNonEscapingProcessor);
    }

    public String interpolateEscapingSquareBracesAsHTML(String s) {
        if (s == null) {
            return null;
        }
        return process(process(s, curlyProcessor), squareHTMLEscapingProcessor);
    }

    public Object recursivelyInterpolate(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof String) {
            return interpolate((String) o);
        }
        if (o instanceof Map) {
            Map<String, Object> asMap = (Map) o;
            Map<String, Object> rv = new HashMap<>();
            for (String k : asMap.keySet()) {
                rv.put(k, recursivelyInterpolate(asMap.get(k)));
            }
            return rv;
        }
        if (!(o instanceof List)) {
            return o;
        }
        List<Object> asList = (List) o;
        List<Object> rv2 = new ArrayList<>();
        for (Object nested : asList) {
            rv2.add(recursivelyInterpolate(nested));
        }
        return rv2;
    }

    private String process(String s, PatternProcessor pp) {
        Matcher m = pp.pattern().matcher(s);
        int start = 0;
        StringBuilder builder = new StringBuilder();
        while (m.find()) {
            builder.append(s.substring(start, m.start()));
            Object interpolated = valueForKeyPath(m.group(1));
            if (interpolated != null && !(interpolated instanceof Map) && !(interpolated instanceof List)) {
                builder.append(pp.process(interpolated.toString()));
            }
            start = m.end();
        }
        builder.append(s.substring(start));
        return builder.toString();
    }

    public Object valueForKeyPath(String path) {
        try {
            Object obj = this.combined;
            String[] arr$ = dot.split(path);
            for (String subpath : arr$) {
                obj = ((Map) obj).get(subpath);
            }
            return obj;
        } catch (Exception e) {
            OFLog.m182e(tag, "valueForKeyPath failed, return null");
            return null;
        }
    }
}
