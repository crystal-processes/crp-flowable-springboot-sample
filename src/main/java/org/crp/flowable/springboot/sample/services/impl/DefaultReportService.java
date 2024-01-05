package org.crp.flowable.springboot.sample.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.crp.flowable.springboot.sample.entities.Contract;
import org.crp.flowable.springboot.sample.services.ReportService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

public class DefaultReportService implements ReportService {

    @Autowired
    HistoryService historyService;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public ArrayNode accountAmountReport() {
        return getAllProcessInstances().stream().map(
                mapDataToJsonObject()
        ).collect(
                arrayNodeCollector()
        );
    }

    private Collector<ObjectNode, ArrayNode, ArrayNode> arrayNodeCollector() {
        return Collector.of(
                () -> objectMapper.createArrayNode(),
                ArrayNode::add,
                ArrayNode::addAll
        );
    }

    private Function<HistoricProcessInstance, ObjectNode> mapDataToJsonObject() {
        return process -> {
            Object contract = process.getProcessVariables().get("contract");
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (contract instanceof JsonNode jsonContract) {
                if (jsonContract.has("account")) {
                    JsonNode account = jsonContract.get("account");
                    if (account.has("id")) {
                        // version 0.2.3
                        objectNode.put("account", account.get("id").asText());
                    } else {
                        // version 0.2.2
                        objectNode.put("account", account.asText());
                    }
                } else {
                    throw new RuntimeException("Unexpected json contract format in process " + process.getId());
                }
            } else if (contract instanceof Contract javaSerializationContract) {
                // version 0.2.1 (version 0.2.0 java serialization was omitted).
                objectNode.put("account", javaSerializationContract.getAccount().getId());
            } else if (contract instanceof String stringContract) {
                objectNode.put("account", stringContract);
            } else {
                throw new RuntimeException("Unexpected contract format in process "+process.getId());
            }
            objectNode.put("amount", getAmount(process));
            return objectNode;
        };
    }

    private static Integer getAmount(HistoricProcessInstance process) {
        Object amount = process.getProcessVariables().get("amount");
        return amount == null ? 0 : (Integer) amount;
    }

    private List<HistoricProcessInstance> getAllProcessInstances() {
        return historyService.createHistoricProcessInstanceQuery().processDefinitionKey("P002-processInsuranceEvent")
                .includeProcessVariables().orderByProcessInstanceStartTime().asc().list();
    }
}
