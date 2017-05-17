package org.rhc.renewals.common;

import org.rhc.renewals.states.ServiceState;

import java.util.Map;

/**
 * Created by nbalkiss on 5/10/17.
 */
public class RenewalStateContext {

    private ServiceState currentState;

    private Map<String, String> data;

    public RenewalStateContext(Map<String, String> data, ServiceState currentState) {
        this.data = data;
        this.currentState = currentState;
    }

    public ServiceState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ServiceState currentState) {
        this.currentState = currentState;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
