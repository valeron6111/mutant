package com.openfeint.internal.request;

import com.openfeint.api.OpenFeintSettings;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.logcat.OFLog;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/* loaded from: classes.dex */
public class Compression {
    private static String TAG = "Compression";
    private static final byte[] MagicHeader = "OFZLHDR0".getBytes();

    private enum CompressionMethod {
        Default,
        Uncompressed,
        LegacyHeaderless
    }

    public static byte[] compress(byte[] uncompressedData) {
        byte[] uploadData = uncompressedData;
        try {
            switch (compressionMethod()) {
                case Default:
                    byte[] tenativeData = _compress(uncompressedData);
                    byte[] uncompressedSize = integerToLittleEndianByteArray(uncompressedData.length);
                    int compressedLength = tenativeData.length + MagicHeader.length + uncompressedSize.length;
                    if (compressedLength < uncompressedData.length) {
                        uploadData = new byte[compressedLength];
                        System.arraycopy(MagicHeader, 0, uploadData, 0, MagicHeader.length);
                        System.arraycopy(uncompressedSize, 0, uploadData, MagicHeader.length, uncompressedSize.length);
                        System.arraycopy(tenativeData, 0, uploadData, MagicHeader.length + 4, tenativeData.length);
                        OFLog.m183i(TAG, String.format("Using Default strategy: orig %d bytes, compressed %d bytes (%.2f%% of original size)", Integer.valueOf(uncompressedData.length), Integer.valueOf(compressedLength), Float.valueOf((compressedLength / uncompressedData.length) * 100.0f)));
                        break;
                    } else {
                        OFLog.m183i(TAG, "Using Default strategy: compression declined");
                        break;
                    }
                case LegacyHeaderless:
                    uploadData = _compress(uncompressedData);
                    OFLog.m183i(TAG, String.format("Using Default strategy: orig %d bytes, compressed %d bytes (%.2f%% of original size)", Integer.valueOf(uncompressedData.length), Integer.valueOf(uploadData.length), Float.valueOf((uploadData.length / uncompressedData.length) * 100.0f)));
                    break;
                default:
                    OFLog.m183i(TAG, "Using Uncompressed strategy");
                    break;
            }
            return uploadData;
        } catch (IOException e) {
            return null;
        }
    }

    public static byte[] decompress(byte[] body) throws IOException {
        switch (compressionMethod()) {
            case Default:
                int i = 0;
                if (MagicHeader.length < body.length) {
                    while (i < MagicHeader.length && MagicHeader[i] == body[i]) {
                        i++;
                    }
                }
                if (i == MagicHeader.length) {
                    int skip = MagicHeader.length + 4;
                    ByteArrayInputStream postHeaderStream = new ByteArrayInputStream(body, skip, body.length - skip);
                    InputStream decompressedStream = new InflaterInputStream(postHeaderStream);
                    break;
                }
                break;
        }
        return body;
    }

    private static CompressionMethod compressionMethod() {
        String s = (String) OpenFeintInternal.getInstance().getSettings().get(OpenFeintSettings.SettingCloudStorageCompressionStrategy);
        if (s != null) {
            if (s.equals(OpenFeintSettings.CloudStorageCompressionStrategyLegacyHeaderlessCompression)) {
                return CompressionMethod.LegacyHeaderless;
            }
            if (s.equals(OpenFeintSettings.CloudStorageCompressionStrategyNoCompression)) {
                return CompressionMethod.Uncompressed;
            }
        }
        return CompressionMethod.Default;
    }

    private static byte[] _compress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflaterOutputStream dos = new DeflaterOutputStream(baos);
        dos.write(data);
        dos.close();
        return baos.toByteArray();
    }

    private static byte[] integerToLittleEndianByteArray(int i) {
        byte[] rv = {(byte) (i >> 0), (byte) (i >> 8), (byte) (i >> 16), (byte) (i >> 24)};
        return rv;
    }
}
