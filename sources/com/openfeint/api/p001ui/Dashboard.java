package com.openfeint.api.p001ui;

import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuItem;
import com.openfeint.api.Notification;
import com.openfeint.api.OpenFeint;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.eventlog.EventLogDispatcher;
import com.openfeint.internal.notifications.TwoLineNotification;
import com.openfeint.internal.p004ui.Settings;
import com.openfeint.internal.p004ui.WebNav;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class Dashboard extends WebNav {
    private static String openfrom;
    private static List<Dashboard> sOpenDashboards = new ArrayList();
    boolean mRootIsHome = true;

    public static String getOpenfrom() {
        return openfrom;
    }

    public static void setOpenfrom(String openfrom2) {
        openfrom = openfrom2;
    }

    public static void open() {
        open(null, false);
    }

    public static void openFromSpotlight() {
        open("user.json?spotlight=true", true);
    }

    public static void close() {
        for (Dashboard d : sOpenDashboards) {
            d.finish();
        }
    }

    public static void openLeaderboards() {
        open("leaderboards", false);
    }

    public static void openLeaderboard(String leaderboardId) {
        open("leaderboard?leaderboard_id=" + leaderboardId, false);
    }

    public static void openAchievements() {
        open("achievements", false);
    }

    public static void openGameDetail(String appId) {
        open("game?game_id=" + appId, false);
    }

    public static void openPath(String path) {
        open(path, false);
    }

    private static void open(String screenName, boolean spotlight) {
        OpenFeint.trySubmitOfflineData();
        OpenFeintInternal ofi = OpenFeintInternal.getInstance();
        if (!ofi.isFeintServerReachable()) {
            Resources r = OpenFeintInternal.getInstance().getContext().getResources();
            TwoLineNotification.show(r.getString(C0207RR.string("of_offline_notification")), r.getString(C0207RR.string("of_offline_notification_line2")), Notification.Category.Foreground, Notification.Type.NetworkOffline);
            return;
        }
        EventLogDispatcher.getInstance().postEvent(EventLogDispatcher.DASHBOARD_START, openfrom);
        Intent intent = new Intent(ofi.getContext(), (Class<?>) Dashboard.class);
        if (screenName != null) {
            intent.putExtra("screenName", screenName);
        }
        ofi.submitIntent(intent, spotlight);
    }

    @Override // com.openfeint.internal.p004ui.WebNav, android.app.Activity
    public void onResume() {
        super.onResume();
        if (!sOpenDashboards.contains(this)) {
            sOpenDashboards.add(this);
        }
        if (OpenFeintInternal.getInstance().getCurrentUser() == null) {
            finish();
        }
    }

    @Override // com.openfeint.internal.p004ui.WebNav, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        sOpenDashboards.remove(this);
    }

    @Override // com.openfeint.internal.p004ui.WebNav, android.app.Activity
    public void onStop() {
        EventLogDispatcher.getInstance().postEvent(EventLogDispatcher.DASHBOARD_END, openfrom);
        super.onStop();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0207RR.menu("of_dashboard"), menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(C0207RR.m180id("home")).setVisible(!this.mRootIsHome || this.pageStackCount > 1);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        String menuButtonName = null;
        if (item.getItemId() == C0207RR.m180id("home")) {
            menuButtonName = "home";
            this.mRootIsHome = true;
        } else if (item.getItemId() == C0207RR.m180id("settings")) {
            menuButtonName = "settings";
        } else if (item.getItemId() == C0207RR.m180id("exit_feint")) {
            menuButtonName = "exit";
        }
        if (menuButtonName == null) {
            return super.onOptionsItemSelected(item);
        }
        executeJavascript(String.format("OF.menu('%s')", menuButtonName));
        return true;
    }

    @Override // com.openfeint.internal.p004ui.WebNav
    protected String initialContentPath() {
        String screenName = getIntent().getStringExtra("screenName");
        if (screenName == null) {
            return "dashboard/user";
        }
        this.mRootIsHome = false;
        return "dashboard/" + screenName;
    }

    @Override // com.openfeint.internal.p004ui.WebNav
    protected WebNav.ActionHandler createActionHandler(WebNav webNav) {
        return new DashboardActionHandler(webNav);
    }

    private class DashboardActionHandler extends WebNav.ActionHandler {
        public DashboardActionHandler(WebNav webNav) {
            super(webNav);
        }

        @Override // com.openfeint.internal.ui.WebNav.ActionHandler
        protected void populateActionList(List<String> actionList) {
            super.populateActionList(actionList);
            actionList.add("openSettings");
        }

        public final void openSettings(Map<String, String> options) {
            Settings.open();
        }
    }
}
