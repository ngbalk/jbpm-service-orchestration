package org.rhc.renewals.errors;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by nbalkiss on 5/31/17.
 */

@XmlRootElement
@XmlType(name="Severity")
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
