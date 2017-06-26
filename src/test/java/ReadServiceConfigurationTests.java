import org.junit.Assert;
import org.junit.Test;
import org.rhc.workflow.services.SVMServiceRegistry;

/**
 * Created by nbalkiss on 6/14/17.
 */
public class ReadServiceConfigurationTests {

    @Test
    public void testSVMServiceRegistryCanLoadServicesConfigAsClasspathResource(){
        Assert.assertNotNull(SVMServiceRegistry.getInstance());
    }
}
