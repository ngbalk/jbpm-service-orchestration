package org.rhc.workflow.errors;

import java.util.List;

/**
 * Created by nbalkiss on 5/31/17.
 */
public class WorkerException extends Exception {

    public WorkerException(List<WorkerError> errors){
        super(buildMessage(errors));
    }

    private static String buildMessage(List<WorkerError> errors){
        StringBuilder sb = new StringBuilder();
        sb.append("Worker failed with " + errors.size() + " errors: \n");
        for(WorkerError error : errors){
            sb.append(error.toString()+"\n");
        }
        return sb.toString();
    }
}
