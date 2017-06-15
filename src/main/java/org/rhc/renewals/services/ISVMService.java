package org.rhc.renewals.services;

import org.rhc.renewals.common.ServiceRequest;
import org.rhc.renewals.errors.ServiceConfigurationException;
import org.rhc.renewals.errors.ServiceException;

/**
 * Created by nbalkiss on 6/12/17.
 */
public interface ISVMService {

    public void execute(ServiceRequest request) throws ServiceException, ServiceConfigurationException;
}
