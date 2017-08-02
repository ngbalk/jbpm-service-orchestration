package org.bpm.workflow.common;

/**
 * Created by nbalkiss on 5/16/17.
 */
public class RequestBuilder {

    private Object data;

    private String serviceName;

    private String containerId;

    private Long processInstanceId;

    private String signalName;

    private String dataType;

    public static RequestBuilder get(){
        return new RequestBuilder();
    }

    public RequestBuilder addData(Object data){

        this.data = data;

        return this;
    }

    public RequestBuilder addDataType(String dataType){

        this.dataType = dataType;

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

        request.setDataType(dataType);

        request.setWorkerName(serviceName);

        SignalInstanceInfo signalInstanceInfo = new SignalInstanceInfo(containerId,processInstanceId,signalName);

        request.setSignalInstanceInfo(signalInstanceInfo);

        return request;

    }
}
