package com.captechconsulting.workflow.stereotypes;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Indicates a task. The task will be a method that is called by the flow.
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Task {

    /**
     * The name of the task, defaults to the method name if empty
     * @return
     */
    String value() default "";

    /**
     * An optional description of the task
     * @return
     */
    String description() default "";
}
