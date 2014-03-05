package com.captechconsulting.workflow;

import com.captechconsulting.workflow.stereotypes.No;
import com.captechconsulting.workflow.stereotypes.Start;
import com.captechconsulting.workflow.stereotypes.Task;
import com.captechconsulting.workflow.stereotypes.Yes;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * Wraps a task to enable executing from a flow. Any method which has
 *
 * @Task and return type boolean/Boolean will be wrapped.
 */
public class TaskAdapter {

    private static final transient Logger LOG = LoggerFactory.getLogger(TaskAdapter.class);
    private Object bean;
    private String name;
    private String flow;
    private String yes;
    private String no;
    private boolean start;
    private MethodHandle methodHandle = null;
    private String description;

    /**
     * Creates a new TaskAdapter
     *
     * @param task
     * @param flowAdapter
     * @param bean
     * @param method
     * @param types
     */
    public TaskAdapter(Task task, FlowAdapter flowAdapter, Object bean, Method method, Class... types) {

        Yes yes = AnnotationUtils.findAnnotation(method, Yes.class);
        No no = AnnotationUtils.findAnnotation(method, No.class);
        Start start = AnnotationUtils.findAnnotation(method, Start.class);

        this.bean = bean;
        this.flow = flowAdapter.getName();
        this.yes = yes != null ? yes.value() : null;
        this.no = no != null ? no.value() : null;
        this.start = start != null;
        this.name = StringUtils.isNotBlank(task.value()) ? task.value() : method.getName();
        this.description = task.description();
        this.methodHandle = getMethodHandle(method.getName(), types);
    }

    /**
     * Tries to create a method handle from methodName and specified types.
     * Takes into account both Boolean and boolean return type.
     *
     * @param methodName
     * @param types
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private MethodHandle getMethodHandle(String methodName, Class... types) {
        MethodType mt = MethodType.methodType(Boolean.class, types);
        try {
            try {
                return MethodHandles.publicLookup().findVirtual(this.bean.getClass(), methodName, mt);
            } catch (NoSuchMethodException e) {
                return MethodHandles.publicLookup().findVirtual(this.bean.getClass(), methodName, mt.unwrap());
            }
        } catch (NoSuchMethodException e) {
            throw new BeanInitializationException("Unable to find a method matching the name '" + methodName + "' for task '" +
                    this.flow + ":" + this.name + "'. Argument types of the flow must match the method's arguments.", e);
        } catch (IllegalAccessException e) {
            throw new BeanInitializationException("Unable to access the method matching the name '" + methodName +
                    "' for task '" + this.flow + ":" + this.name + "'. Must be a public method.", e);
        }
    }

    /**
     * Execute the wrapped task
     *
     * @param args
     * @return true or false depending on the task called.
     * @throws WorkflowException
     */
    public boolean process(Object... args) throws WorkflowException {
        LOG.debug("Executing '" + getFlow() + ':' + getName() + "'");
        try {
            return (boolean) methodHandle.asSpreader(args.getClass(), args.length).invoke(bean, args);
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new WorkflowException(getFlow(), getName(), t);
        }
    }

    public String getName() {
        return name;
    }

    public String getFlow() {
        return flow;
    }

    public boolean isStart() {
        return start;
    }

    public String getYes() {
        return yes;
    }

    public String getNo() {
        return no;
    }

    public String getBeanName() {
        return bean.getClass().getName();
    }

    public String getMethod() {
        return methodHandle.toString();
    }

    public String getDescription() {
        return description;
    }
}
