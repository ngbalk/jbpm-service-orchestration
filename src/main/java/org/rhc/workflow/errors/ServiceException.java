package org.rhc.workflow.errors;

/**
 * Created by nbalkiss on 6/12/17.
 */
public class ServiceException extends Exception {

    public ServiceException(String message){
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
