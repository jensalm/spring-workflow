package com.captechconsulting.workflow.export;

import com.captechconsulting.workflow.FlowAdapter;
import com.captechconsulting.workflow.TaskAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Creates a JSON tree from the flow or task.
 */
public final class JsonExporter {

    private static final String YES = "yes";
    private static final String NO = "no";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String TASKS = "tasks";
    private static final String FLOW = "flow";
    private static final String START = "start";
    private static final String CLASS = "class";
    private static final String METHOD = "method";
    private static final String END = "END";

    private static ObjectMapper objectMapper = new ObjectMapper();

    private JsonExporter() { }

    /**
     * Creates a small JSON tree of all the adapters
     * @param adapters
     * @return JSON tree
     * @throws JsonProcessingException
     */
    public static JsonNode export(Collection<FlowAdapter> adapters) throws JsonProcessingException {
        return export(adapters, false);
    }

    /**
     * Creates a JSON tree of all the adapters
     * @param adapters
     * @param fullExport true gives all details while false is small
     * @return JSON tree
     * @throws JsonProcessingException
     */
    public static JsonNode export(Collection<FlowAdapter> adapters, Boolean fullExport) throws JsonProcessingException {
        ArrayNode flowNode = objectMapper.createArrayNode();
        for (FlowAdapter entry : adapters) {
            flowNode.add(export(entry, fullExport));
        }

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("flows", flowNode);
        return rootNode;
    }

    /**
     * Creates a small JSON tree from a flow.
     * @param flowAdapter
     * @return JSON tree
     * @throws JsonProcessingException
     * @see FlowAdapter
     */
    public static JsonNode export(FlowAdapter flowAdapter) throws JsonProcessingException {
        return export(flowAdapter, false);
    }

    /**
     * Creates a JSON tree from a flow.
     * @param flowAdapter
     * @return JSON tree
     * @param fullExport true gives all details while false is small
     * @throws JsonProcessingException
     * @see FlowAdapter
     */
    public static JsonNode export(FlowAdapter flowAdapter, Boolean fullExport) throws JsonProcessingException {

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put(NAME, flowAdapter.getName());
        rootNode.put(DESCRIPTION, flowAdapter.getDescription());
        if (fullExport) {
            ArrayNode taskNode = objectMapper.createArrayNode();
            for (TaskAdapter adapter : (Collection<TaskAdapter>)flowAdapter.getTasks().values()) {
                taskNode.add(export(adapter, fullExport));
            }
            rootNode.put(TASKS, taskNode);
            rootNode.put(FLOW, createFlow(flowAdapter, flowAdapter.getStartTask(), Sets.<String>newHashSet()));
        }
        return rootNode;
    }

    /**
     * Creates a small JSON tree from a task.
     * @param adapter
     * @return JSON tree
     * @see TaskAdapter
     */
    public static JsonNode export(TaskAdapter adapter) {
        return export(adapter, false);
    }

    /**
     * Creates a JSON tree from a task.
     * @param adapter
     * @param fullExport true gives all details while false is small
     * @return JSON tree
     * @see TaskAdapter
     */
    public static JsonNode export(TaskAdapter adapter, Boolean fullExport) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put(NAME, adapter.getName());
        node.put(DESCRIPTION, adapter.getDescription());
        if (fullExport) {
            if (StringUtils.isNotBlank(adapter.getYes())) {
                node.put(YES, adapter.getYes());
            } else {
                node.put(YES, END);
            }
            if (StringUtils.isNotBlank(adapter.getNo())) {
                node.put(NO, adapter.getNo());
            } else {
                node.put(NO, END);
            }
            if (adapter.isStart()) {
                node.put(START, Boolean.TRUE);
            }
            node.put(CLASS, adapter.getBeanName());
            node.put(METHOD, adapter.getMethod());
        }
        return node;
    }

    private static JsonNode createFlow(FlowAdapter flowAdapter, TaskAdapter taskAdapter, Set<String> done) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put(NAME, taskAdapter.getName());
        addTaskNode(node, flowAdapter, taskAdapter.getName(), YES, taskAdapter.getYes(), done);
        addTaskNode(node, flowAdapter, taskAdapter.getName(), NO, taskAdapter.getNo(), done);
        return node;
    }

    private static void addTaskNode(ObjectNode node, FlowAdapter flowAdapter, String taskName,
                                    String key, String nextTask, Set<String> done) {
        if (StringUtils.isNotBlank(nextTask) && flowAdapter.getTasks().containsKey(nextTask) &&
                !done.contains(createKey(taskName, nextTask))) {
            done.add(createKey(taskName, nextTask));
            JsonNode jsonNode = createFlow(flowAdapter, flowAdapter.getTask(nextTask), done);
            if (jsonNode != null) {
                node.put(key, jsonNode);
            }
        } else {
            node.put(key, END);
        }
    }

    private static String createKey(String taskName, String nextTask) {
        return taskName + " -> " + nextTask;
    }

}
