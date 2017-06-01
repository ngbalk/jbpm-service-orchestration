package org.rhc.renewals.workitems;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.common.RequestBuilder;
import org.rhc.renewals.common.ServiceRequest;
import org.rhc.renewals.services.ServiceHandler;
import org.rhc.renewals.states.ServiceState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class InvokeServiceAndWaitWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        logThrownException = false;

        String serviceName = (String) workItem.getParameter("serviceName");

        HashMap<String, String> data = (HashMap<String,String>) workItem.getParameter("data");

        if(data==null){
            data = new HashMap<>();
        }

        ServiceRequest request =
                RequestBuilder.get()
                    .addData(data)
                    .addCallBackUrl("")
                    .addServiceName(serviceName)
                    .buildRequest();

        RenewalStateContext stateContext = new RenewalStateContext(data, ServiceState.NOT_STARTED);

        ServiceHandler handler = new ServiceHandler(stateContext);

        try{
            handler.execute(request);
        }
        catch(Exception e){
            handleException(e);
        }

        workItem.getParameters().put("state", stateContext.getCurrentState());

        manager.completeWorkItem(workItem.getId(), workItem.getParameters());

    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

    }
}
