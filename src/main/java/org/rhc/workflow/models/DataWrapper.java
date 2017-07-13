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
    private Map<String, Object> data = new HashMap<String, Object>();

    public DataWrapper(Map<String, Object> data){
        this.data = data;
    }

    public DataWrapper(){}

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        if(data == null){
            return "empty";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("DataWrapper:");
        for(Object key : data.keySet()){
            sb.append("\n" + key + " : " + data.get(key));
        }
        return sb.toString();
    }
}
