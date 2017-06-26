package org.rhc.workflow.states;

/**
 * Created by nbalkiss on 5/10/17.
 */
public enum ServiceState {

    NOT_STARTED("NOT_STARTED"),
    WAITING("WAITING"),
    COMPLETED("COMPLETED"),
    ERROR("ERROR");

    private String value;

    ServiceState(String value){
        this.value = value;
    }

    public String value(){
        return value;
    }

}
