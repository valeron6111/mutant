package com.openfeint.internal.p003db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import com.openfeint.internal.Util;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.p004ui.WebViewCache;
import java.io.File;

/* renamed from: com.openfeint.internal.db.DB */
/* loaded from: classes.dex */
public class C0208DB {
    public static final String DBNAME = "manifest.db";
    private static final String DBPATH = "/openfeint/webui/manifest.db";
    private static final String TAG = "SQL";
    private static final int VERSION = 3;
    public static DataStorageHelperX storeHelper;

    private static boolean removeDB(Context ctx) {
        if (Util.noSdcardPermission()) {
            return ctx.getDatabasePath(DBNAME).delete();
        }
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state)) {
            File sdcard = Environment.getExternalStorageDirectory();
            File db = new File(sdcard, DBPATH);
            return db.delete();
        }
        return ctx.getDatabasePath(DBNAME).delete();
    }

    public static void createDB(Context ctx) {
        if (Util.noSdcardPermission()) {
            storeHelper = new DataStorageHelperX(ctx);
            return;
        }
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state)) {
            File sdcard = Environment.getExternalStorageDirectory();
            storeHelper = new DataStorageHelperX(sdcard.getAbsolutePath() + DBPATH);
        } else {
            storeHelper = new DataStorageHelperX(ctx);
        }
    }

    public static boolean recover(Context ctx) {
        if (storeHelper != null) {
            storeHelper.close();
        }
        boolean success = removeDB(ctx);
        if (success) {
            createDB(ctx);
            return storeHelper != null;
        }
        return success;
    }

    public static void setClientManifestBatch(String[] paths, String[] clientHashes) {
        SQLiteDatabase db = null;
        try {
            if (paths.length == clientHashes.length) {
                try {
                    db = storeHelper.getWritableDatabase();
                    SQLiteStatement statement = db.compileStatement("INSERT OR REPLACE INTO manifest(path, hash) VALUES(?,?)");
                    db.beginTransaction();
                    for (int i = 0; i < paths.length; i++) {
                        statement.bindString(1, paths[i]);
                        statement.bindString(2, clientHashes[i]);
                        statement.execute();
                    }
                    statement.close();
                    db.setTransactionSuccessful();
                } catch (SQLiteDiskIOException e) {
                    WebViewCache.diskError();
                    try {
                        db.endTransaction();
                    } catch (Exception e2) {
                    }
                } catch (Exception e3) {
                    OFLog.m182e(TAG, e3.toString());
                    try {
                        db.endTransaction();
                    } catch (Exception e4) {
                    }
                }
            }
        } finally {
            try {
                db.endTransaction();
            } catch (Exception e5) {
            }
        }
    }

    public static void setClientManifest(String path, String clientHash) {
        try {
            SQLiteDatabase db = storeHelper.getWritableDatabase();
            db.execSQL("INSERT OR REPLACE INTO manifest(path, hash) VALUES(?,?)", new String[]{path, clientHash});
        } catch (SQLiteDiskIOException e) {
            WebViewCache.diskError();
        } catch (Exception e2) {
            OFLog.m182e(TAG, e2.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, 3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 0) {
            db.execSQL("CREATE TABLE IF NOT EXISTS manifest (path TEXT PRIMARY KEY, hash TEXT);");
            oldVersion++;
        }
        if (oldVersion == 1) {
            db.execSQL("CREATE TABLE IF NOT EXISTS store (ID TEXT PRIMARY KEY, VALUE TEXT);");
            oldVersion++;
        }
        if (oldVersion == 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS server_manifest (path TEXT PRIMARY KEY NOT NULL, hash TEXT DEFAULT NULL, is_global INTEGER DEFAULT 0);");
            db.execSQL("CREATE TABLE IF NOT EXISTS dependencies (path TEXT NOT NULL, has_dependency TEXT NOT NULL);");
            oldVersion++;
        }
        if (oldVersion != newVersion) {
            OFLog.m182e(TAG, String.format("Unable to upgrade DB from %d to %d.", Integer.valueOf(oldVersion), Integer.valueOf(newVersion)));
        }
    }

    /* renamed from: com.openfeint.internal.db.DB$DataStorageHelperX */
    public static class DataStorageHelperX extends SQLiteOpenHelperX {
        DataStorageHelperX(Context context) {
            super(new DataStorageHelper(context));
        }

        DataStorageHelperX(String path) {
            super(path, 3);
        }

        @Override // com.openfeint.internal.p003db.SQLiteOpenHelperX
        public void onCreate(SQLiteDatabase db) {
            C0208DB.onCreate(db);
        }

        @Override // com.openfeint.internal.p003db.SQLiteOpenHelperX
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            C0208DB.onUpgrade(db, oldVersion, newVersion);
        }
    }

    /* renamed from: com.openfeint.internal.db.DB$DataStorageHelper */
    public static class DataStorageHelper extends SQLiteOpenHelper {
        DataStorageHelper(Context context) {
            super(context, C0208DB.DBNAME, (SQLiteDatabase.CursorFactory) null, 3);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            C0208DB.onCreate(db);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            C0208DB.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
