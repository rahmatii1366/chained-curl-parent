package ir.piana.dev.chainedcurl.core.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ir.piana.dev.chainedcurl.core.service.exp.ExpressionSourceType;
import ir.piana.dev.jsonparser.json.JsonTarget;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class ChainedCurlContext {
    @JsonProperty("context-key")
    private final String uuid;
    @JsonProperty("initial-values")
    private final ChainedCurlStepContext initialContext;
    @JsonProperty("context")
    private final Map<String, ChainedCurlStepContext> contextMap;
    @JsonIgnore
    private String lastStepName;

    public ChainedCurlContext(String uuid) {
        this.uuid = uuid;
        contextMap = new LinkedHashMap<>();
        initialContext = new ChainedCurlStepContext();
    }

    ChainedCurlStepContext newStep(String stepName) {
        if (Objects.nonNull(stepName) && !contextMap.containsKey(stepName)) {
            contextMap.put(stepName, new ChainedCurlStepContext());
            return contextMap.get(stepName);
        }
        throw new RuntimeException();
    }

    public void putInitiateValue(ExpressionSourceType sourceType, String key, String value) {
        switch (sourceType) {
            case FIX -> {
                initialContext.putFix(key, value);
            }
            case SUPPLIER -> {
                initialContext.putSupplier(key, value);
            }
            case INPUT -> {
                initialContext.putInput(key, value);
            }
            /*case EXTRACTED -> {
                initialContext.putExtracted(key, value);
            }*/
        }
    }

    public void putInputObject(String stepName, String key, String value) {
        contextMap.get(stepName).putInput(key, value);
    }

    @JsonIgnore
    public ChainedCurlStepContext getStepContext(String stepName) {
        if (stepName.trim().equalsIgnoreCase("provide"))
            return initialContext;
        return contextMap.getOrDefault(stepName, null);
    }

    @JsonIgnore
    public ChainedCurlStepContext getStepContext() {
        return contextMap.getOrDefault(lastStepName, null);
    }

    @JsonIgnore
    public ChainedCurlStepContext getCurrentStepContext() {
        return contextMap.get(lastStepName);
    }
}