package com.captechconsulting.workflow.stereotypes;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Flow {

    String value() default "";

    Class[] types() default {};

}
