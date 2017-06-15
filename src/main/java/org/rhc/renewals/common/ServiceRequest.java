package org.rhc.renewals.common;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created by nbalkiss on 5/16/17.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceRequest {

    @XmlElement(name="Data")
    @JsonProperty("Data")
    private Map<String, String> data;

    @XmlElement(name="WorkerName")
    @JsonProperty("WorkerName")
    private String workerName;

    @XmlElement(name="SignalInstanceInfo")
    @JsonProperty("SignalInstanceInfo")
    private SignalInstanceInfo signalInstanceInfo;

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public SignalInstanceInfo getSignalInstanceInfo() {
        return signalInstanceInfo;
    }

    public void setSignalInstanceInfo(SignalInstanceInfo signalInstanceInfo) {
        this.signalInstanceInfo = signalInstanceInfo;
    }
}
