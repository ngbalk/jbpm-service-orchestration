package org.bpm.workflow.services;

import org.bpm.workflow.config.ServiceConfig;

/**
 * Created by nbalkiss on 6/12/17.
 */
public interface IServiceRegistry {

    IService getService(String serviceName);

    void addService(ServiceConfig serviceConfig, boolean overwrite);

}
