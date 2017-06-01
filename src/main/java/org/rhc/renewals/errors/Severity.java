package org.rhc.renewals.errors;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by nbalkiss on 5/31/17.
 */
public enum Severity {

    Warning("Warning"),
    Severe("Severe"),
    Critical("Critical");

    private String value;

    Severity(String value){
        this.value = value;
    }

    public String value(){
        return value;
    }
}
