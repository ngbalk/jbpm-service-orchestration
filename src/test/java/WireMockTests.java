import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.rhc.workflow.common.RequestBuilder;
import org.rhc.workflow.common.ServiceRequest;
import org.rhc.workflow.common.StateContext;
import org.rhc.workflow.models.IncidentData;
import org.rhc.workflow.services.ServiceHandler;
import org.rhc.workflow.states.ServiceState;

import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


/**
 * Created by nbalkiss on 7/11/17.
 */
public class WireMockTests {

    private final int WIRE_MOCK_PORT = 8089;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIRE_MOCK_PORT);

    @Test
    public void testMarshallWithCustomDomainDataObject() throws Exception{

        stubFor(post(urlEqualTo("/wire-mock"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Hello, World!")));

        StateContext context = new StateContext(new HashMap<>(), ServiceState.NOT_STARTED);
        ServiceHandler executor = new ServiceHandler(context);
        HashMap<String, Object> data = new HashMap<>();

        IncidentData incidentData = new IncidentData("abcdef","xyz","Normal");

        incidentData.setId(123L);

        data.put("incidentData", incidentData);

        ServiceRequest request =
                RequestBuilder.get()
                        .addData(data)
                        .addContainerId("SVMContainer")
                        .addProcessInstanceId(1L)
                        .addSignalName("A")
                        .addServiceName("wire-mock")
                        .buildRequest();

        executor.execute(request);

        verify(postRequestedFor(urlMatching("/wire-mock"))
                .withRequestBody(equalToJson("{\"Data\":{\"incidentData\":{\"ID\":123,\"SupportActivityId\":\"abcdef\",\"OrganizationId\":\"xyz\",\"IncidentType\":\"Normal\"}},\"WorkerName\":\"wire-mock\",\"SignalInstanceInfo\":{\"ContainerId\":\"SVMContainer\",\"ProcessInstanceId\":1,\"SignalName\":\"A\"}}")));

    }
}
