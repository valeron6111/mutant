package com.openfeint.api.resource;

import com.openfeint.internal.APICallback;
import com.openfeint.internal.request.JSONRequest;
import com.openfeint.internal.request.OrderedArgList;
import com.openfeint.internal.resource.Resource;
import com.openfeint.internal.resource.ResourceClass;

/* loaded from: classes.dex */
public class CurrentUser extends User {

    public static abstract class BefriendCB extends APICallback {
        public abstract void onSuccess();
    }

    public void befriend(User userToBefriend, final BefriendCB cb) {
        OrderedArgList args = new OrderedArgList();
        args.put("friend_id", userToBefriend.resourceID());
        JSONRequest req = new JSONRequest(args) { // from class: com.openfeint.api.resource.CurrentUser.1
            @Override // com.openfeint.internal.request.BaseRequest
            public boolean wantsLogin() {
                return true;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "POST";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return "/xp/friend_requests";
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onSuccess(Object responseBody) {
                if (cb != null) {
                    cb.onSuccess();
                }
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onFailure(String exceptionMessage) {
                super.onFailure(exceptionMessage);
                if (cb != null) {
                    cb.onFailure(exceptionMessage);
                }
            }
        };
        req.launch();
    }

    public static ResourceClass getResourceClass() {
        return new ResourceClass(CurrentUser.class, "current_user") { // from class: com.openfeint.api.resource.CurrentUser.2
            @Override // com.openfeint.internal.resource.ResourceClass
            public Resource factory() {
                return new CurrentUser();
            }
        };
    }
}
