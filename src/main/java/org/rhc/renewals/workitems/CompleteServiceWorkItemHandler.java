package org.rhc.renewals.workitems;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.common.ServiceResponse;
import org.rhc.renewals.services.ServiceExecutor;
import org.rhc.renewals.states.ServiceState;

import java.util.Map;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class CompleteServiceWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        logThrownException = false;

        Map<String, Object> parameters = workItem.getParameters();

        ServiceState state = (ServiceState) parameters.get("state");

        Map<String, String> data = (Map<String,String>) parameters.get("data");

        ServiceResponse lastServiceResponse = (ServiceResponse) parameters.get("lastServiceResponse");

        RenewalStateContext context = new RenewalStateContext(data,state);

        ServiceExecutor executor = new ServiceExecutor(context);

        try{
            executor.complete(lastServiceResponse);
            parameters.put("data",context.getData());
            parameters.put("state",context.getCurrentState());
        }
        catch(IllegalStateException e){
            handleException(e);
        }

        manager.completeWorkItem(workItem.getId(), parameters);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

    }
}
