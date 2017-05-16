package org.rhc.renewals.common;

import org.rhc.renewals.states.RenewalState;

import java.util.Map;

/**
 * Created by nbalkiss on 5/10/17.
 */
public class RenewalStateContext {

    private RenewalState currentState;

    private Map<String, String> data;

    public RenewalStateContext(Map<String, String> data, RenewalState currentState) {
        this.data = data;
        this.currentState = currentState;
    }

    public RenewalState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(RenewalState currentState) {
        this.currentState = currentState;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
