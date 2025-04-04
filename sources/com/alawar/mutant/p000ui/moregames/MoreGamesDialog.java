package com.alawar.mutant.p000ui.moregames;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.alawar.mutant.C0019R;
import com.alawar.subscriber.SubscriberActivity;

/* loaded from: classes.dex */
public class MoreGamesDialog extends Dialog {
    public MoreGamesDialog(Context context) {
        super(context);
        requestWindowFeature(1);
        setContentView(LayoutInflater.from(context).inflate(C0019R.layout.more_games, (ViewGroup) null));
        Button subscriptionButton = (Button) findViewById(C0019R.id.newsletter_subscription_button);
        subscriptionButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.moregames.MoreGamesDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MoreGamesDialog.this.onSubscription();
            }
        });
        Button moreGamesButton = (Button) findViewById(C0019R.id.more_games_button);
        moreGamesButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.moregames.MoreGamesDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MoreGamesDialog.this.onMoreGames();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubscription() {
        dismiss();
        getContext().startActivity(new Intent(getContext(), (Class<?>) SubscriberActivity.class));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMoreGames() {
        dismiss();
        getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://search?q=pub:Alawar Entertainment, Inc.")));
    }
}
