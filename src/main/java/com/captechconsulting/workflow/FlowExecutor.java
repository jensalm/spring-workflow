package com.captechconsulting.workflow;

import com.captechconsulting.workflow.engine.FlowAdapter;

import java.util.HashMap;
import java.util.Map;

public class FlowExecutor extends HashMap<String, FlowAdapter> {

    public FlowExecutor(Map<String, FlowAdapter> flows) {
        super(flows);
    }

    public boolean execute(String name, Object... types) throws Throwable {
        FlowAdapter flowAdapter = get(name);
        if (flowAdapter == null ) {
            throw new Throwable("No flow named: "+name);
        }
        return flowAdapter.start(types);
    }

}
