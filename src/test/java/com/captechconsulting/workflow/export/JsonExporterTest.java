package com.captechconsulting.workflow.export;

import com.captechconsulting.workflow.config.EnableWorkFlow;
import com.captechconsulting.workflow.FlowAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Configuration
@ComponentScan(basePackages = "com.captechconsulting.workflow.simple2")
@EnableWorkFlow
public class JsonExporterTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(JsonExporterTest.class);

    @Test
    public void print() throws JsonProcessingException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(JsonExporterTest.class);
        Map<String, FlowAdapter> executors = ctx.getBeansOfType(FlowAdapter.class);

        FlowAdapter flowAdapter = executors.get("SimpleFlow2");
        JsonNode node = JsonExporter.export(flowAdapter, true);
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(node));

        assertTrue(node.isObject());

        ObjectNode objectNode = (ObjectNode) node;
        assertEquals("SimpleFlow2", objectNode.get("name").asText());

        // Flow node
        assertTrue(node.get("flow").isObject());
        ObjectNode flow = (ObjectNode) node.get("flow");

        // Task1 node
        assertTrue(flow.get("name").isTextual());
        assertEquals("task1", flow.get("name").asText());

        // Task1 Yes -> Task2
        assertTrue(flow.get("yes").isObject());
        ObjectNode task1Yes = (ObjectNode) flow.get("yes");
        assertEquals("task2", task1Yes.get("name").asText());

        // Task1 Yes -> Task2 No -> END
        assertTrue(task1Yes.get("no").isTextual());
        assertEquals("END", task1Yes.get("no").asText());

        // Task1 No -> Task3
        assertTrue(flow.get("no").isObject());
        ObjectNode task1No = (ObjectNode) flow.get("no");
        assertEquals("task3", task1No.get("name").asText());

        // Task1 Yes -> Task2 Yes -> Task5
        assertTrue(task1Yes.get("yes").isObject());
        ObjectNode task2Yes = (ObjectNode) task1Yes.get("yes");
        assertEquals("task5", task2Yes.get("name").asText());

        // Task1 Yes -> Task2 Yes -> Task5 No -> END
        assertTrue(task2Yes.get("no").isTextual());
        assertEquals("END", task2Yes.get("no").asText());

        // Task1 Yes -> Task2 Yes -> Task5 Yes -> END
        assertTrue(task2Yes.get("yes").isTextual());
        assertEquals("END", task2Yes.get("yes").asText());

        // Task1 No -> Task3 Yes -> Task4
        assertTrue(task1No.get("yes").isObject());
        ObjectNode task3Yes = (ObjectNode) task1No.get("yes");
        assertEquals("task4", task3Yes.get("name").asText());

        // Task1 No -> Task3 No -> Task2
        assertTrue(task1No.get("no").isObject());
        ObjectNode task3No = (ObjectNode) task1No.get("no");
        assertEquals("task2", task3No.get("name").asText());

        // Task1 No -> Task3 Yes -> Task4 No -> Task 5
        assertTrue(task3Yes.get("no").isObject());
        ObjectNode task4No = (ObjectNode) task3Yes.get("no");
        assertEquals("task5", task4No.get("name").asText());

        // Task1 No -> Task3 Yes -> Task4 Yes -> END
        assertTrue(task3Yes.get("yes").isTextual());
        assertEquals("END", task3Yes.get("yes").asText());

        // Task1 No -> Task3 Yes -> Task4 Yes -> END
        assertTrue(task3No.get("yes").isTextual());
        assertEquals("END", task3No.get("yes").asText());

        // Task1 No -> Task3 Yes -> Task4 No -> END
        assertTrue(task3No.get("yes").isTextual());
        assertEquals("END", task3No.get("no").asText());

    }

}
