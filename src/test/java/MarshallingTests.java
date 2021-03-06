import org.junit.Assert;
import org.junit.Test;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.bpm.workflow.common.RequestBuilder;
import org.bpm.workflow.common.ServiceRequest;
import org.bpm.workflow.common.ServiceResponse;
import org.bpm.workflow.common.SignalInstanceInfo;
import org.bpm.workflow.errors.Severity;
import org.bpm.workflow.errors.WorkerError;
import org.bpm.workflow.models.IncidentData;
import org.bpm.workflow.models.PaymentData;
import org.bpm.workflow.states.WorkerCallState;

import java.util.*;

/**
 * Created by nbalkiss on 6/7/17.
 */
public class MarshallingTests {

    @Test
    public void testUnmarshal(){
        String data = "{\"ServiceResponse\":{\"Data\":{\"uID\":\"12345\",\"pId\":\"abcdef\"},\"WorkerName\":\"my-worker\",\"SignalInstanceInfo\":{\"ContainerId\":\"123\",\"ProcessInstanceId\":1,\"SignalName\":\"abc\"},\"Message\":\"FAIL\",\"WorkerCallState\":{\"Completed\":true,\"Errors\":[{\"ErrorID\":null,\"Severity\":\"Critical\",\"Description\":\"this is an error\"}]}}}\n";

        Set<Class<?>> allClasses = new HashSet<Class<?>>();
        allClasses.add(ServiceResponse.class);
        Marshaller marshaller = MarshallerFactory.getMarshaller(allClasses, MarshallingFormat.JSON, this.getClass().getClassLoader());
        Object unmarshal = marshaller.unmarshall(data, Object.class);
        ServiceResponse serviceResponse = (ServiceResponse) unmarshal;
        Assert.assertNotNull(serviceResponse.getMessage());
        Assert.assertNotNull(serviceResponse.getData());
        Assert.assertEquals(((HashMap<String,String>)serviceResponse.getData()).get("uID"),"12345");
        Assert.assertNotNull(serviceResponse.getWorkerName());
        Assert.assertNotNull(serviceResponse.getSignalInstanceInfo());
        Assert.assertNotNull(serviceResponse.getSignalInstanceInfo().getSignalName());

    }

    @Test
    public void testUnmarshalIncidentDataObject(){

        String data = "{\"ServiceResponse\":{\"Data\":{\"IncidentData\":{\"ID\":123,\"SupportActivityId\":\"abcdef\",\"OrganizationId\":\"xyz\",\"IncidentType\":\"Normal\"}},\"SignalInstanceInfo\":{\"ContainerId\":\"MyContainer\",\"ProcessInstanceId\":1,\"SignalName\":\"A\"},\"Message\":\"SUCCESS\",\"WorkerName\":\"generate-renewal-success\",\"WorkerCallState\":{\"Completed\":true}}}";

        Marshaller marshaller = MarshallerFactory.getMarshaller(null, MarshallingFormat.JSON, this.getClass().getClassLoader());
        Object unmarshal = marshaller.unmarshall(data, Object.class);
        ServiceResponse serviceResponse = (ServiceResponse) unmarshal;

        Assert.assertNotNull(serviceResponse.getMessage());
        Assert.assertNotNull(serviceResponse.getData());
        Assert.assertEquals(((IncidentData) serviceResponse.getData()).getIncidentType(),"Normal");
        Assert.assertEquals(((IncidentData) serviceResponse.getData()).getSupportActivityId(),"abcdef");
        Assert.assertNotNull(serviceResponse.getWorkerName());
        Assert.assertNotNull(serviceResponse.getSignalInstanceInfo());
        Assert.assertNotNull(serviceResponse.getSignalInstanceInfo().getSignalName());
        Assert.assertNull(((IncidentData) serviceResponse.getData()).getId());
    }

    @Test
    public void testMarshal(){

        ServiceResponse response = new ServiceResponse();
        response.setMessage("FAIL");
        response.setWorkerName("my-worker");
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("uID","12345");
        data.put("pId","abcdef");
        response.setData(data);
        WorkerError workerError = new WorkerError();
        workerError.setDescription("this is an error");
        workerError.setSeverity(Severity.Critical);
        WorkerCallState state = new WorkerCallState();
        state.setErrors(Arrays.asList(workerError));
        state.setCompleted(true);
        response.setWorkerCallState(state);

        SignalInstanceInfo signalInstanceInfo = new SignalInstanceInfo("123",1L,"abc");
        response.setSignalInstanceInfo(signalInstanceInfo);

        Set<Class<?>> allClasses = new HashSet<Class<?>>();
        allClasses.add(ServiceResponse.class);
        Marshaller marshaller = MarshallerFactory.getMarshaller(allClasses, MarshallingFormat.JSON, this.getClass().getClassLoader());
        String payload = marshaller.marshall(response);
        System.out.println(payload);

    }

    @Test
    public void testMarshalUnmarshalWithIncidentDataObjectAndWrapper(){

        ServiceRequest serviceRequest = new ServiceRequest();

        IncidentData incidentData = new IncidentData("xyz","123","abc");

        serviceRequest.setData(incidentData);

        HashMap<String,Object> wrapper = new HashMap<>();

        wrapper.put(ServiceRequest.class.getName(),serviceRequest);

        Set<Class<?>> allClasses = new HashSet<Class<?>>();

        allClasses.add(IncidentData.class);

        Marshaller marshaller = MarshallerFactory.getMarshaller(allClasses, MarshallingFormat.JSON, this.getClass().getClassLoader());

        String payload = marshaller.marshall(wrapper);

        System.out.println(payload);

        ServiceRequest unmarshal = (ServiceRequest) marshaller.unmarshall(payload, Object.class);

        Assert.assertTrue(unmarshal instanceof ServiceRequest);

        Assert.assertNotNull(unmarshal.getData());

        Assert.assertNotNull(((IncidentData)unmarshal.getData()));

        Assert.assertEquals(unmarshal,serviceRequest);
    }

    @Test
    public void testDBGeneratedIdIsNotSerializedForIncidentData(){

        ServiceRequest serviceRequest = new ServiceRequest();

        IncidentData incidentData = new IncidentData("xyz","123","abc");

        incidentData.setId(123L);

        serviceRequest.setData(incidentData);

        HashMap<String,Object> wrapper = new HashMap<>();

        wrapper.put(ServiceRequest.class.getName(),serviceRequest);

        Set<Class<?>> allClasses = new HashSet<Class<?>>();

        allClasses.add(IncidentData.class);

        Marshaller marshaller = MarshallerFactory.getMarshaller(allClasses, MarshallingFormat.JSON, this.getClass().getClassLoader());

        String payload = marshaller.marshall(wrapper);

        System.out.println(payload);

        ServiceRequest unmarshal = (ServiceRequest) marshaller.unmarshall(payload, Object.class);

        Assert.assertTrue(unmarshal instanceof ServiceRequest);

        Assert.assertNotNull(unmarshal.getData());

        Assert.assertNotEquals(((IncidentData) unmarshal.getData()).getId(),((IncidentData) serviceRequest.getData()).getId());

    }


    @Test
    public void testMarshalWithPaymentData(){

        ServiceRequest serviceRequest = new ServiceRequest();

        PaymentData paymentData = new PaymentData(UUID.randomUUID().toString(),UUID.randomUUID().toString());

        paymentData.setId(1L);

        serviceRequest.setData(paymentData);

        HashMap<String,Object> wrapper = new HashMap<>();

        wrapper.put(ServiceRequest.class.getName(),serviceRequest);

        Set<Class<?>> allClasses = new HashSet<Class<?>>();

        allClasses.add(PaymentData.class);

        Marshaller marshaller = MarshallerFactory.getMarshaller(allClasses, MarshallingFormat.JSON, this.getClass().getClassLoader());

        String payload = marshaller.marshall(wrapper);

        System.out.println(payload);

        ServiceRequest unmarshal = (ServiceRequest) marshaller.unmarshall(payload, Object.class);

        Assert.assertTrue(unmarshal instanceof ServiceRequest);

        Assert.assertNotNull(unmarshal.getData());

        Assert.assertNotNull(((PaymentData)unmarshal.getData()));

        Assert.assertEquals(((PaymentData) unmarshal.getData()).getPaymentId(),((PaymentData)serviceRequest.getData()).getPaymentId());

        Assert.assertEquals(((PaymentData) unmarshal.getData()).getRetryId(),((PaymentData)serviceRequest.getData()).getRetryId());

        Assert.assertNull(((PaymentData) unmarshal.getData()).getId());
    }

    @Test
    public void testMarshalUnmarshalServiceRequestWithDataType(){

        ServiceRequest serviceRequest = RequestBuilder.get()
                .addDataType(new IncidentData().getClass().getName())
                .buildRequest();

        HashMap<String,Object> wrapper = new HashMap<>();

        wrapper.put(ServiceRequest.class.getName(),serviceRequest);

        Set<Class<?>> allClasses = new HashSet<Class<?>>();

        Marshaller marshaller = MarshallerFactory.getMarshaller(allClasses, MarshallingFormat.JSON, this.getClass().getClassLoader());

        String payload = marshaller.marshall(wrapper);

        System.out.println(payload);

        ServiceRequest unmarshal = (ServiceRequest) marshaller.unmarshall(payload, Object.class);

        Assert.assertEquals(unmarshal.getDataType(),serviceRequest.getDataType());
    }
}
