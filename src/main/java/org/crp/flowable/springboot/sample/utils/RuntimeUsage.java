package org.crp.flowable.springboot.sample.utils;

import java.lang.annotation.*;

/**
 * Flag for methods used in the expressions.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface RuntimeUsage {
    /**
     * The description of how the method is used in expressions.
     *
     * @return the usage description
     */
    String value();
}
