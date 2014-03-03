package com.captechconsulting.workflow.config;

import com.captechconsulting.workflow.FlowExecutor;
import com.captechconsulting.workflow.engine.FlowAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.Map;

public class WorkFlowConfigurationSupport implements BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "WorkFlowConfigurationSupport requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Bean
    public FlowAnnotationBeanPostProcessor createFlowAnnotationBeanPostProcessor() {
        return new FlowAnnotationBeanPostProcessor();
    }

    @Bean
    public FlowExecutor createFlow() {
        Map<String, FlowAdapter> flows = beanFactory.getBeansOfType(FlowAdapter.class);
        return new FlowExecutor(flows);
    }
}
