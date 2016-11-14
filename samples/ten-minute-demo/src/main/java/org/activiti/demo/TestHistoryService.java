package org.activiti.demo;

import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricDetailQuery;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.history.NativeHistoricActivityInstanceQuery;

public class TestHistoryService {
	public static void main(String[] args) {
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();

		HistoryService historyService = engine.getHistoryService();

		checkHistoricActivityInstanceQuery(historyService.createHistoricActivityInstanceQuery());
		/*
		 * checkHistoricDetailQuery(historyService.createHistoricDetailQuery());
		 * checkHistoricProcessInstanceQuery(historyService.
		 * createHistoricProcessInstanceQuery());
		 * checkHistoricTaskInstanceQuery(historyService.
		 * createHistoricTaskInstanceQuery());
		 * checkHistoricVariableInstanceQuery(historyService.
		 * createHistoricVariableInstanceQuery());
		 * 
		 * checkNativeHistoricActivityInstanceQuery(historyService.
		 * createNativeHistoricActivityInstanceQuery());
		 */
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
		// from the UI perspective

	}

	private static void checkHistoricProcessInstanceQuery(HistoricProcessInstanceQuery historicProcessInstanceQuery) {

	}

	private static void checkHistoricTaskInstanceQuery(HistoricTaskInstanceQuery historicTaskInstanceQuery) {
		// TODO Auto-generated method stub

	}

	private static void checkHistoricVariableInstanceQuery(
			HistoricVariableInstanceQuery historicVariableInstanceQuery) {
		// TODO Auto-generated method stub

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
