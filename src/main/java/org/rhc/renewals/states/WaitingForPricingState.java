package org.rhc.renewals.states;

import org.rhc.renewals.common.RenewalStateContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nbalkiss on 5/15/17.
 */
public class WaitingForPricingState implements RenewalState {

    private static final Logger LOG = LoggerFactory.getLogger(WaitingForPricingState.class);

    @Override
    public void action(RenewalStateContext context) {

        LOG.debug("Transitioning state from WaitingForPricingState to PricedState");

        context.setCurrentState(new PricedState());
    }

    @Override
    public String toString() {
        return "WAITING FOR PRICING";
    }
}
