package org.activiti.demo;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class TestHistoryLevel {

  public static void main(String[] args) throws Exception {
    
    deployAndRunOneTaskProcess(HistoryLevel.NONE);
  }

  private static void deployAndRunOneTaskProcess(HistoryLevel historyLevel) throws FileNotFoundException {
    ProcessEngine engine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("org/activiti/demo/activiti.cfg.xml")
            .setHistoryLevel(historyLevel).buildProcessEngine();

    engine.getRepositoryService().createDeployment().addClasspathResource("org/activiti/demo/process/oneTaskProcess.bpmn20.xml").deploy();

   
    Map<String, Object> procVariables = new HashMap<String, Object>();
    procVariables.put("procVar1", "procValue1");
    procVariables.put("procVar2", "procValue2");

    ProcessInstance processInstance = engine.getRuntimeService().startProcessInstanceByKey("oneTaskProcess", procVariables);
    processInstance.getProcessVariables().put("procVar2", "procValue2b");

    TaskService taskService = engine.getTaskService();
    Task task = taskService.createTaskQuery().singleResult();
    
    taskService.claim(task.getId(), "gonzo");
    //taskService.addComment(task.getId(), processInstance.getId(), "Me Gonzo!");
    taskService.setVariable(task.getId(), "procVar2", "procValue2c");
    taskService.setVariableLocal(task.getId(), "taskVar1", "taskValue1");
    taskService.setVariableLocal(task.getId(), "taskVar2", "taskValue2");
    taskService.setVariableLocal(task.getId(), "taskVar1", "taskValue1b");
    
    InputStream content = engine.getRepositoryService().getProcessModel(processInstance.getProcessDefinitionId());
    taskService.createAttachment("application/text", task.getId(), processInstance.getId(), "theProcessFile", "some file", content);
    
    taskService.complete(task.getId());
  }
}
