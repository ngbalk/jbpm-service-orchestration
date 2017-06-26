package org.rhc.workflow.services;

import org.rhc.workflow.config.SVMServiceConfig;

/**
 * Created by nbalkiss on 6/12/17.
 */
public interface ISVMServiceRegistry {

    ISVMService getService(String serviceName);

    void addService(SVMServiceConfig serviceConfig, boolean overwrite);

}
