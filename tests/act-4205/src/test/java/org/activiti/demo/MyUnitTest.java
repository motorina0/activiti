package org.activiti.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

public class MyUnitTest {

  @Rule
  public ActivitiRule activitiRule = new ActivitiRule();

  @Test
  @Deployment(resources = { "org/activiti/demo/signalTestProcess.bpmn20.xml" })
  public void testSignalEvent() {
    activitiRule.getIdentityService().setAuthenticatedUserId("kermit");
    RuntimeService runtimeService = activitiRule.getRuntimeService();   
    TaskService taskService = activitiRule.getTaskService();
    
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("signalTestProcess");
    assertNotNull(processInstance);

    ProcessInstance secondProcessInstance = runtimeService.startProcessInstanceByKey("signalTestProcess");
    assertNotNull(secondProcessInstance);
    
    assertEquals(2, runtimeService.createExecutionQuery().signalEventSubscriptionName("Cancel Signal").list().size());
    assertEquals(2, taskService.createTaskQuery().count());
    
    String executionId = runtimeService.createExecutionQuery().signalEventSubscriptionName("Cancel Signal").list().get(0).getId();
    runtimeService.signalEventReceived("Cancel Signal", executionId);

    assertEquals(1, taskService.createTaskQuery().count());
    
    String secondExecutionId = runtimeService.createExecutionQuery().signalEventSubscriptionName("Cancel Signal").singleResult().getId();
    runtimeService.signalEventReceived("Cancel Signal", secondExecutionId);

    assertEquals(0, taskService.createTaskQuery().count());
  }

}
