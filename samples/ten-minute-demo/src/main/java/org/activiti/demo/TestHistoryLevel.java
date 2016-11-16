package org.activiti.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.history.HistoryLevel;

public class TestHistoryLevel {

  public static void main(String[] args) {
    ProcessEngine engine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("org/activiti/demo/activiti.cfg.xml")
            .setHistoryLevel(HistoryLevel.NONE).buildProcessEngine();

    engine.getRepositoryService().createDeployment().addClasspathResource("org/activiti/demo/process/oneTaskProcess.bpmn20.xml").deploy();
  }
}
