package ir.piana.dev.jsonparser.json;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

public class JsonCrawler extends JsonTarget {
    private final JsonParser jsonParser;

    protected JsonCrawler(Object json, JsonParser jsonParser) {
        super(json);
        this.jsonParser = jsonParser;
    }

    JsonValue checkAndFill(String key, boolean throwIfNotValid) {
        if (key == null || key.isEmpty())
            return null;
        String[] keySplit = key.split(" ");
        if (!map.containsKey(keySplit[0])) {
            synchronized (map) {
                if (!map.containsKey(keySplit[0])) {
                    JsonValue jsonValue = jsonParser.parsOneField(json, this, key, throwIfNotValid);
                    addJsonValue(jsonValue);
                }
            }
        }
        return map.get(keySplit[0]);
    }

    public String asString(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asString() : null;
    }

    public Integer asInteger(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asInteger() : null;
    }

    public Long asLong(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asLong() : null;
    }

    public Boolean asBoolean(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asBoolean() : null;
    }

    public Float asFloat(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asFloat() : null;
    }

    public Double asDouble(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asDouble() : null;
    }

    public byte[] asBinary(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asBinary() : null;
    }

    public Buffer asBuffer(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asBuffer() : null;
    }

    public Instant asInstant(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asInstant() : null;
    }

    public Number asNumber(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asNumber() : null;
    }

    public JsonObject asJsonObject(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asJsonObject() : null;
    }

    public JsonArray asJsonArray(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asJsonArray() : null;
    }

    public Object asObject(String key) {
        JsonValue jsonValue = checkAndFill(key, false);
        return jsonValue != null ? jsonValue.asObject() : null;
    }

    public String toStringOnDemand() {
        Object onDemand = JsonParser.createOnDemandJson(this).json;
        return onDemand instanceof JsonObject ?
                JsonParser.object(onDemand).toString() :
                JsonParser.array(onDemand).toString();
    }
}
