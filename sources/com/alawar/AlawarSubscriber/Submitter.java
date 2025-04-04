package com.alawar.AlawarSubscriber;

import android.net.Uri;
import com.alawar.mutant.jni.MutantMessages;
import com.tapjoy.TapjoyConstants;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/* loaded from: classes.dex */
public class Submitter {
    private static final String DEVICE_PARAM = "&device=%s";
    private static final String GAME_PARAM = "&game=%s";
    private static final int HTTP_STATUS_OK = 200;
    private static final String LOCALE_PARAM = "&locale=%s";
    private static final String NAME_PARAM = "&name=%s";
    private static final String SECRET_WORD = "AlAwAr";
    private static final String SUBSCRIPTION_PAGE = "http://subscribe.import.services.alawar.com/subscribe.php?email=%s&sign=%s";
    private static final String USER_AGENT_TPL = "%s (Linux; Android)";
    private String mGameName;
    private String mUserAgent;

    public Submitter(String gameName) {
        this.mUserAgent = null;
        this.mGameName = MutantMessages.sEmpty;
        if (gameName != null) {
            this.mGameName = gameName;
        }
        this.mUserAgent = String.format(USER_AGENT_TPL, gameName);
    }

    public Boolean subscribe(String userEmail, String userName) {
        if (userEmail == null) {
            return false;
        }
        String deviceName = getDeviceInfo();
        String localeName = getLocaleInfo();
        String signInput = userEmail + "::" + this.mGameName + "::" + userName + "::" + deviceName + "::" + localeName + "::" + SECRET_WORD;
        String sign = getMD5Hash(signInput);
        if (sign == null) {
            return false;
        }
        String request = String.format(SUBSCRIPTION_PAGE, Uri.encode(userEmail), Uri.encode(sign));
        if (this.mGameName != null) {
            request = request + String.format(GAME_PARAM, Uri.encode(this.mGameName));
        }
        if (userName != null) {
            request = request + String.format(NAME_PARAM, Uri.encode(userName));
        }
        if (deviceName != null) {
            request = request + String.format(DEVICE_PARAM, Uri.encode(deviceName));
        }
        if (localeName != null) {
            request = request + String.format(LOCALE_PARAM, Uri.encode(localeName));
        }
        boolean result = requestURL(request).booleanValue();
        return Boolean.valueOf(result);
    }

    protected Boolean requestURL(String url) {
        if (url == null) {
            return false;
        }
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        request.setHeader("User-Agent", this.mUserAgent);
        try {
            HttpResponse response = client.execute(request);
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == HTTP_STATUS_OK) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static String getDeviceInfo() {
        return TapjoyConstants.TJC_DEVICE_PLATFORM_TYPE;
    }

    private static String getLocaleInfo() {
        return Locale.getDefault().getLanguage();
    }

    public static String getMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);
            while (md5.length() < 32) {
                md5 = "0" + md5;
            }
            return md5;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
