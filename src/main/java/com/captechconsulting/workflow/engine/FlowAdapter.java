package com.captechconsulting.workflow.engine;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;

import java.util.Map;

public class FlowAdapter {

    private static final transient Logger LOG = LoggerFactory.getLogger(FlowAdapter.class);

    private static final String START_TASK = "_start_";

    private String name;

    private Map<String, TaskAdapter> tasks = Maps.newHashMap();

    public FlowAdapter(String name) {
        this.name = name;
    }

    public boolean start(Object... args) throws Throwable {
        return processTask(START_TASK, args);
    }

    private boolean processTask(String taskName, Object... args) throws Throwable {
        if (StringUtils.isNotBlank(taskName)) {
            if (LOG.isDebugEnabled()) {
                if (START_TASK.equals(taskName)) {
                    LOG.debug("Looking up starting point");
                } else {
                    LOG.debug("Looking up task [" + taskName + "]");
                }
            }
            TaskAdapter adapter = tasks.get(taskName);
            if (adapter != null) {
                LOG.debug("Processing task [" + adapter.getName() + "]");
                boolean result = adapter.process(args);
                if (result) {
                    LOG.debug("Task returned true, processing yes task");
                    return processTask(adapter.getYes(), args);
                } else {
                    LOG.debug("Task returned false, processing no task");
                    return processTask(adapter.getNo(), args);
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("No task, this process is ending");
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void add(TaskAdapter taskAdapter) {
        tasks.put(taskAdapter.getName(), taskAdapter);
        if (taskAdapter.isStart()) {
            setStartPoint(taskAdapter);
        }
    }

    public void setStartPoint(TaskAdapter taskAdapter) {
        if (tasks.containsKey(START_TASK)) {
            throw new BeanInstantiationException(taskAdapter.getBean().getClass(), "Only one @Task can be annotated with @Start");
        }
        tasks.put(START_TASK, taskAdapter);
    }
}