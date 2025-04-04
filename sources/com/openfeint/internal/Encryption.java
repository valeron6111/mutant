package com.openfeint.internal;

import com.openfeint.internal.logcat.OFLog;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/* loaded from: classes.dex */
public class Encryption {
    private static final String INSTANCE = "PBEWithSHA256And256BitAES-CBC-BC";
    private static final int SALT_ITERATIONS = 10;
    private static final int SALT_LENGTH = 10;
    private static final String TAG = "Encryption";
    private static SecretKey secretKey;

    public static CipherOutputStream encryptionWrap(OutputStream os) {
        try {
            byte[] salt = new byte[10];
            new SecureRandom().nextBytes(salt);
            os.write(salt);
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 10);
            Cipher c = Cipher.getInstance(INSTANCE);
            c.init(1, secretKey, pbeParamSpec);
            return new CipherOutputStream(os, c);
        } catch (Exception e) {
            OFLog.m182e(TAG, e.getMessage());
            return null;
        }
    }

    public static CipherInputStream decryptionWrap(InputStream is) {
        try {
            byte[] salt = new byte[10];
            if (is.read(salt) != 10) {
                throw new Exception("Couldn't read entire salt");
            }
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 10);
            Cipher c = Cipher.getInstance(INSTANCE);
            c.init(2, secretKey, pbeParamSpec);
            return new CipherInputStream(is, c);
        } catch (Exception e) {
            OFLog.m182e(TAG, e.getMessage());
            return null;
        }
    }

    public static boolean init(String password) {
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory keyFac = SecretKeyFactory.getInstance(INSTANCE);
            secretKey = keyFac.generateSecret(pbeKeySpec);
            byte[] testString = INSTANCE.getBytes();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CipherOutputStream cos = encryptionWrap(baos);
            cos.write(testString);
            cos.close();
            byte[] encryptedContents = baos.toByteArray();
            if (encryptedContents.length == 0) {
                throw new Exception();
            }
            CipherInputStream cis = decryptionWrap(new ByteArrayInputStream(encryptedContents));
            byte[] decryptedContents = Util.toByteArray(cis);
            if (Arrays.equals(decryptedContents, testString)) {
                return true;
            }
            throw new Exception();
        } catch (Exception e) {
            secretKey = null;
            return false;
        }
    }

    public static boolean initialized() {
        return secretKey != null;
    }

    public static InputStream decrypt(File file) throws FileNotFoundException {
        return decryptionWrap(new FileInputStream(file));
    }

    public static byte[] decryptFile(String path) throws FileNotFoundException, IOException {
        return Util.toByteArray(decrypt(new File(path)));
    }

    public static byte[] decrypt(byte[] input) {
        try {
            return Util.toByteArray(decryptionWrap(new ByteArrayInputStream(input)));
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean encrypt(byte[] in, String path) {
        try {
            OutputStream os = encrypt(path);
            os.write(in);
            os.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static OutputStream encrypt(String path) throws FileNotFoundException {
        return encryptionWrap(new FileOutputStream(new File(path)));
    }

    public static byte[] encrypt(byte[] input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CipherOutputStream enc = encryptionWrap(baos);
            enc.write(input);
            enc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
