package com.captechconsulting.workflow.config;

import com.captechconsulting.workflow.stereotypes.*;
import com.captechconsulting.workflow.engine.FlowAdapter;
import com.captechconsulting.workflow.engine.TaskAdapter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class FlowAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware, Ordered {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "FlowAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
        Flow flow = AnnotationUtils.findAnnotation(bean.getClass(), Flow.class);
        if (flow != null) {
            String name = StringUtils.isNotBlank(flow.value()) ? flow.value() : bean.getClass().getSimpleName();
            FlowAdapter adapter;
            try {
                adapter = beanFactory.getBean(name, FlowAdapter.class);
            } catch (NoSuchBeanDefinitionException e) {
                adapter = new FlowAdapter(name);
                beanFactory.registerSingleton(name, adapter);
            }
            scanTasks(bean, adapter, flow.types());
        }
        return bean;
    }

    protected void scanTasks(final Object bean, final FlowAdapter flowAdapter, final Class... types) {
        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) {
                Task task = AnnotationUtils.findAnnotation(method, Task.class);
                if (task != null) {
                    Yes yes = AnnotationUtils.findAnnotation(method, Yes.class);
                    No no = AnnotationUtils.findAnnotation(method, No.class);
                    Start start = AnnotationUtils.findAnnotation(method, Start.class);
                    TaskAdapter taskAdapter = new TaskAdapter(task, yes, no, start, bean, method.getName(), types);
                    flowAdapter.add(taskAdapter);
                }
            }
        });
    }

}
