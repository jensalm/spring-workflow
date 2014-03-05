package com.captechconsulting.workflow.config;

import com.captechconsulting.workflow.FlowAdapter;
import com.captechconsulting.workflow.FlowExecutor;
import com.captechconsulting.workflow.TaskAdapter;
import com.captechconsulting.workflow.stereotypes.Flow;
import com.captechconsulting.workflow.stereotypes.Task;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Performs the scan for @Flow and @Task.
 */
public class FlowExecutorBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "FlowExecutorBeanPostProcessor requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    /**
     * Not implemented.
     *
     * @param bean
     * @param name
     * @return the bean untouched
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        return bean;
    }

    /**
     * If the bean being initialized is a FlowExecutor all beans annotated
     * with @Flow will be retrieved from the application context. All these
     * will in turn be scanned for @Task annotations.
     * All Flow beans will generate a FlowAdapter and all Task beans will
     * generated a TaskAdapter. A brief sanity check will be performed to
     * make sure a task doesn't specify another task that doesn't exist.
     *
     * @param bean
     * @param name
     * @return the initialized bean
     * @see FlowExecutor
     * @see FlowAdapter
     * @see TaskAdapter
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        if (bean.getClass().isAssignableFrom(FlowExecutor.class)) {
            FlowExecutor flowExecutor = (FlowExecutor) bean;
            if (flowExecutor.isEmpty()) {
                Map<String, Object> flowAnnotatedBeans = beanFactory.getBeansWithAnnotation(Flow.class);
                Map<String, FlowAdapter> flowAdapters = Maps.newHashMap();
                for (Object flowAnnotatedBean : flowAnnotatedBeans.values()) {
                    flowAdapters.putAll(createFlowAdapters(flowAnnotatedBean));
                }
                for (FlowAdapter adapter : flowAdapters.values()) {
                    sanityCheck(adapter);
                }
                flowExecutor.putAll(flowAdapters);
            }
        }
        return bean;
    }

    /**
     * Creates one or many adapters from the annotated bean.
     * @param bean
     * @return flow adapters
     */
    private Map<String, FlowAdapter> createFlowAdapters(Object bean) {
        Map<String, FlowAdapter> flowAdapters = Maps.newHashMap();
        Flow flow = AnnotationUtils.findAnnotation(bean.getClass(), Flow.class);
        if (flow != null) {
            if (flow.name().length == 0) {
                FlowAdapter adapter = createFlowAdapter(bean, bean.getClass().getSimpleName(), "", flow.types());
                flowAdapters.put(bean.getClass().getSimpleName(), adapter);
            } else {
                for (String flowName : flow.name()) {
                    FlowAdapter adapter = createFlowAdapter(bean, flowName, flow.description(), flow.types());
                    flowAdapters.put(flowName, adapter);
                }
            }
        }
        return flowAdapters;
    }

    /**
     * Creates a single flow adapter, scans and adds all tasks in the bean.
     *
     * @param flowName
     * @param description
     * @param types
     * @return a flow adapter with tasks
     */
    private FlowAdapter createFlowAdapter(Object bean, String flowName, String description, Class... types) {
        FlowAdapter adapter;
        try {
            adapter = beanFactory.getBean(flowName, FlowAdapter.class);
        } catch (NoSuchBeanDefinitionException | BeanNotOfRequiredTypeException e) {
            adapter = new FlowAdapter(flowName, description);
            beanFactory.registerSingleton(flowName, adapter);
        }
        scanTasks(bean, adapter, types);
        return adapter;
    }

    /**
     * Basic sanity check that the flow doesn't have tasks named that
     * don't exist.
     * @param flowAdapter
     */
    protected void sanityCheck(FlowAdapter flowAdapter) {
        if (!flowAdapter.hasStartTask()) {
            throw new BeanDefinitionValidationException("Flow '" + flowAdapter.getName() + "' does not have a @Start");
        }
        for (TaskAdapter taskAdapter : flowAdapter.getTasks().values()) {
            sanityCheck(flowAdapter, taskAdapter);
        }
    }

    /**
     * Basic sanity check of task.
     * @param flowAdapter
     * @param taskAdapter
     */
    protected void sanityCheck(FlowAdapter flowAdapter, TaskAdapter taskAdapter) {
        assertNextTaskExists(flowAdapter, taskAdapter.getName(), "yes", taskAdapter.getYes());
        assertNextTaskExists(flowAdapter, taskAdapter.getName(), "no", taskAdapter.getNo());
    }

    /**
     * Checks that a single tasks options (yes or no) both exists
     * if they are named.
     * @param flowAdapter
     * @param taskName
     * @param key
     * @param value
     */
    protected void assertNextTaskExists(FlowAdapter flowAdapter, String taskName, String key, String value) {
        if (StringUtils.isNotBlank(value) && !flowAdapter.getTasks().containsKey(value)) {
            throw new BeanDefinitionValidationException("Task '" + taskName +
                    "' has a " + key + " annotation with task name '" + value + "' which doesn't exist");
        }
    }

    /**
     * Scans a bean annotated with @Flow for methods annotated
     * with @Task and creates TaskAdapters for each one.
     * @param bean
     * @param flowAdapter
     * @param types
     */
    protected void scanTasks(final Object bean, final FlowAdapter flowAdapter, final Class... types) {
        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) {
                Task task = AnnotationUtils.findAnnotation(method, Task.class);
                if (task != null) {
                    flowAdapter.add(new TaskAdapter(task, flowAdapter, bean, method, types));
                }
            }
        });
    }

}
