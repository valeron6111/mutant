package com.openfeint.internal.p003db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* loaded from: classes.dex */
public abstract class SQLiteOpenHelperX {
    private static final String TAG = SQLiteOpenHelperX.class.getSimpleName();
    private SQLiteDatabase mDatabase = null;
    private SQLiteOpenHelper mHelper;
    private String mName;
    private int mNewVersion;

    public abstract void onCreate(SQLiteDatabase sQLiteDatabase);

    public abstract void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2);

    public SQLiteOpenHelperX(String path, int version) {
        if (version < 1) {
            throw new IllegalArgumentException("Version must be >= 1, was " + version);
        }
        this.mName = path;
        this.mNewVersion = version;
    }

    public SQLiteOpenHelperX(SQLiteOpenHelper helper) {
        this.mHelper = helper;
    }

    public void setSQLiteOpenHelper(String path, int version) {
        if (version < 1) {
            throw new IllegalArgumentException("Version must be >= 1, was " + version);
        }
        this.mName = path;
        this.mNewVersion = version;
        close();
    }

    public void setSQLiteOpenHelper(SQLiteOpenHelper helper) {
        this.mHelper = helper;
        close();
    }

    /* JADX WARN: Finally extract failed */
    public synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db;
        if (this.mHelper != null) {
            db = this.mHelper.getWritableDatabase();
        } else if (this.mDatabase != null && this.mDatabase.isOpen() && !this.mDatabase.isReadOnly()) {
            db = this.mDatabase;
        } else {
            db = null;
            try {
                db = SQLiteDatabase.openOrCreateDatabase(this.mName, (SQLiteDatabase.CursorFactory) null);
                int version = db.getVersion();
                if (version != this.mNewVersion) {
                    db.beginTransaction();
                    try {
                        if (version == 0) {
                            onCreate(db);
                        } else {
                            onUpgrade(db, version, this.mNewVersion);
                        }
                        db.setVersion(this.mNewVersion);
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                }
                if (1 != 0) {
                    if (this.mDatabase != null) {
                        try {
                            this.mDatabase.close();
                        } catch (Exception e) {
                        }
                    }
                    this.mDatabase = db;
                } else if (db != null) {
                    db.close();
                }
            } catch (Throwable th) {
                if (0 != 0) {
                    if (this.mDatabase != null) {
                        try {
                            this.mDatabase.close();
                        } catch (Exception e2) {
                        }
                    }
                    this.mDatabase = db;
                } else if (db != null) {
                    db.close();
                }
                throw th;
            }
        }
        return db;
    }

    public synchronized SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase sQLiteDatabase;
        if (this.mHelper != null) {
            sQLiteDatabase = this.mHelper.getReadableDatabase();
        } else if (this.mDatabase != null && this.mDatabase.isOpen()) {
            sQLiteDatabase = this.mDatabase;
        } else {
            try {
                sQLiteDatabase = getWritableDatabase();
            } catch (SQLiteException e) {
                if (this.mName == null) {
                    throw e;
                }
                Log.e(TAG, "Couldn't open " + this.mName + " for writing (will try read-only):", e);
                SQLiteDatabase db = null;
                try {
                    SQLiteDatabase db2 = SQLiteDatabase.openDatabase(this.mName, null, 1);
                    if (db2.getVersion() != this.mNewVersion) {
                        throw new SQLiteException("Can't upgrade read-only database from version " + db2.getVersion() + " to " + this.mNewVersion + ": " + this.mName);
                    }
                    Log.w(TAG, "Opened " + this.mName + " in read-only mode");
                    this.mDatabase = db2;
                    sQLiteDatabase = this.mDatabase;
                    if (db2 != null && db2 != this.mDatabase) {
                        db2.close();
                    }
                } catch (Throwable th) {
                    if (0 != 0 && null != this.mDatabase) {
                        db.close();
                    }
                    throw th;
                }
            }
        }
        return sQLiteDatabase;
    }

    public synchronized void close() {
        if (this.mDatabase != null && this.mDatabase.isOpen()) {
            this.mDatabase.close();
            this.mDatabase = null;
        }
        if (this.mHelper != null) {
            this.mHelper.close();
        }
    }
}
