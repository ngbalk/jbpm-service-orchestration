package org.bpm.workflow.services;

import org.bpm.workflow.common.ServiceRequest;
import org.bpm.workflow.common.StateContext;
import org.bpm.workflow.models.Copyable;
import org.bpm.workflow.common.ServiceResponse;
import org.bpm.workflow.errors.ServiceConfigurationException;
import org.bpm.workflow.errors.ServiceException;
import org.bpm.workflow.errors.WorkerException;
import org.bpm.workflow.states.ServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class ServiceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);

    private static final String TEST_FLAG = "$TEST:";

    private static final String TEST_WORKER = "test-worker";

    private StateContext context;

    public ServiceHandler(StateContext context){

        this.context = context;
    }

    public void execute(ServiceRequest request) throws IllegalStateException, ServiceConfigurationException, ServiceException {

        if(!context.getCurrentState().equals(ServiceState.NOT_STARTED)){

            LOG.warn("Trying to execute service from an illegal state: {} ", context.getCurrentState());

            throw new IllegalStateException("Cannot execute service from state: " + context.getCurrentState().value());
        }

        IService service;

        if(request.getWorkerName().startsWith(TEST_FLAG)){

            LOG.debug("Loading test-worker in TEST mode");

            service = ServiceRegistry.getInstance().getService(TEST_WORKER);
        }
        else{

            if(!ServiceRegistry.getInstance().isInitialized()){

                throw new IllegalStateException("ServiceRegistry has not been properly initialized");
            }

            service = ServiceRegistry.getInstance().getService(request.getWorkerName());
        }

        service.execute(request);

        LOG.debug("Set current state to WAITING");

        context.setCurrentState(ServiceState.WAITING);

    }

    public void complete(ServiceResponse lastServiceResponse) throws IllegalStateException, WorkerException {

        if(context.getCurrentState() == null || !context.getCurrentState().equals(ServiceState.WAITING)){

            LOG.warn("Trying to execute service from an illegal state: {} ", context.getCurrentState());

            throw new IllegalStateException("Cannot complete service from state: " + context.getCurrentState());
        }

        if(lastServiceResponse.getWorkerCallState().isCompleted()){

            final Object newData = lastServiceResponse.getData();

            Object data = context.getData();

            LOG.debug("Data in ServiceResponse is {}", newData == null ? "null" : newData.toString());

            LOG.debug("Current data in process is {}", data == null ? "null" : data.toString());

            if((data != null) && (data instanceof Copyable) && (newData != null) && (newData instanceof Copyable)){

                LOG.debug("Data is Copyable, copying new data into process data");

                ((Copyable) data).copy(newData);

                LOG.debug("Data after copy: {}", data.toString());
            }
            else{

                LOG.debug("Data is not Copyable, replacing current process data with new data");

                data = newData;
            }

            context.setData(data);

            context.setCurrentState(ServiceState.COMPLETED);

            LOG.debug("Set current state to {} ", context.getCurrentState());
        }
        else{

            context.setCurrentState(ServiceState.ERROR);

            LOG.debug("Set current state to {} ", context.getCurrentState());

            throw new WorkerException(lastServiceResponse.getWorkerCallState().getErrors());
        }

    }

}
