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
    protected Expression groups;
    protected Expression documentsVariableName;

    @Override
    public void execute(DelegateExecution execution) {
        LOG.debug("Adding documents to vector store with metadata.");

        String documentsId = CommandContextUtil.getProcessEngineConfiguration().getIdGenerator().getNextId();
        List<Document> documentInstances = addMetadataToDocuments(getDocuments(execution), execution, documentsId);
        getVectorStore(execution).add(documentInstances);
        getAllowedUsers(execution).forEach(userId -> addUserIdentityLinkToDocuments(userId, documentsId));
        getAllowedGroups(execution).forEach(groupId -> addGroupIdentityLinkToDocuments(groupId, documentsId));

        Optional.ofNullable(getDocumentsVariableName(execution))
                .ifPresent(docVarName -> execution.setVariable(docVarName, documentInstances.stream().map(Document::getId).toList()));
        LOG.debug("Documents added to vector store with metadata.");
    }

    private static void addUserIdentityLinkToDocuments(String userId, String documentsId) {
            IdentityLinkEntity identityLink = CommandContextUtil.getIdentityLinkService().createIdentityLink();
            identityLink.setScopeId(documentsId);
            identityLink.setScopeType("vector");
            identityLink.setUserId(userId);
            CommandContextUtil.getIdentityLinkService().insertIdentityLink(identityLink);
    }

    private static void addGroupIdentityLinkToDocuments(String groupId, String documentsId) {
            IdentityLinkEntity identityLink = CommandContextUtil.getIdentityLinkService().createIdentityLink();
            identityLink.setScopeId(documentsId);
            identityLink.setScopeType("vector");
            identityLink.setGroupId(groupId);
            CommandContextUtil.getIdentityLinkService().insertIdentityLink(identityLink);
    }

    private List<Document> getDocuments(DelegateExecution execution) {
        return new TikaDocumentReader(getDocumentUrl(execution)).get();
    }

    private List<Document> addMetadataToDocuments(List<Document> documents, DelegateExecution execution, String documentsId) {
        HashMap<String, Object> additionalMetadata = getAdditionalMetadata(execution, documentsId);
        
        return documents.stream()
                .map(document -> {
					// Add additional metadata to the document
                    document.getMetadata().putAll(additionalMetadata);

					return Document.builder()
                            .text(document.getText())
                            .metadata(document.getMetadata())
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

    private Collection<String> getAllowedGroups(DelegateExecution execution) {
        return Objects.requireNonNullElse(ExpressionsHelper.getValue(groups, execution, Collection.class), Collections.<String>emptySet());
    }

    private String getDocumentsVariableName(DelegateExecution execution) {
        return Objects.requireNonNullElse(ExpressionsHelper.getValue(documentsVariableName, execution, String.class), null);
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, Object> getAdditionalMetadata(DelegateExecution execution, String documentsId) {
        Map<String, Object> metadataFromModel = ExpressionsHelper.getValue(metadata, execution, Map.class);
        HashMap<String, Object> additionalMetadata = new HashMap<>();
        if (metadataFromModel != null) {
            additionalMetadata.putAll(metadataFromModel);
        }
        additionalMetadata.put("documentsId", documentsId);
        return additionalMetadata;
    }
}
