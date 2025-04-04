package com.openfeint.internal;

import android.content.Context;
import android.content.res.Configuration;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public class Util11 {
    public static boolean isPad(Context ctx) {
        try {
            Field f = Configuration.class.getField("SCREENLAYOUT_SIZE_XLARGE");
            int mask = f.getInt(null);
            return (ctx.getResources().getConfiguration().screenLayout & mask) == mask;
        } catch (Exception e) {
            return false;
        }
    }
}
