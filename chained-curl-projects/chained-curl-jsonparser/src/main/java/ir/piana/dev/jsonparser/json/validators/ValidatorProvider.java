package ir.piana.dev.jsonparser.json.validators;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ValidatorProvider {
    @Autowired
    private List<Validator> validators;

    private Map<String, Validator> validatorMap;

    @PostConstruct
    public void init() {
        validatorMap = new LinkedHashMap<>();
        Set<String> keySet = new HashSet<>();
        validatorMap = validators.stream()
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
                    List<Map.Entry<String, Validator>> collect = (List) v.commandNames().stream()
                            .map(c -> new AbstractMap.SimpleEntry<>(
                                    v.fromValueType().concat(".").concat(c.toString()),
                                    v))
                            .collect(Collectors.toList());
                    return collect.stream();
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        validatorMap.putAll(validatorMap.entrySet().stream()
                .map(e -> {
                    String[] split = e.getKey().split("\\.");
                    return new AbstractMap.SimpleEntry<>(split[0] + ".!" + split[1],
                            new ValidatorProxy<>(e.getValue(), true));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public Validator getValidator(String command, String forType) {
        Validator validator = validatorMap.get(forType + "." + command);
        if (Objects.isNull(validator))
            validator = validatorMap.get("*." + command);
        return validator;
    }
}
