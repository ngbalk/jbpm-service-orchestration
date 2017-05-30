import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.mockito.Mockito;
import org.rhc.renewals.commands.InvokeServiceCommand;
import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.common.RequestBuilder;
import org.rhc.renewals.common.ServiceRequest;
import org.rhc.renewals.common.ServiceResponse;
import org.rhc.renewals.exceptions.ServiceRESTException;
import org.rhc.renewals.services.SVMServiceRegistry;
import org.rhc.renewals.services.ServiceExecutor;
import org.rhc.renewals.states.*;
import org.rhc.renewals.workitems.CompleteServiceWorkItemHandler;

import javax.ws.rs.ProcessingException;
import java.io.IOException;
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
        this.exec.destroy();
    }

    @Test
    public void testServiceStateTransitionToWaiting(){

        RenewalStateContext context = new RenewalStateContext(new HashMap<>(), ServiceState.NOT_STARTED);

        ServiceExecutor executor = new ServiceExecutor(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<>())
                        .addCallBackUrl("")
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
    public void testServiceStateTransitionToCompletion(){

        Map<String,String> data = new HashMap<>();
        data.put("uId","12345");

        ServiceState currentState = ServiceState.WAITING;

        RenewalStateContext context = new RenewalStateContext(data,currentState);

        Map<String,String> newData = new HashMap<>();
        newData.put("uId","12345");
        newData.put("pId","abcdef");

        ServiceResponse response = new ServiceResponse();
        response.setSvcState(ServiceState.COMPLETED);
        response.setData(newData);

        ServiceExecutor executor = new ServiceExecutor(context);

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

        Map<String,String> newData = new HashMap<>();
        newData.put("uId","12345");
        newData.put("pId","abcdef");

        ServiceResponse response = new ServiceResponse();
        response.setSvcState(ServiceState.ERROR);
        response.setData(newData);

        ServiceExecutor executor = new ServiceExecutor(context);

        executor.complete(response);

        Assert.assertEquals(ServiceState.ERROR, context.getCurrentState());
        Assert.assertNull(context.getData().get("pId"));
    }


    @Test
    public void testTimeoutWorks(){

        RenewalStateContext context = new RenewalStateContext(new HashMap<>(), ServiceState.NOT_STARTED);

        ServiceExecutor executor = new ServiceExecutor(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<>())
                        .addCallBackUrl("")
                        .addServiceName("timeout")
                        .buildRequest();

        try{
            executor.execute(request);
            Assert.fail();
        }
        catch(Exception e){
            System.out.println(e.getCause().getMessage());
            Assert.assertTrue(e instanceof ProcessingException);
        }
    }


    @Test
    public void test404Exception(){

        RenewalStateContext context = new RenewalStateContext(new HashMap<>(), ServiceState.NOT_STARTED);

        ServiceExecutor executor = new ServiceExecutor(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<>())
                        .addCallBackUrl("")
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

        ServiceExecutor executor = new ServiceExecutor(context);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(new HashMap<>())
                        .addCallBackUrl("")
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

        Mockito.when(responseMock.getSvcState()).thenReturn(ServiceState.COMPLETED);

        Mockito.when(wi.getParameter("data")).thenReturn(Mockito.mock(Map.class));
        Mockito.when(wi.getParameter("state")).thenReturn(ServiceState.WAITING);
        Mockito.when(wi.getParameter("lastServiceResponse")).thenReturn(responseMock);

        WorkItemManager wim = Mockito.mock(WorkItemManager.class);

        CompleteServiceWorkItemHandler wih = new CompleteServiceWorkItemHandler();

        wih.executeWorkItem(wi,wim);

    }

    @Test
    public void testSVMServiceRegistryCanLoadServicesConfigAsClasspathResource(){
        Assert.assertNotNull(SVMServiceRegistry.getInstance());
    }
}
