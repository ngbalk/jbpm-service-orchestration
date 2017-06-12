package org.rhc.renewals.services;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.rhc.renewals.common.ServiceRequest;
import org.rhc.renewals.errors.ServiceRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by nbalkiss on 5/10/17.
 */

// TODO Add ISVMService to interface and use interface
// TODO Change to  SVMServiceREST to support rest
public class SVMService {

    private static final Logger LOG = LoggerFactory.getLogger(SVMService.class);

    public static final int HTTP_STATUS_OK = 200;

    private SVMServiceConfig config;

    private ResteasyWebTarget webTarget;

    public SVMService(SVMServiceConfig config){

        this.config = config;

        LOG.debug("Configuring service with configuration: {} " , config.toString());

        this.webTarget = WebRequestInvocationBuilder.create(config.getUrl())
                .setTimeout(config.getTimeout())
                .addAuthToken(config.getToken())
                .addUsernameAndPassword(config.getUsername(),config.getPassword()).buildPost();
    }

    public Response execute(ServiceRequest request) throws ServiceRESTException, InterruptedException{

        LOG.debug("Invoking service call");

        Entity entity = Entity.entity(request, MediaType.APPLICATION_JSON);

        int invokeTimes = this.config.getRetryTimes() + 1;

        Response response = null;

        for(int i=0;i<invokeTimes;i++){

            response = webTarget.request().post(entity);

            response.close();

            if(response.getStatus() == HTTP_STATUS_OK){

                LOG.debug("Service call successfully invoked with code: {} ", response.getStatus());

                return response;

            }

            LOG.warn("Service call failed with status code {} \nRetrying {} more times", response.getStatus(), config.getRetryTimes()-i);

        }

        LOG.warn("{} service calls failed with status {} ", config.getUrl(), response.getStatus());

        throw new ServiceRESTException(response.getStatusInfo().getReasonPhrase(), response.getStatus());

    }
}
