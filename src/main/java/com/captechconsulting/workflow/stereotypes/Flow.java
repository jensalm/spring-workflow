package com.captechconsulting.workflow.stereotypes;

import java.lang.annotation.*;

/**
 * Names and connects tasks. Flows can be split over several
 * classes as long as the name is the same.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Flow {

    /**
     * Name of the flow, will default to the simple class name if not specified
     * @return an optional name
     */
    String[] name() default { };

    /**
     * Description of the flow
     * @return an optional description
     */
    String description() default "";
}
