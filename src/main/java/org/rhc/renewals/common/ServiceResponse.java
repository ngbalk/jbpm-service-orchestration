package org.rhc.renewals.common;

import org.codehaus.jackson.annotate.JsonProperty;
import org.rhc.renewals.states.WorkerCallState;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by nbalkiss on 5/16/17.
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceResponse extends AbstractServiceRequestResponse {

    @JsonProperty("Message")
    private String message;

    @JsonProperty("WorkerCallState")
    private WorkerCallState workerCallState;

    public WorkerCallState getWorkerCallState() {
        return workerCallState;
    }

    public void setWorkerCallState(WorkerCallState workerCallState) {
        this.workerCallState = workerCallState;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
