package org.activiti;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

public class MyUnitTest {

  @Rule
  public ActivitiRule activitiRule = new ActivitiRule();

  @Test
  @Deployment(resources = { "org/activiti/test/my-process.bpmn", "org/activiti/test/SubProcess1.bpmn", "org/activiti/test/SubProcess2.bpmn" })
  public void test() {
    Map<String, Object> initialVariables = new HashMap<String, Object>();
    initialVariables.put("test", "begin");
    ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", initialVariables);

    assertNotNull(processInstance);

    // get task from first subprocess
    Task task = activitiRule.getTaskService().createTaskQuery().singleResult();

    Map<String, String> taskFormVariables = new HashMap<String, String>();
    taskFormVariables.put("test", "begin");
    activitiRule.getFormService().submitTaskFormData(task.getId(), new HashMap<String, String>());

    // get task from second subprocess
    task = activitiRule.getTaskService().createTaskQuery().singleResult();

    activitiRule.getFormService().submitTaskFormData(task.getId(), new HashMap<String, String>());

    // get task from main process
    task = activitiRule.getTaskService().createTaskQuery().singleResult();

    Object testVariable2 = activitiRule.getRuntimeService().getVariable(processInstance.getId(), "test2");
    assertNotNull(testVariable2);
    assertEquals("compensated2", testVariable2.toString());

    Object testVariable1 = activitiRule.getRuntimeService().getVariable(processInstance.getId(), "test1");
    assertNotNull(testVariable1);
    assertEquals("compensated1", testVariable1.toString());

  }

}
