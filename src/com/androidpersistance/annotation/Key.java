package com.androidpersistance.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Annotation to signify that a field is a primary key, or part of a composite key
 * @author brandon
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {

}
