package com.captechconsulting.workflow.config;

import com.captechconsulting.workflow.FlowAdapter;
import com.captechconsulting.workflow.TaskAdapter;
import com.captechconsulting.workflow.stereotypes.Flow;
import com.captechconsulting.workflow.stereotypes.Task;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorkflowBeanFactoryPostProcessor<T> implements BeanFactoryPostProcessor {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Flow.class));
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("");

        HashMap<String, FlowAdapter> flowAdapters = Maps.newHashMap();
        for (BeanDefinition bd : beanDefinitions) {
            createFlowAdapter(flowAdapters, (AnnotatedBeanDefinition) bd);
        }
        for (FlowAdapter flowAdapter : flowAdapters.values()) {
            sanityCheck(flowAdapter);
            beanFactory.registerSingleton(flowAdapter.getName(), flowAdapter);
        }
    }

    /**
     * Creates one or many adapters from the annotated bean.
     *
     * @param flowAdapters
     * @param bd
     */
    private void createFlowAdapter(Map<String, FlowAdapter> flowAdapters, AnnotatedBeanDefinition bd) {

        MultiValueMap<String, Object> attributes = bd.getMetadata().getAllAnnotationAttributes(Flow.class.getName());

        Class<T> type;
        try {
            type = (Class<T>) ClassUtils.forName(bd.getBeanClassName(), beanFactory.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new BeanCreationException("Unable to get class", e);
        }

        T bean = beanFactory.createBean(type);

        String[] names = getFirstOrDefault(attributes, "name", new String[]{bean.getClass().getSimpleName()});
        String description = getFirstOrDefault(attributes, "description", "");

        for (String name : names) {
            createFlowAdapter(flowAdapters, bean, name, description);
        }

    }

    private void createFlowAdapter(Map<String, FlowAdapter> flowAdapters, T bean, String name, String description) {
        FlowAdapter flowAdapter = flowAdapters.get(name);
        if (flowAdapter == null) {
            flowAdapter = new FlowAdapter(name, description);
            flowAdapters.put(flowAdapter.getName(), flowAdapter);
        } else {
            if (flowAdapter.getDescription() == null || "".equals(flowAdapter.getDescription())) {
                try {
                    Field field = ReflectionUtils.findField(FlowAdapter.class, "description");
                    ReflectionUtils.makeAccessible(field);
                    field.set(flowAdapter, description);
                } catch (IllegalAccessException e) {
                    throw new BeanInitializationException("Unable to update 'description'", e);
                }
            }
        }
        scanTasks(bean, flowAdapter);
    }

    private String getFirstOrDefault(MultiValueMap<String, Object> attributes, String attribute, String defaultValue) {
        Object value = attributes.getFirst(attribute);
        if (value != null && !"".equals(value)) {
            return (String) value;
        }
        return defaultValue;
    }

    private String[] getFirstOrDefault(MultiValueMap<String, Object> attributes, String attribute, String[] defaultValue) {
        Object value = attributes.getFirst(attribute);
        if (value != null && !Arrays.equals((String[]) value, new String[]{})) {
            return (String[]) value;
        }
        return defaultValue;
    }

    /**
     * Basic sanity check that the flow doesn't have tasks named that
     * don't exist.
     *
     * @param flowAdapter
     */
    protected void sanityCheck(FlowAdapter flowAdapter) {
        if (!flowAdapter.hasStartTask()) {
            throw new BeanDefinitionValidationException("Flow '" + flowAdapter.getName() + "' does not have a @Start");
        }
        Map<String, TaskAdapter> tasks = flowAdapter.getTasks();
        for (TaskAdapter taskAdapter : tasks.values()) {
            sanityCheck(flowAdapter, taskAdapter);
        }
    }

    /**
     * Basic sanity check of task.
     *
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
     *
     * @param flowAdapter
     * @param taskName
     * @param key
     * @param value
     */
    protected void assertNextTaskExists(FlowAdapter flowAdapter, String taskName, String key, String value) {
        if (StringUtils.isNotBlank(value) && !flowAdapter.getTasks().containsKey(value)) {
            String format = "Task '%s' in flow '%s' has a %s annotation with task name '%s' which doesn't exist";
            throw new BeanDefinitionValidationException(String.format(format, taskName, flowAdapter.getName(), key, value));
        }
    }

    /**
     * Scans a bean annotated with @Flow for methods annotated
     * with @Task and creates TaskAdapters for each one.
     *
     * @param bean
     * @param flowAdapter
     */
    protected void scanTasks(final Object bean, final FlowAdapter flowAdapter) {
        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) {
                Task task = AnnotationUtils.findAnnotation(method, Task.class);
                if (task != null) {
                    flowAdapter.add(new TaskAdapter(task, flowAdapter, bean, method));
                }
            }
        });
    }
}
