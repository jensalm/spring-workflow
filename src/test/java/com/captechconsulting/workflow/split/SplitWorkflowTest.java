package com.captechconsulting.workflow.split;

import com.captechconsulting.workflow.FlowExecutor;
import com.captechconsulting.workflow.config.EnableWorkFlow;
import com.captechconsulting.workflow.stereotypes.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Configuration
@ComponentScan(basePackages = "com.captechconsulting.workflow.split")
@EnableWorkFlow
public class SplitWorkflowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(SplitWorkflowTest.class);

    @Test
    public void test() throws Throwable {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SplitWorkflowTest.class);
        FlowExecutor executor = ctx.getBean(FlowExecutor.class);
        assertNotNull(executor);
        boolean success = executor.get("split").start("");
        assertTrue(success);
    }

    @Flow(name="split", types = {String.class})
    public static class Split1 {

        @Task
        @Start
        @Yes("task2")
        @No("task3")
        public boolean task1(String s) {
            LOG.debug("-----------------------");
            LOG.debug("Task 1");
            if (new Random().nextBoolean()) {
                LOG.debug(" --> Yes");
                return true;
            }
            LOG.debug(" --> No");
            LOG.debug("-----------------------");
            return false;
        }

        @Task
        @Yes("task5")
        public Boolean task2(String s) {
            LOG.debug("-----------------------");
            LOG.debug("Task 2");
            LOG.debug("-----------------------");
            return true;
        }
    }

    @Flow(name="split", types = {String.class})
    public static class Split2 {
        @Task
        @Yes("task4")
        @No("task2")
        public Boolean task3(String s) {
            LOG.debug("-----------------------");
            LOG.debug("Task 3");
            if (new Random().nextBoolean()) {
                LOG.debug(" --> Yes");
                return true;
            }
            LOG.debug(" --> No");
            LOG.debug("-----------------------");
            return false;
        }

        @Task
        public Boolean task4(String s) {
            LOG.debug("-----------------------");
            LOG.debug("Task 4");
            LOG.debug("-----------------------");
            return true;
        }

        @Task
        public Boolean task5(String s) {
            LOG.debug("-----------------------");
            LOG.debug("Task 5");
            LOG.debug("-----------------------");
            return true;
        }

    }

}
