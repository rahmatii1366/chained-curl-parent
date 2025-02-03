package ir.piana.dev.chainedcurl.core.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ir.piana.dev.jsonparser.json.JsonTarget;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Getter
public class ChainedCurlServerContext {
    @JsonIgnore
    private final String uuid;
    @JsonIgnore
    private final ChainedCurlDto chainedCurlDto;
    @JsonProperty("context")
    private final ChainedCurlContext clientContext;

    Map<String, String> getExtractMap(String stepName) {
        return Optional.ofNullable(getChainedCurlDto().getChainMap().get(stepName).getExtractMap())
                .orElse(Collections.emptyMap());
    }

    Map<String, Map<String, String>> getShowMap(String stepName) {
        return Optional.ofNullable(getChainedCurlDto().getChainMap().get(stepName).getShowMap())
                .orElse(Collections.emptyMap());
    }

    public ChainedCurlServerContext(String uuid, ChainedCurlDto chainedCurlDto) {
        this.uuid = uuid;
        this.chainedCurlDto = chainedCurlDto;
        clientContext = new ChainedCurlContext(uuid);
    }

    public void putInputValues(String stepName, JsonTarget jsonTarget) {
        if (Objects.nonNull(chainedCurlDto.getChainMap().get(stepName).getProvideDto()))
            Optional.ofNullable(chainedCurlDto.getChainMap().get(stepName).getProvideDto().getInput())
                    .orElse(Collections.emptyMap()).keySet().forEach(key -> {
                        clientContext.putInputObject(stepName, key, jsonTarget.asString(key));
                    });
    }

    public void putResponse(String stepName, ChainedCurlStepResponse response) {
        clientContext.getStepContext(stepName).putResponse(response);
    }

    public static record ChainedCurlServerConfig(
            String contextId, ChainedCurlDto chainedCurlDto, ChainedCurlStepContext initialValues,
            Map<String, ChainedCurlStepContext> contextMap) {
    }
}