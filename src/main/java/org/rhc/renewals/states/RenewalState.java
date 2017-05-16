package org.rhc.renewals.states;

import org.rhc.renewals.common.RenewalStateContext;

/**
 * Created by nbalkiss on 5/10/17.
 */
public interface RenewalState {

    void action(RenewalStateContext context);

    String toString();

}
