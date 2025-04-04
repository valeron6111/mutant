package com.alawar.mutant.util;

/* loaded from: classes.dex */
public class Base64 {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final byte[] ALPHABET;
    private static final byte[] DECODABET;
    public static final boolean DECODE = false;
    public static final boolean ENCODE = true;
    private static final byte EQUALS_SIGN = 61;
    private static final byte EQUALS_SIGN_ENC = -1;
    private static final byte NEW_LINE = 10;
    private static final byte[] WEBSAFE_ALPHABET;
    private static final byte[] WEBSAFE_DECODABET;
    private static final byte WHITE_SPACE_ENC = -5;

    static {
        $assertionsDisabled = !Base64.class.desiredAssertionStatus();
        ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        WEBSAFE_ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
        DECODABET = new byte[]{-9, -9, -9, -9, -9, -9, -9, -9, -9, WHITE_SPACE_ENC, WHITE_SPACE_ENC, -9, -9, WHITE_SPACE_ENC, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, WHITE_SPACE_ENC, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, EQUALS_SIGN, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, NEW_LINE, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9};
        WEBSAFE_DECODABET = new byte[]{-9, -9, -9, -9, -9, -9, -9, -9, -9, WHITE_SPACE_ENC, WHITE_SPACE_ENC, -9, -9, WHITE_SPACE_ENC, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, WHITE_SPACE_ENC, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 54, 55, 56, 57, 58, 59, 60, EQUALS_SIGN, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, NEW_LINE, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9};
    }

    private Base64() {
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0028, code lost:
    
        return r8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static byte[] encode3to4(byte[] r5, int r6, int r7, byte[] r8, int r9, byte[] r10) {
        /*
            r4 = 61
            r1 = 0
            if (r7 <= 0) goto L29
            r2 = r5[r6]
            int r2 = r2 << 24
            int r2 = r2 >>> 8
            r3 = r2
        Lc:
            r2 = 1
            if (r7 <= r2) goto L2b
            int r2 = r6 + 1
            r2 = r5[r2]
            int r2 = r2 << 24
            int r2 = r2 >>> 16
        L17:
            r2 = r2 | r3
            r3 = 2
            if (r7 <= r3) goto L23
            int r1 = r6 + 2
            r1 = r5[r1]
            int r1 = r1 << 24
            int r1 = r1 >>> 24
        L23:
            r0 = r2 | r1
            switch(r7) {
                case 1: goto L6f;
                case 2: goto L50;
                case 3: goto L2d;
                default: goto L28;
            }
        L28:
            return r8
        L29:
            r3 = r1
            goto Lc
        L2b:
            r2 = r1
            goto L17
        L2d:
            int r1 = r0 >>> 18
            r1 = r10[r1]
            r8[r9] = r1
            int r1 = r9 + 1
            int r2 = r0 >>> 12
            r2 = r2 & 63
            r2 = r10[r2]
            r8[r1] = r2
            int r1 = r9 + 2
            int r2 = r0 >>> 6
            r2 = r2 & 63
            r2 = r10[r2]
            r8[r1] = r2
            int r1 = r9 + 3
            r2 = r0 & 63
            r2 = r10[r2]
            r8[r1] = r2
            goto L28
        L50:
            int r1 = r0 >>> 18
            r1 = r10[r1]
            r8[r9] = r1
            int r1 = r9 + 1
            int r2 = r0 >>> 12
            r2 = r2 & 63
            r2 = r10[r2]
            r8[r1] = r2
            int r1 = r9 + 2
            int r2 = r0 >>> 6
            r2 = r2 & 63
            r2 = r10[r2]
            r8[r1] = r2
            int r1 = r9 + 3
            r8[r1] = r4
            goto L28
        L6f:
            int r1 = r0 >>> 18
            r1 = r10[r1]
            r8[r9] = r1
            int r1 = r9 + 1
            int r2 = r0 >>> 12
            r2 = r2 & 63
            r2 = r10[r2]
            r8[r1] = r2
            int r1 = r9 + 2
            r8[r1] = r4
            int r1 = r9 + 3
            r8[r1] = r4
            goto L28
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alawar.mutant.util.Base64.encode3to4(byte[], int, int, byte[], int, byte[]):byte[]");
    }

    public static String encode(byte[] source) {
        return encode(source, 0, source.length, ALPHABET, true);
    }

    public static String encodeWebSafe(byte[] source, boolean doPadding) {
        return encode(source, 0, source.length, WEBSAFE_ALPHABET, doPadding);
    }

    public static String encode(byte[] source, int off, int len, byte[] alphabet, boolean doPadding) {
        byte[] outBuff = encode(source, off, len, alphabet, Integer.MAX_VALUE);
        int outLen = outBuff.length;
        while (!doPadding && outLen > 0 && outBuff[outLen - 1] == 61) {
            outLen--;
        }
        return new String(outBuff, 0, outLen);
    }

    public static byte[] encode(byte[] source, int off, int len, byte[] alphabet, int maxLineLength) {
        int lenDiv3 = (len + 2) / 3;
        int len43 = lenDiv3 * 4;
        byte[] outBuff = new byte[(len43 / maxLineLength) + len43];
        int d = 0;
        int e = 0;
        int len2 = len - 2;
        int lineLength = 0;
        while (d < len2) {
            int inBuff = ((source[d + off] << 24) >>> 8) | ((source[(d + 1) + off] << 24) >>> 16) | ((source[(d + 2) + off] << 24) >>> 24);
            outBuff[e] = alphabet[inBuff >>> 18];
            outBuff[e + 1] = alphabet[(inBuff >>> 12) & 63];
            outBuff[e + 2] = alphabet[(inBuff >>> 6) & 63];
            outBuff[e + 3] = alphabet[inBuff & 63];
            lineLength += 4;
            if (lineLength == maxLineLength) {
                outBuff[e + 4] = NEW_LINE;
                e++;
                lineLength = 0;
            }
            d += 3;
            e += 4;
        }
        if (d < len) {
            encode3to4(source, d + off, len - d, outBuff, e, alphabet);
            if (lineLength + 4 == maxLineLength) {
                outBuff[e + 4] = NEW_LINE;
                e++;
            }
            e += 4;
        }
        if ($assertionsDisabled || e == outBuff.length) {
            return outBuff;
        }
        throw new AssertionError();
    }

    private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, byte[] decodabet) {
        if (source[srcOffset + 2] == 61) {
            destination[destOffset] = (byte) ((((decodabet[source[srcOffset]] << 24) >>> 6) | ((decodabet[source[srcOffset + 1]] << 24) >>> 12)) >>> 16);
            return 1;
        }
        if (source[srcOffset + 3] == 61) {
            int outBuff = ((decodabet[source[srcOffset]] << 24) >>> 6) | ((decodabet[source[srcOffset + 1]] << 24) >>> 12) | ((decodabet[source[srcOffset + 2]] << 24) >>> 18);
            destination[destOffset] = (byte) (outBuff >>> 16);
            destination[destOffset + 1] = (byte) (outBuff >>> 8);
            return 2;
        }
        int outBuff2 = ((decodabet[source[srcOffset]] << 24) >>> 6) | ((decodabet[source[srcOffset + 1]] << 24) >>> 12) | ((decodabet[source[srcOffset + 2]] << 24) >>> 18) | ((decodabet[source[srcOffset + 3]] << 24) >>> 24);
        destination[destOffset] = (byte) (outBuff2 >> 16);
        destination[destOffset + 1] = (byte) (outBuff2 >> 8);
        destination[destOffset + 2] = (byte) outBuff2;
        return 3;
    }

    public static byte[] decode(String s) throws Base64DecoderException {
        byte[] bytes = s.getBytes();
        return decode(bytes, 0, bytes.length);
    }

    public static byte[] decodeWebSafe(String s) throws Base64DecoderException {
        byte[] bytes = s.getBytes();
        return decodeWebSafe(bytes, 0, bytes.length);
    }

    public static byte[] decode(byte[] source) throws Base64DecoderException {
        return decode(source, 0, source.length);
    }

    public static byte[] decodeWebSafe(byte[] source) throws Base64DecoderException {
        return decodeWebSafe(source, 0, source.length);
    }

    public static byte[] decode(byte[] source, int off, int len) throws Base64DecoderException {
        return decode(source, off, len, DECODABET);
    }

    public static byte[] decodeWebSafe(byte[] source, int off, int len) throws Base64DecoderException {
        return decode(source, off, len, WEBSAFE_DECODABET);
    }

    public static byte[] decode(byte[] source, int off, int len, byte[] decodabet) throws Base64DecoderException {
        int b4Posn;
        int len34 = (len * 3) / 4;
        byte[] outBuff = new byte[len34 + 2];
        int outBuffPosn = 0;
        byte[] b4 = new byte[4];
        int b4Posn2 = 0;
        int i = 0;
        while (true) {
            b4Posn = b4Posn2;
            if (i >= len) {
                break;
            }
            byte sbiCrop = (byte) (source[i + off] & Byte.MAX_VALUE);
            byte sbiDecode = decodabet[sbiCrop];
            if (sbiDecode >= -5) {
                if (sbiDecode < -1) {
                    b4Posn2 = b4Posn;
                } else if (sbiCrop == 61) {
                    int bytesLeft = len - i;
                    byte lastByte = (byte) (source[(len - 1) + off] & Byte.MAX_VALUE);
                    if (b4Posn == 0 || b4Posn == 1) {
                        throw new Base64DecoderException("invalid padding byte '=' at byte offset " + i);
                    }
                    if ((b4Posn == 3 && bytesLeft > 2) || (b4Posn == 4 && bytesLeft > 1)) {
                        throw new Base64DecoderException("padding byte '=' falsely signals end of encoded value at offset " + i);
                    }
                    if (lastByte != 61 && lastByte != 10) {
                        throw new Base64DecoderException("encoded value has invalid trailing byte");
                    }
                } else {
                    b4Posn2 = b4Posn + 1;
                    b4[b4Posn] = sbiCrop;
                    if (b4Posn2 == 4) {
                        outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, decodabet);
                        b4Posn2 = 0;
                    }
                }
                i++;
            } else {
                throw new Base64DecoderException("Bad Base64 input character at " + i + ": " + ((int) source[i + off]) + "(decimal)");
            }
        }
        if (b4Posn != 0) {
            if (b4Posn == 1) {
                throw new Base64DecoderException("single trailing character at offset " + (len - 1));
            }
            int i2 = b4Posn + 1;
            b4[b4Posn] = EQUALS_SIGN;
            outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, decodabet);
        }
        byte[] out = new byte[outBuffPosn];
        System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
        return out;
    }
}
