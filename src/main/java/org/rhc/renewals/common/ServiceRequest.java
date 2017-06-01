package org.rhc.renewals.common;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class ServiceRequest extends AbstractServiceRequestResponse{

    @XmlElement(name="CallbackUrl")
    private String callbackUrl;

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
