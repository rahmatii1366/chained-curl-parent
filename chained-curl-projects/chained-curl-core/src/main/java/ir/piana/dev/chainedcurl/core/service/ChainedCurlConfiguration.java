package ir.piana.dev.chainedcurl.core.service;

import ir.piana.dev.jsonparser.json.JsonParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChainedCurlConfiguration {

    @Bean
    ChainedCurlCollectorService chainedCurlCollectorService(List<ChainedCurlProvidable> chainedCurlProvidableList) {
        return new ChainedCurlCollectorService(chainedCurlProvidableList);
    }

    @Bean
    ExpressionInterpreterService expressionInterpreterService(
            InitiateChainedCurlExpCommandHandlerService initiateChainedCurlExpCommandHandlerService) {
        return new ExpressionInterpreterService(
                initiateChainedCurlExpCommandHandlerService);
    }

    @Bean
    InitiateChainedCurlExpCommandHandlerService InitiateChainedCurlExpCommandHandlerService() {
        return new InitiateChainedCurlExpCommandHandlerService();
    }

    @Bean
    ChainedCurlContextHolderService chainedCurlContextHolderService(
            InitiateChainedCurlExpCommandHandlerService expCommandHandlerService,
            ApplicationContext applicationContext
    ) {
        return new ChainedCurlContextHolderService(expCommandHandlerService, applicationContext);
    }

    @Bean
    CurlCommandProcessor curlCommandProcessor(ExpressionInterpreterService expressionInterpreterService) {
        return new CurlCommandProcessor(expressionInterpreterService);
    }

    @Bean
    CurlExecutorService curlExecutorService(
            CurlCommandProcessor curlCommandProcessor,
            ExpressionInterpreterService expressionInterpreterService,
            ChainedCurlContextHolderService chainedCurlContextHolderService,
            JsonParser jsonParser
    ) {
        return new CurlExecutorService(
                curlCommandProcessor,
                chainedCurlContextHolderService,
                jsonParser);
    }
}
