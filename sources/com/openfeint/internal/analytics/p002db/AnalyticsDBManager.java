package com.openfeint.internal.analytics.p002db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.openfeint.internal.logcat.OFLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class AnalyticsDBManager {
    private static final String DATABASE_NAME = "analytics.db";
    private static final int DATABASE_VERSION = 1;
    public static final String KEY_ID = "log_id";
    public static final String KEY_JSON = "json";
    public static final String KEY_TIME = "log_time";
    private static final String TABLE_NAME = "eventLogs";
    private static AnalyticsDBManager instance = null;
    private static final String tag = "AnalyticsDBManager";

    /* renamed from: db */
    private SQLiteDatabase f286db;
    private DBOpenHelper dbHelper;

    private AnalyticsDBManager(Context context) {
        if (this.dbHelper == null) {
            this.dbHelper = new DBOpenHelper(context, DATABASE_NAME, null, 1);
        }
    }

    public static AnalyticsDBManager instance(Context context) {
        if (instance == null) {
            instance = new AnalyticsDBManager(context);
        }
        return instance;
    }

    private class DBOpenHelper extends SQLiteOpenHelper {
        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase _db) {
            onUpgrade(_db, 0, 1);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            if (_oldVersion == 0 && _newVersion == 1) {
                OFLog.m183i(AnalyticsDBManager.tag, "Migrating DB form V0 to V1..");
                _db.execSQL("create table eventLogs (log_id integer primary key autoincrement, log_time integer not null, json text not null );");
                _oldVersion++;
            }
            if (_oldVersion != _newVersion) {
                OFLog.m182e(AnalyticsDBManager.tag, String.format("Unable to upgrade DB from %d to %d.", Integer.valueOf(_oldVersion), Integer.valueOf(_newVersion)));
            } else {
                OFLog.m183i(AnalyticsDBManager.tag, String.format("Success on upgrade DB from %d to %d.", Integer.valueOf(_oldVersion), Integer.valueOf(_newVersion)));
            }
        }
    }

    public void close() {
        OFLog.m184v(tag, "close db...");
        if (this.f286db != null && this.f286db.isOpen()) {
            try {
                this.f286db.close();
            } catch (Exception e) {
                OFLog.m182e(tag, "db close failed");
            }
        }
    }

    public void openToWR() {
        OFLog.m184v(tag, "open db to WR..");
        if (this.f286db == null || !this.f286db.isOpen()) {
            try {
                this.f286db = this.dbHelper.getWritableDatabase();
                return;
            } catch (SQLiteException e) {
                OFLog.m182e(tag, "db open failed");
                try {
                    this.f286db.close();
                    return;
                } catch (Exception e2) {
                    return;
                }
            }
        }
        OFLog.m184v(tag, "db already open ");
    }

    public void openToRead() {
        OFLog.m184v(tag, "open db readonly..");
        if (this.f286db == null || !this.f286db.isOpen()) {
            try {
                this.f286db = this.dbHelper.getWritableDatabase();
                return;
            } catch (SQLiteException e) {
                OFLog.m182e(tag, "db open failed");
                try {
                    this.f286db.close();
                    return;
                } catch (Exception e2) {
                    return;
                }
            }
        }
        OFLog.m184v(tag, "db already open ");
    }

    public long insertLog(String log_json) {
        openToWR();
        ContentValues newValues = new ContentValues();
        long now = System.currentTimeMillis();
        newValues.put(KEY_TIME, Long.valueOf(now));
        newValues.put(KEY_JSON, log_json);
        long success = -1;
        try {
            success = this.f286db.insert(TABLE_NAME, null, newValues);
            OFLog.m181d(tag, "insert " + String.valueOf(success));
        } catch (Exception e) {
            OFLog.m182e(tag, "insert failed");
        } finally {
            close();
        }
        return success;
    }

    public boolean removeLog(long _rowIndex) {
        openToWR();
        boolean success = false;
        try {
            success = this.f286db.delete(TABLE_NAME, new StringBuilder().append("log_id=").append(_rowIndex).toString(), null) > 0;
            if (success) {
                OFLog.m181d(tag, "removeLog success");
            } else {
                OFLog.m181d(tag, "removeLog failed");
            }
        } catch (Exception e) {
            OFLog.m182e(tag, String.format("remove row %i failed", Long.valueOf(_rowIndex)));
        } finally {
            close();
        }
        return success;
    }

    public boolean removeLog(long start_id, long end_id) {
        openToWR();
        boolean success = this.f286db.delete(TABLE_NAME, new StringBuilder().append("log_id>=").append(start_id).append(" AND ").append(KEY_ID).append("<=").append(end_id).toString(), null) > 0;
        if (success) {
            OFLog.m181d(tag, String.format("batch removeLog  success from %d to %d", Long.valueOf(start_id), Long.valueOf(end_id)));
        } else {
            OFLog.m185w(tag, String.format("batch removeLog failed from %d to %d", Long.valueOf(start_id), Long.valueOf(end_id)));
        }
        close();
        return success;
    }

    public boolean clearValue() {
        return this.f286db.delete(TABLE_NAME, "log_id=*", null) > 0;
    }

    public boolean updateLog(long _rowIndex, String log_json) {
        openToWR();
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TIME, Long.valueOf(System.currentTimeMillis()));
        newValues.put(KEY_TIME, log_json);
        boolean success = this.f286db.update(TABLE_NAME, newValues, new StringBuilder().append("log_id=").append(_rowIndex).toString(), null) > 0;
        close();
        return success;
    }

    public List<Map<String, Object>> getAllItems() {
        openToRead();
        List<Map<String, Object>> listKeyValues = new ArrayList<>();
        try {
            Cursor cursor = this.f286db.query(TABLE_NAME, null, null, null, null, null, null);
            try {
                cursor.moveToFirst();
                do {
                    Map<String, Object> line = new HashMap<>(3);
                    Long id = Long.valueOf(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                    line.put(KEY_ID, id);
                    Long time = Long.valueOf(cursor.getLong(cursor.getColumnIndex(KEY_TIME)));
                    line.put(KEY_TIME, time);
                    String json = cursor.getString(cursor.getColumnIndex(KEY_JSON));
                    line.put(KEY_JSON, json);
                    listKeyValues.add(line);
                } while (cursor.moveToNext());
            } catch (Exception e) {
                OFLog.m182e(tag, "Exception in query.");
            } finally {
                cursor.close();
            }
        } catch (Exception e2) {
            OFLog.m182e(tag, "Exception creating query.");
        } finally {
            close();
        }
        return listKeyValues;
    }
}
