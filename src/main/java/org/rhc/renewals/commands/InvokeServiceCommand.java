package org.rhc.renewals.commands;

import org.drools.core.command.runtime.process.AbortProcessInstanceCommand;
import org.drools.core.process.instance.WorkItem;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.process.instance.command.ResumeProcessInstanceCommand;
import org.jbpm.process.instance.command.SuspendProcessInstanceCommand;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.executor.*;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.rhc.renewals.common.ProcessStateCommandFactory;
import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.common.RequestBuilder;
import org.rhc.renewals.common.ServiceRequest;
import org.rhc.renewals.services.ServiceHandler;
import org.rhc.renewals.states.ServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nbalkiss on 5/17/17.
 */
public class InvokeServiceCommand implements Command{

    private static final Logger LOG = LoggerFactory.getLogger(InvokeServiceCommand.class);

    @Override
    // TODO Method is to big. Split into multiple methods
    public ExecutionResults execute(CommandContext ctx) throws Exception {

        // TODO Move names to constants
        WorkItem workItem = (WorkItem) ctx.getData("workItem");

        Long processInstanceId = (Long)ctx.getData("processInstanceId");

        String deploymentId = (String) ctx.getData("deploymentId");

        // TODO Add validation logic for parameters
        RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);

        // TODO Check runtime manager
        RuntimeEngine engine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));

        KieSession ksession = engine.getKieSession();

        ProcessInstanceImpl processInstance = (ProcessInstanceImpl) ksession.getProcessInstance(processInstanceId);

        // TODO Check processInstance != null

        // Move to method beginning abd add validation logic
        String serviceName = (String) workItem.getParameter("serviceName");

        // TODO Use interface type
        HashMap<String, String> data = (HashMap<String,String>) workItem.getParameter("data");

        // TODO Use inline if
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

        ServiceHandler executor = new ServiceHandler(stateContext);

        if(processInstance.getState() == ProcessInstance.STATE_SUSPENDED){

            LOG.debug("RESUMING process with id {} in state SUSPENDED", processInstanceId);

            ksession.execute(ProcessStateCommandFactory.getCommand(ResumeProcessInstanceCommand.class, processInstanceId));
        }

        try{
            executor.execute(request);
        }
        catch(Exception e){



            LOG.warn("Service call {} failed while executing process ( with id {} )", serviceName, processInstanceId);

            ExecutorService executorService = ExecutorServiceFactory.newExecutorService();

            List<RequestInfo> requestsByBusinessKey = executorService.getRequestsByBusinessKey((String) ctx.getData("businessKey"), new QueryContext());

            RequestInfo requestInfo = requestsByBusinessKey.get(0);

            if(requestInfo.getRetries() > 0){

                LOG.debug("Set process ( with id {} ) state to SUSPENDED", processInstanceId);

                ksession.execute(ProcessStateCommandFactory.getCommand(SuspendProcessInstanceCommand.class, processInstanceId));

                throw e;
            }
            else{

                LOG.debug("Set process ( with id {} ) final state to ABORTED", processInstanceId);

                ksession.execute(ProcessStateCommandFactory.getCommand(AbortProcessInstanceCommand.class, processInstanceId));

                return null;
            }
        }

        ExecutionResults results = new ExecutionResults();

        results.setData("state", stateContext.getCurrentState());

        return results;
    }
}
