package ir.piana.dev.jsonparser.json;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

import static ir.piana.dev.jsonparser.json.JsonParser.*;

public class JsonTargetBuilder {
    protected JsonObject rootObject;
    protected JsonArray rootArray;

    protected List<JsonValue> jsonValues = new ArrayList<>();

    protected JsonTargetBuilder(JsonObject rootObject) {
        this.rootObject = rootObject;
    }

    protected JsonTargetBuilder(JsonArray rootArray) {
        this.rootArray = rootArray;
    }

    protected JsonTargetBuilder(JsonTarget jsonTarget) {
        if (jsonTarget.isArray())
            this.rootArray = ((JsonArray) jsonTarget.json).copy();
        else
            this.rootObject = ((JsonObject) jsonTarget.json).copy();
    }

    public static JsonTargetBuilder asObject() {
        return new JsonTargetBuilder(new JsonObject());
    }

    public static JsonTargetBuilder asArray() {
        return new JsonTargetBuilder(new JsonArray());
    }

    public static JsonTargetBuilder edit(JsonTarget jsonTarget) {
        return new JsonTargetBuilder(jsonTarget);
    }

    public JsonTargetBuilder addZero(String key) {
        jsonValues.add(addJsonValue(key, 0));
        return this;
    }

    public JsonTargetBuilder addEmptyString(String key) {
        jsonValues.add(addJsonValue(key, ""));
        return this;
    }

    public JsonTargetBuilder add(String key, Object value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder add(
            String key, JsonTarget crawler,
            String commands,
            JsonParser jsonParser) {
        JsonValue jsonValue = addJsonValue(key, crawler.asObject(commands));

        if(commands.contains("$cast(")) {
            jsonParser.castOneField(jsonValue,
                    commands/*.substring(commands.indexOf("$cast("))*/,
                    crawler, false);
        }
        jsonValues.add(jsonValue);
        return this;
    }

    public JsonTargetBuilder replace(String key, Object value) {
        JsonValue jsonValue = replaceJsonValue(key, value);

        jsonValues.add(jsonValue);
        return this;
    }

    public JsonTargetBuilder addString(String key, String value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addInteger(String key, Integer value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addLong(String key, Long value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addBoolean(String key, Boolean value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addFloat(String key, Float value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addDouble(String key, Double value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addBinary(String key, byte[] value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addBuffer(String key, Buffer value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addInstant(String key, Instant value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addNumber(String key, Number value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addJsonObject(String key, JsonObject value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addJsonArray(String key, JsonArray value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonTargetBuilder addObject(String key, Object value) {
        jsonValues.add(addJsonValue(key, value));
        return this;
    }

    public JsonValue addJsonValue(String key, Object value) {
        String[] split = key.split("\\.");
        Object root = rootArray == null ? rootObject : rootArray;
        for (int i = 0; i < split.length; i++) {
            String partKey = split[i];
            Matcher matcher = Array_Index_Pattern.matcher(partKey);
            if (matcher.find()) {
                if (matcher.start() == 0) {
                    if (i == 0 && rootArray == null) {
                        throw new RuntimeException("Don't you know it's an object?!!!");
                    } else if (array(root) == null) {
                        throw new RuntimeException("here is not any array?!!!");
                    } else if (i < split.length - 1) {
                        Integer index = Integer.valueOf(matcher.group().substring(1, matcher.end() - 1));
                        for (int j = array(root).size(); j < index; j++) {
                            array(root).add(null);
                        }
                        if(split[i + 1].startsWith("[")) {
                            array(root).add(index, new JsonArray());
                            root = array(root).getJsonArray(index);
                        } else {
                            if (array(root).size() <= index)
                                array(root).add(index, new JsonObject());
                            root = array(root).getJsonObject(index);
                        }
                    } else {
                        Integer index = Integer.valueOf(matcher.group().substring(1, matcher.end() - 1));
                        if (rootArray.getValue(index) != null) {
                            throw new RuntimeException("This index is occupied");
                        } else {
                            for (int j = rootArray.size(); j < index; j++) {
                                rootArray.add(null);
                            }
                            rootArray.add(index, value);
                            return new JsonValue(rootArray.getValue(index));
                        }
                    }
                } else {
                    Integer index = Integer.valueOf(partKey.substring(matcher.start() + 1, matcher.end() - 1));
                    String fieldName = partKey.substring(0, partKey.indexOf("["));
                    if (!object(root).containsKey(fieldName)) {
                        object(root).put(fieldName, new JsonArray());
                    }
                    root = object(root).getJsonArray(fieldName);
                    for (int j = array(root).size(); j < index; j++)
                        array(root).add(j, null);
                    if (i < split.length - 1) {
                        if(split[i + 1].startsWith("[")) {
                            if(array(root).size() <= index) {
                                array(root).add(index, new JsonObject());
                            } else if(array(root).getValue(index) == null) {
                                array(root).remove(index.intValue());
                                array(root).add(index, new JsonObject());
                            }
                            root = array(root).getJsonArray(index);
                        } else {
                            if(array(root).size() <= index) {
                                array(root).add(index, new JsonObject());
                            } else if(array(root).getValue(index) == null) {
                                array(root).remove(index.intValue());
                                array(root).add(index, new JsonObject());
                            }
                            root = array(root).getJsonObject(index);
                        }
                    } else {
                        for (int j = array(root).size(); j < index; j++) {
                            array(root).add(j, null);
                        }
                        array(root).add(index, value);
                        if (value instanceof JsonObject)
                            return new JsonValue(object(value), partKey, key);
                        else
                            return new JsonValue(array(root));
                    }
                }
            } else {
                if (i < split.length - 1) {
                    if (!object(root).containsKey(partKey)) {
                        if (split[i + 1].startsWith("["))
                            object(root).put(partKey, new JsonArray());
                        else
                            object(root).put(partKey, new JsonObject());
                    }
                    root = object(root).getJsonObject(partKey);
                } else {
                    object(root).put(partKey, value);
                    return new JsonValue(object(root), partKey, key);
                }
            }
        }
        return null;
    }

    public JsonValue replaceJsonValue(String key, Object value) {
        String[] split = key.split("\\.");
        Object root = rootArray == null ? rootObject : rootArray;
        for (int i = 0; i < split.length; i++) {
            String partKey = split[i];
            Matcher matcher = Array_Index_Pattern.matcher(partKey);
            if (matcher.find()) {
                if (matcher.start() == 0) {
                    if (i == 0 && rootArray == null) {
                        throw new RuntimeException("Don't you know it's an object?!!!");
                    } else if (array(root) == null) {
                        throw new RuntimeException("here is not any array?!!!");
                    } else if (i < split.length - 1) {
                        Integer index = Integer.valueOf(matcher.group().substring(1, matcher.end() - 1));
                        for (int j = array(root).size(); j < index; j++) {
                            array(root).add(null);
                        }
                        if(split[i + 1].startsWith("[")) {
                            array(root).add(index, new JsonArray());
                            root = array(root).getJsonArray(index);
                        } else {
                            if (array(root).size() <= index)
                                array(root).add(index, new JsonObject());
                            root = array(root).getJsonObject(index);
                        }
                    } else {
                        Integer index = Integer.valueOf(matcher.group().substring(1, matcher.end() - 1));
                        if (rootArray.getValue(index) != null) {
                            throw new RuntimeException("This index is occupied");
                        } else {
                            for (int j = rootArray.size(); j < index; j++) {
                                rootArray.add(null);
                            }
                            rootArray.add(index, value);
                            return new JsonValue(rootArray.getValue(index));
                        }
                    }
                } else {
                    Integer index = Integer.valueOf(partKey.substring(matcher.start() + 1, matcher.end() - 1));
                    String fieldName = partKey.substring(0, partKey.indexOf("["));
                    if (!object(root).containsKey(fieldName)) {
                        object(root).put(fieldName, new JsonArray());
                    }
                    try {
                        root = object(root).getJsonArray(fieldName);
                    } catch (ClassCastException e) {
                        object(root).remove(fieldName);
                        object(root).put(fieldName, new JsonArray());
                        root = object(root).getJsonArray(fieldName);
                    }
                    for (int j = array(root).size(); j < index; j++)
                        array(root).add(j, null);
                    if (i < split.length - 1) {
                        if(split[i + 1].startsWith("[")) {
                            if(array(root).size() <= index) {
                                array(root).add(index, new JsonObject());
                            } else if(array(root).getValue(index) == null) {
                                array(root).remove(index.intValue());
                                array(root).add(index, new JsonObject());
                            }
                            root = array(root).getJsonArray(index);
                        } else {
                            if(array(root).size() <= index) {
                                array(root).add(index, new JsonObject());
                            } else if(array(root).getValue(index) == null) {
                                array(root).remove(index.intValue());
                                array(root).add(index, new JsonObject());
                            }
                            root = array(root).getJsonObject(index);
                        }
                    } else {
                        for (int j = array(root).size(); j < index; j++) {
                            array(root).add(j, null);
                        }
                        array(root).add(index, value);
                        if (value instanceof JsonObject)
                            return new JsonValue(object(value), partKey, key);
                        else
                            return new JsonValue(array(root));
                    }
                }
            } else {
                if (i < split.length - 1) {
                    if (!object(root).containsKey(partKey)) {
                        if (split[i + 1].startsWith("["))
                            object(root).put(partKey, new JsonArray());
                        else
                            object(root).put(partKey, new JsonObject());
                    }
                    try {
                        root = object(root).getJsonObject(partKey);
                    } catch (ClassCastException e) {
                        object(root).remove(partKey);
                        object(root).put(partKey, new JsonObject());
                        root = object(root).getJsonObject(partKey);
                    }

                } else {
                    object(root).put(partKey, value);
                    return new JsonValue(object(root), partKey, key);
                }
            }
        }
        return null;
    }

    public JsonTarget build() {
        JsonTarget jsonTarget = new JsonTarget(Objects.isNull(rootArray) ? rootObject : rootArray);
        addJsonValues(jsonTarget);
        return jsonTarget;
    }

    protected void addJsonValues(JsonTarget jsonTarget) {
        jsonValues.stream().forEach(jsonTarget::addJsonValue);
    }
}
