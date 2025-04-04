package com.openfeint.internal.resource;

/* loaded from: classes.dex */
public class BlobUploadParameters extends Resource {
    public String AWSAccessKeyId;
    public String acl;
    public String action;
    public String key;
    public String policy;
    public String signature;

    public static ResourceClass getResourceClass() {
        ResourceClass klass = new ResourceClass(BlobUploadParameters.class, "blob_upload_parameters") { // from class: com.openfeint.internal.resource.BlobUploadParameters.1
            @Override // com.openfeint.internal.resource.ResourceClass
            public Resource factory() {
                return new BlobUploadParameters();
            }
        };
        klass.mProperties.put("action", new StringResourceProperty() { // from class: com.openfeint.internal.resource.BlobUploadParameters.2
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((BlobUploadParameters) obj).action;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((BlobUploadParameters) obj).action = val;
            }
        });
        klass.mProperties.put("key", new StringResourceProperty() { // from class: com.openfeint.internal.resource.BlobUploadParameters.3
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((BlobUploadParameters) obj).key;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((BlobUploadParameters) obj).key = val;
            }
        });
        klass.mProperties.put("AWSAccessKeyId", new StringResourceProperty() { // from class: com.openfeint.internal.resource.BlobUploadParameters.4
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((BlobUploadParameters) obj).AWSAccessKeyId;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((BlobUploadParameters) obj).AWSAccessKeyId = val;
            }
        });
        klass.mProperties.put("acl", new StringResourceProperty() { // from class: com.openfeint.internal.resource.BlobUploadParameters.5
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((BlobUploadParameters) obj).acl;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((BlobUploadParameters) obj).acl = val;
            }
        });
        klass.mProperties.put("policy", new StringResourceProperty() { // from class: com.openfeint.internal.resource.BlobUploadParameters.6
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((BlobUploadParameters) obj).policy;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((BlobUploadParameters) obj).policy = val;
            }
        });
        klass.mProperties.put("signature", new StringResourceProperty() { // from class: com.openfeint.internal.resource.BlobUploadParameters.7
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((BlobUploadParameters) obj).signature;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((BlobUploadParameters) obj).signature = val;
            }
        });
        return klass;
    }
}
