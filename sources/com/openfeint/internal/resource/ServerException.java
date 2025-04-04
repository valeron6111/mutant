package com.openfeint.internal.resource;

/* loaded from: classes.dex */
public class ServerException extends Resource {
    public String exceptionClass;
    public String message;
    public boolean needsDeveloperAttention;

    public ServerException(String klass, String message) {
        this.exceptionClass = klass;
        this.message = message;
    }

    public ServerException() {
    }

    public static ResourceClass getResourceClass() {
        ResourceClass klass = new ResourceClass(ServerException.class, "exception") { // from class: com.openfeint.internal.resource.ServerException.1
            @Override // com.openfeint.internal.resource.ResourceClass
            public Resource factory() {
                return new ServerException();
            }
        };
        klass.mProperties.put("class", new StringResourceProperty() { // from class: com.openfeint.internal.resource.ServerException.2
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((ServerException) obj).exceptionClass;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((ServerException) obj).exceptionClass = val;
            }
        });
        klass.mProperties.put("message", new StringResourceProperty() { // from class: com.openfeint.internal.resource.ServerException.3
            @Override // com.openfeint.internal.resource.StringResourceProperty
            public String get(Resource obj) {
                return ((ServerException) obj).message;
            }

            @Override // com.openfeint.internal.resource.StringResourceProperty
            public void set(Resource obj, String val) {
                ((ServerException) obj).message = val;
            }
        });
        klass.mProperties.put("needs_developer_attention", new BooleanResourceProperty() { // from class: com.openfeint.internal.resource.ServerException.4
            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public boolean get(Resource obj) {
                return ((ServerException) obj).needsDeveloperAttention;
            }

            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public void set(Resource obj, boolean val) {
                ((ServerException) obj).needsDeveloperAttention = val;
            }
        });
        return klass;
    }
}
