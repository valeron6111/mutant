package com.sponsorpay.sdk.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

/* loaded from: classes.dex */
public class HttpResponseParser {
    public static String extractResponseString(HttpResponse httpResponse) {
        try {
            HttpEntity responseEntity = httpResponse.getEntity();
            InputStream inStream = responseEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    sb.append(line + "\n");
                } else {
                    inStream.close();
                    String responseString = sb.toString();
                    return responseString;
                }
            }
        } catch (IOException e) {
            return null;
        }
    }
}
