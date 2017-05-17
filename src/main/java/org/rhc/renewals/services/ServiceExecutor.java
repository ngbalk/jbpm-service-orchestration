package org.rhc.renewals.services;

import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.common.ServiceRequest;
import org.rhc.renewals.common.ServiceResponse;
import org.rhc.renewals.states.ServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class ServiceExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceExecutor.class);

    private RenewalStateContext context;

    public ServiceExecutor(RenewalStateContext context){

        this.context = context;
    }

    public void execute(ServiceRequest request) throws Exception {

        if(!context.getCurrentState().equals(ServiceState.NOT_STARTED)){

            LOG.warn("Trying to execute service from an illegal state: " + context.getCurrentState());

            throw new IllegalStateException("Cannot execute service from state: " + context.getCurrentState().value());
        }

        SVMService service = SVMServiceRegistry.getInstance().getService(request.getSvcName());

        service.execute(request);


        LOG.debug("Set current state to WAITING");

        context.setCurrentState(ServiceState.WAITING);

    }

    public void complete(ServiceResponse lastServiceResponse) throws IllegalStateException {

        if(!context.getCurrentState().equals(ServiceState.WAITING)){

            LOG.warn("Trying to execute service from an illegal state: " + context.getCurrentState());

            throw new IllegalStateException("Cannot complete service from state: " + context.getCurrentState().value());
        }

        if(!lastServiceResponse.getSvcState().equals(ServiceState.ERROR)){

            context.setData(lastServiceResponse.getData());
        }

        LOG.debug("Set current state to " + lastServiceResponse.getSvcState());

        context.setCurrentState(lastServiceResponse.getSvcState());

    }
}
