package com.openfeint.api;

import android.content.Context;
import com.openfeint.api.resource.CurrentUser;
import com.openfeint.api.resource.User;

/* loaded from: classes.dex */
public abstract class OpenFeintDelegate {
    public void userLoggedIn(CurrentUser user) {
    }

    public void userLoggedOut(User user) {
    }

    public void onDashboardAppear() {
    }

    public void onDashboardDisappear() {
    }

    public boolean showCustomApprovalFlow(Context ctx) {
        return false;
    }
}
