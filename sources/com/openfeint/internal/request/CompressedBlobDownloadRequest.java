package com.openfeint.internal.request;

import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class CompressedBlobDownloadRequest extends DownloadRequest {
    protected abstract void onSuccessDecompress(byte[] bArr);

    @Override // com.openfeint.internal.request.DownloadRequest
    protected final void onSuccess(byte[] body) {
        try {
            onSuccessDecompress(Compression.decompress(body));
        } catch (IOException e) {
            onFailure(OpenFeintInternal.getRString(C0207RR.string("of_io_exception_on_download")));
        }
    }
}
