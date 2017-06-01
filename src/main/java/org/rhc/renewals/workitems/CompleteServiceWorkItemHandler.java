package org.rhc.renewals.workitems;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.common.ServiceResponse;
import org.rhc.renewals.services.ServiceHandler;
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

        ServiceState state = (ServiceState) workItem.getParameter("state");

        Map<String, String> data = (Map<String,String>) workItem.getParameter("data");

        ServiceResponse lastServiceResponse = (ServiceResponse) workItem.getParameter("lastServiceResponse");

        RenewalStateContext context = new RenewalStateContext(data,state);

        ServiceHandler handler = new ServiceHandler(context);

        try{
            handler.complete(lastServiceResponse);
            parameters.put("data",context.getData());
            parameters.put("state",context.getCurrentState());
        }
        catch(Exception e){
            handleException(e);
        }

        manager.completeWorkItem(workItem.getId(), parameters);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

    }
}
