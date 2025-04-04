package com.openfeint.internal.vendor.org.codehaus.jackson.util;

import java.util.Arrays;

/* loaded from: classes.dex */
public final class CharTypes {
    static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    static final int[] sHexValues;
    static final int[] sInputCodes;
    static final int[] sInputCodesComment;
    static final int[] sInputCodesJsNames;
    static final int[] sInputCodesUtf8;
    static final int[] sInputCodesUtf8JsNames;
    static final int[] sOutputEscapes;

    static {
        int code;
        int[] table = new int[256];
        for (int i = 0; i < 32; i++) {
            table[i] = -1;
        }
        table[34] = 1;
        table[92] = 1;
        sInputCodes = table;
        int[] table2 = new int[sInputCodes.length];
        System.arraycopy(sInputCodes, 0, table2, 0, sInputCodes.length);
        for (int c = 128; c < 256; c++) {
            if ((c & 224) == 192) {
                code = 2;
            } else if ((c & 240) == 224) {
                code = 3;
            } else if ((c & 248) == 240) {
                code = 4;
            } else {
                code = -1;
            }
            table2[c] = code;
        }
        sInputCodesUtf8 = table2;
        int[] table3 = new int[256];
        Arrays.fill(table3, -1);
        for (int i2 = 33; i2 < 256; i2++) {
            if (Character.isJavaIdentifierPart((char) i2)) {
                table3[i2] = 0;
            }
        }
        sInputCodesJsNames = table3;
        int[] table4 = new int[256];
        System.arraycopy(sInputCodesJsNames, 0, table4, 0, sInputCodesJsNames.length);
        Arrays.fill(table4, 128, 128, 0);
        sInputCodesUtf8JsNames = table4;
        sInputCodesComment = new int[256];
        System.arraycopy(sInputCodesUtf8, 128, sInputCodesComment, 128, 128);
        Arrays.fill(sInputCodesComment, 0, 32, -1);
        sInputCodesComment[9] = 0;
        sInputCodesComment[10] = 10;
        sInputCodesComment[13] = 13;
        sInputCodesComment[42] = 42;
        int[] table5 = new int[256];
        for (int i3 = 0; i3 < 32; i3++) {
            table5[i3] = -(i3 + 1);
        }
        table5[34] = 34;
        table5[92] = 92;
        table5[8] = 98;
        table5[9] = 116;
        table5[12] = 102;
        table5[10] = 110;
        table5[13] = 114;
        sOutputEscapes = table5;
        sHexValues = new int[128];
        Arrays.fill(sHexValues, -1);
        for (int i4 = 0; i4 < 10; i4++) {
            sHexValues[i4 + 48] = i4;
        }
        for (int i5 = 0; i5 < 6; i5++) {
            sHexValues[i5 + 97] = i5 + 10;
            sHexValues[i5 + 65] = i5 + 10;
        }
    }

    public static final int[] getInputCodeLatin1() {
        return sInputCodes;
    }

    public static final int[] getInputCodeUtf8() {
        return sInputCodesUtf8;
    }

    public static final int[] getInputCodeLatin1JsNames() {
        return sInputCodesJsNames;
    }

    public static final int[] getInputCodeUtf8JsNames() {
        return sInputCodesUtf8JsNames;
    }

    public static final int[] getInputCodeComment() {
        return sInputCodesComment;
    }

    public static final int[] getOutputEscapes() {
        return sOutputEscapes;
    }

    public static int charToHex(int ch) {
        if (ch > 127) {
            return -1;
        }
        return sHexValues[ch];
    }

    public static void appendQuoted(StringBuilder sb, String content) {
        int[] escCodes = sOutputEscapes;
        int escLen = escCodes.length;
        int len = content.length();
        for (int i = 0; i < len; i++) {
            char c = content.charAt(i);
            if (c >= escLen || escCodes[c] == 0) {
                sb.append(c);
            } else {
                sb.append('\\');
                int escCode = escCodes[c];
                if (escCode < 0) {
                    sb.append('u');
                    sb.append('0');
                    sb.append('0');
                    int value = -(escCode + 1);
                    sb.append(HEX_CHARS[value >> 4]);
                    sb.append(HEX_CHARS[value & 15]);
                } else {
                    sb.append((char) escCode);
                }
            }
        }
    }
}
