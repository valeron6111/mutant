package com.alawar.mutant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import com.alawar.mutant.notification.Notifications;
import com.arellomobile.android.push.PushManager;

/* loaded from: classes.dex */
public class PreloaderActivity extends Activity {
    private static final String TAG = "MutantPreloader";
    boolean disappearing = false;
    private ImageView image;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MutantActivity.instance != null) {
            startGame();
            return;
        }
        setContentView(C0019R.layout.splash_screen);
        findViewById(C0019R.id.splash_screen);
        this.image = (ImageView) findViewById(C0019R.id.logo_image);
        PushManager pushManager = new PushManager(this);
        pushManager.onStartup(savedInstanceState, this);
        Notifications.checkMessage(this, getIntent());
        startAppearing();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");
        setIntent(intent);
        Notifications.checkMessage(getActiveGameActivity(), intent);
        setIntent(new Intent());
    }

    private Activity getActiveGameActivity() {
        return MutantActivity.instance == null ? this : MutantActivity.instance;
    }

    private void startAppearing() {
        this.disappearing = false;
        Animation appearAnimation = new AlphaAnimation(0.0f, 1.0f);
        appearAnimation.setDuration(1000L);
        appearAnimation.setAnimationListener(new AnimationAnimationListenerC00171());
        this.image.startAnimation(appearAnimation);
    }

    /* renamed from: com.alawar.mutant.PreloaderActivity$1 */
    class AnimationAnimationListenerC00171 implements Animation.AnimationListener {
        AnimationAnimationListenerC00171() {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
        }

        /* JADX WARN: Type inference failed for: r0v2, types: [com.alawar.mutant.PreloaderActivity$1$2] */
        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            PreloaderActivity.this.image.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.PreloaderActivity.1.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    PreloaderActivity.this.startDisappearing();
                }
            });
            new Thread() { // from class: com.alawar.mutant.PreloaderActivity.1.2
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                    }
                    PreloaderActivity.this.runOnUiThread(new Runnable() { // from class: com.alawar.mutant.PreloaderActivity.1.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            PreloaderActivity.this.startDisappearing();
                        }
                    });
                }
            }.start();
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDisappearing() {
        if (!this.disappearing) {
            this.disappearing = true;
            Animation disappearAnimation = new AlphaAnimation(1.0f, 0.0f);
            disappearAnimation.setDuration(1000L);
            disappearAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.alawar.mutant.PreloaderActivity.2
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    PreloaderActivity.this.image.setVisibility(8);
                    PreloaderActivity.this.startGame();
                    PreloaderActivity.this.finish();
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }
            });
            this.image.startAnimation(disappearAnimation);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startGame() {
        Intent intent = new Intent();
        intent.setClass(this, MutantActivity.class);
        startActivity(intent);
    }
}
