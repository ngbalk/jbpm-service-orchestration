package org.rhc.renewals.common;

import org.rhc.renewals.states.ServiceState;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class ServiceResponse extends AbstractServiceRequestResponse {

    private ServiceState svcState;

    private String message;

    public ServiceState getSvcState() {
        return svcState;
    }

    public void setSvcState(ServiceState svcState) {
        this.svcState = svcState;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
