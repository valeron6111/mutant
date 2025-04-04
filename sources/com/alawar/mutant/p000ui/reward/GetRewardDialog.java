package com.alawar.mutant.p000ui.reward;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.alawar.mutant.C0019R;

/* loaded from: classes.dex */
public class GetRewardDialog extends Dialog {
    public GetRewardDialog(Context context) {
        super(context);
        setCancelable(true);
        setTitle(C0019R.string.get_reward_title);
        setContentView(LayoutInflater.from(context).inflate(C0019R.layout.get_reward, (ViewGroup) null));
        Button giftCodeButton = (Button) findViewById(C0019R.id.gift_code_button);
        giftCodeButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.GetRewardDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                GetRewardDialog.this.onGiftCode();
            }
        });
        Button inviteFriendsButton = (Button) findViewById(C0019R.id.invite_friends_button);
        inviteFriendsButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.GetRewardDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                GetRewardDialog.this.onInviteFriends();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGiftCode() {
        dismiss();
        new PromoCodeDialog(getContext()).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInviteFriends() {
        dismiss();
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
