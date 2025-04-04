package com.alawar.subscriber;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import com.alawar.mutant.C0019R;
import com.alawar.subscriber.Subscriber;

/* loaded from: classes.dex */
public class SubscriberActivity extends Activity implements TextWatcher, Subscriber.SubscriberListener {
    private Subscriber mAlawarSubscriber = null;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String appName = getString(C0019R.string.app_name);
        setContentView(C0019R.layout.subscriber_layout);
        ViewGroup subLayout = (ViewGroup) findViewById(C0019R.id.NewsletterLayout);
        this.mAlawarSubscriber = new Subscriber(appName, this, subLayout);
        this.mAlawarSubscriber.setSubscriberListener(this);
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        this.mAlawarSubscriber.showView();
    }

    @Override // com.alawar.subscriber.Subscriber.SubscriberListener
    public void OnSubscriberHide() {
        finish();
    }

    @Override // com.alawar.subscriber.Subscriber.SubscriberListener
    public void OnSubscriberShow() {
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
