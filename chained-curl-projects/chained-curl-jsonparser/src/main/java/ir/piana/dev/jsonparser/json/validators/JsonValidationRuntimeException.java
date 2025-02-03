package ir.piana.dev.jsonparser.json.validators;

import java.io.Serializable;

public class JsonValidationRuntimeException extends RuntimeException implements Serializable {
    public JsonValidationRuntimeException(String message) {
        super(message);
    }
}
