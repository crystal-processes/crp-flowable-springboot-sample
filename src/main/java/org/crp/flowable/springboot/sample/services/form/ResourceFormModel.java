package org.crp.flowable.springboot.sample.services.form;

import org.apache.commons.io.IOUtils;
import org.crp.flowable.springboot.sample.config.impl.FormEngineException;
import org.flowable.form.api.FormModel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResourceFormModel implements FormModel, DirectFormModel {

    private final String formDefinitionKey;

    public ResourceFormModel(String formDefinitionKey) {
        this.formDefinitionKey = formDefinitionKey;
    }

    @Override
    public String getForm() {
        try {
            return IOUtils.resourceToString(formDefinitionKey.startsWith("/") ? formDefinitionKey: "/"+formDefinitionKey, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FormEngineException("Unable to load form "+ formDefinitionKey, e);
        }
    }
}
