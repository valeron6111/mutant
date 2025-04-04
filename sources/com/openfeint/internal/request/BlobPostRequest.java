package com.openfeint.internal.request;

import com.alawar.mutant.jni.MutantMessages;
import com.openfeint.internal.request.multipart.FilePart;
import com.openfeint.internal.request.multipart.MultipartHttpEntity;
import com.openfeint.internal.request.multipart.Part;
import com.openfeint.internal.request.multipart.PartSource;
import com.openfeint.internal.request.multipart.StringPart;
import com.openfeint.internal.resource.BlobUploadParameters;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

/* loaded from: classes.dex */
public class BlobPostRequest extends BaseRequest {
    String mContentType;
    private IRawRequestDelegate mDelegate;
    BlobUploadParameters mParams;
    protected PartSource mPartSource;

    @Override // com.openfeint.internal.request.BaseRequest
    public boolean signed() {
        return false;
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public String method() {
        return "POST";
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public String url() {
        return this.mParams.action;
    }

    public void sign() {
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public String path() {
        return MutantMessages.sEmpty;
    }

    @Override // com.openfeint.internal.request.BaseRequest
    protected HttpUriRequest generateRequest() {
        if (this.mPartSource == null) {
            return null;
        }
        HttpPost retval = new HttpPost(url());
        Part[] parts = new Part[6];
        int idx = 0 + 1;
        parts[0] = new StringPart("AWSAccessKeyId", this.mParams.AWSAccessKeyId);
        int idx2 = idx + 1;
        parts[idx] = new StringPart("acl", this.mParams.acl);
        int idx3 = idx2 + 1;
        parts[idx2] = new StringPart("key", this.mParams.key);
        int idx4 = idx3 + 1;
        parts[idx3] = new StringPart("policy", this.mParams.policy);
        int idx5 = idx4 + 1;
        parts[idx4] = new StringPart("signature", this.mParams.signature);
        int i = idx5 + 1;
        parts[idx5] = new FilePart("file", this.mPartSource, this.mContentType, (String) null);
        HttpEntity e = new MultipartHttpEntity(parts);
        retval.setEntity(e);
        addParams(retval);
        return retval;
    }

    public BlobPostRequest(BlobUploadParameters params, PartSource partSource, String contentType) {
        super(null);
        this.mParams = params;
        this.mPartSource = partSource;
        this.mContentType = contentType;
    }

    @Override // com.openfeint.internal.request.BaseRequest
    public void onResponse(int responseCode, byte[] body) {
        if (this.mDelegate != null) {
            this.mDelegate.onResponse(responseCode, new String(body));
        }
    }

    public void setDelegate(IRawRequestDelegate mDelegate) {
        this.mDelegate = mDelegate;
    }
}
