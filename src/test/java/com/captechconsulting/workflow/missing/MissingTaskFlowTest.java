package com.captechconsulting.workflow.missing;

import com.captechconsulting.workflow.Workflow;
import com.captechconsulting.workflow.config.EnableWorkFlow;
import com.captechconsulting.workflow.stereotypes.Start;
import com.captechconsulting.workflow.stereotypes.Task;
import com.captechconsulting.workflow.stereotypes.Yes;
import org.junit.Ignore;
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
@ContextConfiguration(classes = com.captechconsulting.workflow.missing.MissingTaskFlowTest.class)
@Configuration
@EnableWorkFlow
public class MissingTaskFlowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(MissingTaskFlowTest.class);

    @Autowired
    @Qualifier("MissingTaskFlow")
    private Workflow missingTaskFlow;

    @Ignore
    @Test(expected = IllegalArgumentException.class)
    public void test() throws Throwable {
        boolean success = missingTaskFlow.execute();
        assertTrue(success);
    }

    //@Flow
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
