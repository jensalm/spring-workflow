package com.captechconsulting.workflow;

public interface Workflow<T> {

    boolean execute(Object... args) throws WorkflowException;

    String getName();
}
