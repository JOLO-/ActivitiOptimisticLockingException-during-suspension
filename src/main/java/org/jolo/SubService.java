package org.jolo;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import static org.jolo.Main.log;

@Service
public class SubService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String executionToken = execution.getProcessDefinitionId() + ":" + execution.getProcessInstanceId() + "\t" + execution.getCurrentActivityName();
        log(executionToken);

        /**
         * Wait to ensure current service task is in progress when we trying to suspend main process
         */
        for(int i = 0; i < 10; i++) {
            log(executionToken + " is proceeding now...");
            Thread.currentThread().sleep(1000);
        }
    }
}
