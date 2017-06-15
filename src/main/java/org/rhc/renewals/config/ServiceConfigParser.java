package org.rhc.renewals.config;

import org.rhc.renewals.errors.ServiceConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nbalkiss on 6/12/17.
 */
public class ServiceConfigParser {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceConfigParser.class);

    private static final String URL = "url";

    private static final String SERVICE_NAME = "name";

    private static final String SERVICES = "services";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    private static final String TOKEN = "token";

    private static final String TIMEOUT = "timeout";

    private static final String RETRIES = "retries";

    private static final int TIMEOUT_DEFAULT_MILLIS = 10000;

    private static final int RETRIES_DEFAULT = 0;

    public static List<SVMServiceConfig> parseYAMLConfig(InputStream yamlResource) throws ServiceConfigurationException {

        try {

            List<SVMServiceConfig> serviceConfigs = new ArrayList<>();

            Object obj = new Yaml().load(yamlResource);

            Map<String, List<Map<String, Object>>> servicesConfig = (Map<String, List<Map<String, Object>>>) obj;

            List<Map<String, Object>> services = servicesConfig.get(SERVICES);

            for (Map<String, Object> serviceConfigMap : services) {

                String serviceName = (String) serviceConfigMap.get(SERVICE_NAME);

                if(serviceName == null){

                    throw new ServiceConfigurationException("Property 'name' cannot be null in the service configuration file");
                }

                LOG.debug("Loading YAML formatted service configuration for service: {} ", serviceName);

                String url = (String) serviceConfigMap.get(URL);

                if(url == null){

                    throw new ServiceConfigurationException("Property 'name' cannot be null in service configuration file");
                }

                String username =  (String) serviceConfigMap.get(USERNAME);

                String password = (String) serviceConfigMap.get(PASSWORD);

                String token = (String) serviceConfigMap.get(TOKEN);

                Object timeoutObj = serviceConfigMap.get(TIMEOUT);

                Object retriesObj = serviceConfigMap.get(RETRIES);

                int timeout = TIMEOUT_DEFAULT_MILLIS;

                if(!(timeoutObj == null) && timeoutObj instanceof Integer){

                    timeout = (Integer) timeoutObj;
                }
                else if(timeoutObj != null){

                    throw new ServiceConfigurationException("timeout is not an integer");
                }

                int retries = RETRIES_DEFAULT;

                if(!(retriesObj == null) && retriesObj instanceof Integer){

                    retries = (Integer) retriesObj;
                }
                else if(retriesObj != null){

                    throw new ServiceConfigurationException("retries is not an integer");
                }

                SVMServiceConfig config = new SVMServiceConfig(serviceName, url, username, password, token, timeout, retries);

                serviceConfigs.add(config);
            }

            return serviceConfigs;

        }
        catch (Exception e) {
            throw new ServiceConfigurationException("Failed to parse YAML configuration. " + e.getMessage());
        }

    }

}
