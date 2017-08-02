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
 * Created by nbalkiss on 7/12/17.
 */
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class PaymentData extends DomainData implements Serializable, Copyable{

    static final long serialVersionUID = 365784392199L;

    @Column(name = "payment_id", length = 128)
    @XmlElement(name="PaymentId")
    @JsonProperty("PaymentId")
    private String paymentId;

    @Column(name = "retry_id", length = 128)
    @XmlElement(name="RetryId")
    @JsonProperty("RetryId")
    private String retryId;

    @Column(name = "payment_worker_schedule_result", length = 128)
    @XmlElement(name="PaymentWorkerScheduleResult")
    @JsonProperty("PaymentWorkerScheduleResult")
    private String paymentWorkerScheduleResult;

    public PaymentData(String paymentId, String retryId) {
        this.paymentId = paymentId;
        this.retryId = retryId;
    }

    public PaymentData(String paymentId, String retryId, String paymentWorkerScheduleResult) {
        this.paymentId = paymentId;
        this.retryId = retryId;
        this.paymentWorkerScheduleResult = paymentWorkerScheduleResult;
    }

    public PaymentData() {}

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

    public String getPaymentWorkerScheduleResult() {
        return paymentWorkerScheduleResult;
    }

    public void setPaymentWorkerScheduleResult(String paymentWorkerScheduleResult) {
        this.paymentWorkerScheduleResult = paymentWorkerScheduleResult;
    }

    @Override
    public String toString() {
        return "PaymentData{" +
                "id=" + id +
                ", paymentId='" + paymentId + '\'' +
                ", retryId='" + retryId + '\'' +
                ", paymentWorkerScheduleResult='" + paymentWorkerScheduleResult + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentData that = (PaymentData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (paymentId != null ? !paymentId.equals(that.paymentId) : that.paymentId != null) return false;
        if (paymentWorkerScheduleResult != null ? !paymentWorkerScheduleResult.equals(that.paymentWorkerScheduleResult) : that.paymentWorkerScheduleResult != null) return false;
        return retryId != null ? retryId.equals(that.retryId) : that.retryId == null;
    }

    @Override
    public boolean copy(Object other) {
        if(other == null || !(other instanceof PaymentData)){
            return false;
        }
        this.setPaymentId(((PaymentData) other).getPaymentId());
        this.setRetryId(((PaymentData) other).getRetryId());
        this.setPaymentWorkerScheduleResult(((PaymentData) other).getPaymentWorkerScheduleResult());
        return true;
    }
}
