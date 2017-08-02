package org.bpm.workflow.common;

import org.kie.api.command.Command;
import org.kie.internal.command.ProcessInstanceIdCommand;

/**
 * Created by nbalkiss on 5/25/17.
 */
public class ProcessStateCommandFactory {

    public static Command getCommand(Class<? extends ProcessInstanceIdCommand> className, Long processInstanceId){
        ProcessInstanceIdCommand command = null;
        try {
            command = (ProcessInstanceIdCommand) className.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        command.setProcessInstanceId(processInstanceId);
        return (Command) command;
    }
}
