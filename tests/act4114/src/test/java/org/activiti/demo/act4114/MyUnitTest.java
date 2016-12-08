package org.activiti.demo.act4114;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableUpdate;
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
	@Deployment(resources = { "org/activiti/demo/act4114/my-process.bpmn20.xml" })
	public void test() {
		System.out.println("Process [start > someTask > end]:");

		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
		assertNotNull(processInstance);
		System.out.println("\tProcess '" + processInstance.getId() + "' --> Process Start ");

		TaskService taskService = activitiRule.getTaskService();

		// Get UserTask
		Task task = taskService.createTaskQuery().singleResult();
		assertEquals("Activiti is awesome!", task.getName());
		String taskId = task.getId();

		// Save var without complete
		taskService.setVariable(taskId, "var_1", "this is a local var without complete");

		// Complete task with other vars
		Map<String, Object> variableMapComplete = new HashMap<String, Object>();
		variableMapComplete.put("var_2", "this is a complete var");
		taskService.complete(taskId, variableMapComplete);

		// Check end process
		checkEndProcess("end", processInstance.getId());

		System.out.println("History details of variables associated with the user task 'someTask':");

		// Check history vars.
		checkHistoryVars(processInstance.getId()); // <-- Possible Bug!!!
	}

	@Test
	@Deployment(resources = { "org/activiti/demo/act4114/oneTaskProcess.bpmn20.xml" })
	public void testGetVariableByHistoricActivityInstance() {
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("oneTaskProcess");
		assertNotNull(processInstance);
		Task task = activitiRule.getTaskService().createTaskQuery().singleResult();

		activitiRule.getTaskService().setVariable(task.getId(), "variable1", "value1");
		activitiRule.getTaskService().setVariable(task.getId(), "variable1", "value2");

		HistoricActivityInstance historicActivitiInstance = activitiRule.getHistoryService()
				.createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).activityId("theTask")
				.singleResult();
		assertNotNull(historicActivitiInstance);

		List<HistoricDetail> resultSet = activitiRule.getHistoryService().createHistoricDetailQuery().variableUpdates()
				.activityInstanceId(historicActivitiInstance.getId()).orderByTime().asc().list();

		assertEquals(2, resultSet.size());
		assertEquals("variable1", ((HistoricVariableUpdate) resultSet.get(0)).getVariableName());
		assertEquals("value1", ((HistoricVariableUpdate) resultSet.get(0)).getValue());
		assertEquals("variable1", ((HistoricVariableUpdate) resultSet.get(1)).getVariableName());
		assertEquals("value2", ((HistoricVariableUpdate) resultSet.get(1)).getValue());
	}

	/**
	 * Check end process.
	 * 
	 * @param expectedEndEvent
	 *            End event id (eg: "end").
	 * @param processInstanceId
	 *            ProcessInstanceId to evaluate
	 */
	private void checkEndProcess(String expectedEndEvent, String processInstanceId) {
		HistoryService historyService = activitiRule.getHistoryService();

		// Check end process
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		assertNotNull(historicProcessInstance);
		assertNotNull(historicProcessInstance.getEndTime());

		// Check expected end event
		List<HistoricActivityInstance> listEndEvents = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).activityType("endEvent").list();
		assertNotNull(listEndEvents);
		assertEquals(1, listEndEvents.size());
		assertEquals(expectedEndEvent, listEndEvents.get(0).getActivityId());

		System.out.println("\tProcess '" + processInstanceId + "' --> Process Finish: " + expectedEndEvent);
	}

	/**
	 * Check history vars detail.
	 * 
	 * @param processInstanceId
	 *            ProcessInstanceId to evaluate
	 */
	private void checkHistoryVars(String processInstanceId) {
		HistoryService historyService = activitiRule.getHistoryService();

		HistoricActivityInstance hcoActivityInstance = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).activityId("someTask").singleResult();
		assertNotNull(hcoActivityInstance);

		List<HistoricDetail> historicVariableUpdateList = historyService.createHistoricDetailQuery().variableUpdates()
				.activityInstanceId(hcoActivityInstance.getId()).orderByTime().asc().list();

		for (HistoricDetail historicDetail : historicVariableUpdateList) {
			HistoricVariableUpdate hcoVar = (HistoricVariableUpdate) historicDetail;
			System.out.println("\tVariable Name: " + hcoVar.getVariableName() + ". Value: " + hcoVar.getValue());
		}

		/*
		 * =======================================================
		 * 
		 * This assert pass in Activiti 5.16.4, but not in later versions.
		 * 
		 * I hope two variables: "var_1" and "var_2"
		 * 
		 * =======================================================
		 */
		assertEquals(2, historicVariableUpdateList.size());
		assertEquals("var_1", ((HistoricVariableUpdate) historicVariableUpdateList.get(0)).getVariableName());
		assertEquals("var_2", ((HistoricVariableUpdate) historicVariableUpdateList.get(1)).getVariableName());
	}

	/**
	 * Check history vars detail.
	 * 
	 * @param processInstanceId
	 *            ProcessInstanceId to evaluate
	 */
	private void checkHistoryVars2(String processInstanceId) {
		HistoryService historyService = activitiRule.getHistoryService();

		List<HistoricVariableInstance> historicVariableUpdateList = historyService.createHistoricVariableInstanceQuery()
				.processInstanceId(processInstanceId).orderByVariableName().asc().list();

		for (HistoricVariableInstance historicDetail : historicVariableUpdateList) {
			HistoricVariableInstance hcoVar = (HistoricVariableInstance) historicDetail;
			System.out.println("\tVariable Name: " + hcoVar.getVariableName() + ". Value: " + hcoVar.getValue());
		}

		/*
		 * =======================================================
		 * 
		 * This assert pass in Activiti 5.16.4, but not in later versions.
		 * 
		 * I hope two variables: "var_1" and "var_2"
		 * 
		 * =======================================================
		 */
		assertEquals(2, historicVariableUpdateList.size());
		assertEquals("var_1", ((HistoricVariableInstance) historicVariableUpdateList.get(0)).getVariableName());
		assertEquals("var_2", ((HistoricVariableInstance) historicVariableUpdateList.get(1)).getVariableName());
	}
}
