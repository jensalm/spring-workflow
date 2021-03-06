package com.captechconsulting.workflow.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DelegatingWorkFlowConfiguration.class)
public @interface EnableWorkFlow {
}
