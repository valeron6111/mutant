package com.openfeint.internal.p004ui;

import android.content.Intent;
import android.graphics.Bitmap;
import com.openfeint.api.OpenFeint;
import com.openfeint.internal.ImagePicker;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.Util5;
import com.openfeint.internal.p004ui.WebNav;
import com.openfeint.internal.request.IRawRequestDelegate;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class IntroFlow extends WebNav {
    Bitmap cachedImage;

    @Override // com.openfeint.internal.p004ui.WebNav
    protected String initialContentPath() {
        String contentName = getIntent().getStringExtra("content_name");
        return contentName != null ? "intro/" + contentName : "intro/index";
    }

    @Override // com.openfeint.internal.p004ui.WebNav, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        if (ImagePicker.isImagePickerActivityResult(requestCode)) {
            this.cachedImage = ImagePicker.onImagePickerActivityResult(this, resultCode, 152, returnedIntent);
        }
    }

    @Override // com.openfeint.internal.p004ui.WebNav
    protected WebNav.ActionHandler createActionHandler(WebNav webNav) {
        return new IntroFlowActionHandler(webNav);
    }

    private class IntroFlowActionHandler extends WebNav.ActionHandler {
        public IntroFlowActionHandler(WebNav webNav) {
            super(webNav);
        }

        @Override // com.openfeint.internal.ui.WebNav.ActionHandler
        protected void populateActionList(List<String> actionList) {
            super.populateActionList(actionList);
            actionList.add("createUser");
            actionList.add("loginUser");
            actionList.add("cacheImage");
            actionList.add("uploadImage");
            actionList.add("clearImage");
            actionList.add("decline");
            actionList.add("getEmail");
        }

        public final void createUser(final Map<String, String> options) {
            OpenFeintInternal.getInstance().createUser(options.get("name"), options.get("email"), options.get("password"), options.get("password_confirmation"), new IRawRequestDelegate() { // from class: com.openfeint.internal.ui.IntroFlow.IntroFlowActionHandler.1
                @Override // com.openfeint.internal.request.IRawRequestDelegate
                public void onResponse(int status, String response) {
                    String js = String.format("%s('%d', %s)", options.get("callback"), Integer.valueOf(status), response.trim());
                    IntroFlowActionHandler.this.mWebNav.executeJavascript(js);
                }
            });
        }

        public final void loginUser(final Map<String, String> options) {
            OpenFeintInternal.getInstance().loginUser(options.get("email"), options.get("password"), options.get("user_id"), new IRawRequestDelegate() { // from class: com.openfeint.internal.ui.IntroFlow.IntroFlowActionHandler.2
                @Override // com.openfeint.internal.request.IRawRequestDelegate
                public void onResponse(int status, String response) {
                    String js = String.format("%s('%d', %s)", options.get("callback"), Integer.valueOf(status), response.trim());
                    IntroFlowActionHandler.this.mWebNav.executeJavascript(js);
                }
            });
        }

        public final void cacheImage(Map<String, String> options) {
            ImagePicker.show(IntroFlow.this);
        }

        public final void uploadImage(Map<String, String> options) {
            if (IntroFlow.this.cachedImage != null) {
                String apiUrl = "/xp/users/" + OpenFeintInternal.getInstance().getCurrentUser().resourceID() + "/profile_picture";
                ImagePicker.compressAndUpload(IntroFlow.this.cachedImage, apiUrl, null);
            }
        }

        public final void clearImage(Map<String, String> options) {
            IntroFlow.this.cachedImage = null;
        }

        public void decline(Map<String, String> options) {
            OpenFeint.userDeclinedFeint();
            IntroFlow.this.finish();
        }

        public void getEmail(Map<String, String> options) {
            String account = Util5.getAccountNameEclair(IntroFlow.this);
            if (account != null) {
                IntroFlow.this.executeJavascript(String.format("%s('%s');", options.get("callback"), account));
            }
        }
    }
}
