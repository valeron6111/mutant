package com.openfeint.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.CookieManager;
import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.api.Notification;
import com.openfeint.api.OpenFeintDelegate;
import com.openfeint.api.OpenFeintSettings;
import com.openfeint.api.resource.CurrentUser;
import com.openfeint.api.resource.User;
import com.openfeint.internal.SyncedStore;
import com.openfeint.internal.analytics.AnalyticsManager;
import com.openfeint.internal.eventlog.EventLogDispatcher;
import com.openfeint.internal.eventlog.SDKEventListener;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.notifications.SimpleNotification;
import com.openfeint.internal.notifications.TwoLineNotification;
import com.openfeint.internal.offline.OfflineSupport;
import com.openfeint.internal.p003db.C0208DB;
import com.openfeint.internal.p004ui.IntroFlow;
import com.openfeint.internal.p004ui.WebViewCache;
import com.openfeint.internal.request.BaseRequest;
import com.openfeint.internal.request.BlobPostRequest;
import com.openfeint.internal.request.Client;
import com.openfeint.internal.request.GenericRequest;
import com.openfeint.internal.request.IRawRequestDelegate;
import com.openfeint.internal.request.JSONRequest;
import com.openfeint.internal.request.OrderedArgList;
import com.openfeint.internal.request.RawRequest;
import com.openfeint.internal.request.multipart.ByteArrayPartSource;
import com.openfeint.internal.request.multipart.FilePartSource;
import com.openfeint.internal.request.multipart.PartSource;
import com.openfeint.internal.resource.BlobUploadParameters;
import com.openfeint.internal.resource.Device;
import com.openfeint.internal.resource.ServerException;
import com.openfeint.internal.vendor.org.apache.commons.codec.binary.Hex;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonFactory;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.tapjoy.TapjoyConstants;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import oauth.signpost.OAuth;
import org.apache.http.impl.client.AbstractHttpClient;

/* loaded from: classes.dex */
public class OpenFeintInternal {
    private static final String TAG = "OpenFeintInternal";
    private static String createUserSource;
    private static OpenFeintInternal sInstance;
    private static String userEnableSource;
    String mAdServerUrl;
    int mAppVersion = -1;
    private boolean mApproved;
    private boolean mBanned;
    Client mClient;
    private Context mContext;
    private boolean mCreatingDeviceSession;
    private CurrentUser mCurrentUser;
    private boolean mCurrentlyLoggingIn;
    private boolean mDeclined;
    OpenFeintDelegate mDelegate;
    private boolean mDeserializedAlready;
    private boolean mDeviceSessionCreated;
    private String mGSDI;
    Properties mInternalProperties;
    private LoginDelegate mLoginDelegate;
    Handler mMainThreadHandler;
    private boolean mParentalControlsEnabled;
    private Runnable mPostDeviceSessionRunnable;
    private Runnable mPostLoginRunnable;
    private SyncedStore mPrefs;
    private List<Runnable> mQueuedPostDeviceSessionRunnables;
    private List<Runnable> mQueuedPostLoginRunnables;
    String mServerUrl;
    private String mSessionID;
    private Date mSessionStartDate;
    OpenFeintSettings mSettings;
    private String mUDID;

    public interface IUploadDelegate {
        void fileUploadedTo(String str, boolean z);
    }

    public interface LoginDelegate {
        void login(User user);
    }

    public void setLoginDelegate(LoginDelegate delegate) {
        this.mLoginDelegate = delegate;
    }

    private void _saveInstanceState(Bundle outState) {
        if (this.mCurrentUser != null) {
            outState.putString("mCurrentUser", this.mCurrentUser.generate());
        }
        if (this.mClient != null) {
            this.mClient.saveInstanceState(outState);
        }
        outState.putBoolean("mCurrentlyLoggingIn", this.mCurrentlyLoggingIn);
        outState.putBoolean("mCreatingDeviceSession", this.mCreatingDeviceSession);
        outState.putBoolean("mDeviceSessionCreated", this.mDeviceSessionCreated);
        outState.putBoolean("mBanned", this.mBanned);
        outState.putBoolean("mParentalControlsEnabled", this.mParentalControlsEnabled);
        outState.putBoolean("mApproved", this.mApproved);
        outState.putBoolean("mDeclined", this.mDeclined);
    }

    private void _restoreInstanceState(Bundle inState) {
        if (!this.mDeserializedAlready && inState != null) {
            this.mCurrentUser = (CurrentUser) userFromString(inState.getString("mCurrentUser"));
            if (this.mClient != null) {
                this.mClient.restoreInstanceState(inState);
            }
            this.mCurrentlyLoggingIn = inState.getBoolean("mCurrentlyLoggingIn");
            this.mCreatingDeviceSession = inState.getBoolean("mCreatingDeviceSession");
            this.mDeviceSessionCreated = inState.getBoolean("mDeviceSessionCreated");
            this.mBanned = inState.getBoolean("mBanned");
            this.mParentalControlsEnabled = inState.getBoolean("mParentalControlsEnabled");
            this.mApproved = inState.getBoolean("mApproved");
            this.mDeclined = inState.getBoolean("mDeclined");
            this.mDeserializedAlready = true;
        }
    }

    public static void saveInstanceState(Bundle outState) {
        getInstance()._saveInstanceState(outState);
    }

    public static void restoreInstanceState(Bundle inState) {
        getInstance()._restoreInstanceState(inState);
    }

    public static OpenFeintInternal getInstance(OpenFeintSettings settings, Context ctx) {
        if (sInstance == null) {
            sInstance = new OpenFeintInternal(settings, ctx);
        }
        return sInstance;
    }

    public static OpenFeintInternal getInstance() {
        return sInstance;
    }

    public OpenFeintDelegate getDelegate() {
        return this.mDelegate;
    }

    public AbstractHttpClient getClient() {
        return this.mClient;
    }

    public SyncedStore getPrefs() {
        if (this.mPrefs == null) {
            this.mPrefs = new SyncedStore(getContext());
        }
        return this.mPrefs;
    }

    private void saveUser(SyncedStore.Editor e, User u) {
        e.putString("last_logged_in_user", u.generate());
    }

    private void clearUser(SyncedStore.Editor e) {
        e.remove("last_logged_in_user");
    }

    private User loadUser() {
        SyncedStore.Reader r = getPrefs().read();
        try {
            String urep = r.getString("last_logged_in_user", null);
            r.complete();
            return userFromString(urep);
        } catch (Throwable th) {
            r.complete();
            throw th;
        }
    }

    private static User userFromString(String urep) {
        if (urep == null) {
            return null;
        }
        try {
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jp = jsonFactory.createJsonParser(new ByteArrayInputStream(urep.getBytes()));
            JsonResourceParser jrp = new JsonResourceParser(jp);
            Object responseBody = jrp.parse();
            if (responseBody != null && (responseBody instanceof User)) {
                return (User) responseBody;
            }
        } catch (IOException e) {
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void userLoggedIn(User loggedInUser) {
        this.mCurrentUser = new CurrentUser();
        this.mCurrentUser.shallowCopyAncestorType(loggedInUser);
        EventLogDispatcher.getInstance().postEvent(EventLogDispatcher.LOGIN_SUCESS, null);
        User lliu = lastLoggedInUser();
        if (lliu == null || !lliu.resourceID().equals(loggedInUser.resourceID())) {
            CookieManager.getInstance().removeAllCookie();
        }
        SyncedStore.Editor e = getPrefs().edit();
        try {
            e.putString("last_logged_in_server", getServerUrl());
            saveUserApproval(e);
            saveUser(e, loggedInUser);
            e.commit();
            if (this.mDelegate != null) {
                this.mDelegate.userLoggedIn(this.mCurrentUser);
            }
            if (this.mLoginDelegate != null) {
                this.mLoginDelegate.login(this.mCurrentUser);
            }
            if (this.mPostLoginRunnable != null) {
                this.mMainThreadHandler.post(this.mPostLoginRunnable);
                this.mPostLoginRunnable = null;
            }
            OfflineSupport.setUserID(loggedInUser.resourceID());
        } catch (Throwable th) {
            e.commit();
            throw th;
        }
    }

    private void userLoggedOut() {
        User previousLocalUser = this.mCurrentUser;
        this.mCurrentUser = null;
        this.mDeviceSessionCreated = false;
        clearPrefs();
        if (this.mDelegate != null) {
            this.mDelegate.userLoggedOut(previousLocalUser);
        }
        OfflineSupport.setUserDeclined();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearPrefs() {
        SyncedStore.Editor e = getPrefs().edit();
        try {
            e.remove("last_logged_in_server");
            e.remove("last_logged_in_user_name");
            clearUser(e);
        } finally {
            e.commit();
        }
    }

    public void createUser(String userName, String email, String password, String passwordConfirmation, IRawRequestDelegate delegate) {
        OrderedArgList bootstrapArgs = new OrderedArgList();
        bootstrapArgs.put("user[name]", userName);
        bootstrapArgs.put("user[http_basic_credential_attributes][email]", email);
        bootstrapArgs.put("user[http_basic_credential_attributes][password]", password);
        bootstrapArgs.put("user[http_basic_credential_attributes][password_confirmation]", passwordConfirmation);
        RawRequest userCreate = new RawRequest(bootstrapArgs) { // from class: com.openfeint.internal.OpenFeintInternal.1
            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "POST";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return "/xp/users.json";
            }

            @Override // com.openfeint.internal.request.JSONRequest
            protected void onResponse(int responseCode, Object responseBody) {
                if (200 <= responseCode && responseCode < 300 && (responseBody == null || !(responseBody instanceof ServerException))) {
                    OFLog.m182e(TAG, "new user or enable of: response code:" + responseCode);
                    EventLogDispatcher.getInstance().postEvent(responseCode == 201 ? EventLogDispatcher.NEW_USER : EventLogDispatcher.ENABLED_OF, OpenFeintInternal.createUserSource);
                    OpenFeintInternal.this.userLoggedIn((User) responseBody);
                    return;
                }
                onFailure(responseBody);
            }
        };
        userCreate.setDelegate(delegate);
        _makeRequest(userCreate);
    }

    public String getLocaleString() {
        return this.mContext.getResources().getConfiguration().locale.toString();
    }

    public String getCountryString() {
        return this.mContext.getResources().getConfiguration().locale.getCountry();
    }

    public static String getModelString() {
        return "p(" + Build.PRODUCT + ")/m(" + Build.MODEL + ")";
    }

    public static String getOSVersionString() {
        return "v" + Build.VERSION.RELEASE + " (" + Build.VERSION.INCREMENTAL + ")";
    }

    public static String getScreenInfo() {
        DisplayMetrics metrics = Util.getDisplayMetrics();
        return String.format("%dx%d (%f dpi)", Integer.valueOf(metrics.widthPixels), Integer.valueOf(metrics.heightPixels), Float.valueOf(metrics.density));
    }

    public static String getProcessorInfo() {
        String family = "unknown";
        try {
            Iterator i$ = cat("/proc/cpuinfo").iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                String l = i$.next();
                if (l.startsWith("Processor\t")) {
                    family = l.split(":")[1].trim();
                    break;
                }
            }
            return String.format("family(%s) min(%s) max(%s)", family, cat("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq").get(0), cat("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq").get(0));
        } catch (Exception e) {
            return "family(unknown) min(unknown) max(unknown)";
        }
    }

    private static List<String> cat(String filename) {
        List<String> rv = new ArrayList<>();
        try {
            FileInputStream f = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(f), 8192);
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                rv.add(line);
            }
            br.close();
        } catch (Exception e) {
        }
        return rv;
    }

    public Map<String, Object> getDeviceParams() {
        HashMap<String, Object> device = new HashMap<>();
        device.put("identifier", getUDID());
        device.put("hardware", getModelString());
        device.put("os", getOSVersionString());
        device.put("screen_resolution", getScreenInfo());
        device.put("processor", getProcessorInfo());
        return device;
    }

    public boolean parentalControlsEnabled() {
        return this.mParentalControlsEnabled;
    }

    public void setParentalControlsEnabled(boolean parentalControlsEnabled) {
        this.mParentalControlsEnabled = parentalControlsEnabled;
    }

    public void createDeviceSession() {
        if (!this.mCreatingDeviceSession && !this.mDeviceSessionCreated) {
            HashMap<String, Object> argMap = new HashMap<>();
            argMap.put(TapjoyConstants.TJC_PLATFORM, TapjoyConstants.TJC_DEVICE_PLATFORM_TYPE);
            argMap.put("device", getDeviceParams());
            argMap.put("of-version", getOFVersion());
            argMap.put("game_version", Integer.toString(getAppVersion()));
            argMap.put("protocol_version", OAuth.VERSION_1_0);
            argMap.put("gsdi", getGSDI());
            OrderedArgList args = new OrderedArgList(argMap);
            this.mCreatingDeviceSession = true;
            RawRequest deviceSession = new RawRequest(args) { // from class: com.openfeint.internal.OpenFeintInternal.2
                @Override // com.openfeint.internal.request.BaseRequest
                public String method() {
                    return "POST";
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String path() {
                    return "/xp/devices";
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public boolean needsDeviceSession() {
                    return false;
                }

                @Override // com.openfeint.internal.request.JSONRequest
                public void onResponse(int responseCode, Object responseBody) {
                    boolean z = false;
                    OpenFeintInternal.this.mCreatingDeviceSession = false;
                    if (200 > responseCode || responseCode >= 300) {
                        OpenFeintInternal.this.mPostLoginRunnable = null;
                        OpenFeintInternal.this.showOfflineNotification(responseCode, responseBody);
                    } else {
                        if (responseBody instanceof Device) {
                            Device d = (Device) responseBody;
                            OpenFeintInternal openFeintInternal = OpenFeintInternal.this;
                            if (d.parental_control != null && d.parental_control.enabled) {
                                z = true;
                            }
                            openFeintInternal.mParentalControlsEnabled = z;
                        }
                        OpenFeintInternal.this.mDeviceSessionCreated = true;
                        if (OpenFeintInternal.this.mPostDeviceSessionRunnable != null) {
                            OFLog.m183i(TAG, "Launching post-device-session runnable now.");
                            OpenFeintInternal.this.mMainThreadHandler.post(OpenFeintInternal.this.mPostDeviceSessionRunnable);
                        }
                    }
                    if (OpenFeintInternal.this.mQueuedPostDeviceSessionRunnables != null) {
                        for (Runnable r : OpenFeintInternal.this.mQueuedPostDeviceSessionRunnables) {
                            OpenFeintInternal.this.mMainThreadHandler.post(r);
                        }
                    }
                    OpenFeintInternal.this.mPostDeviceSessionRunnable = null;
                    OpenFeintInternal.this.mQueuedPostDeviceSessionRunnables = null;
                }
            };
            _makeRequest(deviceSession);
        }
    }

    public final void runOnUiThread(Runnable action) {
        this.mMainThreadHandler.post(action);
    }

    public void loginUser(final String userName, final String password, final String userID, final IRawRequestDelegate delegate) {
        if (!checkBan()) {
            if (this.mCreatingDeviceSession || !this.mDeviceSessionCreated) {
                if (!this.mCreatingDeviceSession) {
                    createDeviceSession();
                }
                OFLog.m183i(TAG, "No device session yet - queueing login.");
                this.mPostDeviceSessionRunnable = new Runnable() { // from class: com.openfeint.internal.OpenFeintInternal.3
                    @Override // java.lang.Runnable
                    public void run() {
                        OpenFeintInternal.this.loginUser(userName, password, userID, delegate);
                    }
                };
                return;
            }
            boolean allowToast = true;
            OrderedArgList bootstrapArgs = new OrderedArgList();
            if (userName != null && password != null) {
                bootstrapArgs.put("login", userName);
                bootstrapArgs.put("password", password);
                allowToast = false;
            }
            if (userID != null && password != null) {
                bootstrapArgs.put("user_id", userID);
                bootstrapArgs.put("password", password);
                allowToast = false;
            }
            this.mCurrentlyLoggingIn = true;
            final boolean finalToast = allowToast;
            RawRequest userLogin = new RawRequest(bootstrapArgs) { // from class: com.openfeint.internal.OpenFeintInternal.4
                @Override // com.openfeint.internal.request.BaseRequest
                public String method() {
                    return "POST";
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String path() {
                    return "/xp/sessions.json";
                }

                @Override // com.openfeint.internal.request.JSONRequest
                public void onResponse(int responseCode, Object responseBody) {
                    OpenFeintInternal.this.mCurrentlyLoggingIn = false;
                    if (200 <= responseCode && responseCode < 300) {
                        OpenFeintInternal.this.userLoggedIn((User) responseBody);
                        if (OpenFeintInternal.this.mPostLoginRunnable != null) {
                            OFLog.m183i(TAG, "Launching post-login runnable now.");
                            OpenFeintInternal.this.mMainThreadHandler.post(OpenFeintInternal.this.mPostLoginRunnable);
                        }
                    } else if (finalToast) {
                        OpenFeintInternal.this.showOfflineNotification(responseCode, responseBody);
                    }
                    if (OpenFeintInternal.this.mQueuedPostLoginRunnables != null) {
                        for (Runnable r : OpenFeintInternal.this.mQueuedPostLoginRunnables) {
                            OpenFeintInternal.this.mMainThreadHandler.post(r);
                        }
                    }
                    OpenFeintInternal.this.mPostLoginRunnable = null;
                    OpenFeintInternal.this.mQueuedPostLoginRunnables = null;
                }
            };
            userLogin.setDelegate(delegate);
            _makeRequest(userLogin);
        }
    }

    public void submitIntent(final Intent intent, boolean spotlight) {
        this.mDeclined = false;
        Runnable r = new Runnable() { // from class: com.openfeint.internal.OpenFeintInternal.5
            @Override // java.lang.Runnable
            public void run() {
                intent.addFlags(268435456);
                OpenFeintInternal.this.getContext().startActivity(intent);
            }
        };
        if (!isUserLoggedIn()) {
            OFLog.m183i(TAG, "Not logged in yet - queueing intent " + intent.toString() + " for now.");
            this.mPostLoginRunnable = r;
            if (!currentlyLoggingIn()) {
                login(spotlight);
                return;
            }
            return;
        }
        this.mMainThreadHandler.post(r);
    }

    public void logoutUser(IRawRequestDelegate delegate) {
        OrderedArgList bootstrapArgs = new OrderedArgList();
        bootstrapArgs.put(TapjoyConstants.TJC_PLATFORM, TapjoyConstants.TJC_DEVICE_PLATFORM_TYPE);
        RawRequest userLogout = new RawRequest(bootstrapArgs) { // from class: com.openfeint.internal.OpenFeintInternal.6
            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "DELETE";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return "/xp/sessions.json";
            }
        };
        userLogout.setDelegate(delegate);
        _makeRequest(userLogout);
        userLoggedOut();
    }

    public static void genericRequest(String path, String method, Map<String, Object> args, Map<String, Object> httpParams, IRawRequestDelegate delegate) {
        makeRequest(new GenericRequest(path, method, args, httpParams, delegate));
    }

    public void userApprovedFeint() {
        this.mApproved = true;
        this.mDeclined = false;
        EventLogDispatcher.getInstance().postEvent(EventLogDispatcher.ACCEPTED_OF, null);
        SyncedStore.Editor e = getPrefs().edit();
        try {
            saveUserApproval(e);
            e.commit();
            launchIntroFlow(false);
            OfflineSupport.setUserTemporary();
        } catch (Throwable th) {
            e.commit();
            throw th;
        }
    }

    private void saveUserApproval(SyncedStore.Editor e) {
        e.remove(getContext().getPackageName() + ".of_declined");
    }

    public void userDeclinedFeint() {
        this.mApproved = false;
        this.mDeclined = true;
        EventLogDispatcher.getInstance().postEvent(EventLogDispatcher.DECLINED_OF, null);
        SyncedStore.Editor e = getPrefs().edit();
        try {
            e.putString(getContext().getPackageName() + ".of_declined", "sadly");
            e.commit();
            OfflineSupport.setUserDeclined();
        } catch (Throwable th) {
            e.commit();
            throw th;
        }
    }

    public boolean currentlyLoggingIn() {
        return this.mCurrentlyLoggingIn || this.mCreatingDeviceSession;
    }

    public CurrentUser getCurrentUser() {
        return this.mCurrentUser;
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public String getUDID() {
        if (this.mUDID == null) {
            this.mUDID = findUDID();
        }
        return this.mUDID;
    }

    public String getGSDI() {
        if (this.mGSDI == null) {
            this.mGSDI = findGSDI();
        }
        return this.mGSDI;
    }

    public Properties getInternalProperties() {
        return this.mInternalProperties;
    }

    private String normalizeUrl(String raw) {
        String raw2 = raw.toLowerCase().trim();
        if (raw2.endsWith("/")) {
            return raw2.substring(0, raw2.length() - 1);
        }
        return raw2;
    }

    public String getAdServerUrl() {
        if (this.mAdServerUrl == null) {
            this.mAdServerUrl = normalizeUrl(getInternalProperties().getProperty("ad-server-url"));
        }
        return this.mAdServerUrl;
    }

    public String getServerUrl() {
        if (this.mServerUrl == null) {
            this.mServerUrl = normalizeUrl(getInternalProperties().getProperty("server-url"));
        }
        return this.mServerUrl;
    }

    public String getOFVersion() {
        return getInternalProperties().getProperty("of-version");
    }

    public String getAppName() {
        return this.mSettings.name;
    }

    public String getAppID() {
        return this.mSettings.f268id;
    }

    public Map<String, Object> getSettings() {
        return this.mSettings.settings;
    }

    public int getAppVersion() {
        if (this.mAppVersion == -1) {
            Context c = getContext();
            try {
                PackageInfo p = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
                this.mAppVersion = p.versionCode;
            } catch (Exception e) {
                this.mAppVersion = 0;
            }
        }
        return this.mAppVersion;
    }

    public Context getContext() {
        return this.mContext;
    }

    public void displayErrorDialog(CharSequence errorMessage) {
        SimpleNotification.show(errorMessage.toString(), Notification.Category.Foreground, Notification.Type.Error);
    }

    private String findGSDI() {
        String tUDID = getUDID();
        String tAppID = getAppID();
        String raw = String.format("OFGSDI.%s.%s", tUDID, tAppID);
        return Util.hexSHA1(raw);
    }

    private String findUDID() {
        String androidID = Settings.Secure.getString(getContext().getContentResolver(), TapjoyConstants.TJC_ANDROID_ID);
        if (androidID != null && !androidID.equals("9774d56d682e549c")) {
            return "android-id-" + androidID;
        }
        SyncedStore.Reader r = getPrefs().read();
        try {
            String androidID2 = r.getString(TapjoyConstants.TJC_DEVICE_ID_NAME, null);
            if (androidID2 == null) {
                byte[] randomBytes = new byte[16];
                new Random().nextBytes(randomBytes);
                androidID2 = "android-emu-" + new String(Hex.encodeHex(randomBytes)).replace("\r\n", MutantMessages.sEmpty);
                SyncedStore.Editor e = getPrefs().edit();
                try {
                    e.putString(TapjoyConstants.TJC_DEVICE_ID_NAME, androidID2);
                } finally {
                    e.commit();
                }
            }
            return androidID2;
        } finally {
            r.complete();
        }
    }

    public static void makeRequest(BaseRequest req) {
        OpenFeintInternal ofi = getInstance();
        if (ofi == null) {
            ServerException e = new ServerException();
            e.exceptionClass = "NoFeint";
            e.message = "OpenFeint has not been initialized.";
            req.onResponse(0, e.generate().getBytes());
            return;
        }
        ofi._makeRequest(req);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void _makeRequest(final BaseRequest req) {
        if (!isUserLoggedIn() && req.wantsLogin() && lastLoggedInUser() != null && isFeintServerReachable()) {
            login(false);
            if (this.mQueuedPostLoginRunnables == null) {
                this.mQueuedPostLoginRunnables = new ArrayList();
            }
            this.mQueuedPostLoginRunnables.add(new Runnable() { // from class: com.openfeint.internal.OpenFeintInternal.7
                @Override // java.lang.Runnable
                public void run() {
                    OpenFeintInternal.this.mClient.makeRequest(req);
                }
            });
            return;
        }
        if (!this.mDeviceSessionCreated && req.needsDeviceSession()) {
            createDeviceSession();
            if (this.mQueuedPostDeviceSessionRunnables == null) {
                this.mQueuedPostDeviceSessionRunnables = new ArrayList();
            }
            this.mQueuedPostDeviceSessionRunnables.add(new Runnable() { // from class: com.openfeint.internal.OpenFeintInternal.8
                @Override // java.lang.Runnable
                public void run() {
                    OpenFeintInternal.this.mClient.makeRequest(req);
                }
            });
            return;
        }
        this.mClient.makeRequest(req);
    }

    public void uploadFile(String xpApiPath, String filePath, String contentType, IUploadDelegate delegate) {
        String fileName = filePath;
        try {
            String[] parts = filePath.split("/");
            if (parts.length > 0) {
                fileName = parts[parts.length - 1];
            }
            uploadFile(xpApiPath, new FilePartSource(fileName, new File(filePath)), contentType, delegate);
        } catch (FileNotFoundException e) {
            delegate.fileUploadedTo(MutantMessages.sEmpty, false);
        }
    }

    public void uploadFile(String xpApiPath, String fileName, byte[] fileData, String contentType, IUploadDelegate delegate) {
        uploadFile(xpApiPath, new ByteArrayPartSource(fileName, fileData), contentType, delegate);
    }

    public void uploadFile(final String xpApiPath, final PartSource partSource, final String contentType, final IUploadDelegate delegate) {
        JSONRequest xpRequest = new JSONRequest() { // from class: com.openfeint.internal.OpenFeintInternal.9
            @Override // com.openfeint.internal.request.BaseRequest
            public boolean wantsLogin() {
                return true;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "POST";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return xpApiPath;
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onSuccess(Object responseBody) {
                final BlobUploadParameters params = (BlobUploadParameters) responseBody;
                BlobPostRequest bp = new BlobPostRequest(params, partSource, contentType);
                if (delegate != null) {
                    bp.setDelegate(new IRawRequestDelegate() { // from class: com.openfeint.internal.OpenFeintInternal.9.1
                        @Override // com.openfeint.internal.request.IRawRequestDelegate
                        public void onResponse(int responseCode, String responseBody2) {
                            delegate.fileUploadedTo(params.action + params.key, 200 <= responseCode && responseCode < 300);
                        }
                    });
                }
                OpenFeintInternal.this._makeRequest(bp);
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onFailure(String reason) {
                if (delegate != null) {
                    delegate.fileUploadedTo(MutantMessages.sEmpty, false);
                }
            }
        };
        _makeRequest(xpRequest);
    }

    public int getResource(String resourceName) {
        String packageName = getContext().getPackageName();
        return getContext().getResources().getIdentifier(resourceName, null, packageName);
    }

    public static String getRString(int id) {
        OpenFeintInternal ofi = getInstance();
        Context ctx = ofi.getContext();
        return ctx.getResources().getString(id);
    }

    public static void initializeWithoutLoggingIn(Context ctx, OpenFeintSettings settings, OpenFeintDelegate delegate) {
        validateManifest(ctx);
        if (sInstance == null) {
            sInstance = new OpenFeintInternal(settings, ctx);
        }
        sInstance.mDelegate = delegate;
        if (!sInstance.mDeclined) {
            String userID = sInstance.getUserID();
            if (userID == null) {
                OfflineSupport.setUserTemporary();
            } else {
                OfflineSupport.setUserID(userID);
            }
            sInstance.createDeviceSession();
        } else {
            OfflineSupport.setUserDeclined();
        }
        EventLogDispatcher.getInstance().postEvent(EventLogDispatcher.GAME_START, null);
    }

    public static void initialize(Context ctx, OpenFeintSettings settings, OpenFeintDelegate delegate) {
        initializeWithoutLoggingIn(ctx, settings, delegate);
        OpenFeintInternal ofi = getInstance();
        if (ofi != null) {
            ofi.login(false);
        }
    }

    private static void initEventDispatchSystem() {
        OFLog.m185w(TAG, "init EventDispacherSystem");
        SDKEventListener sdkEventListener = new SDKEventListener();
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.PROMPT_ENABLE_OF, sdkEventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.ACCEPTED_OF, sdkEventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.DECLINED_OF, sdkEventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.ENABLED_OF, sdkEventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.NEW_USER, sdkEventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.DASHBOARD_START, sdkEventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.DASHBOARD_END, sdkEventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.GAME_START, sdkEventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.GAME_BACKGROUND, sdkEventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.GAME_FOREGROUND, sdkEventListener);
        EventLogDispatcher.getInstance().subscribe(EventLogDispatcher.GAME_EXIT, sdkEventListener);
    }

    public void setDelegate(OpenFeintDelegate delegate) {
        this.mDelegate = delegate;
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0060, code lost:
    
        if (r14 != false) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x007e, code lost:
    
        r6 = r6 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0062, code lost:
    
        android.util.Log.v(com.openfeint.internal.OpenFeintInternal.TAG, java.lang.String.format("Couldn't find ActivityInfo for %s.\nPlease consult README.txt for the correct configuration.", r9));
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:?, code lost:
    
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static boolean validateManifest(android.content.Context r20) {
        /*
            Method dump skipped, instructions count: 190
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.openfeint.internal.OpenFeintInternal.validateManifest(android.content.Context):boolean");
    }

    private OpenFeintInternal(OpenFeintSettings settings, Context ctx) {
        sInstance = this;
        this.mContext = ctx;
        this.mSettings = settings;
        this.mSessionStartDate = new Date();
        this.mSessionID = UUID.randomUUID().toString();
        SyncedStore.Reader r = getPrefs().read();
        try {
            this.mDeclined = r.getString(new StringBuilder().append(getContext().getPackageName()).append(".of_declined").toString(), null) != null;
            r.complete();
            this.mMainThreadHandler = new Handler();
            this.mInternalProperties = new Properties();
            this.mInternalProperties.put("server-url", "https://api.openfeint.com");
            this.mInternalProperties.put("ad-server-url", "http://ads.openfeint.com");
            this.mInternalProperties.put("of-version", "1.10.2");
            loadPropertiesFromXMLResource(this.mInternalProperties, getResource("@xml/openfeint_internal_settings"));
            Log.i(TAG, "Using OpenFeint version " + this.mInternalProperties.get("of-version") + " (" + this.mInternalProperties.get("server-url") + ")");
            Properties appProperties = new Properties();
            loadPropertiesFromXMLResource(appProperties, getResource("@xml/openfeint_app_settings"));
            this.mSettings.applyOverrides(appProperties);
            this.mSettings.verify();
            if (!Encryption.initialized()) {
                Encryption.init(this.mSettings.secret);
            }
            this.mClient = new Client(this.mSettings.key, this.mSettings.secret, getPrefs());
            Util.moveWebCache(ctx);
            WebViewCache.initialize(ctx);
            C0208DB.createDB(ctx);
            WebViewCache.start();
            initEventDispatchSystem();
            AnalyticsManager.instance(this.mContext);
        } catch (Throwable th) {
            r.complete();
            throw th;
        }
    }

    public String getUserID() {
        User user = getCurrentUser();
        if (user != null) {
            return user.userID();
        }
        User user2 = lastLoggedInUser();
        if (user2 != null) {
            return user2.userID();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final User lastLoggedInUser() {
        User savedUser = loadUser();
        SyncedStore.Reader r = getPrefs().read();
        try {
            URL saved = new URL(getServerUrl());
            URL loaded = new URL(r.getString("last_logged_in_server", MutantMessages.sEmpty));
            if (savedUser != null) {
                if (saved.equals(loaded)) {
                    return savedUser;
                }
            }
        } catch (MalformedURLException e) {
        } finally {
            r.complete();
        }
        return null;
    }

    public void login(final boolean spotlight) {
        Runnable r = new Runnable() { // from class: com.openfeint.internal.OpenFeintInternal.10
            @Override // java.lang.Runnable
            public void run() {
                if (!OpenFeintInternal.this.mDeclined && !OpenFeintInternal.this.mCurrentlyLoggingIn && !OpenFeintInternal.this.isUserLoggedIn()) {
                    OpenFeintInternal.this.mDeserializedAlready = true;
                    final User savedUser = OpenFeintInternal.this.lastLoggedInUser();
                    if (savedUser != null) {
                        OFLog.m183i(OpenFeintInternal.TAG, "Logging in last known user: " + savedUser.name);
                        OpenFeintInternal.this.loginUser(null, null, null, new IRawRequestDelegate() { // from class: com.openfeint.internal.OpenFeintInternal.10.1
                            @Override // com.openfeint.internal.request.IRawRequestDelegate
                            public void onResponse(int responseCode, String responseBody) {
                                if (200 > responseCode || responseCode >= 300) {
                                    if (403 == responseCode) {
                                        OpenFeintInternal.this.mBanned = true;
                                        return;
                                    } else {
                                        OpenFeintInternal.this.launchIntroFlow(spotlight);
                                        return;
                                    }
                                }
                                SimpleNotification.show("Welcome back " + savedUser.name, Notification.Category.Login, Notification.Type.Success);
                            }
                        });
                    } else {
                        OFLog.m183i(OpenFeintInternal.TAG, "No last user, launch intro flow");
                        OpenFeintInternal.this.clearPrefs();
                        OpenFeintInternal.this.launchIntroFlow(spotlight);
                    }
                }
            }
        };
        this.mMainThreadHandler.post(r);
    }

    private boolean checkBan() {
        if (!this.mBanned) {
            return false;
        }
        displayErrorDialog(getContext().getText(C0207RR.string("of_banned_dialog")));
        return true;
    }

    public void launchIntroFlow(final boolean spotlight) {
        if (!checkBan()) {
            if (isFeintServerReachable()) {
                OpenFeintDelegate d = getDelegate();
                if (this.mApproved || d == null || !d.showCustomApprovalFlow(getContext())) {
                    Runnable r = new Runnable() { // from class: com.openfeint.internal.OpenFeintInternal.11
                        @Override // java.lang.Runnable
                        public void run() {
                            Intent i = new Intent(OpenFeintInternal.this.getContext(), (Class<?>) IntroFlow.class);
                            if (OpenFeintInternal.this.mApproved && spotlight) {
                                i.putExtra("content_name", "index?preapproved=true&spotlight=true");
                            } else if (!spotlight) {
                                if (OpenFeintInternal.this.mApproved) {
                                    i.putExtra("content_name", "index?preapproved=true");
                                }
                            } else {
                                i.putExtra("content_name", "index?spotlight=true");
                            }
                            i.addFlags(268435456);
                            OFLog.m185w(OpenFeintInternal.TAG, "show prompt_enable_of");
                            EventLogDispatcher.getInstance().postEvent(EventLogDispatcher.PROMPT_ENABLE_OF, null);
                            OpenFeintInternal.this.getContext().startActivity(i);
                        }
                    };
                    if (this.mCreatingDeviceSession || !this.mDeviceSessionCreated) {
                        if (!this.mCreatingDeviceSession) {
                            createDeviceSession();
                        }
                        this.mPostDeviceSessionRunnable = r;
                        return;
                    }
                    r.run();
                    return;
                }
                return;
            }
            showOfflineNotification(0, MutantMessages.sEmpty);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showOfflineNotification(int httpCode, Object responseBody) {
        Resources r = getContext().getResources();
        String serverMessage = r.getString(C0207RR.string("of_offline_notification_line2"));
        if (httpCode != 0) {
            if (403 == httpCode) {
                this.mBanned = true;
            }
            if (responseBody instanceof ServerException) {
                serverMessage = ((ServerException) responseBody).message;
            }
        }
        TwoLineNotification.show(r.getString(C0207RR.string("of_offline_notification")), serverMessage, Notification.Category.Foreground, Notification.Type.NetworkOffline);
        OFLog.m182e("Reachability", "Unable to launch IntroFlow because: " + serverMessage);
    }

    private void loadPropertiesFromXMLResource(Properties defaults, int resourceID) {
        XmlResourceParser xml = null;
        try {
            xml = getContext().getResources().getXml(resourceID);
        } catch (Exception e) {
        }
        if (xml != null) {
            String k = null;
            try {
                int eventType = xml.getEventType();
                while (xml.getEventType() != 1) {
                    if (eventType == 2) {
                        k = xml.getName();
                    } else if (xml.getEventType() == 4) {
                        defaults.setProperty(k, xml.getText());
                    }
                    xml.next();
                    eventType = xml.getEventType();
                }
                xml.close();
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    public boolean isFeintServerReachable() {
        if (Util.noPermission("android.permission.ACCESS_NETWORK_STATE", getContext())) {
            return true;
        }
        ConnectivityManager conMan = (ConnectivityManager) getContext().getSystemService("connectivity");
        NetworkInfo activeNetwork = conMan.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public Date getSessionStartDate() {
        return this.mSessionStartDate;
    }

    public String getSessionID() {
        return this.mSessionID;
    }

    public static void setCreateUserSource(String createUserSource2) {
        createUserSource = createUserSource2;
    }

    public static String getCreateUserSource() {
        return createUserSource;
    }

    public static void setUserEnableSource(String userEnableSource2) {
        userEnableSource = userEnableSource2;
    }

    public static String getUserEnableSource() {
        return userEnableSource;
    }
}
