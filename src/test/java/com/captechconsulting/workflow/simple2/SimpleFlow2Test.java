package com.captechconsulting.workflow.simple2;

import com.captechconsulting.workflow.Workflow;
import com.captechconsulting.workflow.config.EnableWorkFlow;
import com.captechconsulting.workflow.stereotypes.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = com.captechconsulting.workflow.simple2.SimpleFlow2Test.class)
@Configuration
@EnableWorkFlow
public class SimpleFlow2Test {

    private static final transient Logger LOG = LoggerFactory.getLogger(SimpleFlow2Test.class);

    @Autowired
    @Qualifier("SimpleFlow2")
    private Workflow simpleFlow;

    @Test
    public void test() throws Throwable {
        boolean success = simpleFlow.execute("", Maps.newHashMap(), Lists.newArrayList());
        assertTrue(success);
    }

    @Flow
    public static class SimpleFlow2 {

        @Task
        @Start
        @Yes("task2")
        @No("task3")
        public boolean task1(String s, Map m, List l) {
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
        public Boolean task2(String s, Map m, List l) {
            LOG.debug("-----------------------");
            LOG.debug("Task 2");
            LOG.debug("-----------------------");
            return true;
        }

        @Task
        @Yes("task4")
        @No("task2")
        public Boolean task3(String s, Map m, List l) {
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
        @No("task5")
        public Boolean task4(String s, Map m, List l) {
            LOG.debug("-----------------------");
            LOG.debug("Task 4");
            LOG.debug("-----------------------");
            return true;
        }

        @Task
        public Boolean task5(String s, Map m, List l) {
            LOG.debug("-----------------------");
            LOG.debug("Task 5");
            LOG.debug("-----------------------");
            return true;
        }


    }
}
