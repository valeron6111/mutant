package com.openfeint.api.resource;

import com.openfeint.internal.APICallback;
import com.openfeint.internal.request.JSONRequest;
import com.openfeint.internal.resource.DateResourceProperty;
import com.openfeint.internal.resource.LongResourceProperty;
import com.openfeint.internal.resource.Resource;
import com.openfeint.internal.resource.ResourceClass;
import com.tapjoy.TapjoyConstants;
import java.util.Date;

/* loaded from: classes.dex */
public class ServerTimestamp extends Resource {
    public long secondsSinceEpoch;
    public Date timestamp;

    public static abstract class GetCB extends APICallback {
        public abstract void onSuccess(ServerTimestamp serverTimestamp);
    }

    public static void get(final GetCB cb) {
        JSONRequest req = new JSONRequest() { // from class: com.openfeint.api.resource.ServerTimestamp.1
            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "GET";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return "/xp/server_timestamp";
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onSuccess(Object responseBody) {
                if (GetCB.this != null) {
                    GetCB.this.onSuccess((ServerTimestamp) responseBody);
                }
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onFailure(String exceptionMessage) {
                super.onFailure(exceptionMessage);
                if (GetCB.this != null) {
                    GetCB.this.onFailure(exceptionMessage);
                }
            }
        };
        req.launch();
    }

    public static ResourceClass getResourceClass() {
        ResourceClass klass = new ResourceClass(ServerTimestamp.class, "server_timestamp") { // from class: com.openfeint.api.resource.ServerTimestamp.2
            @Override // com.openfeint.internal.resource.ResourceClass
            public Resource factory() {
                return new ServerTimestamp();
            }
        };
        klass.mProperties.put(TapjoyConstants.TJC_TIMESTAMP, new DateResourceProperty() { // from class: com.openfeint.api.resource.ServerTimestamp.3
            @Override // com.openfeint.internal.resource.DateResourceProperty
            public Date get(Resource obj) {
                return ((ServerTimestamp) obj).timestamp;
            }

            @Override // com.openfeint.internal.resource.DateResourceProperty
            public void set(Resource obj, Date val) {
                ((ServerTimestamp) obj).timestamp = val;
            }
        });
        klass.mProperties.put("seconds_since_epoch", new LongResourceProperty() { // from class: com.openfeint.api.resource.ServerTimestamp.4
            @Override // com.openfeint.internal.resource.LongResourceProperty
            public long get(Resource obj) {
                return ((ServerTimestamp) obj).secondsSinceEpoch;
            }

            @Override // com.openfeint.internal.resource.LongResourceProperty
            public void set(Resource obj, long val) {
                ((ServerTimestamp) obj).secondsSinceEpoch = val;
            }
        });
        return klass;
    }
}
