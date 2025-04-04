package com.openfeint.internal.notifications;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;
import com.openfeint.api.Notification;
import com.openfeint.internal.OpenFeintInternal;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class NotificationBase extends Notification {
    static Map<String, Drawable> drawableCache = new HashMap();
    private Notification.Category category;
    View displayView;
    protected String imageName;
    private String text;
    Toast toast;
    private Notification.Type type;
    private Map<String, Object> userData;

    protected abstract boolean createView();

    protected abstract void drawView(Canvas canvas);

    @Override // com.openfeint.api.Notification
    public String getText() {
        return this.text;
    }

    @Override // com.openfeint.api.Notification
    public Notification.Category getCategory() {
        return this.category;
    }

    @Override // com.openfeint.api.Notification
    public Notification.Type getType() {
        return this.type;
    }

    @Override // com.openfeint.api.Notification
    public Map<String, Object> getUserData() {
        return this.userData;
    }

    protected NotificationBase(String _text, String _imageName, Notification.Category _cat, Notification.Type _type, Map<String, Object> _userData) {
        this.text = _text;
        this.imageName = _imageName;
        this.category = _cat;
        this.type = _type;
        this.userData = _userData;
    }

    protected String clippedText(Paint paint, String text, int length) {
        int endLength = paint.breakText(text, true, length, null);
        if (endLength < text.length()) {
            String outText = text.substring(0, endLength - 1) + "...";
            return outText;
        }
        return text;
    }

    protected void showToast() {
        OpenFeintInternal.getInstance().runOnUiThread(new Runnable() { // from class: com.openfeint.internal.notifications.NotificationBase.1
            @Override // java.lang.Runnable
            public void run() {
                Context appContext = OpenFeintInternal.getInstance().getContext();
                NotificationBase.this.toast = new Toast(appContext);
                NotificationBase.this.toast.setGravity(80, 0, 0);
                NotificationBase.this.toast.setDuration(1);
                NotificationBase.this.toast.setView(NotificationBase.this.displayView);
                NotificationBase.this.toast.show();
            }
        });
    }

    static Drawable getResourceDrawable(String name) {
        if (!drawableCache.containsKey(name)) {
            OpenFeintInternal ofi = OpenFeintInternal.getInstance();
            int bitmapHandle = ofi.getResource(name);
            if (bitmapHandle == 0) {
                drawableCache.put(name, null);
            } else {
                Resources r = ofi.getContext().getResources();
                drawableCache.put(name, r.getDrawable(bitmapHandle));
            }
        }
        return drawableCache.get(name);
    }

    protected void checkDelegateAndView() {
        if (getDelegate().canShowNotification(this)) {
            getDelegate().notificationWillShow(this);
            if (createView()) {
                showToast();
                return;
            }
            return;
        }
        getDelegate().displayNotification(this);
    }
}
