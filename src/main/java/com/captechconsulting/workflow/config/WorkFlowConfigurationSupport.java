package com.captechconsulting.workflow.config;

import com.captechconsulting.workflow.FlowExecutor;
import org.springframework.context.annotation.Bean;

public class WorkFlowConfigurationSupport {

    @Bean
    public FlowExecutorBeanPostProcessor createFlowExecutorBeanPostProcessor() {
        return new FlowExecutorBeanPostProcessor();
    }

    @Bean
    public FlowExecutor createFlow() {
        return new FlowExecutor();
    }
}
