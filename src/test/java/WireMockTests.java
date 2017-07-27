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
                .withRequestBody(equalToJson("{\"Data\":{\"incidentData\":{\"SupportActivityId\":\"abcdef\",\"OrganizationId\":\"xyz\",\"IncidentType\":\"Normal\"}},\"WorkerName\":\"wire-mock\",\"SignalInstanceInfo\":{\"ContainerId\":\"SVMContainer\",\"ProcessInstanceId\":1,\"SignalName\":\"A\"}}"))
                .withHeader("Authorization",equalTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkF1ZHBIa0tnS2lhZ3Q2U0pxajdVcjFRX3M5USJ9.eyJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjEzMzcvIiwiaXNzIjoiaHR0cDovL2FkZnMuc2VydmljZW1hc3Rlci5jb20vYWRmcy9zZXJ2aWNlcy90cnVzdCIsImlhdCI6MTQ2MzYwNzEyNiwiZXhwIjoxNTI2Njc5MTI2LCJ3aW5hY2NvdW50bmFtZSI6InN2YzNTY2FsZUZTR2Vjb21tIiwiZ3JvdXAiOlsiQklaIFNlcnZpY2UgQWNjb3VudHMiLCJEb21haW4gVXNlcnMiLCJTZXJ2aWNlIEFjY291bnRzIExvZ29uIERlbnkiXSwiYXV0aF90aW1lIjoiMjAxNi0wNS0xOFQyMTozMjowNi4zODZaIiwiYXV0aG1ldGhvZCI6InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphYzpjbGFzc2VzOlBhc3N3b3JkUHJvdGVjdGVkVHJhbnNwb3J0IiwidmVyIjoiMS4wIiwiYXBwaWQiOiIzc2NhbGVhcGl0b2tlbiJ9.mRZPeJvapHEK70QKMkSakVkg16AKnEzF6Xcx6IVKouJaXYGdHx2o1QWlIGrS_UUpSNuYiDULwGbiNoKQx4oQi3and-LuQALN0mDqigB34wqG90RbNHC5cN-K29X2oAT8bJdKazeELOwO19keExNSfdD_8KZTg0ebgcIYgvjVCvx3zvMEcMfE5Y5mw-9kXznZFTuWRJyjmqRhq19WEB08rCDD_Eg9hRy9b1fn3gNDnhQ-fbbDqfK7z2Nht9RckjNslF0nVLH41a-WNsV6kkNcObl68bfWAffZNG9KAwGBxr7oWwmGyHhR_NGZ8ixCiZxkaUfqhxexupbys46Rp7iIuQ")));

    }

    @Test
    public void testAuthorizationHeaderSet() throws Exception{

        stubFor(post(urlEqualTo("/wire-mock"))
                .willReturn(aResponse()
                        .withStatus(200)));

        ServiceHandler executor = new ServiceHandler(new StateContext(new HashMap<>(), ServiceState.NOT_STARTED));

        ServiceRequest request = RequestBuilder.get().addSignalName("A").addServiceName("wire-mock").buildRequest();

        executor.execute(request);

        verify(postRequestedFor(urlMatching("/wire-mock"))
                .withHeader("Authorization",equalTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkF1ZHBIa0tnS2lhZ3Q2U0pxajdVcjFRX3M5USJ9.eyJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjEzMzcvIiwiaXNzIjoiaHR0cDovL2FkZnMuc2VydmljZW1hc3Rlci5jb20vYWRmcy9zZXJ2aWNlcy90cnVzdCIsImlhdCI6MTQ2MzYwNzEyNiwiZXhwIjoxNTI2Njc5MTI2LCJ3aW5hY2NvdW50bmFtZSI6InN2YzNTY2FsZUZTR2Vjb21tIiwiZ3JvdXAiOlsiQklaIFNlcnZpY2UgQWNjb3VudHMiLCJEb21haW4gVXNlcnMiLCJTZXJ2aWNlIEFjY291bnRzIExvZ29uIERlbnkiXSwiYXV0aF90aW1lIjoiMjAxNi0wNS0xOFQyMTozMjowNi4zODZaIiwiYXV0aG1ldGhvZCI6InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphYzpjbGFzc2VzOlBhc3N3b3JkUHJvdGVjdGVkVHJhbnNwb3J0IiwidmVyIjoiMS4wIiwiYXBwaWQiOiIzc2NhbGVhcGl0b2tlbiJ9.mRZPeJvapHEK70QKMkSakVkg16AKnEzF6Xcx6IVKouJaXYGdHx2o1QWlIGrS_UUpSNuYiDULwGbiNoKQx4oQi3and-LuQALN0mDqigB34wqG90RbNHC5cN-K29X2oAT8bJdKazeELOwO19keExNSfdD_8KZTg0ebgcIYgvjVCvx3zvMEcMfE5Y5mw-9kXznZFTuWRJyjmqRhq19WEB08rCDD_Eg9hRy9b1fn3gNDnhQ-fbbDqfK7z2Nht9RckjNslF0nVLH41a-WNsV6kkNcObl68bfWAffZNG9KAwGBxr7oWwmGyHhR_NGZ8ixCiZxkaUfqhxexupbys46Rp7iIuQ")));

    }
}
