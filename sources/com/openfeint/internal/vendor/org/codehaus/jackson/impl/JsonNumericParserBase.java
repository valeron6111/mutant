package com.openfeint.internal.vendor.org.codehaus.jackson.impl;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParseException;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonToken;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.IOContext;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.NumberInput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: classes.dex */
public abstract class JsonNumericParserBase extends JsonParserBase {
    protected static final char CHAR_NULL = 0;
    protected static final int INT_0 = 48;
    protected static final int INT_1 = 49;
    protected static final int INT_2 = 50;
    protected static final int INT_3 = 51;
    protected static final int INT_4 = 52;
    protected static final int INT_5 = 53;
    protected static final int INT_6 = 54;
    protected static final int INT_7 = 55;
    protected static final int INT_8 = 56;
    protected static final int INT_9 = 57;
    protected static final int INT_DECIMAL_POINT = 46;
    protected static final int INT_E = 69;
    protected static final int INT_MINUS = 45;
    protected static final int INT_PLUS = 43;
    protected static final int INT_e = 101;
    static final double MAX_INT_D = 2.147483647E9d;
    static final long MAX_INT_L = 2147483647L;
    static final double MAX_LONG_D = 9.223372036854776E18d;
    static final double MIN_INT_D = -2.147483648E9d;
    static final long MIN_INT_L = -2147483648L;
    static final double MIN_LONG_D = -9.223372036854776E18d;
    protected static final int NR_BIGDECIMAL = 16;
    protected static final int NR_BIGINT = 4;
    protected static final int NR_DOUBLE = 8;
    protected static final int NR_INT = 1;
    protected static final int NR_LONG = 2;
    protected static final int NR_UNKNOWN = 0;
    protected int _numTypesValid;
    protected BigDecimal _numberBigDecimal;
    protected BigInteger _numberBigInt;
    protected double _numberDouble;
    protected int _numberInt;
    protected long _numberLong;
    protected boolean _numberNegative;
    protected int mExpLength;
    protected int mFractLength;
    protected int mIntLength;
    static final BigDecimal BD_MIN_LONG = new BigDecimal(Long.MIN_VALUE);
    static final BigDecimal BD_MAX_LONG = new BigDecimal(Long.MAX_VALUE);
    static final BigDecimal BD_MIN_INT = new BigDecimal(Long.MIN_VALUE);
    static final BigDecimal BD_MAX_INT = new BigDecimal(Long.MAX_VALUE);

    protected abstract JsonToken parseNumberText(int i) throws IOException, JsonParseException;

    protected JsonNumericParserBase(IOContext ctxt, int features) {
        super(ctxt, features);
        this._numTypesValid = 0;
    }

    protected final JsonToken reset(boolean negative, int intLen, int fractLen, int expLen) {
        this._numberNegative = negative;
        this.mIntLength = intLen;
        this.mFractLength = fractLen;
        this.mExpLength = expLen;
        this._numTypesValid = 0;
        return (fractLen >= 1 || expLen >= 1) ? JsonToken.VALUE_NUMBER_FLOAT : JsonToken.VALUE_NUMBER_INT;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser
    public Number getNumberValue() throws IOException, JsonParseException {
        if (this._numTypesValid == 0) {
            parseNumericValue(0);
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if ((this._numTypesValid & 1) != 0) {
                return Integer.valueOf(this._numberInt);
            }
            if ((this._numTypesValid & 2) != 0) {
                return Long.valueOf(this._numberLong);
            }
            if ((this._numTypesValid & 4) != 0) {
                return this._numberBigInt;
            }
            return this._numberBigDecimal;
        }
        if ((this._numTypesValid & NR_BIGDECIMAL) != 0) {
            return this._numberBigDecimal;
        }
        if ((this._numTypesValid & 8) == 0) {
            _throwInternal();
        }
        return Double.valueOf(this._numberDouble);
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser
    public JsonParser.NumberType getNumberType() throws IOException, JsonParseException {
        if (this._numTypesValid == 0) {
            parseNumericValue(0);
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if ((this._numTypesValid & 1) != 0) {
                return JsonParser.NumberType.INT;
            }
            if ((this._numTypesValid & 2) != 0) {
                return JsonParser.NumberType.LONG;
            }
            return JsonParser.NumberType.BIG_INTEGER;
        }
        if ((this._numTypesValid & NR_BIGDECIMAL) != 0) {
            return JsonParser.NumberType.BIG_DECIMAL;
        }
        return JsonParser.NumberType.DOUBLE;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser
    public int getIntValue() throws IOException, JsonParseException {
        if ((this._numTypesValid & 1) == 0) {
            if (this._numTypesValid == 0) {
                parseNumericValue(1);
            }
            if ((this._numTypesValid & 1) == 0) {
                convertNumberToInt();
            }
        }
        return this._numberInt;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser
    public long getLongValue() throws IOException, JsonParseException {
        if ((this._numTypesValid & 2) == 0) {
            if (this._numTypesValid == 0) {
                parseNumericValue(2);
            }
            if ((this._numTypesValid & 2) == 0) {
                convertNumberToLong();
            }
        }
        return this._numberLong;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser
    public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
        if ((this._numTypesValid & 4) == 0) {
            if (this._numTypesValid == 0) {
                parseNumericValue(4);
            }
            if ((this._numTypesValid & 4) == 0) {
                convertNumberToBigInteger();
            }
        }
        return this._numberBigInt;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser
    public float getFloatValue() throws IOException, JsonParseException {
        double value = getDoubleValue();
        return (float) value;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser
    public double getDoubleValue() throws IOException, JsonParseException {
        if ((this._numTypesValid & 8) == 0) {
            if (this._numTypesValid == 0) {
                parseNumericValue(8);
            }
            if ((this._numTypesValid & 8) == 0) {
                convertNumberToDouble();
            }
        }
        return this._numberDouble;
    }

    @Override // com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser
    public BigDecimal getDecimalValue() throws IOException, JsonParseException {
        if ((this._numTypesValid & NR_BIGDECIMAL) == 0) {
            if (this._numTypesValid == 0) {
                parseNumericValue(NR_BIGDECIMAL);
            }
            if ((this._numTypesValid & NR_BIGDECIMAL) == 0) {
                convertNumberToBigDecimal();
            }
        }
        return this._numberBigDecimal;
    }

    protected final void parseNumericValue(int expType) throws JsonParseException {
        if (this._currToken == null || !this._currToken.isNumeric()) {
            _reportError("Current token (" + this._currToken + ") not numeric, can not use numeric value accessors");
        }
        try {
            if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
                char[] buf = this._textBuffer.getTextBuffer();
                int offset = this._textBuffer.getTextOffset();
                int len = this.mIntLength;
                if (this._numberNegative) {
                    offset++;
                }
                if (len <= 9) {
                    int i = NumberInput.parseInt(buf, offset, len);
                    if (this._numberNegative) {
                        i = -i;
                    }
                    this._numberInt = i;
                    this._numTypesValid = 1;
                    return;
                }
                if (len <= 18) {
                    long l = NumberInput.parseLong(buf, offset, len);
                    if (this._numberNegative) {
                        l = -l;
                    }
                    if (len == 10) {
                        if (this._numberNegative) {
                            if (l >= MIN_INT_L) {
                                this._numberInt = (int) l;
                                this._numTypesValid = 1;
                                return;
                            }
                        } else if (l <= MAX_INT_L) {
                            this._numberInt = (int) l;
                            this._numTypesValid = 1;
                            return;
                        }
                    }
                    this._numberLong = l;
                    this._numTypesValid = 2;
                    return;
                }
                String numStr = this._textBuffer.contentsAsString();
                if (NumberInput.inLongRange(buf, offset, len, this._numberNegative)) {
                    this._numberLong = Long.parseLong(numStr);
                    this._numTypesValid = 2;
                    return;
                } else {
                    this._numberBigInt = new BigInteger(numStr);
                    this._numTypesValid = 4;
                    return;
                }
            }
            if (expType == NR_BIGDECIMAL) {
                this._numberBigDecimal = this._textBuffer.contentsAsDecimal();
                this._numTypesValid = NR_BIGDECIMAL;
            } else {
                this._numberDouble = this._textBuffer.contentsAsDouble();
                this._numTypesValid = 8;
            }
        } catch (NumberFormatException nex) {
            _wrapError("Malformed numeric value '" + this._textBuffer.contentsAsString() + "'", nex);
        }
    }

    protected void convertNumberToInt() throws IOException, JsonParseException {
        if ((this._numTypesValid & 2) != 0) {
            int result = (int) this._numberLong;
            if (result != this._numberLong) {
                _reportError("Numeric value (" + getText() + ") out of range of int");
            }
            this._numberInt = result;
        } else if ((this._numTypesValid & 4) != 0) {
            this._numberInt = this._numberBigInt.intValue();
        } else if ((this._numTypesValid & 8) != 0) {
            if (this._numberDouble < MIN_INT_D || this._numberDouble > MAX_INT_D) {
                reportOverflowInt();
            }
            this._numberInt = (int) this._numberDouble;
        } else if ((this._numTypesValid & NR_BIGDECIMAL) != 0) {
            if (BD_MIN_INT.compareTo(this._numberBigDecimal) > 0 || BD_MAX_INT.compareTo(this._numberBigDecimal) < 0) {
                reportOverflowInt();
            }
            this._numberInt = this._numberBigDecimal.intValue();
        } else {
            _throwInternal();
        }
        this._numTypesValid |= 1;
    }

    protected void convertNumberToLong() throws IOException, JsonParseException {
        if ((this._numTypesValid & 1) != 0) {
            this._numberLong = this._numberInt;
        } else if ((this._numTypesValid & 4) != 0) {
            this._numberLong = this._numberBigInt.longValue();
        } else if ((this._numTypesValid & 8) != 0) {
            if (this._numberDouble < MIN_LONG_D || this._numberDouble > MAX_LONG_D) {
                reportOverflowLong();
            }
            this._numberLong = (long) this._numberDouble;
        } else if ((this._numTypesValid & NR_BIGDECIMAL) != 0) {
            if (BD_MIN_LONG.compareTo(this._numberBigDecimal) > 0 || BD_MAX_LONG.compareTo(this._numberBigDecimal) < 0) {
                reportOverflowLong();
            }
            this._numberLong = this._numberBigDecimal.longValue();
        } else {
            _throwInternal();
        }
        this._numTypesValid |= 2;
    }

    protected void convertNumberToBigInteger() throws IOException, JsonParseException {
        if ((this._numTypesValid & NR_BIGDECIMAL) != 0) {
            this._numberBigInt = this._numberBigDecimal.toBigInteger();
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberBigInt = BigInteger.valueOf(this._numberLong);
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberBigInt = BigInteger.valueOf(this._numberInt);
        } else if ((this._numTypesValid & 8) != 0) {
            this._numberBigInt = BigDecimal.valueOf(this._numberDouble).toBigInteger();
        } else {
            _throwInternal();
        }
        this._numTypesValid |= 4;
    }

    protected void convertNumberToDouble() throws IOException, JsonParseException {
        if ((this._numTypesValid & NR_BIGDECIMAL) != 0) {
            this._numberDouble = this._numberBigDecimal.doubleValue();
        } else if ((this._numTypesValid & 4) != 0) {
            this._numberDouble = this._numberBigInt.doubleValue();
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberDouble = this._numberLong;
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberDouble = this._numberInt;
        } else {
            _throwInternal();
        }
        this._numTypesValid |= 8;
    }

    protected void convertNumberToBigDecimal() throws IOException, JsonParseException {
        if ((this._numTypesValid & 8) != 0) {
            this._numberBigDecimal = new BigDecimal(getText());
        } else if ((this._numTypesValid & 4) != 0) {
            this._numberBigDecimal = new BigDecimal(this._numberBigInt);
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberBigDecimal = BigDecimal.valueOf(this._numberLong);
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberBigDecimal = BigDecimal.valueOf(this._numberInt);
        } else {
            _throwInternal();
        }
        this._numTypesValid |= NR_BIGDECIMAL;
    }

    protected void reportUnexpectedNumberChar(int ch, String comment) throws JsonParseException {
        String msg = "Unexpected character (" + _getCharDesc(ch) + ") in numeric value";
        if (comment != null) {
            msg = msg + ": " + comment;
        }
        _reportError(msg);
    }

    protected void reportInvalidNumber(String msg) throws JsonParseException {
        _reportError("Invalid numeric value: " + msg);
    }

    protected void reportOverflowInt() throws IOException, JsonParseException {
        _reportError("Numeric value (" + getText() + ") out of range of int (-2147483648 - 2147483647)");
    }

    protected void reportOverflowLong() throws IOException, JsonParseException {
        _reportError("Numeric value (" + getText() + ") out of range of long (-9223372036854775808 - 9223372036854775807)");
    }
}
