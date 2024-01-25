package org.crp.flowable.springboot.sample;

import com.fasterxml.jackson.databind.JsonNode;
import org.flowable.common.rest.api.DataResponse;
import org.flowable.dmn.rest.service.api.repository.DmnDeploymentResponse;
import org.flowable.rest.service.api.identity.GroupResponse;
import org.flowable.rest.service.api.repository.ProcessDefinitionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@AcmeApplicationTest
@AutoConfigureWebClient(registerRestTemplate = true)
public class RestApiApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int serverPort;

    @Test
    public void testRestApiIntegration() {
        String processDefinitionsUrl = "http://localhost:" + serverPort + "/process-api/repository/process-definitions";

        ResponseEntity<DataResponse<ProcessDefinitionResponse>> response = restTemplate
            .exchange(processDefinitionsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {

            });

        assertThat(response.getStatusCode())
            .as("Status code")
            .isEqualTo(HttpStatus.OK);
        DataResponse<ProcessDefinitionResponse> processDefinitions = response.getBody();
        assertThat(processDefinitions).extracting(DataResponse::getTotal).isEqualTo(4L);
        assert processDefinitions != null;
        assertThat(processDefinitions.getData()).as("Deployed process definitions must contain exactly one Hello World process")
                .extracting(ProcessDefinitionResponse::getKey)
                .containsExactlyInAnyOrder("P001-helloWorld", "P002-processInsuranceEvent",
                        "P003-jpaProcessInsuranceEvent", "P004-jpaServicesProcessInsuranceEvent");
    }

    @Test
    public void testCmmnRestApiIntegrationNotFound() {
        String processDefinitionsUrl = "http://localhost:" + serverPort + "/cmmn-api/cmmn-repository/case-definitions/does-not-exist";

        ResponseEntity<String> response = restTemplate.getForEntity(processDefinitionsUrl, String.class);

        BasicJsonTester jsonTester = new BasicJsonTester(getClass());

        assertThat(jsonTester.from(response.getBody())).isEqualToJson("{"
            + "\"message\": \"Not found\","
            + "\"exception\": \"no deployed case definition found with id 'does-not-exist'\""
            + "}");
        assertThat(response.getStatusCode())
            .as("Status code")
            .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDmnRestApiIntegration() {
        String processDefinitionsUrl = "http://localhost:" + serverPort + "/dmn-api/dmn-repository/deployments";

        ResponseEntity<DataResponse<DmnDeploymentResponse>> response = restTemplate
            .exchange(processDefinitionsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            });

        assertThat(response.getStatusCode())
            .as("Status code")
            .isEqualTo(HttpStatus.OK);
        DataResponse<DmnDeploymentResponse> deployments = response.getBody();
        assertThat(deployments).isNotNull();
        assertThat(deployments.getData())
            .isEmpty();
        assertThat(deployments.getTotal()).isZero();
    }
    @Test
    public void testIdmRestApiIntegration() {
        String processDefinitionsUrl = "http://localhost:" + serverPort + "/idm-api/groups";

        ResponseEntity<DataResponse<GroupResponse>> response = restTemplate
            .exchange(processDefinitionsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            });

        assertThat(response.getStatusCode())
            .as("Status code")
            .isEqualTo(HttpStatus.OK);
        DataResponse<GroupResponse> groups = response.getBody();
        assertThat(groups).isNotNull();
        assertThat(groups.getData())
            .extracting(GroupResponse::getId, GroupResponse::getType, GroupResponse::getName, GroupResponse::getUrl)
            .containsExactly(
                tuple("user", "security-role", "users", null)
            );
        assertThat(groups.getTotal()).isEqualTo(1);
    }

    @Test
    public void testExternalJobRestApiIntegration() {
        String url = "http://localhost:" + serverPort + "/external-job-api/jobs";

        ResponseEntity<DataResponse<JsonNode>> response = restTemplate
                .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode())
                .as("Status code")
                .isEqualTo(HttpStatus.OK);
        DataResponse<JsonNode> jobs = response.getBody();
        assertThat(jobs).isNotNull();
        assertThat(jobs.getTotal()).isZero();
        assertThat(jobs.getData()).isEmpty();
    }

    @Test
    public void testExternalJobRestApiIntegrationNotFound() {
        String url = "http://localhost:" + serverPort + "/external-job-api/jobs/does-not-exist";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        BasicJsonTester jsonTester = new BasicJsonTester(getClass());

        assertThat(jsonTester.from(response.getBody())).isEqualToJson("{"
                + "\"message\": \"Not found\","
                + "\"exception\": \"Could not find external worker job with id 'does-not-exist'.\""
                + "}");
        assertThat(response.getStatusCode())
                .as("Status code")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
