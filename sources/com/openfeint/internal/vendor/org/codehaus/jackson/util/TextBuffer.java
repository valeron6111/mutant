package com.openfeint.internal.vendor.org.codehaus.jackson.util;

import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.internal.vendor.org.codehaus.jackson.util.BufferRecycler;
import java.math.BigDecimal;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class TextBuffer {
    static final int MAX_SEGMENT_LEN = 262144;
    static final char[] NO_CHARS = new char[0];
    private final BufferRecycler _allocator;
    private char[] _currentSegment;
    private int _currentSize;
    private boolean _hasSegments = false;
    private char[] _inputBuffer;
    private int _inputLen;
    private int _inputStart;
    private char[] _resultArray;
    private String _resultString;
    private int _segmentSize;
    private ArrayList<char[]> _segments;

    public TextBuffer(BufferRecycler allocator) {
        this._allocator = allocator;
    }

    public void releaseBuffers() {
        if (this._allocator != null && this._currentSegment != null) {
            resetWithEmpty();
            char[] buf = this._currentSegment;
            this._currentSegment = null;
            this._allocator.releaseCharBuffer(BufferRecycler.CharBufferType.TEXT_BUFFER, buf);
        }
    }

    public void resetWithEmpty() {
        this._inputBuffer = null;
        this._inputStart = -1;
        this._inputLen = 0;
        this._resultString = null;
        this._resultArray = null;
        if (this._hasSegments) {
            clearSegments();
        }
        this._currentSize = 0;
    }

    public void resetWithShared(char[] buf, int start, int len) {
        this._resultString = null;
        this._resultArray = null;
        this._inputBuffer = buf;
        this._inputStart = start;
        this._inputLen = len;
        if (this._hasSegments) {
            clearSegments();
        }
    }

    public void resetWithCopy(char[] buf, int start, int len) {
        this._inputBuffer = null;
        this._inputStart = -1;
        this._inputLen = 0;
        this._resultString = null;
        this._resultArray = null;
        if (this._hasSegments) {
            clearSegments();
        } else if (this._currentSegment == null) {
            this._currentSegment = findBuffer(len);
        }
        this._segmentSize = 0;
        this._currentSize = 0;
        append(buf, start, len);
    }

    private final char[] findBuffer(int needed) {
        return this._allocator.allocCharBuffer(BufferRecycler.CharBufferType.TEXT_BUFFER, needed);
    }

    private final void clearSegments() {
        this._hasSegments = false;
        this._segments.clear();
        this._segmentSize = 0;
        this._currentSize = 0;
    }

    public int size() {
        return this._inputStart >= 0 ? this._inputLen : this._segmentSize + this._currentSize;
    }

    public int getTextOffset() {
        if (this._inputStart >= 0) {
            return this._inputStart;
        }
        return 0;
    }

    public char[] getTextBuffer() {
        if (this._inputStart >= 0) {
            return this._inputBuffer;
        }
        if (!this._hasSegments) {
            return this._currentSegment;
        }
        return contentsAsArray();
    }

    public String contentsAsString() {
        if (this._resultString == null) {
            if (this._resultArray != null) {
                this._resultString = new String(this._resultArray);
            } else if (this._inputStart >= 0) {
                if (this._inputLen < 1) {
                    this._resultString = MutantMessages.sEmpty;
                    return MutantMessages.sEmpty;
                }
                this._resultString = new String(this._inputBuffer, this._inputStart, this._inputLen);
            } else {
                int segLen = this._segmentSize;
                int currLen = this._currentSize;
                if (segLen == 0) {
                    this._resultString = currLen == 0 ? MutantMessages.sEmpty : new String(this._currentSegment, 0, currLen);
                } else {
                    StringBuilder sb = new StringBuilder(segLen + currLen);
                    if (this._segments != null) {
                        int len = this._segments.size();
                        for (int i = 0; i < len; i++) {
                            char[] curr = this._segments.get(i);
                            sb.append(curr, 0, curr.length);
                        }
                    }
                    sb.append(this._currentSegment, 0, this._currentSize);
                    this._resultString = sb.toString();
                }
            }
        }
        return this._resultString;
    }

    public char[] contentsAsArray() {
        char[] result = this._resultArray;
        if (result == null) {
            char[] result2 = buildResultArray();
            this._resultArray = result2;
            return result2;
        }
        return result;
    }

    public BigDecimal contentsAsDecimal() throws NumberFormatException {
        if (this._resultArray != null) {
            return new BigDecimal(this._resultArray);
        }
        if (this._inputStart >= 0) {
            return new BigDecimal(this._inputBuffer, this._inputStart, this._inputLen);
        }
        if (this._segmentSize == 0) {
            return new BigDecimal(this._currentSegment, 0, this._currentSize);
        }
        return new BigDecimal(contentsAsArray());
    }

    public double contentsAsDouble() throws NumberFormatException {
        return Double.parseDouble(contentsAsString());
    }

    public void ensureNotShared() {
        if (this._inputStart >= 0) {
            unshare(16);
        }
    }

    public void append(char c) {
        if (this._inputStart >= 0) {
            unshare(16);
        }
        this._resultString = null;
        this._resultArray = null;
        char[] curr = this._currentSegment;
        if (this._currentSize >= curr.length) {
            expand(1);
            curr = this._currentSegment;
        }
        int i = this._currentSize;
        this._currentSize = i + 1;
        curr[i] = c;
    }

    public void append(char[] c, int start, int len) {
        if (this._inputStart >= 0) {
            unshare(len);
        }
        this._resultString = null;
        this._resultArray = null;
        char[] curr = this._currentSegment;
        int max = curr.length - this._currentSize;
        if (max >= len) {
            System.arraycopy(c, start, curr, this._currentSize, len);
            this._currentSize += len;
            return;
        }
        if (max > 0) {
            System.arraycopy(c, start, curr, this._currentSize, max);
            start += max;
            len -= max;
        }
        expand(len);
        System.arraycopy(c, start, this._currentSegment, 0, len);
        this._currentSize = len;
    }

    public void append(String str, int offset, int len) {
        if (this._inputStart >= 0) {
            unshare(len);
        }
        this._resultString = null;
        this._resultArray = null;
        char[] curr = this._currentSegment;
        int max = curr.length - this._currentSize;
        if (max >= len) {
            str.getChars(offset, offset + len, curr, this._currentSize);
            this._currentSize += len;
            return;
        }
        if (max > 0) {
            str.getChars(offset, offset + max, curr, this._currentSize);
            len -= max;
            offset += max;
        }
        expand(len);
        str.getChars(offset, offset + len, this._currentSegment, 0);
        this._currentSize = len;
    }

    public char[] getCurrentSegment() {
        if (this._inputStart >= 0) {
            unshare(1);
        } else {
            char[] curr = this._currentSegment;
            if (curr == null) {
                this._currentSegment = findBuffer(0);
            } else if (this._currentSize >= curr.length) {
                expand(1);
            }
        }
        return this._currentSegment;
    }

    public char[] emptyAndGetCurrentSegment() {
        resetWithEmpty();
        char[] curr = this._currentSegment;
        if (curr == null) {
            char[] curr2 = findBuffer(0);
            this._currentSegment = curr2;
            return curr2;
        }
        return curr;
    }

    public int getCurrentSegmentSize() {
        return this._currentSize;
    }

    public void setCurrentLength(int len) {
        this._currentSize = len;
    }

    public char[] finishCurrentSegment() {
        if (this._segments == null) {
            this._segments = new ArrayList<>();
        }
        this._hasSegments = true;
        this._segments.add(this._currentSegment);
        int oldLen = this._currentSegment.length;
        this._segmentSize += oldLen;
        int newLen = Math.min((oldLen >> 1) + oldLen, MAX_SEGMENT_LEN);
        char[] curr = _charArray(newLen);
        this._currentSize = 0;
        this._currentSegment = curr;
        return curr;
    }

    public char[] expandCurrentSegment() {
        char[] curr = this._currentSegment;
        int len = curr.length;
        int newLen = len == MAX_SEGMENT_LEN ? 262145 : Math.min(MAX_SEGMENT_LEN, (len >> 1) + len);
        this._currentSegment = _charArray(newLen);
        System.arraycopy(curr, 0, this._currentSegment, 0, len);
        return this._currentSegment;
    }

    public String toString() {
        return contentsAsString();
    }

    private void unshare(int needExtra) {
        int sharedLen = this._inputLen;
        this._inputLen = 0;
        char[] inputBuf = this._inputBuffer;
        this._inputBuffer = null;
        int start = this._inputStart;
        this._inputStart = -1;
        int needed = sharedLen + needExtra;
        if (this._currentSegment == null || needed > this._currentSegment.length) {
            this._currentSegment = findBuffer(needed);
        }
        if (sharedLen > 0) {
            System.arraycopy(inputBuf, start, this._currentSegment, 0, sharedLen);
        }
        this._segmentSize = 0;
        this._currentSize = sharedLen;
    }

    private void expand(int minNewSegmentSize) {
        if (this._segments == null) {
            this._segments = new ArrayList<>();
        }
        char[] curr = this._currentSegment;
        this._hasSegments = true;
        this._segments.add(curr);
        this._segmentSize += curr.length;
        int oldLen = curr.length;
        int sizeAddition = oldLen >> 1;
        if (sizeAddition < minNewSegmentSize) {
            sizeAddition = minNewSegmentSize;
        }
        char[] curr2 = _charArray(Math.min(MAX_SEGMENT_LEN, oldLen + sizeAddition));
        this._currentSize = 0;
        this._currentSegment = curr2;
    }

    private char[] buildResultArray() {
        if (this._resultString != null) {
            return this._resultString.toCharArray();
        }
        if (this._inputStart >= 0) {
            if (this._inputLen < 1) {
                return NO_CHARS;
            }
            char[] result = _charArray(this._inputLen);
            System.arraycopy(this._inputBuffer, this._inputStart, result, 0, this._inputLen);
            return result;
        }
        int size = size();
        if (size < 1) {
            return NO_CHARS;
        }
        int offset = 0;
        char[] result2 = _charArray(size);
        if (this._segments != null) {
            int len = this._segments.size();
            for (int i = 0; i < len; i++) {
                char[] curr = this._segments.get(i);
                int currLen = curr.length;
                System.arraycopy(curr, 0, result2, offset, currLen);
                offset += currLen;
            }
        }
        System.arraycopy(this._currentSegment, 0, result2, offset, this._currentSize);
        return result2;
    }

    private final char[] _charArray(int len) {
        return new char[len];
    }
}
