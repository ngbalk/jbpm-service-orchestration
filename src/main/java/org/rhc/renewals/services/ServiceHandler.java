package org.rhc.renewals.services;

import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.common.ServiceRequest;
import org.rhc.renewals.common.ServiceResponse;
import org.rhc.renewals.errors.ServiceConfigurationException;
import org.rhc.renewals.errors.ServiceException;
import org.rhc.renewals.errors.WorkerException;
import org.rhc.renewals.states.ServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class ServiceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);

    private RenewalStateContext context;

    public ServiceHandler(RenewalStateContext context){

        this.context = context;
    }

    public void execute(ServiceRequest request) throws IllegalStateException, ServiceConfigurationException, ServiceException {

        if(!context.getCurrentState().equals(ServiceState.NOT_STARTED)){

            LOG.warn("Trying to execute service from an illegal state: {} ", context.getCurrentState());

            throw new IllegalStateException("Cannot execute service from state: " + context.getCurrentState().value());
        }

        if(!SVMServiceRegistry.getInstance().isInitialized()){

            throw new IllegalStateException("SVMServiceRegistry has not been properly initialized");
        }

        ISVMService service = SVMServiceRegistry.getInstance().getService(request.getWorkerName());

        service.execute(request);

        LOG.debug("Set current state to WAITING");

        context.setCurrentState(ServiceState.WAITING);

    }

    public void complete(ServiceResponse lastServiceResponse) throws IllegalStateException, WorkerException {

        if(!context.getCurrentState().equals(ServiceState.WAITING)){

            LOG.warn("Trying to execute service from an illegal state: {} ", context.getCurrentState());

            throw new IllegalStateException("Cannot complete service from state: " + context.getCurrentState().value());
        }

        if(lastServiceResponse.getWorkerCallState().isCompleted()){

            context.setData(lastServiceResponse.getData());

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
