package com.captechconsulting.workflow.multiple;

import com.captechconsulting.workflow.WorkflowExecutor;
import com.captechconsulting.workflow.config.EnableWorkFlow;
import com.captechconsulting.workflow.stereotypes.Flow;
import com.captechconsulting.workflow.stereotypes.Start;
import com.captechconsulting.workflow.stereotypes.Task;
import com.captechconsulting.workflow.stereotypes.Yes;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = com.captechconsulting.workflow.multiple.MultipleStartFlowTest.class)
@Configuration
@EnableWorkFlow
public class MultipleStartFlowTest {

    private static final transient Logger LOG = LoggerFactory.getLogger(MultipleStartFlowTest.class);

    @Autowired
    @Qualifier("MultipleStartFlow")
    private WorkflowExecutor workflowExecutor;

    @Ignore
    @Test(expected = BeanCreationException.class)
    public void test() throws Throwable {
        boolean success = workflowExecutor.execute();
        assertTrue(success);
    }

    @Flow
    public static class MultipleStartFlow {

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
