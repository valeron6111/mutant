package com.openfeint.api.resource;

import android.content.res.Resources;
import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.api.Notification;
import com.openfeint.internal.APICallback;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.notifications.SimpleNotification;
import com.openfeint.internal.offline.OfflineSupport;
import com.openfeint.internal.request.BlobPostRequest;
import com.openfeint.internal.request.CompressedBlobDownloadRequest;
import com.openfeint.internal.request.CompressedBlobPostRequest;
import com.openfeint.internal.request.DownloadRequest;
import com.openfeint.internal.request.IRawRequestDelegate;
import com.openfeint.internal.request.JSONRequest;
import com.openfeint.internal.request.OrderedArgList;
import com.openfeint.internal.resource.BlobUploadParameters;
import com.openfeint.internal.resource.DoubleResourceProperty;
import com.openfeint.internal.resource.IntResourceProperty;
import com.openfeint.internal.resource.LongResourceProperty;
import com.openfeint.internal.resource.NestedResourceProperty;
import com.openfeint.internal.resource.Resource;
import com.openfeint.internal.resource.ResourceClass;
import com.openfeint.internal.resource.ScoreBlobDelegate;
import com.openfeint.internal.resource.StringResourceProperty;
import java.util.List;

/* loaded from: classes.dex */
public class Score extends Resource {
    public byte[] blob;
    private BlobUploadParameters blobUploadParameters;
    private String blobUrl;
    public String customData;
    public String displayText;
    public double latitude;
    public int leaderboardId;
    public double longitude;
    public int rank;
    public long score;
    public User user;

    public static abstract class DownloadBlobCB extends APICallback {
        public abstract void onSuccess();
    }

    public Score(long score) {
        this.score = score;
    }

    public Score(long score, String displayText) {
        this.score = score;
        this.displayText = displayText;
    }

    public boolean hasBlob() {
        return this.blobUrl != null;
    }

    public static abstract class SubmitToCB extends APICallback {
        public abstract void onSuccess(boolean z);

        public void onBlobUploadSuccess() {
        }

        public void onBlobUploadFailure(String exceptionMessage) {
        }
    }

    public void submitTo(Leaderboard leaderboard, SubmitToCB cb) {
        submitToInternal(leaderboard, null, cb, false);
    }

    public void submitToFromOffline(Leaderboard leaderboard, String timestamp, SubmitToCB cb) {
        submitToInternal(leaderboard, timestamp, cb, true);
    }

    private void submitToInternal(final Leaderboard leaderboard, String timestamp, final SubmitToCB cb, final boolean fromOffline) {
        if (leaderboard == null || leaderboard.resourceID() == null || leaderboard.resourceID().length() == 0) {
            if (cb != null) {
                cb.onFailure("No leaderboard ID provided.  Please provide a leaderboard ID from the Dev Dashboard.");
                return;
            }
            return;
        }
        if (!OpenFeintInternal.getInstance().isUserLoggedIn()) {
            OfflineSupport.postOfflineScore(this, leaderboard);
            if (cb != null) {
                cb.onSuccess(false);
                return;
            }
            return;
        }
        final String path = "/xp/games/" + OpenFeintInternal.getInstance().getAppID() + "/leaderboards/" + leaderboard.resourceID() + "/high_scores";
        OrderedArgList args = new OrderedArgList();
        args.put("high_score[score]", new Long(this.score).toString());
        if (this.displayText != null) {
            args.put("high_score[display_text]", this.displayText);
        }
        final boolean uploadBlob = this.blob != null;
        args.put("high_score[has_blob]", uploadBlob ? "1" : "0");
        if (timestamp != null) {
            args.put("high_score[timestamp]", timestamp);
        }
        JSONRequest req = new JSONRequest(args) { // from class: com.openfeint.api.resource.Score.1
            @Override // com.openfeint.internal.request.BaseRequest
            public String method() {
                return "POST";
            }

            @Override // com.openfeint.internal.request.BaseRequest
            public String path() {
                return path;
            }

            @Override // com.openfeint.internal.request.JSONRequest
            protected void onResponse(int responseCode, Object responseBody) {
                if (201 == responseCode) {
                    if (!fromOffline) {
                        Resources r = OpenFeintInternal.getInstance().getContext().getResources();
                        SimpleNotification.show(r.getString(C0207RR.string("of_score_submitted_notification")), "@drawable/of_icon_highscore_notification", Notification.Category.HighScore, Notification.Type.Success);
                    }
                    if (cb != null) {
                        cb.onSuccess(true);
                    }
                    perhapsUploadBlob(uploadBlob, responseBody);
                    return;
                }
                if (200 <= responseCode && responseCode < 300) {
                    if (cb != null) {
                        cb.onSuccess(false);
                    }
                } else {
                    if ((responseCode == 0 || 500 <= responseCode) && !fromOffline) {
                        OfflineSupport.postOfflineScore(Score.this, leaderboard);
                        if (cb != null) {
                            cb.onSuccess(false);
                            return;
                        }
                        return;
                    }
                    onFailure(responseBody);
                }
            }

            private final void perhapsUploadBlob(boolean uploadBlob2, Object responseBody) {
                if (uploadBlob2 && (responseBody instanceof List)) {
                    List<Score> scores = (List) responseBody;
                    Score s = scores.get(0);
                    BlobPostRequest postRequest = new CompressedBlobPostRequest(s.blobUploadParameters, String.format("blob.%s.bin", s.resourceID()), Score.this.blob);
                    if (cb != null) {
                        postRequest.setDelegate(new IRawRequestDelegate() { // from class: com.openfeint.api.resource.Score.1.1
                            @Override // com.openfeint.internal.request.IRawRequestDelegate
                            public void onResponse(int responseCode, String responseBody2) {
                                if (200 <= responseCode && responseCode < 300) {
                                    cb.onBlobUploadSuccess();
                                } else {
                                    cb.onBlobUploadFailure(responseBody2);
                                }
                            }
                        });
                    }
                    postRequest.launch();
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

    public void downloadBlob(final DownloadBlobCB cb) {
        if (hasBlob()) {
            DownloadRequest req = new CompressedBlobDownloadRequest() { // from class: com.openfeint.api.resource.Score.2
                @Override // com.openfeint.internal.request.BaseRequest
                public boolean signed() {
                    return false;
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String url() {
                    return Score.this.blobUrl;
                }

                @Override // com.openfeint.internal.request.BaseRequest
                public String path() {
                    return MutantMessages.sEmpty;
                }

                @Override // com.openfeint.internal.request.CompressedBlobDownloadRequest
                protected void onSuccessDecompress(byte[] bodyData) {
                    Score.this.blob = bodyData;
                    if (cb != null) {
                        cb.onSuccess();
                    }
                }

                @Override // com.openfeint.internal.request.DownloadRequest
                public void onFailure(String exceptionMessage) {
                    super.onFailure(exceptionMessage);
                    if (cb != null) {
                        cb.onFailure(exceptionMessage);
                    }
                }
            };
            req.launch();
        } else if (cb != null) {
            cb.onFailure(OpenFeintInternal.getRString(C0207RR.string("of_no_blob")));
        }
    }

    public static abstract class BlobDownloadedDelegate {
        public void blobDownloadedForScore(Score score) {
        }
    }

    public static void setBlobDownloadedDelegate(BlobDownloadedDelegate delegate) {
        ScoreBlobDelegate.sBlobDownloadedDelegate = delegate;
    }

    public Score() {
    }

    public static ResourceClass getResourceClass() {
        ResourceClass klass = new ResourceClass(Score.class, "high_score") { // from class: com.openfeint.api.resource.Score.3
            @Override // com.openfeint.internal.resource.ResourceClass
            public Resource factory() {
                return new Score();
            }
        };
        klass.mProperties.put("score", new LongResourceProperty() { // from class: com.openfeint.api.resource.Score.4
            @Override // com.openfeint.internal.resource.LongResourceProperty
            public long get(Resource obj) {
                return ((Score) obj).score;
            }

            @Override // com.openfeint.internal.resource.LongResourceProperty
            public void set(Resource obj, long val) {
                ((Score) obj).score = val;
            }
        });
        klass.mProperties.put("rank", new IntResourceProperty() { // from class: com.openfeint.api.resource.Score.5
            @Override // com.openfeint.internal.resource.IntResourceProperty
            public int get(Resource obj) {
                return ((Score) obj).rank;
            }

            @Override // com.openfeint.internal.resource.IntResourceProperty
            public void set(Resource obj, int val) {
                ((Score) obj).rank = val;
            }
        });
        klass.mProperties.put("leaderboard_id", new IntResourceProperty() { // from class: com.openfeint.api.resource.Score.6
            @Override // com.openfeint.internal.resource.IntResourceProperty
            public int get(Resource obj) {
                return ((Score) obj).leaderboardId;
            }

            @Override // com.openfeint.internal.resource.IntResourceProperty
            public void set(Resource obj, int val) {
                ((Score) obj).leaderboardId = val;
            }
        });
        klass.mProperties.put("display_text", new StringResourceProperty() { // from class: com.openfeint.api.resource.Score.7
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((Score) obj).displayText;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((Score) obj).displayText = val;
            }
        });
        klass.mProperties.put("custom_data", new StringResourceProperty() { // from class: com.openfeint.api.resource.Score.8
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((Score) obj).customData;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((Score) obj).customData = val;
            }
        });
        klass.mProperties.put("lat", new DoubleResourceProperty() { // from class: com.openfeint.api.resource.Score.9
            @Override // com.openfeint.internal.resource.DoubleResourceProperty
            public double get(Resource obj) {
                return ((Score) obj).latitude;
            }

            @Override // com.openfeint.internal.resource.DoubleResourceProperty
            public void set(Resource obj, double val) {
                ((Score) obj).latitude = val;
            }
        });
        klass.mProperties.put("lng", new DoubleResourceProperty() { // from class: com.openfeint.api.resource.Score.10
            @Override // com.openfeint.internal.resource.DoubleResourceProperty
            public double get(Resource obj) {
                return ((Score) obj).longitude;
            }

            @Override // com.openfeint.internal.resource.DoubleResourceProperty
            public void set(Resource obj, double val) {
                ((Score) obj).longitude = val;
            }
        });
        klass.mProperties.put("user", new NestedResourceProperty(User.class) { // from class: com.openfeint.api.resource.Score.11
            @Override // com.openfeint.internal.resource.NestedResourceProperty
            public Resource get(Resource obj) {
                return ((Score) obj).user;
            }

            @Override // com.openfeint.internal.resource.NestedResourceProperty
            public void set(Resource obj, Resource val) {
                ((Score) obj).user = (User) val;
            }
        });
        klass.mProperties.put("blob_url", new StringResourceProperty() { // from class: com.openfeint.api.resource.Score.12
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((Score) obj).blobUrl;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((Score) obj).blobUrl = val;
            }
        });
        klass.mProperties.put("blob_upload_parameters", new NestedResourceProperty(BlobUploadParameters.class) { // from class: com.openfeint.api.resource.Score.13
            @Override // com.openfeint.internal.resource.NestedResourceProperty
            public Resource get(Resource obj) {
                return ((Score) obj).blobUploadParameters;
            }

            @Override // com.openfeint.internal.resource.NestedResourceProperty
            public void set(Resource obj, Resource val) {
                ((Score) obj).blobUploadParameters = (BlobUploadParameters) val;
            }
        });
        return klass;
    }
}
