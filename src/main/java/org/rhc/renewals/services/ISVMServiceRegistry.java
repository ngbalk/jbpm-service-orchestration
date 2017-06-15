package org.rhc.renewals.services;

import org.rhc.renewals.config.SVMServiceConfig;

/**
 * Created by nbalkiss on 6/12/17.
 */
public interface ISVMServiceRegistry {

    ISVMService getService(String serviceName);

    void addService(SVMServiceConfig serviceConfig, boolean overwrite);

}
