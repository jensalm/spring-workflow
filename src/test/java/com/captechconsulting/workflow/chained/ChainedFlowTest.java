package com.captechconsulting.workflow.chained;

import com.captechconsulting.workflow.FlowExecutor;
import com.captechconsulting.workflow.WorkflowException;
import com.captechconsulting.workflow.config.EnableWorkFlow;
import com.captechconsulting.workflow.simple.SimpleFlowContext;
import com.captechconsulting.workflow.stereotypes.Flow;
import com.captechconsulting.workflow.stereotypes.Start;
import com.captechconsulting.workflow.stereotypes.Task;
import com.captechconsulting.workflow.stereotypes.Yes;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Configuration
@ComponentScan(basePackages = "com.captechconsulting.workflow.chained")
@EnableWorkFlow
public class ChainedFlowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(ChainedFlowTest.class);

    @Test
    public void test() throws Throwable {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ChainedFlowTest.class);
        FlowExecutor executor = ctx.getBean(FlowExecutor.class);
        assertNotNull(executor);
        boolean success = executor.execute("ChainedFlow", "");
        assertTrue(success);
    }

    @Flow(types = String.class)
    public static class ChainedFlow {

        @Autowired
        private FlowExecutor flowExecutor;

        @Task
        @Start
        @Yes("task2")
        public Boolean task1(String s) {
            LOG.debug("Task 1");
            try {
                return flowExecutor.execute("SimpleFlow", new SimpleFlowContext());
            } catch (WorkflowException e) {
                return false;
            }
        }

        @Task
        public Boolean task2(String s) {
            LOG.debug("Task 2");
            return true;
        }
    }
}

