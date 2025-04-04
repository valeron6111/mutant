package com.alawar.mutant.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: classes.dex */
public class Signer {
    public static String md5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
