package com.openfeint.gamefeed.internal;

import android.graphics.Typeface;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.p004ui.WebViewCache;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class FontHolder {
    private static FontHolder instance = null;
    private static final String tag = "FontHolder";
    private Map<String, Integer> styleNameToValue = new HashMap();
    private Map<Integer, Map<String, Typeface>> typefaceCache;

    public static FontHolder getInstance() {
        if (instance == null) {
            instance = new FontHolder();
        }
        return instance;
    }

    private FontHolder() {
        this.styleNameToValue.put("bold", 1);
        this.styleNameToValue.put("italic", 2);
        this.styleNameToValue.put("oblique", 2);
        this.styleNameToValue.put("bolditalic", 3);
        this.styleNameToValue.put("boldoblique", 3);
        this.styleNameToValue.put("italicbold", 3);
        this.styleNameToValue.put("obliquebold", 3);
        this.typefaceCache = new HashMap();
        this.typefaceCache.put(0, new HashMap());
        this.typefaceCache.put(2, new HashMap());
        this.typefaceCache.put(1, new HashMap());
        this.typefaceCache.put(3, new HashMap());
    }

    public Typeface getTypeface(String typefaceName) {
        String typefaceName2 = typefaceName.toLowerCase();
        String familyName = typefaceName2;
        int style = 0;
        String[] parsed = typefaceName2.split("-");
        if (parsed != null && parsed.length == 2) {
            familyName = parsed[0];
            Integer styleValue = this.styleNameToValue.get(parsed[1]);
            if (styleValue != null) {
                style = styleValue.intValue();
            }
        }
        Typeface rv = this.typefaceCache.get(Integer.valueOf(style)).get(familyName);
        if (rv != null) {
            return rv;
        }
        try {
            String fontPath = WebViewCache.getItemAbsolutePath(typefaceName + ".ttf");
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                rv = Typeface.createFromFile(fontFile);
            }
        } catch (Exception e) {
            OFLog.m185w(tag, String.format("no file for %s in manifest", typefaceName));
        }
        if (rv == null) {
            try {
                rv = Typeface.create(familyName, style);
            } catch (Exception e2) {
                OFLog.m185w(tag, String.format("no file for %s in system", typefaceName));
            }
        }
        if (rv == null) {
            OFLog.m182e(tag, String.format("Completely unable to load font '%s'", typefaceName));
            rv = Typeface.DEFAULT;
        }
        this.typefaceCache.get(Integer.valueOf(style)).put(familyName, rv);
        return rv;
    }
}
