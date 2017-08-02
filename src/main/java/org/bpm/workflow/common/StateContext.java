package org.bpm.workflow.common;

import org.bpm.workflow.states.ServiceState;

/**
 * Created by nbalkiss on 5/10/17.
 */
public class StateContext {

    private ServiceState currentState;

    private Object data;

    public StateContext(Object data, ServiceState currentState) {
        this.data = data;
        this.currentState = currentState;
    }

    public ServiceState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ServiceState currentState) {
        this.currentState = currentState;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
