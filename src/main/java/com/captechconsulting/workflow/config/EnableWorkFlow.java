package com.captechconsulting.workflow.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(WorkFlowConfigurationSupport.class)
public @interface EnableWorkFlow {
}
