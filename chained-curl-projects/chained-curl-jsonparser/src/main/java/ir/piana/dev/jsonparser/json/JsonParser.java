package ir.piana.dev.jsonparser.json;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.JsonEventType;
import ir.piana.dev.jsonparser.json.converter.Converter;
import ir.piana.dev.jsonparser.json.converter.ConverterProvider;
import ir.piana.dev.jsonparser.json.validators.JsonValidationRuntimeException;
import ir.piana.dev.jsonparser.json.validators.Validator;
import ir.piana.dev.jsonparser.json.validators.ValidatorProvider;
import ir.piana.dev.jsonparser.util.NumberParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JsonParser {
    public static Pattern Array_Index_Pattern = Pattern.compile("\\[(0|(([1-9])([0-9]*)))\\]");
    public static Pattern Enclosed_In_Dollar_Pattern = Pattern.compile("\\$((\\[\\d*]|[a-zA-Z])[.a-zA-Z0-9(\\[\\d*\\])]{0,100})\\$");

    protected ValidatorProvider validatorProvider;
    protected ConverterProvider converterProvider;

    public JsonTarget bodyAsJsonObject(byte[] bytes) {
        return new JsonTarget(new JsonObject(Buffer.buffer().appendBytes(bytes)));
    }

    public JsonParser(@Autowired ValidatorProvider validatorProvider, @Autowired ConverterProvider converterProvider) {
        this.validatorProvider = validatorProvider;
        this.converterProvider = converterProvider;
    }

    public JsonTarget fromJson(JsonObject json) {
        return new JsonCrawler(json, this);
    }

    public JsonTarget fromJson(Object json, boolean throwIfNotValid) {
        JsonObject rootObject = null;
        JsonArray rootArray = null;
        if (json instanceof JsonArray)
            rootArray = (JsonArray) json;
        else
            rootObject = (JsonObject) json;
        return fromJson(json, null, throwIfNotValid);
    }

    public JsonTarget fromJson(Object json,
                               String targetFieldByItsValidationCommands,
                               boolean throwIfNotValid) {
        JsonObject rootObject = null;
        JsonArray rootArray = null;
        if (json instanceof JsonArray)
            rootArray = (JsonArray) json;
        else
            rootObject = (JsonObject) json;
        return init(createJsonTarget(rootObject, rootArray, targetFieldByItsValidationCommands),
                targetFieldByItsValidationCommands, throwIfNotValid);
    }

    public JsonTarget fromBytes(
            byte[] bytes,
            String targetFieldByItsValidationCommands,
            boolean throwIfNotValid) {
        Buffer buffer = Buffer.buffer(bytes);
        JsonObject rootObject = null;
        JsonArray rootArray = null;
        Object object = buffer.toJson();
        if (object instanceof JsonArray) {
            rootArray = (JsonArray) object;
        } else {
            rootObject = (JsonObject) object;
        }

        return init(
                createJsonTarget(rootObject, rootArray, targetFieldByItsValidationCommands),
                targetFieldByItsValidationCommands, throwIfNotValid);
    }

    protected Map<String, Properties> resourceMap = new LinkedHashMap<>();

    protected Properties getYamlPropertiesFactoryBean(Resource transformYaml) {
        if (!resourceMap.containsKey(transformYaml.getFilename())) {
            YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
            bean.setResources(transformYaml);
            resourceMap.put(transformYaml.getFilename(), bean.getObject());
            return bean.getObject();
        }
        return resourceMap.get(transformYaml.getFilename());
    }

    public JsonTarget transform(byte[] jsonBytes,
                                Resource transformYaml,
                                boolean throwsIfNotValid) {
        JsonTarget crawler = fromBytes(jsonBytes, null, throwsIfNotValid);

        Properties properties = getYamlPropertiesFactoryBean(transformYaml);

        JsonTargetBuilder targetBuilder;
        if (properties.keySet().stream().filter(k -> ((String) k).startsWith("[")).findAny().isPresent())
            targetBuilder = JsonTargetBuilder.asArray();
        else
            targetBuilder = JsonTargetBuilder.asObject();

        return transform(crawler, properties, targetBuilder);
    }

    public JsonTarget transform(
            JsonTarget from, Resource transformYaml) {
        Properties properties = getYamlPropertiesFactoryBean(transformYaml);

        JsonTargetBuilder targetBuilder;
        if (properties.keySet().stream().filter(k -> ((String) k).startsWith("[")).findAny().isPresent())
            targetBuilder = JsonTargetBuilder.asArray();
        else
            targetBuilder = JsonTargetBuilder.asObject();

        return transform(from, properties, targetBuilder);
    }

    public JsonTarget transform(Object json, Resource transformYaml) {
        JsonTarget crawler = fromJson(json, false);

        Properties properties = getYamlPropertiesFactoryBean(transformYaml);

        JsonTargetBuilder targetBuilder;
        if (properties.keySet().stream().filter(k -> ((String) k).startsWith("[")).findAny().isPresent())
            targetBuilder = JsonTargetBuilder.asArray();
        else
            targetBuilder = JsonTargetBuilder.asObject();

        return transform(crawler, properties, targetBuilder);
    }

    //*
    protected JsonTarget transform(
            JsonTarget crawler,
            Properties transformYaml,
            final JsonTargetBuilder targetBuilder) {
        transformYaml.forEach((k, v) -> {
            String val = (String) v;
//            System.out.println(k);
            if (val.startsWith("$")) {
                if (val.startsWith("$null")) {
                    targetBuilder.add((String) k, null);
                } else if (val.startsWith("$string")) {
                    targetBuilder.add((String) k, val.substring(8, val.length() - 1));
                } else if (val.startsWith("$number")) {
                    targetBuilder.add((String) k, NumberParser.parseUp(val.substring(8, val.length() - 1)));
                } else if (val.startsWith("$boolean")) {
                    targetBuilder.add((String) k, NumberParser.parseBoolean(val.substring(9, val.length() - 1)));
                }
            } else {
                targetBuilder.add((String) k, crawler, (String) v, this);
//                targetBuilder.add((String) k, crawler.asObject((String) v));
            }
        });
        return targetBuilder.build();
    }

    protected JsonTarget init(
            JsonTarget jsonTarget,
            String targetFieldByItsValidationCommands,
            boolean throwIfNotValid) {
        if (jsonTarget instanceof JsonCrawler)
            return jsonTarget;

        String[] lines = targetFieldByItsValidationCommands.split("\\r?\\n");

        for (String line : lines) {
            JsonValue jsonValue = parsOneField(
                    jsonTarget.json,
                    jsonTarget,
                    line, throwIfNotValid);
            jsonTarget.addJsonValue(jsonValue);
        }

        jsonTarget.onDemandJson = createOnDemandJson(jsonTarget).json;
        return jsonTarget;
    }

    protected JsonTarget createJsonTarget(JsonObject rootObject, JsonArray rootArray,
                                          String targetFieldByItsValidationCommands,
                                          Object... others) {
        if (Objects.isNull(targetFieldByItsValidationCommands) ||
                targetFieldByItsValidationCommands.isEmpty() ||
                targetFieldByItsValidationCommands.isBlank()) {
            return new JsonCrawler(
                    Objects.isNull(rootArray) ? rootObject : rootArray, this);
        }
        return new JsonTarget(Objects.isNull(rootArray) ? rootObject : rootArray);
    }

    protected JsonValue castOneField(
            JsonValue jsonValue, String command, JsonTarget jsonTarget, boolean throwIfNotValid) {
        String[] commands = command.trim().split(" ");

        String type = null;
        for (int i = 2; i < commands.length; i++) {
            if (commands[i].startsWith("$cast")) {
                String[] options = commands[i].substring(6, commands[i].length() - 1).split("#");
                Converter converter = converterProvider.getConverter(
                        options[0], commands[1]);

                String[] theOptions = options.length > 1 ?
                        Arrays.copyOfRange(options, 1, options.length) : new String[0];
                Object convert = converter.convert(jsonValue.asObject(), theOptions);
                object(jsonValue.object).put(jsonValue.partKey, convert);
                type = converter.toValueType();
            } else {
                if (type != null) {
                    String[] options = commands[i].split("#");
                    Validator validator = validatorProvider.getValidator(options[0], type);

                    String[] theOptions = options.length > 1 ?
                            Arrays.copyOfRange(options, 1, options.length) : new String[0];
                    boolean hasIftt = theOptions.length > 0 && theOptions[theOptions.length - 1]
                            .equalsIgnoreCase("iftt");

                    for (int j = 0; j < (hasIftt ? theOptions.length - 1 : theOptions.length); j++) {
                        if (theOptions[j].contains("$")) {
                            Matcher matcher = Enclosed_In_Dollar_Pattern.matcher(theOptions[j]);
                            while (matcher.find()) {
                                String string = Optional.ofNullable(
                                                jsonTarget.asObject(matcher.group().substring(1, matcher.end() - 1)))
                                        .orElse("null").toString();
                                theOptions[j] = string;
                            }
                        }
                    }
                    if (!validator.validate(jsonValue.asObject(), theOptions)) {
                        if (hasIftt) {
                            break;
                        } else if (throwIfNotValid) {
                            throwsException(
                                    jsonTarget,
                                    "$" + jsonValue.getKey() + "."
                                            .concat(validator.originalName()));
                        }
                    }
                }
            }
        }
        return jsonValue;
    }

    protected JsonValue parsOneField(
            Object jsonObject, JsonTarget jsonTarget,
            String targetFieldByItsValidationCommand,
            boolean throwIfNotValid) {
        if (Objects.isNull(targetFieldByItsValidationCommand))
            return null;
        String[] commands = targetFieldByItsValidationCommand.trim().split(" ");

        JsonValue jsonValue = getJsonValue(jsonObject, commands[0]);
        /**
         * validation
         */
        for (int i = 2; i < commands.length; i++) {
            if (commands[i].startsWith("$cast")) {
                break;
            } else {
                String[] options = commands[i].split("#");
                Validator validator = validatorProvider.getValidator(options[0], commands[1]);

                String[] theOptions = options.length > 1 ?
                        Arrays.copyOfRange(options, 1, options.length) : new String[0];
                boolean hasIftt = theOptions.length > 0 && theOptions[theOptions.length - 1]
                        .equalsIgnoreCase("iftt");

                for (int j = 0; j < (hasIftt ? theOptions.length - 1 : theOptions.length); j++) {
                    if (theOptions[j].contains("$")) {
                        Matcher matcher = Enclosed_In_Dollar_Pattern.matcher(theOptions[j]);
                        while (matcher.find()) {
                            String string = Optional.ofNullable(
                                            jsonTarget.asObject(matcher.group().substring(1, matcher.end() - 1)))
                                    .orElse("null").toString();
                            theOptions[j] = string;
                        }
                    }
                }
                if (!validator.validate(jsonValue.asObject(), theOptions)) {
                    if (hasIftt) {
                        break;
                    } else if (throwIfNotValid) {
                        throwsException(
                                jsonTarget,
                                "$" + jsonValue.getKey() + "."
                                        .concat(validator.originalName()));
                    }
                }
            }
        }

        jsonTarget.addJsonValue(jsonValue);
        return jsonValue;
    }

    public static JsonValue getJsonValue(Object root, String key) {
        String[] split = key.split("\\.");
        for (int i = 0; i < split.length; i++) {
            String partKey = split[i];
            Matcher matcher = Array_Index_Pattern.matcher(partKey);
            if (matcher.find()) {
                if (matcher.start() == 0) {
                    if (i < split.length - 1) {
                        Integer index = Integer.valueOf(matcher.group().substring(1, matcher.end() - 1));
                        root = array(root).getValue(index);
                    } else {
                        Integer index = Integer.valueOf(matcher.group().substring(1, matcher.end() - 1));
                        return new JsonValue(array(root).getValue(index));
                    }
                } else {
                    if (i < split.length - 1) {
                        Integer index = Integer.valueOf(partKey.substring(matcher.start() + 1, matcher.end() - 1));
                        root = object(root).getJsonArray(partKey.substring(0, partKey.indexOf("["))).getValue(index);
                    } else {
                        Integer index = Integer.valueOf(partKey.substring(matcher.start() + 1, matcher.end() - 1));
                        return new JsonValue(
                                object(root).getJsonArray(partKey.substring(0, partKey.indexOf("["))).getValue(index));
                    }
                }
            } else {
                if (i < split.length - 1) {
                    root = object(root).getJsonObject(partKey);
                } else {
                    return new JsonValue(object(root), partKey, key);
                }
            }
        }
        return null;
    }

    protected static JsonTarget createOnDemandJson(JsonTarget jsonTarget) {
        JsonTargetBuilder builder = jsonTarget.json instanceof JsonArray ?
                JsonTargetBuilder.asArray() : JsonTargetBuilder.asObject();
        jsonTarget.map.entrySet().stream().forEach(k -> {
            builder.add(k.getKey(), k.getValue().asObject());
        });
        return builder.build();
    }

    protected boolean isJsonArray(Buffer buffer) {
        io.vertx.core.parsetools.JsonParser parser = io.vertx.core.parsetools.JsonParser.newParser();
        AtomicReference<Object> result = new AtomicReference<>();
        parser.handler(event -> {
            if (Objects.requireNonNull(event.type()) == JsonEventType.START_ARRAY) {
                result.set(new ArrayList<>());
                parser.objectValueMode();
            }
            parser.pause();
        });

        parser.handle(buffer);
        parser.end();
        Object o = result.get();
        return o instanceof List;
    }

    protected static JsonArray array(Object o) {
        return (JsonArray) o;
    }

    protected static JsonObject object(Object o) {
        return (JsonObject) o;
    }

    protected void throwsException(
            JsonTarget jsonTarget, String detailKey) {
        throw new JsonValidationRuntimeException(detailKey);
    }

    protected String getKey(JsonValue jsonValue) {
        return jsonValue.getKey();
    }
}
