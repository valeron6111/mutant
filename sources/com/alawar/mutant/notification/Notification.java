package com.alawar.mutant.notification;

import com.tapjoy.TapjoyConstants;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Notification {
    public String data;

    /* renamed from: id */
    public long f0id;
    public NotifyTypes notifyType;
    public long timestamp;
    public String userId;

    public enum NotifyTypes {
        redeem,
        offer,
        bonus,
        credit,
        balance
    }

    public Notification() {
    }

    public Notification(JSONObject o) {
        try {
            this.timestamp = o.getLong(TapjoyConstants.TJC_TIMESTAMP);
            this.f0id = o.getLong("id");
            this.notifyType = NotifyTypes.valueOf(o.getString("notifyType"));
            this.userId = o.getString("userId");
            this.data = o.getString("data");
        } catch (JSONException e) {
            throw new RuntimeException("Can't construct Notification", e);
        }
    }

    public String toString() {
        return Long.toString(this.f0id) + "!" + this.userId + "!" + Integer.toString(this.notifyType.ordinal()) + "!" + this.data + "!" + Long.toString(this.timestamp) + "!";
    }
}
