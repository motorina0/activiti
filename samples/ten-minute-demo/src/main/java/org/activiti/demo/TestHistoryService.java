package org.activiti.demo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricDetailQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.history.NativeHistoricActivityInstanceQuery;
import org.activiti.engine.task.Task;

public class TestHistoryService {
	public static void main(String[] args) {
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();

		HistoryService historyService = engine.getHistoryService();

		checkHistoricActivityInstanceQuery(historyService.createHistoricActivityInstanceQuery());
		checkHistoricDetailQuery(historyService.createHistoricDetailQuery());

		checkHistoricProcessInstanceQuery(historyService.createHistoricProcessInstanceQuery());

		checkHistoricTaskInstanceQuery(historyService.createHistoricTaskInstanceQuery());
		checkHistoricVariableInstanceQuery(historyService.createHistoricVariableInstanceQuery());

//		checkNativeHistoricActivityInstanceQuery(historyService.createNativeHistoricActivityInstanceQuery());

	}

	private static void checkHistoricActivityInstanceQuery(
			HistoricActivityInstanceQuery historicActivityInstanceQuery) {

		long count = historicActivityInstanceQuery.count();
		log("historicActivityInstanceQuery", "count: " + count);

		String actvityType = historicActivityInstanceQuery.activityId("request").singleResult().getActivityType();
		log("historicActivityInstanceQuery", "activityType: " + actvityType);

		String processInstanceId = historicActivityInstanceQuery.finished().singleResult().getProcessInstanceId();
		log("historicActivityInstanceQuery", "processInstanceId: " + processInstanceId);

		List<HistoricActivityInstance> processSteps = historicActivityInstanceQuery.processInstanceId(processInstanceId)
				.list();
		log("historicActivityInstanceQuery", "stepListForProcess: " + processSteps);

		long stepCountForProcess = historicActivityInstanceQuery.processInstanceId(processInstanceId).count();
		log("historicActivityInstanceQuery", "stepCountForProcess: " + stepCountForProcess);

		List<HistoricActivityInstance> tasksAssignedToUser = historicActivityInstanceQuery.taskAssignee("gonzo")
				.orderByActivityId().asc().list();
		log("historicActivityInstanceQuery", "tasksAssignedToUser: " + tasksAssignedToUser);

		List<HistoricActivityInstance> allStartEvents = historicActivityInstanceQuery.activityType("startEvent").list();
		log("historicActivityInstanceQuery", "allStartEvents: " + allStartEvents);

		List<HistoricActivityInstance> runningSteps = historicActivityInstanceQuery.processInstanceId(processInstanceId)
				.unfinished().orderByHistoricActivityInstanceStartTime().desc().list();

		log("historicActivityInstanceQuery", "runningSteps: " + runningSteps);
	}

	private static void checkHistoricDetailQuery(HistoricDetailQuery historicDetailQuery) {
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		TaskService taskService = engine.getTaskService();
		Task firstTask = taskService.newTask("123");
		taskService.saveTask(firstTask);

		taskService.setVariable(firstTask.getId(), "variable1", "value1");
		taskService.setVariable(firstTask.getId(), "variable1", "value2");
		taskService.setVariable(firstTask.getId(), "variable1", "value3");

		List<HistoricDetail> resultSet = historicDetailQuery.variableUpdates().orderByTime().asc().list();
		log("historicDetailQuery", "result count: " + resultSet.size());
	}

	private static void checkHistoricProcessInstanceQuery(HistoricProcessInstanceQuery historicProcessInstanceQuery) {
		long finishedProcesses = historicProcessInstanceQuery.finished().list().size();
		long unfinishedProcesses = historicProcessInstanceQuery.unfinished().list().size();
		HistoricProcessInstance historicProcessInstanceByKermit = historicProcessInstanceQuery.startedBy("kermit").singleResult();
		HistoricProcessInstance historicProcessInstance = historicProcessInstanceQuery
				.variableValueEquals("numberOfDays", "42").singleResult();

		//very buggy
		log("historicProcessInstanceQuery", "finishedProcesses: " + finishedProcesses);
		log("historicProcessInstanceQuery", "unfinishedProcesses: " + unfinishedProcesses);
		log("historicProcessInstanceQuery", "historicProcessInstance.startedBy(): " + historicProcessInstanceByKermit.getId());
		log("historicProcessInstanceQuery", "historicProcessInstance.getId(): " + historicProcessInstance.getId());
	}

	private static void checkHistoricTaskInstanceQuery(HistoricTaskInstanceQuery historicTaskInstanceQuery) {
		Calendar caledar = Calendar.getInstance();
		caledar.set(2016, 10, 1);
		Date firstOfNovember = caledar.getTime();
		HistoricTaskInstance hti = historicTaskInstanceQuery.taskAssignee("gonzo").taskCreatedAfter(firstOfNovember)
				.singleResult();

		log("historicTaskInstanceQuery", "task name: " + hti.getName());
	}

	private static void checkHistoricVariableInstanceQuery(
			HistoricVariableInstanceQuery historicVariableInstanceQuery) {
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		TaskService taskService = engine.getTaskService();
		Task firstTask = taskService.newTask("123");
		taskService.saveTask(firstTask);

		taskService.setVariable(firstTask.getId(), "variable1", "value1");
		taskService.setVariable(firstTask.getId(), "variable1", "value2");
		taskService.setVariable(firstTask.getId(), "variable1", "value3");

		List<HistoricVariableInstance> variables = historicVariableInstanceQuery.variableName("variable1").list();
		
		log("historicVariableInstanceQuery", "variables: " + variables);
	}

	private static void checkNativeHistoricActivityInstanceQuery(
			NativeHistoricActivityInstanceQuery nativeHistoricActivityInstanceQuery) {

		nativeHistoricActivityInstanceQuery.sql("select RES.* from `activiti`.`ACT_HI_ACTINST` RES"
				+ " WHERE RES.PROC_INST_ID_ = \"30\"  order by RES.ID_ asc LIMIT 2147483647 OFFSET 0").list();
	}

	private static void log(String queryName, String message) {
		System.out.println(">>  " + queryName + ": " + message);
	}

}
