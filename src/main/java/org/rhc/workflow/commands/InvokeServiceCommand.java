package org.rhc.workflow.commands;

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
import org.rhc.workflow.common.ProcessStateCommandFactory;
import org.rhc.workflow.common.RequestBuilder;
import org.rhc.workflow.common.ServiceRequest;
import org.rhc.workflow.common.StateContext;
import org.rhc.workflow.services.ServiceHandler;
import org.rhc.workflow.states.ServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by nbalkiss on 5/17/17.
 */
public class InvokeServiceCommand implements Command{

    private static final Logger LOG = LoggerFactory.getLogger(InvokeServiceCommand.class);

    private static final String WORKITEM = "workItem";

    private static final String PROCESS_INSTANCE_ID = "processInstanceId";

    private static final String DEPLOYMENT_ID = "deploymentId";

    private static final String SERVICE_NAME = "serviceName";

    private static final String CALLBACK_SIGNAL_NAME = "callbackSignalName";

    private static final String DATA = "data";

    private static final String BUSINESS_KEY = "businessKey";

    @Override
    // TODO Method is too big. Split into multiple methods
    public ExecutionResults execute(CommandContext ctx) throws Exception {

        WorkItem workItem = (WorkItem) ctx.getData(WORKITEM);

        Long processInstanceId = (Long)ctx.getData(PROCESS_INSTANCE_ID);

        String deploymentId = (String) ctx.getData(DEPLOYMENT_ID);

        validateWorkItemParameters(workItem);

        String serviceName = (String) workItem.getParameter(SERVICE_NAME);

        String signalName = (String) workItem.getParameter(CALLBACK_SIGNAL_NAME);

        Object data = workItem.getParameter(DATA);

        if(data == null){

            data = new Object();

            LOG.warn("Domain data object is null, this could cause problems");
        }

        RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);

        if(runtimeManager == null) {
            throw new Exception("RuntimeManager is null");
        }

        RuntimeEngine engine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));

        KieSession kieSession = engine.getKieSession();

        ProcessInstanceImpl processInstance = (ProcessInstanceImpl) kieSession.getProcessInstance(processInstanceId);

        if(processInstance == null) {
            throw new Exception("Process Instance with ID " + processInstanceId + " is null");
        }

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(data)
                        .addServiceName(serviceName)
                        .addContainerId(deploymentId)
                        .addProcessInstanceId(processInstanceId)
                        .addSignalName(signalName)
                        .buildRequest();

        StateContext stateContext = new StateContext(data, ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(stateContext);

        if(processInstance.getState() == ProcessInstance.STATE_SUSPENDED){

            LOG.debug("RESUMING process with id {} in state SUSPENDED", processInstanceId);

            kieSession.execute(ProcessStateCommandFactory.getCommand(ResumeProcessInstanceCommand.class, processInstanceId));
        }

        try{
            executor.execute(request);
        }
        catch(Exception e){

            LOG.warn("Service call {} failed while executing process ( with id {} )", serviceName, processInstanceId);

            ExecutorService executorService = ExecutorServiceFactory.newExecutorService();

            List<RequestInfo> requestsByBusinessKey = executorService.getRequestsByBusinessKey((String) ctx.getData(BUSINESS_KEY), new QueryContext());

            RequestInfo requestInfo = requestsByBusinessKey.get(0);

            if(requestInfo.getRetries() > 0){

                LOG.debug("Set process ( with id {} ) state to SUSPENDED", processInstanceId);

                kieSession.execute(ProcessStateCommandFactory.getCommand(SuspendProcessInstanceCommand.class, processInstanceId));

                throw e;
            }
            else{

                LOG.debug("Set process ( with id {} ) final state to ABORTED", processInstanceId);

                kieSession.execute(ProcessStateCommandFactory.getCommand(AbortProcessInstanceCommand.class, processInstanceId));

                return null;
            }
        }

        ExecutionResults results = new ExecutionResults();

        results.setData("state", stateContext.getCurrentState());

        return results;
    }

    private void validateWorkItemParameters(WorkItem workItem) {

        Object serviceName = workItem.getParameter(SERVICE_NAME);

        if(serviceName == null || !(serviceName instanceof String)){

            throw new IllegalArgumentException("Parameter 'serviceName' in AsyncWorkItemHandler is null or is not of type String");
        }

        Object signalName = workItem.getParameter(CALLBACK_SIGNAL_NAME);

        if(signalName == null || !(signalName instanceof String)){

            throw new IllegalArgumentException("Parameter 'callbackSignalName' in AsyncWorkItemHandler is null or is not of type String");
        }
    }
}
