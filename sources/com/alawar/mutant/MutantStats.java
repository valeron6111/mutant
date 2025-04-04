package com.alawar.mutant;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import com.alawar.mutant.database.DbBuilder;
import com.alawar.mutant.database.DbItem;
import com.alawar.mutant.jni.MutantMessages;

/* loaded from: classes.dex */
public class MutantStats extends MutantMessages {
    private static DbBuilder mStatsDB = null;
    private static final int queryGetString = 1;
    private static final int querySetString = 0;

    public static void initialize(Activity activity) {
        if (mStatsDB != null && !mStatsDB.isOpen()) {
            mStatsDB.stop();
            mStatsDB = null;
        }
        if (mStatsDB == null) {
            mStatsDB = new DbBuilder(activity, "mutant_stats");
        }
    }

    public static void destroy() {
        if (mStatsDB != null) {
            mStatsDB.stop();
            mStatsDB = null;
        }
    }

    public static boolean setString(String args) {
        String[] splitTag = args.split("=");
        if (splitTag.length == 2) {
            String key = splitTag[0];
            String value = splitTag[1];
            return setString(key, value);
        }
        Log.e("MutantStats", "setString: " + args);
        return false;
    }

    public static boolean setString(String key, String value) {
        Log.i("MutantStats", key + " saving value: " + value);
        if (value == null) {
            return false;
        }
        ensureDb().safeAddItem(new DbItem(key, MutantMessages.sEmpty, value));
        return true;
    }

    private static DbBuilder ensureDb() {
        if (Global.applicationContext != null) {
            initialize(Global.applicationContext);
        }
        return mStatsDB;
    }

    public static String getString(String tag) {
        return getString(tag, null);
    }

    public static String getString(String tag, String defaultValue) {
        try {
            Cursor cursor = ensureDb().getRecords(tag);
            if (cursor != null) {
                try {
                    if (cursor.moveToNext()) {
                        return cursor.getString(cursor.getColumnIndex(DbBuilder.KEY_VALUE));
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
                return defaultValue;
            }
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String statsProcess(int id, String args) {
        String result;
        if (id == 0) {
            if (setString(args)) {
                return MutantMessages.sSuccess;
            }
        } else if (id == 1 && (result = getString(args)) != null) {
            return result;
        }
        return MutantMessages.sFail;
    }
}
