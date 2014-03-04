package com.captechconsulting.workflow.export;

import com.captechconsulting.workflow.FlowExecutor;
import com.captechconsulting.workflow.config.EnableWorkFlow;
import com.captechconsulting.workflow.engine.FlowAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.captechconsulting.workflow.simple2")
@EnableWorkFlow
public class JsonPrinterTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(JsonPrinterTest.class);

    private JsonPrinter jsonPrinter = new JsonPrinter(true);

    @Test
    public void print() throws JsonProcessingException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(JsonPrinterTest.class);
        FlowExecutor executor = ctx.getBean(FlowExecutor.class);

        FlowAdapter flowAdapter = executor.get("SimpleFlow2");
        LOG.debug(jsonPrinter.write2(flowAdapter));
        String value = jsonPrinter.write(flowAdapter);
        LOG.debug(value);

    }

}
