package org.rhc.renewals.common;

import java.util.Map;

/**
 * Created by nbalkiss on 5/16/17.
 */
public abstract class AbstractServiceRequestResponse {

    private Map<String, String> data;

    private String svcName;

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getSvcName() {
        return svcName;
    }

    public void setSvcName(String svcName) {
        this.svcName = svcName;
    }
}
