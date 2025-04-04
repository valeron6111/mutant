package com.openfeint.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.openfeint.internal.logcat.OFLog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* loaded from: classes.dex */
public class SyncedStore {
    private static final String FILENAME = "of_prefs";
    private static final String TAG = "DistributedPrefs";
    private Context mContext;
    private HashMap<String, String> mMap = new HashMap<>();
    private ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();

    public class Editor {
        public Editor() {
        }

        public void putString(String k, String v) {
            SyncedStore.this.mMap.put(k, v);
        }

        public void remove(String k) {
            SyncedStore.this.mMap.remove(k);
        }

        public Set<String> keySet() {
            return new HashSet(SyncedStore.this.mMap.keySet());
        }

        public void commit() {
            SyncedStore.this.save();
            SyncedStore.this.mLock.writeLock().unlock();
        }
    }

    Editor edit() {
        this.mLock.writeLock().lock();
        return new Editor();
    }

    public class Reader {
        public Reader() {
        }

        public String getString(String k, String defValue) {
            String rv = (String) SyncedStore.this.mMap.get(k);
            return rv != null ? rv : defValue;
        }

        public Set<String> keySet() {
            return SyncedStore.this.mMap.keySet();
        }

        public void complete() {
            SyncedStore.this.mLock.readLock().unlock();
        }
    }

    Reader read() {
        this.mLock.readLock().lock();
        return new Reader();
    }

    public SyncedStore(Context c) {
        this.mContext = c;
        load();
    }

    public void load() {
        this.mMap = null;
        boolean mustSaveAfterLoad = false;
        long start = System.currentTimeMillis();
        File myStore = this.mContext.getFileStreamPath(FILENAME);
        this.mLock.writeLock().lock();
        try {
            PackageManager packageManager = this.mContext.getPackageManager();
            List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
            ApplicationInfo myInfo = null;
            Iterator i$ = apps.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                ApplicationInfo ai = i$.next();
                if (ai.packageName.equals(this.mContext.getPackageName())) {
                    myInfo = ai;
                    break;
                }
            }
            String myStoreCPath = myStore.getCanonicalPath();
            if (myInfo != null && myStoreCPath.startsWith(myInfo.dataDir)) {
                String underDataDir = myStoreCPath.substring(myInfo.dataDir.length());
                Iterator i$2 = apps.iterator();
                while (i$2.hasNext()) {
                    File otherStore = new File(i$2.next().dataDir, underDataDir);
                    if (myStore.lastModified() < otherStore.lastModified()) {
                        mustSaveAfterLoad = true;
                        myStore = otherStore;
                    }
                }
                this.mMap = mapFromStore(myStore);
            }
            if (this.mMap == null) {
                this.mMap = new HashMap<>();
            }
        } catch (IOException e) {
            OFLog.m182e(TAG, "broken");
        } finally {
            this.mLock.writeLock().unlock();
        }
        if (mustSaveAfterLoad) {
            save();
        }
        long elapsed = System.currentTimeMillis() - start;
        OFLog.m181d(TAG, "Loading prefs took " + new Long(elapsed).toString() + " millis");
    }

    private HashMap<String, String> mapFromStore(File myStore) {
        InputStream is;
        ObjectInputStream ois;
        Object o;
        InputStream is2 = null;
        ObjectInputStream ois2 = null;
        try {
            try {
                is = new FileInputStream(myStore);
                try {
                    ois = new ObjectInputStream(is);
                } catch (FileNotFoundException e) {
                    is2 = is;
                } catch (StreamCorruptedException e2) {
                    is2 = is;
                } catch (IOException e3) {
                    is2 = is;
                } catch (ClassNotFoundException e4) {
                    is2 = is;
                } catch (Throwable th) {
                    th = th;
                    is2 = is;
                }
                try {
                    o = ois.readObject();
                } catch (FileNotFoundException e5) {
                    ois2 = ois;
                    is2 = is;
                    OFLog.m182e(TAG, "Couldn't open of_prefs");
                    try {
                    } catch (IOException e6) {
                        OFLog.m182e(TAG, "IOException while cleaning up");
                    }
                    if (ois2 == null) {
                        if (is2 != null) {
                            is2.close();
                        }
                        return null;
                    }
                    ois2.close();
                    return null;
                } catch (StreamCorruptedException e7) {
                    ois2 = ois;
                    is2 = is;
                    OFLog.m182e(TAG, "StreamCorruptedException");
                    try {
                    } catch (IOException e8) {
                        OFLog.m182e(TAG, "IOException while cleaning up");
                    }
                    if (ois2 == null) {
                        if (is2 != null) {
                            is2.close();
                        }
                        return null;
                    }
                    ois2.close();
                    return null;
                } catch (IOException e9) {
                    ois2 = ois;
                    is2 = is;
                    OFLog.m182e(TAG, "IOException while reading");
                    try {
                    } catch (IOException e10) {
                        OFLog.m182e(TAG, "IOException while cleaning up");
                    }
                    if (ois2 == null) {
                        if (is2 != null) {
                            is2.close();
                        }
                        return null;
                    }
                    ois2.close();
                    return null;
                } catch (ClassNotFoundException e11) {
                    ois2 = ois;
                    is2 = is;
                    OFLog.m182e(TAG, "ClassNotFoundException");
                    try {
                    } catch (IOException e12) {
                        OFLog.m182e(TAG, "IOException while cleaning up");
                    }
                    if (ois2 == null) {
                        if (is2 != null) {
                            is2.close();
                        }
                        return null;
                    }
                    ois2.close();
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    ois2 = ois;
                    is2 = is;
                    try {
                    } catch (IOException e13) {
                        OFLog.m182e(TAG, "IOException while cleaning up");
                    }
                    if (ois2 == null) {
                        if (is2 != null) {
                            is2.close();
                        }
                        throw th;
                    }
                    ois2.close();
                    throw th;
                }
            } catch (FileNotFoundException e14) {
            } catch (StreamCorruptedException e15) {
            } catch (IOException e16) {
            } catch (ClassNotFoundException e17) {
            }
            if (o != null && (o instanceof HashMap)) {
                HashMap<String, String> hashMap = (HashMap) o;
                try {
                } catch (IOException e18) {
                    OFLog.m182e(TAG, "IOException while cleaning up");
                }
                if (ois == null) {
                    if (is != null) {
                        is.close();
                    }
                    return hashMap;
                }
                ois.close();
                return hashMap;
            }
            try {
            } catch (IOException e19) {
                OFLog.m182e(TAG, "IOException while cleaning up");
                ois2 = ois;
                is2 = is;
            }
            if (ois == null) {
                if (is != null) {
                    is.close();
                }
                ois2 = ois;
                is2 = is;
                return null;
            }
            ois.close();
            ois2 = ois;
            is2 = is;
            return null;
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public void save() {
        OutputStream os = null;
        ObjectOutputStream oos = null;
        this.mLock.readLock().lock();
        try {
            try {
                os = this.mContext.openFileOutput(FILENAME, 1);
                ObjectOutputStream oos2 = new ObjectOutputStream(os);
                try {
                    oos2.writeObject(this.mMap);
                    try {
                    } catch (IOException e) {
                        OFLog.m182e(TAG, "IOException while cleaning up");
                    } finally {
                    }
                    if (oos2 == null) {
                        if (os != null) {
                            os.close();
                        }
                    }
                    oos2.close();
                } catch (IOException e2) {
                    oos = oos2;
                    OFLog.m182e(TAG, "Couldn't open of_prefs for writing");
                    try {
                        if (oos == null) {
                            if (os != null) {
                                os.close();
                            }
                        }
                        oos.close();
                    } catch (IOException e3) {
                        OFLog.m182e(TAG, "IOException while cleaning up");
                    } finally {
                    }
                } catch (Throwable th) {
                    th = th;
                    oos = oos2;
                    try {
                    } catch (IOException e4) {
                        OFLog.m182e(TAG, "IOException while cleaning up");
                    } finally {
                    }
                    if (oos == null) {
                        if (os != null) {
                            os.close();
                        }
                        throw th;
                    }
                    oos.close();
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e5) {
        }
    }
}
