package com.alawar.mutant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import com.alawar.mutant.achievements.Achievements;
import com.alawar.mutant.billing.MutantPayment;
import com.alawar.mutant.jni.MutantMessages;
import com.alawar.mutant.network.NetworkService;
import com.alawar.mutant.network.http.RestClient;
import com.alawar.mutant.notification.NotificationHandler;
import com.alawar.mutant.notification.Notifications;
import com.alawar.mutant.thirdparty.facebook.FacebookClient;
import com.alawar.mutant.thirdparty.flurry.MutantFlurry;
import com.alawar.mutant.thirdparty.openfeint.OpenFeintClient;
import com.alawar.mutant.thirdparty.openfeint.OpenFeintClientInitializedCallback;
import com.alawar.mutant.util.DeviceUUID;
import com.sponsorpay.sdk.android.advertiser.SponsorPayAdvertiser;
import com.tapjoy.TapjoyConnect;
import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;

/* loaded from: classes.dex */
public class MutantActivity extends Cocos2dxActivity implements OnPostInitialization {
    private boolean initialized = false;
    private Cocos2dxGLSurfaceView mGLView;

    @Override // org.cocos2dx.lib.Cocos2dxActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(0);
        Global.initialize(this);
        MutantMessages.initialize(this);
        MutantStats.initialize(this);
        setPackageName(getApplication().getPackageName());
        setContentView(C0019R.layout.mutant);
        this.mGLView = (Cocos2dxGLSurfaceView) findViewById(C0019R.id.mutant_gl_surfaceview);
        this.mGLView.setTextField((EditText) findViewById(C0019R.id.textField));
        DeviceUUID.init(this);
        RestClient.initialize(this);
        NotificationHandler.initialize();
        NetworkService.initialize();
        SponsorPayAdvertiser.register(this);
        TapjoyConnect.requestTapjoyConnect(this, "a225951d-6825-4ada-9874-ab55ecdc9fb6", "QCjsKIE4vLUxtsywIPZO");
        OpenFeintClient.instance().initialize(this, new OpenFeintClientInitializedCallback() { // from class: com.alawar.mutant.MutantActivity.1
            @Override // com.alawar.mutant.thirdparty.openfeint.OpenFeintClientInitializedCallback
            public void onFinish() {
                Achievements.instance().initialize(OpenFeintClient.instance());
            }
        });
        this.initialized = true;
    }

    @Override // com.alawar.mutant.OnPostInitialization
    public void onPostInitialization() {
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(Notifications.TAG, "onNewIntent");
        setIntent(intent);
        Notifications.checkMessage(this, intent);
        setIntent(new Intent());
    }

    @Override // org.cocos2dx.lib.Cocos2dxActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        Log.i(Notifications.TAG, "onPause");
        if (this.initialized) {
            this.mGLView.onPause();
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        Log.i(Notifications.TAG, "onStart");
        MutantStats.initialize(this);
        MutantPayment.initialize(this);
        MutantFlurry.start(this);
        MutantPayment.start(this);
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onStop() {
        Log.i(Notifications.TAG, "onStop");
        MutantPayment.stop(this);
        MutantFlurry.stop(this);
        MutantPayment.destroy(this);
        MutantStats.destroy();
        super.onStop();
    }

    @Override // org.cocos2dx.lib.Cocos2dxActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        Log.i(Notifications.TAG, "onResume");
        if (this.initialized) {
            this.mGLView.onResume();
        }
        FacebookClient.instance().extendAccessTokenIfNeeded(this);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        Log.i(Notifications.TAG, "onDestroy");
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookClient.instance().authorized(requestCode, resultCode, data);
    }

    static {
        System.loadLibrary("cocos2d");
        System.loadLibrary("cocosdenshion");
        System.loadLibrary("pugixml");
        System.loadLibrary("mutant");
    }
}
