package com.alawar.mutant.billing;

import android.text.TextUtils;
import android.util.Log;
import com.alawar.mutant.billing.Consts;
import com.alawar.mutant.jni.MutantMessages;
import com.alawar.mutant.util.Base64;
import com.alawar.mutant.util.Base64DecoderException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Security {
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String TAG = "MutantSecurity";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static HashSet<Long> sKnownNonces = new HashSet<>();

    public static class VerifiedPurchase {
        public String developerPayload;
        public String notificationId;
        public String orderId;
        public String productId;
        public Consts.PurchaseState purchaseState;
        public long purchaseTime;

        public VerifiedPurchase(Consts.PurchaseState purchaseState, String notificationId, String productId, String orderId, long purchaseTime, String developerPayload) {
            this.purchaseState = purchaseState;
            this.notificationId = notificationId;
            this.productId = productId;
            this.orderId = orderId;
            this.purchaseTime = purchaseTime;
            this.developerPayload = developerPayload;
        }
    }

    public static long generateNonce() {
        long nonce = RANDOM.nextLong();
        sKnownNonces.add(Long.valueOf(nonce));
        return nonce;
    }

    public static void removeNonce(long nonce) {
        sKnownNonces.remove(Long.valueOf(nonce));
    }

    public static boolean isNonceKnown(long nonce) {
        return sKnownNonces.contains(Long.valueOf(nonce));
    }

    public static ArrayList<VerifiedPurchase> verifyPurchase(String signedData, String signature) {
        if (signedData == null) {
            Log.e(TAG, "data is null");
            return null;
        }
        Log.i(TAG, "signedData: " + signedData);
        boolean verified = false;
        if (!TextUtils.isEmpty(signature)) {
            PublicKey key = generatePublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqFb8unUQfyGNIjQz4T4yeW4N7req84S1srcV6Jpkc7U4yueVNTKQ4R/3WOvk0zkXI+d+Ny5R9oCFOhJrHKs9t5smq96p0pXAuvENF1QYkA0tl7eYaoRluvK0VmfJGtPiq1n5L4zBrLlTPoGXwGAKzqm1NtkueZURCVW9u6+zfysqlqpsg9FQedtnQFkkpYO8v2gcWtZOs3i1a4/zIY88Uq3+258dWMaLOHD5ZTcaPFiSvpkZ85CUdfVhxzIMOJ4hLk+dfyIc+tRZQhHDj4rsjEMz+eGtmymC0ExMo1Q9BNUNPxIl+IsVjRoBcdIFloX1FRu15b1Hmm9USlB4xiF3SQIDAQAB");
            verified = verify(key, signedData, signature);
            if (!verified) {
                Log.w(TAG, "signature does not match data.");
                return null;
            }
        }
        int numTransactions = 0;
        try {
            JSONObject jObject = new JSONObject(signedData);
            long nonce = jObject.optLong("nonce");
            JSONArray jTransactionsArray = jObject.optJSONArray("orders");
            if (jTransactionsArray != null) {
                numTransactions = jTransactionsArray.length();
            }
            if (!isNonceKnown(nonce)) {
                Log.w(TAG, "Nonce not found: " + nonce);
                return null;
            }
            ArrayList<VerifiedPurchase> purchases = new ArrayList<>();
            for (int i = 0; i < numTransactions; i++) {
                try {
                    JSONObject jElement = jTransactionsArray.getJSONObject(i);
                    int response = jElement.getInt("purchaseState");
                    Consts.PurchaseState purchaseState = Consts.PurchaseState.valueOf(response);
                    String productId = jElement.getString("productId");
                    jElement.getString("packageName");
                    long purchaseTime = jElement.getLong("purchaseTime");
                    String orderId = jElement.optString("orderId", MutantMessages.sEmpty);
                    String notifyId = null;
                    if (jElement.has("notificationId")) {
                        notifyId = jElement.getString("notificationId");
                    }
                    String developerPayload = jElement.optString("developerPayload", null);
                    if (purchaseState != Consts.PurchaseState.PURCHASED || verified) {
                        purchases.add(new VerifiedPurchase(purchaseState, notifyId, productId, orderId, purchaseTime, developerPayload));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception: ", e);
                    return null;
                }
            }
            removeNonce(nonce);
            return purchases;
        } catch (JSONException e2) {
            return null;
        }
    }

    public static PublicKey generatePublicKey(String encodedPublicKey) {
        try {
            byte[] decodedKey = Base64.decode(encodedPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (Base64DecoderException e) {
            Log.e(TAG, "Base64 decoding failed.");
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        } catch (InvalidKeySpecException e3) {
            Log.e(TAG, "Invalid key specification.");
            throw new IllegalArgumentException(e3);
        }
    }

    public static boolean verify(PublicKey publicKey, String signedData, String signature) {
        Log.i(TAG, "signature: " + signature);
        try {
            Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            if (!sig.verify(Base64.decode(signature))) {
                Log.e(TAG, "Signature verification failed.");
                return false;
            }
            return true;
        } catch (Base64DecoderException e) {
            Log.e(TAG, "Base64 decoding failed.");
            return false;
        } catch (InvalidKeyException e2) {
            Log.e(TAG, "Invalid key specification.");
            return false;
        } catch (NoSuchAlgorithmException e3) {
            Log.e(TAG, "NoSuchAlgorithmException.");
            return false;
        } catch (SignatureException e4) {
            Log.e(TAG, "Signature exception.");
            return false;
        }
    }
}
