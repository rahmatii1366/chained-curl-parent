package ir.piana.dev.chainedcurl.core.service;

import ir.piana.dev.chainedcurl.core.service.exp.DateExpCommandHandler;
import ir.piana.dev.chainedcurl.core.service.exp.PersianDateExpCommandHandler;
import ir.piana.dev.chainedcurl.core.service.exp.StringExpCommandHandler;
import ir.piana.dev.chainedcurl.core.service.exp.UrlExpCommandHandler;

import java.util.Objects;


public class InitiateChainedCurlExpCommandHandlerService {

    public ChainedCurlExpCommandHandler<?> initiate(
            String value, String[] commands, ChainedCurlContext chainedCurlContext) {
        if (Objects.isNull(commands) || commands.length == 0 || !commands[0].startsWith("as-"))
            throw new RuntimeException();

        ChainedCurlExpCommandHandler<?> expCommandHandler = detect(value, commands[0].split(":"));


        for (int i = 1; i < commands.length; i++) {
            expCommandHandler = expCommandHandler.handle(commands[i], chainedCurlContext);
        }

        return expCommandHandler;
    }

    //$(res-json#stepName:fieldKey&as-date:yyyyMMdd&addYear:1);
    private ChainedCurlExpCommandHandler<?> detect(String value, String[] colonSplitter) {
        switch (colonSplitter[0]) {
            case "as-url" -> {
                return new UrlExpCommandHandler(value);
            }
            case "as-string" -> {
                return new StringExpCommandHandler(value);
            }
            case "as-date" -> {
                return new DateExpCommandHandler(
                        value, colonSplitter.length > 1 ? colonSplitter[1] : null);
            }
            case "as-persian-date" -> {
                return new PersianDateExpCommandHandler(
                        value, colonSplitter.length > 1 ? colonSplitter[1] : null);
            }
        }
        throw new RuntimeException();
    }
}
