import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.common.RequestBuilder;
import org.rhc.renewals.common.ServiceRequest;
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
    public void testCallToServiceWithCallback() throws Exception{

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

        executor.execute(request);
        Assert.assertEquals(ServiceState.WAITING, context.getCurrentState());

    }


    @Test
    public void testCallToServiceWithCallbackError() throws Exception{

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


        executor.execute(request);
        Assert.assertEquals(ServiceState.WAITING, context.getCurrentState());

    }

}