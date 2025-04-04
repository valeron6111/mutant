package com.alawar.mutant.p000ui.common;

import android.app.Activity;
import android.app.ProgressDialog;
import com.alawar.mutant.C0019R;
import com.alawar.mutant.Global;

/* loaded from: classes.dex */
public class ProgressBar {
    private static ProgressDialog dialog;

    public static void showProgress(boolean show) {
        if (!show) {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
                return;
            }
            return;
        }
        if (dialog == null) {
            Activity appContext = Global.applicationContext;
            dialog = new ProgressDialog(appContext);
            dialog.setProgressStyle(0);
            dialog.setMessage(appContext.getString(C0019R.string.progress_text));
            dialog.setIndeterminate(true);
            dialog.setOwnerActivity(appContext);
            dialog.show();
        }
    }
}
