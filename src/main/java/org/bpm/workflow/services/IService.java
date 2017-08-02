package org.bpm.workflow.services;

import org.bpm.workflow.common.ServiceRequest;
import org.bpm.workflow.errors.ServiceConfigurationException;
import org.bpm.workflow.errors.ServiceException;

/**
 * Created by nbalkiss on 6/12/17.
 */
public interface IService {

    public void execute(ServiceRequest request) throws ServiceException, ServiceConfigurationException;
}
