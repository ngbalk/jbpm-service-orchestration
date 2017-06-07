package org.rhc.renewals.states;


import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.rhc.renewals.errors.WorkerError;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by nbalkiss on 5/31/17.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="WorkerCallState")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class WorkerCallState {

    @XmlElement(name="Completed")
    @JsonProperty("Completed")
    private boolean completed;

    @XmlElement(name="Errors")
    @JsonProperty("Errors")
    private List<WorkerError> errors;

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<WorkerError> getErrors() {
        return errors;
    }

    public void setErrors(List<WorkerError> errors) {
        this.errors = errors;
    }
}
