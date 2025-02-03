package ir.piana.dev.jsonparser.json;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

public class JsonValue {
    protected JsonObject object;
    protected JsonArray array;
    protected String partKey;
    protected String key;

    JsonValue() {

    }

    public JsonValue(JsonObject object, String partKey, String key) {
        this.object = object;
        this.partKey = partKey;
        this.key = key;
    }

    String getKey() {
        return key;
    }

    String getPartKey() {
        return partKey;
    }

    public JsonValue(Object object) {
        if (object instanceof JsonObject)
            this.object = (JsonObject) object;
        else
            this.array = (JsonArray) object;
    }

    public String asString(String... validationCommands) {
        return object.getString(partKey);
    }

    public String asString() {
        return object.getString(partKey);
    }

    public Integer asInteger() {
        return object.getInteger(partKey);
    }

    public Long asLong() {
        return object.getLong(partKey);
    }

    public Boolean asBoolean() {
        return object.getBoolean(partKey);
    }

    public Float asFloat() {
        return object.getFloat(partKey);
    }

    public Double asDouble() {
        return object.getDouble(partKey);
    }

    public byte[] asBinary() {
        return object.getBinary(partKey);
    }

    public Buffer asBuffer() {
        return object.getBuffer(partKey);
    }

    public Instant asInstant() {
        return object.getInstant(partKey);
    }

    public Number asNumber() {
        return object.getNumber(partKey);
    }

    public JsonObject asJsonObject() {
        return partKey == null ? object : object.getJsonObject(partKey);
    }

    public JsonArray asJsonArray() {
        return object.getJsonArray(partKey);
    }

    public Object asObject() {
        return object.getValue(partKey);
    }

}
