package com.openfeint.internal.resource;

/* loaded from: classes.dex */
public class ParentalControl extends Resource {
    public boolean enabled;

    public static ResourceClass getResourceClass() {
        ResourceClass klass = new ResourceClass(ParentalControl.class, "parental_control") { // from class: com.openfeint.internal.resource.ParentalControl.1
            @Override // com.openfeint.internal.resource.ResourceClass
            public Resource factory() {
                return new ParentalControl();
            }
        };
        klass.mProperties.put("enabled", new BooleanResourceProperty() { // from class: com.openfeint.internal.resource.ParentalControl.2
            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public boolean get(Resource obj) {
                return ((ParentalControl) obj).enabled;
            }

            @Override // com.openfeint.internal.resource.BooleanResourceProperty
            public void set(Resource obj, boolean val) {
                ((ParentalControl) obj).enabled = val;
            }
        });
        return klass;
    }
}
