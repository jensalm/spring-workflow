package com.captechconsulting.workflow.simple;

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
@ContextConfiguration(classes = com.captechconsulting.workflow.simple.SimpleFlowTest.class)
@Configuration
@EnableWorkFlow
public class SimpleFlowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(SimpleFlowTest.class);

    @Autowired
    @Qualifier("SimpleFlow")
    private WorkflowExecutor simpleFlow;

    @Test
    public void test() throws Throwable {
        boolean success = simpleFlow.execute(new SimpleFlowContext());
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
