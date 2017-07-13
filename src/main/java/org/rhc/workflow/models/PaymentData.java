package org.rhc.workflow.models;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.drools.persistence.jpa.marshaller.VariableEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * Created by nbalkiss on 7/12/17.
 */
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class PaymentData extends VariableEntity implements Serializable{

    static final long serialVersionUID = 365784392199L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_data_id")
    @XmlElement(name="ID")
    @JsonProperty("ID")
    public Long id;

    @Column(name = "payment_id", length = 128)
    @XmlElement(name="PaymentId")
    @JsonProperty("PaymentId")
    private String paymentId;

    @Column(name = "retry_id", length = 128)
    @XmlElement(name="RetryId")
    @JsonProperty("RetryId")
    private String retryId;

    public PaymentData(String paymentId, String retryId) {
        this.paymentId = paymentId;
        this.retryId = retryId;
    }

    public PaymentData() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getRetryId() {
        return retryId;
    }

    public void setRetryId(String retryId) {
        this.retryId = retryId;
    }

    @Override
    public String toString() {
        return "PaymentData{" +
                "id=" + id +
                ", paymentId='" + paymentId + '\'' +
                ", retryId='" + retryId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentData that = (PaymentData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (paymentId != null ? !paymentId.equals(that.paymentId) : that.paymentId != null) return false;
        return retryId != null ? retryId.equals(that.retryId) : that.retryId == null;

    }
}
