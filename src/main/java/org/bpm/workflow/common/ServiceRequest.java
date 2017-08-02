package org.bpm.workflow.common;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by nbalkiss on 5/16/17.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ServiceRequest {

    @XmlElement(name="Data")
    @JsonProperty("Data")
    private Object data;

    @XmlElement(name="DataType")
    @JsonProperty("DataType")
    private String dataType;

    @XmlElement(name="WorkerName")
    @JsonProperty("WorkerName")
    private String workerName;

    @XmlElement(name="SignalInstanceInfo")
    @JsonProperty("SignalInstanceInfo")
    private SignalInstanceInfo signalInstanceInfo;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceRequest that = (ServiceRequest) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        if (dataType != null ? !dataType.equals(that.dataType) : that.dataType != null) return false;
        if (workerName != null ? !workerName.equals(that.workerName) : that.workerName != null) return false;
        return signalInstanceInfo != null ? signalInstanceInfo.equals(that.signalInstanceInfo) : that.signalInstanceInfo == null;

    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (workerName != null ? workerName.hashCode() : 0);
        result = 31 * result + (signalInstanceInfo != null ? signalInstanceInfo.hashCode() : 0);
        return result;
    }
}
