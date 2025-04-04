package com.openfeint.internal.eventlog;

import com.openfeint.internal.logcat.OFLog;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class EventLogDispatcher {
    public static final String ACCEPTED_OF = "accepted_of.openfeint.com";
    public static final String DASHBOARD_END = "dashboard_end.openfeint.com";
    public static final String DASHBOARD_START = "dashboard_start.openfeint.com";
    public static final String DECLINED_OF = "declined_of.openfeint.com";
    public static final String ENABLED_OF = "enabled_of.openfeint.com";
    public static final String GAME_BACKGROUND = "game_background.openfeint.com";
    public static final String GAME_EXIT = "game_exit.openfeint.com";
    public static final String GAME_FOREGROUND = "game_foreground.openfeint.com";
    public static final String GAME_START = "game_start.openfeint.com";
    public static final String LOGIN_SUCESS = "login_success.openfeint.com";
    public static final String LOGOUT_SUCESS = "logout_success.openfeint.com";
    public static final String NEW_USER = "new_user.openfeint.com";
    public static final String PROMPT_ENABLE_OF = "prompt_enable_of.openfeint.com";
    private static EventLogDispatcher instance = null;
    public static final String tag = "EventLogDispatcher";
    private Map<String, Map<String, IEventListener>> subscription = new HashMap();

    private EventLogDispatcher() {
    }

    public static EventLogDispatcher getInstance() {
        if (instance == null) {
            instance = new EventLogDispatcher();
        }
        return instance;
    }

    public void postEvent(String type, Object body) {
        if (this.subscription.containsKey(type)) {
            Map<String, IEventListener> goodListeners = this.subscription.get(type);
            for (String key : goodListeners.keySet()) {
                IEventListener listener = goodListeners.get(key);
                OFLog.m181d(tag, "posteEvent:" + type + " to listener: " + listener.getName());
                listener.handleEvent(type, body);
            }
        }
    }

    public boolean subscribe(String type, IEventListener listener) {
        if (listener == null || listener.getName() == null || type == null) {
            return false;
        }
        OFLog.m181d(tag, "subscribe:" + type + " from: " + listener.getName());
        Map<String, IEventListener> eventMapping = this.subscription.get(type);
        if (eventMapping == null) {
            eventMapping = new HashMap<>();
            eventMapping.put(listener.getName(), listener);
        } else {
            eventMapping.put(listener.getName(), listener);
        }
        this.subscription.put(type, eventMapping);
        return true;
    }

    public boolean unSubscribe(String type, IEventListener listener) {
        if (listener == null || listener.getName() == null || type == null) {
            return false;
        }
        OFLog.m181d(tag, "unsubscribe:" + type + " from: " + listener.getName());
        Map<String, IEventListener> eventMapping = this.subscription.get(type);
        if (eventMapping != null) {
            eventMapping.remove(listener.getName());
        }
        return true;
    }
}
