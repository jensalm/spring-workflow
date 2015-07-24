package com.captechconsulting.workflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkFlowConfigurationSupport {

    @Bean
    public WorkflowBeanFactoryPostProcessor createWorkflowBeanFactoryPostProcessor() {
        return new WorkflowBeanFactoryPostProcessor();
    }

}
