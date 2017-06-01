package org.rhc.renewals.states;

import org.codehaus.jackson.annotate.JsonProperty;
import org.rhc.renewals.errors.WorkerError;

import java.util.List;

/**
 * Created by nbalkiss on 5/31/17.
 */
public class WorkerCallState {

    @JsonProperty("Completed")
    private boolean completed;

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
