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
public class SVMServiceRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(SVMServiceRegistry.class);

    private static SVMServiceRegistry instance = new SVMServiceRegistry();

    private Map<String, SVMService> services;

    private static final String SERVICES_CONFIG_LOCATION = "services.yml";

    private SVMServiceRegistry(){

        services = new HashMap<>();

        InputStream yamlResource = this.getClass().getClassLoader().getResourceAsStream(SERVICES_CONFIG_LOCATION);

        Object obj = new Yaml().load(yamlResource);

        Map <String,List <Map <String,Object>>>  servicesConfig = (Map<String,List <Map <String,Object>>>) obj;

        for(Map<String, Object> serviceConfigMap : servicesConfig.get("services")){

            String serviceName = (String) serviceConfigMap.get("name");

            LOG.debug("Loading service configuration for service: {} ", serviceName);

            SVMServiceConfig config = new SVMServiceConfig();
            config.setUrl((String) serviceConfigMap.get("url"));
            config.setUsername((String) serviceConfigMap.get("username"));
            config.setPassword((String) serviceConfigMap.get("password"));
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

    public synchronized static SVMServiceRegistry getInstance(){
        return instance;
    }

    public SVMService getService(String serviceName){
        return services.get(serviceName);
    }

    public void addService(String serviceName, SVMService service) {
        this.services.put(serviceName, service);
    }

}
