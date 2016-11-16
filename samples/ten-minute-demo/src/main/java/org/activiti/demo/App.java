package org.activiti.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		//ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		ProcessEngine engine =  ProcessEngineConfiguration
				.createProcessEngineConfigurationFromResource("org/activiti/demo/activiti.cfg.xml").buildProcessEngine();
		
		
		RepositoryService repositoryService = engine.getRepositoryService();

		Deployment deployment = repositoryService.createDeployment()
				.addClasspathResource("org/activiti/demo/process/FinancialReportProcess.bpmn20.xml").deploy();
	}
}
