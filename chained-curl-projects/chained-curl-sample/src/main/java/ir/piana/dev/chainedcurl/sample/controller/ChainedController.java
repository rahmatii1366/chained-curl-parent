package ir.piana.dev.chainedcurl.sample.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.dev.chainedcurl.core.service.*;
import ir.piana.dev.jsonparser.json.JsonParser;
import ir.piana.dev.jsonparser.json.JsonTarget;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("api/v1/chained-curl/test")
public class ChainedController {
    private final JsonParser jsonParser;
    private final ChainedCurlContextHolderService contextHolderService;
    private final ObjectMapper objectMapper;
    private final ObjectMapper yamlObjectMapper;
    private final CurlExecutorService curlExecutorService;

    public ChainedController(
            JsonParser jsonParser,
            ChainedCurlContextHolderService contextHolderService,
            CurlExecutorService curlExecutorService,
            @Qualifier("chainedCurlYamlMapper") ObjectMapper yamlObjectMapper,
            @Qualifier("chainedCurlJsonMapper") ObjectMapper objectMapper) {
        this.jsonParser = jsonParser;
        this.contextHolderService = contextHolderService;
        this.curlExecutorService = curlExecutorService;
        this.yamlObjectMapper = yamlObjectMapper;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<String> hay() {
        return ResponseEntity.ok("Hi!");
    }

    /*@PostMapping(path = "upload-and-create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChainedCurlContext> uploadAndCreatedContext(@RequestParam("ChainedCurlConfig") MultipartFile file) {
        ChainedCurlDto chainedCurlDto;
        try (InputStream is = file.getInputStream()) {
            chainedCurlDto = yamlObjectMapper.readValue(is, ChainedCurlDto.class);
            ChainedCurlServerContext newContext = contextHolderService.createNewContext(chainedCurlDto);
            return ResponseEntity.ok(newContext.getClientContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    @PostMapping(path = "upload-and-get-curl-config", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChainedCurlServerContext.ChainedCurlServerConfig> uploadAndGetCurlConfig(@RequestParam("ChainedCurlConfig") MultipartFile file) {
        ChainedCurlDto chainedCurlDto;
        try (InputStream is = file.getInputStream()) {
            chainedCurlDto = yamlObjectMapper.readValue(is, ChainedCurlDto.class);

            ChainedCurlServerContext newContext = contextHolderService.createNewContext(chainedCurlDto);

            return ResponseEntity.ok(new ChainedCurlServerContext.ChainedCurlServerConfig(
                    newContext.getUuid(), chainedCurlDto, newContext.getClientContext().getInitialContext(), newContext.getClientContext().getContextMap()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(path = "create-context",
            consumes = "application/x-yaml",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChainedCurlServerContext> createNewChainedCurlContext(@RequestBody ChainedCurlDto chainedCurlDto) throws JsonProcessingException {
//    public ResponseEntity<byte[]> createNewChainedCurlContext(@RequestBody ChainedCurlDto chainedCurlDto) throws JsonProcessingException {
        ChainedCurlServerContext newContext = contextHolderService.createNewContext(chainedCurlDto);

//        byte[] bytes = objectMapper.writeValueAsBytes(newContext);
//        return ResponseEntity.ok(bytes);
        return ResponseEntity.ok(newContext);
    }

    /*@GetMapping(path = "evaluated-initial-values", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChainedCurlStepContext> evaluatedInitialValues(@RequestParam("context-id") String contextId) {
    }*/

    @PostMapping(path = "do-curl",
            produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CurlExecutorService.StepResponse> doStepCurlRequest(
            HttpServletRequest request,
            @RequestParam("context-id") String contextId,
            @RequestParam("step-name") String stepName)
            throws IOException {
        CurlExecutorService.StepResponse execute = curlExecutorService.execute(
                contextId, stepName, request.getInputStream().readAllBytes());

        return ResponseEntity.ok(execute);
    }

    @GetMapping(path = "get-prev-steps-values",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> getPrevStepsValues(
            HttpServletRequest request,
            @RequestParam("context-id") String contextId,
            @RequestParam("step-name") String stepName)
            throws IOException {
        Map<String, String> execute = curlExecutorService.fetchPrevStepsValues(
                contextId, stepName);

        return ResponseEntity.ok(execute);
    }

    @GetMapping(path = "check-if-required-prev-step-values",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Boolean>> checkIfRequiredPrevStepValues(
            HttpServletRequest request,
            @RequestParam("context-id") String contextId,
            @RequestParam("step-name") String stepName)
            throws IOException {
        boolean execute = curlExecutorService.checkIfRequiredPrevStepValues(
                contextId, stepName);

        return ResponseEntity.ok(Collections.singletonMap("checked", execute));
    }
}
