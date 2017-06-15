package org.rhc.renewals.services;

import org.rhc.renewals.config.SVMServiceConfig;
import org.rhc.renewals.config.ServiceConfigParser;
import org.rhc.renewals.errors.ServiceConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nbalkiss on 5/10/17.
 */
public class SVMServiceRegistry implements ISVMServiceRegistry{

    private static final Logger LOG = LoggerFactory.getLogger(SVMServiceRegistry.class);

    private static SVMServiceRegistry instance = new SVMServiceRegistry();

    private boolean initialized = false;

    private Map<String, ISVMService> services;

    protected static final String DEFAULT_SERVICES_CONFIG_VALUE = "classpath:/org.rhc.renewals.service.config.yml";

    protected static final String SERVICES_CONFIG_PROP_NAME = "org.rhc.renewals.service.config.location";

    private SVMServiceRegistry() {

        services = new ConcurrentHashMap<>();

        final String propertiesLocation = System.getProperty(SERVICES_CONFIG_PROP_NAME);

        List<SVMServiceConfig> configs = readServicesConfig(propertiesLocation, DEFAULT_SERVICES_CONFIG_VALUE);

        if(configs != null){

            for(SVMServiceConfig config : configs){

                services.put(config.getName(), new SVMServiceREST(config));
            }

            initialized = true;

            LOG.debug("SVMServiceRegistry initialized status = {}",initialized);
        }
        else{

            LOG.error("Service configurations were not able to be loaded, this could cause problems. SVMServiceRegistry initialized status = {}", initialized);
        }
    }

    public boolean isInitialized(){
        return initialized;
    }

    public static SVMServiceRegistry getInstance(){
        return instance;
    }

    @Override
    public ISVMService getService(String serviceName){

        if(!services.containsKey(serviceName)){

            throw new ServiceConfigurationException("Service with name " + serviceName + " not found.");
        }
        return services.get(serviceName);
    }

    @Override
    public void addService(SVMServiceConfig serviceConfig, boolean overwrite) {

        this.services.put(serviceConfig.getName(), new SVMServiceREST(serviceConfig));
    }

    protected List<SVMServiceConfig> readServicesConfig(String configLocation, String defaultProperties) {

        URL locationUrl = null;

        if (configLocation == null) {

            configLocation = defaultProperties;
        }

        LOG.debug("Service configuration will be loaded from {}", configLocation);

        if (configLocation.startsWith("classpath:")) {

            String strippedLocation = configLocation.replaceFirst("classpath:", "");

            locationUrl = this.getClass().getResource(strippedLocation);

            if (locationUrl == null) {

                locationUrl = Thread.currentThread().getContextClassLoader().getResource(strippedLocation);
            }
        }
        else {

            try {

                locationUrl = new URL(configLocation);
            }
            catch (MalformedURLException e) {

                locationUrl = this.getClass().getResource(configLocation);

                if (locationUrl == null) {

                    locationUrl = Thread.currentThread().getContextClassLoader().getResource(configLocation);
                }
            }
        }

        List<SVMServiceConfig> configs = null;

        if (locationUrl != null) {
            try {

                configs = ServiceConfigParser.parseYAMLConfig(locationUrl.openStream());
            }
            catch (Exception e) {

                LOG.error("Error when loading services configuration ", e);
            }
        }
        else{

            LOG.error("Failed to load configuration from location {}  Make sure that this location exists",configLocation);
        }

        return configs;
    }

}
