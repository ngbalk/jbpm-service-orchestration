package org.rhc.renewals.services;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Created by nbalkiss on 5/10/17.
 */
public class SVMService {

    private static final Logger LOG = LoggerFactory.getLogger(SVMService.class);

    private ResteasyWebTarget webTarget;

    public SVMService(SVMServiceConfig config){

        LOG.debug("Configuring service with configuration: " + config.toString());

        this.webTarget = WebRequestInvocationBuilder.create(config.getUrl())
                .setTimeout(config.getTimeout())
                .addAuthToken(config.getToken())
                .addUsernameAndPassword(config.getUsername(),config.getPassword()).buildPost();
    }

    public Response execute(Map<String,String> requestParams){

        LOG.debug("Invoking service call");

        Entity entity = Entity.entity(requestParams, MediaType.APPLICATION_JSON);

        Response response = webTarget.request().post(entity);

        LOG.debug("Service call returned with code: " + response.getStatus());

        return response;
    }
}
