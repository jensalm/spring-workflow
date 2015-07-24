package com.captechconsulting.workflow.split;

import com.captechconsulting.workflow.WorkflowExecutor;
import com.captechconsulting.workflow.config.EnableWorkFlow;
import com.captechconsulting.workflow.stereotypes.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Random;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = com.captechconsulting.workflow.split.SplitWorkflowTest.class)
@Configuration
@EnableWorkFlow
public class SplitWorkflowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(SplitWorkflowTest.class);

    @Autowired
    @Qualifier("split")
    private WorkflowExecutor splitFlow;

    @Test
    public void test() throws Throwable {
        boolean success = splitFlow.execute("");
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
