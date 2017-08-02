package org.bpm.workflow.models;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.drools.persistence.jpa.marshaller.VariableEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * Created by nbalkiss on 7/14/17.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class DomainData extends VariableEntity implements Serializable{

    static final long serialVersionUID = 8790987634L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "domain_data_id")
    @JsonIgnore
    public Long id;

    public DomainData() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
