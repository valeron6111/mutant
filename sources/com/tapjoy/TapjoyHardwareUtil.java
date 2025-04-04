package com.tapjoy;

import android.os.Build;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public class TapjoyHardwareUtil {
    public String getSerial() {
        try {
            Field field = Build.class.getField("SERIAL");
            if (field == null) {
                return null;
            }
            return field.get(Build.class).toString();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e2) {
            throw new RuntimeException(e2);
        }
    }
}
