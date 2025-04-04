package com.alawar.mutant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.alawar.mutant.achievements.Achievements;
import com.alawar.mutant.billing.MutantPayment;
import com.alawar.mutant.jni.MutantMessages;
import com.alawar.mutant.network.NetworkService;
import com.alawar.mutant.network.http.RestClient;
import com.alawar.mutant.notification.NotificationHandler;
import com.alawar.mutant.p000ui.moregames.MoreGamesDialog;
import com.alawar.mutant.p000ui.reward.RewardsDialog;
import com.alawar.mutant.p000ui.share.ShareDialog;
import com.alawar.mutant.thirdparty.facebook.FacebookClient;
import com.alawar.mutant.thirdparty.flurry.MutantFlurry;
import com.alawar.mutant.thirdparty.openfeint.OpenFeintClient;
import com.alawar.mutant.thirdparty.openfeint.OpenFeintClientInitializedCallback;
import com.alawar.mutant.thirdparty.sponsorpay.SponsorPayActivity;
import com.alawar.mutant.util.DeviceUUID;
import com.arellomobile.android.push.PushManager;
import com.sugree.twitter.Util;
import java.util.Random;
import org.cocos2dx.lib.Cocos2dxActivity;

/* loaded from: classes.dex */
public class MutantDebugActivity extends Cocos2dxActivity {
    static Random random = new Random(System.currentTimeMillis());

    @Override // org.cocos2dx.lib.Cocos2dxActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.initialize(this);
        MutantMessages.initialize(this);
        MutantStats.initialize(this);
        DeviceUUID.init(this);
        RestClient.initialize(this);
        OpenFeintClient.instance().initialize(this, new OpenFeintClientInitializedCallback() { // from class: com.alawar.mutant.MutantDebugActivity.1
            @Override // com.alawar.mutant.thirdparty.openfeint.OpenFeintClientInitializedCallback
            public void onFinish() {
                Achievements.instance().initialize(OpenFeintClient.instance());
                MutantDebugActivity.this.findViewById(C0019R.id.test_ach).setEnabled(true);
            }
        });
        MutantPayment.initialize(this);
        NotificationHandler.initialize();
        NetworkService.initialize();
        PushManager pushManager = new PushManager(this);
        pushManager.onStartup(savedInstanceState, this);
        checkMessage(getIntent());
        setContentView(C0019R.layout.main);
        initButtons();
    }

    private void checkMessage(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
                showMessage(intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
                return;
            }
            if (intent.hasExtra(PushManager.REGISTER_EVENT) || intent.hasExtra(PushManager.UNREGISTER_EVENT) || intent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
            }
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, 1).show();
    }

    private void initButtons() {
        Button openFeintAchievementsButton = (Button) findViewById(C0019R.id.openfeint_achievements_button);
        openFeintAchievementsButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.MutantDebugActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MutantDebugActivity.this.onOpenFeintAchievements();
            }
        });
        Button facebookLoginButton = (Button) findViewById(C0019R.id.facebook_login_button);
        facebookLoginButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.MutantDebugActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MutantDebugActivity.this.onFacebookLogin();
            }
        });
        Button twitterLoginButton = (Button) findViewById(C0019R.id.twitter_login_button);
        twitterLoginButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.MutantDebugActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MutantDebugActivity.this.onTwitterLogin();
            }
        });
        Button getRewardButton = (Button) findViewById(C0019R.id.get_reward_button);
        getRewardButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.MutantDebugActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MutantDebugActivity.this.onGetReward();
            }
        });
        Button subscriptionButton = (Button) findViewById(C0019R.id.subscription_button);
        subscriptionButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.MutantDebugActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MutantDebugActivity.this.onSubscription();
            }
        });
        Button testAchButton = (Button) findViewById(C0019R.id.test_ach);
        testAchButton.setEnabled(false);
        testAchButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.MutantDebugActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MutantDebugActivity.this.onTestAchievement();
            }
        });
        Button addNotifButton = (Button) findViewById(C0019R.id.add_notification_button);
        addNotifButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.MutantDebugActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MutantDebugActivity.this.onAddNotification();
            }
        });
        Button offerWallButton = (Button) findViewById(C0019R.id.offerwall_button);
        offerWallButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.MutantDebugActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MutantDebugActivity.this.onOfferWall();
            }
        });
        Button balanceButton = (Button) findViewById(C0019R.id.balance_button);
        balanceButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.MutantDebugActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MutantDebugActivity.this.onBalance();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onOpenFeintAchievements() {
        MutantMessages.process(13, 0, MutantMessages.sEmpty);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onBalance() {
        Toast.makeText(this, "Balance: " + MutantMessages.process(1, 10, MutantMessages.sEmpty), 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onOfferWall() {
        SponsorPayActivity.openOfferWall();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAddNotification() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubscription() {
        new MoreGamesDialog(this).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGetReward() {
        new RewardsDialog(this).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTestAchievement() {
        if (OpenFeintClient.instance().isAvailable()) {
            Achievements.instance().award(1);
        } else {
            Util.showAlert(this, "OpenFeintClient", "OpenFeintClient is not initialized");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFacebookLogin() {
        new ShareDialog(this, "Message to Facebook").show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTwitterLogin() {
        new ShareDialog(this, "Message to Twitter " + random.nextInt(10000)).show();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkMessage(intent);
        setIntent(new Intent());
    }

    @Override // org.cocos2dx.lib.Cocos2dxActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onStart() {
        MutantFlurry.start(this);
        MutantPayment.start(this);
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onStop() {
        MutantPayment.stop(this);
        MutantFlurry.stop(this);
        super.onStop();
    }

    @Override // org.cocos2dx.lib.Cocos2dxActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        FacebookClient.instance().extendAccessTokenIfNeeded(this);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        MutantPayment.destroy(this);
        MutantStats.destroy();
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookClient.instance().authorized(requestCode, resultCode, data);
    }
}
