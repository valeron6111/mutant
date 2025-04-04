package com.openfeint.internal.resource;

import com.openfeint.api.resource.User;
import java.util.List;

/* loaded from: classes.dex */
public class Device extends Resource {
    public ParentalControl parental_control;
    public List<User> users;

    public static ResourceClass getResourceClass() {
        ResourceClass klass = new ResourceClass(Device.class, "device") { // from class: com.openfeint.internal.resource.Device.1
            @Override // com.openfeint.internal.resource.ResourceClass
            public Resource factory() {
                return new Device();
            }
        };
        klass.mProperties.put("users", new ArrayResourceProperty(User.class) { // from class: com.openfeint.internal.resource.Device.2
            @Override // com.openfeint.internal.resource.ArrayResourceProperty
            public List<? extends Resource> get(Resource obj) {
                return ((Device) obj).users;
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.openfeint.internal.resource.ArrayResourceProperty
            public void set(Resource obj, List<?> list) {
                ((Device) obj).users = list;
            }
        });
        klass.mProperties.put("parental_control", new NestedResourceProperty(ParentalControl.class) { // from class: com.openfeint.internal.resource.Device.3
            @Override // com.openfeint.internal.resource.NestedResourceProperty
            public Resource get(Resource obj) {
                return ((Device) obj).parental_control;
            }

            @Override // com.openfeint.internal.resource.NestedResourceProperty
            public void set(Resource obj, Resource val) {
                ((Device) obj).parental_control = (ParentalControl) val;
            }
        });
        return klass;
    }
}
