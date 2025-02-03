package ir.piana.dev.jsonparser.json.validators;

import java.util.List;

class ValidatorProxy<V> implements Validator<V> {
    private final Validator<V> validator;
    private final boolean isNot;

    ValidatorProxy(Validator<V> validator, boolean isNot) {
        this.validator = validator;
        this.isNot = isNot;
    }

    @Override
    public String fromValueType() {
        return validator.fromValueType();
    }

    @Override
    public List<String> commandNames() {
        return validator.commandNames();
    }

    public boolean validate(V value, String... options) {
        return (validator.validate(value, options) != isNot);
    }
}
