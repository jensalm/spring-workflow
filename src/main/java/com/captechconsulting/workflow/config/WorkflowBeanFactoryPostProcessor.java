package com.captechconsulting.workflow.config;

import com.captechconsulting.workflow.FlowAdapter;
import com.captechconsulting.workflow.FlowExecutor;
import com.captechconsulting.workflow.TaskAdapter;
import com.captechconsulting.workflow.WorkflowExecutor;
import com.captechconsulting.workflow.stereotypes.Flow;
import com.captechconsulting.workflow.stereotypes.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;

public class WorkflowBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;

        FlowExecutor flowExecutor = beanFactory.getBean(FlowExecutor.class);
        if (flowExecutor.isEmpty()) {
            Map<String, Object> flowAnnotatedBeans = beanFactory.getBeansWithAnnotation(Flow.class);
            for (Object flowAnnotatedBean : flowAnnotatedBeans.values()) {
                createFlowAdapters(flowExecutor, flowAnnotatedBean);
            }
            for (FlowAdapter adapter : flowExecutor.values()) {
                sanityCheck(adapter);
            }
        }
    }

    /**
     * Creates one or many adapters from the annotated bean.
     * @param bean
     */
    private void createFlowAdapters(FlowExecutor flowExecutor, Object bean) {
        Flow flow = AnnotationUtils.findAnnotation(bean.getClass(), Flow.class);
        if (flow != null) {
            if (flow.name().length == 0) {
                createFlowAdapter(flowExecutor, bean, bean.getClass().getSimpleName(), "", flow.types());
            } else {
                for (String flowName : flow.name()) {
                    createFlowAdapter(flowExecutor, bean, flowName, flow.description(), flow.types());
                }
            }
        }
    }

    /**
     * Creates a single flow adapter, scans and adds all tasks in the bean.
     *
     * @param flowName
     * @param description
     * @param types
     */
    private void createFlowAdapter(FlowExecutor flowExecutor, Object bean, String flowName, String description, Class... types) {
        FlowAdapter adapter;
        if (flowExecutor.containsKey(flowName)) {
            adapter = flowExecutor.get(flowName);
        } else {
            adapter = new FlowAdapter(flowName, description);
            flowExecutor.put(flowName, adapter);
            WorkflowExecutor we = new WorkflowExecutor();
            we.setFlowExecutor(flowExecutor);
            we.setName(flowName);
            beanFactory.registerSingleton(flowName, we);
        }
        scanTasks(bean, adapter, types);
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
