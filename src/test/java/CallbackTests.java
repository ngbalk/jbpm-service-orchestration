import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rhc.workflow.common.StateContext;
import org.rhc.workflow.common.RequestBuilder;
import org.rhc.workflow.common.ServiceRequest;
import org.rhc.workflow.services.ServiceHandler;
import org.rhc.workflow.states.ServiceState;

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

        StateContext context = new StateContext(new HashMap<>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        HashMap<String,String> data = new HashMap<>();
        data.put("uID","12345");

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(data)
                        .addContainerId("SVMContainer")
                        .addProcessInstanceId(1L)
                        .addSignalName("A")
                        .addServiceName("generate-renewal-success")
                        .buildRequest();

        executor.execute(request);
        Assert.assertEquals(ServiceState.WAITING, context.getCurrentState());

    }


    @Test
    public void testCallToServiceWithCallbackError() throws Exception{

        StateContext context = new StateContext(new HashMap<>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        HashMap<String,String> data = new HashMap<>();
        data.put("uID","12345");

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(data)
                        .addContainerId("SVMContainer")
                        .addProcessInstanceId(1L)
                        .addSignalName("A")
                        .addServiceName("generate-renewal-error")
                        .buildRequest();


        executor.execute(request);
        Assert.assertEquals(ServiceState.WAITING, context.getCurrentState());

    }

}
