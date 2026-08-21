package org.crp.flowable.springboot.sample.ai.delegates;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.*;

/**
 * Java Delegate that adds documents to a VectorStore with additional metadata.
 * Extends the basic AddToVectorStore functionality by adding the process instance ID
 * and optionally other metadata to the documents.
 */
public class AddToVectorStoreWithMetadataJavaDelegate implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(AddToVectorStoreWithMetadataJavaDelegate.class);

    protected Expression vectorStore;
    protected Expression documentUrl;
    protected Expression metadata;
    protected Expression users;

    @Override
    public void execute(DelegateExecution execution) {
        LOG.debug("Adding documents to vector store with metadata.");

        List<Document> documentInstances = addMetadataToDocuments(getDocuments(execution), execution);
        getVectorStore(execution).add(documentInstances);
        getAllowedUsers(execution).forEach(userId -> addUserIdentityLinkToDocuments(userId, documentInstances));
        LOG.debug("Documents added to vector store with metadata.");
    }

    private static void addUserIdentityLinkToDocuments(String userId, List<Document> documentInstances) {
        documentInstances.forEach(doc -> {
            IdentityLinkEntity identityLink = CommandContextUtil.getIdentityLinkService().createIdentityLink();
            identityLink.setScopeId(doc.getId());
            identityLink.setScopeType("vector");
            identityLink.setUserId(userId);
            CommandContextUtil.getIdentityLinkService().insertIdentityLink(identityLink);
            });
    }

    private List<Document> getDocuments(DelegateExecution execution) {
        return new TikaDocumentReader(getDocumentUrl(execution)).get();
    }

    private List<Document> addMetadataToDocuments(List<Document> documents, DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        Map<String, Object> additionalMetadata = getAdditionalMetadata(execution);
        
        return documents.stream()
                .map(document -> {
                    Map<String, Object> newMetadata = new HashMap<>();
                    
                    // Add existing metadata from the document
                    if (document.getMetadata() != null) {
                        newMetadata.putAll(document.getMetadata());
                    }

                    // Add any additional metadata from expression
                    if (additionalMetadata != null) {
                        newMetadata.putAll(additionalMetadata);
                    }

                    Document.Builder documentBuilder = Document.builder()
                            .text(document.getText());
                    if (newMetadata != null) {
                        documentBuilder.metadata(newMetadata);
                    }
                    return documentBuilder
                            .metadata(newMetadata)
                            .build();
                })
                .toList();
    }

    private String getDocumentUrl(DelegateExecution execution) {
        return ExpressionsHelper.getMandatoryValue("documentUrl", documentUrl, execution, String.class);
    }

    private VectorStore getVectorStore(DelegateExecution execution) {
        return ExpressionsHelper.getMandatoryValue("vectorStore", vectorStore, execution, VectorStore.class);
    }

    private Collection<String> getAllowedUsers(DelegateExecution execution) {
        return Objects.requireNonNullElse(ExpressionsHelper.getValue(users, execution, Collection.class), Collections.<String>emptySet());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getAdditionalMetadata(DelegateExecution execution) {
        return ExpressionsHelper.getValue(metadata, execution, Map.class);
    }
}
