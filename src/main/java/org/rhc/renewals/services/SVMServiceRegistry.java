package org.rhc.renewals.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nbalkiss on 5/10/17.
 */
// TODO Add interface ISVMServiceRegistry
public class SVMServiceRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(SVMServiceRegistry.class);

    private static SVMServiceRegistry instance = new SVMServiceRegistry();

    private Map<String, SVMService> services;

    private static final String SERVICES_CONFIG_LOCATION = "services.yml";

    private SVMServiceRegistry(){

        // TODO Not concurrent map (to support put operation
        services = new HashMap<>();

        InputStream yamlResource = this.getClass().getClassLoader().getResourceAsStream(SERVICES_CONFIG_LOCATION);

        Object obj = new Yaml().load(yamlResource);

        Map <String,List <Map <String,Object>>>  servicesConfig = (Map<String,List <Map <String,Object>>>) obj;

        // TODO All harcoded names to constants
        for(Map<String, Object> serviceConfigMap : servicesConfig.get("services")){

            String serviceName = (String) serviceConfigMap.get("name");

            LOG.debug("Loading service configuration for service: {} ", serviceName);

            SVMServiceConfig config = new SVMServiceConfig();
            config.setUrl((String) serviceConfigMap.get("url"));
                config.setUsername((String) serviceConfigMap.get("username"));
            config.setPassword((String) serviceConfigMap.get("password"));

            // TODO Try to use serviceConfigMap.getOrDefault()
            if(serviceConfigMap.get("timeout")!=null){
                config.setTimeout((int) serviceConfigMap.get("timeout"));
            }


            if(serviceConfigMap.get("retry")!=null){
                config.setRetryTimes((int) serviceConfigMap.get("retry"));
            }
            if(serviceConfigMap.get("delay")!=null){
                config.setDelay((int) serviceConfigMap.get("delay"));
            }

            services.put(serviceName, new SVMService(config));

        }

    }

    // TODO Do we need synchronized ?
    public synchronized static SVMServiceRegistry getInstance(){
        return instance;
    }

    // TODO What happens if service is not found ?
    public SVMService getService(String serviceName){
        return services.get(serviceName);
    }

    // TODO Concurrency - service registry map is not concurrent map
    // TODO Add the setting to override existing service if service is already registered
    public void addService(String serviceName, SVMService service) {
        this.services.put(serviceName, service);
    }

}
