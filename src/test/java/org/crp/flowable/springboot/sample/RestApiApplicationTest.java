package org.crp.flowable.springboot.sample;

import com.fasterxml.jackson.databind.JsonNode;
import org.flowable.common.rest.api.DataResponse;
import org.flowable.dmn.rest.service.api.repository.DmnDeploymentResponse;
import org.flowable.rest.service.api.identity.GroupResponse;
import org.flowable.rest.service.api.repository.ProcessDefinitionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@AcmeApplicationTest
@AutoConfigureWebTestClient
public class RestApiApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testRestApiIntegration() {
        webTestClient.get()
            .uri("/process-api/repository/process-definitions")
            .exchange()
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<DataResponse<ProcessDefinitionResponse>>() {})
            .consumeWith(response -> {
                DataResponse<ProcessDefinitionResponse> processDefinitions = response.getResponseBody();
                assertThat(processDefinitions).isNotNull();
                assertThat(processDefinitions).extracting(DataResponse::getTotal).isEqualTo(4L);
                assertThat(processDefinitions.getData()).as("Deployed process definitions must contain exactly one Hello World process")
                        .extracting(ProcessDefinitionResponse::getKey)
                        .containsExactlyInAnyOrder("P001-helloWorld", "P002-processInsuranceEvent",
                                "P003-jpaProcessInsuranceEvent", "P004-jpaServicesProcessInsuranceEvent");
            });
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
        webTestClient.get()
            .uri("/dmn-api/dmn-repository/deployments")
            .exchange()
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<DataResponse<DmnDeploymentResponse>>() {})
            .consumeWith(response -> {
                DataResponse<DmnDeploymentResponse> deployments = response.getResponseBody();
                assertThat(deployments).isNotNull();
                assertThat(deployments.getData()).isEmpty();
                assertThat(deployments.getTotal()).isZero();
            });
    }

    @Test
    public void testIdmRestApiIntegration() {
        webTestClient.get()
            .uri("/idm-api/groups")
            .exchange()
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<DataResponse<GroupResponse>>() {})
            .consumeWith(response -> {
                DataResponse<GroupResponse> groups = response.getResponseBody();
                assertThat(groups).isNotNull();
                assertThat(groups.getData())
                        .extracting(GroupResponse::getId, GroupResponse::getType, GroupResponse::getName, GroupResponse::getUrl)
                        .containsExactly(
                                tuple("user", "security-role", "users", null)
                        );
                assertThat(groups.getTotal()).isEqualTo(1);
            });
    }

    @Test
    public void testExternalJobRestApiIntegration() {
        webTestClient.get()
            .uri("/external-job-api/jobs")
            .exchange()
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<DataResponse<JsonNode>>() {})
            .consumeWith(response -> {
                DataResponse<JsonNode> jobs = response.getResponseBody();
                assertThat(jobs).isNotNull();
                assertThat(jobs.getTotal()).isZero();
                assertThat(jobs.getData()).isEmpty();
            });
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
