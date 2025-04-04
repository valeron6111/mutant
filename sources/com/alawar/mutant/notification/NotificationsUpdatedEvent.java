package com.alawar.mutant.notification;

import com.alawar.common.event.Event;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NotificationsUpdatedEvent implements Event {
    public ArrayList<Notification> notifications = new ArrayList<>();

    public NotificationsUpdatedEvent(JSONArray notifList) throws JSONException {
        for (int i = 0; i < notifList.length(); i++) {
            Notification item = new Notification((JSONObject) notifList.get(i));
            this.notifications.add(item);
        }
    }
}
