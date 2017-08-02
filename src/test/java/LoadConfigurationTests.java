import org.junit.Assert;
import org.junit.Test;
import org.bpm.workflow.services.ServiceRegistry;

/**
 * Created by nbalkiss on 7/25/17.
 */
public class LoadConfigurationTests {

    @Test
    public void testServiceRegistryCanLoadServicesConfigAsClasspathResource(){
        Assert.assertNotNull(ServiceRegistry.getInstance());
    }

}