package com.openfeint.internal.request.multipart;

import com.openfeint.internal.logcat.OFLog;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

/* loaded from: classes.dex */
public class MultipartHttpEntity implements HttpEntity {
    private static byte[] MULTIPART_CHARS = EncodingUtil.getAsciiBytes("-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    private static final String MULTIPART_FORM_CONTENT_TYPE = "multipart/form-data";
    private static final String TAG = "MultipartRequestEntity";
    private byte[] multipartBoundary;
    protected Part[] parts;

    private static byte[] generateMultipartBoundary() {
        Random rand = new Random();
        byte[] bytes = new byte[rand.nextInt(11) + 30];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)];
        }
        return bytes;
    }

    public MultipartHttpEntity(Part[] parts) {
        if (parts == null) {
            throw new IllegalArgumentException("parts cannot be null");
        }
        this.parts = parts;
    }

    protected byte[] getMultipartBoundary() {
        if (this.multipartBoundary == null) {
            this.multipartBoundary = generateMultipartBoundary();
        }
        return this.multipartBoundary;
    }

    @Override // org.apache.http.HttpEntity
    public boolean isRepeatable() {
        for (int i = 0; i < this.parts.length; i++) {
            if (!this.parts[i].isRepeatable()) {
                return false;
            }
        }
        return true;
    }

    @Override // org.apache.http.HttpEntity
    public void writeTo(OutputStream out) throws IOException {
        Part.sendParts(out, this.parts, getMultipartBoundary());
    }

    @Override // org.apache.http.HttpEntity
    public long getContentLength() {
        try {
            return Part.getLengthOfParts(this.parts, getMultipartBoundary());
        } catch (Exception e) {
            OFLog.m182e(TAG, "An exception occurred while getting the length of the parts");
            return 0L;
        }
    }

    @Override // org.apache.http.HttpEntity
    public Header getContentType() {
        StringBuffer buffer = new StringBuffer(MULTIPART_FORM_CONTENT_TYPE);
        buffer.append("; boundary=");
        buffer.append(EncodingUtil.getAsciiString(getMultipartBoundary()));
        return new BasicHeader("Content-Type", buffer.toString());
    }

    @Override // org.apache.http.HttpEntity
    public void consumeContent() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.http.HttpEntity
    public InputStream getContent() throws IOException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.http.HttpEntity
    public Header getContentEncoding() {
        return new BasicHeader("Content-Encoding", "text/html; charset=UTF-8");
    }

    @Override // org.apache.http.HttpEntity
    public boolean isChunked() {
        return getContentLength() < 0;
    }

    @Override // org.apache.http.HttpEntity
    public boolean isStreaming() {
        return false;
    }
}
