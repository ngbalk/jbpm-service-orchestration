package org.bpm.workflow.services;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.Headers;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * Request builder
 */
public class WebRequestInvocationBuilder {

    private final String url;
    private String username;
    private String password;
    private String authToken;
    private int timeout;
    private Entity entity;

    private WebRequestInvocationBuilder(final String url) {

        this.url = url;
    }

    /**
     * Creates builder
     *
     * @param url
     * @return
     */
    public static WebRequestInvocationBuilder create(final String url) {

        final WebRequestInvocationBuilder retVal = new WebRequestInvocationBuilder(url);

        return  retVal;
    }

    public WebRequestInvocationBuilder addAuthToken(final String authToken) {

        this.authToken = authToken;

        return this;
    }

    public WebRequestInvocationBuilder setTimeout(int timeout){

        this.timeout = timeout;

        return this;
    }

    public WebRequestInvocationBuilder addUsernameAndPassword(String username, String password){

        this.username = username;

        this.password = password;

        return this;
    }

    public WebRequestInvocationBuilder addEntity(Entity entity){

        this.entity = entity;

        return this;
    }

    /**
     * Builds POST invocation
     *
     * @return
     */
    public Invocation buildPost() {

        final ResteasyClientBuilder builder = new ResteasyClientBuilder();

        builder.socketTimeout(timeout, TimeUnit.MILLISECONDS);

        final ResteasyClient client = builder.build();

        final ResteasyWebTarget target  = client.target(this.url);

        final MultivaluedMap<String, Object>  headers = new Headers();

        // basic auth
        if(this.username != null && this.password != null){

            String credentials = Base64.getEncoder().encodeToString((username+":"+password).getBytes());

            headers.put("Authorization", Arrays.asList(new String[]{credentials}));
        }

        // token auth
        else if (this.authToken != null ) {

            headers.put("Authorization", Arrays.asList(new String[]{this.authToken}));
        }

        return target.request().headers(headers).buildPost(this.entity);

    }
}
