package org.rhc.workflow.workitems;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.rhc.workflow.common.ServiceResponse;
import org.rhc.workflow.common.StateContext;
import org.rhc.workflow.services.ServiceHandler;
import org.rhc.workflow.states.ServiceState;

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

        Map<String, Object> data = (Map<String,Object>) workItem.getParameter("data");

        ServiceResponse lastServiceResponse = (ServiceResponse) workItem.getParameter("lastServiceResponse");

        StateContext context = new StateContext(data,state);

        ServiceHandler handler = new ServiceHandler(context);

        try{
            handler.complete(lastServiceResponse);

            parameters.put("data", context.getData());

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
