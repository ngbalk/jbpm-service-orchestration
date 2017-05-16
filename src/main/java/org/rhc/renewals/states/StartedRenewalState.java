package org.rhc.renewals.states;

import org.rhc.renewals.common.RenewalStateContext;
import org.rhc.renewals.services.SVMService;
import org.rhc.renewals.services.SVMServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nbalkiss on 5/11/17.
 */
public class StartedRenewalState implements RenewalState {

    private static final Logger LOG = LoggerFactory.getLogger(StartedRenewalState.class);

    @Override
    public void action(RenewalStateContext context) {

        SVMService service = SVMServiceRegistry.getInstance().getService("calculate-price");

        service.execute(context.getData());

        LOG.debug("Transitioning state from StartedRenewalState to WaitingForPricingState");

        context.setCurrentState(new WaitingForPricingState());

    }

    @Override
    public String toString() {
        return "WAITING FOR PRICING";
    }
}
