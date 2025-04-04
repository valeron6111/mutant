package com.openfeint.internal.vendor.org.apache.commons.codec.binary;

import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.internal.vendor.org.apache.commons.codec.BinaryDecoder;
import com.openfeint.internal.vendor.org.apache.commons.codec.BinaryEncoder;
import com.openfeint.internal.vendor.org.apache.commons.codec.DecoderException;
import com.openfeint.internal.vendor.org.apache.commons.codec.EncoderException;
import java.math.BigInteger;

/* loaded from: classes.dex */
public class Base64 implements BinaryEncoder, BinaryDecoder {
    static final int CHUNK_SIZE = 76;
    private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MASK_6BITS = 63;
    private static final int MASK_8BITS = 255;
    private byte[] buffer;
    private int currentLinePos;
    private final int decodeSize;
    private final int encodeSize;
    private final byte[] encodeTable;
    private boolean eof;
    private final int lineLength;
    private final byte[] lineSeparator;
    private int modulus;
    private int pos;
    private int readPos;

    /* renamed from: x */
    private int f289x;
    static final byte[] CHUNK_SEPARATOR = {13, 10};
    private static final byte[] STANDARD_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] URL_SAFE_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
    private static final byte PAD = 61;
    private static final byte[] DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, PAD, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};

    public Base64() {
        this(false);
    }

    public Base64(boolean urlSafe) {
        this(CHUNK_SIZE, CHUNK_SEPARATOR, urlSafe);
    }

    public Base64(int lineLength) {
        this(lineLength, CHUNK_SEPARATOR);
    }

    public Base64(int lineLength, byte[] lineSeparator) {
        this(lineLength, lineSeparator, false);
    }

    public Base64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
        if (lineSeparator == null) {
            lineLength = 0;
            lineSeparator = CHUNK_SEPARATOR;
        }
        this.lineLength = lineLength > 0 ? (lineLength / 4) * 4 : 0;
        this.lineSeparator = new byte[lineSeparator.length];
        System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
        if (lineLength > 0) {
            this.encodeSize = lineSeparator.length + 4;
        } else {
            this.encodeSize = 4;
        }
        this.decodeSize = this.encodeSize - 1;
        if (containsBase64Byte(lineSeparator)) {
            String sep = StringUtils.newStringUtf8(lineSeparator);
            throw new IllegalArgumentException("lineSeperator must not contain base64 characters: [" + sep + "]");
        }
        this.encodeTable = urlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
    }

    public boolean isUrlSafe() {
        return this.encodeTable == URL_SAFE_ENCODE_TABLE;
    }

    boolean hasData() {
        return this.buffer != null;
    }

    int avail() {
        if (this.buffer != null) {
            return this.pos - this.readPos;
        }
        return 0;
    }

    private void resizeBuffer() {
        if (this.buffer == null) {
            this.buffer = new byte[DEFAULT_BUFFER_SIZE];
            this.pos = 0;
            this.readPos = 0;
        } else {
            byte[] b = new byte[this.buffer.length * 2];
            System.arraycopy(this.buffer, 0, b, 0, this.buffer.length);
            this.buffer = b;
        }
    }

    int readResults(byte[] b, int bPos, int bAvail) {
        if (this.buffer != null) {
            int len = Math.min(avail(), bAvail);
            if (this.buffer != b) {
                System.arraycopy(this.buffer, this.readPos, b, bPos, len);
                this.readPos += len;
                if (this.readPos >= this.pos) {
                    this.buffer = null;
                    return len;
                }
                return len;
            }
            this.buffer = null;
            return len;
        }
        return this.eof ? -1 : 0;
    }

    void setInitialBuffer(byte[] out, int outPos, int outAvail) {
        if (out != null && out.length == outAvail) {
            this.buffer = out;
            this.pos = outPos;
            this.readPos = outPos;
        }
    }

    void encode(byte[] bArr, int inPos, int inAvail) {
        if (!this.eof) {
            if (inAvail < 0) {
                this.eof = true;
                if (this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
                    resizeBuffer();
                }
                switch (this.modulus) {
                    case 1:
                        byte[] bArr2 = this.buffer;
                        int i = this.pos;
                        this.pos = i + 1;
                        bArr2[i] = this.encodeTable[(this.f289x >> 2) & MASK_6BITS];
                        byte[] bArr3 = this.buffer;
                        int i2 = this.pos;
                        this.pos = i2 + 1;
                        bArr3[i2] = this.encodeTable[(this.f289x << 4) & MASK_6BITS];
                        if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                            byte[] bArr4 = this.buffer;
                            int i3 = this.pos;
                            this.pos = i3 + 1;
                            bArr4[i3] = PAD;
                            byte[] bArr5 = this.buffer;
                            int i4 = this.pos;
                            this.pos = i4 + 1;
                            bArr5[i4] = PAD;
                            break;
                        }
                        break;
                    case 2:
                        byte[] bArr6 = this.buffer;
                        int i5 = this.pos;
                        this.pos = i5 + 1;
                        bArr6[i5] = this.encodeTable[(this.f289x >> 10) & MASK_6BITS];
                        byte[] bArr7 = this.buffer;
                        int i6 = this.pos;
                        this.pos = i6 + 1;
                        bArr7[i6] = this.encodeTable[(this.f289x >> 4) & MASK_6BITS];
                        byte[] bArr8 = this.buffer;
                        int i7 = this.pos;
                        this.pos = i7 + 1;
                        bArr8[i7] = this.encodeTable[(this.f289x << 2) & MASK_6BITS];
                        if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                            byte[] bArr9 = this.buffer;
                            int i8 = this.pos;
                            this.pos = i8 + 1;
                            bArr9[i8] = PAD;
                            break;
                        }
                        break;
                }
                if (this.lineLength > 0 && this.pos > 0) {
                    System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
                    this.pos += this.lineSeparator.length;
                    return;
                }
                return;
            }
            int i9 = 0;
            int inPos2 = inPos;
            while (i9 < inAvail) {
                if (this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
                    resizeBuffer();
                }
                int i10 = this.modulus + 1;
                this.modulus = i10;
                this.modulus = i10 % 3;
                int inPos3 = inPos2 + 1;
                int i11 = bArr[inPos2];
                if (i11 < 0) {
                    i11 += 256;
                }
                this.f289x = (this.f289x << 8) + i11;
                if (this.modulus == 0) {
                    byte[] bArr10 = this.buffer;
                    int i12 = this.pos;
                    this.pos = i12 + 1;
                    bArr10[i12] = this.encodeTable[(this.f289x >> 18) & MASK_6BITS];
                    byte[] bArr11 = this.buffer;
                    int i13 = this.pos;
                    this.pos = i13 + 1;
                    bArr11[i13] = this.encodeTable[(this.f289x >> 12) & MASK_6BITS];
                    byte[] bArr12 = this.buffer;
                    int i14 = this.pos;
                    this.pos = i14 + 1;
                    bArr12[i14] = this.encodeTable[(this.f289x >> 6) & MASK_6BITS];
                    byte[] bArr13 = this.buffer;
                    int i15 = this.pos;
                    this.pos = i15 + 1;
                    bArr13[i15] = this.encodeTable[this.f289x & MASK_6BITS];
                    this.currentLinePos += 4;
                    if (this.lineLength > 0 && this.lineLength <= this.currentLinePos) {
                        System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
                        this.pos += this.lineSeparator.length;
                        this.currentLinePos = 0;
                    }
                }
                i9++;
                inPos2 = inPos3;
            }
        }
    }

    void decode(byte[] in, int inPos, int inAvail) {
        int result;
        if (!this.eof) {
            if (inAvail < 0) {
                this.eof = true;
            }
            int i = 0;
            int inPos2 = inPos;
            while (true) {
                if (i >= inAvail) {
                    break;
                }
                if (this.buffer == null || this.buffer.length - this.pos < this.decodeSize) {
                    resizeBuffer();
                }
                int inPos3 = inPos2 + 1;
                byte b = in[inPos2];
                if (b == 61) {
                    this.eof = true;
                    break;
                }
                if (b >= 0 && b < DECODE_TABLE.length && (result = DECODE_TABLE[b]) >= 0) {
                    int i2 = this.modulus + 1;
                    this.modulus = i2;
                    this.modulus = i2 % 4;
                    this.f289x = (this.f289x << 6) + result;
                    if (this.modulus == 0) {
                        byte[] bArr = this.buffer;
                        int i3 = this.pos;
                        this.pos = i3 + 1;
                        bArr[i3] = (byte) ((this.f289x >> 16) & 255);
                        byte[] bArr2 = this.buffer;
                        int i4 = this.pos;
                        this.pos = i4 + 1;
                        bArr2[i4] = (byte) ((this.f289x >> 8) & 255);
                        byte[] bArr3 = this.buffer;
                        int i5 = this.pos;
                        this.pos = i5 + 1;
                        bArr3[i5] = (byte) (this.f289x & 255);
                    }
                }
                i++;
                inPos2 = inPos3;
            }
            if (this.eof && this.modulus != 0) {
                this.f289x <<= 6;
                switch (this.modulus) {
                    case 2:
                        this.f289x <<= 6;
                        byte[] bArr4 = this.buffer;
                        int i6 = this.pos;
                        this.pos = i6 + 1;
                        bArr4[i6] = (byte) ((this.f289x >> 16) & 255);
                        break;
                    case 3:
                        byte[] bArr5 = this.buffer;
                        int i7 = this.pos;
                        this.pos = i7 + 1;
                        bArr5[i7] = (byte) ((this.f289x >> 16) & 255);
                        byte[] bArr6 = this.buffer;
                        int i8 = this.pos;
                        this.pos = i8 + 1;
                        bArr6[i8] = (byte) ((this.f289x >> 8) & 255);
                        break;
                }
            }
        }
    }

    public static boolean isBase64(byte octet) {
        return octet == 61 || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1);
    }

    public static boolean isArrayByteBase64(byte[] arrayOctet) {
        for (int i = 0; i < arrayOctet.length; i++) {
            if (!isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean containsBase64Byte(byte[] arrayOctet) {
        for (byte b : arrayOctet) {
            if (isBase64(b)) {
                return true;
            }
        }
        return false;
    }

    public static byte[] encodeBase64(byte[] binaryData) {
        return encodeBase64(binaryData, false);
    }

    public static String encodeBase64String(byte[] binaryData) {
        return StringUtils.newStringUtf8(encodeBase64(binaryData, true));
    }

    public static byte[] encodeBase64URLSafe(byte[] binaryData) {
        return encodeBase64(binaryData, false, true);
    }

    public static String encodeBase64URLSafeString(byte[] binaryData) {
        return StringUtils.newStringUtf8(encodeBase64(binaryData, false, true));
    }

    public static byte[] encodeBase64Chunked(byte[] binaryData) {
        return encodeBase64(binaryData, true);
    }

    @Override // com.openfeint.internal.vendor.org.apache.commons.codec.Decoder
    public Object decode(Object pObject) throws DecoderException {
        if (pObject instanceof byte[]) {
            return decode((byte[]) pObject);
        }
        if (pObject instanceof String) {
            return decode((String) pObject);
        }
        throw new DecoderException("Parameter supplied to Base64 decode is not a byte[] or a String");
    }

    public byte[] decode(String pArray) {
        return decode(StringUtils.getBytesUtf8(pArray));
    }

    @Override // com.openfeint.internal.vendor.org.apache.commons.codec.BinaryDecoder
    public byte[] decode(byte[] pArray) {
        reset();
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        long len = (pArray.length * 3) / 4;
        byte[] buf = new byte[(int) len];
        setInitialBuffer(buf, 0, buf.length);
        decode(pArray, 0, pArray.length);
        decode(pArray, 0, -1);
        byte[] result = new byte[this.pos];
        readResults(result, 0, result.length);
        return result;
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
        return encodeBase64(binaryData, isChunked, false);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
        return encodeBase64(binaryData, isChunked, urlSafe, Integer.MAX_VALUE);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
        if (binaryData != null && binaryData.length != 0) {
            long len = getEncodeLength(binaryData, CHUNK_SIZE, CHUNK_SEPARATOR);
            if (len > maxResultSize) {
                throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maxium size of " + maxResultSize);
            }
            Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe);
            return b64.encode(binaryData);
        }
        return binaryData;
    }

    public static byte[] decodeBase64(String base64String) {
        return new Base64().decode(base64String);
    }

    public static byte[] decodeBase64(byte[] base64Data) {
        return new Base64().decode(base64Data);
    }

    static byte[] discardWhitespace(byte[] data) {
        byte[] groomedData = new byte[data.length];
        int bytesCopied = 0;
        for (int i = 0; i < data.length; i++) {
            switch (data[i]) {
                case MutantMessages.cProgress /* 9 */:
                case 10:
                case MutantMessages.cOpenAchievements /* 13 */:
                case 32:
                    break;
                default:
                    groomedData[bytesCopied] = data[i];
                    bytesCopied++;
                    break;
            }
        }
        byte[] packedData = new byte[bytesCopied];
        System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
        return packedData;
    }

    private static boolean isWhiteSpace(byte byteToCheck) {
        switch (byteToCheck) {
            case MutantMessages.cProgress /* 9 */:
            case 10:
            case MutantMessages.cOpenAchievements /* 13 */:
            case 32:
                return true;
            default:
                return false;
        }
    }

    @Override // com.openfeint.internal.vendor.org.apache.commons.codec.Encoder
    public Object encode(Object pObject) throws EncoderException {
        if (!(pObject instanceof byte[])) {
            throw new EncoderException("Parameter supplied to Base64 encode is not a byte[]");
        }
        return encode((byte[]) pObject);
    }

    public String encodeToString(byte[] pArray) {
        return StringUtils.newStringUtf8(encode(pArray));
    }

    @Override // com.openfeint.internal.vendor.org.apache.commons.codec.BinaryEncoder
    public byte[] encode(byte[] pArray) {
        reset();
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        long len = getEncodeLength(pArray, this.lineLength, this.lineSeparator);
        byte[] buf = new byte[(int) len];
        setInitialBuffer(buf, 0, buf.length);
        encode(pArray, 0, pArray.length);
        encode(pArray, 0, -1);
        if (this.buffer != buf) {
            readResults(buf, 0, buf.length);
        }
        if (isUrlSafe() && this.pos < buf.length) {
            byte[] smallerBuf = new byte[this.pos];
            System.arraycopy(buf, 0, smallerBuf, 0, this.pos);
            return smallerBuf;
        }
        return buf;
    }

    private static long getEncodeLength(byte[] pArray, int chunkSize, byte[] chunkSeparator) {
        int chunkSize2 = (chunkSize / 4) * 4;
        long len = (pArray.length * 4) / 3;
        long mod = len % 4;
        if (mod != 0) {
            len += 4 - mod;
        }
        if (chunkSize2 > 0) {
            boolean lenChunksPerfectly = len % ((long) chunkSize2) == 0;
            long len2 = len + ((len / chunkSize2) * chunkSeparator.length);
            if (!lenChunksPerfectly) {
                return len2 + chunkSeparator.length;
            }
            return len2;
        }
        return len;
    }

    public static BigInteger decodeInteger(byte[] pArray) {
        return new BigInteger(1, decodeBase64(pArray));
    }

    public static byte[] encodeInteger(BigInteger bigInt) {
        if (bigInt == null) {
            throw new NullPointerException("encodeInteger called with null parameter");
        }
        return encodeBase64(toIntegerBytes(bigInt), false);
    }

    static byte[] toIntegerBytes(BigInteger bigInt) {
        int bitlen = ((bigInt.bitLength() + 7) >> 3) << 3;
        byte[] bigBytes = bigInt.toByteArray();
        if (bigInt.bitLength() % 8 == 0 || (bigInt.bitLength() / 8) + 1 != bitlen / 8) {
            int startSrc = 0;
            int len = bigBytes.length;
            if (bigInt.bitLength() % 8 == 0) {
                startSrc = 1;
                len--;
            }
            int startDst = (bitlen / 8) - len;
            byte[] resizedBytes = new byte[bitlen / 8];
            System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
            return resizedBytes;
        }
        return bigBytes;
    }

    private void reset() {
        this.buffer = null;
        this.pos = 0;
        this.readPos = 0;
        this.currentLinePos = 0;
        this.modulus = 0;
        this.eof = false;
    }
}
