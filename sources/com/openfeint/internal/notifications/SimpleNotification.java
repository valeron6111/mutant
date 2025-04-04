package com.openfeint.internal.notifications;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.openfeint.api.Notification;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.request.BitmapRequest;
import java.util.Map;

/* loaded from: classes.dex */
public class SimpleNotification extends NotificationBase {
    protected SimpleNotification(String text, String imageName, Notification.Category cat, Notification.Type type, Map<String, Object> userData) {
        super(text, imageName, cat, type, userData);
    }

    @Override // com.openfeint.internal.notifications.NotificationBase
    protected boolean createView() {
        LayoutInflater inflater = (LayoutInflater) OpenFeintInternal.getInstance().getContext().getSystemService("layout_inflater");
        this.displayView = inflater.inflate(C0207RR.layout("of_simple_notification"), (ViewGroup) null);
        ((TextView) this.displayView.findViewById(C0207RR.m180id("of_text"))).setText(getText());
        final ImageView icon = (ImageView) this.displayView.findViewById(C0207RR.m180id("of_icon"));
        if (this.imageName != null) {
            Drawable image = getResourceDrawable(this.imageName);
            if (image == null) {
                BitmapRequest req = new BitmapRequest() { // from class: com.openfeint.internal.notifications.SimpleNotification.1
                    @Override // com.openfeint.internal.request.BaseRequest
                    public String path() {
                        return SimpleNotification.this.imageName;
                    }

                    @Override // com.openfeint.internal.request.BitmapRequest
                    public void onSuccess(Bitmap responseBody) {
                        icon.setImageDrawable(new BitmapDrawable(responseBody));
                        SimpleNotification.this.showToast();
                    }

                    @Override // com.openfeint.internal.request.DownloadRequest
                    public void onFailure(String exceptionMessage) {
                        OFLog.m182e("NotificationImage", "Failed to load image " + SimpleNotification.this.imageName + ":" + exceptionMessage);
                        icon.setVisibility(4);
                        SimpleNotification.this.showToast();
                    }
                };
                req.launch();
                return false;
            }
            icon.setImageDrawable(image);
        } else {
            icon.setVisibility(4);
        }
        return true;
    }

    @Override // com.openfeint.internal.notifications.NotificationBase
    protected void drawView(Canvas canvas) {
        this.displayView.draw(canvas);
    }

    public static void show(String text, Notification.Category c, Notification.Type t) {
        show(text, null, c, t);
    }

    public static void show(String text, String imageName, Notification.Category c, Notification.Type t) {
        SimpleNotification notification = new SimpleNotification(text, imageName, c, t, null);
        notification.checkDelegateAndView();
    }
}
