package com.captechconsulting.workflow.multiple;

import com.captechconsulting.workflow.FlowExecutor;
import com.captechconsulting.workflow.config.EnableWorkFlow;
import com.captechconsulting.workflow.stereotypes.Flow;
import com.captechconsulting.workflow.stereotypes.Start;
import com.captechconsulting.workflow.stereotypes.Task;
import com.captechconsulting.workflow.stereotypes.Yes;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Configuration
@ComponentScan(basePackages = "com.captechconsulting.workflow.multiple")
@EnableWorkFlow
public class MultipleStartFlowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(MultipleStartFlowTest.class);
    private static final String FLOW_NAME = "MultipleStartFlow";

    @Test(expected = BeanCreationException.class)
    public void test() throws Throwable {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MultipleStartFlowTest.class);
        FlowExecutor executor = ctx.getBean(FlowExecutor.class);
        assertNotNull(executor);
        boolean success = executor.execute(FLOW_NAME);
        assertTrue(success);
    }

    @Flow(FLOW_NAME)
    public static class SimpleFlow {

        @Task
        @Start
        @Yes("task2")
        public Boolean task1() {
            LOG.debug("Task 1");
            return true;
        }

        @Task
        @Start
        public Boolean task2() {
            LOG.debug("Task 2");
            return true;
        }

    }

}
