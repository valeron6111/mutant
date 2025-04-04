package com.openfeint.internal.request.multipart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class FilePart extends PartBase {
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    public static final String DEFAULT_TRANSFER_ENCODING = "binary";
    protected static final String FILE_NAME = "; filename=";
    private static final byte[] FILE_NAME_BYTES = EncodingUtil.getAsciiBytes(FILE_NAME);
    private PartSource source;

    public FilePart(String name, PartSource partSource, String contentType, String charset) {
        super(name, contentType == null ? DEFAULT_CONTENT_TYPE : contentType, charset == null ? "ISO-8859-1" : charset, DEFAULT_TRANSFER_ENCODING);
        if (partSource == null) {
            throw new IllegalArgumentException("Source may not be null");
        }
        this.source = partSource;
    }

    public FilePart(String name, PartSource partSource) {
        this(name, partSource, (String) null, (String) null);
    }

    public FilePart(String name, File file) throws FileNotFoundException {
        this(name, new FilePartSource(file), (String) null, (String) null);
    }

    public FilePart(String name, File file, String contentType, String charset) throws FileNotFoundException {
        this(name, new FilePartSource(file), contentType, charset);
    }

    public FilePart(String name, String fileName, File file) throws FileNotFoundException {
        this(name, new FilePartSource(fileName, file), (String) null, (String) null);
    }

    public FilePart(String name, String fileName, File file, String contentType, String charset) throws FileNotFoundException {
        this(name, new FilePartSource(fileName, file), contentType, charset);
    }

    @Override // com.openfeint.internal.request.multipart.Part
    protected void sendDispositionHeader(OutputStream out) throws IOException {
        super.sendDispositionHeader(out);
        String filename = this.source.getFileName();
        if (filename != null) {
            out.write(FILE_NAME_BYTES);
            out.write(QUOTE_BYTES);
            out.write(EncodingUtil.getAsciiBytes(filename));
            out.write(QUOTE_BYTES);
        }
    }

    @Override // com.openfeint.internal.request.multipart.Part
    protected void sendData(OutputStream out) throws IOException {
        if (lengthOfData() != 0) {
            byte[] tmp = new byte[4096];
            InputStream instream = this.source.createInputStream();
            while (true) {
                try {
                    int len = instream.read(tmp);
                    if (len >= 0) {
                        out.write(tmp, 0, len);
                    } else {
                        return;
                    }
                } finally {
                    instream.close();
                }
            }
        }
    }

    protected PartSource getSource() {
        return this.source;
    }

    @Override // com.openfeint.internal.request.multipart.Part
    protected long lengthOfData() throws IOException {
        return this.source.getLength();
    }
}
