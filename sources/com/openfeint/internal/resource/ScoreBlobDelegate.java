package com.openfeint.internal.resource;

import com.openfeint.api.resource.Score;

/* loaded from: classes.dex */
public final class ScoreBlobDelegate {
    public static Score.BlobDownloadedDelegate sBlobDownloadedDelegate = null;

    public static final void notifyBlobDownloaded(Score score) {
        if (sBlobDownloadedDelegate != null && score.blob != null) {
            sBlobDownloadedDelegate.blobDownloadedForScore(score);
        }
    }
}
