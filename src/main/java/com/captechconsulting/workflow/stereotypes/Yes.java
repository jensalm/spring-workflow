package com.captechconsulting.workflow.stereotypes;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Yes {

    String value();

}
