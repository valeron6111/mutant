package com.openfeint.internal.vendor.org.codehaus.jackson.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JacksonAnnotation
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes.dex */
public @interface JsonCreator {
}
