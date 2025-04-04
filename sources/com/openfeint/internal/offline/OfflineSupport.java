package com.openfeint.internal.offline;

import android.content.Context;
import com.alawar.mutant.database.DbBuilder;
import com.openfeint.api.Notification;
import com.openfeint.api.OpenFeint;
import com.openfeint.api.resource.Leaderboard;
import com.openfeint.api.resource.Score;
import com.openfeint.internal.C0207RR;
import com.openfeint.internal.Encryption;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.Util;
import com.openfeint.internal.logcat.OFLog;
import com.openfeint.internal.notifications.SimpleNotification;
import com.openfeint.internal.request.BaseRequest;
import com.openfeint.internal.request.OrderedArgList;
import com.openfeint.internal.resource.DateResourceProperty;
import com.openfeint.internal.vendor.org.apache.commons.codec.binary.Hex;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public class OfflineSupport {
    private static final String TAG = "OfflineSupport";
    private static final String TEMPORARY_USER_ID = "0";

    /* renamed from: db */
    private static C0215DB f287db = null;
    private static String userID = null;
    private static AtomicBoolean updateInProgress = new AtomicBoolean(false);

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean streq(String lhs, String rhs) {
        return lhs == null ? rhs == null : lhs.equals(rhs);
    }

    public static class OfflineAchievement {
        public float clientCompletionPercentage;
        public String resourceID;
        public float serverCompletionPercentage;
        public String timestamp;

        public OfflineAchievement dup() {
            OfflineAchievement rv = new OfflineAchievement();
            rv.resourceID = this.resourceID;
            rv.clientCompletionPercentage = this.clientCompletionPercentage;
            rv.serverCompletionPercentage = this.serverCompletionPercentage;
            rv.timestamp = this.timestamp;
            return rv;
        }

        /* renamed from: eq */
        public boolean m186eq(OfflineAchievement rhs) {
            return OfflineSupport.streq(this.resourceID, rhs.resourceID) && this.clientCompletionPercentage == rhs.clientCompletionPercentage && this.serverCompletionPercentage == rhs.serverCompletionPercentage && OfflineSupport.streq(this.timestamp, rhs.timestamp);
        }
    }

    public static class OfflineScore {
        public String blobFileName;
        public String customData;
        public String displayText;
        public String leaderboardID;
        public long score;
        public String timestamp;

        public OfflineScore dup() {
            OfflineScore rv = new OfflineScore();
            rv.leaderboardID = this.leaderboardID;
            rv.score = this.score;
            rv.displayText = this.displayText;
            rv.customData = this.customData;
            rv.blobFileName = this.blobFileName;
            rv.timestamp = this.timestamp;
            return rv;
        }

        /* renamed from: eq */
        public boolean m187eq(OfflineScore rhs) {
            return OfflineSupport.streq(this.leaderboardID, rhs.leaderboardID) && this.score == rhs.score && OfflineSupport.streq(this.displayText, rhs.displayText) && OfflineSupport.streq(this.customData, rhs.customData) && OfflineSupport.streq(this.blobFileName, rhs.blobFileName) && OfflineSupport.streq(this.timestamp, rhs.timestamp);
        }
    }

    /* renamed from: com.openfeint.internal.offline.OfflineSupport$DB */
    public static class C0215DB {
        private static final int STREAM_VERSION = 0;
        public ArrayList<OfflineAchievement> achievements = new ArrayList<>();
        public ArrayList<OfflineScore> scores = new ArrayList<>();

        public C0215DB dup() {
            C0215DB rv = new C0215DB();
            Iterator i$ = this.scores.iterator();
            while (i$.hasNext()) {
                OfflineScore s = i$.next();
                rv.scores.add(s.dup());
            }
            Iterator i$2 = this.achievements.iterator();
            while (i$2.hasNext()) {
                OfflineAchievement a = i$2.next();
                rv.achievements.add(a.dup());
            }
            return rv;
        }

        public void merge(C0215DB newUserDB) {
            Iterator i$ = newUserDB.achievements.iterator();
            while (i$.hasNext()) {
                OfflineAchievement newUserAchievement = i$.next();
                OfflineAchievement currentUserAchievement = findAchievement(newUserAchievement.resourceID);
                if (currentUserAchievement == null) {
                    this.achievements.add(newUserAchievement.dup());
                } else {
                    if (currentUserAchievement.clientCompletionPercentage < newUserAchievement.clientCompletionPercentage) {
                        currentUserAchievement.clientCompletionPercentage = newUserAchievement.clientCompletionPercentage;
                        currentUserAchievement.timestamp = newUserAchievement.timestamp;
                    }
                    currentUserAchievement.serverCompletionPercentage = Math.max(currentUserAchievement.serverCompletionPercentage, newUserAchievement.serverCompletionPercentage);
                }
            }
            this.scores.addAll(newUserDB.scores);
        }

        public void updateOnUpload(C0215DB otherDB) {
            Iterator i$ = otherDB.achievements.iterator();
            while (i$.hasNext()) {
                OfflineAchievement otherAchievement = i$.next();
                OfflineAchievement myAchievement = findAchievement(otherAchievement.resourceID);
                if (myAchievement == null) {
                    myAchievement = otherAchievement.dup();
                    this.achievements.add(myAchievement);
                }
                myAchievement.serverCompletionPercentage = Math.max(myAchievement.serverCompletionPercentage, otherAchievement.clientCompletionPercentage);
            }
            ArrayList<OfflineScore> oldScores = this.scores;
            this.scores = new ArrayList<>();
            Iterator i$2 = oldScores.iterator();
            while (i$2.hasNext()) {
                OfflineScore myScore = i$2.next();
                if (myScore.blobFileName != null) {
                    this.scores.add(myScore);
                } else {
                    boolean found = false;
                    Iterator i$3 = otherDB.scores.iterator();
                    while (true) {
                        if (!i$3.hasNext()) {
                            break;
                        }
                        OfflineScore otherScore = i$3.next();
                        if (myScore.m187eq(otherScore)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        this.scores.add(myScore);
                    }
                }
            }
        }

        public static C0215DB load(String fileName) {
            C0215DB rv = new C0215DB();
            ObjectInputStream ois = null;
            try {
                if (fileName != null) {
                    try {
                        InputStream is = Encryption.decryptionWrap(OpenFeintInternal.getInstance().getContext().openFileInput(fileName));
                        ObjectInputStream ois2 = new ObjectInputStream(is);
                        try {
                            int streamVersion = ois2.readInt();
                            switch (streamVersion) {
                                case DbBuilder.ID_COLUMN /* 0 */:
                                    for (int numAchievements = ois2.readInt(); numAchievements > 0; numAchievements--) {
                                        OfflineAchievement a = new OfflineAchievement();
                                        a.resourceID = (String) ois2.readObject();
                                        a.clientCompletionPercentage = ois2.readFloat();
                                        a.serverCompletionPercentage = ois2.readFloat();
                                        a.timestamp = (String) ois2.readObject();
                                        rv.achievements.add(a);
                                    }
                                    for (int numScores = ois2.readInt(); numScores > 0; numScores--) {
                                        OfflineScore s = new OfflineScore();
                                        s.leaderboardID = (String) ois2.readObject();
                                        s.score = ois2.readLong();
                                        s.displayText = (String) ois2.readObject();
                                        s.customData = (String) ois2.readObject();
                                        s.blobFileName = (String) ois2.readObject();
                                        s.timestamp = (String) ois2.readObject();
                                        rv.scores.add(s);
                                    }
                                    if (ois2 != null) {
                                        try {
                                            ois2.close();
                                        } catch (IOException e) {
                                            ois = ois2;
                                            break;
                                        }
                                    }
                                    ois = ois2;
                                    break;
                                default:
                                    throw new Exception(String.format("Unrecognized stream version %d", Integer.valueOf(streamVersion)));
                            }
                        } catch (Exception e2) {
                            e = e2;
                            ois = ois2;
                            OFLog.m182e(OfflineSupport.TAG, "Couldn't load offline achievements - " + e.getMessage());
                            rv.achievements.clear();
                            rv.scores.clear();
                            if (ois != null) {
                                try {
                                    ois.close();
                                } catch (IOException e3) {
                                }
                            }
                            return rv;
                        } catch (Throwable th) {
                            th = th;
                            ois = ois2;
                            if (ois != null) {
                                try {
                                    ois.close();
                                } catch (IOException e4) {
                                }
                            }
                            throw th;
                        }
                    } catch (Exception e5) {
                        e = e5;
                    }
                }
                return rv;
            } catch (Throwable th2) {
                th = th2;
            }
        }

        public void save(String fileName) {
            ObjectOutputStream oos = null;
            OutputStream os = null;
            try {
                os = Encryption.encryptionWrap(OpenFeintInternal.getInstance().getContext().openFileOutput(fileName, 0));
                ObjectOutputStream oos2 = new ObjectOutputStream(os);
                try {
                    oos2.writeInt(0);
                    oos2.writeInt(this.achievements.size());
                    Iterator i$ = this.achievements.iterator();
                    while (i$.hasNext()) {
                        OfflineAchievement a = i$.next();
                        oos2.writeObject(a.resourceID);
                        oos2.writeFloat(a.clientCompletionPercentage);
                        oos2.writeFloat(a.serverCompletionPercentage);
                        oos2.writeObject(a.timestamp);
                    }
                    oos2.writeInt(this.scores.size());
                    Iterator i$2 = this.scores.iterator();
                    while (i$2.hasNext()) {
                        OfflineScore s = i$2.next();
                        oos2.writeObject(s.leaderboardID);
                        oos2.writeLong(s.score);
                        oos2.writeObject(s.displayText);
                        oos2.writeObject(s.customData);
                        oos2.writeObject(s.blobFileName);
                        oos2.writeObject(s.timestamp);
                    }
                    oos2.close();
                    if (oos2 != null) {
                        try {
                            oos2.close();
                        } catch (IOException e) {
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e2) {
                        }
                    }
                } catch (Exception e3) {
                    oos = oos2;
                    if (oos != null) {
                        try {
                            oos.close();
                        } catch (IOException e4) {
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e5) {
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    oos = oos2;
                    if (oos != null) {
                        try {
                            oos.close();
                        } catch (IOException e6) {
                        }
                    }
                    if (os == null) {
                        throw th;
                    }
                    try {
                        os.close();
                        throw th;
                    } catch (IOException e7) {
                        throw th;
                    }
                }
            } catch (Exception e8) {
            } catch (Throwable th2) {
                th = th2;
            }
        }

        public void removeReferencedBlobs() {
            Iterator i$ = this.scores.iterator();
            while (i$.hasNext()) {
                OfflineScore os = i$.next();
                OfflineSupport.deleteDataFile(os.blobFileName);
            }
        }

        public void clear() {
            this.achievements.clear();
            this.scores.clear();
        }

        public OfflineAchievement findAchievement(String resourceID) {
            Iterator i$ = this.achievements.iterator();
            while (i$.hasNext()) {
                OfflineAchievement a = i$.next();
                if (a.resourceID.equals(resourceID)) {
                    return a;
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void deleteDataFile(String fileName) {
        try {
            Context context = OpenFeintInternal.getInstance().getContext();
            context.deleteFile(context.getFileStreamPath(fileName).getPath());
        } catch (Exception e) {
        }
    }

    private static String fullPath(String fileName) {
        Context context = OpenFeintInternal.getInstance().getContext();
        return context.getFileStreamPath(fileName).getPath();
    }

    private static String filename(String forUserID) {
        String appID;
        if (forUserID == null || (appID = OpenFeintInternal.getInstance().getAppID()) == null) {
            return null;
        }
        return "of.offline." + forUserID + "." + appID;
    }

    private static String now() {
        return DateResourceProperty.sDateParser.format(new Date());
    }

    public static void setUserDeclined() {
        setUserID(null);
    }

    public static void setUserTemporary() {
        setUserID(TEMPORARY_USER_ID);
    }

    private static boolean isUserTemporary() {
        return TEMPORARY_USER_ID.equals(userID);
    }

    private static void removeDBForUser(String forUserID) {
        deleteDataFile(filename(forUserID));
    }

    public static void setUserID(String newUserID) {
        if (newUserID == null || !newUserID.equals(userID)) {
            C0215DB newUserDB = C0215DB.load(filename(newUserID));
            if (isUserTemporary()) {
                f287db.merge(newUserDB);
                removeDBForUser(TEMPORARY_USER_ID);
            } else {
                f287db = newUserDB;
            }
            userID = newUserID;
            trySubmitOfflineData();
        }
    }

    public static void trySubmitOfflineData() {
        if (userID != null && !userID.equals(TEMPORARY_USER_ID) && OpenFeint.isUserLoggedIn()) {
            updateToServer();
        }
    }

    static final void removeAndUploadNext(OfflineScore os) {
        f287db.scores.remove(os);
        save();
        uploadScoresWithBlobs();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void uploadScoresWithBlobs() {
        Iterator i$ = f287db.scores.iterator();
        while (i$.hasNext()) {
            final OfflineScore os = i$.next();
            if (os.blobFileName != null) {
                Score s = new Score(os.score);
                s.customData = os.customData;
                s.displayText = os.displayText;
                try {
                    s.blob = Util.readWholeFile(fullPath(os.blobFileName));
                } catch (IOException e) {
                }
                if (s.blob == null) {
                    removeAndUploadNext(os);
                    return;
                } else {
                    s.submitToFromOffline(new Leaderboard(os.leaderboardID), os.timestamp, new Score.SubmitToCB() { // from class: com.openfeint.internal.offline.OfflineSupport.1
                        @Override // com.openfeint.api.resource.Score.SubmitToCB
                        public void onSuccess(boolean newHighScore) {
                            OfflineSupport.removeAndUploadNext(OfflineScore.this);
                        }

                        @Override // com.openfeint.internal.APICallback
                        public void onFailure(String failureMessage) {
                            OfflineSupport.updateInProgress.set(false);
                        }
                    });
                    return;
                }
            }
        }
        updateInProgress.set(false);
    }

    private static synchronized void updateToServer() {
        synchronized (OfflineSupport.class) {
            if (updateInProgress.compareAndSet(false, true)) {
                final C0215DB clonedDB = f287db.dup();
                OrderedArgList oal = new OrderedArgList();
                int currAchievement = 0;
                Iterator i$ = clonedDB.achievements.iterator();
                while (i$.hasNext()) {
                    OfflineAchievement oa = i$.next();
                    if (oa.clientCompletionPercentage != oa.serverCompletionPercentage) {
                        OFLog.m181d(TAG, String.format("Updating achievement %s from known %f to %f completion", oa.resourceID, Float.valueOf(oa.serverCompletionPercentage), Float.valueOf(oa.clientCompletionPercentage)));
                        oal.put(String.format("achievements[%d][id]", Integer.valueOf(currAchievement)), oa.resourceID);
                        oal.put(String.format("achievements[%d][percent_complete]", Integer.valueOf(currAchievement)), Float.toString(oa.clientCompletionPercentage));
                        oal.put(String.format("achievements[%d][timestamp]", Integer.valueOf(currAchievement)), oa.timestamp);
                        currAchievement++;
                    }
                }
                int currScore = 0;
                Iterator i$2 = clonedDB.scores.iterator();
                while (i$2.hasNext()) {
                    OfflineScore os = i$2.next();
                    if (os.blobFileName == null) {
                        OFLog.m181d(TAG, String.format("Posting score %d to leaderboard %s", Long.valueOf(os.score), os.leaderboardID));
                        oal.put(String.format("high_scores[%d][leaderboard_id]", Integer.valueOf(currScore)), os.leaderboardID);
                        oal.put(String.format("high_scores[%d][score]", Integer.valueOf(currScore)), Long.toString(os.score));
                        if (os.displayText != null) {
                            oal.put(String.format("high_scores[%d][display_text]", Integer.valueOf(currScore)), os.displayText);
                        }
                        if (os.customData != null) {
                            oal.put(String.format("high_scores[%d][custom_data]", Integer.valueOf(currScore)), os.customData);
                        }
                        oal.put(String.format("high_scores[%d][timestamp]", Integer.valueOf(currScore)), os.timestamp);
                        currScore++;
                    }
                }
                if (currAchievement == 0 && currScore == 0) {
                    uploadScoresWithBlobs();
                } else {
                    final String path = "/xp/games/" + OpenFeintInternal.getInstance().getAppID() + "/offline_syncs";
                    new BaseRequest(oal) { // from class: com.openfeint.internal.offline.OfflineSupport.2
                        @Override // com.openfeint.internal.request.BaseRequest
                        public String method() {
                            return "POST";
                        }

                        @Override // com.openfeint.internal.request.BaseRequest
                        public String path() {
                            return path;
                        }

                        @Override // com.openfeint.internal.request.BaseRequest
                        public void onResponse(int responseCode, byte[] body) {
                        }

                        @Override // com.openfeint.internal.request.BaseRequest
                        public void onResponseOffMainThread(int responseCode, byte[] body) {
                            if (200 <= responseCode && responseCode < 300) {
                                OfflineSupport.f287db.updateOnUpload(clonedDB);
                                OfflineSupport.save();
                                OfflineSupport.uploadScoresWithBlobs();
                            } else {
                                if (responseCode != 0 && 500 > responseCode) {
                                    OfflineSupport.f287db.removeReferencedBlobs();
                                    OfflineSupport.f287db.clear();
                                    OfflineSupport.save();
                                }
                                OfflineSupport.updateInProgress.set(false);
                            }
                        }
                    }.launch();
                }
            }
        }
    }

    public static void updateClientCompletionPercentage(String resourceID, float completionPercentage) {
        if (userID != null) {
            OfflineAchievement a = f287db.findAchievement(resourceID);
            if (a != null) {
                if (a.clientCompletionPercentage < completionPercentage) {
                    a.clientCompletionPercentage = completionPercentage;
                    a.timestamp = now();
                    save();
                    return;
                }
                return;
            }
            OfflineAchievement a2 = new OfflineAchievement();
            a2.resourceID = resourceID;
            a2.serverCompletionPercentage = 0.0f;
            a2.clientCompletionPercentage = completionPercentage;
            a2.timestamp = now();
            f287db.achievements.add(a2);
            save();
        }
    }

    public static void updateServerCompletionPercentage(String resourceID, float completionPercentage) {
        if (userID != null) {
            OfflineAchievement a = f287db.findAchievement(resourceID);
            if (a == null) {
                a = new OfflineAchievement();
                a.resourceID = resourceID;
                a.clientCompletionPercentage = completionPercentage;
                f287db.achievements.add(a);
            }
            a.serverCompletionPercentage = completionPercentage;
            a.timestamp = now();
            save();
        }
    }

    public static float getClientCompletionPercentage(String resourceID) {
        OfflineAchievement a = f287db.findAchievement(resourceID);
        if (a == null) {
            return 0.0f;
        }
        return a.clientCompletionPercentage;
    }

    public static void postOfflineScore(Score s, Leaderboard l) {
        if (userID != null) {
            OfflineScore os = new OfflineScore();
            os.leaderboardID = l.resourceID();
            os.score = s.score;
            os.displayText = s.displayText;
            os.customData = s.customData;
            os.timestamp = now();
            if (s.blob != null) {
                OfflineScore existingScore = null;
                Iterator i$ = f287db.scores.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    OfflineScore scan = i$.next();
                    if (os.leaderboardID.equals(scan.leaderboardID)) {
                        existingScore = scan;
                        break;
                    }
                }
                if (existingScore != null) {
                    if (l.allowsWorseScores || ((l.descendingSortOrder && existingScore.score < s.score) || (!l.descendingSortOrder && existingScore.score > s.score))) {
                        deleteDataFile(existingScore.blobFileName);
                        f287db.scores.remove(existingScore);
                    } else {
                        return;
                    }
                }
                String filename = "unknown.blob";
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA1");
                    md.update(s.blob);
                    filename = String.format("%s.blob", new String(Hex.encodeHex(md.digest())));
                } catch (NoSuchAlgorithmException e) {
                }
                try {
                    Util.saveFile(s.blob, fullPath(filename));
                    os.blobFileName = filename;
                } catch (IOException e2) {
                    return;
                }
            }
            f287db.scores.add(os);
            save();
            if (!isUserTemporary()) {
                SimpleNotification.show(OpenFeintInternal.getRString(C0207RR.string("of_score_submitted_notification")), "@drawable/of_icon_highscore_notification", Notification.Category.HighScore, Notification.Type.Success);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void save() {
        String fileName = filename(userID);
        if (fileName != null) {
            f287db.save(fileName);
        }
    }
}
