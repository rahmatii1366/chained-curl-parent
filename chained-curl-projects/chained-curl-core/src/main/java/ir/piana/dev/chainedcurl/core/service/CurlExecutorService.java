package ir.piana.dev.chainedcurl.core.service;

import ir.piana.dev.chainedcurl.core.curl.CurlHeaderBuilder;
import ir.piana.dev.chainedcurl.core.curl.CurlHeaders;
import ir.piana.dev.chainedcurl.core.curl.CurlResponse;
import ir.piana.dev.jsonparser.json.JsonParser;
import ir.piana.dev.jsonparser.json.JsonTarget;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class CurlExecutorService {
    private final CurlCommandProcessor curlCommandProcessor;
    private final ChainedCurlContextHolderService chainedCurlContextHolderService;
    private final JsonParser jsonParser;

    public CurlExecutorService(
            CurlCommandProcessor curlCommandProcessor,
            ChainedCurlContextHolderService chainedCurlContextHolderService,
            JsonParser jsonParser) {
        this.curlCommandProcessor = curlCommandProcessor;
        this.chainedCurlContextHolderService = chainedCurlContextHolderService;
        this.jsonParser = jsonParser;
    }

    public Map<String, String> fetchPrevStepsValues(String contextId, String stepName) {
        ChainedCurlServerContext serverContext = chainedCurlContextHolderService.getContext(contextId)
                .orElseThrow(() -> new ChainedCurlProcessRuntimeException(null));

        return Collections.emptyMap();
    }

    public boolean checkIfRequiredPrevStepValues(String contextId, String stepName) {
        ChainedCurlServerContext serverContext = chainedCurlContextHolderService.getContext(contextId)
                .orElseThrow(() -> new ChainedCurlProcessRuntimeException(null));

        return false;
    }

    public StepResponse execute(String contextId, String stepName, byte[] inputBytes)
            throws ChainedCurlProcessRuntimeException {
        ChainedCurlServerContext serverContext = chainedCurlContextHolderService.getContext(contextId)
                .orElseThrow(() -> new ChainedCurlProcessRuntimeException(null));

        JsonTarget jsonTarget = jsonParser.fromBytes(inputBytes,
                serverContext.getClientContext().getStepContext(stepName)
                        .getInputKeys().stream().collect(Collectors.joining("\n")),
                true);

        serverContext.putInputValues(stepName, jsonTarget);

        ChainedCurlRequestDto chainedCurlRequestDto = serverContext.getChainedCurlDto().getChainMap().get(stepName);

        List<String> curl = Stream.concat(Stream.concat(Stream.of("curl"), chainedCurlRequestDto.getCurl().stream())
                .filter(str -> str != null && !str.trim().isEmpty())
                .<List<String>>map(str -> {
                    if (str.startsWith("-X ")) {
                        String[] reqCmds = str.split(" ");
                        return Arrays.asList("-X", reqCmds[1], curlCommandProcessor.processCommandExp(
                                stepName, curlCommandProcessor.extractURL(str, reqCmds[1]), serverContext));
//                        return Arrays.asList(str.split(" "));
                    } else if (str.startsWith("-H ")) {
                        return Arrays.asList("-H", curlCommandProcessor.processCommandExp(
                                stepName, curlCommandProcessor.extractHeader(str), serverContext));
                    } else if (str.startsWith("-d ")) {
                        return Arrays.asList("-d", curlCommandProcessor.processCommandExp(
                                stepName, curlCommandProcessor.extractBody(str), serverContext));
                    } else {
                        return Arrays.asList(str);
                    }
                }).flatMap(List::stream), Stream.of("-i")).toList();

        if (true/*curlContextProcessor.checkStartConditions(entry.getKey(), curlChainContext)*/) {
            ProcessBuilder processBuilder = new ProcessBuilder(curl);
            InputStreamReader isr = null;
            try {
                Process start = processBuilder.start();

                CurlResponse curlResponse = null;
                if (start.waitFor() != 0) {
                    try (InputStream eis = start.getErrorStream()) {
                        curlResponse = generateErrorResponse(eis);
                        log.error(curlResponse.getBodyAsString());
                        /*curlContextManipulator.putResponse(
                                (CurlChainContextImpl) curlChainContext, entry.getKey(),
                                curlCommandDto,
                                errorResponse);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try (InputStream is = start.getInputStream()) {
                        curlResponse = generateResponse(is);
                        /*curlContextManipulator.putResponse(
                                (CurlChainContextImpl) curlChainContext, entry.getKey(),
                                curlCommandDto, curlResponse);*/
                    }
                }

                ChainedCurlStepResponse chainedCurlStepResponse = generateCurlStepResponse(
                        serverContext, stepName,
                        curl.stream().collect(Collectors.joining(" ")),
                        curlResponse);

                return new StepResponse(chainedCurlStepResponse.getCurlRequest(),
                        chainedCurlStepResponse.getStatus(),
                        Objects.nonNull(chainedCurlStepResponse.getCurlHeaders()) ?
                                chainedCurlStepResponse.getCurlHeaders().getHeaders() :
                                Collections.emptyMap(),
                        chainedCurlStepResponse.getBodyAsString(),
                        chainedCurlStepResponse.getExtracted(),
                        chainedCurlStepResponse.getShowControls());
//                curlContextProcessor.checkContinueConditions(entry.getKey(), curlChainContext);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public record StepResponse(
            String curlRequest,
            int status,
            Map<String, List<String>> headers,
            String bodyAsString,
            Map<String, String> extracted,
            Map<String, Map<String, String>> showControls
    ) {
    }

    public CurlResponse generateErrorResponse(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            StringBuilder responseStrBuilder = new StringBuilder();

            String line;
            StringBuilder responseBody = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseStrBuilder.append(line);
            }

            return CurlResponse.builder()
                    .bodyAsString(responseBody.toString())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            try {
                if (inputStreamReader != null)
                    inputStreamReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    ChainedCurlStepResponse generateCurlStepResponse(
            final ChainedCurlServerContext serverContext,
            String stepName,
            String curlCommand,
            CurlResponse curlResponse) {
        Optional<String> contentType = curlResponse.getCurlHeaders() == null ?
                Optional.empty() :
                curlResponse.getCurlHeaders().getFirstHeader("Content-Type");
        JsonTarget jsonTarget = null;
        if (contentType.isPresent() && contentType.get().trim().toLowerCase().startsWith("application/json")) {
            jsonTarget = jsonParser.fromBytes(curlResponse.getBodyAsString().getBytes(
                            contentType.get().toLowerCase().contains("charset=utf-8") ?
                                    StandardCharsets.UTF_8 : StandardCharsets.UTF_8/* ToDo => StandardCharsets.US_ASCII*/),
                    null, false);
        }

        ChainedCurlStepResponse build = ChainedCurlStepResponse.builder()
                .curlRequest(curlCommand)
                .status(curlResponse.getStatus())
                .bodyAsString(new String(Optional.ofNullable(jsonTarget)
                        .orElse(jsonParser.fromBytes(
                                "{}".getBytes(), null, false))
                        .getUtf8Bytes(false)))
                .jsonBody(jsonTarget)
                .curlHeaders(curlResponse.getCurlHeaders()).build();

        serverContext.getClientContext().getStepContext(stepName).putResponse(build);

        build.setExtracted(
                serverContext.getExtractMap(stepName).entrySet().stream().map(entry -> {
                    String value = curlCommandProcessor.processCommandExp(
                            stepName, entry.getValue(), serverContext);
                    return new AbstractMap.SimpleEntry<String, String>(entry.getKey(), value);
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        Map<String, Map<String, String>> collect1 = serverContext.getShowMap(stepName).entrySet().stream().map(entry -> {
            Map<String, String> collect = entry.getValue().entrySet().stream().map(e -> {
                String value = curlCommandProcessor.processCommandExp(
                        stepName, e.getValue(), serverContext);
                return new AbstractMap.SimpleEntry<String, String>(e.getKey(), value);
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return new AbstractMap.SimpleEntry<String, Map<String, String>>(entry.getKey(), collect);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        build.setShowControls(collect1);

        return build;
    }

    public CurlResponse generateResponse(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            StringBuilder responseStrBuilder = new StringBuilder();

            String line = new String();

            List<String> headers = new ArrayList<>();

            int status = 0;
            CurlHeaderBuilder curlHeaderBuilder = CurlHeaderBuilder.builder();
            boolean isHeader = true;
            StringBuilder responseBody = new StringBuilder();
            boolean isReadStatus = false;
            while ((line = br.readLine()) != null) {
                if (!isReadStatus) {
                    isReadStatus = true;
                    status = Integer.parseInt(line.split(" ")[1].trim());
                } else if (isHeader && line.trim().isEmpty())
                    isHeader = false;
                else if (isHeader) {
                    headers.add(line);
                    String[] split = line.split(":");
                    curlHeaderBuilder.add(split[0], split[1]);
                } else
                    responseBody.append(line);
                responseStrBuilder.append(line);
            }

            CurlHeaders curlHeaders = curlHeaderBuilder.build();
            return CurlResponse.builder()
                    .status(status)
                    .curlHeaders(curlHeaders)
                    .bodyAsString(responseBody.toString())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            try {
                if (inputStreamReader != null)
                    inputStreamReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*boolean checkStartConditions(
            String serviceName,
            ChainedCurlServerContext curlChainContext) {
        for (String continueCondition : curlChainContext.getChainedCurlDto().getChainMap().get(serviceName).getStartConditions()) {
            if (ifNotFulfillCondition(continueCondition, curlChainContext))
                return false;
        }
        return true;
    }

    void checkContinueConditions(
            String serviceName,
            ChainedCurlServerContext serverContext) {
        for (String continueCondition : serverContext.getContinueConditions(serviceName)) {
            if (ifNotFulfillCondition(continueCondition, serverContext))
                throw new CurlProcessRuntimeException(serverContext);
        }
    }

    boolean ifNotFulfillCondition(
            String condition,
            ChainedCurlServerContext serverContext) {
        if (condition.startsWith("$(status")) {
            return ifNotFulfillStatusCondition(condition, serverContext);
        }
        return false;
    }

    boolean ifNotFulfillStatusCondition(
            String statusCondition,
            ChainedCurlStepContext stepContext) {
        if (statusCondition.startsWith("$(status#")) {
            String[] split = statusCondition.substring(9, statusCondition.length() - 1).split(":");
            if (!stepContext.getStatus(split[0])
                    .orElseThrow(() -> new CurlProcessRuntimeException(serverContext)).toString()
                    .equalsIgnoreCase(split[1]))
                return true;
        } else if (statusCondition.startsWith("$(status")) {
            if (!serverContext.getStatus()
                    .orElseThrow(() -> new CurlProcessRuntimeException(serverContext)).toString()
                    .equalsIgnoreCase(statusCondition.substring(9, statusCondition.length() - 1)))
                return true;
        }
        return false;
    }*/
}
