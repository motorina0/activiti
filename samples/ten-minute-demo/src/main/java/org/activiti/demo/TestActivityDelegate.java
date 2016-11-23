package org.activiti.demo;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

public class TestActivityDelegate {

  public static void main(String[] args) {
    ProcessEngine engine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("org/activiti/demo/activiti.cfg.xml").buildProcessEngine();

    engine.getRepositoryService().createDeployment().addClasspathResource("org/activiti/demo/process/ActivityDelegateExecution.bpmn").deploy();

    Map<String, Object> procVariables = new HashMap<String, Object>();
    procVariables.put("iteration", 0L);
    
    engine.getRuntimeService().startProcessInstanceByKey("activity-delegate-execution", procVariables);

    TaskService taskService = engine.getTaskService();
    
    completeTask(taskService, true);
    completeTask(taskService, true);
    completeTask(taskService, false);
  }

  private static void completeTask(TaskService taskService,boolean retry) {
    Task task = taskService.createTaskQuery().singleResult();    
    taskService.setVariable(task.getId(), "retry", retry);
    taskService.complete(task.getId());
  }

}
