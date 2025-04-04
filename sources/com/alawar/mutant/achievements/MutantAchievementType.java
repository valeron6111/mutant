package com.alawar.mutant.achievements;

import com.alawar.mutant.database.DbBuilder;
import com.alawar.mutant.jni.MutantMessages;

/* loaded from: classes.dex */
public enum MutantAchievementType {
    Experienced,
    Zombie,
    Hero,
    Stalker,
    Lucky,
    DemolitionMan,
    ZombieSlayer,
    Strategist,
    SuperHero,
    Survivor,
    Dodger,
    Arachnophobic,
    Steel,
    SelfKiller,
    Shooter,
    Titan,
    Blood,
    InsectKiller,
    UniversalSoldier,
    Terminator,
    Immortal,
    Monumental;

    public static MutantAchievementType nativeToJava(int nativeValue) {
        switch (nativeValue) {
            case DbBuilder.ID_COLUMN /* 0 */:
                return Zombie;
            case 1:
                return ZombieSlayer;
            case 2:
                return Experienced;
            case 3:
                return Stalker;
            case 4:
            case 6:
            case 8:
            case MutantMessages.cOpenAchievements /* 13 */:
            case MutantMessages.cReportTotalRating /* 15 */:
            case 17:
            case 20:
            case 23:
            case 27:
            case 28:
            case 29:
            case 31:
            case 32:
            case 33:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            default:
                throw new RuntimeException("Invalid perk value");
            case 5:
                return Survivor;
            case MutantMessages.cShareWithFriends /* 7 */:
                return Hero;
            case MutantMessages.cProgress /* 9 */:
                return Steel;
            case 10:
                return Strategist;
            case MutantMessages.cShareWithFriendsImmediate /* 11 */:
                return UniversalSoldier;
            case MutantMessages.cRateApp /* 12 */:
                return Arachnophobic;
            case MutantMessages.cOpenLeaderboard /* 14 */:
                return SelfKiller;
            case 16:
                return Lucky;
            case 18:
                return Monumental;
            case 19:
                return InsectKiller;
            case 21:
                return Dodger;
            case 22:
                return Shooter;
            case 24:
                return DemolitionMan;
            case 25:
                return SuperHero;
            case 26:
                return Terminator;
            case 30:
                return Blood;
            case 34:
                return Titan;
            case 46:
                return Immortal;
        }
    }
}
