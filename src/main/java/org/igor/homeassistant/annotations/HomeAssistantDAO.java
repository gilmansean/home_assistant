package org.igor.homeassistant.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HomeAssistantDAO {
    /**
     * Location of where the data is stored.  Could be a file or table name.
     */
    String location() default "";

    /**
     * Fields on the class to use as primary identifiers for finding unique entries.
     */
    String[] primaryIdentifiers() default {""};
}
