package com.captechconsulting.workflow.shared;

import com.bofa.p3d.workflow.FlowExecutor;
import com.bofa.p3d.workflow.config.EnableWorkFlow;
import com.bofa.p3d.workflow.stereotypes.Flow;
import com.bofa.p3d.workflow.stereotypes.Start;
import com.bofa.p3d.workflow.stereotypes.Task;
import com.bofa.p3d.workflow.stereotypes.Yes;
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
@ComponentScan(basePackages = "com.bofa.p3d.workflow.shared")
@EnableWorkFlow
public class SharedTaskWorkflowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(SharedTaskWorkflowTest.class);

    @Test
    public void test() throws Throwable {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SharedTaskWorkflowTest.class);
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
