package com.openfeint.api;

import java.util.Map;

/* loaded from: classes.dex */
public abstract class Notification {
    private static Delegate sDelegate = new Delegate();

    public enum Category {
        Foreground,
        Login,
        Challenge,
        HighScore,
        Leaderboard,
        Achievement,
        SocialNotification,
        Presence,
        Multiplayer
    }

    public enum Type {
        None,
        Submitting,
        Downloading,
        Error,
        Success,
        NewResources,
        UserPresenceOnLine,
        UserPresenceOffline,
        NewMessage,
        Multiplayer,
        NetworkOffline
    }

    public abstract Category getCategory();

    public abstract String getText();

    public abstract Type getType();

    public abstract Map<String, Object> getUserData();

    public static class Delegate {
        public boolean canShowNotification(Notification notification) {
            return true;
        }

        public void displayNotification(Notification notification) {
        }

        public void notificationWillShow(Notification notification) {
        }
    }

    public static Delegate getDelegate() {
        return sDelegate;
    }

    public static void setDelegate(Delegate delegate) {
        sDelegate = delegate;
    }
}
