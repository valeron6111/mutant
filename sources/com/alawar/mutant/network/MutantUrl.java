package com.alawar.mutant.network;

/* loaded from: classes.dex */
public class MutantUrl {
    private static final String BaseURL = "http://mutantm.alawar.com:8080/mutant-server/";

    public static String giftCode() {
        return "http://mutantm.alawar.com:8080/mutant-server/redeem.jsp";
    }

    public static String flurryCallback() {
        return "http://mutantm.alawar.com:8080/mutant-server/flurry.jsp";
    }

    public static String ping() {
        return "http://mutantm.alawar.com:8080/mutant-server/ping.jsp";
    }
}
