package org.jolo;


import org.activiti.engine.*;
import org.activiti.engine.impl.cmd.SuspendProcessInstanceCmd;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static ProcessInstance processInstance;
    public final static int DELAY_BEFORE_SUSPEND = 5000;
    public static final String SIGNAL = "launchSubprocessSignal";
    public static final String MAIN_PROCESS = "mainProcess";

    /**
     * Activiti services
     */
    private static ClassPathXmlApplicationContext applicationContext;
    private static RuntimeService runtimeService;
    private static TaskService taskService;

    static {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        runtimeService = (RuntimeService) applicationContext.getBean("runtimeService");
        taskService = (TaskService) applicationContext.getBean("taskService");
    }

	public static void main(String[] args) throws  Exception {
        if (!areSuspendedProcessInstances()) {
            //launch main process
            processInstance = runtimeService.startProcessInstanceByKey(MAIN_PROCESS);

            //launch asynchronous subprocess of the main process
            Execution execution = runtimeService.createExecutionQuery()
                                                .signalEventSubscriptionName(SIGNAL)
                                                .processInstanceId(processInstance.getId())
                                                .singleResult();
            runtimeService.signalEventReceived(SIGNAL, execution.getId());
        } else {
            activateSuspendedProcesses();
        }

        /**
         * Wait to ensure that SubService is in progress. SubService is a service task of asynchronous subProcess
         */
        Thread.currentThread().sleep(DELAY_BEFORE_SUSPEND);

        suspendActiveProcesses();
    }

    private  static boolean areSuspendedProcessInstances() {
        log("areSuspendedProcessInstances");
        return !runtimeService.createProcessInstanceQuery().suspended().list().isEmpty();
    }

    private static void activateSuspendedProcesses() {
        log("activateSuspendedProcesses");
        List<ProcessInstance> suspendedProcesses = runtimeService.createProcessInstanceQuery().suspended().list();
        for (ProcessInstance processInstance : suspendedProcesses) {
            log("Activate process instance with id " + processInstance.getProcessInstanceId());
            runtimeService.activateProcessInstanceById(processInstance.getProcessInstanceId());
        }
    }

    private static void suspendActiveProcesses() {
        log("suspendActiveProcesses");
        runtimeService.suspendProcessInstanceById(processInstance.getProcessInstanceId());
        ProcessEngines.destroy();
    }

    public synchronized static void log(String msg) {
        System.out.println(msg);
    }
}
