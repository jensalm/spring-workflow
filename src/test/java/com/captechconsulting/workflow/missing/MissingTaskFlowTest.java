package com.captechconsulting.workflow.missing;

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
@ComponentScan(basePackages = "com.captechconsulting.workflow.missing")
@EnableWorkFlow
public class MissingTaskFlowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(MissingTaskFlowTest.class);

    @Test(expected = BeanCreationException.class)
    public void test() throws Throwable {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MissingTaskFlowTest.class);
        FlowExecutor executor = ctx.getBean(FlowExecutor.class);
        assertNotNull(executor);
        boolean success = executor.execute("MissingTaskFlow");
        assertTrue(success);
    }

    @Flow
    public static class MissingTaskFlow {

        @Task
        @Start
        @Yes("task3")
        public Boolean task1() {
            LOG.debug("Task 1");
            return true;
        }

        @Task
        public Boolean task2() {
            LOG.debug("Task 2");
            return true;
        }

    }

}
