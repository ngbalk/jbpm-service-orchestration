import org.junit.Assert;
import org.junit.Test;
import org.rhc.workflow.common.ServiceResponse;
import org.rhc.workflow.common.StateContext;
import org.rhc.workflow.models.IncidentData;
import org.rhc.workflow.services.ServiceHandler;
import org.rhc.workflow.states.ServiceState;
import org.rhc.workflow.states.WorkerCallState;

/**
 * Created by nbalkiss on 7/13/17.
 */
public class ServiceHandlerTests {

    @Test
    public void testServiceHandlerCompleteWithNullCurrentDataShouldSetWithNewData() throws Exception{

        IncidentData currentData = null;

        ServiceState currentState = ServiceState.WAITING;

        StateContext context = new StateContext(currentData,currentState);

        ServiceHandler executor = new ServiceHandler(context);

        WorkerCallState workerCallState = new WorkerCallState();

        workerCallState.setCompleted(true);

        ServiceResponse response = new ServiceResponse();

        response.setWorkerCallState(workerCallState);

        IncidentData newIncidentData = new IncidentData("123","abc","xyz");

        response.setData(newIncidentData);

        executor.complete(response);

        Assert.assertEquals(context.getData(),newIncidentData);
    }

    @Test
    public void testServiceHandlerCompleteWithNullNewDataShouldSetWithNull() throws Exception{

        IncidentData currentData = new IncidentData("123","abc","xyz");

        ServiceState currentState = ServiceState.WAITING;

        StateContext context = new StateContext(currentData,currentState);

        ServiceHandler executor = new ServiceHandler(context);

        WorkerCallState workerCallState = new WorkerCallState();

        workerCallState.setCompleted(true);

        ServiceResponse response = new ServiceResponse();

        response.setWorkerCallState(workerCallState);

        IncidentData newIncidentData = null;

        response.setData(newIncidentData);

        executor.complete(response);

        Assert.assertEquals(context.getData(),null);
    }

    @Test
    public void testServiceHandlerCompleteWithNonCopyableCurrentDataShouldSetWithNewData() throws Exception{

        Object currentData = new Object();

        ServiceState currentState = ServiceState.WAITING;

        StateContext context = new StateContext(currentData,currentState);

        ServiceHandler executor = new ServiceHandler(context);

        WorkerCallState workerCallState = new WorkerCallState();

        workerCallState.setCompleted(true);

        ServiceResponse response = new ServiceResponse();

        response.setWorkerCallState(workerCallState);

        IncidentData newIncidentData = new IncidentData("123","abc","xyz");

        response.setData(newIncidentData);

        executor.complete(response);

        Assert.assertEquals(context.getData(),newIncidentData);

    }

    @Test
    public void testServiceHandlerCompleteWithNonCopyableNewDataShouldSetNewData() throws Exception{

        IncidentData currentData = new IncidentData("123","456","789");

        ServiceState currentState = ServiceState.WAITING;

        StateContext context = new StateContext(currentData,currentState);

        ServiceHandler executor = new ServiceHandler(context);

        WorkerCallState workerCallState = new WorkerCallState();

        workerCallState.setCompleted(true);

        ServiceResponse response = new ServiceResponse();

        response.setWorkerCallState(workerCallState);

        Object newIncidentData = new Object();

        response.setData(newIncidentData);

        executor.complete(response);

        Assert.assertEquals(context.getData(),newIncidentData);
    }

    @Test
    public void testCurrentDataShouldBeReplacedWithNewDataButSameIDWhenBothNotNullAndBothCopyable() throws Exception{

        IncidentData currentData = new IncidentData("123","456","789");

        currentData.setId(123L);

        ServiceState currentState = ServiceState.WAITING;

        StateContext context = new StateContext(currentData,currentState);

        ServiceHandler executor = new ServiceHandler(context);

        WorkerCallState workerCallState = new WorkerCallState();

        workerCallState.setCompleted(true);

        ServiceResponse response = new ServiceResponse();

        response.setWorkerCallState(workerCallState);

        IncidentData newIncidentData = new IncidentData("abc","def","ghi");

        response.setData(newIncidentData);

        executor.complete(response);

        Assert.assertTrue((((IncidentData)context.getData()).getId())==123L);

        Assert.assertEquals(((IncidentData)context.getData()).getSupportActivityId(),"abc");
    }
}
