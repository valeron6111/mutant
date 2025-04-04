package com.alawar.mutant.p000ui.share;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.alawar.mutant.C0019R;
import com.alawar.mutant.thirdparty.facebook.FacebookClient;
import com.alawar.mutant.thirdparty.twitter.TwitterClient;

/* loaded from: classes.dex */
public class ShareDialog extends Dialog {
    public ShareDialog(final Activity context, final String message) {
        super(context);
        requestWindowFeature(1);
        setContentView(LayoutInflater.from(context).inflate(C0019R.layout.share, (ViewGroup) null));
        TextView shareText = (TextView) findViewById(C0019R.id.share_with_friends_text);
        shareText.setText(((Object) shareText.getText()) + "\n" + message);
        Button inviteFriendFacebook = (Button) findViewById(C0019R.id.share_with_friends_facebook);
        inviteFriendFacebook.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.share.ShareDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ShareDialog.this.dismiss();
                FacebookClient.instance().post(context, message);
            }
        });
        Button inviteFriendTwitter = (Button) findViewById(C0019R.id.share_with_friends_twitter);
        inviteFriendTwitter.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.share.ShareDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ShareDialog.this.dismiss();
                new TwitterClient().post(context, message);
            }
        });
    }
}
