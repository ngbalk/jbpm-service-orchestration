package org.rhc.renewals.common;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class ServiceRequest extends AbstractServiceRequestResponse{

    private String callBackUrl;

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }
}
