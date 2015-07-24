package com.captechconsulting.workflow.mismatched;

import com.captechconsulting.workflow.WorkflowException;
import com.captechconsulting.workflow.Workflow;
import com.captechconsulting.workflow.config.EnableWorkFlow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = com.captechconsulting.workflow.simple2.SimpleFlow2Test.class)
@Configuration
@EnableWorkFlow
public class MismatchedArgumentsFlowTest {

    @Autowired
    @Qualifier("SimpleFlow2")
    private Workflow simpleFlow;

    @Test(expected = WorkflowException.class)
    public void test() throws Throwable {
        simpleFlow.execute(new HashMap(), new ArrayList());
    }

}
