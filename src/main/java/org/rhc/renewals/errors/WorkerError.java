package org.rhc.renewals.errors;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by nbalkiss on 5/31/17.
 */
public class WorkerError {

    @JsonProperty("ErrorID")
    private String errorId;

    @JsonProperty("Severity")
    private Severity severity;

    @JsonProperty("Description")
    private String description;

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public org.rhc.renewals.errors.Severity getSeverity() {
        return severity;
    }

    public void setSeverity(org.rhc.renewals.errors.Severity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "WorkerError{" +
                "errorId='" + errorId + '\'' +
                ", severity=" + severity +
                ", description='" + description + '\'' +
                '}';
    }
}
