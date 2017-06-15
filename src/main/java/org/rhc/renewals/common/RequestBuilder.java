package org.rhc.renewals.common;

import java.util.Map;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class RequestBuilder {

    private Map<String, String> data;

    private String serviceName;

    private String containerId;

    private Long processInstanceId;

    private String signalName;

    public static RequestBuilder get(){
        return new RequestBuilder();
    }

    public RequestBuilder addData(Map<String, String> data){

        this.data = data;

        return this;
    }

    public RequestBuilder addServiceName(String serviceName){

        this.serviceName = serviceName;

        return this;
    }

    public RequestBuilder addContainerId(String containerId){

        this.containerId = containerId;

        return this;
    }

    public RequestBuilder addProcessInstanceId(Long pId){

        this.processInstanceId = pId;

        return this;
    }

    public RequestBuilder addSignalName(String signalName){

        this.signalName = signalName;

        return this;
    }


    public ServiceRequest buildRequest(){

        ServiceRequest request = new ServiceRequest();

        request.setData(data);

        request.setWorkerName(serviceName);

        SignalInstanceInfo signalInstanceInfo = new SignalInstanceInfo(containerId,processInstanceId,signalName);

        request.setSignalInstanceInfo(signalInstanceInfo);

        return request;

    }
}
