package com.captechconsulting.workflow.shared;

import com.captechconsulting.workflow.WorkflowExecutor;
import com.captechconsulting.workflow.config.EnableWorkFlow;
import com.captechconsulting.workflow.stereotypes.Flow;
import com.captechconsulting.workflow.stereotypes.Start;
import com.captechconsulting.workflow.stereotypes.Task;
import com.captechconsulting.workflow.stereotypes.Yes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = com.captechconsulting.workflow.shared.SharedWorkflowTest.class)
@Configuration
@EnableWorkFlow
public class SharedWorkflowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(SharedWorkflowTest.class);

    @Autowired
    @Qualifier("shared1")
    private WorkflowExecutor shared1;


    @Autowired
    @Qualifier("shared2")
    private WorkflowExecutor shared2;


    @Test
    public void test() throws Throwable {
        boolean success = shared1.execute("");
        assertTrue(success);
        success = shared2.execute("");
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
