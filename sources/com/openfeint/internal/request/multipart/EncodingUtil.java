package com.openfeint.internal.request.multipart;

import com.openfeint.internal.vendor.org.apache.commons.codec.CharEncoding;
import java.io.UnsupportedEncodingException;

/* loaded from: classes.dex */
public class EncodingUtil {
    public static byte[] getAsciiBytes(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return data.getBytes(CharEncoding.US_ASCII);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("HttpClient requires ASCII support");
        }
    }

    public static String getAsciiString(byte[] data, int offset, int length) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return new String(data, offset, length, CharEncoding.US_ASCII);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("HttpClient requires ASCII support");
        }
    }

    public static String getAsciiString(byte[] data) {
        return getAsciiString(data, 0, data.length);
    }

    public static byte[] getBytes(String data, String charset) {
        if (data == null) {
            throw new IllegalArgumentException("data may not be null");
        }
        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }
        try {
            return data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            return data.getBytes();
        }
    }

    private EncodingUtil() {
    }
}
