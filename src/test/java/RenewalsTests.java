import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.common.RequestBuilder;
import org.rhc.renewals.common.ServiceRequest;
import org.rhc.renewals.common.ServiceResponse;
import org.rhc.renewals.errors.ServiceRESTException;
import org.rhc.renewals.errors.WorkerError;
import org.rhc.renewals.errors.WorkerException;
import org.rhc.renewals.services.SVMServiceRegistry;
import org.rhc.renewals.services.ServiceHandler;
import org.rhc.renewals.states.ServiceState;
import org.rhc.renewals.states.WorkerCallState;
import org.rhc.renewals.workitems.CompleteServiceWorkItemHandler;

import javax.ws.rs.ProcessingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nbalkiss on 5/11/17.
 */
public class RenewalsTests {

    Process exec;
    @Before
    public void setup(){
        try {
            ProcessBuilder pb = new ProcessBuilder("node", "src/test/resources/unit-test-server.js");
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
        this.exec.destroy();
    }

    @Test
    public void testServiceStateTransitionToWaiting(){

        RenewalStateContext context = new RenewalStateContext(new HashMap<>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<>())
                        .addContainerId("SVMContainer")
                        .addProcessInstanceId(1L)
                        .addSignalName("A")
                        .addServiceName("calculate-price")
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
    public void testServiceStateTransitionToCompletion() throws WorkerException {

        Map<String,String> data = new HashMap<>();
        data.put("uId","12345");

        ServiceState currentState = ServiceState.WAITING;

        RenewalStateContext context = new RenewalStateContext(data,currentState);

        HashMap<String, String> newData = new HashMap<>();
        newData.put("uId","12345");
        newData.put("pId","abcdef");

        WorkerCallState workerCallState = new WorkerCallState();

        workerCallState.setCompleted(true);

        ServiceResponse response = new ServiceResponse();
        response.setWorkerCallState(workerCallState);
        response.setData(newData);

        ServiceHandler executor = new ServiceHandler(context);

        executor.complete(response);

        Assert.assertEquals(ServiceState.COMPLETED, context.getCurrentState());
        Assert.assertNotNull(context.getData().get("pId"));
    }

    @Test
    public void testServiceStateTransitionToError(){

        Map<String,String> data = new HashMap<>();
        data.put("uId","12345");

        ServiceState currentState = ServiceState.WAITING;

        RenewalStateContext context = new RenewalStateContext(data,currentState);

        HashMap<String, String> newData = new HashMap<>();
        newData.put("uId","12345");
        newData.put("pId","abcdef");

        ServiceResponse response = new ServiceResponse();

        WorkerCallState workerCallState = new WorkerCallState();

        workerCallState.setCompleted(false);

        WorkerError error = new WorkerError();
        error.setDescription("Major Error!");

        workerCallState.setErrors(Arrays.asList(error));

        response.setWorkerCallState(workerCallState);
        response.setData(newData);

        ServiceHandler executor = new ServiceHandler(context);

        try {
            executor.complete(response);
            Assert.fail();
        } catch (WorkerException e) {
            e.printStackTrace();
            Assert.assertEquals(ServiceState.ERROR, context.getCurrentState());
            Assert.assertNull(context.getData().get("pId"));
        }

    }


    @Test
    public void testTimeoutWorks(){

        RenewalStateContext context = new RenewalStateContext(new HashMap<>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<>())
                        .addContainerId("SVMContainer")
                        .addProcessInstanceId(1L)
                        .addSignalName("A")
                        .addServiceName("timeout")
                        .buildRequest();

        try{
            executor.execute(request);
            Assert.fail();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            Assert.assertTrue(e instanceof ProcessingException);
        }
    }


    @Test
    public void test404Exception(){

        RenewalStateContext context = new RenewalStateContext(new HashMap<>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<>())
                        .addContainerId("SVMContainer")
                        .addProcessInstanceId(1L)
                        .addSignalName("A")
                        .addServiceName("invalid")
                        .buildRequest();

        try{
            executor.execute(request);
            Assert.fail();
        }
        catch(Exception e){
            System.out.println(e.getCause().getMessage());
            Assert.assertTrue(e instanceof ServiceRESTException);
        }
    }

    @Test
    public void testServiceSuccessfullyRetries(){

        RenewalStateContext context = new RenewalStateContext(new HashMap<>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<>())
                        .addContainerId("SVMContainer")
                        .addProcessInstanceId(1L)
                        .addSignalName("A")
                        .addServiceName("try-again")
                        .buildRequest();

        try{
            executor.execute(request);

        }

        catch(Exception e){
            System.out.println(e.getCause().getMessage());
            Assert.fail();
        }
    }

    @Test
    public void testCompleteServiceWIHCompletes(){
        WorkItem wi = Mockito.mock(WorkItem.class);

        ServiceResponse responseMock = Mockito.mock(ServiceResponse.class);

        WorkerCallState workerCallState = new WorkerCallState();

        workerCallState.setCompleted(true);

        Mockito.when(responseMock.getWorkerCallState()).thenReturn(workerCallState);

        Mockito.when(wi.getParameter("data")).thenReturn(Mockito.mock(Map.class));
        Mockito.when(wi.getParameter("state")).thenReturn(ServiceState.WAITING);
        Mockito.when(wi.getParameter("lastServiceResponse")).thenReturn(responseMock);

        WorkItemManager wim = Mockito.mock(WorkItemManager.class);

        CompleteServiceWorkItemHandler wih = new CompleteServiceWorkItemHandler();

        wih.executeWorkItem(wi,wim);

    }

}
