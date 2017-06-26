package org.rhc.workflow.services;

import org.rhc.workflow.common.ServiceRequest;
import org.rhc.workflow.errors.ServiceConfigurationException;
import org.rhc.workflow.errors.ServiceException;

/**
 * Created by nbalkiss on 6/12/17.
 */
public interface ISVMService {

    public void execute(ServiceRequest request) throws ServiceException, ServiceConfigurationException;
}
