package com.alawar.mutant.achievements;

import com.alawar.mutant.jni.MutantMessages;
import com.alawar.mutant.thirdparty.openfeint.OpenFeintClient;
import com.openfeint.api.resource.Leaderboard;
import com.openfeint.api.resource.Score;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Achievements {
    public static final String TOTAL_SCORE_LEADER_BOARD_ID = "1180217";
    static Achievements _instance;
    OpenFeintClient _client;
    static final String[] ofAchievementIDs = {MutantMessages.sEmpty, "1556592", "1565702", "1565712", "1565722", "1565732", "1565742", "1565752", "1565762", "1565772", "1565782", "1565792", "1565802", "1565812", "1565822", "1565832", "1565842", "1565852", "1565862", "1565872", "1565882", "1565892"};
    private static List<String> awardsToSend = new ArrayList();

    public static Achievements instance() {
        if (_instance == null) {
            _instance = new Achievements();
        }
        return _instance;
    }

    public void initialize(OpenFeintClient client) {
        this._client = client;
    }

    private void awardInternal(MutantAchievementType achType) {
        awardsToSend.add(ofAchievementIDs[achType.ordinal()]);
        if (this._client != null) {
            List<String> awardsToRemove = new ArrayList<>();
            try {
                for (String award : awardsToSend) {
                    this._client.reportAchievement(award);
                    awardsToRemove.add(award);
                }
            } finally {
                awardsToSend.removeAll(awardsToRemove);
            }
        }
    }

    public void award(int achType) {
        awardInternal(MutantAchievementType.nativeToJava(achType));
    }

    public void reportTotalRating(int rating) {
        new Score(rating).submitTo(new Leaderboard(TOTAL_SCORE_LEADER_BOARD_ID), new Score.SubmitToCB() { // from class: com.alawar.mutant.achievements.Achievements.1
            @Override // com.openfeint.api.resource.Score.SubmitToCB
            public void onSuccess(boolean newHighScore) {
            }
        });
    }
}
