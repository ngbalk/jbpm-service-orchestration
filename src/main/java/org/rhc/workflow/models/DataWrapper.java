package org.rhc.workflow.models;

import org.drools.persistence.jpa.marshaller.VariableEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nbalkiss on 6/29/17.
 */
@Entity
public class DataWrapper extends VariableEntity implements Serializable {

    private static final long serialVersionUID = 523l;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Access(AccessType.PROPERTY)
    @ElementCollection(targetClass=String.class)
    @MapKeyColumn(name = "field_name", length = 64)
    @Column(name = "field_val", length = 128)
    private Map<String, String> data = new HashMap<>();

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
