package com.openfeint.internal.p004ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.alawar.mutant.billing.Consts;
import com.alawar.mutant.database.DbBuilder;
import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.api.OpenFeintDelegate;
import com.openfeint.api.p001ui.Dashboard;
import com.openfeint.api.resource.Score;
import com.openfeint.api.resource.User;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.ImagePicker;
import com.openfeint.internal.JsonResourceParser;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.Util;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.request.IRawRequestDelegate;
import com.openfeint.internal.resource.ScoreBlobDelegate;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonFactory;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.tapjoy.TapjoyConstants;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class WebNav extends NestedWindow {
    protected static final int REQUEST_CODE_NATIVE_BROWSER = 25565;
    protected static final String TAG = "WebUI";
    ActionHandler mActionHandler;
    Dialog mLaunchLoadingView;
    private WebNavClient mWebViewClient;
    protected int pageStackCount;
    boolean mIsPageLoaded = false;
    boolean mIsFrameworkLoaded = false;
    private boolean mShouldRefreshOnResume = true;
    protected ArrayList<String> mPreloadConsoleOutput = new ArrayList<>();
    private Map<String, String> mNativeBrowserParameters = null;

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        OpenFeintInternal.saveInstanceState(outState);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle inState) {
        OpenFeintInternal.restoreInstanceState(inState);
    }

    public ActionHandler getActionHandler() {
        return this.mActionHandler;
    }

    public Dialog getLaunchLoadingView() {
        return this.mLaunchLoadingView;
    }

    protected void setFrameworkLoaded(boolean value) {
        this.mIsFrameworkLoaded = value;
    }

    @Override // com.openfeint.internal.p004ui.NestedWindow, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OFLog.m183i(TAG, "--- WebUI Bootup ---");
        this.pageStackCount = 0;
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.getSettings().setPluginsEnabled(false);
        this.mWebView.setScrollBarStyle(33554432);
        this.mWebView.getSettings().setCacheMode(2);
        this.mLaunchLoadingView = new Dialog(this, C0207RR.style("OFLoading"));
        this.mLaunchLoadingView.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.openfeint.internal.ui.WebNav.1
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                WebNav.this.finish();
            }
        });
        this.mLaunchLoadingView.setCancelable(true);
        this.mLaunchLoadingView.setContentView(C0207RR.layout("of_native_loader"));
        ProgressBar progress = (ProgressBar) this.mLaunchLoadingView.findViewById(C0207RR.m180id("progress"));
        progress.setIndeterminate(true);
        progress.setIndeterminateDrawable(OpenFeintInternal.getInstance().getContext().getResources().getDrawable(C0207RR.drawable("of_native_loader_progress")));
        this.mActionHandler = createActionHandler(this);
        this.mWebViewClient = createWebNavClient(this.mActionHandler);
        this.mWebView.setWebViewClient(this.mWebViewClient);
        this.mWebView.setWebChromeClient(new WebNavChromeClient());
        this.mWebView.addJavascriptInterface(new Object() { // from class: com.openfeint.internal.ui.WebNav.2
            public void action(final String actionUri) {
                WebNav.this.runOnUiThread(new Runnable() { // from class: com.openfeint.internal.ui.WebNav.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        WebNav.this.getActionHandler().dispatch(Uri.parse(actionUri));
                    }
                });
            }

            public void frameworkLoaded() {
                WebNav.this.setFrameworkLoaded(true);
            }
        }, "NativeInterface");
        String path = initialContentPath();
        if (path.contains("?")) {
            path = path.split("\\?")[0];
        }
        if (!path.endsWith(".json")) {
            path = path + ".json";
        }
        WebViewCache.prioritize(path);
        load(false);
        this.mLaunchLoadingView.show();
    }

    protected String rootPage() {
        return "index.html";
    }

    protected void load(final boolean reload) {
        this.mIsPageLoaded = false;
        WebViewCache.trackPath(rootPage(), new WebViewCacheCallback() { // from class: com.openfeint.internal.ui.WebNav.3
            @Override // com.openfeint.internal.p004ui.WebViewCacheCallback
            public void pathLoaded(String itemPath) {
                if (WebNav.this.mWebView != null) {
                    String url = WebViewCache.getItemUri(itemPath);
                    OFLog.m183i(WebNav.TAG, "Loading URL: " + url);
                    if (reload) {
                        WebNav.this.mWebView.reload();
                    } else {
                        WebNav.this.mWebView.loadUrl(url);
                    }
                }
            }

            @Override // com.openfeint.internal.p004ui.WebViewCacheCallback
            public void failLoaded() {
                WebNav.this.closeForDiskError();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeForDiskError() {
        runOnUiThread(new Runnable() { // from class: com.openfeint.internal.ui.WebNav.4
            @Override // java.lang.Runnable
            public void run() {
                WebNav.this.dismissDialog();
                String place = Util.sdcardReady(WebNav.this) ? OpenFeintInternal.getRString(C0207RR.string("of_sdcard")) : OpenFeintInternal.getRString(C0207RR.string("of_device"));
                new AlertDialog.Builder(WebNav.this).setMessage(String.format(OpenFeintInternal.getRString(C0207RR.string("of_nodisk")), place)).setPositiveButton(OpenFeintInternal.getRString(C0207RR.string("of_no")), new DialogInterface.OnClickListener() { // from class: com.openfeint.internal.ui.WebNav.4.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        WebNav.this.finish();
                    }
                }).show();
            }
        });
    }

    private static final String jsQuotedStringLiteral(String unquotedString) {
        return unquotedString == null ? "''" : "'" + unquotedString.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        User localUser = OpenFeintInternal.getInstance().getCurrentUser();
        if (localUser != null && this.mIsFrameworkLoaded) {
            executeJavascript(String.format("if (OF.user) { OF.user.name = %s; OF.user.id = '%s'; }", jsQuotedStringLiteral(localUser.name), localUser.resourceID()));
            if (this.mShouldRefreshOnResume) {
                executeJavascript("if (OF.page) OF.refresh();");
            }
        }
        this.mShouldRefreshOnResume = true;
    }

    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
        dismissDialog();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissDialog() {
        if (this.mLaunchLoadingView.isShowing()) {
            this.mLaunchLoadingView.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDialog() {
        if (!this.mLaunchLoadingView.isShowing()) {
            this.mLaunchLoadingView.show();
        }
    }

    @Override // com.openfeint.internal.p004ui.NestedWindow, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        String orientationString;
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2) {
            orientationString = "landscape";
        } else {
            orientationString = "portrait";
        }
        executeJavascript(String.format("OF.setOrientation('%s');", orientationString));
    }

    public void loadInitialContent() {
        String path = initialContentPath();
        if (path.contains("?")) {
            path = path.split("\\?")[0];
        }
        if (!path.endsWith(".json")) {
            path = path + ".json";
        }
        WebViewCache.trackPath(path, new WebViewCacheCallback() { // from class: com.openfeint.internal.ui.WebNav.5
            @Override // com.openfeint.internal.p004ui.WebViewCacheCallback
            public void pathLoaded(String itemPath) {
                WebNav.this.executeJavascript("OF.navigateToUrl('" + WebNav.this.initialContentPath() + "')");
            }

            @Override // com.openfeint.internal.p004ui.WebViewCacheCallback
            public void failLoaded() {
                WebNav.this.closeForDiskError();
            }
        });
    }

    protected ActionHandler createActionHandler(WebNav webNav) {
        return new ActionHandler(webNav);
    }

    protected WebNavClient createWebNavClient(ActionHandler actionHandler) {
        return new WebNavClient(actionHandler);
    }

    protected String initialContentPath() {
        String contentPath = getIntent().getStringExtra("content_path");
        if (contentPath == null) {
            throw new RuntimeException("WebNav intent requires extra value 'content_path'");
        }
        return contentPath;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 84) {
            executeJavascript(String.format("OF.menu('%s')", "search"));
            return true;
        }
        if (keyCode == 4 && this.pageStackCount > 1) {
            executeJavascript("OF.goBack()");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void executeJavascript(String js) {
        if (this.mWebView != null) {
            this.mWebView.loadUrl("javascript:" + js);
        }
    }

    protected class WebNavClient extends WebViewClient {
        ActionHandler mActionHandler;

        public WebNavClient(ActionHandler anActionHandler) {
            this.mActionHandler = anActionHandler;
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String stringUrl) {
            Uri uri = Uri.parse(stringUrl);
            if (uri.getScheme().equals("http") || uri.getScheme().equals("https")) {
                view.loadUrl(stringUrl);
                return true;
            }
            if (uri.getScheme().equals("openfeint")) {
                this.mActionHandler.dispatch(uri);
                return true;
            }
            OFLog.m182e(WebNav.TAG, "UNHANDLED PROTOCOL: " + uri.getScheme());
            return true;
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            this.mActionHandler.hideLoader(null);
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView view, String url) {
            if (!WebNav.this.mIsPageLoaded) {
                WebNav.this.mIsPageLoaded = true;
                if (WebNav.this.mIsFrameworkLoaded) {
                    loadInitialContent();
                } else {
                    attemptRecovery(view, url);
                }
            }
        }

        protected void attemptRecovery(WebView view, String url) {
            if (WebViewCache.recover()) {
                WebNav.this.load(true);
                new AlertDialog.Builder(view.getContext()).setMessage(OpenFeintInternal.getRString(C0207RR.string("of_crash_report_query"))).setNegativeButton(OpenFeintInternal.getRString(C0207RR.string("of_no")), new DialogInterface.OnClickListener() { // from class: com.openfeint.internal.ui.WebNav.WebNavClient.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        WebNav.this.finish();
                    }
                }).setPositiveButton(OpenFeintInternal.getRString(C0207RR.string("of_yes")), new DialogInterface.OnClickListener() { // from class: com.openfeint.internal.ui.WebNav.WebNavClient.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        WebNavClient.this.submitCrashReport();
                    }
                }).show();
            } else if (!WebViewCache.isDiskError()) {
                WebNav.this.finish();
            }
        }

        protected void submitCrashReport() {
            Map<String, Object> crashReport = new HashMap<>();
            crashReport.put("console", new JSONArray((Collection) WebNav.this.mPreloadConsoleOutput));
            JSONObject json = new JSONObject(crashReport);
            Map<String, Object> params = new HashMap<>();
            params.put("crash_report", json.toString());
            OpenFeintInternal.genericRequest("/webui/crash_report", "POST", params, null, null);
        }

        public void loadInitialContent() {
            OpenFeintInternal of = OpenFeintInternal.getInstance();
            User localUser = of.getCurrentUser();
            int orientation = WebNav.this.getResources().getConfiguration().orientation;
            HashMap<String, Object> user = new HashMap<>();
            if (localUser != null) {
                user.put("id", localUser.resourceID());
                user.put("name", localUser.name);
            }
            HashMap<String, Object> game = new HashMap<>();
            game.put("id", of.getAppID());
            game.put("name", of.getAppName());
            game.put("version", Integer.toString(of.getAppVersion()));
            Map<String, Object> device = OpenFeintInternal.getInstance().getDeviceParams();
            device.put("parentalControls", Boolean.valueOf(OpenFeintInternal.getInstance().parentalControlsEnabled()));
            HashMap<String, Object> config = new HashMap<>();
            config.put(TapjoyConstants.TJC_PLATFORM, TapjoyConstants.TJC_DEVICE_PLATFORM_TYPE);
            config.put("clientVersion", of.getOFVersion());
            config.put("hasNativeInterface", true);
            config.put("dpi", Util.getDpiName(WebNav.this));
            config.put("locale", WebNav.this.getResources().getConfiguration().locale.toString());
            config.put("user", new JSONObject(user));
            config.put("game", new JSONObject(game));
            config.put("device", new JSONObject(device));
            config.put("actions", new JSONArray((Collection) WebNav.this.getActionHandler().getActionList()));
            config.put("orientation", orientation == 2 ? "landscape" : "portrait");
            config.put("serverUrl", of.getServerUrl());
            JSONObject json = new JSONObject(config);
            WebNav.this.executeJavascript(String.format("OF.init.clientBoot(%s)", json.toString()));
            this.mActionHandler.mWebNav.loadInitialContent();
        }
    }

    private class WebNavChromeClient extends WebChromeClient {
        private WebNavChromeClient() {
        }

        @Override // android.webkit.WebChromeClient
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(view.getContext()).setMessage(message).setNegativeButton(OpenFeintInternal.getRString(C0207RR.string("of_ok")), new DialogInterface.OnClickListener() { // from class: com.openfeint.internal.ui.WebNav.WebNavChromeClient.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.openfeint.internal.ui.WebNav.WebNavChromeClient.1
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface dialog) {
                    result.cancel();
                }
            }).show();
            return true;
        }

        @Override // android.webkit.WebChromeClient
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(view.getContext()).setMessage(message).setPositiveButton(OpenFeintInternal.getRString(C0207RR.string("of_ok")), new DialogInterface.OnClickListener() { // from class: com.openfeint.internal.ui.WebNav.WebNavChromeClient.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            }).setNegativeButton(OpenFeintInternal.getRString(C0207RR.string("of_cancel")), new DialogInterface.OnClickListener() { // from class: com.openfeint.internal.ui.WebNav.WebNavChromeClient.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.openfeint.internal.ui.WebNav.WebNavChromeClient.3
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface dialog) {
                    result.cancel();
                }
            }).show();
            return true;
        }

        @Override // android.webkit.WebChromeClient
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            if (!WebNav.this.mIsFrameworkLoaded) {
                String line = String.format("%s at %s:%d)", message, sourceID, Integer.valueOf(lineNumber));
                WebNav.this.mPreloadConsoleOutput.add(line);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public class ActionHandler {
        private static final String WEBUI_PREFS = "OFWebUI";
        private static final String WEBUI_SETTING_PREFIX = "OFWebUISetting_";
        List<String> mActionList = new ArrayList();
        WebNav mWebNav;

        protected List<String> getActionList() {
            return this.mActionList;
        }

        public ActionHandler(WebNav webNav) {
            this.mWebNav = webNav;
            populateActionList(this.mActionList);
        }

        protected void populateActionList(List<String> actionList) {
            actionList.add("log");
            actionList.add("apiRequest");
            actionList.add("contentLoaded");
            actionList.add("startLoading");
            actionList.add("back");
            actionList.add("showLoader");
            actionList.add("hideLoader");
            actionList.add("alert");
            actionList.add("dismiss");
            actionList.add("openMarket");
            actionList.add("isApplicationInstalled");
            actionList.add("openYoutubePlayer");
            actionList.add("profilePicture");
            actionList.add("openBrowser");
            actionList.add("downloadBlob");
            actionList.add("dashboard");
            actionList.add("readSetting");
            actionList.add("writeSetting");
            actionList.add("setParentalControlsEnabled");
        }

        public void dispatch(Uri uri) {
            if (uri.getHost().equals("action")) {
                Map<String, Object> options = parseQueryString(uri);
                String actionName = uri.getPath().replaceFirst("/", MutantMessages.sEmpty);
                if (!actionName.equals("log")) {
                    Map<String, Object> escapedOptions = new HashMap<>(options);
                    String params = (String) options.get("params");
                    if (params != null && params.contains("password")) {
                        escapedOptions.put("params", "---FILTERED---");
                    }
                    OFLog.m183i(WebNav.TAG, "ACTION: " + actionName + " " + escapedOptions.toString());
                }
                if (this.mActionList.contains(actionName)) {
                    try {
                        getClass().getMethod(actionName, Map.class).invoke(this, options);
                        return;
                    } catch (NoSuchMethodException e) {
                        OFLog.m182e(WebNav.TAG, "mActionList contains this method, but it is not implemented: " + actionName);
                        return;
                    } catch (Exception e2) {
                        OFLog.m182e(WebNav.TAG, "Unhandled Exception: " + e2.toString() + "   " + e2.getCause());
                        return;
                    }
                }
                OFLog.m182e(WebNav.TAG, "UNHANDLED ACTION: " + actionName);
                return;
            }
            OFLog.m182e(WebNav.TAG, "UNHANDLED MESSAGE TYPE: " + uri.getHost());
        }

        private Map<String, Object> parseQueryString(Uri uri) {
            return parseQueryString(uri.getEncodedQuery());
        }

        private Map<String, Object> parseQueryString(String queryString) {
            Map<String, Object> options = new HashMap<>();
            if (queryString != null) {
                String[] pairs = queryString.split("&");
                for (String stringPair : pairs) {
                    String[] pair = stringPair.split("=");
                    if (pair.length == 2) {
                        options.put(pair[0], Uri.decode(pair[1]));
                    } else {
                        options.put(pair[0], null);
                    }
                }
            }
            return options;
        }

        public void apiRequest(Map<String, String> options) {
            final String requestID = options.get(Consts.INAPP_REQUEST_ID);
            Map<String, Object> params = parseQueryString(options.get("params"));
            Map<String, Object> httpParams = parseQueryString(options.get("httpParams"));
            OpenFeintInternal.genericRequest(options.get("path"), options.get("method"), params, httpParams, new IRawRequestDelegate() { // from class: com.openfeint.internal.ui.WebNav.ActionHandler.1
                @Override // com.openfeint.internal.request.IRawRequestDelegate
                public void onResponse(int statusCode, String responseBody) {
                    String response = responseBody.trim();
                    if (response.length() == 0) {
                        response = "{}";
                    }
                    String js = String.format("OF.api.completeRequest(\"%s\", \"%d\", %s)", requestID, Integer.valueOf(statusCode), response);
                    ActionHandler.this.mWebNav.executeJavascript(js);
                }
            });
        }

        public void contentLoaded(Map<String, String> options) {
            if (options.get("keepLoader") == null || !options.get("keepLoader").equals("true")) {
                hideLoader(null);
                WebNav.this.setTitle(options.get("title"));
            }
            this.mWebNav.fade(true);
            WebNav.this.dismissDialog();
        }

        public void startLoading(Map<String, String> options) {
            this.mWebNav.fade(false);
            showLoader(null);
            WebViewCache.trackPath(options.get("path"), new WebViewCacheCallback() { // from class: com.openfeint.internal.ui.WebNav.ActionHandler.2
                @Override // com.openfeint.internal.p004ui.WebViewCacheCallback
                public void pathLoaded(String itemPath) {
                    WebNav.this.executeJavascript("OF.navigateToUrlCallback()");
                }

                @Override // com.openfeint.internal.p004ui.WebViewCacheCallback
                public void failLoaded() {
                    WebNav.this.closeForDiskError();
                }

                @Override // com.openfeint.internal.p004ui.WebViewCacheCallback
                public void onTrackingNeeded() {
                    WebNav.this.showDialog();
                }
            });
            this.mWebNav.pageStackCount++;
        }

        public void back(Map<String, String> options) {
            this.mWebNav.fade(false);
            String root = options.get("root");
            if (root != null && !root.equals("false")) {
                this.mWebNav.pageStackCount = 1;
            }
            if (this.mWebNav.pageStackCount > 1) {
                WebNav webNav = this.mWebNav;
                webNav.pageStackCount--;
            }
        }

        public void showLoader(Map<String, String> options) {
        }

        public void hideLoader(Map<String, String> options) {
        }

        public void log(Map<String, String> options) {
            String message = options.get("message");
            if (message != null) {
                OFLog.m183i(WebNav.TAG, "WEBLOG: " + options.get("message"));
            }
        }

        public void alert(Map<String, String> options) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mWebNav);
            builder.setTitle(options.get("title"));
            builder.setMessage(options.get("message"));
            builder.setNegativeButton(OpenFeintInternal.getRString(C0207RR.string("of_ok")), (DialogInterface.OnClickListener) null);
            builder.show();
        }

        public void dismiss(Map<String, String> options) {
            WebNav.this.finish();
        }

        public void openMarket(Map<String, String> options) {
            String packageName = options.get("package_name");
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName));
            this.mWebNav.startActivity(intent);
        }

        public void isApplicationInstalled(Map<String, String> options) {
            boolean installed = false;
            PackageManager manager = this.mWebNav.getPackageManager();
            List<ApplicationInfo> installedApps = manager.getInstalledApplications(0);
            String searchString = options.get("package_name");
            for (ApplicationInfo info : installedApps) {
                if (info.packageName.equals(searchString)) {
                    installed = true;
                }
            }
            WebNav webNav = WebNav.this;
            Object[] objArr = new Object[2];
            objArr[0] = options.get("callback");
            objArr[1] = installed ? "true" : "false";
            webNav.executeJavascript(String.format("%s(%s)", objArr));
        }

        public void openYoutubePlayer(Map<String, String> options) {
            String videoID = options.get("video_id");
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("vnd.youtube:" + videoID));
            List<ResolveInfo> list = WebNav.this.getPackageManager().queryIntentActivities(intent, 65536);
            if (list.size() == 0) {
                Toast.makeText(this.mWebNav, OpenFeintInternal.getRString(C0207RR.string("of_no_video")), 0).show();
            } else {
                this.mWebNav.startActivity(intent);
            }
        }

        public final void profilePicture(Map<String, String> options) {
            ImagePicker.show(WebNav.this);
        }

        public void openBrowser(Map<String, String> options) {
            Intent browserIntent = new Intent(this.mWebNav, (Class<?>) NativeBrowser.class);
            WebNav.this.mNativeBrowserParameters = new HashMap();
            String[] arr$ = {"src", "callback", "on_cancel", "on_failure", "timeout"};
            for (String arg : arr$) {
                String val = options.get(arg);
                if (val != null) {
                    WebNav.this.mNativeBrowserParameters.put(arg, val);
                    browserIntent.putExtra(NativeBrowser.INTENT_ARG_PREFIX + arg, val);
                }
            }
            WebNav.this.startActivityForResult(browserIntent, WebNav.REQUEST_CODE_NATIVE_BROWSER);
        }

        public void downloadBlob(Map<String, String> options) {
            String scoreJSON = options.get("score");
            final String onError = options.get("onError");
            final String onSuccess = options.get("onSuccess");
            try {
                JsonFactory jsonFactory = new JsonFactory();
                JsonParser jp = jsonFactory.createJsonParser(new StringReader(scoreJSON));
                JsonResourceParser jrp = new JsonResourceParser(jp);
                Object scoreObject = jrp.parse();
                if (scoreObject != null && (scoreObject instanceof Score)) {
                    final Score score = (Score) scoreObject;
                    score.downloadBlob(new Score.DownloadBlobCB() { // from class: com.openfeint.internal.ui.WebNav.ActionHandler.3
                        @Override // com.openfeint.api.resource.Score.DownloadBlobCB
                        public void onSuccess() {
                            if (onSuccess != null) {
                                WebNav.this.executeJavascript(String.format("%s()", onSuccess));
                            }
                            ScoreBlobDelegate.notifyBlobDownloaded(score);
                        }

                        @Override // com.openfeint.internal.APICallback
                        public void onFailure(String exceptionMessage) {
                            if (onError != null) {
                                WebNav.this.executeJavascript(String.format("%s(%s)", onError, exceptionMessage));
                            }
                        }
                    });
                }
            } catch (Exception e) {
                if (onError != null) {
                    WebNav.this.executeJavascript(String.format("%s(%s)", onError, e.getLocalizedMessage()));
                }
            }
        }

        public void dashboard(Map<String, String> options) {
            Dashboard.openFromSpotlight();
        }

        public void readSetting(Map<String, String> options) {
            String k = options.get("key");
            String cb = options.get("callback");
            if (cb != null) {
                String key = k != null ? WEBUI_SETTING_PREFIX + k : null;
                SharedPreferences prefs = OpenFeintInternal.getInstance().getContext().getSharedPreferences(WEBUI_PREFS, 0);
                String val = prefs.getString(key, null);
                OFLog.m183i(WebNav.TAG, String.format("readSetting(%s) => %s", k, val));
                WebNav webNav = WebNav.this;
                Object[] objArr = new Object[2];
                objArr[0] = cb;
                if (val == null) {
                    val = "null";
                }
                objArr[1] = val;
                webNav.executeJavascript(String.format("%s(%s)", objArr));
            }
        }

        public void writeSetting(Map<String, String> options) {
            String k = options.get("key");
            String v = options.get(DbBuilder.KEY_VALUE);
            if (k != null && v != null) {
                String key = WEBUI_SETTING_PREFIX + k;
                SharedPreferences.Editor editor = OpenFeintInternal.getInstance().getContext().getSharedPreferences(WEBUI_PREFS, 0).edit();
                editor.putString(key, v);
                editor.commit();
            }
        }

        public void setParentalControlsEnabled(Map<String, String> options) {
            String v = options.get("parentalControlsEnabled");
            OpenFeintInternal.getInstance().setParentalControlsEnabled("true".equals(v));
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap image;
        super.onActivityResult(requestCode, resultCode, data);
        if (this.mNativeBrowserParameters != null && requestCode == REQUEST_CODE_NATIVE_BROWSER) {
            if (resultCode != 0) {
                this.mShouldRefreshOnResume = false;
                if (data.getBooleanExtra("com.openfeint.internal.ui.NativeBrowser.argument.failed", false)) {
                    String cb = this.mNativeBrowserParameters.get("on_failure");
                    if (cb != null) {
                        int code = data.getIntExtra("com.openfeint.internal.ui.NativeBrowser.argument.failure_code", 0);
                        String desc = data.getStringExtra("com.openfeint.internal.ui.NativeBrowser.argument.failure_desc");
                        executeJavascript(String.format("%s(%d, %s)", cb, Integer.valueOf(code), jsQuotedStringLiteral(desc)));
                    }
                } else {
                    String cb2 = this.mNativeBrowserParameters.get("callback");
                    if (cb2 != null) {
                        String rv = data.getStringExtra("com.openfeint.internal.ui.NativeBrowser.argument.result");
                        Object[] objArr = new Object[2];
                        objArr[0] = cb2;
                        if (rv == null) {
                            rv = MutantMessages.sEmpty;
                        }
                        objArr[1] = rv;
                        executeJavascript(String.format("%s(%s)", objArr));
                    }
                }
            } else {
                String cb3 = this.mNativeBrowserParameters.get("on_cancel");
                if (cb3 != null) {
                    executeJavascript(String.format("%s()", cb3));
                }
            }
            this.mNativeBrowserParameters = null;
            return;
        }
        if (ImagePicker.isImagePickerActivityResult(requestCode) && (image = ImagePicker.onImagePickerActivityResult(this, resultCode, 152, data)) != null) {
            String apiPath = "/xp/users/" + OpenFeintInternal.getInstance().getCurrentUser().resourceID() + "/profile_picture";
            ImagePicker.compressAndUpload(image, apiPath, new OpenFeintInternal.IUploadDelegate() { // from class: com.openfeint.internal.ui.WebNav.6
                @Override // com.openfeint.internal.OpenFeintInternal.IUploadDelegate
                public void fileUploadedTo(String url, boolean success) {
                    if (success) {
                        WebNav.this.executeJavascript("try { OF.page.onProfilePictureChanged('" + url + "'); } catch (e) {}");
                    }
                }
            });
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        OpenFeintDelegate d = OpenFeintInternal.getInstance().getDelegate();
        if (d != null) {
            if (hasFocus) {
                d.onDashboardAppear();
            } else {
                d.onDashboardDisappear();
            }
        }
    }

    @Override // android.app.Activity
    public void onDestroy() {
        this.mWebView.destroy();
        this.mWebView = null;
        super.onDestroy();
    }
}
