package org.crp.flowable.springboot.sample;

import tools.jackson.databind.JsonNode;
import org.flowable.common.rest.api.DataResponse;
import org.flowable.dmn.rest.service.api.repository.DmnDeploymentResponse;
import org.flowable.rest.service.api.identity.GroupResponse;
import org.flowable.rest.service.api.repository.ProcessDefinitionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webclient.test.autoconfigure.AutoConfigureWebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@AcmeApplicationTest
@AutoConfigureWebClient
@AutoConfigureTestRestTemplate
public class RestApiApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testRestApiIntegration() {
        String processDefinitionsUrl = "http://localhost:" + serverPort + "/process-api/repository/process-definitions";

        ResponseEntity<DataResponse<ProcessDefinitionResponse>> response = restTemplate
            .exchange(processDefinitionsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<DataResponse<ProcessDefinitionResponse>>() {});

        assertThat(response.getStatusCode())
            .as("Status code")
            .isEqualTo(HttpStatus.OK);
        DataResponse<ProcessDefinitionResponse> processDefinitions = response.getBody();
        assertThat(processDefinitions).extracting(DataResponse::getTotal).isEqualTo(6L);
        assert processDefinitions != null;
        assertThat(processDefinitions.getData()).as("Deployed process definitions must contain exactly one Hello World process")
                .extracting(ProcessDefinitionResponse::getKey)
                .containsExactlyInAnyOrder( "A001-migration", "P001-helloWorld", "P002-processInsuranceEvent",
                        "P003-jpaProcessInsuranceEvent", "P004-jpaServicesProcessInsuranceEvent",
                        "P030-processInsuranceEvent");
    }

    @Test
    public void testCmmnRestApiIntegrationNotFound() {
        BasicJsonTester jsonTester = new BasicJsonTester(getClass());

        webTestClient.get()
            .uri("/cmmn-api/cmmn-repository/case-definitions/does-not-exist")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(String.class)
            .consumeWith(response -> {
                String body = response.getResponseBody();
                assertThat(jsonTester.from(body)).isEqualToJson("{" +
                        "\"message\": \"Not found\"," +
                        "\"exception\": \"no deployed case definition found with id 'does-not-exist'\"" +
                        "}");
            });
    }

    @Test
    public void testDmnRestApiIntegration() {
        String processDefinitionsUrl = "http://localhost:" + serverPort + "/dmn-api/dmn-repository/deployments";

        ResponseEntity<DataResponse<DmnDeploymentResponse>> response = restTemplate
            .exchange(processDefinitionsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<DataResponse<DmnDeploymentResponse>>() {});

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
            .exchange(processDefinitionsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<DataResponse<GroupResponse>>() {});

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
                .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<DataResponse<JsonNode>>() {});

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
        BasicJsonTester jsonTester = new BasicJsonTester(getClass());

        webTestClient.get()
            .uri("/external-job-api/jobs/does-not-exist")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(String.class)
            .consumeWith(response -> {
                String body = response.getResponseBody();
                assertThat(jsonTester.from(body)).isEqualToJson("{" +
                        "\"message\": \"Not found\"," +
                        "\"exception\": \"Could not find external worker job with id 'does-not-exist'.\"" +
                        "}");
            });
    }
}
