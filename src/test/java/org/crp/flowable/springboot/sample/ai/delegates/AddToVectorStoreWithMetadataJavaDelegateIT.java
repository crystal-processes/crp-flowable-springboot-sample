package org.crp.flowable.springboot.sample.ai.delegates;

import org.apache.commons.io.IOUtils;
import org.crp.flowable.springboot.sample.AcmeApplicationTest;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.IdentityAwarePgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for AddToVectorStoreWithMetadataJavaDelegate using real PgVectorStore.
 * Uses Testcontainers to spin up PostgreSQL with pgvector extension and Ollama for embeddings.
 * Deploy a test BPMN process and create process instances to test the delegate.
 */
@AcmeApplicationTest
class AddToVectorStoreWithMetadataJavaDelegateIT {

    private static final String TEST_DOCUMENT_CONTENT = "This is a test document for vector store integration testing.";
    private static final String TEST_DOCUMENT_PATH = "target/test-docs/test-document-it.txt";
    private static final String PROCESS_DEFINITION_KEY = "testVectorStoreProcess";

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    IdentityAwarePgVectorStore vectorStore;
    @Autowired
    EmbeddingModel embeddingModel;
    private File testDocumentFile;
    private String deploymentId;

    @BeforeEach
    void setup() throws IOException, InterruptedException {
        // Initialize pgvector extension
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");

        // Ensure the nomic-embed-text model is pulled and ready
        pullOllamaModel("http://localhost:11434", "nomic-embed-text");

        // Create test document
        testDocumentFile = new File(TEST_DOCUMENT_PATH);
        testDocumentFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(testDocumentFile)) {
            writer.write(TEST_DOCUMENT_CONTENT);
        }

        // Deploy the test process
        deploymentId = repositoryService.createDeployment()
                .addClasspathResource("processes/test-vector-store.bpmn20.xml")
                .name("Test Vector Store Deployment")
                .deploy()
                .getId();
    }

    private void pullOllamaModel(String baseUrl, String modelName) throws IOException, InterruptedException {
        // Check if model exists
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new URL(baseUrl + "/api/tags").openConnection();
        conn.setRequestMethod("GET");
        String response = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8.name());
        conn.disconnect();

        if (!response.contains("\"name\":\"" + modelName + "\"")) {
            // Model not found, pull it
            conn = (java.net.HttpURLConnection) new URL(baseUrl + "/api/pull").openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            try (java.io.OutputStream os = conn.getOutputStream()) {
                String body = "{\"name\":\"" + modelName + "\"}";
                os.write(body.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
            conn.getInputStream().close();

            // Wait for model to be ready
            for (int i = 0; i < 60; i++) {
                Thread.sleep(5000);
                try {
                    conn = (java.net.HttpURLConnection) new URL(baseUrl + "/api/tags").openConnection();
                    conn.setRequestMethod("GET");
                    response = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8.name());
                    conn.disconnect();
                    if (response.contains(modelName)) {
                        return;
                    }
                } catch (Exception e) {
                    // Model not ready yet, continue waiting
                }
            }
            throw new IOException("Model " + modelName + " did not become ready within timeout");
        }
    }

    @AfterEach
    void cleanup() throws IOException {
        // Clean up deployment
        if (deploymentId != null) {
            repositoryService.deleteDeployment(deploymentId, true);
        }

        // Clean up test document
        Files.deleteIfExists(testDocumentFile.toPath());

        // Clean up vector store table
        jdbcTemplate.execute("delete from vector_store");
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void processExecutionAddsDocumentWithProcessInstanceIdMetadata() throws MalformedURLException {
        ProcessInstance processInstance = startProcessInstance(null, null);

        assertThat(processInstance).isNotNull();
        assertThat(processInstance.getId()).isNotNull();
        assertThat(processInstance.isEnded()).isTrue();

        // Verify documents were added to vector store with metadata
        List<Document> similarDocuments = vectorStore.similaritySearch(TEST_DOCUMENT_CONTENT);

        assertThat(similarDocuments).as("Nobody is allowed to access the vector").hasSize(0);
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void processExecutionAddsDocumentWithAdditionalMetadata() throws MalformedURLException {
        // Create additional metadata
        Map<String, Object> additionalMetadata = new HashMap<>();
        additionalMetadata.put("customKey", "customValue");
        additionalMetadata.put("documentType", "test");

        // Start a process instance with all variables including additional metadata
        Map<String, Object> variables = new HashMap<>();
        variables.put("documentUrl", new File(TEST_DOCUMENT_PATH).toURI().toURL().toString());
        variables.put("metadata", additionalMetadata);
        variables.put("users", null);

        startProcessInstance(additionalMetadata, null);

        // Verify documents were added with all metadata
        List<Document> similarDocuments = vectorStore.similaritySearch(TEST_DOCUMENT_CONTENT);

        assertThat(similarDocuments).as("Nobody is allowed to access the vector").hasSize(0);
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void withUserAccess() throws MalformedURLException {
        // Create additional metadata
        Map<String, Object> additionalMetadata = new HashMap<>();
        additionalMetadata.put("customKey", "customValue");
        additionalMetadata.put("documentType", "test");
        startProcessInstance(additionalMetadata, Set.of("testUser"));


        // Verify documents were added with all metadata
        List<Document> similarDocuments = vectorStore.similaritySearch(TEST_DOCUMENT_CONTENT);

        assertThat(similarDocuments).hasSize(1);
        Document storedDocument = similarDocuments.get(0);
        assertThat(storedDocument.getText()).contains(TEST_DOCUMENT_CONTENT);
        assertThat(storedDocument.getMetadata()).containsEntry("customKey", "customValue");
        assertThat(storedDocument.getMetadata()).containsEntry("documentType", "test");

    }

    private @NotNull ProcessInstance startProcessInstance(Map<String, Object> additionalMetadata, Set<String> testUsers) throws MalformedURLException {
        // Start a process instance with all variables including additional metadata
        Map<String, Object> variables = new HashMap<>();
        variables.put("documentUrl", new File(TEST_DOCUMENT_PATH).toURI().toURL().toString());
        variables.put("metadata", additionalMetadata);
        variables.put("users", testUsers);

        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey(PROCESS_DEFINITION_KEY)
                .variables(variables)
                .start();

        assertThat(processInstance);
        assertThat(processInstance.getId()).isNotNull();
        assertThat(processInstance.isEnded()).isTrue();
        return processInstance;
    }

}
