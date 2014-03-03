package com.captechconsulting.workflow.engine;

import com.captechconsulting.workflow.stereotypes.*;
import org.apache.commons.lang.StringUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class TaskAdapter {

    private Object bean;
    private String name;
    private String flow;
    private String yes;
    private String no;
    private boolean start;

    private MethodHandle methodHandle = null;

    public TaskAdapter(Task task, Yes yes, No no, Start start, Object bean, String methodName, Class... types) {
        this.bean = bean;
        this.flow = StringUtils.isNotBlank(task.flow()) ? task.flow() : this.bean.getClass().getSimpleName();
        this.yes = yes != null ? yes.value() : null;
        this.no = no != null ? no.value() : null;
        this.start = start != null;
        this.name = StringUtils.isNotBlank(task.value()) ? task.value() : methodName;
        try {
            methodHandle = getMethodHandle(methodName, types);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("", e);
        }
    }

    private MethodHandle getMethodHandle(String methodName, Class... types) throws IllegalAccessException, NoSuchMethodException {
        MethodType mt = MethodType.methodType(Boolean.class, types);
        try {
            return MethodHandles.publicLookup().findVirtual(this.bean.getClass(), methodName, mt);
        } catch (NoSuchMethodException e) {
            return MethodHandles.publicLookup().findVirtual(this.bean.getClass(), methodName, mt.unwrap());
        }
    }

    public boolean process(Object... args) throws Throwable {
        //return (boolean) methodHandle.asVarargsCollector(Object[].class).invoke(bean, args);
        // Fugly hack. Need to find a way to do this correctly
        switch(args.length) {
            case 0:
                return (boolean) methodHandle.invoke(bean);
            case 1:
                return (boolean) methodHandle.invoke(bean, args[0]);
            case 2:
                return (boolean) methodHandle.invoke(bean, args[0], args[1]);
            case 3:
                return (boolean) methodHandle.invoke(bean, args[0], args[1], args[2]);
            case 4:
                return (boolean) methodHandle.invoke(bean, args[0], args[1], args[2], args[3]);
            case 5:
                return (boolean) methodHandle.invoke(bean, args[0], args[1], args[2], args[3], args[4]);
            default:
                throw new RuntimeException("To many args: "+args.length);
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

    public String getYes(){
        return yes;
    }

    public String getNo(){
        return no;
    }

    public Object getBean() {
        return bean;
    }
}
