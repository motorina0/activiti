package org.activiti.demo.delegate;

import org.activiti.engine.delegate.DelegateTask;

public class ListenToTask {

  public void logUserTaskEvent(DelegateTask delegateTask, String eventName) {
    System.out.println("!!!! task created: " + delegateTask.getId());
  }
}
