package org.rhc.renewals.common;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by nbalkiss on 6/14/17.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SignalInstanceInfo {

    public SignalInstanceInfo(){

    }

    public SignalInstanceInfo(String containerId, Long processInstanceId, String signalName) {
        this.containerId = containerId;
        this.processInstanceId = processInstanceId;
        this.signalName = signalName;
    }

    @XmlElement(name="ContainerId")
    @JsonProperty("ContainerId")
    private String containerId;

    @XmlElement(name="ProcessInstanceId")
    @JsonProperty("ProcessInstanceId")
    private Long processInstanceId;

    @XmlElement(name="SignalName")
    @JsonProperty("SignalName")
    private String signalName;

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }
}
