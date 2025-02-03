package ir.piana.dev.chainedcurl.core.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ir.piana.dev.chainedcurl.core.curl.CurlHeaders;
import ir.piana.dev.chainedcurl.core.service.exp.ExpressionSourceType;
import ir.piana.dev.jsonparser.json.JsonTarget;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public class ChainedCurlStepContext {
    @JsonProperty("suppliers")
    private final Map<String, String> supplierMap;
    @JsonProperty("fixes")
    private final Map<String, String> fixMap;
    @JsonProperty("inputs")
    private final Map<String, String> inputMap;
    @Getter(AccessLevel.NONE)
    private ChainedCurlStepResponse stepResponse;
    /*@JsonProperty("extracts")
    private final Map<String, String> extractedMap;*/

    @JsonIgnore
    public ChainedCurlStepResponse getBody() {
        return stepResponse;
    }

    public ChainedCurlStepContext() {
        this.supplierMap = new LinkedHashMap<>();
        this.fixMap = new LinkedHashMap<>();
        this.inputMap = new LinkedHashMap<>();
//        this.extractedMap = new LinkedHashMap<>();
    }

    public void putSupplier(String key, String value) {
        supplierMap.put(key, value);
    }

    public Optional<String> getSupplier(String key) {
        return Optional.ofNullable(supplierMap.getOrDefault(key, null));
    }

    public String getSupplier(String key, String defaultValue) {
        return supplierMap.getOrDefault(key, defaultValue);
    }

    public void putFix(String key, String value) {
        fixMap.put(key, value);
    }

    public Optional<String> getFix(String key) {
        return Optional.ofNullable(fixMap.getOrDefault(key, null));
    }

    public String getFix(String key, String defaultValue) {
        return fixMap.getOrDefault(key, defaultValue);
    }

    public void putInput(String key, String value) {
        inputMap.put(key, value);
    }

    public void putResponse(ChainedCurlStepResponse stepResponse) {
        this.stepResponse = stepResponse;
    }

    public Set<String> getInputKeys() {
        return Optional.ofNullable(inputMap).orElse(Collections.emptyMap()).keySet();
    }

    public Optional<String> getInput(String key) {
        return Optional.ofNullable(inputMap.getOrDefault(key, null));
    }

    public String getInput(String key, String defaultValue) {
        return inputMap.getOrDefault(key, defaultValue);
    }

    public Optional<String> getJsonTarget(String key) {
        return Optional.ofNullable(stepResponse.getJsonBody().asString(key));
    }

    public String getJsonTarget(String key, String defaultValue) {
        return Optional.ofNullable(stepResponse.getJsonBody().asString(key)).orElse(defaultValue);
    }

    /*public void putExtracted(String key, String value) {
        extractedMap.put(key, value);
    }*/

    public Optional<String> getExtracted(String key) {
        return Optional.ofNullable(stepResponse.getExtracted().getOrDefault(key, null));
    }

    public String getExtracted(String key, String defaultValue) {
        return stepResponse.getExtracted().getOrDefault(key, defaultValue);
    }

    public Optional<String> get(String key, ExpressionSourceType sourceType) {
        switch (sourceType) {
            case RES_JSON -> {
                return getJsonTarget(key);
            }
            case FIX -> {
                return getFix(key);
            }
            case SUPPLIER -> {
                return getSupplier(key);
            }
            case INPUT -> {
                return getInput(key);
            }
            case EXTRACTED -> {
                return getExtracted(key);
            }
        }
        return Optional.ofNullable(stepResponse.getExtracted().getOrDefault(key, null));
    }

    public String get(String key, String defaultValue, ExpressionSourceType sourceType) {
        switch (sourceType) {
            case RES_JSON -> {
                return getJsonTarget(key, defaultValue);
            }
            case FIX -> {
                return getFix(key, defaultValue);
            }
            case INPUT -> {
                return getInput(key, defaultValue);
            }
            case EXTRACTED -> {
                return getExtracted(key, defaultValue);
            }
        }
        throw new RuntimeException();
    }

    public static record ChainedCurlResponse(
            Map<String, Object> body,
            Map<String, String> extractedMap
    ) {}
}
