package com.captechconsulting.workflow.simple;

import com.captechconsulting.workflow.FlowExecutor;
import com.captechconsulting.workflow.config.EnableWorkFlow;
import com.captechconsulting.workflow.stereotypes.Flow;
import com.captechconsulting.workflow.stereotypes.Start;
import com.captechconsulting.workflow.stereotypes.Task;
import com.captechconsulting.workflow.stereotypes.Yes;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Configuration
@ComponentScan(basePackages = "com.captechconsulting.workflow.simple")
@EnableWorkFlow
public class SimpleFlowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(SimpleFlowTest.class);

    @Test
    public void test() throws Throwable {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SimpleFlowTest.class);
        FlowExecutor executor = ctx.getBean(FlowExecutor.class);
        assertNotNull(executor);
        boolean success = executor.execute("SimpleFlow", new SimpleFlowContext());
        assertTrue(success);
    }

    @Flow(types = SimpleFlowContext.class)
    public static class SimpleFlow {

        @Task
        @Start
        @Yes("task2")
        public Boolean task1(SimpleFlowContext context) {
            LOG.debug("Task 1");
            return true;
        }

        @Task
        public Boolean task2(SimpleFlowContext context) {
            LOG.debug("Task 2");
            return true;
        }

    }
}
