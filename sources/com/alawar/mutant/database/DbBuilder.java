package com.alawar.mutant.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* loaded from: classes.dex */
public class DbBuilder {
    private static final int DB_VESION = 1;
    public static final int ID_COLUMN = 0;
    public static final String KEY_ID = "_id";
    public static final String KEY_TAG = "tag";
    public static final String KEY_VALUE = "value";
    public static final int TAG_COLUMN = 2;
    public static final int VALUE_COLUMN = 1;
    private Context _context;
    private SQLiteDatabase _database;
    private DbOpenHelper _dbOpenHelper;
    private String dbName;
    private String tableName;

    public DbBuilder(Context context, String tableName) {
        this.dbName = null;
        this.tableName = null;
        this._context = context;
        this.dbName = tableName + ".db";
        this.tableName = tableName;
        init();
    }

    public Cursor queryAllItems() {
        String[] columnsToTake = {KEY_ID, KEY_VALUE, KEY_TAG};
        return ensureDb().query(this.tableName, columnsToTake, null, null, null, null, KEY_ID);
    }

    private SQLiteDatabase ensureDb() {
        if (!this._database.isOpen()) {
            stop();
            init();
        }
        return this._database;
    }

    public long addItem(DbItem item) {
        ContentValues values = new ContentValues();
        values.put(KEY_ID, item.getId());
        values.put(KEY_VALUE, item.getValue());
        values.put(KEY_TAG, item.getTag());
        return ensureDb().insert(this.tableName, null, values);
    }

    /* JADX WARN: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:5:0x0015  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void safeAddItem(com.alawar.mutant.database.DbItem r3) {
        /*
            r2 = this;
            java.lang.String r1 = r3.getId()
            android.database.Cursor r0 = r2.getRecords(r1)
            if (r0 == 0) goto L19
            boolean r1 = r0.moveToNext()     // Catch: java.lang.Throwable -> L1d
            if (r1 == 0) goto L19
            r2.updateItem(r3)     // Catch: java.lang.Throwable -> L1d
        L13:
            if (r0 == 0) goto L18
            r0.close()
        L18:
            return
        L19:
            r2.addItem(r3)     // Catch: java.lang.Throwable -> L1d
            goto L13
        L1d:
            r1 = move-exception
            if (r0 == 0) goto L23
            r0.close()
        L23:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alawar.mutant.database.DbBuilder.safeAddItem(com.alawar.mutant.database.DbItem):void");
    }

    public Cursor getRecords(String id) {
        return ensureDb().query(this.tableName, null, "_id = ?", new String[]{id}, null, null, KEY_ID);
    }

    public boolean updateItem(DbItem item) {
        ContentValues values = new ContentValues();
        values.put(KEY_VALUE, item.getValue());
        values.put(KEY_TAG, item.getTag());
        return ensureDb().update(this.tableName, values, "_id=?", new String[]{item.getId()}) > 0;
    }

    public boolean removeItem(String uid) {
        return ensureDb().delete(this.tableName, "_id=?", new String[]{uid}) > 0;
    }

    public void stop() {
        this._database.close();
        this._dbOpenHelper.close();
    }

    private void init() {
        this._dbOpenHelper = new DbOpenHelper(this._context, this.dbName, null, 1);
        try {
            SQLiteDatabase db = this._dbOpenHelper.getReadableDatabase();
            db.close();
            this._database = this._dbOpenHelper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(toString(), "Error Getting Database: " + e.toString());
            throw new RuntimeException("Cant Open Database", e);
        }
    }

    public boolean isOpen() {
        return this._database.isOpen();
    }

    private class DbOpenHelper extends SQLiteOpenHelper {
        public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            try {
                String CREATE_DB = "CREATE TABLE " + DbBuilder.this.tableName + " (" + DbBuilder.KEY_ID + " VARCHAR(256) PRIMARY KEY, " + DbBuilder.KEY_VALUE + " VARCHAR(256) NOT NULL, " + DbBuilder.KEY_TAG + " VARCHAR(256));";
                db.execSQL(CREATE_DB);
            } catch (SQLException e) {
                Log.e(toString(), "Error Creating Database: " + e.toString());
                throw new RuntimeException("Cant Create Database", e);
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + DbBuilder.this.tableName);
                onCreate(db);
            } catch (SQLException e) {
                Log.e(toString(), "Error Droping Database: " + e.toString());
                throw new RuntimeException("Cant Upgrade Database", e);
            }
        }
    }
}
