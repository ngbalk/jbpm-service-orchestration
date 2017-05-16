package org.rhc.renewals.states;

import org.rhc.renewals.common.RenewalStateContext;

/**
 * Created by nbalkiss on 5/15/17.
 */
public class PricedState implements RenewalState {


    @Override
    public void action(RenewalStateContext context) {

    }

    @Override
    public String toString() {
        return "PRICED STATE";
    }
}
