package com.openfeint.api.resource;

import com.openfeint.internal.APICallback;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.request.JSONRequest;
import com.openfeint.internal.request.OrderedArgList;
import com.openfeint.internal.resource.ArrayResourceProperty;
import com.openfeint.internal.resource.BooleanResourceProperty;
import com.openfeint.internal.resource.NestedResourceProperty;
import com.openfeint.internal.resource.Resource;
import com.openfeint.internal.resource.ResourceClass;
import com.openfeint.internal.resource.ServerException;
import com.openfeint.internal.resource.StringResourceProperty;
import java.util.List;

/* loaded from: classes.dex */
public class Leaderboard extends Resource {
    public boolean allowsWorseScores;
    public boolean descendingSortOrder = true;
    public List<Score> highScores;
    public Score localUserScore;
    public String name;

    public static abstract class GetScoresCB extends APICallback {
        public abstract void onSuccess(List<Score> list);
    }

    public static abstract class GetUserScoreCB extends APICallback {
        public abstract void onSuccess(Score score);
    }

    public static abstract class ListCB extends APICallback {
        public abstract void onSuccess(List<Leaderboard> list);
    }

    public Leaderboard(String resourceID) {
        setResourceID(resourceID);
    }

    public void getScores(GetScoresCB cb) {
        getScores(false, cb);
    }

    public void getFriendScores(GetScoresCB cb) {
        getScores(true, cb);
    }

    private void getScores(final boolean friends, final GetScoresCB cb) {
        final String path = "/xp/games/" + OpenFeintInternal.getInstance().getAppID() + "/leaderboards/" + resourceID() + "/high_scores";
        OrderedArgList args = new OrderedArgList();
        if (friends) {
            args.put("friends_leaderboard", "true");
        }
        JSONRequest req = new JSONRequest(args) { // from class: com.openfeint.api.resource.Leaderboard.1
            @Override // com.openfeint.internal.request.BaseRequest
            public boolean wantsLogin() {
                return friends;
            }

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
                    List<Score> scores = (List) responseBody;
                    cb.onSuccess(scores);
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

    public void getUserScore(User forUser, final GetUserScoreCB cb) {
        final String path = "/xp/users/" + forUser.resourceID() + "/games/" + OpenFeintInternal.getInstance().getAppID() + "/leaderboards/" + resourceID() + "/current_score";
        OrderedArgList args = new OrderedArgList();
        JSONRequest req = new JSONRequest(args) { // from class: com.openfeint.api.resource.Leaderboard.2
            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "GET";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return path;
            }

            @Override // com.openfeint.internal.request.JSONRequest
            public void onResponse(int responseCode, Object responseBody) {
                if (cb != null) {
                    if (200 <= responseCode && responseCode < 300) {
                        cb.onSuccess((Score) responseBody);
                        return;
                    }
                    if (404 == responseCode) {
                        cb.onSuccess(null);
                    } else if (responseBody instanceof ServerException) {
                        onFailure(((ServerException) responseBody).message);
                    } else {
                        onFailure(OpenFeintInternal.getRString(C0207RR.string("of_unknown_server_error")));
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

    public static void list(final ListCB cb) {
        final String path = "/xp/games/" + OpenFeintInternal.getInstance().getAppID() + "/leaderboards";
        JSONRequest req = new JSONRequest() { // from class: com.openfeint.api.resource.Leaderboard.3
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
                        List<Leaderboard> leaderboards = (List) responseBody;
                        cb.onSuccess(leaderboards);
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

    public Leaderboard() {
    }

    public static ResourceClass getResourceClass() {
        ResourceClass klass = new ResourceClass(Leaderboard.class, "leaderboard") { // from class: com.openfeint.api.resource.Leaderboard.4
            @Override // com.openfeint.internal.resource.ResourceClass
            public Resource factory() {
                return new Leaderboard();
            }
        };
        klass.mProperties.put("name", new StringResourceProperty() { // from class: com.openfeint.api.resource.Leaderboard.5
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((Leaderboard) obj).name;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((Leaderboard) obj).name = val;
            }
        });
        klass.mProperties.put("current_user_high_score", new NestedResourceProperty(Score.class) { // from class: com.openfeint.api.resource.Leaderboard.6
            @Override // com.openfeint.internal.resource.NestedResourceProperty
            public Resource get(Resource obj) {
                return ((Leaderboard) obj).localUserScore;
            }

            @Override // com.openfeint.internal.resource.NestedResourceProperty
            public void set(Resource obj, Resource val) {
                ((Leaderboard) obj).localUserScore = (Score) val;
            }
        });
        klass.mProperties.put("descending_sort_order", new BooleanResourceProperty() { // from class: com.openfeint.api.resource.Leaderboard.7
            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public boolean get(Resource obj) {
                return ((Leaderboard) obj).descendingSortOrder;
            }

            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public void set(Resource obj, boolean val) {
                ((Leaderboard) obj).descendingSortOrder = val;
            }
        });
        klass.mProperties.put("allow_posting_lower_scores", new BooleanResourceProperty() { // from class: com.openfeint.api.resource.Leaderboard.8
            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public boolean get(Resource obj) {
                return ((Leaderboard) obj).allowsWorseScores;
            }

            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public void set(Resource obj, boolean val) {
                ((Leaderboard) obj).allowsWorseScores = val;
            }
        });
        klass.mProperties.put("high_scores", new ArrayResourceProperty(Score.class) { // from class: com.openfeint.api.resource.Leaderboard.9
            @Override // com.openfeint.internal.resource.ArrayResourceProperty
            public List<? extends Resource> get(Resource obj) {
                return ((Leaderboard) obj).highScores;
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.openfeint.internal.resource.ArrayResourceProperty
            public void set(Resource obj, List<?> list) {
                ((Leaderboard) obj).highScores = list;
            }
        });
        return klass;
    }
}
