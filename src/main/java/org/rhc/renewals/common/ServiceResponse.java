package org.rhc.renewals.common;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.kie.api.remote.Remotable;
import org.rhc.renewals.states.WorkerCallState;

import javax.xml.bind.annotation.*;

/**
 * Created by nbalkiss on 5/16/17.
 */

@Remotable
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ServiceResponse")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ServiceResponse extends AbstractServiceRequestResponse{

    @XmlElement(name="Message")
    @JsonProperty("Message")
    private String message;

    @XmlElement(name="WorkerCallState")
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
