package com.openfeint.internal.request;

import com.openfeint.internal.request.multipart.ByteArrayPartSource;
import com.openfeint.internal.request.multipart.FilePart;
import com.openfeint.internal.resource.BlobUploadParameters;

/* loaded from: classes.dex */
public class CompressedBlobPostRequest extends BlobPostRequest {
    String mFilename;
    BlobUploadParameters mParameters;
    byte mUncompressedData;

    public CompressedBlobPostRequest(BlobUploadParameters parameters, String filename, byte[] uncompressedData) {
        super(parameters, new ByteArrayPartSource(filename, Compression.compress(uncompressedData)), FilePart.DEFAULT_CONTENT_TYPE);
    }
}
