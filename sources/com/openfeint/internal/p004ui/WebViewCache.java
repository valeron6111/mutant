package com.openfeint.internal.p004ui;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import com.alawar.mutant.database.DbBuilder;
import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.api.p001ui.Dashboard;
import com.openfeint.internal.InternalSettings;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.Util;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.p003db.C0208DB;
import com.openfeint.internal.request.BaseRequest;
import com.openfeint.internal.request.CacheRequest;
import com.openfeint.internal.request.OrderedArgList;
import com.tapjoy.TapjoyConstants;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/* loaded from: classes.dex */
public class WebViewCache {
    private static final String ManifestRequestKey = "manifest";
    private static final String OPENFEINT_ROOT = "openfeint";
    static final String TAG = "WebViewCache";
    private static final String WEBUI = "webui";
    private static boolean diskError = false;
    static final int kBatchLoaded = 2;
    static final long kBatchRetryDelayMillis = 5000;
    static final int kClientManifestReady = 3;
    static final int kDataLoaded = 1;
    static final int kFinishWhenClientReady = 4;
    static final int kNumBatchRetries = 3;
    static final int kServerManifestReady = 0;
    public static String manifestProductOverride;
    private static String rootPath;
    private static String rootUri;
    static WebViewCache sInstance;
    public static URI serverOverride;
    Context appContext;
    Map<String, String> clientManifest;
    WebViewCacheCallback delegate;
    ManifestData serverManifest;
    boolean loadingFinished = false;
    boolean globalsFinished = false;
    boolean batchesAreBroken = false;
    boolean finishWhenClientManifestDone = false;
    final URI serverURI = getServerURI();
    Set<PathAndCallback> trackedPaths = new HashSet();
    Map<String, ItemAndCallback> trackedItems = new HashMap();
    Set<String> pathsToLoad = new HashSet();
    Set<String> prioritizedPaths = new HashSet();
    Handler mDelayHandler = new Handler();
    Handler mHandler = new Handler() { // from class: com.openfeint.internal.ui.WebViewCache.3
        @Override // android.os.Handler
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case DbBuilder.ID_COLUMN /* 0 */:
                    OFLog.m183i(WebViewCache.TAG, "kServerManifestReady");
                    WebViewCache.this.serverManifest = (ManifestData) msg.obj;
                    WebViewCache.this.triggerUpdates();
                    break;
                case 1:
                    WebViewCache.this.finishItem((String) msg.obj, msg.arg1 > 0);
                    break;
                case 2:
                    WebViewCache.this.finishItems((Set) msg.obj, msg.arg1 > 0);
                    break;
                case 3:
                    WebViewCache.this.clientManifest = (Map) msg.obj;
                    WebViewCache.this.triggerUpdates();
                    break;
                case 4:
                    if (WebViewCache.this.clientManifest != null) {
                        WebViewCache.this.finishWithoutLoading();
                        break;
                    } else {
                        WebViewCache.this.finishWhenClientManifestDone = true;
                        break;
                    }
            }
        }
    };

    public static WebViewCache initialize(Context context) {
        if (sInstance != null) {
            sInstance.finishWithoutLoading();
        }
        sInstance = new WebViewCache(context);
        return sInstance;
    }

    public static void prioritize(String path) {
        sInstance.prioritizeInner(path);
    }

    public static boolean trackPath(String path, WebViewCacheCallback cb) {
        return sInstance.trackPathInner(path, cb);
    }

    public static boolean isLoaded(String path) {
        return sInstance.isLoadedInner(path);
    }

    public final void setRootUriSdcard(File path) {
        final File webui = new File(path, WEBUI);
        boolean copyDefault = !webui.exists();
        if (copyDefault) {
            File noMedia = new File(path, ".nomedia");
            try {
                noMedia.createNewFile();
            } catch (IOException e) {
            }
            if (!webui.mkdirs()) {
                setRootUriInternal();
                return;
            }
        }
        rootPath = webui.getAbsolutePath() + "/";
        rootUri = "file://" + rootPath;
        if (copyDefault) {
            final File baseDir = this.appContext.getFilesDir();
            final File inPhoneWebui = new File(baseDir, WEBUI);
            if (inPhoneWebui.isDirectory()) {
                try {
                    Util.copyFile(this.appContext.getDatabasePath(C0208DB.DBNAME), new File(webui, C0208DB.DBNAME));
                } catch (IOException e2) {
                }
            }
            Thread t = new Thread(new Runnable() { // from class: com.openfeint.internal.ui.WebViewCache.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        if (inPhoneWebui.isDirectory()) {
                            Util.copyDirectory(inPhoneWebui, webui);
                            WebViewCache.this.deleteAll();
                            OFLog.m183i(WebViewCache.TAG, "copy in phone data finish");
                            WebViewCache.this.clientManifestReady();
                        } else {
                            OFLog.m183i(WebViewCache.TAG, "copy from asset");
                            WebViewCache.this.copyDefaultBackground(baseDir);
                        }
                    } catch (IOException e3) {
                        OFLog.m182e(WebViewCache.TAG, e3.getMessage());
                        WebViewCache.this.setRootUriInternal();
                    }
                }
            });
            t.start();
            return;
        }
        clientManifestReady();
        deleteAll();
    }

    public final void setRootUriInternal() {
        OFLog.m182e(TAG, "can't use sdcard");
        final File baseDir = this.appContext.getFilesDir();
        File rootDir = new File(baseDir, WEBUI);
        rootPath = rootDir.getAbsolutePath() + "/";
        rootUri = "file://" + rootPath;
        File inPhoneWebui = new File(baseDir, WEBUI);
        boolean hasInPhoneData = inPhoneWebui.isDirectory();
        if (!hasInPhoneData) {
            Thread t = new Thread(new Runnable() { // from class: com.openfeint.internal.ui.WebViewCache.2
                @Override // java.lang.Runnable
                public void run() {
                    WebViewCache.this.copyDefaultBackground(baseDir);
                }
            });
            t.start();
        } else {
            clientManifestReady();
        }
    }

    public static final String getItemUri(String itemPath) {
        return rootUri + itemPath;
    }

    public static final String getItemAbsolutePath(String itemPath) {
        return rootPath + itemPath;
    }

    public static void start() {
        sInstance.updateExternalStorageState();
        sInstance.sync();
    }

    private static class ManifestItem {
        public Set<String> dependentObjects;
        public String hash;
        public String path;

        ManifestItem(String _path, String _hash) {
            this.path = _path;
            this.hash = _hash;
            this.dependentObjects = new HashSet();
        }

        ManifestItem(ManifestItem item) {
            this.path = item.path;
            this.dependentObjects = new HashSet(item.dependentObjects);
        }
    }

    public static void diskError() {
        diskError = true;
        for (PathAndCallback pathAndCb : sInstance.trackedPaths) {
            pathAndCb.callback.failLoaded();
        }
        sInstance.trackedPaths.clear();
        sInstance.finishWithoutLoading();
    }

    private static class ManifestData {
        Set<String> globals = new HashSet();
        Map<String, ManifestItem> objects = new HashMap();

        ManifestData(SQLiteDatabase db) {
            ManifestItem manifestItem;
            Cursor result = null;
            try {
                try {
                    result = db.rawQuery("SELECT path, hash, is_global FROM server_manifest", null);
                    if (result.getCount() > 0) {
                        result.moveToFirst();
                        do {
                            String path = result.getString(0);
                            String hash = result.getString(1);
                            boolean isGlobal = result.getInt(2) != 0;
                            this.objects.put(path, new ManifestItem(path, hash));
                            if (isGlobal) {
                                this.globals.add(path);
                            }
                        } while (result.moveToNext());
                    }
                    result.close();
                    for (String path2 : this.objects.keySet()) {
                        result = db.rawQuery("SELECT has_dependency FROM dependencies WHERE path = ?", new String[]{path2});
                        if (result.getCount() > 0 && (manifestItem = this.objects.get(path2)) != null) {
                            Set<String> deps = manifestItem.dependentObjects;
                            result.moveToFirst();
                            do {
                                deps.add(result.getString(0));
                            } while (result.moveToNext());
                        }
                        result.close();
                    }
                    try {
                        result.close();
                    } catch (Exception e) {
                    }
                } catch (SQLiteDiskIOException e2) {
                    WebViewCache.diskError();
                } catch (Exception e3) {
                    OFLog.m182e(WebViewCache.TAG, "SQLite exception. " + e3.toString());
                    try {
                        result.close();
                    } catch (Exception e4) {
                    }
                }
            } finally {
                try {
                    result.close();
                } catch (Exception e5) {
                }
            }
        }

        void saveTo(SQLiteDatabase db) {
            try {
                try {
                    db.beginTransaction();
                    db.execSQL("DELETE FROM server_manifest;");
                    db.execSQL("DELETE FROM dependencies;");
                    SQLiteStatement insertIntoManifest = db.compileStatement("INSERT INTO server_manifest(path, hash, is_global) VALUES(?, ?, ?)");
                    SQLiteStatement insertIntoDependencies = db.compileStatement("INSERT INTO dependencies(path, has_dependency) VALUES(?, ?)");
                    for (String path : this.objects.keySet()) {
                        ManifestItem item = this.objects.get(path);
                        insertIntoManifest.bindString(1, path);
                        insertIntoManifest.bindString(2, item.hash);
                        insertIntoManifest.bindLong(3, this.globals.contains(path) ? 1L : 0L);
                        insertIntoManifest.execute();
                        insertIntoDependencies.bindString(1, path);
                        for (String dep : item.dependentObjects) {
                            insertIntoDependencies.bindString(2, dep);
                            insertIntoDependencies.execute();
                        }
                    }
                    db.setTransactionSuccessful();
                    try {
                        db.endTransaction();
                    } catch (Exception e) {
                    }
                } catch (SQLiteDiskIOException e2) {
                    WebViewCache.diskError();
                } catch (Exception e3) {
                    OFLog.m182e(WebViewCache.TAG, "SQLite exception. " + e3.toString());
                    try {
                        db.endTransaction();
                    } catch (Exception e4) {
                    }
                }
            } finally {
                try {
                    db.endTransaction();
                } catch (Exception e5) {
                }
            }
        }

        ManifestData(byte[] stm) throws Exception {
            ManifestItem item;
            String path;
            try {
                InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(stm));
                BufferedReader buffered = new BufferedReader(reader, 8192);
                ManifestItem item2 = null;
                while (true) {
                    try {
                        String line = buffered.readLine();
                        if (line != null) {
                            String line2 = line.trim();
                            if (line2.length() != 0) {
                                switch (line2.charAt(0)) {
                                    case '#':
                                        item = item2;
                                        item2 = item;
                                        break;
                                    case '-':
                                        if (item2 != null) {
                                            item2.dependentObjects.add(line2.substring(1).trim());
                                            item = item2;
                                            item2 = item;
                                            break;
                                        } else {
                                            throw new Exception("Manifest Syntax Error: Dependency without an item");
                                        }
                                    default:
                                        String[] pieces = line2.split(" ");
                                        if (pieces.length >= 2) {
                                            if (pieces[0].charAt(0) == '@') {
                                                path = pieces[0].substring(1);
                                                this.globals.add(path);
                                            } else {
                                                path = pieces[0];
                                            }
                                            item = new ManifestItem(path, pieces[1]);
                                            this.objects.put(path, item);
                                            item2 = item;
                                            break;
                                        } else {
                                            throw new Exception("Manifest Syntax Error: Extra items in line");
                                        }
                                }
                            }
                        } else {
                            return;
                        }
                    } catch (Exception e) {
                        e = e;
                        throw new Exception(e);
                    }
                }
            } catch (Exception e2) {
                e = e2;
            }
        }
    }

    private static class ItemAndCallback {
        public final WebViewCacheCallback callback;
        public final ManifestItem item;

        public ItemAndCallback(ManifestItem _item, WebViewCacheCallback _cb) {
            this.item = _item;
            this.callback = _cb;
        }
    }

    private static class PathAndCallback {
        public final WebViewCacheCallback callback;
        public final String path;

        public PathAndCallback(String _path, WebViewCacheCallback _cb) {
            this.path = _path;
            this.callback = _cb;
        }
    }

    private boolean trackPathInner(String path, WebViewCacheCallback cb) {
        if (this.loadingFinished) {
            cb.pathLoaded(path);
            return false;
        }
        if (this.serverManifest == null) {
            cb.onTrackingNeeded();
            this.trackedPaths.add(new PathAndCallback(path, cb));
            return true;
        }
        ManifestItem loadedItem = this.serverManifest.objects.get(path);
        if (loadedItem != null) {
            cb.onTrackingNeeded();
            ManifestItem newItem = new ManifestItem(loadedItem);
            newItem.dependentObjects.retainAll(this.pathsToLoad);
            this.trackedItems.put(path, new ItemAndCallback(newItem, cb));
            return true;
        }
        cb.pathLoaded(path);
        return false;
    }

    private boolean isLoadedInner(String path) {
        return this.serverManifest == null ? this.loadingFinished : !this.pathsToLoad.contains(path);
    }

    private WebViewCache(Context _appContext) {
        this.appContext = _appContext;
    }

    private void updateExternalStorageState() {
        if (Util.noSdcardPermission()) {
            OFLog.m182e(TAG, "no sdcard permission");
            setRootUriInternal();
            return;
        }
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state)) {
            File sdcard = Environment.getExternalStorageDirectory();
            File feintRoot = new File(sdcard, OPENFEINT_ROOT);
            setRootUriSdcard(feintRoot);
        } else {
            OFLog.m183i(TAG, state);
            setRootUriInternal();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sync() {
        OFLog.m183i(TAG, "--- WebViewCache Sync ---");
        ManifestRequest req = new ManifestRequest(ManifestRequestKey);
        req.launch();
    }

    private class ManifestRequest extends CacheRequest {
        private ManifestData data;

        public ManifestRequest(String key) {
            super(key);
            this.data = null;
        }

        @Override // com.openfeint.internal.request.BaseRequest
        public boolean signed() {
            return false;
        }

        @Override // com.openfeint.internal.request.BaseRequest
        public String path() {
            return WebViewCache.getManifestPath(WebViewCache.this.appContext);
        }

        @Override // com.openfeint.internal.request.BaseRequest
        public void onResponse(int responseCode, byte[] body) {
        }

        @Override // com.openfeint.internal.request.BaseRequest
        public void onResponseOffMainThread(int responseCode, byte[] body) {
            if (responseCode == 200) {
                try {
                    this.data = new ManifestData(body);
                } catch (Exception e) {
                    OFLog.m182e(WebViewCache.TAG, e.toString());
                }
            } else {
                try {
                    this.data = new ManifestData(C0208DB.storeHelper.getReadableDatabase());
                } catch (Exception e2) {
                    OFLog.m182e(WebViewCache.TAG, e2.toString());
                }
            }
            if (this.data == null || this.data.objects.isEmpty()) {
                this.data = null;
                new BaseRequest() { // from class: com.openfeint.internal.ui.WebViewCache.ManifestRequest.1
                    @Override // com.openfeint.internal.request.BaseRequest
                    public String method() {
                        return "GET";
                    }

                    @Override // com.openfeint.internal.request.BaseRequest
                    public String path() {
                        return ManifestRequest.this.path();
                    }

                    @Override // com.openfeint.internal.request.BaseRequest
                    public void onResponse(int responseCode2, byte[] body2) {
                    }

                    @Override // com.openfeint.internal.request.BaseRequest
                    public void onResponseOffMainThread(int responseCode2, byte[] body2) {
                        if (200 != responseCode2) {
                            WebViewCache.this.finishWhenClientManifestReady();
                            return;
                        }
                        try {
                            ManifestRequest.this.data = new ManifestData(body2);
                            ManifestRequest.this.finishManifest();
                            ManifestRequest.this.updateLastModifiedFromResponse(getResponse());
                        } catch (Exception e3) {
                        }
                    }
                }.launch();
            } else {
                finishManifest();
                updateLastModifiedFromResponse(getResponse());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void finishManifest() {
            if (this.data == null) {
                WebViewCache.this.finishWhenClientManifestReady();
                return;
            }
            try {
                this.data.saveTo(C0208DB.storeHelper.getWritableDatabase());
            } catch (Exception e) {
                OFLog.m182e(WebViewCache.TAG, e.toString());
            }
            Message msg = Message.obtain(WebViewCache.this.mHandler, 0, this.data);
            msg.sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteAll() {
        File baseDir = this.appContext.getFilesDir();
        File webui = new File(baseDir, WEBUI);
        Util.deleteFiles(webui);
        this.appContext.getDatabasePath(C0208DB.DBNAME).delete();
    }

    private void gatherDefaultItems(String path, Set<String> items) {
        try {
            String[] stuff = this.appContext.getAssets().list(path);
            for (String s : stuff) {
                String fullpath = path + "/" + s;
                try {
                    InputStream check = this.appContext.getAssets().open(fullpath);
                    items.add(fullpath);
                    check.close();
                } catch (IOException e) {
                    gatherDefaultItems(fullpath, items);
                }
            }
        } catch (IOException e2) {
            OFLog.m182e(TAG, e2.toString());
        }
    }

    private void copySingleItem(File baseDir, String path) {
        try {
            File filePath = new File(baseDir, path);
            InputStream inputStream = this.appContext.getAssets().open(path);
            DataInputStream reader = new DataInputStream(inputStream);
            filePath.getParentFile().mkdirs();
            FileOutputStream fileStream = new FileOutputStream(filePath);
            DataOutputStream writer = new DataOutputStream(fileStream);
            Util.copyStream(reader, writer);
        } catch (Exception e) {
            OFLog.m182e(TAG, e.toString());
        }
    }

    private Set<String> stripUnused(Set<String> table) {
        String currentDpi = Util.getDpiName(this.appContext);
        String test = currentDpi.equals("mdpi") ? ".hdpi." : ".mdpi.";
        Set<String> reducedSet = new HashSet<>();
        for (String path : table) {
            if (!path.contains(test)) {
                reducedSet.add(path);
            }
        }
        return reducedSet;
    }

    private void copySpecific(File baseDir, String path, Set<String> items) {
        if (items.contains(path)) {
            copySingleItem(baseDir, path);
            items.remove(path);
        }
    }

    private void copyDirectory(File baseDir, String root, Set<String> items) {
        Set<String> dirItems = new HashSet<>();
        for (String path : items) {
            if (path.startsWith(root)) {
                dirItems.add(path);
            }
        }
        Iterator i$ = dirItems.iterator();
        while (i$.hasNext()) {
            copySpecific(baseDir, i$.next(), items);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void copyDefaultBackground(File baseDir) {
        Set<String> defaultItems = new HashSet<>();
        gatherDefaultItems(WEBUI, defaultItems);
        Set<String> defaultItems2 = stripUnused(defaultItems);
        copySpecific(baseDir, "webui/manifest.plist", defaultItems2);
        copyDirectory(baseDir, "webui/javascripts/", defaultItems2);
        copyDirectory(baseDir, "webui/stylesheets/", defaultItems2);
        copyDirectory(baseDir, "webui/intro/", defaultItems2);
        if (Util.getDpiName(this.appContext).equals("mdpi")) {
            copySpecific(baseDir, "webui/images/space.grid.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/button.gray.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/button.gray.hit.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/button.green.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/button.green.hit.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/logo.small.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/header_bg.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/loading.spinner.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/input.text.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/frame.small.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/icon.leaf.gray.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/tab.divider.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/tab.active_indicator.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/logo.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/header_bg.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/loading.spinner.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/icon.user.male.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/intro.leaderboards.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/intro.friends.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/intro.achievements.mdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/intro.games.mdpi.png", defaultItems2);
        } else {
            copySpecific(baseDir, "webui/images/space.grid.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/button.gray.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/button.gray.hit.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/button.green.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/button.green.hit.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/logo.small.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/header_bg.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/loading.spinner.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/input.text.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/frame.small.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/icon.leaf.gray.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/tab.divider.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/tab.active_indicator.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/logo.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/header_bg.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/loading.spinner.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/icon.user.male.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/intro.leaderboards.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/intro.friends.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/intro.achievements.hdpi.png", defaultItems2);
            copySpecific(baseDir, "webui/images/intro.games.hdpi.png", defaultItems2);
        }
        boolean delayReadyCall = OpenFeintInternal.getInstance().getSettings().get(InternalSettings.WebUIDelayUntilCopyFinished) != null;
        if (delayReadyCall) {
            for (String path : defaultItems2) {
                copySingleItem(baseDir, path);
            }
            clientManifestReady();
            return;
        }
        clientManifestReady();
        for (String path2 : defaultItems2) {
            copySingleItem(baseDir, path2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clientManifestReady() {
        Map obj = getDefaultClientManifest();
        Message msg = Message.obtain(this.mHandler, 3);
        msg.obj = obj;
        msg.sendToTarget();
    }

    private class SaxHandler extends DefaultHandler {
        String key;
        String loadingString;
        Map<String, String> outputMap;

        private SaxHandler() {
            this.outputMap = new HashMap();
        }

        public Map<String, String> getOutputMap() {
            return this.outputMap;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String uri, String name, String qName, Attributes attr) {
            this.loadingString = MutantMessages.sEmpty;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String uri, String name, String qName) {
            String clipped = name.trim();
            if (!clipped.equals("key")) {
                if (clipped.equals("string")) {
                    this.outputMap.put(this.key, this.loadingString);
                    C0208DB.setClientManifest(this.key, this.loadingString);
                    return;
                }
                return;
            }
            this.key = this.loadingString;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void characters(char[] ch, int start, int length) {
            this.loadingString = new String(ch).substring(start, start + length);
        }
    }

    public static boolean isDiskError() {
        return diskError;
    }

    public static boolean recover() {
        if (diskError) {
            return false;
        }
        return sInstance.recoverInternal();
    }

    void markSyncRequired() {
        this.loadingFinished = false;
        this.globalsFinished = false;
        this.finishWhenClientManifestDone = false;
    }

    boolean recoverInternal() {
        boolean success = C0208DB.recover(this.appContext);
        this.serverManifest = null;
        if (success) {
            this.clientManifest = getDefaultClientManifestFromAsset();
            success = !this.clientManifest.isEmpty();
        }
        markSyncRequired();
        sync();
        return success;
    }

    private Map<String, String> getDefaultClientManifest() {
        Cursor result = null;
        try {
            try {
                SQLiteDatabase db = C0208DB.storeHelper.getReadableDatabase();
                result = db.rawQuery("SELECT * FROM manifest", null);
            } catch (SQLiteDiskIOException e) {
                diskError();
                try {
                    result.close();
                } catch (Exception e2) {
                }
            } catch (Exception e3) {
                OFLog.m182e(TAG, "SQLite exception. " + e3.toString());
                try {
                    result.close();
                } catch (Exception e4) {
                }
            }
            if (result.getCount() <= 0) {
                return getDefaultClientManifestFromAsset();
            }
            Map<String, String> outManifest = new HashMap<>();
            result.moveToFirst();
            do {
                String path = result.getString(0);
                String hash = result.getString(1);
                outManifest.put(path, hash);
            } while (result.moveToNext());
            result.close();
            OFLog.m183i(TAG, "create client Manifest from db");
            try {
                result.close();
                return outManifest;
            } catch (Exception e5) {
                return outManifest;
            }
        } finally {
            try {
                result.close();
            } catch (Exception e6) {
            }
        }
    }

    private Map<String, String> getDefaultClientManifestFromAsset() {
        File manifestFile = new File(rootPath, "manifest.plist");
        if (manifestFile.isFile()) {
            try {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();
                SaxHandler handler = new SaxHandler();
                xr.setContentHandler(handler);
                InputStream inputStream = new FileInputStream(manifestFile.getPath());
                xr.parse(new InputSource(inputStream));
                return handler.getOutputMap();
            } catch (Exception e) {
                OFLog.m182e(TAG, e.toString());
            }
        }
        return new HashMap();
    }

    private static final URI getServerURI() {
        try {
            return serverOverride != null ? serverOverride : new URI(OpenFeintInternal.getInstance().getServerUrl());
        } catch (Exception e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String getManifestPath(Context ctx) {
        String product = manifestProductOverride != null ? manifestProductOverride : "embed";
        return String.format("/webui/manifest/%s.%s.%s", TapjoyConstants.TJC_DEVICE_PLATFORM_TYPE, product, Util.getDpiName(ctx));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void triggerUpdates() {
        OFLog.m183i(TAG, "loadedManifest");
        if (this.serverManifest != null && this.clientManifest != null) {
            for (ManifestItem item : this.serverManifest.objects.values()) {
                if (!item.hash.equals(this.clientManifest.get(item.path))) {
                    this.pathsToLoad.add(item.path);
                }
            }
            loadNextItem();
            return;
        }
        if (this.clientManifest != null && this.finishWhenClientManifestDone) {
            finishWithoutLoading();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishWhenClientManifestReady() {
        Message msg = Message.obtain(this.mHandler, 4, null);
        msg.sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishWithoutLoading() {
        OFLog.m183i(TAG, "finishWithoutLoading");
        finishLoading();
        for (PathAndCallback pathAndCb : this.trackedPaths) {
            pathAndCb.callback.pathLoaded(pathAndCb.path);
        }
        this.trackedPaths.clear();
        this.prioritizedPaths.clear();
        if (this.serverManifest == null) {
            try {
                this.serverManifest = new ManifestData(new byte[0]);
            } catch (Exception e) {
            }
        }
        this.serverManifest.globals.clear();
        this.pathsToLoad.clear();
    }

    private void finishLoading() {
        C0208DB.storeHelper.close();
        this.loadingFinished = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void batchFetch(final String originalUrl, final String currentUrl, final int retriesLeft, final Set<String> paths) {
        new BaseRequest() { // from class: com.openfeint.internal.ui.WebViewCache.4
            @Override // com.openfeint.internal.request.BaseRequest
            public boolean signed() {
                return false;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "GET";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return MutantMessages.sEmpty;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String url() {
                return originalUrl;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public void onResponse(int responseCode, byte[] body) {
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public void onResponseOffMainThread(int responseCode, byte[] body) {
                WebViewCache.this.handleBatchBody(responseCode, body, originalUrl, currentUrl, retriesLeft, paths);
            }
        }.launch();
    }

    private void batchRequest(Set<String> paths) {
        batchRequest(paths, 3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void batchRequest(final Set<String> paths, final int numRetries) {
        OFLog.m183i(TAG, String.format("Syncing %d items", Integer.valueOf(paths.size())));
        OrderedArgList oal = new OrderedArgList();
        for (String s : paths) {
            ManifestItem manifestItem = this.serverManifest.objects.get(s);
            oal.put("files[][path]", manifestItem.path);
            oal.put("files[][hash]", manifestItem.hash);
        }
        new BaseRequest(oal) { // from class: com.openfeint.internal.ui.WebViewCache.5
            @Override // com.openfeint.internal.request.BaseRequest
            public boolean signed() {
                return false;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "POST";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return "/webui/assets";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            protected void onResponseOffMainThread(int responseCode, byte[] body) {
                WebViewCache.this.handleBatchBody(responseCode, body, url(), currentURL(), numRetries, paths);
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public void onResponse(int responseCode, byte[] body) {
            }
        }.launch();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBatchBody(int responseCode, byte[] body, final String originalUrl, final String currentUrl, final int retriesLeft, final Set<String> paths) {
        if (200 <= responseCode && responseCode < 300) {
            processBatch(paths, body);
            return;
        }
        if (302 == responseCode || 303 == responseCode) {
            batchFetch(originalUrl, currentUrl, retriesLeft, paths);
            return;
        }
        if (responseCode == 0 || (400 <= responseCode && responseCode < 500)) {
            if (retriesLeft > 0) {
                this.mDelayHandler.postDelayed(new Runnable() { // from class: com.openfeint.internal.ui.WebViewCache.6
                    @Override // java.lang.Runnable
                    public void run() {
                        if (originalUrl.equals(currentUrl)) {
                            WebViewCache.this.batchRequest(paths, retriesLeft - 1);
                        } else {
                            WebViewCache.this.batchFetch(originalUrl, currentUrl, retriesLeft - 1, paths);
                        }
                    }
                }, kBatchRetryDelayMillis);
                return;
            } else {
                Message msg = Message.obtain(this.mHandler, 2, 0, 0, paths);
                msg.sendToTarget();
                return;
            }
        }
        Message msg2 = Message.obtain(this.mHandler, 2, 0, 0, paths);
        msg2.sendToTarget();
    }

    private void finishGlobals() {
        ManifestItem item;
        for (PathAndCallback pathAndCb : this.trackedPaths) {
            if (!this.pathsToLoad.contains(pathAndCb.path)) {
                pathAndCb.callback.pathLoaded(pathAndCb.path);
            } else {
                ManifestItem newItem = new ManifestItem(this.serverManifest.objects.get(pathAndCb.path));
                newItem.dependentObjects.retainAll(this.pathsToLoad);
                this.trackedItems.put(pathAndCb.path, new ItemAndCallback(newItem, pathAndCb.callback));
            }
        }
        this.trackedPaths.clear();
        Set<String> priorityDependents = new HashSet<>();
        for (String path : this.prioritizedPaths) {
            if (this.pathsToLoad.contains(path) && (item = this.serverManifest.objects.get(path)) != null) {
                priorityDependents.addAll(item.dependentObjects);
            }
        }
        priorityDependents.retainAll(this.pathsToLoad);
        this.prioritizedPaths.addAll(priorityDependents);
        this.globalsFinished = true;
    }

    private void loadNextItem() {
        OFLog.m183i(TAG, "loadNextItem");
        this.serverManifest.globals.retainAll(this.pathsToLoad);
        if (!this.globalsFinished && this.serverManifest.globals.isEmpty()) {
            finishGlobals();
        }
        this.prioritizedPaths.retainAll(this.pathsToLoad);
        int numGlobalsAndPrioritized = this.serverManifest.globals.size() + this.prioritizedPaths.size();
        if (!this.batchesAreBroken && numGlobalsAndPrioritized > 1) {
            Set<String> combinedGlobalsAndPrio = new HashSet<>();
            combinedGlobalsAndPrio.addAll(this.serverManifest.globals);
            combinedGlobalsAndPrio.addAll(this.prioritizedPaths);
            batchRequest(combinedGlobalsAndPrio);
            return;
        }
        if (this.serverManifest.globals.size() > 0) {
            singleRequest(this.serverManifest.globals.iterator().next());
            return;
        }
        if (this.prioritizedPaths.size() > 0) {
            singleRequest(this.prioritizedPaths.iterator().next());
            return;
        }
        if (!this.batchesAreBroken && this.pathsToLoad.size() > 1) {
            batchRequest(this.pathsToLoad);
        } else if (this.pathsToLoad.size() > 0) {
            singleRequest(this.pathsToLoad.iterator().next());
        } else {
            finishLoading();
        }
    }

    private final void singleRequest(final String finalPath) {
        OFLog.m183i(TAG, "Syncing item: " + finalPath);
        new BaseRequest() { // from class: com.openfeint.internal.ui.WebViewCache.7
            @Override // com.openfeint.internal.request.BaseRequest
            public boolean signed() {
                return false;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "GET";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return "/webui/" + finalPath;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public void onResponse(int responseCode, byte[] body) {
                if (responseCode == 200) {
                    try {
                        Util.saveFile(body, WebViewCache.rootPath + finalPath);
                        Message msg = Message.obtain(WebViewCache.this.mHandler, 1, 1, 0, finalPath);
                        msg.sendToTarget();
                        return;
                    } catch (Exception e) {
                        Message msg2 = Message.obtain(WebViewCache.this.mHandler, 1, 0, 0, finalPath);
                        msg2.sendToTarget();
                        return;
                    }
                }
                Message msg3 = Message.obtain(WebViewCache.this.mHandler, 1, 0, 0, finalPath);
                msg3.sendToTarget();
            }
        }.launch();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishItem(String path, boolean succeeded) {
        HashSet<String> tiny = new HashSet<>(1);
        tiny.add(path);
        finishItems(tiny, succeeded, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishItems(Set<String> paths, boolean succeeded) {
        finishItems(paths, succeeded, false);
    }

    private void finishItems(Set<String> paths, boolean succeeded, boolean wasSingular) {
        if (this.serverManifest != null) {
            if (!succeeded && !wasSingular) {
                this.batchesAreBroken = true;
            } else {
                Iterator i$ = this.trackedItems.values().iterator();
                while (i$.hasNext()) {
                    i$.next().item.dependentObjects.removeAll(paths);
                }
                this.pathsToLoad.removeAll(paths);
                this.serverManifest.globals.removeAll(paths);
                this.prioritizedPaths.removeAll(paths);
                if (this.globalsFinished) {
                    HashSet<String> pathsToRemove = new HashSet<>();
                    for (ItemAndCallback itemAndCb : this.trackedItems.values()) {
                        if (!this.pathsToLoad.contains(itemAndCb.item.path) && itemAndCb.item.dependentObjects.size() == 0) {
                            pathsToRemove.add(itemAndCb.item.path);
                            itemAndCb.callback.pathLoaded(itemAndCb.item.path);
                        }
                    }
                    Iterator i$2 = pathsToRemove.iterator();
                    while (i$2.hasNext()) {
                        String removePath = i$2.next();
                        this.trackedItems.remove(removePath);
                    }
                }
                String[] pathsArray = new String[paths.size()];
                String[] hashArray = new String[pathsArray.length];
                int i = 0;
                for (String path : paths) {
                    String hashValue = succeeded ? this.serverManifest.objects.get(path).hash : "INVALID";
                    pathsArray[i] = path;
                    hashArray[i] = hashValue;
                    i++;
                    this.clientManifest.put(path, hashValue);
                }
                C0208DB.setClientManifestBatch(pathsArray, hashArray);
            }
            loadNextItem();
        }
    }

    private void prioritizeInner(String path) {
        ManifestItem item;
        if (!this.loadingFinished) {
            this.prioritizedPaths.add(path);
            if (this.serverManifest != null && (item = this.serverManifest.objects.get(path)) != null) {
                Set<String> loadingDependents = new HashSet<>(item.dependentObjects);
                loadingDependents.retainAll(this.pathsToLoad);
                this.prioritizedPaths.addAll(loadingDependents);
                OFLog.m183i(TAG, "Prioritizing " + path + " deps:" + loadingDependents.toString());
            }
        }
    }

    private void processBatch(Set<String> paths, byte[] body) {
        HashSet<String> fetchedPaths = new HashSet<>();
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(body));
        while (true) {
            try {
                ZipEntry ze = zis.getNextEntry();
                if (ze == null) {
                    break;
                }
                if (!ze.isDirectory()) {
                    String finalPath = ze.getName();
                    Util.saveStreamAndLeaveInputOpen(zis, rootPath + finalPath);
                    fetchedPaths.add(finalPath);
                }
            } catch (Exception e) {
                OFLog.m182e(TAG, e.getMessage());
            }
        }
        if (!fetchedPaths.isEmpty()) {
            Message msg = Message.obtain(this.mHandler, 2, 1, 0, fetchedPaths);
            msg.sendToTarget();
        } else {
            Message msg2 = Message.obtain(this.mHandler, 2, 0, 0, paths);
            msg2.sendToTarget();
        }
    }

    public static class TestOnlyManifestItem {
        public String clientHash;
        public String path;
        public String serverHash;

        public enum Status {
            NotYetDownloaded,
            NotOnServer,
            UpToDate,
            OutOfDate
        }

        public TestOnlyManifestItem(String _path, String _clientHash, String _serverHash) {
            this.path = _path;
            this.clientHash = _clientHash;
            this.serverHash = _serverHash;
        }

        public Status status() {
            return this.clientHash == null ? Status.NotYetDownloaded : this.serverHash == null ? Status.NotOnServer : this.serverHash.equals(this.clientHash) ? Status.UpToDate : Status.OutOfDate;
        }

        public void invalidate() {
            C0208DB.setClientManifest(this.path, "INVALID");
            WebViewCache.sInstance.clientManifest.put(this.path, "INVALID");
            Util.deleteFiles(new File(WebViewCache.rootPath + this.path));
            WebViewCache.sInstance.markSyncRequired();
        }

        public static void syncAndOpenDashboard() {
            if (!WebViewCache.sInstance.loadingFinished) {
                WebViewCache.sInstance.serverManifest = null;
                WebViewCache.sInstance.sync();
            }
            Dashboard.open();
        }
    }

    private static String fullOuterJoin(String fields, String table1, String table2, String condition) {
        String join1 = String.format("SELECT %s from %s LEFT OUTER JOIN %s ON %s", fields, table1, table2, condition);
        String join2 = String.format("SELECT %s from %s LEFT OUTER JOIN %s ON %s", fields, table2, table1, condition);
        return String.format("%s UNION %s;", join1, join2);
    }

    public static TestOnlyManifestItem[] testOnlyManifestItems() {
        SQLiteDatabase db = C0208DB.storeHelper.getReadableDatabase();
        Cursor result = null;
        ArrayList<TestOnlyManifestItem> items = new ArrayList<>();
        try {
            result = db.rawQuery(fullOuterJoin("server_manifest.path, server_manifest.hash, manifest.hash", "server_manifest", ManifestRequestKey, "server_manifest.path = manifest.path"), null);
            if (result.getCount() > 0) {
                result.moveToFirst();
                do {
                    String path = result.getString(0);
                    if (path != null) {
                        String serverHash = result.getString(1);
                        String clientHash = result.getString(2);
                        items.add(new TestOnlyManifestItem(path, clientHash, serverHash));
                    }
                } while (result.moveToNext());
            }
            try {
                result.close();
            } catch (Exception e) {
            }
        } catch (Exception e2) {
            try {
                result.close();
            } catch (Exception e3) {
            }
        } catch (Throwable th) {
            try {
                result.close();
            } catch (Exception e4) {
            }
            throw th;
        }
        TestOnlyManifestItem[] rv = (TestOnlyManifestItem[]) items.toArray(new TestOnlyManifestItem[0]);
        Arrays.sort(rv, new Comparator<TestOnlyManifestItem>() { // from class: com.openfeint.internal.ui.WebViewCache.8
            @Override // java.util.Comparator
            public int compare(TestOnlyManifestItem lhs, TestOnlyManifestItem rhs) {
                return lhs.path.compareTo(rhs.path);
            }
        });
        return rv;
    }
}
