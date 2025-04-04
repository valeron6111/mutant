package com.openfeint.api.resource;

import android.graphics.Bitmap;
import android.net.Uri;
import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.internal.APICallback;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.request.BitmapRequest;
import com.openfeint.internal.request.JSONRequest;
import com.openfeint.internal.request.OrderedArgList;
import com.openfeint.internal.resource.BooleanResourceProperty;
import com.openfeint.internal.resource.DoubleResourceProperty;
import com.openfeint.internal.resource.IntResourceProperty;
import com.openfeint.internal.resource.Resource;
import com.openfeint.internal.resource.ResourceClass;
import com.openfeint.internal.resource.StringResourceProperty;
import java.util.List;

/* loaded from: classes.dex */
public class User extends Resource {
    public boolean followedByLocalUser;
    public boolean followsLocalUser;
    public int gamerScore;
    public String lastPlayedGameId;
    public String lastPlayedGameName;
    public double latitude;
    public double longitude;
    public String name;
    public boolean online;
    public String profilePictureSource;
    public String profilePictureUrl;
    public boolean usesFacebookProfilePicture;

    public static abstract class DownloadProfilePictureCB extends APICallback {
        public abstract void onSuccess(Bitmap bitmap);
    }

    public static abstract class FindCB extends APICallback {
        public abstract void onSuccess(User user);
    }

    public static abstract class GetFriendsCB extends APICallback {
        public abstract void onSuccess(List<User> list);
    }

    public static abstract class LoadCB extends APICallback {
        public abstract void onSuccess();
    }

    public User(String resourceID) {
        setResourceID(resourceID);
    }

    public String userID() {
        return resourceID();
    }

    public void load(final LoadCB cb) {
        JSONRequest req = new JSONRequest() { // from class: com.openfeint.api.resource.User.1
            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "GET";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return "/xp/users/" + User.this.resourceID();
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onSuccess(Object responseBody) {
                User.this.shallowCopyAncestorType((User) responseBody);
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

    public static void findByName(final String name, final FindCB cb) {
        JSONRequest req = new JSONRequest() { // from class: com.openfeint.api.resource.User.2
            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "GET";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return "/xp/users/" + Uri.encode(name);
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onSuccess(Object responseBody) {
                if (cb != null) {
                    cb.onSuccess((User) responseBody);
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

    public static void findByID(final String resourceID, final FindCB cb) {
        JSONRequest req = new JSONRequest() { // from class: com.openfeint.api.resource.User.3
            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "GET";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return "/xp/users/" + resourceID;
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onSuccess(Object responseBody) {
                if (cb != null) {
                    cb.onSuccess((User) responseBody);
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

    public void getFriends(final GetFriendsCB cb) {
        OrderedArgList args = new OrderedArgList();
        args.put("user_id", resourceID());
        JSONRequest req = new JSONRequest(args) { // from class: com.openfeint.api.resource.User.4
            @Override // com.openfeint.internal.request.BaseRequest
            public boolean wantsLogin() {
                return true;
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "GET";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return "/xp/friends";
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onSuccess(Object responseBody) {
                if (cb != null) {
                    try {
                        List<User> friends = (List) responseBody;
                        cb.onSuccess(friends);
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

    public void downloadProfilePicture(final DownloadProfilePictureCB cb) {
        if (OpenFeintInternal.getInstance().parentalControlsEnabled()) {
            if (cb != null) {
                cb.onFailure(OpenFeintInternal.getRString(C0207RR.string("of_operation_not_permitted_due_to_parental_controls")));
            }
        } else if (this.profilePictureUrl == null) {
            if (cb != null) {
                cb.onFailure(OpenFeintInternal.getRString(C0207RR.string("of_profile_url_null")));
            }
        } else {
            BitmapRequest req = new BitmapRequest() { // from class: com.openfeint.api.resource.User.5
                @Override // com.openfeint.internal.request.BaseRequest
                public String url() {
                    return User.this.profilePictureUrl;
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

    public User() {
    }

    public static ResourceClass getResourceClass() {
        ResourceClass klass = new ResourceClass(User.class, "user") { // from class: com.openfeint.api.resource.User.6
            @Override // com.openfeint.internal.resource.ResourceClass
            public Resource factory() {
                return new User();
            }
        };
        klass.mProperties.put("name", new StringResourceProperty() { // from class: com.openfeint.api.resource.User.7
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((User) obj).name;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((User) obj).name = val;
            }
        });
        klass.mProperties.put("profile_picture_url", new StringResourceProperty() { // from class: com.openfeint.api.resource.User.8
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((User) obj).profilePictureUrl;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((User) obj).profilePictureUrl = val;
            }
        });
        klass.mProperties.put("profile_picture_source", new StringResourceProperty() { // from class: com.openfeint.api.resource.User.9
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((User) obj).profilePictureSource;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((User) obj).profilePictureSource = val;
            }
        });
        klass.mProperties.put("uses_facebook_profile_picture", new BooleanResourceProperty() { // from class: com.openfeint.api.resource.User.10
            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public boolean get(Resource obj) {
                return ((User) obj).usesFacebookProfilePicture;
            }

            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public void set(Resource obj, boolean val) {
                ((User) obj).usesFacebookProfilePicture = val;
            }
        });
        klass.mProperties.put("last_played_game_id", new StringResourceProperty() { // from class: com.openfeint.api.resource.User.11
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((User) obj).lastPlayedGameId;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((User) obj).lastPlayedGameId = val;
            }
        });
        klass.mProperties.put("last_played_game_name", new StringResourceProperty() { // from class: com.openfeint.api.resource.User.12
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((User) obj).lastPlayedGameName;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((User) obj).lastPlayedGameName = val;
            }
        });
        klass.mProperties.put("gamer_score", new IntResourceProperty() { // from class: com.openfeint.api.resource.User.13
            @Override // com.openfeint.internal.resource.IntResourceProperty
            public int get(Resource obj) {
                return ((User) obj).gamerScore;
            }

            @Override // com.openfeint.internal.resource.IntResourceProperty
            public void set(Resource obj, int val) {
                ((User) obj).gamerScore = val;
            }
        });
        klass.mProperties.put("following_local_user", new BooleanResourceProperty() { // from class: com.openfeint.api.resource.User.14
            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public boolean get(Resource obj) {
                return ((User) obj).followsLocalUser;
            }

            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public void set(Resource obj, boolean val) {
                ((User) obj).followsLocalUser = val;
            }
        });
        klass.mProperties.put("followed_by_local_user", new BooleanResourceProperty() { // from class: com.openfeint.api.resource.User.15
            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public boolean get(Resource obj) {
                return ((User) obj).followedByLocalUser;
            }

            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public void set(Resource obj, boolean val) {
                ((User) obj).followedByLocalUser = val;
            }
        });
        klass.mProperties.put("online", new BooleanResourceProperty() { // from class: com.openfeint.api.resource.User.16
            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public boolean get(Resource obj) {
                return ((User) obj).online;
            }

            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public void set(Resource obj, boolean val) {
                ((User) obj).online = val;
            }
        });
        klass.mProperties.put("lat", new DoubleResourceProperty() { // from class: com.openfeint.api.resource.User.17
            @Override // com.openfeint.internal.resource.DoubleResourceProperty
            public double get(Resource obj) {
                return ((User) obj).latitude;
            }

            @Override // com.openfeint.internal.resource.DoubleResourceProperty
            public void set(Resource obj, double val) {
                ((User) obj).latitude = val;
            }
        });
        klass.mProperties.put("lng", new DoubleResourceProperty() { // from class: com.openfeint.api.resource.User.18
            @Override // com.openfeint.internal.resource.DoubleResourceProperty
            public double get(Resource obj) {
                return ((User) obj).longitude;
            }

            @Override // com.openfeint.internal.resource.DoubleResourceProperty
            public void set(Resource obj, double val) {
                ((User) obj).longitude = val;
            }
        });
        return klass;
    }
}
