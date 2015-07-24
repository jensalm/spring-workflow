package com.captechconsulting.workflow;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps a flow of tasks. It has a starting point and will process
 * all tasks depending on the tasks return type.
 * @see TaskAdapter
 */
public class FlowAdapter implements Workflow {

    private static final transient Logger LOG = LoggerFactory.getLogger(FlowAdapter.class);

    private static final String START_TASK = "_start_";

    private String name;

    private String description;

    private Map<String, TaskAdapter> tasks = new HashMap<>();

    /**
     * Creates a new FlowAdapter
     * @param name
     */
    public FlowAdapter(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Starts the processing of the flow by getting the task marked with
     * \@Start and executing it.
     * @param args
     * @return if the the task succeed or not
     * @throws WorkflowException
     */
    public boolean execute(Object... args) throws WorkflowException {
        return processTask(START_TASK, args);
    }

    /**
     * Recursively executes all tasks associated with this FlowAdapter.
     * @param taskName
     * @param args
     * @return true or false
     * @throws WorkflowException
     */
    private boolean processTask(String taskName, Object... args) throws WorkflowException {
        if (StringUtils.isNotBlank(taskName)) {
            if (LOG.isDebugEnabled()) {
                if (START_TASK.equals(taskName)) {
                    LOG.debug("Looking up starting point");
                } else {
                    LOG.debug("Looking up task '" + taskName + "'");
                }
            }
            TaskAdapter adapter = tasks.get(taskName);
            if (adapter != null) {
                if (adapter.isStart()) {
                    adapter.validateArguments(args);
                }
                LOG.debug("Processing task '{}'", adapter.getName());
                boolean result = adapter.process(args);
                if (result) {
                    LOG.debug("Task returned true, processing @Yes task");
                    return processTask(adapter.getYes(), args);
                } else {
                    LOG.debug("Task returned false, processing @No task");
                    return processTask(adapter.getNo(), args);
                }
            }
            String message = String.format("Trying to execute task '%s' but the task does not exist", taskName);
            throw new WorkflowException(getName(), taskName, message);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("No task, this flow is ending");
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns a non-modifiable map of all the tasks in the flow.
     * @return tasks in this flow
     */
    public Map<String, TaskAdapter> getTasks() {
        Map<String, TaskAdapter> uniqueTasks = new HashMap<>(tasks);
        uniqueTasks.remove(START_TASK);
        return Collections.unmodifiableMap(uniqueTasks);
    }

    /**
     * Adds a task to the flow
     * @param taskAdapter
     */
    public void add(TaskAdapter taskAdapter) {
        tasks.put(taskAdapter.getName(), taskAdapter);
        if (taskAdapter.isStart()) {
            setStartTask(taskAdapter);
        }
    }

    /**
     * Removes the task named from the flow
     * @param taskName
     */
    public void remove(String taskName) {
        remove(tasks.get(taskName));
    }

    /**
     * Removes a task from the flow. If it is the start task, the
     * start will be set to null.
     * @param taskAdapter
     */
    public void remove(TaskAdapter taskAdapter) {
        tasks.remove(taskAdapter.getName());
        if (taskAdapter.isStart()) {
            tasks.remove(START_TASK);
        }
    }

    /**
     * Sets the start task for this flow. Can only be 1 start.
     * @param taskAdapter
     */
    public void setStartTask(TaskAdapter taskAdapter) {
        if (tasks.containsKey(START_TASK)) {
            String format = "Only one @Task can be annotated with @Start. Task '%s' of flow '%s' is the second one found.";
            throw new IllegalArgumentException(String.format(format, taskAdapter.getName(), getName()));
        }
        tasks.put(START_TASK, taskAdapter);
    }

    /**
     * Indicates if the start task is set.
     * @return true if start task is set
     */
    public boolean hasStartTask() {
        return tasks.containsKey(START_TASK);
    }

    /**
     * Returns the start task if it is set
     * @return task or null
     */
    public TaskAdapter getStartTask() {
        return tasks.get(START_TASK);
    }

    /**
     * Returns the named task if it exists
     * @param taskName
     * @return task or null
     */
    public TaskAdapter getTask(String taskName) {
        return tasks.get(taskName);
    }
}
