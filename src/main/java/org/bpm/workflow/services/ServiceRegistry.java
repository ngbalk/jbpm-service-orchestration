package org.bpm.workflow.services;

import org.bpm.workflow.config.ServiceConfig;
import org.bpm.workflow.config.ServiceConfigParser;
import org.bpm.workflow.errors.ServiceConfigurationException;
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
public class ServiceRegistry implements IServiceRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistry.class);

    private static ServiceRegistry instance = new ServiceRegistry();

    private boolean initialized = false;

    private Map<String, IService> services;

    protected static final String DEFAULT_SERVICES_CONFIG_VALUE = "file:///opt/jboss-as/org.bpm.workflow.service.config.yml";

    protected static final String SERVICES_CONFIG_PROP_NAME = "org.bpm.workflow.service.config.location";

    private ServiceRegistry() {

        LOG.debug("Starting initializing ServiceRegistry");

        services = new ConcurrentHashMap<>();

        final String propertiesLocation = System.getProperty(SERVICES_CONFIG_PROP_NAME);

        List<ServiceConfig> configs = readServicesConfig(propertiesLocation, DEFAULT_SERVICES_CONFIG_VALUE);

        if(configs != null){

            for(ServiceConfig config : configs){

                services.put(config.getName(), new ServiceREST(config));
            }

            initialized = true;

            LOG.debug("ServiceRegistry initialized status = {}",initialized);
        }
        else{

            LOG.error("Service configurations were not able to be loaded, this could cause problems. ServiceRegistry initialized status = {}", initialized);
        }
    }

    public boolean isInitialized(){
        return initialized;
    }

    public static ServiceRegistry getInstance(){
        return instance;
    }

    @Override
    public IService getService(String serviceName){

        if(!services.containsKey(serviceName)){

            throw new ServiceConfigurationException("Service with name " + serviceName + " not found.");
        }
        return services.get(serviceName);
    }

    @Override
    public void addService(ServiceConfig serviceConfig, boolean overwrite) {

        services.put(serviceConfig.getName(), new ServiceREST(serviceConfig));

        initialized = true;
    }

    protected List<ServiceConfig> readServicesConfig(String configLocation, String defaultProperties) {

        URL locationUrl = null;

        if (configLocation == null) {

            configLocation = defaultProperties;
        }

        LOG.debug("Service configuration will be loaded from {}", configLocation);

        if (configLocation.startsWith("classpath:")) {

            String strippedLocation = configLocation.replaceFirst("classpath:", "");

            LOG.debug("Loading classpath resource for service configuration from {}", strippedLocation);

            locationUrl = this.getClass().getResource(strippedLocation);

            if (locationUrl == null) {

                locationUrl = Thread.currentThread().getContextClassLoader().getResource(strippedLocation);
            }
        }
        else {

            LOG.debug("Loading service configuration directly from {}", configLocation);

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

        List<ServiceConfig> configs = null;

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
