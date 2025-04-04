package com.openfeint.internal.vendor.com.google.api.client.escape;

/* loaded from: classes.dex */
public abstract class UnicodeEscaper extends Escaper {
    private static final int DEST_PAD = 32;

    @Override // com.openfeint.internal.vendor.com.google.api.client.escape.Escaper
    public abstract String escape(String str);

    protected abstract char[] escape(int i);

    protected abstract int nextEscapeIndex(CharSequence charSequence, int i, int i2);

    protected final String escapeSlow(String s, int index) {
        int end = s.length();
        char[] dest = Platform.charBufferFromThreadLocal();
        int destIndex = 0;
        int unescapedChunkStart = 0;
        while (index < end) {
            int cp = codePointAt(s, index, end);
            if (cp < 0) {
                throw new IllegalArgumentException("Trailing high surrogate at end of input");
            }
            char[] escaped = escape(cp);
            int nextIndex = index + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
            if (escaped != null) {
                int charsSkipped = index - unescapedChunkStart;
                int sizeNeeded = destIndex + charsSkipped + escaped.length;
                if (dest.length < sizeNeeded) {
                    int destLength = (end - index) + sizeNeeded + DEST_PAD;
                    dest = growBuffer(dest, destIndex, destLength);
                }
                if (charsSkipped > 0) {
                    s.getChars(unescapedChunkStart, index, dest, destIndex);
                    destIndex += charsSkipped;
                }
                if (escaped.length > 0) {
                    System.arraycopy(escaped, 0, dest, destIndex, escaped.length);
                    destIndex += escaped.length;
                }
                unescapedChunkStart = nextIndex;
            }
            index = nextEscapeIndex(s, nextIndex, end);
        }
        int charsSkipped2 = end - unescapedChunkStart;
        if (charsSkipped2 > 0) {
            int endIndex = destIndex + charsSkipped2;
            if (dest.length < endIndex) {
                dest = growBuffer(dest, destIndex, endIndex);
            }
            s.getChars(unescapedChunkStart, end, dest, destIndex);
            destIndex = endIndex;
        }
        return new String(dest, 0, destIndex);
    }

    protected static int codePointAt(CharSequence seq, int index, int end) {
        if (index < end) {
            int index2 = index + 1;
            char c1 = seq.charAt(index);
            if (c1 >= 55296 && c1 <= 57343) {
                if (c1 <= 56319) {
                    if (index2 == end) {
                        return -c1;
                    }
                    char c2 = seq.charAt(index2);
                    if (Character.isLowSurrogate(c2)) {
                        return Character.toCodePoint(c1, c2);
                    }
                    throw new IllegalArgumentException("Expected low surrogate but got char '" + c2 + "' with value " + ((int) c2) + " at index " + index2);
                }
                throw new IllegalArgumentException("Unexpected low surrogate character '" + c1 + "' with value " + ((int) c1) + " at index " + (index2 - 1));
            }
            return c1;
        }
        throw new IndexOutOfBoundsException("Index exceeds specified range");
    }

    private static char[] growBuffer(char[] dest, int index, int size) {
        char[] copy = new char[size];
        if (index > 0) {
            System.arraycopy(dest, 0, copy, 0, index);
        }
        return copy;
    }
}
