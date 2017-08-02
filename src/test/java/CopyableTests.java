import org.junit.Assert;
import org.junit.Test;
import org.bpm.workflow.models.IncidentData;
import org.bpm.workflow.models.PaymentData;

/**
 * Created by nbalkiss on 7/13/17.
 */
public class CopyableTests {

    @Test
    public void testIncidentDataCopy(){

        IncidentData first = new IncidentData(null,null,null);

        first.setId(123L);

        IncidentData second = new IncidentData("123","abc","xyz");

        Assert.assertTrue(first.copy(second));

        Assert.assertEquals(first.getSupportActivityId(),"123");

        Assert.assertEquals(first.getOrganizationId(),"abc");

        Assert.assertEquals(first.getIncidentType(),"xyz");

        Assert.assertTrue(first.getId()==123L);

    }

    @Test
    public void testPaymentDataCopy(){

        PaymentData first = new PaymentData(null,null);

        first.setId(123L);

        PaymentData second = new PaymentData("123","abc");

        Assert.assertTrue(first.copy(second));

        Assert.assertEquals(first.getPaymentId(),"123");

        Assert.assertEquals(first.getRetryId(),"abc");

        Assert.assertTrue(first.getId()==123L);
    }
}
