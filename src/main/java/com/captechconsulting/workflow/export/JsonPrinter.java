package com.captechconsulting.workflow.export;

import com.captechconsulting.workflow.engine.FlowAdapter;
import com.captechconsulting.workflow.engine.TaskAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class JsonPrinter {

    private static final transient Logger LOG = LoggerFactory.getLogger(JsonPrinter.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    private boolean prettyPrint;

    public JsonPrinter() {
        this.prettyPrint = false;
    }

    public JsonPrinter(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public String write2(FlowAdapter flowAdapter) throws JsonProcessingException {
        return getWriter().writeValueAsString(flowAdapter);
    }

    public String write(FlowAdapter flowAdapter) throws JsonProcessingException {

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("name", flowAdapter.getName());
        rootNode.put("tasks", createTasks(flowAdapter.getTasks()));
        rootNode.put("flow", createFlow(flowAdapter, flowAdapter.getStart()));

        return getWriter().writeValueAsString(rootNode);
    }

    private ObjectNode createTasks(Map<String, TaskAdapter> tasks) {
        ObjectNode taskNode = objectMapper.createObjectNode();
        for (String name : tasks.keySet()) {
            TaskAdapter adapter = tasks.get(name);
            taskNode.put(name, createTask(adapter));
        }
        return taskNode;
    }

    private JsonNode createTask(TaskAdapter adapter) {
        ObjectNode node = objectMapper.createObjectNode();
        if (StringUtils.isNotBlank(adapter.getYes())) {
            node.put("yes", adapter.getYes());
        }
        if (StringUtils.isNotBlank(adapter.getNo())) {
            node.put("no", adapter.getNo());
        }
        if (adapter.isStart()) {
            node.put("start", Boolean.TRUE);
        }
        node.put("class", adapter.getBeanName());
        node.put("method", adapter.getMethod());
        return node;
    }

    private JsonNode createFlow(FlowAdapter flowAdapter, TaskAdapter taskAdapter) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("name", taskAdapter.getName());
        String yes = taskAdapter.getYes();
        String no = taskAdapter.getNo();
        if (StringUtils.isNotBlank(yes) || StringUtils.isNotBlank(no)) {
            if (StringUtils.isNotBlank(yes) && flowAdapter.getTasks().containsKey(yes)) {
                JsonNode jsonNode = createFlow(flowAdapter, flowAdapter.getTask(taskAdapter.getYes()));
                if (jsonNode != null) {
                    node.put("yes", jsonNode);
                }
            }
            if (StringUtils.isNotBlank(no) && flowAdapter.getTasks().containsKey(no)) {
                JsonNode jsonNode = createFlow(flowAdapter, flowAdapter.getTask(taskAdapter.getNo()));
                if (jsonNode != null) {
                    node.put("no", jsonNode);
                }
            }
        }
        return node;
    }


    private ObjectWriter getWriter() {
        if (prettyPrint) {
            LOG.debug("Using pretty print");
            return objectMapper.writerWithDefaultPrettyPrinter();
        } else {
            LOG.debug("Using standard print");
            return objectMapper.writer();
        }
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }
}
