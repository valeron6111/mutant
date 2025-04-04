package com.openfeint.internal.analytics.internal;

import com.openfeint.internal.analytics.AnalyticsManager;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.request.BaseRequest;
import com.openfeint.internal.request.OrderedArgList;

/* loaded from: classes.dex */
public class AnalyticsRequest extends BaseRequest {
    Long endid;
    Long startid;
    public final String tag;

    public AnalyticsRequest(Long startid, Long endid, OrderedArgList arg) {
        super(arg);
        this.tag = "AnalyticsRequest";
        this.startid = startid;
        this.endid = endid;
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public String method() {
        return "POST";
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public String path() {
        return "/xp/events";
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public void onResponse(int responseCode, byte[] body) {
        OFLog.m184v("AnalyticsRequest", "ResponseCode:" + responseCode);
        if (200 <= responseCode && responseCode < 300) {
            OFLog.m181d("AnalyticsRequest", String.format("push log num form %d to %d, Success!", this.startid, this.endid));
            AnalyticsManager.instance().deleteLog(this.startid, this.endid);
            AnalyticsManager.instance().unlock();
        } else {
            OFLog.m185w("AnalyticsRequest", String.format("push log num form %d to %d, Failed!", this.startid, this.endid));
            AnalyticsManager.instance().unlock();
        }
    }
}
