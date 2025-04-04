package com.alawar.mutant.jni;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.alawar.mutant.C0019R;
import com.alawar.mutant.Global;
import com.alawar.mutant.MutantStats;
import com.alawar.mutant.OnPostInitialization;
import com.alawar.mutant.achievements.Achievements;
import com.alawar.mutant.billing.MutantPayment;
import com.alawar.mutant.notification.Notification;
import com.alawar.mutant.notification.NotificationHandler;
import com.alawar.mutant.p000ui.common.ProgressBar;
import com.alawar.mutant.p000ui.reward.RewardsDialog;
import com.alawar.mutant.p000ui.share.ShareDialog;
import com.alawar.mutant.thirdparty.facebook.FacebookClient;
import com.alawar.mutant.thirdparty.flurry.MutantFlurry;
import com.alawar.mutant.thirdparty.twitter.TwitterClient;
import com.alawar.subscriber.SubscriberActivity;
import com.openfeint.api.OpenFeint;
import com.openfeint.api.p001ui.Dashboard;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MutantMessages {
    public static final int cFlurry = 2;
    public static final int cInitializationCompleted = 100;
    public static final int cMoreGames = 8;
    public static final int cOpenAchievements = 13;
    public static final int cOpenLeaderboard = 14;
    public static final int cPayment = 1;
    public static final int cPollEvents = 5;
    public static final int cProgress = 9;
    public static final int cPromo = 6;
    public static final int cRateApp = 12;
    public static final int cReportAchievement = 4;
    public static final int cReportTotalRating = 15;
    public static final int cShareWithFriends = 7;
    public static final int cShareWithFriendsImmediate = 11;
    public static final int cStats = 3;
    public static final int cSubscription = 10;
    static Activity context = null;
    public static final String sEmpty = "";
    public static final String sFail = "[MUTANT_RESULT:FAILED]";
    public static final String sSuccess = "[MUTANT_RESULT:OK]";

    public static void initialize(Activity cont) {
        context = cont;
    }

    private static String processSync(int cls, int id, String args) {
        StringBuilder ret = new StringBuilder();
        switch (cls) {
            case 1:
                Log.i("@PAYMENT MESSAGE", "[" + String.valueOf(id) + "]" + args);
                ret.append(MutantPayment.payProcess(id, args));
                Log.i("@ < PAYMENT MESSAGE", "[" + String.valueOf(id) + "]" + args + ", RET: " + ((Object) ret));
                break;
            case 3:
                Log.i("@STATS MESSAGE", "[" + String.valueOf(id) + "]" + args);
                ret.append(MutantStats.statsProcess(id, args));
                Log.i("@ < STATS MESSAGE", "[" + String.valueOf(id) + "]" + args + ", RET: " + ((Object) ret));
                break;
            case 5:
                Log.i("@POLL_EVENTS MESSAGE ", "[" + String.valueOf(id) + "]" + args);
                StringBuilder b = new StringBuilder();
                ArrayList<Notification> notif = NotificationHandler.popEvents();
                Iterator i$ = notif.iterator();
                while (i$.hasNext()) {
                    Notification n = i$.next();
                    if (b.length() > 0) {
                        b.append(":");
                    }
                    b.append("1*");
                    b.append(n.toString());
                }
                ret.append(b.length() > 0 ? b.toString() : sFail);
                Log.i("@ < POLL_EVENTS MESSAGE", "[" + String.valueOf(id) + "]" + args + ", RET: " + ((Object) ret));
                break;
        }
        return ret.length() == 0 ? sFail : ret.toString();
    }

    private static void processOnUiThread(final int cls, final int id, final String args) {
        final StringBuilder ret = new StringBuilder();
        context.runOnUiThread(new Runnable() { // from class: com.alawar.mutant.jni.MutantMessages.1
            @Override // java.lang.Runnable
            public void run() {
                switch (cls) {
                    case 2:
                        Log.i("@FLURRY MESSAGE", "[" + String.valueOf(id) + "]" + args);
                        ret.append(MutantFlurry.fluProcess(id, args));
                        Log.i("@ < FLURRY MESSAGE", "[" + String.valueOf(id) + "]" + args + ", RET: " + ((Object) ret));
                        break;
                    case 4:
                        Log.i("@REPORT_ACHIEVEMENT MESSAGE ", "[" + String.valueOf(id) + "]" + args);
                        Achievements.instance().award(id);
                        ret.append(MutantMessages.sSuccess);
                        Log.i("@ < REPORT_ACHIEVEMENT MESSAGE", "[" + String.valueOf(id) + "]" + args + ", RET: " + ((Object) ret));
                        break;
                    case 6:
                        new RewardsDialog(MutantMessages.context).show();
                        break;
                    case MutantMessages.cShareWithFriends /* 7 */:
                        new ShareDialog(MutantMessages.context, args).show();
                        break;
                    case 8:
                        Global.applicationContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://search?q=pub:Alawar Entertainment, Inc.")));
                        break;
                    case MutantMessages.cProgress /* 9 */:
                        ProgressBar.showProgress(id != 0);
                        break;
                    case 10:
                        Global.applicationContext.startActivity(new Intent(Global.applicationContext, (Class<?>) SubscriberActivity.class));
                        break;
                    case MutantMessages.cShareWithFriendsImmediate /* 11 */:
                        if ((id & 1) != 0) {
                            FacebookClient.instance().post(MutantMessages.context, args);
                        }
                        if ((id & 2) != 0) {
                            new TwitterClient().post(MutantMessages.context, args);
                            break;
                        }
                        break;
                    case MutantMessages.cRateApp /* 12 */:
                        Global.applicationContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.alawar.mutant")));
                        break;
                    case MutantMessages.cOpenAchievements /* 13 */:
                        if (MutantMessages.checkOpenFeintConnection()) {
                            Log.i("OpenFeint", "Opening achievements");
                            Dashboard.openAchievements();
                            break;
                        }
                        break;
                    case MutantMessages.cOpenLeaderboard /* 14 */:
                        if (MutantMessages.checkOpenFeintConnection()) {
                            Log.i("OpenFeint", "Opening leaderboards");
                            Dashboard.openLeaderboards();
                            break;
                        }
                        break;
                    case MutantMessages.cReportTotalRating /* 15 */:
                        Log.i("@REPORT_TOTAL_RATING MESSAGE ", "[" + String.valueOf(id) + "]" + args);
                        Achievements.instance().reportTotalRating(id);
                        ret.append(MutantMessages.sSuccess);
                        Log.i("@ < REPORT_TOTAL_RATING MESSAGE", "[" + String.valueOf(id) + "]" + args + ", RET: " + ((Object) ret));
                        break;
                    case MutantMessages.cInitializationCompleted /* 100 */:
                        ((OnPostInitialization) MutantMessages.context).onPostInitialization();
                        break;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean checkOpenFeintConnection() {
        if (!OpenFeint.isNetworkConnected()) {
            Toast.makeText(context, C0019R.string.internet_not_available, 3000);
            Log.w("OpenFeint", "No connection");
            return false;
        }
        if (!OpenFeint.isUserLoggedIn()) {
            Toast.makeText(context, C0019R.string.openfeint_not_logged_in, 3000);
            OpenFeint.login();
            Log.w("OpenFeint", "Not logged in");
            return false;
        }
        return true;
    }

    public static String process(int cls, int id, String args) {
        String ret = sEmpty;
        if (cls == 1 || cls == 3 || cls == 5) {
            ret = processSync(cls, id, args);
        } else {
            processOnUiThread(cls, id, args);
        }
        return ret.length() == 0 ? sSuccess : ret;
    }
}
