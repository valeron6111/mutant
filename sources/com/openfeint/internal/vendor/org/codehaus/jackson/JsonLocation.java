package com.openfeint.internal.vendor.org.codehaus.jackson;

import com.openfeint.internal.vendor.org.codehaus.jackson.annotate.JsonCreator;
import com.openfeint.internal.vendor.org.codehaus.jackson.annotate.JsonProperty;
import java.io.Serializable;

/* loaded from: classes.dex */
public class JsonLocation implements Serializable {

    /* renamed from: NA */
    public static final JsonLocation f290NA = new JsonLocation("N/A", -1, -1, -1, -1);
    private static final long serialVersionUID = 1;
    final int _columnNr;
    final int _lineNr;
    final Object _sourceRef;
    final long _totalBytes;
    final long _totalChars;

    public JsonLocation(Object srcRef, long totalChars, int lineNr, int colNr) {
        this(srcRef, -1L, totalChars, lineNr, colNr);
    }

    @JsonCreator
    public JsonLocation(@JsonProperty("sourceRef") Object sourceRef, @JsonProperty("byteOffset") long totalBytes, @JsonProperty("charOffset") long totalChars, @JsonProperty("lineNr") int lineNr, @JsonProperty("columnNr") int columnNr) {
        this._sourceRef = sourceRef;
        this._totalBytes = totalBytes;
        this._totalChars = totalChars;
        this._lineNr = lineNr;
        this._columnNr = columnNr;
    }

    public Object getSourceRef() {
        return this._sourceRef;
    }

    public int getLineNr() {
        return this._lineNr;
    }

    public int getColumnNr() {
        return this._columnNr;
    }

    public long getCharOffset() {
        return this._totalChars;
    }

    public long getByteOffset() {
        return this._totalBytes;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(80);
        sb.append("[Source: ");
        if (this._sourceRef == null) {
            sb.append("UNKNOWN");
        } else {
            sb.append(this._sourceRef.toString());
        }
        sb.append("; line: ");
        sb.append(this._lineNr);
        sb.append(", column: ");
        sb.append(this._columnNr);
        sb.append(']');
        return sb.toString();
    }

    public int hashCode() {
        int hash = this._sourceRef == null ? 1 : this._sourceRef.hashCode();
        return (((hash ^ this._lineNr) + this._columnNr) ^ ((int) this._totalChars)) + ((int) this._totalBytes);
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || !(other instanceof JsonLocation)) {
            return false;
        }
        JsonLocation otherLoc = (JsonLocation) other;
        if (this._sourceRef == null) {
            if (otherLoc._sourceRef != null) {
                return false;
            }
        } else if (!this._sourceRef.equals(otherLoc._sourceRef)) {
            return false;
        }
        return this._lineNr == otherLoc._lineNr && this._columnNr == otherLoc._columnNr && this._totalChars == otherLoc._totalChars && getByteOffset() == otherLoc.getByteOffset();
    }
}
