package com.alawar.mutant.p000ui.reward;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.alawar.mutant.C0019R;
import com.alawar.mutant.jni.MutantMessages;
import com.alawar.mutant.network.Coins;
import com.alawar.mutant.network.UserService;
import com.alawar.mutant.util.RUtil;
import com.sugree.twitter.Util;

/* loaded from: classes.dex */
public class PromoCodeDialog extends Dialog {
    public PromoCodeDialog(Context context) {
        super(context);
        setCancelable(true);
        setTitle(C0019R.string.promo_code_title);
        setContentView(LayoutInflater.from(context).inflate(C0019R.layout.gift_code, (ViewGroup) null));
        Button getGiftButton = (Button) findViewById(C0019R.id.get_gift_button);
        getGiftButton.setOnClickListener(new View.OnClickListener() { // from class: com.alawar.mutant.ui.reward.PromoCodeDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PromoCodeDialog.this.onGetGift();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGetGift() {
        String promoCode = ((EditText) findViewById(C0019R.id.gift_code)).getText().toString();
        if (promoCode.trim().equalsIgnoreCase(MutantMessages.sEmpty)) {
            Util.showAlert(getContext(), RUtil.getString(getContext(), C0019R.string.gift_code), RUtil.getString(getContext(), C0019R.string.gift_fail));
            return;
        }
        Coins coins = UserService.instance.submitPromoCode(promoCode);
        if (coins == null) {
            Util.showAlert(getContext(), RUtil.getString(getContext(), C0019R.string.gift_code), RUtil.getString(getContext(), C0019R.string.gift_fail));
        } else {
            Util.showAlert(getContext(), RUtil.getString(getContext(), C0019R.string.gift_code), String.format(RUtil.getString(getContext(), C0019R.string.gift_success), Integer.valueOf(coins.amount)));
        }
        dismiss();
    }
}
