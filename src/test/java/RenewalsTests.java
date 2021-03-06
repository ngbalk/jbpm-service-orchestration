import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.bpm.workflow.common.RequestBuilder;
import org.bpm.workflow.common.ServiceRequest;
import org.bpm.workflow.common.ServiceResponse;
import org.bpm.workflow.common.StateContext;
import org.bpm.workflow.config.ServiceConfig;
import org.bpm.workflow.errors.ServiceException;
import org.bpm.workflow.errors.ServiceRESTException;
import org.bpm.workflow.errors.WorkerError;
import org.bpm.workflow.errors.WorkerException;
import org.bpm.workflow.services.ServiceRegistry;
import org.bpm.workflow.services.ServiceHandler;
import org.bpm.workflow.states.ServiceState;
import org.bpm.workflow.states.WorkerCallState;
import org.bpm.workflow.workitems.CompleteServiceWorkItemHandler;

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
            Thread.sleep(500);
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

        StateContext context = new StateContext(new HashMap<String, Object>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<String, Object>())
                        .addContainerId("MyContainer")
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

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("uId","12345");

        ServiceState currentState = ServiceState.WAITING;

        StateContext context = new StateContext(data,currentState);

        HashMap<String, Object> newData = new HashMap<String, Object>();
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
        Assert.assertNotNull(((Map<String,Object>)context.getData()).get("pId"));
    }

    @Test
    public void testServiceStateTransitionToError(){

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("uId","12345");

        ServiceState currentState = ServiceState.WAITING;

        StateContext context = new StateContext(data,currentState);

        HashMap<String, Object> newData = new HashMap<String, Object>();
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
            Assert.assertNull(((Map<String,Object>)context.getData()).get("pId"));
        }

    }

    @Test
    public void executeCompleteFailsWhenStateIsNull(){

        StateContext context = new StateContext(new HashMap<String, Object>(),null);

        ServiceResponse response = new ServiceResponse();

        WorkerCallState workerCallState = new WorkerCallState();

        workerCallState.setCompleted(true);

        response.setWorkerCallState(workerCallState);

        ServiceHandler executor = new ServiceHandler(context);

        try {
            executor.complete(response);
            Assert.fail();
        } catch(IllegalStateException e){
            e.printStackTrace();
        } catch (WorkerException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void executeCompleteFailsWhenStateIsNotWaiting(){

        StateContext context = new StateContext(new HashMap<String, Object>(),ServiceState.NOT_STARTED);

        ServiceResponse response = new ServiceResponse();

        WorkerCallState workerCallState = new WorkerCallState();

        workerCallState.setCompleted(true);

        response.setWorkerCallState(workerCallState);

        ServiceHandler executor = new ServiceHandler(context);

        try {
            executor.complete(response);
            Assert.fail();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    public void testTimeoutWorks(){

        StateContext context = new StateContext(new HashMap<String, Object>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<String, Object>())
                        .addContainerId("MyContainer")
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

        StateContext context = new StateContext(new HashMap<String, Object>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<String, Object>())
                        .addContainerId("MyContainer")
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

        StateContext context = new StateContext(new HashMap<String, Object>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<String, Object>())
                        .addContainerId("MyContainer")
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

    @Test
    public void testTESTFlagModeWithValidServiceSucceeds() throws Exception{

        StateContext context = new StateContext(new HashMap<String, Object>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("uID","12345");


        ServiceConfig config = new ServiceConfig("test-worker"
                ,"http://localhost:3000/hello-microservice"
                ,null
                ,null
                ,"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkF1ZHBIa0tnS2lhZ3Q2U0pxajdVcjFRX3M5USJ9.eyJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjEzMzcvIiwiaXNzIjoiaHR0cDovL2FkZnMuc2VydmljZW1hc3Rlci5jb20vYWRmcy9zZXJ2aWNlcy90cnVzdCIsImlhdCI6MTQ2MzYwNzEyNiwiZXhwIjoxNTI2Njc5MTI2LCJ3aW5hY2NvdW50bmFtZSI6InN2YzNTY2FsZUZTR2Vjb21tIiwiZ3JvdXAiOlsiQklaIFNlcnZpY2UgQWNjb3VudHMiLCJEb21haW4gVXNlcnMiLCJTZXJ2aWNlIEFjY291bnRzIExvZ29uIERlbnkiXSwiYXV0aF90aW1lIjoiMjAxNi0wNS0xOFQyMTozMjowNi4zODZaIiwiYXV0aG1ldGhvZCI6InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphYzpjbGFzc2VzOlBhc3N3b3JkUHJvdGVjdGVkVHJhbnNwb3J0IiwidmVyIjoiMS4wIiwiYXBwaWQiOiIzc2NhbGVhcGl0b2tlbiJ9.mRZPeJvapHEK70QKMkSakVkg16AKnEzF6Xcx6IVKouJaXYGdHx2o1QWlIGrS_UUpSNuYiDULwGbiNoKQx4oQi3and-LuQALN0mDqigB34wqG90RbNHC5cN-K29X2oAT8bJdKazeELOwO19keExNSfdD_8KZTg0ebgcIYgvjVCvx3zvMEcMfE5Y5mw-9kXznZFTuWRJyjmqRhq19WEB08rCDD_Eg9hRy9b1fn3gNDnhQ-fbbDqfK7z2Nht9RckjNslF0nVLH41a-WNsV6kkNcObl68bfWAffZNG9KAwGBxr7oWwmGyHhR_NGZ8ixCiZxkaUfqhxexupbys46Rp7iIuQ"
                ,10000,
                3);

        ServiceRegistry.getInstance().addService(config, true);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(data)
                        .addContainerId("MyContainer")
                        .addProcessInstanceId(1L)
                        .addSignalName("A")
                        .addServiceName("$TEST:http://localhost:3000/hello-microservice")
                        .buildRequest();

        executor.execute(request);

        Assert.assertEquals(ServiceState.WAITING, context.getCurrentState());

    }

    @Test
    public void testTESTFlagModeWithInValidServiceFails(){

        StateContext context = new StateContext(new HashMap<String, Object>(), ServiceState.NOT_STARTED);

        ServiceHandler executor = new ServiceHandler(context);

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("uID","12345");

        ServiceConfig config = new ServiceConfig("test-worker"
                        ,"http://localhost:3000/invalid-service"
                        ,null
                        ,null
                        ,"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkF1ZHBIa0tnS2lhZ3Q2U0pxajdVcjFRX3M5USJ9.eyJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjEzMzcvIiwiaXNzIjoiaHR0cDovL2FkZnMuc2VydmljZW1hc3Rlci5jb20vYWRmcy9zZXJ2aWNlcy90cnVzdCIsImlhdCI6MTQ2MzYwNzEyNiwiZXhwIjoxNTI2Njc5MTI2LCJ3aW5hY2NvdW50bmFtZSI6InN2YzNTY2FsZUZTR2Vjb21tIiwiZ3JvdXAiOlsiQklaIFNlcnZpY2UgQWNjb3VudHMiLCJEb21haW4gVXNlcnMiLCJTZXJ2aWNlIEFjY291bnRzIExvZ29uIERlbnkiXSwiYXV0aF90aW1lIjoiMjAxNi0wNS0xOFQyMTozMjowNi4zODZaIiwiYXV0aG1ldGhvZCI6InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphYzpjbGFzc2VzOlBhc3N3b3JkUHJvdGVjdGVkVHJhbnNwb3J0IiwidmVyIjoiMS4wIiwiYXBwaWQiOiIzc2NhbGVhcGl0b2tlbiJ9.mRZPeJvapHEK70QKMkSakVkg16AKnEzF6Xcx6IVKouJaXYGdHx2o1QWlIGrS_UUpSNuYiDULwGbiNoKQx4oQi3and-LuQALN0mDqigB34wqG90RbNHC5cN-K29X2oAT8bJdKazeELOwO19keExNSfdD_8KZTg0ebgcIYgvjVCvx3zvMEcMfE5Y5mw-9kXznZFTuWRJyjmqRhq19WEB08rCDD_Eg9hRy9b1fn3gNDnhQ-fbbDqfK7z2Nht9RckjNslF0nVLH41a-WNsV6kkNcObl68bfWAffZNG9KAwGBxr7oWwmGyHhR_NGZ8ixCiZxkaUfqhxexupbys46Rp7iIuQ"
                        ,10000,
                        3);

        ServiceRegistry.getInstance().addService(config, true);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(data)
                        .addContainerId("MyContainer")
                        .addProcessInstanceId(1L)
                        .addSignalName("A")
                        .addServiceName("$TEST:")
                        .buildRequest();

        try {
            executor.execute(request);

            Assert.fail();
        } catch (ServiceException e) {

            e.printStackTrace();
        }
    }

}
