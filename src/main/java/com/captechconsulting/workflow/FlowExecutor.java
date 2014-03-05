package com.captechconsulting.workflow;

import com.captechconsulting.workflow.engine.FlowAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for all workflow interaction. This class should be a
 * singleton and it should be autowired in to all classes that need
 * to kick off workflows.
 */
public class FlowExecutor extends HashMap<String, FlowAdapter> {

    /**
     * Creates a new FlowExecutor
     */
    public FlowExecutor() {
        super();
    }

    /**
     * Executes the named flow by getting the FlowAdapter that
     * corresponds to the name and calling the start method.
     * @param name
     * @param types
     * @return if the last task return true or false
     * @throws WorkflowException
     * @see FlowAdapter
     * @see TaskAdapter
     */
    public boolean execute(String name, Object... types) throws WorkflowException {
        FlowAdapter flowAdapter = get(name);
        return flowAdapter.start(types);
    }

    @Override
    public FlowAdapter get(Object key) {
        FlowAdapter flowAdapter = super.get(key);
        if (flowAdapter == null) {
            throw new RuntimeException("No flow named: " + key.toString());
        }
        return flowAdapter;
    }
}
