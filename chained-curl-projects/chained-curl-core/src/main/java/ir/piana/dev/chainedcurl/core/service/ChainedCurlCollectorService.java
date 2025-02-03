package ir.piana.dev.chainedcurl.core.service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChainedCurlCollectorService {
    private final Map<String, ChainedCurlDto> chainedCurlMap;
    private final Map<String, List<String>> chainedCurlStepsMap;

    public ChainedCurlCollectorService(List<ChainedCurlProvidable> chainedCurlProvidableList) {
        chainedCurlMap = chainedCurlProvidableList.stream()
                .map(ChainedCurlProvidable::provide)
                .collect(Collectors.toMap(ChainedCurlDto::getName, Function.identity()));
        chainedCurlStepsMap = chainedCurlMap.entrySet().stream()
                .<Map.Entry<String, List<String>>>map(entry -> new AbstractMap.SimpleEntry<String, List<String>>(
                        entry.getKey(), entry.getValue().getChainMap().keySet().stream().toList()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public List<String> getStepNames(String chainedCurlName) {
        return chainedCurlStepsMap.get(chainedCurlName);
    }

    public ChainedCurlDto getChainedCurl(String chainedCurlName) {
        return chainedCurlMap.get(chainedCurlName);
    }
}
