package org.rhc.workflow.states;


import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.rhc.workflow.errors.WorkerError;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by nbalkiss on 5/31/17.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="WorkerCallState")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class WorkerCallState implements Serializable{

    static final long serialVersionUID = 6329230923648192L;

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
