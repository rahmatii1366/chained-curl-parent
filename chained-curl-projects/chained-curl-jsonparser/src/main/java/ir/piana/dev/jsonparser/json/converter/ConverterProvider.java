package ir.piana.dev.jsonparser.json.converter;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ConverterProvider {
    @Autowired
    private List<Converter> converters;

    private Map<String, Converter> converterMap;

    @PostConstruct
    public void init() {
        converterMap = new LinkedHashMap<>();
        Set<String> keySet = new HashSet<>();
        converterMap = converters.stream()
                .filter(v -> {
                    Optional any = v.commandNames().stream()
                            .map(c -> v.fromValueType().concat(".").concat(c.toString()))
                            .filter(keySet::contains).findAny();
                    if (any.isPresent())
                        throw new RuntimeException(any.get() + " => command is repeated!");
                    keySet.addAll((List<String>)v.commandNames().stream()
                            .map(c -> v.fromValueType().concat(".").concat(c.toString()))
                            .collect(Collectors.toList()));
                    return true;
                }).flatMap(v -> {
                    List<Map.Entry<String, Converter>> collect = (List) v.commandNames().stream()
                            .map(c -> new AbstractMap.SimpleEntry<>(
                                    v.fromValueType().concat(".").concat(c.toString()),
                                    v))
                            .collect(Collectors.toList());
                    return collect.stream();
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Converter getConverter(String command, String forType) {
        Converter converter = converterMap.get(forType + "." + command);
        if (Objects.isNull(converter))
            converter = converterMap.get("*." + command);
        return converter;
    }
}
