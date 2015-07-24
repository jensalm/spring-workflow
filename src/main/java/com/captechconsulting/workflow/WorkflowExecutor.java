package com.captechconsulting.workflow;

public class WorkflowExecutor {

    private String name;
    private FlowExecutor flowExecutor;

    public boolean execute(Object... args) throws WorkflowException {
        return flowExecutor.execute(name, args);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FlowExecutor getFlowExecutor() {
        return flowExecutor;
    }

    public void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor;
    }
}
