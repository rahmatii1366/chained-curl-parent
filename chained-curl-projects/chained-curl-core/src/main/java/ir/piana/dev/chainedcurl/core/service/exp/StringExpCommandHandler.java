package ir.piana.dev.chainedcurl.core.service.exp;

import ir.piana.dev.chainedcurl.core.service.ChainedCurlContext;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlExpCommandHandler;

public class StringExpCommandHandler extends ChainedCurlExpCommandHandler<String> {
    public StringExpCommandHandler(String value) {
        super(value);
    }

    @Override
    public ChainedCurlExpCommandHandler handle(String command, ChainedCurlContext curlChainContext) {
        String[] split = command.split(":");
        if (split.length == 1) {
            value = split[0];
            return this;
        }
        /*switch (split[0].trim().toLowerCase()) {
            case "format" -> {
                return new StringExpCommandHandler(value.format(DateTimeFormatter.ofPattern(split[1])));
            }
        }*/

        throw new RuntimeException("command not exist!");
    }
}
