package com.openfeint.internal.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;

/* loaded from: classes.dex */
public abstract class BitmapRequest extends DownloadRequest {
    public void onSuccess(Bitmap responseBody) {
    }

    @Override // com.openfeint.internal.request.DownloadRequest
    protected void onSuccess(byte[] body) {
        Bitmap b = BitmapFactory.decodeByteArray(body, 0, body.length);
        if (b != null) {
            onSuccess(b);
        } else {
            onFailure(OpenFeintInternal.getRString(C0207RR.string("of_bitmap_decode_error")));
        }
    }
}
