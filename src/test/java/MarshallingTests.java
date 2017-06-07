import org.junit.Assert;
import org.junit.Test;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.rhc.renewals.common.ServiceResponse;
import org.rhc.renewals.errors.Severity;
import org.rhc.renewals.errors.WorkerError;
import org.rhc.renewals.states.WorkerCallState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nbalkiss on 6/7/17.
 */
public class MarshallingTests {

    @Test
    public void testUnmarshal(){
        String data = "{\"org.rhc.renewals.common.ServiceResponse\":{\"Data\":{\"uID\":\"12345\",\"pId\":\"abcdef\"},\"WorkerName\":\"my-worker\",\"Message\":\"FAIL\",\"WorkerCallState\":{\"Completed\":true,\"Errors\":[{\"ErrorID\":null,\"Severity\":\"Critical\",\"Description\":\"this is an error\"}]}}}\n";

        Set<Class<?>> allClasses = new HashSet<Class<?>>();
        allClasses.add(ServiceResponse.class);
        Marshaller marshaller = MarshallerFactory.getMarshaller(allClasses, MarshallingFormat.JSON, this.getClass().getClassLoader());
        Object unmarshal = marshaller.unmarshall(data, Object.class);
        ServiceResponse serviceResponse = (ServiceResponse) unmarshal;
        Assert.assertNotNull(serviceResponse.getMessage());
        Assert.assertNotNull(serviceResponse.getData());
        Assert.assertNotNull(serviceResponse.getWorkerName());

    }

    @Test
    public void testMarshal(){

        ServiceResponse response = new ServiceResponse();
        response.setMessage("FAIL");
        HashMap<String,String> data = new HashMap<>();
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

        Set<Class<?>> allClasses = new HashSet<Class<?>>();
        allClasses.add(ServiceResponse.class);
        Marshaller marshaller = MarshallerFactory.getMarshaller(allClasses, MarshallingFormat.JSON, this.getClass().getClassLoader());
        String payload = marshaller.marshall(response);
        System.out.println(payload);

    }
}
