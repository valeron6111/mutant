package com.openfeint.internal.p004ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.openfeint.api.OpenFeint;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.p004ui.WebNav;
import com.openfeint.internal.request.IRawRequestDelegate;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class Settings extends WebNav {
    private static final String SUBPAGE_KEY = "com.openfeint.internal.ui.Settings.subPage";
    String mOldUserId;

    public static void open() {
        open(null);
    }

    public static void open(String subPage) {
        OpenFeintInternal ofi = OpenFeintInternal.getInstance();
        Context currentActivity = ofi.getContext();
        Intent intent = new Intent(currentActivity, (Class<?>) Settings.class);
        intent.addFlags(268435456);
        if (subPage != null) {
            intent.putExtra(SUBPAGE_KEY, subPage);
        }
        currentActivity.startActivity(intent);
    }

    @Override // com.openfeint.internal.p004ui.WebNav, android.app.Activity
    public void onResume() {
        if (this.mOldUserId == null) {
            this.mOldUserId = OpenFeint.getCurrentUser().resourceID();
        } else if (!this.mOldUserId.equals(OpenFeint.getCurrentUser().resourceID())) {
            new AlertDialog.Builder(this).setTitle(OpenFeintInternal.getRString(C0207RR.string("of_switched_accounts"))).setMessage(String.format(OpenFeintInternal.getRString(C0207RR.string("of_now_logged_in_as_format")), OpenFeint.getCurrentUser().name)).setNegativeButton(OpenFeintInternal.getRString(C0207RR.string("of_ok")), new DialogInterface.OnClickListener() { // from class: com.openfeint.internal.ui.Settings.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    Settings.this.finish();
                }
            }).show();
        }
        super.onResume();
    }

    @Override // com.openfeint.internal.p004ui.WebNav
    protected String initialContentPath() {
        Bundle extras;
        String subPage;
        Intent intent = getIntent();
        return (intent == null || (extras = intent.getExtras()) == null || (subPage = extras.getString(SUBPAGE_KEY)) == null) ? "settings/index" : "settings/" + subPage;
    }

    @Override // com.openfeint.internal.p004ui.WebNav
    protected WebNav.ActionHandler createActionHandler(WebNav webNav) {
        return new SettingsActionHandler(webNav);
    }

    @Override // com.openfeint.internal.p004ui.WebNav, android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10009 && data != null) {
            Toast.makeText(this, OpenFeintInternal.getRString(C0207RR.string("of_profile_pic_changed")), 0).show();
        }
    }

    private class SettingsActionHandler extends WebNav.ActionHandler {
        public SettingsActionHandler(WebNav webNav) {
            super(webNav);
        }

        @Override // com.openfeint.internal.ui.WebNav.ActionHandler
        protected void populateActionList(List<String> actionList) {
            super.populateActionList(actionList);
            actionList.add("logout");
            actionList.add("introFlow");
        }

        @Override // com.openfeint.internal.ui.WebNav.ActionHandler
        public void apiRequest(Map<String, String> options) {
            super.apiRequest(options);
            OpenFeint.getCurrentUser().load(null);
        }

        public final void logout(Map<String, String> options) {
            OpenFeintInternal.getInstance().logoutUser(new IRawRequestDelegate() { // from class: com.openfeint.internal.ui.Settings.SettingsActionHandler.1
                @Override // com.openfeint.internal.request.IRawRequestDelegate
                public void onResponse(int responseCode, String responseBody) {
                    Settings.this.finish();
                }
            });
        }

        public final void introFlow(Map<String, String> options) {
            Settings.this.startActivity(new Intent(Settings.this, (Class<?>) IntroFlow.class).putExtra("content_name", "login?mode=switch"));
        }
    }
}
