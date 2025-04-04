package com.openfeint.internal.request.multipart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public abstract class Part {
    private byte[] boundaryBytes;
    protected static final String BOUNDARY = "----------------ULTRASONIC_CUPCAKES___-__-";
    protected static final byte[] BOUNDARY_BYTES = EncodingUtil.getAsciiBytes(BOUNDARY);
    private static final byte[] DEFAULT_BOUNDARY_BYTES = BOUNDARY_BYTES;
    protected static final String CRLF = "\r\n";
    protected static final byte[] CRLF_BYTES = EncodingUtil.getAsciiBytes(CRLF);
    protected static final String QUOTE = "\"";
    protected static final byte[] QUOTE_BYTES = EncodingUtil.getAsciiBytes(QUOTE);
    protected static final String EXTRA = "--";
    protected static final byte[] EXTRA_BYTES = EncodingUtil.getAsciiBytes(EXTRA);
    protected static final String CONTENT_DISPOSITION = "Content-Disposition: form-data; name=";
    protected static final byte[] CONTENT_DISPOSITION_BYTES = EncodingUtil.getAsciiBytes(CONTENT_DISPOSITION);
    protected static final String CONTENT_TYPE = "Content-Type: ";
    protected static final byte[] CONTENT_TYPE_BYTES = EncodingUtil.getAsciiBytes(CONTENT_TYPE);
    protected static final String CHARSET = "; charset=";
    protected static final byte[] CHARSET_BYTES = EncodingUtil.getAsciiBytes(CHARSET);
    protected static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding: ";
    protected static final byte[] CONTENT_TRANSFER_ENCODING_BYTES = EncodingUtil.getAsciiBytes(CONTENT_TRANSFER_ENCODING);

    public abstract String getCharSet();

    public abstract String getContentType();

    public abstract String getName();

    public abstract String getTransferEncoding();

    protected abstract long lengthOfData() throws IOException;

    protected abstract void sendData(OutputStream outputStream) throws IOException;

    public static String getBoundary() {
        return BOUNDARY;
    }

    protected byte[] getPartBoundary() {
        return this.boundaryBytes == null ? DEFAULT_BOUNDARY_BYTES : this.boundaryBytes;
    }

    void setPartBoundary(byte[] boundaryBytes) {
        this.boundaryBytes = boundaryBytes;
    }

    public boolean isRepeatable() {
        return true;
    }

    protected void sendStart(OutputStream out) throws IOException {
        out.write(EXTRA_BYTES);
        out.write(getPartBoundary());
        out.write(CRLF_BYTES);
    }

    protected void sendDispositionHeader(OutputStream out) throws IOException {
        out.write(CONTENT_DISPOSITION_BYTES);
        out.write(QUOTE_BYTES);
        out.write(EncodingUtil.getAsciiBytes(getName()));
        out.write(QUOTE_BYTES);
    }

    protected void sendContentTypeHeader(OutputStream out) throws IOException {
        String contentType = getContentType();
        if (contentType != null) {
            out.write(CRLF_BYTES);
            out.write(CONTENT_TYPE_BYTES);
            out.write(EncodingUtil.getAsciiBytes(contentType));
            String charSet = getCharSet();
            if (charSet != null) {
                out.write(CHARSET_BYTES);
                out.write(EncodingUtil.getAsciiBytes(charSet));
            }
        }
    }

    protected void sendTransferEncodingHeader(OutputStream out) throws IOException {
        String transferEncoding = getTransferEncoding();
        if (transferEncoding != null) {
            out.write(CRLF_BYTES);
            out.write(CONTENT_TRANSFER_ENCODING_BYTES);
            out.write(EncodingUtil.getAsciiBytes(transferEncoding));
        }
    }

    protected void sendEndOfHeader(OutputStream out) throws IOException {
        out.write(CRLF_BYTES);
        out.write(CRLF_BYTES);
    }

    protected void sendEnd(OutputStream out) throws IOException {
        out.write(CRLF_BYTES);
    }

    public void send(OutputStream out) throws IOException {
        sendStart(out);
        sendDispositionHeader(out);
        sendContentTypeHeader(out);
        sendTransferEncodingHeader(out);
        sendEndOfHeader(out);
        sendData(out);
        sendEnd(out);
    }

    public long length() throws IOException {
        if (lengthOfData() < 0) {
            return -1L;
        }
        ByteArrayOutputStream overhead = new ByteArrayOutputStream();
        sendStart(overhead);
        sendDispositionHeader(overhead);
        sendContentTypeHeader(overhead);
        sendTransferEncodingHeader(overhead);
        sendEndOfHeader(overhead);
        sendEnd(overhead);
        return overhead.size() + lengthOfData();
    }

    public String toString() {
        return getName();
    }

    public static void sendParts(OutputStream out, Part[] parts) throws IOException {
        sendParts(out, parts, DEFAULT_BOUNDARY_BYTES);
    }

    public static void sendParts(OutputStream out, Part[] parts, byte[] partBoundary) throws IOException {
        if (parts == null) {
            throw new IllegalArgumentException("Parts may not be null");
        }
        if (partBoundary == null || partBoundary.length == 0) {
            throw new IllegalArgumentException("partBoundary may not be empty");
        }
        for (int i = 0; i < parts.length; i++) {
            parts[i].setPartBoundary(partBoundary);
            parts[i].send(out);
        }
        out.write(EXTRA_BYTES);
        out.write(partBoundary);
        out.write(EXTRA_BYTES);
        out.write(CRLF_BYTES);
    }

    public static long getLengthOfParts(Part[] parts) throws IOException {
        return getLengthOfParts(parts, DEFAULT_BOUNDARY_BYTES);
    }

    public static long getLengthOfParts(Part[] parts, byte[] partBoundary) throws IOException {
        if (parts == null) {
            throw new IllegalArgumentException("Parts may not be null");
        }
        long total = 0;
        for (int i = 0; i < parts.length; i++) {
            parts[i].setPartBoundary(partBoundary);
            long l = parts[i].length();
            if (l < 0) {
                return -1L;
            }
            total += l;
        }
        return total + EXTRA_BYTES.length + partBoundary.length + EXTRA_BYTES.length + CRLF_BYTES.length;
    }
}
