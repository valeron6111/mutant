package com.openfeint.internal;

import android.content.Context;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class Util5 {
    public static String getAccountNameEclair(Context ctx) {
        try {
            Class<?> cAccountManager = Class.forName("android.accounts.AccountManager");
            Method get = cAccountManager.getMethod("get", Context.class);
            Object accountManager = get.invoke(cAccountManager, ctx);
            Method getAccountsByType = cAccountManager.getMethod("getAccountsByType", String.class);
            Object[] accounts = (Object[]) getAccountsByType.invoke(accountManager, "com.google");
            Class<?> cAccount = Class.forName("android.accounts.Account");
            return (String) cAccount.getField("name").get(accounts[0]);
        } catch (Exception e) {
            return null;
        }
    }
}
