import com.google.protobuf.ServiceException;
import org.junit.Assert;
import org.junit.Test;
import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.common.RequestBuilder;
import org.rhc.renewals.common.ServiceRequest;
import org.rhc.renewals.common.ServiceResponse;
import org.rhc.renewals.exceptions.ServiceRESTException;
import org.rhc.renewals.services.SVMServiceRegistry;
import org.rhc.renewals.services.ServiceExecutor;
import org.rhc.renewals.states.*;

import javax.ws.rs.ProcessingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nbalkiss on 5/11/17.
 */
public class RenewalsTests {

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
    public void testSVMServiceRegistryCanLoadServicesConfigAsClasspathResource(){
        Assert.assertNotNull(SVMServiceRegistry.getInstance());
    }
}
