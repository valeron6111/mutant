package com.alawar.mutant.network.http;

/* loaded from: classes.dex */
public interface RequestExecutedCallback {
    void onFailure(RestClient restClient);

    void onSuccess(RestClient restClient);
}
