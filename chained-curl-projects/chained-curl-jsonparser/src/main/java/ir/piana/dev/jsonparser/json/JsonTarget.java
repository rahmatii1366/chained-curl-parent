package ir.piana.dev.jsonparser.json;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class JsonTarget {
    protected final Map<String, JsonValue> map = new LinkedHashMap<>();
    protected Object json;
    protected Object onDemandJson;

    public boolean isArray() {
        return json instanceof JsonArray;
    }

    protected JsonTarget(JsonTarget jsonTarget) {
        this.json = jsonTarget.isArray() ?
                ((JsonArray)jsonTarget.json).copy() :
                ((JsonObject)jsonTarget.json).copy();
    }

    protected JsonTarget(Object json) {
        this.json = json;
    }

    protected void addJsonValue(JsonValue jsonValue) {
        map.put(jsonValue.getKey(), jsonValue);
    }

    Optional<JsonValue> getJsonValue(String key) {
        return Optional.ofNullable(map.get(key));
    }

    public String asString(String key) {
        return map.containsKey(key) ? map.get(key).asString() : null;
    }

    public Integer asInteger(String key) {
        return map.containsKey(key) ? map.get(key).asInteger() : null;
    }

    public Long asLong(String key) {
        return map.containsKey(key) ? map.get(key).asLong() : null;
    }

    public Boolean asBoolean(String key) {
        return map.containsKey(key) ? map.get(key).asBoolean() : null;
    }

    public Float asFloat(String key) {
        return map.containsKey(key) ? map.get(key).asFloat() : null;
    }

    public Double asDouble(String key) {
        return map.containsKey(key) ? map.get(key).asDouble() : null;
    }

    public byte[] asBinary(String key) {
        return map.containsKey(key) ? map.get(key).asBinary() : null;
    }

    public Buffer asBuffer(String key) {
        return map.containsKey(key) ? map.get(key).asBuffer() : null;
    }

    public Instant asInstant(String key) {
        return map.containsKey(key) ? map.get(key).asInstant() : null;
    }

    public Number asNumber(String key) {
        return map.containsKey(key) ? map.get(key).asNumber() : null;
    }

    public JsonObject asJsonObject(String key) {
        return map.containsKey(key) ? map.get(key).asJsonObject() : null;
    }

    public JsonArray asJsonArray(String key) {
        return map.containsKey(key) ? map.get(key).asJsonArray() : null;
    }

    public Object asObject(String key) {
        return map.containsKey(key) ? map.get(key).asObject() : null;
    }

    @Override
    public String toString() {
        return json instanceof JsonObject ?
                JsonParser.object(json).toString() :
                JsonParser.array(json).toString();
    }

    public String toStringOnDemand() {
        return onDemandJson instanceof JsonObject ?
                JsonParser.object(onDemandJson).toString() :
                JsonParser.array(onDemandJson).toString();
    }

    public byte[] getUtf8Bytes(boolean onDemand) {
        return getBytes(onDemand, StandardCharsets.UTF_8);
    }

    public byte[] getBytes(boolean onDemand) {
        return getBytes(onDemand, StandardCharsets.US_ASCII);
    }

    public byte[] getBytes(boolean onDemand, String charset) {
        return getBytes(onDemand, Charset.forName(charset));
    }

    public byte[] getBytes(boolean onDemand, Charset charset) {
        return (onDemand ? toStringOnDemand() : toString()).getBytes(charset);
    }

    public JsonObject getJsonObject() {
        return JsonParser.object(json);
    }

    public JsonArray getJsonArray() {
        return JsonParser.array(json);
    }

    public <T> T mapTo(Class<T> type) {
        return !isArray() ? JsonParser.object(json).mapTo(type) : null;
    }
}
