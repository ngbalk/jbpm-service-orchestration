package org.rhc.renewals.exceptions;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class ServiceRESTException extends Exception{

    private int statusCode;

    public ServiceRESTException(String message, int statusCode){
        super("Service call failed with status code " + statusCode, new Throwable(statusCode+" "+message));
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
