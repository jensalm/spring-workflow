package com.captechconsulting.workflow.config;

import com.captechconsulting.workflow.FlowExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkFlowConfigurationSupport {

    @Bean
    public WorkflowBeanFactoryPostProcessor createWorkflowBeanFactoryPostProcessor() {
        return new WorkflowBeanFactoryPostProcessor();
    }

    @Bean
    public FlowExecutor createFlowExecutor() {
        return new FlowExecutor();
    }
}
