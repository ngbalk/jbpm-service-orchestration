import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.common.RequestBuilder;
import org.rhc.renewals.common.ServiceRequest;
import org.rhc.renewals.common.ServiceResponse;
import org.rhc.renewals.errors.Severity;
import org.rhc.renewals.services.ServiceHandler;
import org.rhc.renewals.states.ServiceState;

import java.util.HashMap;


/**
 * Created by nbalkiss on 5/31/17.
 */
public class CallbackTests {

    Process exec;
    @Before
    public void setup(){
        try {
            ProcessBuilder pb = new ProcessBuilder("node", "src/test/resources/test-microservice.js");
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            this.exec = pb.start();
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void breakdown(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.exec.destroy();
    }

    @Test
    public void testCallToServiceWithCallback(){

        RenewalStateContext context = new RenewalStateContext(new HashMap<>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        HashMap<String,String> data = new HashMap<>();
        data.put("uID","12345");

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(data)
                        .addCallBackUrl("http://localhost:3000/callback")
                        .addServiceName("generate-renewal-success")
                        .buildRequest();

        try{
            executor.execute(request);
            Assert.assertEquals(ServiceState.WAITING, context.getCurrentState());
        }
        catch(Exception e){
            System.out.println(e.getCause().getMessage());
            Assert.fail();
        }
    }


    @Test
    public void testCallToServiceWithCallbackError(){

        RenewalStateContext context = new RenewalStateContext(new HashMap<>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        HashMap<String,String> data = new HashMap<>();
        data.put("uID","12345");

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(data)
                        .addCallBackUrl("http://localhost:3000/callback")
                        .addServiceName("generate-renewal-error")
                        .buildRequest();

        try{
            executor.execute(request);
            Assert.assertEquals(ServiceState.WAITING, context.getCurrentState());
        }
        catch(Exception e){
            System.out.println(e.getCause().getMessage());
            Assert.fail();
        }
    }

    /*
        Marshal / unmarshal tests
     */

    @Test
    public void unmarshalSuccessTest() throws Exception{

        String response = "{\"Data\":{\"uID\":\"12345\",\"pId\":\"abcdef\"},\"Message\":\"SUCCESS\",\"WorkerName\":\"generate-renewal-success\",\"WorkerCallState\":{\"Completed\":true}}\n";

        ObjectMapper mapper = new ObjectMapper();

        ServiceResponse serviceResponse = mapper.readValue(response,ServiceResponse.class);

        Assert.assertNull(serviceResponse.getWorkerCallState().getErrors());

        Assert.assertEquals(true,serviceResponse.getWorkerCallState().isCompleted());

        Assert.assertEquals("SUCCESS",serviceResponse.getMessage());

        Assert.assertEquals("abcdef",serviceResponse.getData().get("pId"));

    }

    @Test
    public void unmarshalFailureTest() throws Exception{

        String response = "{\"Data\":{\"uID\":\"12345\",\"pId\":\"abcdef\"},\"Message\":\"ERROR\",\"WorkerName\":\"generate-renewal-error\",\"WorkerCallState\":{\"Completed\":false,\"Errors\":[{\"ErrorID\":\"ERROR12345\",\"Severity\":\"Critical\",\"Description\":\"This is an error!\"}]}}";

        ObjectMapper mapper = new ObjectMapper();

        ServiceResponse serviceResponse = mapper.readValue(response,ServiceResponse.class);

        Assert.assertNotNull(serviceResponse.getWorkerCallState().getErrors());

        Assert.assertEquals(false,serviceResponse.getWorkerCallState().isCompleted());

        Assert.assertEquals("ERROR",serviceResponse.getMessage());

        Assert.assertEquals(1,serviceResponse.getWorkerCallState().getErrors().size());

        Assert.assertEquals("ERROR12345",serviceResponse.getWorkerCallState().getErrors().get(0).getErrorId());

        Assert.assertEquals(Severity.Critical,serviceResponse.getWorkerCallState().getErrors().get(0).getSeverity());

        Assert.assertEquals("This is an error!",serviceResponse.getWorkerCallState().getErrors().get(0).getDescription());


    }
}
