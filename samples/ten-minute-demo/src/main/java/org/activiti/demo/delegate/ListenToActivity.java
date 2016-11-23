package org.activiti.demo.delegate;

import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

public class ListenToActivity {

  public void logActivityStartEvent(ActivityExecution execution) {
    System.out.println("Activity  " + execution.getCurrentActivityName() + ": " + execution.getProcessInstance().getCurrentActivityId());
  }
}
