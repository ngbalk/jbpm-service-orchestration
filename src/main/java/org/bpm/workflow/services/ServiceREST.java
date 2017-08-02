package org.bpm.workflow.services;

import org.bpm.workflow.common.ServiceRequest;
import org.bpm.workflow.config.ServiceConfig;
import org.bpm.workflow.errors.ServiceRESTException;
import org.bpm.workflow.errors.ServiceConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by nbalkiss on 5/10/17.
 */

public class ServiceREST implements IService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceREST.class);

    public static final int HTTP_STATUS_OK = 200;

    public static final int HTTP_STATUS_ACCEPTED = 202;

    private ServiceConfig config;

    private Invocation invocation;

    public ServiceREST(ServiceConfig config) throws ServiceConfigurationException{

        this.config = config;

        LOG.debug("Configuring service with configuration: {} " , config.toString());

        if(config.getUrl()==null){

            throw new ServiceConfigurationException("Service configuration url is null");
        }
    }

    public void execute(ServiceRequest request) throws ServiceConfigurationException, ServiceRESTException {

        validateRequestParameters(request);

        Entity entity = Entity.entity(request, MediaType.APPLICATION_JSON);

        this.invocation = WebRequestInvocationBuilder.create(config.getUrl())
                .setTimeout(config.getTimeout())
                .addAuthToken(config.getToken())
                .addUsernameAndPassword(config.getUsername(),config.getPassword())
                .addEntity(entity)
                .buildPost();

        int invokeTimes = this.config.getRetryTimes() + 1;

        Response response = null;

        for(int i=0;i<invokeTimes;i++){

            try {

                LOG.debug("Invoking service call");

                response = invocation.invoke();

                if(response == null){

                    LOG.error("Response returned null");

                    throw new ServiceRESTException("Response returned null");
                }

                if(response.getStatus() == HTTP_STATUS_OK || response.getStatus() == HTTP_STATUS_ACCEPTED){

                    LOG.debug("Service call successfully invoked with code: {} ", response.getStatus());

                    return;
                }

                LOG.warn("Service call failed with status code {} \nRetrying {} more times", response.getStatus(), config.getRetryTimes()-i);
            }
            finally {

                if(response!=null) response.close();
            }


        }

        LOG.warn("{} service calls failed with status {} ", config.getUrl(), response.getStatus());

        throw new ServiceRESTException(response.getStatusInfo().getReasonPhrase(), response.getStatus());

    }

    private void validateRequestParameters(ServiceRequest request){
        if(request.getSignalInstanceInfo() == null){

            throw new ServiceConfigurationException("SignalInstanceInfo is not set");
        }
        if(request.getSignalInstanceInfo().getSignalName() == null){

            throw new ServiceConfigurationException("callbackSignalName is not set - Cannot invoke request without callbackSignalName");
        }
        if(request.getWorkerName() == null){

            throw new ServiceConfigurationException("WorkerName for ServiceRequest is null");
        }
        if(request.getSignalInstanceInfo().getContainerId() == null){

            LOG.warn("ContainerId for callback is null - Are you sure? Worker may not be able to call back without that information");
        }
        if(request.getSignalInstanceInfo().getProcessInstanceId() == null){

            LOG.warn("ProcessInstanceId for callback is null - Are you sure? Worker may not be able to call back without that information");
        }
    }

}
