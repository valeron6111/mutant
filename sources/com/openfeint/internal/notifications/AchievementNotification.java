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
import com.openfeint.api.resource.Achievement;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.request.BitmapRequest;
import com.openfeint.internal.request.ExternalBitmapRequest;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class AchievementNotification extends NotificationBase {
    protected AchievementNotification(Achievement achievement, Map<String, Object> userData) {
        super(OpenFeintInternal.getRString(C0207RR.string("of_achievement_unlocked")), null, Notification.Category.Achievement, Notification.Type.Success, userData);
    }

    public void loadedImage(Bitmap map) {
        this.displayView.invalidate();
    }

    @Override // com.openfeint.internal.notifications.NotificationBase
    protected boolean createView() {
        final Achievement achievement = (Achievement) getUserData().get("achievement");
        LayoutInflater inflater = (LayoutInflater) OpenFeintInternal.getInstance().getContext().getSystemService("layout_inflater");
        this.displayView = inflater.inflate(C0207RR.layout("of_achievement_notification"), (ViewGroup) null);
        if (achievement.isUnlocked) {
            this.displayView.findViewById(C0207RR.m180id("of_achievement_progress_icon")).setVisibility(4);
            if (achievement.gamerscore == 0) {
                this.displayView.findViewById(C0207RR.m180id("of_achievement_score_icon")).setVisibility(4);
                this.displayView.findViewById(C0207RR.m180id("of_achievement_score")).setVisibility(4);
            }
        } else {
            this.displayView.findViewById(C0207RR.m180id("of_achievement_score_icon")).setVisibility(4);
        }
        ((TextView) this.displayView.findViewById(C0207RR.m180id("of_achievement_text"))).setText((achievement.title == null || achievement.title.length() <= 0) ? OpenFeintInternal.getRString(C0207RR.string("of_achievement_unlocked")) : achievement.title);
        String scoreText = achievement.isUnlocked ? Integer.toString(achievement.gamerscore) : String.format("%d%%", Integer.valueOf((int) achievement.percentComplete));
        ((TextView) this.displayView.findViewById(C0207RR.m180id("of_achievement_score"))).setText(scoreText);
        if (achievement.iconUrl != null) {
            Drawable iconImage = getResourceDrawable(achievement.iconUrl);
            if (iconImage == null) {
                BitmapRequest req = new ExternalBitmapRequest(achievement.iconUrl) { // from class: com.openfeint.internal.notifications.AchievementNotification.1
                    @Override // com.openfeint.internal.request.BitmapRequest
                    public void onSuccess(Bitmap responseBody) {
                        ((ImageView) AchievementNotification.this.displayView.findViewById(C0207RR.m180id("of_achievement_icon"))).setImageDrawable(new BitmapDrawable(responseBody));
                        AchievementNotification.this.showToast();
                    }

                    @Override // com.openfeint.internal.request.DownloadRequest
                    public void onFailure(String exceptionMessage) {
                        OFLog.m182e("NotificationImage", "Failed to load image " + achievement.iconUrl + ":" + exceptionMessage);
                        AchievementNotification.this.showToast();
                    }
                };
                req.launch();
                return false;
            }
            ((ImageView) this.displayView.findViewById(C0207RR.m180id("of_achievement_icon"))).setImageDrawable(iconImage);
        }
        return true;
    }

    @Override // com.openfeint.internal.notifications.NotificationBase
    protected void drawView(Canvas canvas) {
        this.displayView.draw(canvas);
    }

    public static void showStatus(Achievement achievement) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("achievement", achievement);
        AchievementNotification notification = new AchievementNotification(achievement, userData);
        notification.checkDelegateAndView();
    }
}
