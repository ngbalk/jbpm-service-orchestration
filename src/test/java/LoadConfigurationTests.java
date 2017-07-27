import org.junit.Assert;
import org.junit.Test;
import org.rhc.workflow.services.SVMServiceRegistry;

/**
 * Created by nbalkiss on 7/25/17.
 */
public class LoadConfigurationTests {

    @Test
    public void testSVMServiceRegistryCanLoadServicesConfigAsClasspathResource(){
        Assert.assertNotNull(SVMServiceRegistry.getInstance());
    }

}