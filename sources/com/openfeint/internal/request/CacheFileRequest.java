package com.openfeint.internal.request;

import com.openfeint.internal.Util;
import com.openfeint.internal.logcat.OFLog;

/* loaded from: classes.dex */
public class CacheFileRequest extends CacheRequest {
    private static final String TAG = "CacheFile";
    protected String path;
    protected String url;

    public CacheFileRequest(String path, String url, String key) {
        super(key);
        this.path = path;
        this.url = url;
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public void onResponse(int responseCode, byte[] body) {
        if (responseCode == 200) {
            try {
                Util.saveFile(body, this.path);
                super.on200Response();
            } catch (Exception e) {
                OFLog.m182e(TAG, e.toString());
            }
        }
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public String path() {
        return this.url;
    }
}
