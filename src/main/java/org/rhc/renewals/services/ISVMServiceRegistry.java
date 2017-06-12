package org.rhc.renewals.services;

/**
 * Created by nbalkiss on 6/12/17.
 */
public interface ISVMServiceRegistry {

    ISVMService getService(String serviceName);
}
