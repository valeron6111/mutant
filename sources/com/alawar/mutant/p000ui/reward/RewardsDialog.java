package com.alawar.mutant.p000ui.reward;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import com.alawar.mutant.C0019R;
import com.alawar.mutant.Global;
import com.alawar.mutant.jni.MutantMessages;
import com.alawar.mutant.network.Coins;
import com.alawar.mutant.network.UserService;
import com.alawar.mutant.thirdparty.facebook.FacebookClient;
import com.alawar.mutant.thirdparty.twitter.TwitterClient;
import com.alawar.mutant.util.DeviceUUID;
import com.alawar.mutant.util.RUtil;
import com.sugree.twitter.Util;

/* loaded from: classes.dex */
public class RewardsDialog extends Dialog {
    private TabHost tabs;

    public RewardsDialog(Context context) {
        super(context);
        requestWindowFeature(1);
        setContentView(LayoutInflater.from(context).inflate(C0019R.layout.rewards, (ViewGroup) null));
        this.tabs = (TabHost) findViewById(C0019R.id.rewards_tabhost);
        this.tabs.setup();
        TabHost.TabSpec tabButtons = this.tabs.newTabSpec("tabButtons");
        tabButtons.setContent(C0019R.id.rewards_buttons_tab);
        tabButtons.setIndicator(MutantMessages.sEmpty);
        this.tabs.addTab(tabButtons);
        TabHost.TabSpec tabInviteFriends = this.tabs.newTabSpec("tabInviteFriends");
        tabInviteFriends.setContent(C0019R.id.invite_friends_tab);
        tabInviteFriends.setIndicator(MutantMessages.sEmpty);
        this.tabs.addTab(tabInviteFriends);
        TabHost.TabSpec tabGiftCode = this.tabs.newTabSpec("tabGiftCode");
        tabGiftCode.setContent(C0019R.id.gift_code_tab);
        tabGiftCode.setIndicator(MutantMessages.sEmpty);
        this.tabs.addTab(tabGiftCode);
        TabHost.TabSpec tabPromoCode = this.tabs.newTabSpec("tabPromoCode");
        tabPromoCode.setContent(C0019R.id.promo_code_tab);
        tabPromoCode.setIndicator(MutantMessages.sEmpty);
        this.tabs.addTab(tabPromoCode);
        this.tabs.setCurrentTab(0);
        ((TextView) findViewById(C0019R.id.installation_id)).setText(DeviceUUID.getInstallationId());
        Button inviteFriendsButton = (Button) findViewById(C0019R.id.invite_friends_button);
        inviteFriendsButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.RewardsDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RewardsDialog.this.tabs.setCurrentTab(1);
            }
        });
        Button giftCodeButton = (Button) findViewById(C0019R.id.gift_code_button);
        giftCodeButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.RewardsDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ((EditText) RewardsDialog.this.findViewById(C0019R.id.gift_code)).setText(MutantMessages.sEmpty);
                RewardsDialog.this.tabs.setCurrentTab(2);
            }
        });
        Button getPromoCodeButton = (Button) findViewById(C0019R.id.get_promo_code_button);
        getPromoCodeButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.RewardsDialog.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Global.applicationContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.facebook.com/MutantMobile")));
            }
        });
        Button promoCodeButton = (Button) findViewById(C0019R.id.promo_code_button);
        promoCodeButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.RewardsDialog.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ((EditText) RewardsDialog.this.findViewById(C0019R.id.promo_code)).setText(MutantMessages.sEmpty);
                RewardsDialog.this.tabs.setCurrentTab(3);
            }
        });
        View.OnClickListener backButtonListener = new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.RewardsDialog.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RewardsDialog.this.tabs.setCurrentTab(0);
            }
        };
        createTab1(backButtonListener);
        createTab2(backButtonListener);
        createTab3(backButtonListener);
    }

    private void createTab1(View.OnClickListener backButtonListener) {
        Button backButton1 = (Button) findViewById(C0019R.id.back_button_1);
        backButton1.setOnClickListener(backButtonListener);
        Button inviteFriendFacebook = (Button) findViewById(C0019R.id.invite_friends_facebook);
        inviteFriendFacebook.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.RewardsDialog.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String message = String.format(RewardsDialog.this.getContext().getResources().getString(C0019R.string.gift_code_message_facebook), DeviceUUID.getInstallationId());
                FacebookClient.instance().post(Global.applicationContext, message);
            }
        });
        Button inviteFriendTwitter = (Button) findViewById(C0019R.id.invite_friends_twitter);
        inviteFriendTwitter.setVisibility(8);
        inviteFriendTwitter.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.RewardsDialog.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String message = String.format(RewardsDialog.this.getContext().getResources().getString(C0019R.string.gift_code_message_twitter), DeviceUUID.getInstallationId());
                new TwitterClient().post(Global.applicationContext, message);
            }
        });
    }

    private void createTab2(View.OnClickListener backButtonListener) {
        Button backButton2 = (Button) findViewById(C0019R.id.back_button_2);
        backButton2.setOnClickListener(backButtonListener);
        Button getGiftButton = (Button) findViewById(C0019R.id.get_gift_button);
        getGiftButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.RewardsDialog.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RewardsDialog.this.onGetGift();
            }
        });
    }

    private void createTab3(View.OnClickListener backButtonListener) {
        Button backButton3 = (Button) findViewById(C0019R.id.back_button_3);
        backButton3.setOnClickListener(backButtonListener);
        Button getPromoButton = (Button) findViewById(C0019R.id.get_promo_button);
        getPromoButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.RewardsDialog.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RewardsDialog.this.onGetPromo();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGetGift() {
        String giftCode = ((EditText) findViewById(C0019R.id.gift_code)).getText().toString().trim();
        if (giftCode.equalsIgnoreCase(MutantMessages.sEmpty)) {
            Util.showAlert(getContext(), RUtil.getString(getContext(), C0019R.string.gift_code), RUtil.getString(getContext(), C0019R.string.gift_fail));
            return;
        }
        Coins coins = UserService.instance.submitGiftCode(giftCode);
        if (coins == null) {
            Util.showAlert(getContext(), RUtil.getString(getContext(), C0019R.string.gift_code), RUtil.getString(getContext(), C0019R.string.gift_fail));
        } else {
            Util.showAlert(getContext(), RUtil.getString(getContext(), C0019R.string.gift_code), String.format(RUtil.getString(getContext(), C0019R.string.gift_success), Integer.valueOf(coins.amount)));
            dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGetPromo() {
        String promoCode = ((EditText) findViewById(C0019R.id.promo_code)).getText().toString().trim();
        if (promoCode.equalsIgnoreCase(MutantMessages.sEmpty)) {
            Util.showAlert(getContext(), RUtil.getString(getContext(), C0019R.string.promo_code), RUtil.getString(getContext(), C0019R.string.promo_fail));
            return;
        }
        Coins coins = UserService.instance.submitPromoCode(promoCode);
        if (coins == null) {
            Util.showAlert(getContext(), RUtil.getString(getContext(), C0019R.string.promo_code), RUtil.getString(getContext(), C0019R.string.promo_fail));
        } else {
            Util.showAlert(getContext(), RUtil.getString(getContext(), C0019R.string.promo_code), String.format(RUtil.getString(getContext(), C0019R.string.promo_success), Integer.valueOf(coins.amount)));
            dismiss();
        }
    }
}
