package com.openfeint.api.resource;

import android.graphics.Bitmap;
import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.internal.APICallback;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.notifications.AchievementNotification;
import com.openfeint.internal.offline.OfflineSupport;
import com.openfeint.internal.request.BitmapRequest;
import com.openfeint.internal.request.JSONRequest;
import com.openfeint.internal.request.OrderedArgList;
import com.openfeint.internal.resource.BooleanResourceProperty;
import com.openfeint.internal.resource.DateResourceProperty;
import com.openfeint.internal.resource.FloatResourceProperty;
import com.openfeint.internal.resource.IntResourceProperty;
import com.openfeint.internal.resource.Resource;
import com.openfeint.internal.resource.ResourceClass;
import com.openfeint.internal.resource.StringResourceProperty;
import java.util.Date;
import java.util.List;

/* loaded from: classes.dex */
public class Achievement extends Resource {
    public String description;
    public String endVersion;
    public int gamerscore;
    public String iconUrl;
    public boolean isSecret;
    public boolean isUnlocked;
    public float percentComplete;
    public int position;
    public String startVersion;
    public String title;
    public Date unlockDate;

    public static abstract class DownloadIconCB extends APICallback {
        public abstract void onSuccess(Bitmap bitmap);
    }

    public static abstract class ListCB extends APICallback {
        public abstract void onSuccess(List<Achievement> list);
    }

    public static abstract class LoadCB extends APICallback {
        public abstract void onSuccess();
    }

    public static abstract class UnlockCB extends APICallback {
        public abstract void onSuccess(boolean z);
    }

    public static abstract class UpdateProgressionCB extends APICallback {
        public abstract void onSuccess(boolean z);
    }

    public Achievement(String resourceID) {
        setResourceID(resourceID);
    }

    public static void list(final ListCB cb) {
        final String path = "/xp/games/" + OpenFeintInternal.getInstance().getAppID() + "/achievements";
        JSONRequest req = new JSONRequest() { // from class: com.openfeint.api.resource.Achievement.1
            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "GET";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return path;
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onSuccess(Object responseBody) {
                if (cb != null) {
                    try {
                        List<Achievement> achievements = (List) responseBody;
                        cb.onSuccess(achievements);
                    } catch (Exception e) {
                        onFailure(OpenFeintInternal.getRString(C0207RR.string("of_unexpected_response_format")));
                    }
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

    public void downloadIcon(final DownloadIconCB cb) {
        if (this.iconUrl == null) {
            if (cb != null) {
                cb.onFailure(OpenFeintInternal.getRString(C0207RR.string("of_null_icon_url")));
            }
        } else {
            BitmapRequest req = new BitmapRequest() { // from class: com.openfeint.api.resource.Achievement.2
                @Override // com.openfeint.internal.request.DownloadRequest, com.openfeint.internal.request.BaseRequest
                public String method() {
                    return "GET";
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String url() {
                    return Achievement.this.iconUrl;
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String path() {
                    return MutantMessages.sEmpty;
                }

                @Override // com.openfeint.internal.request.BitmapRequest
                public void onSuccess(Bitmap responseBody) {
                    if (cb != null) {
                        cb.onSuccess(responseBody);
                    }
                }

                @Override // com.openfeint.internal.request.DownloadRequest
                public void onFailure(String exceptionMessage) {
                    if (cb != null) {
                        cb.onFailure(exceptionMessage);
                    }
                }
            };
            req.launch();
        }
    }

    public void unlock(final UnlockCB cb) {
        UpdateProgressionCB upCB = null;
        if (cb != null) {
            upCB = new UpdateProgressionCB() { // from class: com.openfeint.api.resource.Achievement.3
                @Override // com.openfeint.api.resource.Achievement.UpdateProgressionCB
                public void onSuccess(boolean complete) {
                    cb.onSuccess(complete);
                }

                @Override // com.openfeint.internal.APICallback
                public void onFailure(String exceptionMessage) {
                    cb.onFailure(exceptionMessage);
                }
            };
        }
        updateProgression(100.0f, upCB);
    }

    public void updateProgression(float pctComplete, final UpdateProgressionCB cb) {
        final float clampedPctComplete = pctComplete >= 0.0f ? pctComplete > 100.0f ? 100.0f : pctComplete : 0.0f;
        final String resID = resourceID();
        if (resID == null) {
            if (cb != null) {
                cb.onFailure(OpenFeintInternal.getRString(C0207RR.string("of_achievement_unlock_null")));
                return;
            }
            return;
        }
        if (clampedPctComplete <= OfflineSupport.getClientCompletionPercentage(resID)) {
            if (cb != null) {
                cb.onSuccess(false);
                return;
            }
            return;
        }
        OfflineSupport.updateClientCompletionPercentage(resID, clampedPctComplete);
        if (OpenFeintInternal.getInstance().getUserID() == null) {
            this.percentComplete = OfflineSupport.getClientCompletionPercentage(resID);
            this.isUnlocked = this.percentComplete == 100.0f;
            if (cb != null) {
                cb.onSuccess(false);
                return;
            }
            return;
        }
        final String path = "/xp/games/" + OpenFeintInternal.getInstance().getAppID() + "/achievements/" + resID + "/unlock";
        OrderedArgList args = new OrderedArgList();
        args.put("percent_complete", new Float(clampedPctComplete).toString());
        JSONRequest req = new JSONRequest(args) { // from class: com.openfeint.api.resource.Achievement.4
            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "PUT";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return path;
            }

            @Override // com.openfeint.internal.request.JSONRequest
            protected void onResponse(int responseCode, Object responseBody) {
                if (responseCode >= 200 && responseCode < 300) {
                    Achievement achievement = (Achievement) responseBody;
                    float oldPercentComplete = Achievement.this.percentComplete;
                    Achievement.this.shallowCopy(achievement);
                    float newPercentComplete = Achievement.this.percentComplete;
                    OfflineSupport.updateServerCompletionPercentage(resID, newPercentComplete);
                    if (201 == responseCode || newPercentComplete > oldPercentComplete) {
                        AchievementNotification.showStatus(achievement);
                    }
                    if (cb != null) {
                        cb.onSuccess(201 == responseCode);
                        return;
                    }
                    return;
                }
                if (400 <= responseCode && responseCode < 500) {
                    onFailure(responseBody);
                    return;
                }
                if (100.0f == clampedPctComplete) {
                    Achievement.this.percentComplete = clampedPctComplete;
                    Achievement.this.isUnlocked = true;
                    AchievementNotification.showStatus(Achievement.this);
                }
                if (cb != null) {
                    cb.onSuccess(false);
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

    public void load(final LoadCB cb) {
        String resID = resourceID();
        if (resID == null) {
            if (cb != null) {
                cb.onFailure(OpenFeintInternal.getRString(C0207RR.string("of_achievement_load_null")));
            }
        } else {
            final String path = "/xp/games/" + OpenFeintInternal.getInstance().getAppID() + "/achievements/" + resID;
            JSONRequest req = new JSONRequest() { // from class: com.openfeint.api.resource.Achievement.5
                @Override // com.openfeint.internal.request.BaseRequest
                public String method() {
                    return "GET";
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String path() {
                    return path;
                }

                @Override // com.openfeint.internal.request.JSONRequest
                public void onSuccess(Object responseBody) {
                    Achievement.this.shallowCopy((Achievement) responseBody);
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
    }

    public Achievement() {
    }

    public static ResourceClass getResourceClass() {
        ResourceClass klass = new ResourceClass(Achievement.class, "achievement") { // from class: com.openfeint.api.resource.Achievement.6
            @Override // com.openfeint.internal.resource.ResourceClass
            public Resource factory() {
                return new Achievement();
            }
        };
        klass.mProperties.put("title", new StringResourceProperty() { // from class: com.openfeint.api.resource.Achievement.7
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((Achievement) obj).title;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((Achievement) obj).title = val;
            }
        });
        klass.mProperties.put("description", new StringResourceProperty() { // from class: com.openfeint.api.resource.Achievement.8
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((Achievement) obj).description;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((Achievement) obj).description = val;
            }
        });
        klass.mProperties.put("gamerscore", new IntResourceProperty() { // from class: com.openfeint.api.resource.Achievement.9
            @Override // com.openfeint.internal.resource.IntResourceProperty
            public int get(Resource obj) {
                return ((Achievement) obj).gamerscore;
            }

            @Override // com.openfeint.internal.resource.IntResourceProperty
            public void set(Resource obj, int val) {
                ((Achievement) obj).gamerscore = val;
            }
        });
        klass.mProperties.put("icon_url", new StringResourceProperty() { // from class: com.openfeint.api.resource.Achievement.10
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((Achievement) obj).iconUrl;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((Achievement) obj).iconUrl = val;
            }
        });
        klass.mProperties.put("is_secret", new BooleanResourceProperty() { // from class: com.openfeint.api.resource.Achievement.11
            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public boolean get(Resource obj) {
                return ((Achievement) obj).isSecret;
            }

            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public void set(Resource obj, boolean val) {
                ((Achievement) obj).isSecret = val;
            }
        });
        klass.mProperties.put("is_unlocked", new BooleanResourceProperty() { // from class: com.openfeint.api.resource.Achievement.12
            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public boolean get(Resource obj) {
                return ((Achievement) obj).isUnlocked;
            }

            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public void set(Resource obj, boolean val) {
                ((Achievement) obj).isUnlocked = val;
            }
        });
        klass.mProperties.put("percent_complete", new FloatResourceProperty() { // from class: com.openfeint.api.resource.Achievement.13
            @Override // com.openfeint.internal.resource.FloatResourceProperty
            public float get(Resource obj) {
                return ((Achievement) obj).percentComplete;
            }

            @Override // com.openfeint.internal.resource.FloatResourceProperty
            public void set(Resource obj, float val) {
                ((Achievement) obj).percentComplete = val;
            }
        });
        klass.mProperties.put("unlocked_at", new DateResourceProperty() { // from class: com.openfeint.api.resource.Achievement.14
            @Override // com.openfeint.internal.resource.DateResourceProperty
            public Date get(Resource obj) {
                return ((Achievement) obj).unlockDate;
            }

            @Override // com.openfeint.internal.resource.DateResourceProperty
            public void set(Resource obj, Date val) {
                ((Achievement) obj).unlockDate = val;
            }
        });
        klass.mProperties.put("position", new IntResourceProperty() { // from class: com.openfeint.api.resource.Achievement.15
            @Override // com.openfeint.internal.resource.IntResourceProperty
            public int get(Resource obj) {
                return ((Achievement) obj).position;
            }

            @Override // com.openfeint.internal.resource.IntResourceProperty
            public void set(Resource obj, int val) {
                ((Achievement) obj).position = val;
            }
        });
        klass.mProperties.put("end_version", new StringResourceProperty() { // from class: com.openfeint.api.resource.Achievement.16
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((Achievement) obj).endVersion;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((Achievement) obj).endVersion = val;
            }
        });
        klass.mProperties.put("start_version", new StringResourceProperty() { // from class: com.openfeint.api.resource.Achievement.17
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((Achievement) obj).startVersion;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((Achievement) obj).startVersion = val;
            }
        });
        return klass;
    }
}
