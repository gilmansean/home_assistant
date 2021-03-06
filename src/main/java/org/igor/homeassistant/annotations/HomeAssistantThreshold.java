package org.igor.homeassistant.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to tell the threshold monitor what field to watch.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HomeAssistantThreshold {
    /**
     * Field name that the threshold monitor should check.
     *
     * @return
     */
    String thresholdField();

    /**
     * The primary identifier(s) that are used to look up the threshold settings. If multiple identifiers are used they
     * will be concatenated to a single entry in the threshold monitor record.
     *
     * @return
     */
    String[] primaryIdentifiers();
}
