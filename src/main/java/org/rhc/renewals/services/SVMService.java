package org.rhc.renewals.services;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.rhc.renewals.common.ServiceRequest;
import org.rhc.renewals.exceptions.ServiceRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by nbalkiss on 5/10/17.
 */
public class SVMService {

    private static final Logger LOG = LoggerFactory.getLogger(SVMService.class);

    public static final int HTTP_STATUS_OK = 200;

    private ResteasyWebTarget webTarget;

    public SVMService(SVMServiceConfig config){

        LOG.debug("Configuring service with configuration: " + config.toString());

        this.webTarget = WebRequestInvocationBuilder.create(config.getUrl())
                .setTimeout(config.getTimeout())
                .addAuthToken(config.getToken())
                .addUsernameAndPassword(config.getUsername(),config.getPassword()).buildPost();
    }

    public Response execute(ServiceRequest request) throws ServiceRESTException{

        LOG.debug("Invoking service call");

        Entity entity = Entity.entity(request, MediaType.APPLICATION_JSON);

        Response response = webTarget.request().post(entity);

        if(response.getStatus() != HTTP_STATUS_OK){

            LOG.warn("Service call failed with status code " + response.getStatus());

            throw new ServiceRESTException(response.getStatusInfo().getReasonPhrase(), response.getStatus());
        }

        LOG.debug("Service call invoked with code: " + response.getStatus());

        return response;
    }
}
