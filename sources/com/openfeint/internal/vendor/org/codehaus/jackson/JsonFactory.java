package com.openfeint.internal.vendor.org.codehaus.jackson;

import com.openfeint.internal.vendor.org.codehaus.jackson.JsonGenerator;
import com.openfeint.internal.vendor.org.codehaus.jackson.JsonParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.impl.ByteSourceBootstrapper;
import com.openfeint.internal.vendor.org.codehaus.jackson.impl.ReaderBasedParser;
import com.openfeint.internal.vendor.org.codehaus.jackson.impl.WriterBasedGenerator;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.IOContext;
import com.openfeint.internal.vendor.org.codehaus.jackson.p005io.UTF8Writer;
import com.openfeint.internal.vendor.org.codehaus.jackson.sym.BytesToNameCanonicalizer;
import com.openfeint.internal.vendor.org.codehaus.jackson.sym.CharsToNameCanonicalizer;
import com.openfeint.internal.vendor.org.codehaus.jackson.util.BufferRecycler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.net.URL;

/* loaded from: classes.dex */
public class JsonFactory {
    protected int _generatorFeatures;
    protected ObjectCodec _objectCodec;
    protected int _parserFeatures;
    protected BytesToNameCanonicalizer _rootByteSymbols;
    protected CharsToNameCanonicalizer _rootCharSymbols;
    static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
    static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
    static final ThreadLocal<SoftReference<BufferRecycler>> _recyclerRef = new ThreadLocal<>();

    public JsonFactory() {
        this(null);
    }

    public JsonFactory(ObjectCodec oc) {
        this._rootCharSymbols = CharsToNameCanonicalizer.createRoot();
        this._rootByteSymbols = BytesToNameCanonicalizer.createRoot();
        this._parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
        this._generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
        this._objectCodec = oc;
    }

    public final JsonFactory configure(JsonParser.Feature f, boolean state) {
        if (state) {
            enable(f);
        } else {
            disable(f);
        }
        return this;
    }

    public JsonFactory enable(JsonParser.Feature f) {
        this._parserFeatures |= f.getMask();
        return this;
    }

    public JsonFactory disable(JsonParser.Feature f) {
        this._parserFeatures &= f.getMask() ^ (-1);
        return this;
    }

    public final boolean isEnabled(JsonParser.Feature f) {
        return (this._parserFeatures & f.getMask()) != 0;
    }

    public final void enableParserFeature(JsonParser.Feature f) {
        enable(f);
    }

    public final void disableParserFeature(JsonParser.Feature f) {
        disable(f);
    }

    public final void setParserFeature(JsonParser.Feature f, boolean state) {
        configure(f, state);
    }

    public final boolean isParserFeatureEnabled(JsonParser.Feature f) {
        return (this._parserFeatures & f.getMask()) != 0;
    }

    public final JsonFactory configure(JsonGenerator.Feature f, boolean state) {
        if (state) {
            enable(f);
        } else {
            disable(f);
        }
        return this;
    }

    public JsonFactory enable(JsonGenerator.Feature f) {
        this._generatorFeatures |= f.getMask();
        return this;
    }

    public JsonFactory disable(JsonGenerator.Feature f) {
        this._generatorFeatures &= f.getMask() ^ (-1);
        return this;
    }

    public final boolean isEnabled(JsonGenerator.Feature f) {
        return (this._generatorFeatures & f.getMask()) != 0;
    }

    public final void enableGeneratorFeature(JsonGenerator.Feature f) {
        enable(f);
    }

    public final void disableGeneratorFeature(JsonGenerator.Feature f) {
        disable(f);
    }

    public final void setGeneratorFeature(JsonGenerator.Feature f, boolean state) {
        configure(f, state);
    }

    public final boolean isGeneratorFeatureEnabled(JsonGenerator.Feature f) {
        return isEnabled(f);
    }

    public JsonFactory setCodec(ObjectCodec oc) {
        this._objectCodec = oc;
        return this;
    }

    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    public JsonParser createJsonParser(File f) throws IOException, JsonParseException {
        return _createJsonParser(new FileInputStream(f), _createContext(f, true));
    }

    public JsonParser createJsonParser(URL url) throws IOException, JsonParseException {
        return _createJsonParser(_optimizedStreamFromURL(url), _createContext(url, true));
    }

    public JsonParser createJsonParser(InputStream in) throws IOException, JsonParseException {
        return _createJsonParser(in, _createContext(in, false));
    }

    public JsonParser createJsonParser(Reader r) throws IOException, JsonParseException {
        return _createJsonParser(r, _createContext(r, false));
    }

    public JsonParser createJsonParser(byte[] data) throws IOException, JsonParseException {
        return _createJsonParser(data, 0, data.length, _createContext(data, true));
    }

    public JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
        return _createJsonParser(data, offset, len, _createContext(data, true));
    }

    public JsonParser createJsonParser(String content) throws IOException, JsonParseException {
        Reader r = new StringReader(content);
        return _createJsonParser(r, _createContext(r, true));
    }

    public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        IOContext ctxt = _createContext(out, false);
        ctxt.setEncoding(enc);
        return _createJsonGenerator(_createWriter(out, enc, ctxt), ctxt);
    }

    public JsonGenerator createJsonGenerator(Writer out) throws IOException {
        IOContext ctxt = _createContext(out, false);
        return _createJsonGenerator(out, ctxt);
    }

    public JsonGenerator createJsonGenerator(File f, JsonEncoding enc) throws IOException {
        OutputStream out = new FileOutputStream(f);
        IOContext ctxt = _createContext(out, true);
        ctxt.setEncoding(enc);
        return _createJsonGenerator(_createWriter(out, enc, ctxt), ctxt);
    }

    protected IOContext _createContext(Object srcRef, boolean resourceManaged) {
        return new IOContext(_getBufferRecycler(), srcRef, resourceManaged);
    }

    protected JsonParser _createJsonParser(InputStream in, IOContext ctxt) throws IOException, JsonParseException {
        return new ByteSourceBootstrapper(ctxt, in).constructParser(this._parserFeatures, this._objectCodec, this._rootByteSymbols, this._rootCharSymbols);
    }

    protected JsonParser _createJsonParser(Reader r, IOContext ctxt) throws IOException, JsonParseException {
        return new ReaderBasedParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols.makeChild(isEnabled(JsonParser.Feature.CANONICALIZE_FIELD_NAMES), isEnabled(JsonParser.Feature.INTERN_FIELD_NAMES)));
    }

    protected JsonParser _createJsonParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException, JsonParseException {
        return new ByteSourceBootstrapper(ctxt, data, offset, len).constructParser(this._parserFeatures, this._objectCodec, this._rootByteSymbols, this._rootCharSymbols);
    }

    protected JsonGenerator _createJsonGenerator(Writer out, IOContext ctxt) throws IOException {
        return new WriterBasedGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
    }

    public BufferRecycler _getBufferRecycler() {
        SoftReference<BufferRecycler> ref = _recyclerRef.get();
        BufferRecycler br = ref == null ? null : ref.get();
        if (br == null) {
            br = new BufferRecycler();
            if (ref == null) {
                _recyclerRef.set(new SoftReference<>(br));
            }
        }
        return br;
    }

    protected InputStream _optimizedStreamFromURL(URL url) throws IOException {
        String host;
        return ("file".equals(url.getProtocol()) && ((host = url.getHost()) == null || host.length() == 0)) ? new FileInputStream(url.getPath()) : url.openStream();
    }

    protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
        return enc == JsonEncoding.UTF8 ? new UTF8Writer(ctxt, out) : new OutputStreamWriter(out, enc.getJavaName());
    }
}
