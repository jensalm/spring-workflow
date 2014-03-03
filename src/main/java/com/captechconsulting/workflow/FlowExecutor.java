package com.captechconsulting.workflow;

import com.captechconsulting.workflow.engine.FlowAdapter;

import java.util.Map;

public class FlowExecutor {

    private Map<String, FlowAdapter> flows;

    public FlowExecutor(Map<String, FlowAdapter> flows) {
        this.flows = flows;
    }

    public boolean execute(String name, Object... types) throws Throwable {
        FlowAdapter flowAdapter = flows.get(name);
        if (flowAdapter == null ) {
            throw new Throwable("No flow named: "+name);
        }
        return flowAdapter.start(types);
    }
}
