package com.captechconsulting.workflow.shared;

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
@ComponentScan(basePackages = "com.captechconsulting.workflow.shared")
@EnableWorkFlow
public class SharedWorkflowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(SharedWorkflowTest.class);

    @Test
    public void test() throws Throwable {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SharedWorkflowTest.class);
        FlowExecutor executor = ctx.getBean(FlowExecutor.class);
        assertNotNull(executor);
        boolean success = executor.execute("shared1", "");
        assertTrue(success);
        success = executor.get("shared2").start("");
        assertTrue(success);
    }

    @Flow(name="shared1", types = {String.class})
    public static class Shared1 {

        @Task
        @Start
        @Yes("task3")
        public boolean task1(String s) {
            LOG.debug("-----------------------");
            LOG.debug("Task 1");
            LOG.debug("-----------------------");
            return false;
        }

    }

    @Flow( name="shared2", types = {String.class})
    public static class Shared2 {

        @Task
        @Start
        @Yes("task3")
        public Boolean task2(String s) {
            LOG.debug("-----------------------");
            LOG.debug("Task 2");
            LOG.debug("-----------------------");
            return true;
        }

    }

    @Flow(name={"shared1", "shared2"}, types = {String.class})
    public static class Shared3 {

        @Task
        public Boolean task3(String s) {
            LOG.debug("-----------------------");
            LOG.debug("Task 3");
            LOG.debug("-----------------------");
            return false;
        }
    }


}
