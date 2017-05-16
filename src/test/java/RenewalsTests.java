import org.junit.Assert;
import org.junit.Test;
import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.services.SVMServiceRegistry;
import org.rhc.renewals.states.*;
import org.rhc.renewals.states.PricedState;
import org.rhc.renewals.states.StartedRenewalState;
import org.rhc.renewals.states.WaitingForPricingState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nbalkiss on 5/11/17.
 */
public class RenewalsTests {

    @Test
    public void testStartedRenewalStateTransition(){

        Map<String,String> data = new HashMap<>();

        data.put("uID","12345");

        RenewalStateContext context = new RenewalStateContext(data, new StartedRenewalState());

        RenewalState currentState = context.getCurrentState();

        currentState.action(context);

        Assert.assertTrue(context.getCurrentState() instanceof WaitingForPricingState);
    }

    @Test
    public void testWaitingForPricingStateTransition(){

        RenewalStateContext context = new RenewalStateContext(null, new WaitingForPricingState());

        RenewalState currentState = context.getCurrentState();

        currentState.action(context);

        Assert.assertTrue(context.getCurrentState() instanceof PricedState);
    }

    @Test
    public void testSVMServiceRegistryCanLoadServicesConfigAsClasspathResource(){
        Assert.assertNotNull(SVMServiceRegistry.getInstance());
    }
}
