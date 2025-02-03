package ir.piana.dev.chainedcurl.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlContextHolderService;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlDto;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlServerContext;
import ir.piana.dev.chainedcurl.core.service.CurlExecutorService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Hidden
@ConditionalOnProperty(prefix = "chained-curl.controller",
        name = "enabled",
        havingValue = "true")
@RestController
@RequestMapping("${chained-curl.controller.base-url:chained-curl}")
public class ChainedCurlHttpController {
    private final ResourceLoader resourceLoader;
    private final ChainedCurlContextHolderService contextHolderService;
    private final ObjectMapper chainedCurlYamlMapper;
    private final CurlExecutorService curlExecutorService;

    public ChainedCurlHttpController(
            ResourceLoader resourceLoader,
            ChainedCurlContextHolderService contextHolderService,
            @Qualifier("chainedCurlYamlMapper") ObjectMapper chainedCurlYamlMapper,
            CurlExecutorService curlExecutorService) {
        this.resourceLoader = resourceLoader;
        this.contextHolderService = contextHolderService;
        this.chainedCurlYamlMapper = chainedCurlYamlMapper;
        this.curlExecutorService = curlExecutorService;
    }

    @Value("${chained-curl.controller.resource-folder:chained-curl/conf}")
    private String confFolder;

    private static final Map<String, Asset> assetMap = new LinkedHashMap<>();
    private static final Map<String, ChainedCurlDto> chainedCurlMap = new LinkedHashMap<>();
    private static final List<String> chainedCurlNames = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            String assetName = "index.html";
            Resource resource = resourceLoader.getResource("classpath:assets/browser/" + assetName);
            assetMap.put(assetName, new Asset(
                    assetName, resource.getInputStream().readAllBytes(), "text/html"));
            assetName = "favicon.ico";
            resource = resourceLoader.getResource("classpath:assets/browser/" + assetName);
            assetMap.put(assetName, new Asset(
                    assetName, resource.getInputStream().readAllBytes(), "image/x-icon"));
            assetName = "main-M6WD7ILI.js";
            resource = resourceLoader.getResource("classpath:assets/browser/" + assetName);
            assetMap.put(assetName, new Asset(
                    assetName, resource.getInputStream().readAllBytes(), "application/javascript"));
            assetName = "polyfills-SCHOHYNV.js";
            resource = resourceLoader.getResource("classpath:assets/browser/" + assetName);
            assetMap.put(assetName, new Asset(
                    assetName, resource.getInputStream().readAllBytes(), "application/javascript"));
            assetName = "scripts-EEEIPNC3.js";
            resource = resourceLoader.getResource("classpath:assets/browser/" + assetName);
            assetMap.put(assetName, new Asset(
                    assetName, resource.getInputStream().readAllBytes(), "application/javascript"));
            assetName = "styles-7LNID6P2.css";
            resource = resourceLoader.getResource("classpath:assets/browser/" + assetName);
            assetMap.put(assetName, new Asset(
                    assetName, resource.getInputStream().readAllBytes(), "text/css"));
            assetName = "media/bootstrap-icons-OCU552PF.woff";
            resource = resourceLoader.getResource("classpath:assets/browser/" + assetName);
            assetMap.put(assetName.split("/")[1], new Asset(
                    assetName.split("/")[1], resource.getInputStream().readAllBytes(), "font/woff2"));
            assetName = "media/bootstrap-icons-X6UQXWUS.woff2";
            resource = resourceLoader.getResource("classpath:assets/browser/" + assetName);
            assetMap.put(assetName.split("/")[1], new Asset(
                    assetName.split("/")[1], resource.getInputStream().readAllBytes(), "font/woff2"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            List<String> resourceFiles = getResourceFiles(confFolder);
            for (String resourceFile : resourceFiles) {
                Resource resource = resourceLoader.getResource("classpath:" + confFolder + "/" + resourceFile);
                try (InputStream inputStream = resource.getInputStream()) {
                    ChainedCurlDto chainedCurlDto = chainedCurlYamlMapper.readValue(inputStream, ChainedCurlDto.class);
                    chainedCurlMap.put(chainedCurlDto.getName(), chainedCurlDto);
                    chainedCurlNames.add(chainedCurlDto.getName());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = { "ui/{asset-name}", "ui/media/{asset-name}" })
    public void getAsset(@PathVariable("asset-name") String assetName, HttpServletResponse servletResponse)
            throws IOException {
        if (!assetMap.containsKey(assetName.substring(0,
                assetName.indexOf("?") > 0 ?
                        assetName.indexOf("?") : assetName.length()))) {
            noContent(servletResponse);
            return;
        }
        generateResponse(servletResponse, assetMap.get(assetName));
    }

    private void generateResponse(HttpServletResponse servletResponse, Asset asset) throws IOException {
        servletResponse.setStatus(200);
        servletResponse.setContentType(asset.contentType);
        servletResponse.getOutputStream().write(asset.bytes);
    }

    private void noContent(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setStatus(404);
        servletResponse.getOutputStream().print("Asset Not Exist!");
    }

    record Asset(String name, byte[] bytes, String contentType) {
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listChainedCurls() {
        return ResponseEntity.ok(chainedCurlNames);
        /*Resource resource = resourceLoader.getResource(
                "classpath:chained-curl/conf/register-creditor-then-sign-one-tap-mandate.yaml");
        try (InputStream inputStream = resource.getInputStream()) {

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    @GetMapping("/conf/{assetName}")
    public ResponseEntity<ChainedCurlServerContext.ChainedCurlServerConfig> chainedCurlsContext(@PathVariable("assetName") String assetName) {
        ChainedCurlDto chainedCurlDto = chainedCurlMap.get(assetName);

        ChainedCurlServerContext newContext = contextHolderService.createNewContext(chainedCurlDto);

        return ResponseEntity.ok(new ChainedCurlServerContext.ChainedCurlServerConfig(
                newContext.getUuid(), chainedCurlDto, newContext.getClientContext().getInitialContext(), newContext.getClientContext().getContextMap()));
    }

    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @PostMapping(path = "do-curl",
            produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8",
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
}
