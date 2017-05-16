package org.rhc.renewals.workitems;

import org.drools.core.process.instance.WorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.rhc.renewals.common.RenewalStateContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by nbalkiss on 5/15/17.
 */
public class RenewalStateTransitionWorkItemHandler implements WorkItemHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RenewalStateTransitionWorkItemHandler.class);

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        LOG.debug("Executing RenewalStateTransitionWorkItem");

        Map<String, Object> parameters = workItem.getParameters();

        RenewalStateContext stateContext = (RenewalStateContext) parameters.get("stateContext");

        stateContext.getCurrentState().action(stateContext);

        manager.completeWorkItem(workItem.getId(),parameters);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

        LOG.debug("Aborting RenewalStateTransitionWorkItem");

        manager.abortWorkItem(workItem.getId());
    }
}
