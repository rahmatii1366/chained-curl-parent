package ir.piana.dev.chainedcurl.core.service;

import ir.piana.dev.chainedcurl.core.service.exp.ExpressionSourceType;
import org.springframework.context.ApplicationContext;

import java.util.*;

public class ChainedCurlContextHolderService {
    private final Map<String, ChainedCurlServerContext> contextMap;
    private final InitiateChainedCurlExpCommandHandlerService expCommandHandlerService;
    private final ApplicationContext applicationContext;

    public ChainedCurlContextHolderService(
            InitiateChainedCurlExpCommandHandlerService expCommandHandlerService,
            ApplicationContext applicationContext) {
        this.expCommandHandlerService = expCommandHandlerService;
        this.applicationContext = applicationContext;
        this.contextMap = new LinkedHashMap<>();
    }

    public ChainedCurlServerContext createNewContext(ChainedCurlDto chainedCurlDto) {
        String uuid = UUID.randomUUID().toString();
        contextMap.put(uuid, new ChainedCurlServerContext(uuid, chainedCurlDto));
        ChainedCurlServerContext chainedCurlServerContext = contextMap.get(uuid);
        initiateContext(chainedCurlServerContext);
        return chainedCurlServerContext;
    }

    public Optional<ChainedCurlServerContext> getContext(String uuid) {
        return Optional.ofNullable(contextMap.get(uuid));
    }

    private void initiateContext(ChainedCurlServerContext context) {
        ChainedCurlRequestProvideDto provideDto = context.getChainedCurlDto().getProvideDto();
        /*context.getChainedCurlDto().getChainMap().keySet().forEach(key -> {
        });*/
        context.getChainedCurlDto().getChainMap().entrySet()
                .forEach(entry -> {
                    ChainedCurlStepContext stepContext = context.getClientContext().newStep(entry.getKey());
                    ChainedCurlRequestProvideDto stepProvideDto = entry.getValue().getProvideDto();

                    if (Objects.nonNull(stepProvideDto)) {
                        Optional.ofNullable(stepProvideDto.getFix()).orElse(Collections.emptyMap())
                                .forEach((k, command) -> {
                                    String[] split = command.split("#");
                                    String value = (String) expCommandHandlerService.initiate(
                                            split[0], split[1].split("&"), context.getClientContext()).getValue();
                                    stepContext.putFix(k, value);
                                });

                        Optional.ofNullable(stepProvideDto.getSupplier()).orElse(Collections.emptyMap())
                                .forEach((k, command) -> {
                                    stepContext.putSupplier(k, applicationContext.getBean(command, ValueSupplier.class).get());
                                });
                    }
                });
        if (Objects.nonNull(provideDto.getFix())) {
            provideDto.getFix().forEach((key, command) -> {
                String value = (String) expCommandHandlerService.initiate(
                        null, command.split("&"), context.getClientContext()).getValue();
                context.getClientContext().putInitiateValue(ExpressionSourceType.FIX, key, value);
            });
        }

        if (Objects.nonNull(provideDto.getSupplier())) {
            provideDto.getSupplier().forEach((key, command) -> {
                context.getClientContext().putInitiateValue(ExpressionSourceType.SUPPLIER, key,
                        applicationContext.getBean(command, ValueSupplier.class).get());
            });
        }
    }
}
