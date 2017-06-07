package org.rhc.renewals.common;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;

/**
 * Created by nbalkiss on 5/16/17.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractServiceRequestResponse {

    @XmlElement(name="Data")
    @JsonProperty("Data")
    private HashMap<String, String> data;

    @XmlElement(name="WorkerName")
    @JsonProperty("WorkerName")
    private String workerName;

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }
}
