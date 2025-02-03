package ir.piana.dev.chainedcurl.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
public class CurlCommandProcessor {
    public static final Pattern parentheses_pattern = Pattern.compile("\\$\\(([^\\)]+)\\)");

    private final ExpressionInterpreterService expressionInterpreterService;

    public String extractBody(String body) {
        if (body.trim().startsWith("-d $(body:"))
            return body.substring(10, body.length() - 1);
        return body;
    }

    public String extractHeader(String header) {
        return header.trim().substring(3);
    }

    public String extractURL(String url, String method) {
        return url.trim().substring(4 + method.length());
    }

    public String extractCommandExp(String value) {
        return value.trim().substring(2, value.trim().length() - 1);
    }

    public List<String> processUrl(String urlCommand, ChainedCurlServerContext serverContext) {
        String[] urlSplit = urlCommand.split(" ");
        StringBuilder result = new StringBuilder();
        int startIndex = 0;
        int endIndex = 0;
        Matcher matcher = parentheses_pattern.matcher(urlSplit[2]);
        while (matcher.find()) {
            String group = matcher.group().substring(2, matcher.group().length() - 1);
            if (group.startsWith("res-json#")) {
                /*String[] split = group.substring(9).split(":");
                String[] split1 = split[1].split("&");

                expressionInterpreterService.process(split1[0], serverContext.getClientContext());
                ExpCommandHandler expCommandHandler = new InitiateExpCommandHandler(
                        curlChainContext.asStringJsonField(split[0], split1[0])
                                .orElseThrow(() -> new CurlProcessRuntimeException(curlChainContext)));
                for (int i = 1; i < split1.length; i++) {
                    expCommandHandler = expCommandHandler.handle(split1[i], curlChainContext, valueHolder);
                }
                String value = expCommandHandler.getValue();
                endIndex = matcher.end();
                result.append(urlSplit[2].substring(startIndex, matcher.start()).concat(value));
                startIndex = endIndex;*/
            } else if (group.startsWith("res-json")) {
                /*String[] split = group.split(":");
                String val = curlChainContext.asStringJsonField(split[1])
                        .orElseThrow(() -> new CurlProcessRuntimeException(curlChainContext));
                endIndex = matcher.end();
                result.append(urlCommand.substring(startIndex, matcher.start()).concat(val));
                startIndex = endIndex;*/
            } else if (group.startsWith("res-header")) {
            } else if (group.startsWith("value")) {
                /*String[] split = group.split("#");
                String val = valueHolder.getString(split[1])
                        .orElseThrow(() -> new CurlProcessRuntimeException(curlChainContext));
                endIndex = matcher.end();
                result.append(urlSplit[2].substring(startIndex, matcher.start()).concat(val));
                startIndex = endIndex;*/
            }
        }
        return Arrays.asList(urlSplit[0], urlSplit[1],
                result.append(urlSplit[2].substring(endIndex)).toString());
    }

    public String processHeader(String stepName, String headerValue, ChainedCurlServerContext serverContext) {
        StringBuilder result = new StringBuilder();
        int startIndex = 0;
        int endIndex = 0;
        Matcher matcher = parentheses_pattern.matcher(headerValue);
        while (matcher.find()) {
            String group = matcher.group().substring(2, matcher.group().length() - 1);
            if (group.startsWith("res-json#")) {
                /*String[] split = group.substring(9).split(":");
                String[] split1 = split[1].split("&");

                ExpCommandHandler expCommandHandler = new InitiateExpCommandHandler(
                        curlChainContext.asStringJsonField(split[0], split1[0])
                                .orElseThrow(() -> new CurlProcessRuntimeException(curlChainContext)));
                for (int i = 1; i < split1.length; i++) {
                    expCommandHandler = expCommandHandler.handle(split1[i], curlChainContext, valueHolder);
                }
                String value = expCommandHandler.getValue();
                endIndex = matcher.end();
                result.append(headerValue.substring(startIndex, matcher.start()).concat(value));
                startIndex = endIndex;*/
            } else if (group.startsWith("res-json")) {
                /*String[] split = group.split(":");
                String val = curlChainContext.asStringJsonField(split[1])
                        .orElseThrow(() -> new CurlProcessRuntimeException(curlChainContext));
                endIndex = matcher.end();
                result.append(headerValue.substring(startIndex, matcher.start()).concat(val));
                startIndex = endIndex;*/
            } else if (group.startsWith("res-header")) {
            } else if (group.startsWith("value")) {
                /*String[] split = group.split("#");
                String val = valueHolder.getString(split[1])
                        .orElseThrow(() -> new CurlProcessRuntimeException(curlChainContext));
                endIndex = matcher.end();
                result.append(headerValue.substring(startIndex, matcher.start()).concat(val));
                startIndex = endIndex;*/
            }
        }
        return result.append(headerValue.substring(endIndex)).toString();
    }

    public String processBody(String stepName, String body, ChainedCurlServerContext serverContext) {
        StringBuilder result = new StringBuilder();
        int startIndex = 0;
        int endIndex = 0;
        Matcher matcher = parentheses_pattern.matcher(body);
        while (matcher.find()) {
            String group = matcher.group().substring(2, matcher.group().length() - 1);
            String value = expressionInterpreterService.process(stepName, group, serverContext.getClientContext());
            endIndex = matcher.end();
            result.append(body.substring(startIndex, matcher.start()).concat(value));
            startIndex = endIndex;

            /*if (group.startsWith("res-json#")) {
                *//*String[] split = group.substring(9).split(":");

                String val = curlChainContext.asStringJsonField(split[0], split[1])
                        .orElseThrow(() -> new CurlProcessRuntimeException(curlChainContext));
                endIndex = matcher.end();
                result.append(body.substring(startIndex, matcher.start()).concat(val));
                startIndex = endIndex;*//*
            } else if (group.startsWith("res-json")) {
                *//*String[] split = group.split(":");
                String val = curlChainContext.asStringJsonField(split[1])
                        .orElseThrow(() -> new CurlProcessRuntimeException(curlChainContext));
                endIndex = matcher.end();
                result.append(body.substring(startIndex, matcher.start()).concat(val));
                startIndex = endIndex;*//*
            } else if (group.startsWith("res-header")) {
            } else if (group.startsWith("value#")) {
                *//*String key = group.substring(6);

                String val = valueHolder.getString(key).orElseThrow(
                        () -> new CurlProcessRuntimeException(curlChainContext));
                endIndex = matcher.end();
                result.append(body.substring(startIndex, matcher.start()).concat(val));
                startIndex = endIndex;*//*
            } else if (group.startsWith("input#")) {
                String key = group.substring(6);
                String value = expressionInterpreterService.process(key, serverContext.getClientContext());
                endIndex = matcher.end();
                result.append(body.substring(startIndex, matcher.start()).concat(value));
                startIndex = endIndex;
            }*/
        }
        return result.append(body.substring(endIndex)).toString();
    }

    public String processCommandExp(String stepName, String body, ChainedCurlServerContext serverContext) {
        StringBuilder result = new StringBuilder();
        int startIndex = 0;
        int endIndex = 0;
        Matcher matcher = parentheses_pattern.matcher(body);
        while (matcher.find()) {
            String group = matcher.group().substring(2, matcher.group().length() - 1);
            String value = expressionInterpreterService.process(stepName, group, serverContext.getClientContext());
            endIndex = matcher.end();
            result.append(body.substring(startIndex, matcher.start()).concat(value));
            startIndex = endIndex;

            /*if (group.startsWith("res-json#")) {
             *//*String[] split = group.substring(9).split(":");

                String val = curlChainContext.asStringJsonField(split[0], split[1])
                        .orElseThrow(() -> new CurlProcessRuntimeException(curlChainContext));
                endIndex = matcher.end();
                result.append(body.substring(startIndex, matcher.start()).concat(val));
                startIndex = endIndex;*//*
            } else if (group.startsWith("res-json")) {
                *//*String[] split = group.split(":");
                String val = curlChainContext.asStringJsonField(split[1])
                        .orElseThrow(() -> new CurlProcessRuntimeException(curlChainContext));
                endIndex = matcher.end();
                result.append(body.substring(startIndex, matcher.start()).concat(val));
                startIndex = endIndex;*//*
            } else if (group.startsWith("res-header")) {
            } else if (group.startsWith("value#")) {
                *//*String key = group.substring(6);

                String val = valueHolder.getString(key).orElseThrow(
                        () -> new CurlProcessRuntimeException(curlChainContext));
                endIndex = matcher.end();
                result.append(body.substring(startIndex, matcher.start()).concat(val));
                startIndex = endIndex;*//*
            } else if (group.startsWith("input#")) {
                String key = group.substring(6);
                String value = expressionInterpreterService.process(key, serverContext.getClientContext());
                endIndex = matcher.end();
                result.append(body.substring(startIndex, matcher.start()).concat(value));
                startIndex = endIndex;
            }*/
        }
        return result.append(body.substring(endIndex)).toString();
    }
}
