package org.rhc.renewals.common;

import java.util.HashMap;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class RequestBuilder {

    // TODO Chenge to interface type
    private HashMap<String, String> data;

    private String serviceName;

    private String callBackUrl;

    public static RequestBuilder get(){
        return new RequestBuilder();
    }

    public RequestBuilder addData(HashMap<String, String> data){

        this.data = data;

        return this;
    }

    public RequestBuilder addServiceName(String serviceName){

        this.serviceName = serviceName;

        return this;
    }

    public RequestBuilder addCallBackUrl(String callBackUrl){

        this.callBackUrl = callBackUrl;

        return this;
    }

    public ServiceRequest buildRequest(){

        ServiceRequest request = new ServiceRequest();

        request.setData(this.data);

        request.setWorkerName(this.serviceName);

        request.setCallbackUrl(this.callBackUrl);

        return request;

    }
}
