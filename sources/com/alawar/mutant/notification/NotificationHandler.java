package com.alawar.mutant.notification;

import com.alawar.common.event.EventBus;
import com.alawar.common.event.EventHandler;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class NotificationHandler implements EventHandler<NotificationsUpdatedEvent> {
    public static final NotificationHandler instance = new NotificationHandler();
    ArrayList<Notification> queue = new ArrayList<>();

    private NotificationHandler() {
    }

    public static void initialize() {
        EventBus.addHandler(NotificationsUpdatedEvent.class, instance);
    }

    public static ArrayList<Notification> popEvents() {
        ArrayList<Notification> result;
        synchronized (instance) {
            result = new ArrayList<>();
            result.addAll(instance.queue);
            instance.queue.clear();
        }
        return result;
    }

    @Override // com.alawar.common.event.EventHandler
    public void onEvent(NotificationsUpdatedEvent event) {
        synchronized (instance) {
            this.queue.addAll(event.notifications);
        }
    }
}
