package org.bpm.workflow.models;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * Created by nbalkiss on 7/11/17.
 */
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class IncidentData extends DomainData implements Serializable, Copyable{

    static final long serialVersionUID = 156474883874702398L;

    @Column(name = "support_activity_id", length = 128)
    @XmlElement(name="SupportActivityId")
    @JsonProperty("SupportActivityId")
    private String supportActivityId;

    @Column(name = "organization_id", length = 128)
    @XmlElement(name="OrganizationId")
    @JsonProperty("OrganizationId")
    private String organizationId;

    @Column(name = "incident_type", length = 128)
    @XmlElement(name="IncidentType")
    @JsonProperty("IncidentType")
    private String incidentType;

    public IncidentData(String supportActivityId, String organizationId, String incidentType) {
        this.supportActivityId = supportActivityId;
        this.organizationId = organizationId;
        this.incidentType = incidentType;
    }

    public IncidentData(){

    }

    public String getSupportActivityId() {
        return supportActivityId;
    }

    public void setSupportActivityId(String supportActivityId) {
        this.supportActivityId = supportActivityId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    @Override
    public String toString() {
        return "IncidentData{" +
                "id=" + id +
                ", supportActivityId='" + supportActivityId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", incidentType='" + incidentType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IncidentData that = (IncidentData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (supportActivityId != null ? !supportActivityId.equals(that.supportActivityId) : that.supportActivityId != null)
            return false;
        if (organizationId != null ? !organizationId.equals(that.organizationId) : that.organizationId != null)
            return false;
        return incidentType != null ? incidentType.equals(that.incidentType) : that.incidentType == null;

    }

    /**
     * Copy relevant business data from this IncidentData to another IncidentData
     * Will do nothing if other object is null
     * Will do nothing if other object is not of type IncidentData
     * @param other
     */
    @Override
    public boolean copy(Object other) {
        if(other == null || !(other instanceof IncidentData)){
            return false;
        }
        this.setSupportActivityId(((IncidentData) other).getSupportActivityId());
        this.setOrganizationId(((IncidentData) other).getOrganizationId());
        this.setIncidentType(((IncidentData) other).getIncidentType());
        return true;
    }
}
