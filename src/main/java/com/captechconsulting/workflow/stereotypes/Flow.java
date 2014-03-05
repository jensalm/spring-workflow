package com.captechconsulting.workflow.stereotypes;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Names and connects tasks. Flows can be split over several
 * classes as long as the name is the same.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Flow {

    /**
     * Name of the flow, will default to the simple class name if not specified
     * @return
     */
    String[] name() default { };

    /**
     * The types of arguments the tasks in this flow will have.
     * @return
     */
    Class[] types() default { };

    /**
     * Optional description of the flow
     * @return
     */
    String description() default "";
}
