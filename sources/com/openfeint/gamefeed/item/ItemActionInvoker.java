package com.openfeint.gamefeed.item;

import android.content.Context;
import com.openfeint.gamefeed.internal.GameFeedHelper;
import com.openfeint.internal.BaseActionInvoker;

/* loaded from: classes.dex */
public class ItemActionInvoker extends BaseActionInvoker {
    @Override // com.openfeint.internal.BaseActionInvoker
    public void dashboard(Object args, Context ctx) {
        GameFeedHelper.OpenDashboadrFromGameFeed((String) args);
    }
}
