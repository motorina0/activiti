package org.activiti.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
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
  @Deployment(resources = { "org/activiti/demo/my-process.bpmn20.xml" })
  public void test() {
    List<String> categories = new ArrayList<String>();
    categories.add("c1");
    categories.add("c2");
    categories.set(0, null);
    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("categories", categories);

    ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
    assertNotNull(processInstance);

    TaskService taskService = activitiRule.getTaskService();
    Task task = taskService.createTaskQuery().singleResult();
    assertEquals("Activiti is awesome!", task.getName());

    // taskService.setVariable(task.getId(), "categories", categories);
    taskService.complete(task.getId(), vars);
  }

}
