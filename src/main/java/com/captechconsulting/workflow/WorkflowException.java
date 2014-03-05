package com.captechconsulting.workflow;

/**
 * Workflow exception for when anything in the execution of a flow or task goes wrong.
 */
public class WorkflowException extends Exception {

    private final String flowName;
    private final String taskName;

    /**
     * Creates a new WorkFlowException
     * @param flowName
     * @param taskName
     */
    public WorkflowException(String flowName, String taskName) {
        this.flowName = flowName;
        this.taskName = taskName;
    }

    /**
     * Creates a new WorkFlowException
     * @param flowName
     * @param taskName
     * @param message
     */
    public WorkflowException(String flowName, String taskName, String message) {
        super(message);
        this.flowName = flowName;
        this.taskName = taskName;
    }

    /**
     * Creates a new WorkFlowException
     * @param flowName
     * @param taskName
     * @param message
     * @param cause
     */
    public WorkflowException(String flowName, String taskName, String message, Throwable cause) {
        super(message, cause);
        this.flowName = flowName;
        this.taskName = taskName;
    }

    /**
     * Creates a new WorkFlowException
     * @param flowName
     * @param taskName
     * @param cause
     */
    public WorkflowException(String flowName, String taskName, Throwable cause) {
        super(cause);
        this.flowName = flowName;
        this.taskName = taskName;
    }

    /**
     * The name of the flow that was running when the exception was thrown
     * @return flow name
     */
    public String getFlowName() {
        return flowName;
    }

    /**
     * The name of the task that was executing when the exception was thrown
     * @return task name
     */
    public String getTaskName() {
        return taskName;
    }
}
